# NASE Event Management System API Documentation

## Overview

NASE Event Management System API provides endpoints for event management, attendee verification, and geolocation-based attendance tracking. This document details all available endpoints for frontend integration.

## Base URL

All API requests should be prefixed with the base URL:

http://localhost:8081


## Authentication

Most endpoints do not require authentication as security has been disabled for development.

## API Endpoints

### Test Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/test` | Basic test endpoint to verify API is running |
| GET | `/api/test` | API test endpoint without authentication |
| GET | `/api/simple/verificar/{numero}` | Simple phone number verification |

### Authentication

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|-------------|----------|
| POST | `/api/auth/login` | Login with phone number | `{"numeroTelefono": "123456789"}` | User data with authentication status |
| GET | `/api/auth/verificar/{numeroTelefono}` | Verify if a number is registered | - | Registration status and user data if registered |
| POST | `/api/auth/verificar` | Verify if a number is registered (POST method) | `{"numeroTelefono": "123456789"}` | Registration status and user data if registered |

### Events

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|-------------|----------|
| GET | `/api/eventos` | Get all events | - | Array of events |
| GET | `/api/eventos/{id}` | Get event by ID | - | Event details |
| POST | `/api/eventos` | Create new event | Event object | Created event |
| PUT | `/api/eventos/{id}` | Update event | Event object | Updated event |
| DELETE | `/api/eventos/{id}` | Delete event | - | 204 No Content |

### People

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|-------------|----------|
| GET | `/api/personas` | Get all people | - | Array of people |
| GET | `/api/personas/{id}` | Get person by ID | - | Person details |
| POST | `/api/personas` | Create new person | Person object | Created person |
| PUT | `/api/personas/{id}` | Update person | Person object | Updated person |
| DELETE | `/api/personas/{id}` | Delete person | - | 204 No Content |
| POST | `/api/personas/{personaId}/evento/{eventoId}` | Register a person for an event | - | Updated person object |

### Attendance

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|-------------|----------|
| GET | `/api/asistencia/verificar/{personaId}` | Verify person's attendance at an event based on location | - | Attendance details |

### Phone Verification

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|-------------|----------|
| GET | `/check/{numero}` | Simple check if a number exists | - | Existence status |
| GET | `/api/telefono/verificar/{numero}` | Verify if a number exists in the database | - | Verification results |
| POST | `/api/telefono/verificar` | Verify a number (POST method) | `{"numero": "123456789"}` | Verification results |
| POST | `/api/telefono/verificar/{numero}` | Alternative phone verification | - | Verification results |

### User Verification

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|-------------|----------|
| GET | `/api/verificacion/usuario/{numeroTelefono}` | Verify if a number is registered and return associated data | - | Registration status and user data |
| GET | `/api/verificacion/usuario/{numeroTelefono}/evento/{eventoId}` | Verify if a user is present at a specific event | - | Presence verification |
| POST | `/api/verificacion/usuario/verificar` | Verify a user by number (POST method) | `{"numeroTelefono": "123456789"}` | Verification results |

### Dashboard

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|-------------|----------|
| GET | `/api/dashboard/evento/{eventoId}/stats` | Get statistics for an event | - | Event statistics (registrations, attendance) |
| GET | `/api/dashboard/evento/{eventoId}/asistentes` | Get attendees list for an event | - | List of attendees |

## Data Models

### Event Object

json
{
"id": 1,
"nombre": "Event Name",
"ubicacion": "Event Location",
"latitud": 40.416775,
"longitud": -3.703790,
"radioMetros": 100,
"fechaInicio": "2023-01-01T10:00:00",
"fechaFin": "2023-01-01T18:00:00"
}

### Person Object

json
{
"id": 1,
"nombre": "John Doe",
"numeroTelefono": "123456789",
"ipPublica": "203.0.113.1",
"ipPrivada": "192.168.1.100",
"ultimoAcceso": "2023-01-01T12:00:00",
"eventoRegistrado": { "id": 1, "nombre": "Event Name" },
"latitudUltima": 40.416775,
"longitudUltima": -3.703790,
"presenteEvento": true
}

## Nokia API Verification Examples

### Example 1: Verify a user's current phone matches their registered phone

POST http://localhost:8081/api/nokia/verificacion/persona/1
Content-Type: application/json
{
"numeroTelefono": "+34612345678"
}

Response:

json
{
"numeroActual": "+34612345678",
"personaId": 1,
"numeroRegistrado": "612345678",
"coincide": true,
"mensaje": "El número actual coincide con el número registrado"
}

### Example 2: Direct verification with a hash

POST http://localhost:8081/api/nokia/verificacion/directo
Content-Type: application/json
{
"numeroTelefono": "+34612345678",
"hashNumero": "32f67ab4e4312618b09cd23ed8ce41b13e095fe52b73b2e8da8ef49830e50dba"
}

Response:

json
{
"numero": "+34612345678",
"verificado": true,
"mensaje": "Verificación exitosa"
}

## Error Responses

All endpoints may return the following error responses:

- `400 Bad Request`: Invalid input data
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

Error responses include a JSON object with error details:

json
{
"error": "Error message description"
}

## Pagination

Most list endpoints do not implement pagination in this version.