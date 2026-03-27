# User Service - Cafeteria Management System

> User Authentication & Management with JWT Token-based Security

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.java.net/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)

## 📋 Overview

The User Service handles all user-related operations including authentication, authorization, user registration, and profile management. It implements JWT (JSON Web Token) based authentication for secure API access across the Cafeteria Management System.

## 🚀 Features

- **JWT Authentication**: Secure token-based authentication
- **User Registration**: New user account creation with validation
- **User Login**: Email/password authentication with JWT token generation
- **Password Encryption**: BCrypt hashing for secure password storage
- **Role-Based Access**: Support for multiple user roles (CUSTOMER, STAFF, ADMIN, KITCHEN_STAFF)
- **Profile Management**: Update user information and preferences
- **Token Refresh**: Renew JWT tokens without re-authentication
- **Email Validation**: Unique email constraint and format validation
- **Service Discovery**: Registered with Eureka for discoverability

## 🛠️ Tech Stack

| Technology                         | Version           | Purpose                           |
| ---------------------------------- | ----------------- | --------------------------------- |
| Java                               | 25                | Programming Language              |
| Spring Boot                        | 4.0.3             | Application Framework             |
| Spring Cloud Config Client         | 2025.1.0          | Centralized Configuration         |
| Spring Cloud Netflix Eureka Client | 2025.1.0          | Service Discovery                 |
| Spring Data JPA                    | 4.0.3             | Database Access Layer             |
| PostgreSQL                         | 16                | Relational Database               |
| JJWT                               | 0.12.6            | JWT Token Generation & Validation |
| BCrypt                             | (Spring Security) | Password Hashing                  |
| Maven                              | 3.9+              | Build Tool                        |

## 📡 Service Configuration

| Property                | Value                   |
| ----------------------- | ----------------------- |
| **Service Name**        | `user-service`          |
| **Port**                | `8081`                  |
| **Database**            | PostgreSQL              |
| **Database Name**       | `cafeteria_users`       |
| **Eureka Registration** | Yes                     |
| **Config Server**       | `http://localhost:9000` |

## 💾 Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role ENUM('CUSTOMER', 'STAFF', 'ADMIN', 'KITCHEN_STAFF') NOT NULL DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);
```

### User Roles

| Role              | Description            | Permissions                                        |
| ----------------- | ---------------------- | -------------------------------------------------- |
| **CUSTOMER**      | Regular cafeteria user | Browse menu, place orders, view own orders         |
| **STAFF**         | Cafeteria staff member | Manage menu items, view all orders                 |
| **ADMIN**         | System administrator   | Full system access, user management                |
| **KITCHEN_STAFF** | Kitchen employee       | View and update order status, manage kitchen queue |

## 📦 Installation & Setup

### Prerequisites

- Java 25
- Maven 3.9+
- PostgreSQL 16
- Port 8081 available
- Config Server running on port 9000
- Service Registry running on port 8761

### Database Setup

```bash
# Create database
psql -U postgres
CREATE DATABASE cafeteria_users;

# Run initialization script (if provided)
psql -U postgres -d cafeteria_users -f init-scripts/postgres/01_create_users_table.sql
```

### Build

```bash
mvn clean install
```

### Run Locally

```bash
mvn spring-boot:run
```

## 🔧 Configuration

### application.yml (Local)

```yaml
server:
  port: 8081

spring:
  application:
    name: user-service
  config:
    import: optional:configserver:http://localhost:9000

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### user-service.yml (Config Server)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cafeteria_users?useSSL=false&serverTimezone=UTC
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgrespassword}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQL8Dialect
        format_sql: true

# JWT Configuration
jwt:
  secret: ${JWT_SECRET:your-secret-key-min-256-bits-long-for-hs256}
  expiration: 86400000 # 24 hours in milliseconds
  refresh-expiration: 604800000 # 7 days in milliseconds
```

## 🌐 API Endpoints

### Authentication Endpoints

#### Register New User

```http
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "role": "CUSTOMER"
}
```

**Response:**

```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "CUSTOMER",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "CUSTOMER"
  }
}
```

#### Refresh Token

```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### User Management Endpoints

#### Get Current User

```http
GET /users/me
Authorization: Bearer <JWT_TOKEN>
```

#### Get User by ID (Admin only)

```http
GET /users/{id}
Authorization: Bearer <JWT_TOKEN>
```

#### Update User Profile

```http
PUT /users/{id}
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "phone": "+1234567890"
}
```

#### Change Password

```http
POST /users/{id}/change-password
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "currentPassword": "OldPass123!",
  "newPassword": "NewSecurePass456!"
}
```

#### List All Users (Admin only)

```http
GET /users?page=0&size=20&role=CUSTOMER
Authorization: Bearer <JWT_TOKEN>
```

#### Deactivate User (Admin only)

```http
DELETE /users/{id}
Authorization: Bearer <JWT_TOKEN>
```

## 🔐 JWT Authentication

### Token Structure

**Header:**

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload:**

```json
{
  "sub": "1",
  "email": "user@example.com",
  "role": "CUSTOMER",
  "iat": 1678901234,
  "exp": 1678987634
}
```

### JWT Configuration Class Example

```java
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

## 🧪 Testing

### cURL Examples

#### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User",
    "role": "CUSTOMER"
  }'
```

#### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!"
  }'
```

#### Get Current User

```bash
TOKEN="your-jwt-token-here"
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN"
```

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn verify -Pintegration-tests
```

## 🐳 Docker Deployment

### Dockerfile

```dockerfile
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY target/user-service-1.0.0.jar app.jar
EXPOSE 8081
ENV DB_HOST=postgres
ENV DB_PORT=5432
ENV DB_NAME=cafeteria_users
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
user-service:
  build: ./services/user-service
  ports:
    - '8081:8081'
  depends_on:
    - postgres
    - config-server
    - service-registry
  environment:
    - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/cafeteria_users
    - SPRING_DATASOURCE_USERNAME=postgres
    - SPRING_DATASOURCE_PASSWORD=postgrespassword
    - JWT_SECRET=${JWT_SECRET}
```

## ☁️ Cloud Deployment (GCP)

### Environment Variables

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://${DB_IP}:5432/cafeteria_users
export SPRING_DATASOURCE_USERNAME=${DB_USER}
export SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
export JWT_SECRET=${JWT_SECRET_256_BITS}
export EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://${EUREKA_IP}:8761/eureka/
```

### PM2 Configuration

```javascript
{
  name: 'user-service',
  script: 'java',
  args: ['-jar', 'services/user-service/target/user-service-1.0.0.jar'],
  env: {
    SERVER_PORT: 8081,
    SPRING_DATASOURCE_URL: 'jdbc:postgresql://postgres-instance:5432/cafeteria_users',
    JWT_SECRET: process.env.JWT_SECRET
  }
}
```

## 🔒 Security Best Practices

### Password Requirements

- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character

### JWT Secret Management

- **Development**: Use placeholder in config
- **Production**: Store in environment variables or secret manager
- Minimum 256 bits (32 characters) for HS256 algorithm

### Production Security Checklist

✅ Use HTTPS for all communication
✅ Store JWT secret in Google Secret Manager
✅ Implement rate limiting on authentication endpoints
✅ Add account lockout after failed login attempts
✅ Enable SQL injection protection (Spring Security default)
✅ Implement CSRF protection
✅ Add request logging and monitoring
✅ Regular security audits

## 📊 Monitoring

### Health Check

```bash
curl http://localhost:8081/actuator/health
```

### Metrics

```bash
# Database connection pool metrics
curl http://localhost:8081/actuator/metrics/hikaricp.connections.active

# JVM metrics
curl http://localhost:8081/actuator/metrics/jvm.memory.used
```

## 🐛 Troubleshooting

### Database Connection Issues

```bash
# Test PostgreSQL connection
psql -h localhost -U postgres -d cafeteria_users

# Check service logs
tail -f logs/user-service.log

# Verify database configuration
curl http://localhost:8081/actuator/env | jq '.propertySources[] | select(.name | contains("datasource"))'
```

### JWT Token Issues

**Invalid Token**: Verify token structure and signature

```bash
# Decode JWT (header.payload.signature)
echo "eyJhbGc..." | base64 -d
```

**Token Expired**: Check expiration time in configuration

```yaml
jwt:
  expiration: 86400000 # 24 hours
```

## 📚 Additional Resources

- [JWT.io](https://jwt.io/) - JWT Token Debugger
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)

## 🔗 Service Integration

### Called By

- **API Gateway**: Routes all `/api/auth/**` and `/api/users/**` requests
- **Order Service**: Validates user existence via Feign client
- **Kitchen Service**: Validates staff permissions

### Database Dependencies

- **PostgreSQL**: Primary data storage for user information

### Service Discovery

- **Registers with**: Eureka Service Registry (8761)
- **Fetches config from**: Config Server (9000)

## 📄 License

This project is part of the ITS 2130 Enterprise Cloud Architecture course final project.

---

**Part of**: [Cafeteria Management System](../README.md)
**Service Type**: Business Service (Authentication & User Management)
**Maintained By**: ITS 2130 Project Team
