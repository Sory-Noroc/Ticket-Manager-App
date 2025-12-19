import grpc
import protos.auth_pb2 as auth_pb2
import protos.auth_pb2_grpc as auth_pb2_grpc

def run():
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = auth_pb2_grpc.AuthServiceStub(channel)

        print("--- Registering New User ---")
        response = stub.Register(auth_pb2.RegisterRequest(
            username="newuser",
            password="newpassword",
            email="newuser@example.com",
            role="user"
        ))
        print(f"Register Success: {response.success}, Message: {response.message}")

        print("\n--- Logging in as the registered user ---")
        response = stub.Login(auth_pb2.LoginRequest(
            username="newuser",
            password="newpassword"
        ))
        if response.token:
            print(f"Login Successful. Token: {response.token}")
            token = response.token
        else:
            print(f"Login Failed: {response.error}")
            return

        print("\n--- Validating Token ---")
        response = stub.ValidateToken(auth_pb2.ValidateRequest(token=token))
        print(f"Token Valid: {response.valid}, Error: {response.error}")

        print("\n--- Validating Invalid Token ---")
        response = stub.ValidateToken(auth_pb2.ValidateRequest(token="invalidtoken"))
        print(f"Token Valid: {response.valid}, Error: {response.error}")

        print("\n--- Invalidating Token ---")
        response = stub.InvalidateToken(auth_pb2.InvalidateRequest(token=token))
        print(f"Token Invalidated: {response.success}, Error: {response.error}")

if __name__ == '__main__':
    run()
