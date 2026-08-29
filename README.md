# 🏋️ Gym Management System

A full-stack, production-quality **Gym Management System** built with **Java 21**, **Spring Boot 3.x**, **Spring Security (JWT)**, **MySQL**, and a modern **Bootstrap 5** frontend.

---

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Running the Application](#running-the-application)
- [Default Credentials](#default-credentials)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Running Tests](#running-tests)
- [Screenshots](#screenshots)

---

## ✨ Features

### Admin Panel
- 📊 **Dashboard** — Real-time statistics with Chart.js graphs
- 👤 **Member Management** — Add, edit, delete, and activate/deactivate members
- 🏋️ **Trainer Management** — Full CRUD for gym trainers with specialization info
- 💳 **Membership Plans** — Create/edit tiered plans (Basic, Gold, Premium)
- 💰 **Payment Management** — Record and track member payments with PDF receipts
- 📅 **Attendance Tracking** — Daily check-in/check-out management
- 📊 **Reports** — Export monthly PDF and Excel reports

### Member Portal
- 🏠 **Dashboard** — Personal stats, plan status, attendance overview
- 👤 **Profile** — View and update personal information
- 🏋️ **Workout Plans** — View trainer-assigned workout schedules
- 📅 **Attendance History** — Monthly bar chart and full attendance log
- 💰 **Payment History** — Transaction records with downloadable receipts
- ⚖️ **BMI Calculator** — Live BMI calculation with progress tracking charts

### Trainer Portal
- 🏠 **Dashboard** — View assigned members and stats
- 👥 **My Members** — Member card grid with membership expiry warnings
- 📋 **Workout Plans** — Create, view, and delete workout plans per member

---

## 🛠️ Technology Stack

| Layer       | Technology                                  |
|-------------|---------------------------------------------|
| Backend     | Java 21, Spring Boot 3.3, Spring MVC        |
| Security    | Spring Security 6, JWT (io.jsonwebtoken)    |
| Persistence | Spring Data JPA (Hibernate), MySQL 8        |
| Build       | Maven 3.9                                   |
| Utilities   | Lombok, Bean Validation                     |
| Frontend    | HTML5, CSS3 (custom design system)          |
| UI Library  | Bootstrap 5.3, Font Awesome 6.4             |
| Charts      | Chart.js 4                                  |
| Documents   | OpenPDF (iText fork), Apache POI            |
| Testing     | JUnit 5, Mockito, Spring Boot Test, H2      |

---

## 🏗️ Architecture

```
com.gym
├── config/          # Security config, CORS, DataInitializer
├── controller/      # REST API controllers
├── dto/             # Data Transfer Objects (request/response)
├── entity/          # JPA entities (DB models)
├── exception/       # Global exception handler & custom exceptions
├── repository/      # Spring Data JPA repositories
├── security/        # JwtTokenProvider, JwtAuthenticationFilter, UserDetails
├── service/         # Service interfaces
└── serviceImpl/     # Business logic implementations
```

---

## ✅ Prerequisites

- **Java 21** — [Download](https://adoptium.net/temurin/releases/?version=21)
- **Maven 3.9+** — Bundled with IntelliJ IDEA
- **MySQL 8.0+** — [Download](https://dev.mysql.com/downloads/)
- **IntelliJ IDEA** (Community or Ultimate) — [Download](https://www.jetbrains.com/idea/)

---

## 🚀 Setup & Installation

### 1. Clone / Open the Project

```bash
git clone https://github.com/your-org/gym-management-system.git
cd gym-management-system
```

Or open the folder directly in **IntelliJ IDEA** (File → Open).

### 2. Configure MySQL Database

Open MySQL Workbench or a terminal and run:

```sql
CREATE DATABASE gym_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Then run the SQL seed script:

```bash
mysql -u root -p gym_management_db < gym_management_db.sql
```

### 3. Update `application.properties`

Edit `src/main/resources/application.properties` and set your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gym_management_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

---

## ▶️ Running the Application

### In IntelliJ IDEA

1. Open the project
2. Let Maven download dependencies (first run takes ~2 minutes)
3. Locate `GymManagementSystemApplication.java`
4. Click the **▶ Run** button

### Via Maven Command Line

```bash
mvn spring-boot:run
```

The application starts at: **http://localhost:8080**

---

## 🔐 Default Credentials

| Role    | Username  | Password   |
|---------|-----------|------------|
| Admin   | `admin`   | `admin123` |
| Trainer | `trainer1`| `trainer123`|
| Member  | `member1` | `member123` |

> **Security Note:** Change these default credentials immediately in production!

---

## 🔌 API Endpoints

### Authentication (`/api/auth`)

| Method | Endpoint           | Description              | Access  |
|--------|--------------------|--------------------------|---------|
| POST   | `/api/auth/login`  | Login & get JWT token    | Public  |
| POST   | `/api/auth/register` | Register new member    | Public  |

### Members (`/api/members`)

| Method | Endpoint                  | Description              | Access      |
|--------|---------------------------|--------------------------|-------------|
| GET    | `/api/members`            | Get all members          | ADMIN       |
| GET    | `/api/members/{id}`       | Get member by ID         | ADMIN/MEMBER|
| PUT    | `/api/members/{id}`       | Update member            | ADMIN/MEMBER|
| DELETE | `/api/members/{id}`       | Delete member            | ADMIN       |
| PATCH  | `/api/members/{id}/status`| Activate/Deactivate      | ADMIN       |

### Trainers (`/api/trainers`)

| Method | Endpoint             | Description              | Access |
|--------|----------------------|--------------------------|--------|
| GET    | `/api/trainers`      | Get all trainers         | ADMIN  |
| POST   | `/api/trainers`      | Create trainer           | ADMIN  |
| PUT    | `/api/trainers/{id}` | Update trainer           | ADMIN  |
| DELETE | `/api/trainers/{id}` | Delete trainer           | ADMIN  |

### Payments (`/api/payments`)

| Method | Endpoint                      | Description           | Access |
|--------|-------------------------------|----------------------|--------|
| GET    | `/api/payments`               | All payments         | ADMIN  |
| POST   | `/api/payments`               | Record payment       | ADMIN  |
| GET    | `/api/payments/member/{id}`   | Member payments      | MEMBER |
| GET    | `/api/payments/receipt/{id}`  | Download PDF receipt | MEMBER |

### Reports (`/api/reports`)

| Method | Endpoint                    | Description           | Access |
|--------|-----------------------------|-----------------------|--------|
| GET    | `/api/reports/monthly/pdf`  | Monthly PDF report    | ADMIN  |
| GET    | `/api/reports/monthly/excel`| Monthly Excel report  | ADMIN  |

---

## 📁 Project Structure

```
gym-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/gym/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   └── serviceImpl/
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/style.css
│   │       │   ├── js/auth.js
│   │       │   ├── admin/       (admin HTML pages)
│   │       │   ├── member/      (member HTML pages)
│   │       │   └── trainer/     (trainer HTML pages)
│   │       └── application.properties
│   └── test/
│       └── java/com/gym/
│           ├── service/         (unit tests)
│           └── controller/      (integration tests)
├── gym_management_db.sql
├── pom.xml
└── README.md
```

---

## 🧪 Running Tests

```bash
# Run all tests (unit + integration)
mvn test

# Run only unit tests
mvn test -Dtest=AuthServiceTest,MemberServiceTest,PaymentServiceTest

# Run integration tests only
mvn test -Dtest=AuthControllerIntegrationTest
```

Integration tests use an **H2 in-memory database** automatically (no MySQL required for testing).

---

## 🛡️ Security Model

- All API endpoints are protected with **JWT Bearer Token** authentication
- Tokens expire after **24 hours** (configurable in `application.properties`)
- Role-based access control: `ROLE_ADMIN`, `ROLE_TRAINER`, `ROLE_MEMBER`
- Passwords stored using **BCrypt** hashing
- CORS configured for localhost development

---

## 📄 License

This project is licensed for educational purposes. Built as a production-quality demonstration of Spring Boot best practices.

---

*Built with ❤️ using Spring Boot 3.x | Java 21 | MySQL | Bootstrap 5*
