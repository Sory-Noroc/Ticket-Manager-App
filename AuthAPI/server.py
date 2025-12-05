import grpc
from concurrent import futures
import time
import os
import hashlib
import jwt
import datetime
from dotenv import load_dotenv
import protos.auth_pb2 as auth_pb2
import protos.auth_pb2_grpc as auth_pb2_grpc

load_dotenv()

# python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. protos/auth.proto

import grpc
from concurrent import futures
import time
import os
import hashlib
import jwt
import datetime
from dotenv import load_dotenv
import protos.auth_pb2 as auth_pb2
import protos.auth_pb2_grpc as auth_pb2_grpc
import database

load_dotenv()

# python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. protos/auth.proto

class AuthService(auth_pb2_grpc.AuthServiceServicer):
    def __init__(self):
        self.blocklist = set()
        self.jwt_secret = os.getenv("JWT_SECRET")
        database.init_db()

    def _get_user_by_username(self, username):
        db = database.get_db_connection()
        cursor = db.cursor(dictionary=True)
        cursor.execute("SELECT * FROM users WHERE username = %s", (username,))
        user = cursor.fetchone()
        cursor.close()
        db.close()
        return user

    def Register(self, request, context):
        if self._get_user_by_username(request.username):
            return auth_pb2.RegisterResponse(success=False, message="User already exists")
        
        hashed_password = hashlib.sha256(request.password.encode()).hexdigest()
        
        db = database.get_db_connection()
        cursor = db.cursor()
        cursor.execute(
            "INSERT INTO users (username, password, email, role) VALUES (%s, %s, %s, %s)",
            (request.username, hashed_password, request.email, request.role)
        )
        db.commit()
        cursor.close()
        db.close()
        
        return auth_pb2.RegisterResponse(success=True, message="User registered successfully")

    def Login(self, request, context):
        user = self._get_user_by_username(request.username)
        if not user:
            return auth_pb2.LoginResponse(token="", error="Invalid credentials")
        
        hashed_password = hashlib.sha256(request.password.encode()).hexdigest()
        if user["password"] != hashed_password:
            return auth_pb2.LoginResponse(token="", error="Invalid credentials")
        
        payload = {
            "id": user["username"],
            "email": user["email"],
            "role": user["role"],
            "exp": datetime.datetime.utcnow() + datetime.timedelta(hours=1)
        }
        token = jwt.encode(payload, self.jwt_secret, algorithm="HS256")
        return auth_pb2.LoginResponse(token=token, error="")

    def ValidateToken(self, request, context):
        if request.token in self.blocklist:
            return auth_pb2.ValidateResponse(valid=False, error="Token invalidated")
        try:
            jwt.decode(request.token, self.jwt_secret, algorithms=["HS256"])
            return auth_pb2.ValidateResponse(valid=True, error="")
        except jwt.ExpiredSignatureError:
            return auth_pb2.ValidateResponse(valid=False, error="Token expired")
        except jwt.InvalidTokenError:
            return auth_pb2.ValidateResponse(valid=False, error="Invalid token")
    
    def InvalidateToken(self, request, context):
        self.blocklist.add(request.token)
        return auth_pb2.InvalidateResponse(success=True)

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    auth_pb2_grpc.add_AuthServiceServicer_to_server(AuthService(), server)
    server.add_insecure_port('[::]:50051')
    print("Server started on port 50051")
    server.start()
    try:
        while True:
            time.sleep(86400)
    except KeyboardInterrupt:
        server.stop(0)

if __name__ == '__main__':
    serve()
