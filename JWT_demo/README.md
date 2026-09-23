# JWT_demo - Microservices & SOA Authentication Demo

A complete Spring Boot 3.3.x microservice application demonstrating **JSON Web Token (JWT)** authentication, token generation, signature validation, authorization filters, role-based access control (RBAC), and interactive browser testing.

---

## 🌟 Key Features

1. **Stateless Authentication**: Uses `io.jsonwebtoken` (JJWT 0.12.5) for HMAC-SHA256 token creation and validation.
2. **Spring Security Integration**: Stateless `SessionCreationPolicy.STATELESS` filter chain using custom `JwtAuthenticationFilter`.
3. **Role-Based Access Control (RBAC)**:
   - `ROLE_USER`: Access standard protected resources.
   - `ROLE_ADMIN`: Access administrative endpoints.
4. **H2 In-Memory Database**: Embedded database storing registered user profiles with BCrypt password hashing.
5. **Interactive Web Dashboard**: Embedded glassmorphism frontend at `http://localhost:8080` to register, log in, decode tokens live, and test secured endpoints.

---

## 📁 Project Structure

```
JWT_demo/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/klu/jwtdemo/
        │       ├── JwtDemoApplication.java
        │       ├── config/
        │       │   └── SecurityConfig.java
        │       ├── controller/
        │       │   ├── AuthController.java
        │       │   └── DemoController.java
        │       ├── dto/
        │       │   ├── AuthRequest.java
        │       │   └── AuthResponse.java
        │       ├── model/
        │       │   └── UserEntity.java
        │       ├── repository/
        │       │   └── UserRepository.java
        │       └── security/
        │           ├── CustomUserDetailsService.java
        │           ├── JwtAuthenticationFilter.java
        │           └── JwtUtil.java
        └── resources/
            ├── application.properties
            └── static/
                └── index.html
```

---

## 🚀 Getting Started

### 1. Compile & Build
From the project root directory (`d:\3 - 1\SOA programming and microservices\JWT_demo`):
```bash
mvn clean compile
```

### 2. Run Application
```bash
mvn spring-boot:run
```

The server will start on port `8080`.

---

## 🔗 Endpoints Reference

| Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new user with username, password, email & role (`USER` / `ADMIN`) |
| `POST` | `/api/auth/login` | Public | Authenticate user & return signed JWT Bearer Token |
| `GET` | `/api/auth/profile` | Authenticated | Retrieve authenticated user profile from SecurityContext |
| `GET` | `/api/demo/public` | Public | Open endpoint accessible without token |
| `GET` | `/api/demo/user` | User / Admin | Accessible by users with `ROLE_USER` or `ROLE_ADMIN` |
| `GET` | `/api/demo/admin` | Admin Only | Restricted to users with `ROLE_ADMIN` |

---

## 🧪 Testing with cURL / Postman

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe", "password":"password123", "email":"john@example.com", "role":"USER"}'
```

### 2. Login to get JWT Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe", "password":"password123"}'
```

### 3. Access Protected Endpoint using Token
```bash
curl -X GET http://localhost:8080/api/demo/user \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

---

## 🗄️ H2 Database Console
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:jwtdb`
- **Username**: `sa`
- **Password**: *(leave empty)*
