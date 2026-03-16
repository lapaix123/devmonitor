# DevMonitor API Documentation

## Overview
DevMonitor is a production-ready server monitoring system with comprehensive REST APIs for multi-tenant server monitoring, alerting, and logging.

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
All endpoints (except auth and health) require JWT Bearer token authentication.

```bash
Authorization: Bearer <jwt_token>
```

## API Documentation
Interactive API documentation is available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Complete API Endpoints

### 🔐 Authentication APIs (`/auth`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/signup` | Create new user account |
| POST | `/auth/login` | Authenticate user and return JWT |
| POST | `/auth/logout` | Invalidate current session/token |
| POST | `/auth/refresh` | Refresh access token |
| POST | `/auth/verify-email` | Verify user email |
| POST | `/auth/forgot-password` | Request password reset |
| POST | `/auth/reset-password` | Reset user password |

### 👤 User APIs (`/users`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users/me` | Get current user profile |
| PUT | `/users/me` | Update user profile |
| DELETE | `/users/me` | Delete user account |
| GET | `/users` | List all users (admin) |
| GET | `/users/{id}` | Get specific user (admin) |

### 👥 Team APIs (`/teams`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/teams` | Create new team |
| GET | `/teams` | List user teams |
| GET | `/teams/{id}` | Get team details |
| PUT | `/teams/{id}` | Update team info |
| DELETE | `/teams/{id}` | Delete team |

### 👥 Team Members APIs (`/teams/{id}/members`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/teams/{id}/members` | Add member to team |
| GET | `/teams/{id}/members` | List team members |
| PUT | `/teams/{id}/members/{userId}` | Update member role |
| DELETE | `/teams/{id}/members/{userId}` | Remove member from team |

### 🖥️ Server APIs (`/servers`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/servers` | Add new server |
| GET | `/servers` | List all user servers |
| GET | `/servers/{id}` | Get server details |
| PUT | `/servers/{id}` | Update server info |
| DELETE | `/servers/{id}` | Delete server |
| POST | `/servers/{id}/regenerate-token` | Generate new agent token |
| GET | `/servers/{id}/status` | Get server online/offline status |

### 🔐 Server Credentials APIs (`/servers/{id}/credentials`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/servers/{id}/credentials` | Add encrypted credentials |
| GET | `/servers/{id}/credentials` | List server credentials metadata |
| DELETE | `/servers/{id}/credentials/{credId}` | Delete credentials |

### 📊 Metrics APIs (`/metrics`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/metrics/ingest` | Receive metrics from agent |
| GET | `/servers/{id}/metrics` | Get server metrics (filtered) |

### 🚨 Alert Rules APIs (`/alerts/rules`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/alerts/rules` | Create alert rule |
| GET | `/alerts/rules` | List alert rules |
| PUT | `/alerts/rules/{id}` | Update alert rule |
| DELETE | `/alerts/rules/{id}` | Delete alert rule |

### 🚨 Alert Events APIs (`/alerts/events`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/alerts/events` | List triggered alerts |
| GET | `/alerts/events/{id}` | Get alert event details |

### 📜 Logs APIs (`/logs`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/logs/ingest` | Receive logs from agent |
| GET | `/servers/{id}/logs` | Fetch server logs |

### 🔔 Notifications APIs (`/notifications`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/notifications` | List user notifications |
| PUT | `/notifications/{id}/read` | Mark notification as read |

### 🔑 API Tokens APIs (`/tokens`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/tokens` | Create API token |
| GET | `/tokens` | List API tokens |
| DELETE | `/tokens/{id}` | Delete API token |

### 📊 Dashboard APIs (`/dashboard`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/dashboard/summary` | Get overall system summary |
| GET | `/dashboard/servers-health` | Get servers health overview |

### 🧾 Audit Log APIs (`/audit-logs`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/audit-logs` | List user activity logs |

### ❤️ Health & System APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Check API status |
| GET | `/version` | Get API version info |

## Request/Response Examples

### Authentication
```bash
# Signup
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "USER"
  }'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Server Management
```bash
# Add Server
curl -X POST http://localhost:8080/api/v1/servers \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "teamId": "uuid-here",
    "name": "Production Server",
    "ipAddress": "192.168.1.10",
    "port": 22,
    "os": "Ubuntu 22.04",
    "environment": "PROD"
  }'
```

### Metrics Ingestion
```bash
# Agent Metrics
curl -X POST http://localhost:8080/api/v1/metrics/ingest \
  -H "Content-Type: application/json" \
  -d '{
    "agentToken": "agent-token-here",
    "metrics": [
      {"type": "CPU", "value": 45.2},
      {"type": "RAM", "value": 78.5},
      {"type": "DISK", "value": 65.0}
    ]
  }'
```

## Error Responses
All endpoints return consistent error responses:
```json
{
  "error": "Error message description",
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 400
}
```

## Rate Limiting
- 1000 requests per hour per user
- 100 requests per minute for metrics ingestion

## Security Features
- JWT authentication with 24-hour expiration
- BCrypt password hashing
- Role-based access control
- Multi-tenant data isolation
- Encrypted credential storage
- Complete audit logging

## Getting Started
1. Start the application: `./mvnw spring-boot:run`
2. Access Swagger UI: `http://localhost:8080/swagger-ui.html`
3. Create user account via `/auth/signup`
4. Login to get JWT token via `/auth/login`
5. Use token in Authorization header for all API calls