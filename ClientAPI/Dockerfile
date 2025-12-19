# Stage 1: Generate gRPC client code from .proto file
FROM python:3.12-slim as proto-builder
WORKDIR /app
COPY ClientAPI/requirements.txt ./
RUN pip install -r requirements.txt
RUN mkdir protos
RUN touch protos/__init__.py
COPY AuthAPI/protos/auth.proto ./protos/
RUN python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. protos/auth.proto

# Stage 2: Final application image
FROM python:3.10-slim
WORKDIR /app
ENV PYTHONPATH /app:/app/protos

COPY ClientAPI/requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY ClientAPI/ ./

# Copy generated gRPC code from the builder stage
COPY --from=proto-builder /app/protos ./protos

EXPOSE 80
ENTRYPOINT ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "80"]