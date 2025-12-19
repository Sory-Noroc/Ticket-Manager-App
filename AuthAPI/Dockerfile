FROM python:3.9-slim-buster
WORKDIR /app
COPY AuthAPI/requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt
COPY AuthAPI/ ./
EXPOSE 50051
CMD ["python", "server.py"]