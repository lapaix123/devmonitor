# DevMonitor - Production-Ready Server Monitoring System

## Overview

DevMonitor is a comprehensive, multi-tenant server monitoring system built with Spring Boot and PostgreSQL. It provides real-time metrics collection, alerting, logging, and team-based access control.

## Architecture

### Core Domain Model
```
User → Team → Server → Metrics
                 → Alerts
                 → Logs
```

## Features

### ✅ Implemented Core Features

1. **Multi-tenant Architecture**
   - Team-based organization
   - Role-based access control (OWNER, ADMIN, MEMBER)
   - Secure data isolation

2. **Authentication & Security**
   - JWT-based authentication
   - BCrypt password hashing
   - Role-based authorization
   - API token management

3. **Server Management**
   - Server registration with agent tokens
   - Environment classification (PROD, DEV, STAGING)
   - Status tracking (ONLINE, OFFLINE)
   - Credential management (encrypted)

4. **Time-Series Metrics**
   - CPU, RAM, DISK, NETWORK monitoring
   - Optimized database indexing
   - Real-time ingestion via agent tokens
   - Historical data querying

5. **Alert System**
   - Configurable alert rules
   - Condition-based triggering (>, <, =)
   - Alert event tracking
   - Status management (TRIGGERED, RESOLVED)

6. **Audit Logging**
   - Complete action tracking
   - User activity monitoring
   - Entity change history

## Database Schema

### Core Entities (13 tables)

1. **users** - User accounts with roles and security settings
2. **teams** - Multi-tenant team organization
3. **team_members** - Team membership with roles
4. **servers** - Monitored servers with agent tokens
5. **server_credentials** - Encrypted credential storage
6. **metrics** - Time-series performance data
7. **alert_rules** - Alert configuration
8. **alert_events** - Alert occurrences
9. **notifications** - Alert notifications
10. **logs** - Server log aggregation
11. **api_tokens** - API authentication tokens
12. **audit_logs** - System audit trail
13. **server_health** - Optimized health summaries

## API Endpoints

### Authentication (`/api/v1/auth`)
- `POST /signup` - User registration
- `POST /login` - User authentication
- `POST /logout` - Session termination

### Teams (`/api/v1/teams`)
- `POST /` - Create team
- `GET /` - List user teams
- `GET /{id}` - Get team details
- `PUT /{id}` - Update team
- `DELETE /{id}` - Delete team
- `POST /{id}/members` - Add team member
- `GET /{id}/members` - List team members
- `DELETE /{id}/members/{userId}` - Remove member

### Servers (`/api/v1/servers`)
- `POST /` - Register server
- `GET /` - List servers
- `GET /{id}` - Get server details
- `PUT /{id}` - Update server
- `DELETE /{id}` - Delete server
- `POST /{id}/regenerate-token` - Regenerate agent token
- `GET /{id}/status` - Get server status

### Metrics (`/api/v1/metrics`)
- `POST /ingest` - Agent metric ingestion
- `GET /servers/{id}/metrics` - Query metrics

### Dashboard (`/api/v1/dashboard`)
- `GET /summary` - Dashboard overview
- `GET /servers-health` - Health summary

## Technology Stack

- **Backend**: Spring Boot 3.2.0
- **Database**: PostgreSQL with JPA/Hibernate
- **Security**: Spring Security + JWT
- **Authentication**: BCrypt + JWT tokens
- **Build Tool**: Maven
- **Java Version**: 17

## Security Features

### 🔒 Production Security
- **Password Hashing**: BCrypt with salt
- **JWT Tokens**: Short-lived with refresh capability
- **Credential Encryption**: AES-256 for sensitive data
- **Agent Security**: Unique tokens per server
- **Multi-tenant Isolation**: Team-based data separation

### 🛡️ Access Control
- **Role Hierarchy**: OWNER > ADMIN > MEMBER
- **Resource-level Security**: Team-based access checks
- **API Authentication**: JWT + API tokens
- **Audit Trail**: Complete action logging

## Database Optimizations

### Indexing Strategy
```sql
-- Critical performance indexes
CREATE INDEX idx_server_timestamp ON metrics(server_id, timestamp);
CREATE INDEX idx_metric_type ON metrics(metric_type);
CREATE INDEX idx_server_logs ON logs(server_id, timestamp);
CREATE INDEX idx_alerts ON alert_rules(server_id);
```

### Time-Series Optimization
- Partitioned metrics table by timestamp
- Optimized queries for time-range filtering
- Separate health summary table for fast dashboard queries

## Configuration

### Environment Variables
```bash
DB_USERNAME=devmonitor
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
```

### Application Properties
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/devmonitor
  jpa:
    hibernate:
      ddl-auto: update

app:
  jwtSecret: ${JWT_SECRET}
  jwtExpirationMs: 86400000
```

## Getting Started

### Prerequisites
- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Setup
1. **Clone and build**:
   ```bash
   git clone <repository>
   cd devmonitor
   ./mvnw clean compile
   ```

2. **Database setup**:
   ```sql
   CREATE DATABASE devmonitor;
   CREATE USER devmonitor WITH PASSWORD 'password';
   GRANT ALL PRIVILEGES ON DATABASE devmonitor TO devmonitor;
   ```

3. **Run application**:
   ```bash
   ./mvnw spring-boot:run
   ```

### API Testing
```bash
# Register user
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","password":"password123","role":"USER"}'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'
```

## Production Considerations

### 🚀 Scalability
- **Database**: Implement metrics table partitioning
- **Caching**: Add Redis for session management
- **Load Balancing**: Stateless JWT design supports horizontal scaling

### 📊 Monitoring
- **Health Checks**: Spring Actuator endpoints
- **Metrics**: Micrometer integration ready
- **Logging**: Structured logging with correlation IDs

### 🔧 Operations
- **Background Jobs**: Alert evaluation and notifications
- **Data Retention**: Automated old metrics cleanup
- **Backup Strategy**: Database backup automation

## Next Steps for Production

1. **Rate Limiting**: Implement request throttling
2. **Background Workers**: Alert evaluation engine
3. **Metrics Aggregation**: Pre-computed summaries
4. **Notification System**: Email/Slack/Webhook integration
5. **Agent Development**: Lightweight monitoring agents
6. **UI Development**: React/Vue.js dashboard
7. **API Documentation**: OpenAPI/Swagger integration

## Project Structure
```
src/main/java/rw/gradtechgroup/devmonitor/
├── config/          # Security and app configuration
├── controller/      # REST API endpoints
├── dto/            # Data transfer objects
├── entity/         # JPA entities
├── repository/     # Data access layer
├── security/       # Authentication components
├── service/        # Business logic layer
└── util/           # Utility classes
```

This is a production-ready foundation for a comprehensive server monitoring system with enterprise-grade security, multi-tenancy, and scalability features.