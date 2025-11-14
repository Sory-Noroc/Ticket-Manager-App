from typing import List

from fastapi import FastAPI, HTTPException, Depends
from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase

from model.ClientModel import ClientModel
from model.TicketModel import TicketModel, EventInfo
import os
import httpx

MONGO_DETAILS = os.environ.get("MONGO_DETAILS", "mongodb://host.docker.internal:27017")
db_client: AsyncIOMotorClient

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

EVENT_API_BASE_URL = os.environ.get("EVENT_API_BASE_URL", "http://host.docker.internal:8080") + "/api/event-manager"


@app.post("/clients", response_model=ClientModel, status_code=201)
async def create_client(client: ClientModel, db: AsyncIOMotorDatabase = Depends(get_database)):
    if await db.clients.find_one({"email": client.email}):
        raise HTTPException(status_code=405, detail="Client with this email already exists")
    
    client_dict = client.model_dump()
    await db.clients.insert_one(client_dict)
    return client

@app.get("/clients", response_model=List[ClientModel])
async def get_clients(db: AsyncIOMotorDatabase = Depends(get_database)):
    clients = await db.clients.find().to_list(length=100)
    return [ClientModel(**client) for client in clients]


@app.get("/clients/{email}", response_model=ClientModel)
async def get_client(email: str, db: AsyncIOMotorDatabase = Depends(get_database)):
    client = await db.clients.find_one({"email": email})
    if not client:
        raise HTTPException(status_code=404, detail="Client not found")
    return ClientModel(**client)


@app.put("/clients/{email}", response_model=ClientModel)
async def update_client(email: str, client_update: ClientModel, db: AsyncIOMotorDatabase = Depends(get_database)):
    if email != client_update.email:
        raise HTTPException(status_code=400, detail="Cannot change client email via this endpoint.")

    existing_client = await db.clients.find_one({"email": email})
    if not existing_client:
        raise HTTPException(status_code=404, detail="Client not found")

    client_dict = client_update.model_dump()
    await db.clients.replace_one({"email": email}, client_dict)
    return client_update


@app.put("/clients/{email}/tickets/{ticket_code}", response_model=ClientModel)
async def add_ticket_to_client(email: str, ticket_code: str, db: AsyncIOMotorDatabase = Depends(get_database)):
    client_data = await db.clients.find_one({"email": email})
    if not client_data:
        raise HTTPException(status_code=404, detail="Client not found")
    
    client_model = ClientModel(**client_data)

    if any(ticket.code == ticket_code for ticket in client_model.tickets):
        raise HTTPException(status_code=400, detail=f"Ticket {ticket_code} already exists for this client.")

    async with httpx.AsyncClient() as client:
        try:
            response = await client.get(f"{EVENT_API_BASE_URL}/tickets/{ticket_code}")
            if response.status_code == 404:
                raise HTTPException(status_code=404, detail=f"Ticket with code {ticket_code} not found in EventAPI.")
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
                included_events = [EventInfo(name=e.get('name'), location=e.get('location')) for e in events_list]

                new_ticket = TicketModel(
                    code=ticket_code,
                    eventName=pkg_data.get("name"),
                    eventLocation=pkg_data.get("location"),
                    isPackage=True,
                    includedEvents=included_events
                )
            else:
                evt_res = await client.get(f"{EVENT_API_BASE_URL}/events/{event_id}")
                evt_res.raise_for_status()
                evt_data = evt_res.json()
                new_ticket = TicketModel(
                    code=ticket_code,
                    eventName=evt_data.get("name"),
                    eventLocation=evt_data.get("location")
                )

            client_model.tickets.append(new_ticket)
            await db.clients.replace_one({"email": email}, client_model.model_dump())
            return client_model

        except httpx.HTTPStatusError as e:
            raise HTTPException(status_code=e.response.status_code, detail=f"Error from EventAPI: {e.response.text}")
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"An unexpected error occurred: {str(e)}")