import pytest
from httpx import AsyncClient
from unittest.mock import MagicMock

# Import the FastAPI app instance from your main application file
from ..main import app, verify_token

# This is a pytest fixture that provides an async test client.
# It's automatically used in tests that have an 'async_client' parameter.
@pytest.fixture
async def async_client():
    async with AsyncClient(app=app, base_url="http://test") as client:
        yield client

# This fixture mocks the token validation dependency for protected endpoints.
@pytest.fixture(autouse=True)
def override_auth_dependency(mocker):
    # Use pytest-mock's 'mocker' to patch the function used in Depends
    mocker.patch('ClientAPI.main.validate_token', return_value=True)

# This fixture mocks the database dependency.
# It replaces the real MongoDB connection with a simple in-memory dictionary.
@pytest.fixture(autouse=True)
def override_database_dependency():
    # In-memory "database"
    mock_db_storage = {"clients": {}}

    async def get_mock_db():
        # Using a class with __getitem__ and __setitem__ to mimic Motor's collection access
        class MockCollection:
            def __init__(self, collection_name):
                self.collection = mock_db_storage.get(collection_name, {})

            async def find_one(self, query):
                email = query.get("email")
                return self.collection.get(email)

            async def find(self):
                # A simplified find that returns all documents
                class MockCursor:
                    def __init__(self, items):
                        self._items = list(items)
                        self._index = 0
                    def to_list(self, length):
                        return self._items
                return MockCursor(self.collection.values())

            async def insert_one(self, document):
                email = document.get("email")
                self.collection[email] = document
            
            async def replace_one(self, query, replacement):
                email = query.get("email")
                if email in self.collection:
                    self.collection[email] = replacement

        class MockDatabase:
            def __getitem__(self, key):
                return MockCollection(key)

        return MockDatabase()

    # Override the dependency for the duration of the test
    app.dependency_overrides[app.dependency_overrides.keys().__iter__().__next__()] = get_mock_db
    yield
    # Clear the override after the test
    app.dependency_overrides = {}


@pytest.mark.asyncio
async def test_create_client_success(async_client: AsyncClient):
    """
    Tests successful client creation.
    """
    client_data = {"email": "test@example.com", "firstName": "Test", "lastName": "User"}
    response = await async_client.post("/clients", json=client_data)
    
    assert response.status_code == 201
    response_json = response.json()
    assert response_json["email"] == "test@example.com"
    assert "tickets" in response_json  # Ensure default fields are present


@pytest.mark.asyncio
async def test_create_client_already_exists(async_client: AsyncClient):
    """
    Tests that creating a client with a duplicate email fails.
    The API currently returns 405, but 409 (Conflict) would be more appropriate.
    This test checks for the current behavior but documents the ideal.
    """
    client_data = {"email": "existing@example.com", "firstName": "Existing"}
    
    # First, create the client
    await async_client.post("/clients", json=client_data)
    
    # Then, try to create it again
    response = await async_client.post("/clients", json=client_data)
    
    # The current implementation returns 405, a better status would be 409
    assert response.status_code == 405
    assert "already exists" in response.json()["detail"]


@pytest.mark.asyncio
async def test_get_clients_requires_auth(mocker):
    """
    Tests that the /clients GET endpoint is protected.
    It does so by mocking the validation to return False.
    """
    # We need a separate client for this test to avoid the auto-mocking fixture
    async with AsyncClient(app=app, base_url="http://test") as client:
        # Arrange: mock the dependency to simulate a failed token validation
        mocker.patch('ClientAPI.main.validate_token', return_value=False)
        app.dependency_overrides[verify_token] = lambda: False # A simplified override for this case
        
        # Act
        response = await client.get("/clients", headers={"Authorization": "Bearer invalidtoken"})
        
        # Assert
        assert response.status_code == 401

    # Cleanup
    app.dependency_overrides = {}


@pytest.mark.asyncio
async def test_get_client_by_email_not_found(async_client: AsyncClient):
    """
    Tests getting a single client that does not exist.
    """
    response = await async_client.get("/clients/notfound@example.com")
    assert response.status_code == 404
