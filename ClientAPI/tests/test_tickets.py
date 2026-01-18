import pytest
from httpx import AsyncClient
import respx

# Import the FastAPI app instance from your main application file
from ..main import app, EVENT_API_BASE_URL

# Re-using the same fixtures from test_main.py for client and auth mocking
# Pytest will automatically discover and use them.

@pytest.mark.asyncio
@respx.mock
async def test_add_ticket_for_single_event(async_client: AsyncClient):
    """
    Tests adding a ticket that corresponds to a single event in the EventAPI.
    """
    client_email = "client@example.com"
    ticket_code = "TICKET001"
    event_id = 10

    # 1. Arrange: Create the client first
    await async_client.post("/clients", json={"email": client_email, "firstName": "Ticket"})

    # 2. Arrange: Mock the external EventAPI HTTP calls using respx
    #    - Mock the response for getting the ticket
    ticket_api_route = respx.get(f"{EVENT_API_BASE_URL}/tickets/{ticket_code}")
    ticket_api_route.respond(200, json={"eventID": event_id, "groupID": 0})
    
    #    - Mock the response for getting the event details
    event_api_route = respx.get(f"{EVENT_API_BASE_URL}/events/{event_id}")
    event_api_route.respond(200, json={"name": "The Grand Concert", "location": "Main Hall"})

    # 3. Act: Call the endpoint to add the ticket to the client
    response = await async_client.put(f"/clients/{client_email}/tickets/{ticket_code}")

    # 4. Assert
    assert response.status_code == 200
    client = response.json()
    assert len(client["tickets"]) == 1
    ticket = client["tickets"][0]
    assert ticket["code"] == ticket_code
    assert ticket["eventName"] == "The Grand Concert"
    assert not ticket["isPackage"]


@pytest.mark.asyncio
@respx.mock
async def test_add_ticket_for_package(async_client: AsyncClient):
    """
    Tests adding a ticket that corresponds to a package in the EventAPI.
    """
    client_email = "package-client@example.com"
    ticket_code = "PACKTICKET"
    group_id = 20

    # 1. Arrange: Create the client
    await async_client.post("/clients", json={"email": client_email})

    # 2. Arrange: Mock the EventAPI calls for a package
    #    - Mock ticket response
    respx.get(f"{EVENT_API_BASE_URL}/tickets/{ticket_code}").respond(200, json={"eventID": 0, "groupID": group_id})
    
    #    - Mock package details response
    respx.get(f"{EVENT_API_BASE_URL}/event-packets/{group_id}").respond(200, json={"name": "VIP Package", "location": "VIP Lounge"})
    
    #    - Mock the events-within-a-package response (with HATEOAS structure)
    respx.get(f"{EVENT_API_BASE_URL}/event-packets/{group_id}/events").respond(200, json={
        "_embedded": {
            "dataObjects": [
                {"name": "Event A", "location": "Room 1"},
                {"name": "Event B", "location": "Room 2"}
            ]
        }
    })

    # 3. Act
    response = await async_client.put(f"/clients/{client_email}/tickets/{ticket_code}")

    # 4. Assert
    assert response.status_code == 200
    client = response.json()
    assert len(client["tickets"]) == 1
    ticket = client["tickets"][0]
    assert ticket["code"] == ticket_code
    assert ticket["eventName"] == "VIP Package"
    assert ticket["isPackage"]
    assert len(ticket["includedEvents"]) == 2
    assert ticket["includedEvents"][0]["name"] == "Event A"

@pytest.mark.asyncio
@respx.mock
async def test_add_ticket_eventapi_returns_404(async_client: AsyncClient):
    """
    Tests that if EventAPI returns a 404 for the ticket, our API also returns 404.
    """
    client_email = "client@example.com"
    ticket_code = "FAKETICKET"
    await async_client.post("/clients", json={"email": client_email})

    # Arrange: Mock EventAPI to return a 404
    respx.get(f"{EVENT_API_BASE_URL}/tickets/{ticket_code}").respond(404)

    # Act
    response = await async_client.put(f"/clients/{client_email}/tickets/{ticket_code}")

    # Assert
    assert response.status_code == 404
    assert "not found in EventAPI" in response.json()["detail"]
