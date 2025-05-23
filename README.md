# Risk Register Application

A Java application for managing project risks with desktop UI and REST API.

## Components

- **RiskEntity**: Data model for risk items
- **RiskPersistence**: Database operations for risks
- **RiskService**: Business logic for risk management
- **ConnectionManager**: Handles database connections with connection pooling
- **ConfigLoader**: Manages application configuration
- **RiskRegisterApp**: Swing-based GUI frontend
- **RiskRegisterAPI**: REST API with multi-threading support
- **APIClient**: Demo client for API usage

## Setup

1. Ensure you have MySQL installed and running
2. Create a database named `risks`
3. Configure the application by editing `config.properties`:
   ```properties
   # Database Configuration
   db.host=127.0.0.1:3306
   db.name=risks
   db.user=root
   db.password=yourpassword

   # API Configuration
   api.port=8080

   # Security Configuration
   admin.password=547
   ```
4. Create the database schema with the following table:
   ```sql
   CREATE TABLE risk (
     riskID INT,
     projectID INT,
     title VARCHAR(255) NOT NULL,
     description TEXT,
     likelihood VARCHAR(50),
     impact VARCHAR(50),
     mitigation_plan TEXT,
     owner VARCHAR(255),
     status VARCHAR(50),
     PRIMARY KEY (riskID, projectID)
   );
   ```
5. Compile the application with the required dependencies:
   - JSON Simple library (json-simple-1.1.1.jar)
   - MySQL Connector/J (mysql-connector-java-8.0.x.jar)

## Running the Application

### Desktop UI

```
java RiskRegisterApp
```

### REST API Server

```
java RiskRegisterAPI
```

The API server runs on the port specified in config.properties (default: 8080).

## API Endpoints

### Health Check
```
GET /api/health
```

### Get All Risks
```
GET /api/risks
```

### Get a Specific Risk
```
GET /api/risks?id={riskID}&pid={projectID}
```

### Create a New Risk
```
POST /api/risks

Request Body:
{
  "riskID": 1001,
  "projectID": 5001,
  "title": "Security Vulnerability",
  "description": "Potential security vulnerability in authentication module",
  "likelihood": "MEDIUM",
  "impact": "HIGH",
  "mitigationPlan": "Implement additional security measures",
  "owner": "John Doe",
  "status": "OPEN"
}
```

### Update an Existing Risk
```
PUT /api/risks

Request Body:
{
  "riskID": 1001,
  "projectID": 5001,
  "title": "Updated Security Vulnerability",
  "description": "Updated description",
  "likelihood": "HIGH",
  "impact": "CRITICAL"
}
```
Note: Only riskID and projectID are required for updates. Other fields are optional.

### Delete a Risk
```
DELETE /api/risks/delete?id={riskID}&pid={projectID}
```

## API Client Example

See `APIClient.java` for examples of how to use the API from Java code.

## Database Schema

The `risks` database contains a `risk` table with the following structure:

- `riskID`: Risk identifier (primary key)
- `projectID`: Project identifier (part of composite key)
- `title`: Risk title
- `description`: Risk description
- `likelihood`: Probability of the risk occurring (LOW, MEDIUM, HIGH)
- `impact`: Impact if the risk occurs (LOW, MEDIUM, HIGH, CRITICAL)
- `mitigation_plan`: Plan to mitigate the risk
- `owner`: Person responsible for managing the risk
- `status`: Current status of the risk (e.g., OPEN, MITIGATED, CLOSED)

## Features

- Multi-threaded API server for better performance
- Connection pooling for database operations
- Externalized configuration
- Improved input validation
- CORS support for cross-origin requests
- Health check endpoint for monitoring
- Full CRUD operations (Create, Read, Update, Delete)
- Detailed error responses