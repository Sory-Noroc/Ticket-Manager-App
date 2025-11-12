FROM python:3.10.19-alpine3.22
WORKDIR /app
COPY main.py requirements.txt /app/
COPY model /app/model
LABEL authors="nobody"

RUN ["pip", "install", "-r", "requirements.txt"]
EXPOSE 80

ENTRYPOINT ["uvicorn",  "main:app", "--host", "0.0.0.0", "--port", "80"]
