from typing import List, Optional
from fastapi import FastAPI, HTTPException, Depends, Header, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase
from model.ClientModel import ClientModel
from model.TicketModel import TicketModel, EventInfo
from auth_client import validate_token, login_user, register_user
import os
import httpx

MONGO_DETAILS = os.environ.get("MONGO_DETAILS", "mongodb://host.docker.internal:27017")
db_client: AsyncIOMotorClient

class LoginRequest(BaseModel):
    username: str
    password: str

class RegisterRequest(BaseModel):
    username: str
    password: str
    email: str
    role: Optional[str] = "client"

class TokenResponse(BaseModel):
    token: str
    email: str

class PublicClientInfo(BaseModel):
    email: str
    firstName: Optional[str] = None
    lastName: Optional[str] = None
    isNamePublic: bool = False
    socialMedia: Optional[dict] = None

async def verify_token(authorization: Optional[str] = Header(None)):
    if authorization is None:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Authorization header missing")
    
    parts = authorization.split()
    if len(parts) != 2 or parts[0].lower() != "bearer":
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid Authorization header format")

    token = parts[1]
    if not validate_token(token):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid or expired token")

def get_database() -> AsyncIOMotorDatabase:
    return db_client.pos_project

async def connect_to_mongo():
    global db_client
    db_client = AsyncIOMotorClient(MONGO_DETAILS)
    print("Connected to MongoDB.")

async def close_mongo_connection():
    global db_client
    db_client.close()
    print("Closed MongoDB connection.")

app = FastAPI(title="ClientAPI")
app.add_event_handler("startup", connect_to_mongo)
app.add_event_handler("shutdown", close_mongo_connection)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

EVENT_API_BASE_URL = os.environ.get("EVENT_API_BASE_URL", "http://host.docker.internal:8080") + "/api/event-manager"

@app.post("/auth/register")
async def register(req: RegisterRequest, db: AsyncIOMotorDatabase = Depends(get_database)):
    # 1. Register in AuthAPI (gRPC)
    auth_resp = register_user(req.username, req.password, req.email, req.role)
    if not auth_resp or not auth_resp.success:
        detail = auth_resp.message if auth_resp else "AuthAPI connection error"
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=detail)
    
    # 2. Create client profile in MongoDB if role is client
    if req.role == "client":
        if not await db.clients.find_one({"email": req.email}):
            new_client = ClientModel(email=req.email, tickets=[])
            await db.clients.insert_one(new_client.model_dump())
            
    return {"message": "User registered successfully"}

@app.post("/auth/login", response_model=TokenResponse)
async def login(req: LoginRequest, db: AsyncIOMotorDatabase = Depends(get_database)):
    auth_resp = login_user(req.username, req.password)
    if not auth_resp or auth_resp.error:
        detail = auth_resp.error if auth_resp else "AuthAPI connection error"
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail=detail)
    
    client_data = await db.clients.find_one({"email": req.username}) # Assume username is email for now
    email = client_data["email"] if client_data else req.username # This is needed for TokenResponse

    return {"token": auth_resp.token, "email": email}

@app.post("/clients", response_model=ClientModel, status_code=status.HTTP_201_CREATED)
async def create_client(client: ClientModel, db: AsyncIOMotorDatabase = Depends(get_database)):
    if await db.clients.find_one({"email": client.email}):
        raise HTTPException(status_code=status.HTTP_405_METHOD_NOT_ALLOWED, detail="Client with this email already exists")
    client_dict = client.model_dump()
    await db.clients.insert_one(client_dict)
    return client


@app.get("/clients", response_model=List[ClientModel], dependencies=[Depends(verify_token)])
async def get_clients(db: AsyncIOMotorDatabase = Depends(get_database)):
    clients = await db.clients.find().to_list(length=100)
    return [ClientModel(**client) for client in clients]


@app.get("/clients/{email}", response_model=ClientModel)
async def get_client(email: str, db: AsyncIOMotorDatabase = Depends(get_database)):
    client = await db.clients.find_one({"email": email})
    if not client:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Client not found")
    return ClientModel(**client)


@app.put("/clients/{email}", response_model=ClientModel)
async def update_client(email: str, client_update: ClientModel, db: AsyncIOMotorDatabase = Depends(get_database)):
    if email != client_update.email:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Cannot change client email via this endpoint.")

    existing_client = await db.clients.find_one({"email": email})
    if not existing_client:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Client not found")

    client_dict = client_update.model_dump()
    await db.clients.replace_one({"email": email}, client_dict)
    return client_update


@app.put("/clients/{email}/tickets/{ticket_code}", response_model=ClientModel)
async def add_ticket_to_client(email: str, ticket_code: str, db: AsyncIOMotorDatabase = Depends(get_database)):
    client_data = await db.clients.find_one({"email": email})
    if not client_data:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Client not found")
    
    client_model = ClientModel(**client_data)

    if any(ticket.code == ticket_code for ticket in client_model.tickets):
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=f"Ticket {ticket_code} already exists for this client.")

    async with httpx.AsyncClient() as client:
        try:
            response = await client.get(f"{EVENT_API_BASE_URL}/tickets/{ticket_code}")
            if response.status_code == status.HTTP_404_NOT_FOUND:
                raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"Ticket with code {ticket_code} not found in EventAPI.")
            response.raise_for_status()
            ticket_data = response.json()

            event_id = ticket_data.get("eventID")
            group_id = ticket_data.get("groupID")
            new_ticket: TicketModel

            if group_id and group_id > 0:
                pkg_res = await client.get(f"{EVENT_API_BASE_URL}/event-packets/{group_id}")
                pkg_res.raise_for_status()
                pkg_data = pkg_res.json()

                events_res = await client.get(f"{EVENT_API_BASE_URL}/event-packets/{group_id}/events")
                events_res.raise_for_status()
                embedded_data = events_res.json().get('_embedded', {})
                events_list = embedded_data.get('dataObjects', [])
                included_events = [EventInfo(id=e.get('id'), name=e.get('name'), location=e.get('location')) for e in events_list]

                new_ticket = TicketModel(
                    code=ticket_code,
                    eventName=pkg_data.get("name"),
                    eventLocation=pkg_data.get("location"),
                    isPackage=True,
                    eventID=None,
                    groupID=group_id,
                    includedEvents=included_events
                )
            else:
                evt_res = await client.get(f"{EVENT_API_BASE_URL}/events/{event_id}")
                evt_res.raise_for_status()
                evt_data = evt_res.json()
                new_ticket = TicketModel(
                    code=ticket_code,
                    eventName=evt_data.get("name"),
                    eventLocation=evt_data.get("location"),
                    isPackage=False,
                    eventID=event_id,
                    groupID=None
                )

            client_model.tickets.append(new_ticket)
            await db.clients.replace_one({"email": email}, client_model.model_dump())
            return client_model

        except httpx.HTTPStatusError as e:
            raise HTTPException(status_code=e.response.status_code, detail=f"Error from EventAPI: {e.response.text}")
        except Exception as e:
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=f"An unexpected error occurred: {str(e)}")

@app.get("/clients/ticket-holders/{event_id}", response_model=List[PublicClientInfo], dependencies=[Depends(verify_token)])
async def get_ticket_holders_for_event(event_id: int, db: AsyncIOMotorDatabase = Depends(get_database)):
    
    pipeline = [
        {"$match": {"tickets": {"$exists": True, "$not": {"$size": 0}}}},
        {"$unwind": "$tickets"},
        {"$match": {
            "$or": [
                {"tickets.eventID": event_id},
                {"$and": [
                    {"tickets.isPackage": True},
                    {"tickets.includedEvents": {"$elemMatch": {"id": event_id}}}
                ]}
            ]
        }},
        {"$group": {"_id": "$_id", "root": {"$first": "$$ROOT"}}},
        {"$replaceRoot": {"newRoot": "$root"}},
        {"$project": {
            "email": "$email",
            "firstName": "$firstName",
            "lastName": "$lastName",
            "isNamePublic": "$isNamePublic",
            "socialMedia": "$socialMedia",
            "_id": 0
        }}
    ]

    clients_with_tickets = await db.clients.aggregate(pipeline).to_list(length=None)
    
    return clients_with_tickets