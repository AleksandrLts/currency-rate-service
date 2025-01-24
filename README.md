# Currency Rate Service

## Description

The **Currency Rate Service** is a Spring Boot application that retrieves and combines fiat and cryptocurrency rates from external mock APIs. It processes data asynchronously using WebFlux, stores the data in a PostgreSQL database, and provides a consolidated response through a single endpoint.

---

## Features

- **Endpoint:** `GET /currency-rates`
    - Retrieves fiat and cryptocurrency rates from external APIs.
    - Falls back to database-stored data if APIs fail.
    - Returns empty arrays if no data is available from both APIs and the database.

- **Data Sources:**
    - Fiat API: Provides fiat currency rates.
    - Crypto API: Provides cryptocurrency rates.

- **Database:** PostgreSQL (run using Docker).

---

## Prerequisites

- **Java 21**
- **Docker** (for PostgreSQL setup)
- **Gradle** (build tool)

---

## Getting Started

### Clone the Repository
```bash
git clone https://github.com/AleksandrLts/currency-rate-service
cd currency-rate-service
cd currency-rate-service
```
### Set Up PostgreSQL with Docker
```bash
docker-compose up -d
```
### Build and Run the Application
```bash
./gradlew bootRun
```
This will start the application on http://localhost:8081

### Testing the Endpoint
```bash
curl http://localhost:8081/currency-rates
```


