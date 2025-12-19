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
