import mysql.connector
import os
import time

def get_db_connection():
    return mysql.connector.connect(
        host=os.getenv("MYSQL_HOST"),
        user=os.getenv("MYSQL_USER"),
        password=os.getenv("MYSQL_PASSWORD"),
        database=os.getenv("MYSQL_DATABASE")
    )

def init_db():
    retries = 10
    delay = 3
    for i in range(retries):
        try:
            db = get_db_connection()
            cursor = db.cursor()
            print("AuthAPI: Successfully connected to the database.")
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    role VARCHAR(50) NOT NULL
                )
            """)
            db.commit()
            cursor.close()
            db.close()
            return  # On success, exit the function
        except mysql.connector.errors.DatabaseError as err:
            if i < retries - 1:
                print(f"AuthAPI: DB connection failed ({err}). Retrying in {delay}s...")
                time.sleep(delay)
            else:
                print("AuthAPI: Could not connect to database after multiple retries. Exiting.")
                raise err

