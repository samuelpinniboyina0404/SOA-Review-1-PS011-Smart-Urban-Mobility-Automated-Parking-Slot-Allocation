# ParkGrid Solutions: Smart Urban Mobility & Automated Parking Slot Allocation
## SOA Review 2 - Microservices Architecture, JWT Security & Load-Balanced CRUD

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-blue.svg)](https://spring.io/projects/spring-cloud)
[![Security](https://img.shields.io/badge/Security-JWT%20%2F%20HMAC--SHA256-orange.svg)](https://jwt.io/)
[![Load Balancing](https://img.shields.io/badge/Load%20Balancing-Spring%20Cloud%20LoadBalancer-blueviolet.svg)](https://spring.io/projects/spring-cloud-commons)

---

## 📑 Table of Contents
1. [Project Overview](#-project-overview)
2. [Component 1: Complete Microservices Implementation](#1️⃣-component-1-microservice-service-implementation)
3. [Component 2: Robust & Secure JWT Authentication](#2️⃣-component-2-jwt-authentication)
4. [Component 3: CRUD Operations with Load Balancing](#3️⃣-component-3-crud-operations-with-load-balancing)
5. [Architecture Diagram](#-architecture-diagram)
6. [API Endpoints Reference Table](#-api-endpoints-reference-table)
7. [Running the Application](#-running-the-application)
8. [Interactive Browser UI & Load Balancer Visualizer](#-interactive-browser-ui--load-balancer-visualizer)
9. [Review 2 Verification & Test Suite](#-review-2-verification--test-suite)

---

## 🌟 Project Overview
**ParkGrid Solutions** is an automated smart urban mobility parking slot allocation and dynamic tariff billing system built following modern Service-Oriented Architecture (SOA) and Cloud-Native Microservices principles.

It resolves metropolitan parking congestion and eliminates slot race-conditions with:
- **Zero Double-Booking Guarantee**: Atomic pessimistic write-locks and booking status lifecycle management.
- **Dynamic Tariff Engine**: Vehicle type category multipliers (EV charging, SUV, Bike, Car), peak-hour surcharges (1.25x), and tiered overstay fees.
- **Microservices Perimeter Security**: Centralized API Gateway edge router validating JSON Web Tokens (JWT) and enforcing Role-Based Access Control (RBAC).
- **Client-Side Load Balancing**: Round-Robin load distribution between replicated microservice instances via Spring Cloud LoadBalancer and Eureka Service Discovery.

---

## 1️⃣ Component 1: Microservice Service Implementation

The system is decomposed into 5 domain microservices, an edge router, and a service discovery server:

| Microservice | Default Port | Responsibility & Technology |
|---|---|---|
| **`eureka-server`** | `8761` | Netflix Eureka Service Registry where all microservices register dynamically. |
| **`api-gateway`** | `8080` | Spring Cloud Gateway edge router. Performs URI routing (`lb://`), CORS handling, and JWT validation. |
| **`auth-service`** | `8081` | User registration, login, BCrypt password hashing, JWT token issuance, profile retrieval, and user CRUD. |
| **`parking-service`** | `8082`, `8085` | Zone and Slot inventory management, availability tracking, status transitions, and occupancy metrics. |
| **`booking-service`** | `8083` | Advance reservations, zero double-booking concurrency control, arrival check-in, check-out orchestration. |
| **`billing-service`** | `8084` | Dynamic pricing calculation, automated exit invoice generation, payment processing (UPI/CARD), revenue stats. |
| **`parkgrid-frontend`** | Browser | Modern glassmorphism UI with real API integration, live JWT inspector, and cluster load balancing monitor. |

---

## 2️⃣ Component 2: JWT Authentication

### Security Architecture & Implementation
1. **Cryptographic Signing (HMAC-SHA256)**:
   - Uses JJWT `0.12.5` (`io.jsonwebtoken`) with a 256-bit secure secret key.
   - Tokens include `sub` (email), `userId`, `name`, `role` (`ROLE_ADMIN` / `ROLE_USER`), `iat` (issued at), and `exp` (expiration).
2. **Password Security**:
   - Stored passwords hashed with `BCryptPasswordEncoder` (strength 10). Passwords are never stored in plaintext.
3. **Gateway Edge Security Filter (`JwtAuthenticationFilter`)**:
   - Intercepts all incoming HTTP traffic at the API Gateway (`Ordered.HIGHEST_PRECEDENCE`).
   - Whitelists public authentication and health check paths (`/api/auth/login`, `/api/auth/register`, `/api/auth/validate`, `/instance-info`).
   - Verifies the `Authorization: Bearer <token>` header, signature authenticity, and expiration timestamp.
   - **Role-Based Access Control (RBAC)**:
     - Admin-only routes (Zone/Slot/User CRUD, analytics, all bookings) are strictly restricted to `ROLE_ADMIN`.
     - Standard driver routes (availability, reservation, my bookings, checkout, my invoices, payment) permitted for `ROLE_USER`.
   - **Downstream Context Propagation**:
     - Extracts claims and injects trusted identity headers (`X-User-Id`, `X-User-Email`, `X-User-Role`) into downstream requests.
4. **Token Introspection & Profile Endpoints**:
   - `GET /api/auth/validate`: Verifies token status and returns user claims.
   - `GET /api/auth/profile`: Returns authenticated user profile.

---

## 3️⃣ Component 3: CRUD Operations with Load Balancing

### 1. Full CRUD Capabilities Across All Entities

- **Zones (`parking-service`)**:
  - `POST /api/parking/zones` (Create)
  - `GET /api/parking/zones`, `GET /api/parking/zones/{id}` (Read)
  - `PUT /api/parking/zones/{id}` (Update)
  - `DELETE /api/parking/zones/{id}` (Delete)
- **Slots (`parking-service`)**:
  - `POST /api/parking/zones/{zoneId}/slots` (Create)
  - `GET /api/parking/slots`, `GET /api/parking/slots/{id}`, `GET /api/parking/slots/available` (Read)
  - `PUT /api/parking/slots/{id}`, `PUT /api/parking/slots/{id}/status` (Update)
  - `DELETE /api/parking/slots/{id}` (Delete)
- **Bookings (`booking-service`)**:
  - `POST /api/bookings` (Create)
  - `GET /api/bookings/my`, `GET /api/bookings/{id}`, `GET /api/bookings/all` (Read)
  - `PUT /api/bookings/{id}`, `POST /api/bookings/{id}/check-in`, `/checkout`, `/cancel` (Update)
  - `DELETE /api/bookings/{id}` (Delete)
- **Invoices (`billing-service`)**:
  - `POST /api/billing/calculate-and-invoice`, `POST /api/billing/invoices` (Create)
  - `GET /api/billing/invoices/my`, `GET /api/billing/invoices/{id}`, `GET /api/billing/invoices` (Read)
  - `POST /api/billing/invoices/{id}/pay`, `PUT /api/billing/invoices/{id}` (Update)
  - `DELETE /api/billing/invoices/{id}` (Delete)
- **Users (`auth-service`)**:
  - `POST /api/auth/register` (Create)
  - `GET /api/auth/profile`, `GET /api/auth/users`, `GET /api/auth/users/{id}` (Read)
  - `PUT /api/auth/users/{id}` (Update)
  - `DELETE /api/auth/users/{id}` (Delete)

### 2. Client-Side Load Balancing
- **API Gateway Routing**:
  The API Gateway defines routes with `lb://parking-service`, `lb://booking-service`, `lb://billing-service`, and `lb://auth-service`. Spring Cloud LoadBalancer discovers healthy registered instances from Eureka and alternates requests using **Round-Robin Load Balancing**.
- **Inter-Service Communication**:
  `booking-service` connects to `parking-service` and `billing-service` using declarative `@FeignClient(name = "parking-service")` backed by `spring-cloud-starter-loadbalancer`. A `@LoadBalanced RestTemplate` bean is also configured.
- **Instance Tracking & Proof of Load Balancing**:
  Every microservice instance injects an `X-Handled-By: <service-name>:<port>` response header and exposes `GET /api/<service>/instance-info`. Callers can directly observe alternating port assignments (e.g., `8082` vs `8085`).

---

## 🏛 Architecture Diagram

```
                              [ Browser / Client UI ]
                                        |
                            HTTP / JWT Bearer Tokens
                                        v
                    +---------------------------------------+
                    |   Spring Cloud API Gateway (Port 8080)|
                    | - JwtAuthenticationFilter             |
                    | - Role-Based Access Control (RBAC)    |
                    | - Spring Cloud LoadBalancer (lb://)   |
                    +---------------------------------------+
                           |           |           |           |
            +--------------+           |           |           +--------------+
            |                          |           |                          |
            v                          v           v                          v
    +----------------+       +-------------------+ +-------------------+ +----------------+
    |  auth-service  |       |  parking-service  | |  booking-service  | |billing-service |
    |  (Port 8081)   |       |  Instance 1 (8082)| |  (Port 8083)      | |  (Port 8084)   |
    |  - JWT Engine  |       |  Instance 2 (8085)| |  - Feign Client   | |  - Pricing     |
    |  - BCrypt Hash |       |  - Zone/Slot CRUD | |  - Concurrency    | |  - Invoicing   |
    |  - User CRUD   |       |  - Pessimistic Lk | |  - Bookings CRUD  | |  - Payment     |
    +----------------+       +-------------------+ +-------------------+ +----------------+
            ^                          ^                   ^                   ^
            |                          |                   |                   |
            +--------------------------+-------------------+-------------------+
                                       |
                           Register & Discover via Heartbeat
                                       v
                    +---------------------------------------+
                    |      Eureka Server (Port 8761)        |
                    |  - Service Registry & Heartbeat Health|
                    +---------------------------------------+
```

---

## 📊 API Endpoints Reference Table

### 1. Authentication & Security (`auth-service` via Gateway `:8080/api/auth`)
| Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new driver or admin with BCrypt hashed password |
| `POST` | `/api/auth/login` | Public | Authenticate user credentials and return signed JWT Bearer Token |
| `GET` | `/api/auth/validate` | Public | Validate JWT signature, check expiration, and decode claims |
| `GET` | `/api/auth/profile` | Authenticated | Retrieve authenticated user profile from token |
| `GET` | `/api/auth/users` | Admin Only | List all registered users (CRUD Read) |
| `GET` | `/api/auth/users/{id}` | Admin Only | Get user profile by ID |
| `PUT` | `/api/auth/users/{id}` | Admin Only | Update user name, email, or role (CRUD Update) |
| `DELETE` | `/api/auth/users/{id}` | Admin Only | Delete user profile (CRUD Delete) |
| `GET` | `/api/auth/instance-info` | Public | Instance identifier and port for load balancing |

### 2. Parking Inventory (`parking-service` via Gateway `:8080/api/parking`)
| Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `GET` | `/api/parking/zones` | Authenticated | List all parking zones |
| `GET` | `/api/parking/zones/{id}` | Authenticated | Get parking zone details |
| `POST` | `/api/parking/zones` | Admin Only | Create new parking zone (CRUD Create) |
| `PUT` | `/api/parking/zones/{id}` | Admin Only | Update parking zone details (CRUD Update) |
| `DELETE` | `/api/parking/zones/{id}` | Admin Only | Delete parking zone and child slots (CRUD Delete) |
| `GET` | `/api/parking/slots` | Authenticated | List all parking slots across zones |
| `GET` | `/api/parking/slots/available` | Authenticated | Query available slots by zone or vehicle type |
| `GET` | `/api/parking/slots/{id}` | Authenticated | Get slot details by ID |
| `POST` | `/api/parking/zones/{zoneId}/slots`| Admin Only | Add slot to zone (CRUD Create) |
| `PUT` | `/api/parking/slots/{id}` | Admin Only | Update slot configuration (CRUD Update) |
| `PUT` | `/api/parking/slots/{id}/status` | Admin Only | Update status (`AVAILABLE`, `MAINTENANCE`) |
| `DELETE` | `/api/parking/slots/{id}` | Admin Only | Delete slot (CRUD Delete) |
| `PUT` | `/api/parking/slots/{id}/reserve` | Internal/Booking | Atomically reserve slot |
| `PUT` | `/api/parking/slots/{id}/occupy` | Internal/Booking | Mark slot occupied on arrival |
| `PUT` | `/api/parking/slots/{id}/release` | Internal/Booking | Release slot back to available |
| `GET` | `/api/parking/analytics/occupancy` | Admin Only | Facility & zone occupancy statistics |
| `GET` | `/api/parking/instance-info` | Public | Instance identifier and port (`8082` vs `8085`) |

### 3. Bookings (`booking-service` via Gateway `:8080/api/bookings`)
| Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `POST` | `/api/bookings` | Authenticated | Create advance reservation with double-booking check |
| `GET` | `/api/bookings/my` | Authenticated | Get driver's active reservations |
| `GET` | `/api/bookings/{id}` | Authenticated | Get booking details by ID |
| `GET` | `/api/bookings/ref/{ref}` | Authenticated | Lookup booking by reference code |
| `PUT` | `/api/bookings/{id}` | Authenticated | Update booking details (vehicle, duration) |
| `POST` | `/api/bookings/{id}/check-in` | Authenticated | Driver arrival check-in |
| `POST` | `/api/bookings/{id}/checkout` | Authenticated | Driver exit & dynamic billing calculation |
| `POST` | `/api/bookings/{id}/cancel` | Authenticated | Cancel reservation and release slot |
| `GET` | `/api/bookings/all` | Admin Only | View all bookings across system |
| `DELETE` | `/api/bookings/{id}` | Admin Only | Delete booking record (CRUD Delete) |
| `GET` | `/api/bookings/analytics/trends` | Admin Only | Booking statistics and status counts |
| `GET` | `/api/bookings/instance-info` | Public | Instance identifier and port |

### 4. Dynamic Billing (`billing-service` via Gateway `:8080/api/billing`)
| Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `POST` | `/api/billing/calculate-and-invoice` | Internal/Booking | Calculate dynamic tariff and generate invoice |
| `GET` | `/api/billing/invoices/my` | Authenticated | Get driver's invoices |
| `GET` | `/api/billing/invoices/{id}` | Authenticated | Get invoice details by ID |
| `GET` | `/api/billing/invoices` | Admin Only | List all invoices across facility |
| `POST` | `/api/billing/invoices` | Admin Only | Manually issue custom invoice (CRUD Create) |
| `PUT` | `/api/billing/invoices/{id}` | Admin Only | Update invoice details (CRUD Update) |
| `DELETE` | `/api/billing/invoices/{id}` | Admin Only | Delete invoice record (CRUD Delete) |
| `POST` | `/api/billing/invoices/{id}/pay` | Authenticated | Process payment (`CARD`, `UPI`) |
| `GET` | `/api/billing/analytics/revenue` | Admin Only | Financial metrics (gross revenue, pending, tickets) |
| `GET` | `/api/billing/instance-info` | Public | Instance identifier and port |

---

## 🚀 Running the Application

### Option A: Using Docker Compose (Recommended)
From the project root:
```bash
docker-compose up --build
```
This automatically starts:
- `eureka-server` on `http://localhost:8761`
- `api-gateway` on `http://localhost:8080`
- `auth-service` on `http://localhost:8081`
- `parking-service-1` on `http://localhost:8082` (Instance 1)
- `parking-service-2` on `http://localhost:8085` (Instance 2 - Load Balanced)
- `booking-service` on `http://localhost:8083`
- `billing-service` on `http://localhost:8084`

### Option B: Running Locally with Maven Wrapper
Open individual terminal tabs for each service in the following order:

1. **Eureka Server**:
   ```bash
   cd eureka-server
   .\mvnw.cmd spring-boot:run
   ```
2. **Auth Service**:
   ```bash
   cd auth-service
   .\mvnw.cmd spring-boot:run
   ```
3. **Parking Service (Instance 1 on port 8082)**:
   ```bash
   cd parking-service
   .\mvnw.cmd spring-boot:run
   ```
4. **Parking Service (Instance 2 on port 8085 for Load Balancing)**:
   ```bash
   cd parking-service
   .\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--server.port=8085"
   ```
5. **Booking Service**:
   ```bash
   cd booking-service
   .\mvnw.cmd spring-boot:run
   ```
6. **Billing Service**:
   ```bash
   cd billing-service
   .\mvnw.cmd spring-boot:run
   ```
7. **API Gateway**:
   ```bash
   cd api-gateway
   .\mvnw.cmd spring-boot:run
   ```

---

## 💻 Interactive Browser UI & Load Balancer Visualizer

Open `parkgrid-frontend/index.html` in any modern web browser:
```bash
start parkgrid-frontend/index.html
```

### Pre-Configured Demo Accounts:
- **Admin Portal**:
  - Email: `admin@parkgrid.com`
  - Password: `Admin@123`
  - Privileges: Full CRUD on Zones, Slots, Users, Invoices; Analytics; Global Booking Management.
- **Driver Portal**:
  - Email: `user@parkgrid.com`
  - Password: `User@123`
  - Privileges: Slot availability view, Advance booking, Arrival check-in, Exit dynamic tariff calculator, Payment.

### Features in the Frontend:
1. **Interactive Parking Zones Layout**: Real-time visual slot grid with status badges (`AVAILABLE`, `RESERVED`, `OCCUPIED`, `MAINTENANCE`).
2. **Dynamic Tariff Exit Invoice Modal**: Shows transparent calculation breakdown including base rate, duration, vehicle multiplier, and peak-hour surcharge.
3. **CRUD Operations Console**:
   - Manage Zones: Add new zone, Edit existing zone, Delete zone.
   - Manage Slots: Add new slot, Toggle maintenance status, Delete slot.
   - Manage Users: Add new user profile, Toggle admin/user role, Delete user.
4. **Spring Cloud LoadBalancer & Cluster Visualizer**:
   - Live cluster topology cards with real-time status pulses.
   - **"Test Load Balancer (Fire 10 Requests)"** button: Sends requests through the Gateway, displays `X-Handled-By` instance headers, and updates request distribution bars in real time!
5. **Live JWT Token Inspector**:
   - Click "Inspect JWT Token" to inspect decoded header, payload claims, expiration countdown, and signature verification.

---

## 🧪 Review 2 Verification & Test Suite

Run the automated JUnit test suite for all microservices:

```powershell
# 1. Auth Service (BCrypt, Token Issuance, User CRUD)
cd auth-service; .\mvnw.cmd test; cd ..

# 2. Parking Service (Zones & Slots CRUD, Atomic Reservation, Occupancy Metrics)
cd parking-service; .\mvnw.cmd test; cd ..

# 3. Booking Service (Double-Booking Guard, Feign LoadBalancer, Lifecycle CRUD)
cd booking-service; .\mvnw.cmd test; cd ..

# 4. Billing Service (Dynamic Tariffs, Surcharges, Payments, Invoice CRUD)
cd billing-service; .\mvnw.cmd test; cd ..

# 5. API Gateway (Token Validation, Security Whitelisting, Route Config)
cd api-gateway; .\mvnw.cmd test; cd ..
```

---

## 👨‍💻 Project Information
- **Course**: SOA Programming and Microservices (Review 2)
- **Project Title**: Smart Urban Mobility - Automated Parking Slot Allocation (ParkGrid Solutions)
- **Status**: Complete & Verified (All 3 Review 2 Criteria Satisfied)
