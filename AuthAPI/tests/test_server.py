import pytest
import grpc
from concurrent import futures
import time

# Import the generated stubs and the service implementation
from .. import protos
from ..server import AuthService

# This fixture sets up and tears down an in-memory gRPC server for testing.
@pytest.fixture(scope="module")
def grpc_server():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=1))
    servicer = AuthService()
    protos.auth_pb2_grpc.add_AuthServiceServicer_to_server(servicer, server)
    server.add_insecure_port("[::]:50051")
    server.start()
    yield servicer, server  # Provide the servicer and server to the tests
    server.stop(0)

# This fixture provides a client stub connected to the test server.
@pytest.fixture(scope="module")
def grpc_stub(grpc_server):
    _, server = grpc_server
    channel = grpc.insecure_channel("localhost:50051")
    stub = protos.auth_pb2_grpc.AuthServiceStub(channel)
    yield stub
    channel.close()


def test_register_user_success(grpc_stub, mocker):
    """
    Tests successful user registration.
    """
    # Arrange: Mock the database functions to simulate a new user
    mock_db_conn = mocker.patch('AuthAPI.server.database.get_db_connection')
    
    # Mock the SELECT query to return None (user does not exist)
    mocker.patch.object(AuthService, '_get_user_by_username', return_value=None)
    
    # Mock the INSERT query
    mock_cursor = mock_db_conn.return_value.cursor.return_value
    mock_cursor.execute.return_value = None
    mock_db_conn.return_value.commit.return_value = None

    # Act
    request = protos.auth_pb2.RegisterRequest(
        username="newuser",
        password="password123",
        email="new@example.com",
        role="USER"
    )
    response = grpc_stub.Register(request)

    # Assert
    assert response.success is True
    assert "successfully" in response.message
    # Check if the INSERT was called
    mock_cursor.execute.assert_called_once()
    assert "INSERT INTO users" in mock_cursor.execute.call_args[0][0]


def test_register_user_already_exists(grpc_stub, mocker):
    """
    Tests registration failure when a user already exists.
    """
    # Arrange: Mock the _get_user_by_username method to return an existing user
    existing_user = {"username": "existinguser", "password": "hashedpassword", "email": "a@a.com", "role": "USER"}
    mocker.patch.object(AuthService, '_get_user_by_username', return_value=existing_user)

    # Act
    request = protos.auth_pb2.RegisterRequest(username="existinguser", password="password")
    response = grpc_stub.Register(request)

    # Assert
    assert response.success is False
    assert "User already exists" in response.message


def test_login_success(grpc_stub, mocker):
    """
    Tests successful login with correct credentials.
    """
    # Arrange: Mock the user data returned from the database
    username = "testuser"
    password = "password123"
    # The real service hashes the password, so we need to mock the stored hash
    import hashlib
    hashed_password = hashlib.sha256(password.encode()).hexdigest()
    
    user_data = {
        "username": username,
        "password": hashed_password,
        "email": "test@example.com",
        "role": "USER"
    }
    mocker.patch.object(AuthService, '_get_user_by_username', return_value=user_data)

    # Act
    request = protos.auth_pb2.LoginRequest(username=username, password=password)
    response = grpc_stub.Login(request)

    # Assert
    assert response.error == ""
    assert response.token != ""
    # You could add more assertions here to decode the JWT and check its payload


def test_login_invalid_credentials(grpc_stub, mocker):
    """
    Tests login failure with an incorrect password.
    """
    # Arrange
    username = "testuser"
    correct_password = "password123"
    wrong_password = "wrongpassword"
    
    import hashlib
    hashed_password = hashlib.sha256(correct_password.encode()).hexdigest()

    user_data = {"username": username, "password": hashed_password, "email": "test@example.com", "role": "USER"}
    mocker.patch.object(AuthService, '_get_user_by_username', return_value=user_data)

    # Act
    request = protos.auth_pb2.LoginRequest(username=username, password=wrong_password)
    response = grpc_stub.Login(request)

    # Assert
    assert "Invalid credentials" in response.error
    assert response.token == ""


def test_login_user_not_found(grpc_stub, mocker):
    """
    Tests login failure when the user does not exist.
    """
    # Arrange: Mock the database to return no user
    mocker.patch.object(AuthService, '_get_user_by_username', return_value=None)

    # Act
    request = protos.auth_pb2.LoginRequest(username="nouser", password="password")
    response = grpc_stub.Login(request)

    # Assert
    assert "Invalid credentials" in response.error
    assert response.token == ""
