import os
import grpc
from protos import auth_pb2
from protos import auth_pb2_grpc

AUTH_API_ADDRESS = os.getenv("AUTH_API_ADDRESS", "localhost:50051")

def validate_token(token: str) -> bool:
    """
    Calls the AuthAPI to validate a JWT.
    """
    try:
        channel = grpc.insecure_channel(AUTH_API_ADDRESS)
        stub = auth_pb2_grpc.AuthServiceStub(channel)
        request = auth_pb2.ValidateRequest(token=token)
        response = stub.ValidateToken(request)
        return response.valid
    except grpc.RpcError as e:
        print(f"Error validating token: {e}")
        return False

def login_user(username, password):
    try:
        channel = grpc.insecure_channel(AUTH_API_ADDRESS)
        stub = auth_pb2_grpc.AuthServiceStub(channel)
        request = auth_pb2.LoginRequest(username=username, password=password)
        response = stub.Login(request)
        return response
    except grpc.RpcError as e:
        print(f"Error logging in: {e}")
        return None

def register_user(username, password, email, role="client"):
    try:
        channel = grpc.insecure_channel(AUTH_API_ADDRESS)
        stub = auth_pb2_grpc.AuthServiceStub(channel)
        request = auth_pb2.RegisterRequest(username=username, password=password, email=email, role=role)
        response = stub.Register(request)
        return response
    except grpc.RpcError as e:
        print(f"Error registering user: {e}")
        return None
