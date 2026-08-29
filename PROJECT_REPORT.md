# Gym Management System — Project Report

## 1. Project Overview

| Field            | Detail                                      |
|------------------|---------------------------------------------|
| **Project Title**| Gym Management System                       |
| **Purpose**      | Digitize and automate gym operations        |
| **Type**         | Full-Stack Web Application                  |
| **Backend**      | Java 21, Spring Boot 3.3, Spring Security   |
| **Frontend**     | HTML5, CSS3, Bootstrap 5, Chart.js          |
| **Database**     | MySQL 8.0                                   |
| **Architecture** | Layered MVC with REST APIs + SPA Frontend   |

---

## 2. Problem Statement

Traditional gyms rely on manual registers, spreadsheets, or disconnected software to manage members, trainers, payments, and attendance. This leads to:

- Data inconsistency and loss
- Time-consuming manual workflows
- Difficulty tracking member health progress
- Lack of real-time reporting for management

The **Gym Management System** solves these problems with a centralized, role-based digital platform.

---

## 3. Objectives

1. Provide **role-based portals** for Admin, Trainer, and Member
2. Automate **membership management** with plan assignment and expiry tracking
3. Digitize **attendance check-in/check-out** with reports
4. Enable **payment recording** and PDF receipt generation
5. Allow trainers to create personalized **workout plans** per member
6. Empower members with **BMI tracking** and progress charts
7. Generate **automated reports** (PDF and Excel) for management

---

## 4. System Architecture

### Layered MVC Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Frontend (Browser)                      │
│         HTML5 + Bootstrap 5 + Chart.js + auth.js         │
└───────────────────────┬─────────────────────────────────┘
                        │ HTTP/REST (JWT Bearer Token)
┌───────────────────────▼─────────────────────────────────┐
│              Spring Boot Application                      │
│  ┌─────────────┐  ┌────────────┐  ┌─────────────────┐   │
│  │ Controllers  │  │  Services  │  │  Repositories   │   │
│  │  (REST API)  │→ │  (Logic)   │→ │  (Spring Data)  │   │
│  └─────────────┘  └────────────┘  └────────┬────────┘   │
│                                             │            │
│  ┌──────────────────────────────────────────▼──────────┐ │
│  │              MySQL 8 Database                        │ │
│  └──────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────┘
```

### Security Architecture

```
HTTP Request
    │
    ▼
JwtAuthenticationFilter  ←──── JWT Token Validation
    │
    ▼
SecurityConfig (Role-Based Access Control)
    │
    ▼
Controller → Service → Repository → Database
```

---

## 5. Database Design

### Entity Relationship Summary

| Entity          | Key Relationships                                          |
|-----------------|------------------------------------------------------------|
| `users`         | Base entity for login, linked to `roles` (M:N)            |
| `members`       | Extends `users`, linked to `membership_plans`, `trainers` |
| `trainers`      | Extends `users`, manages multiple `members`               |
| `membership_plans` | Assigned to `members`, linked to `payments`            |
| `payments`      | Belongs to `member` and `membership_plan`                 |
| `attendance`    | Belongs to `member`, unique per (member, date)            |
| `workout_plans` | Created by `trainer` for a `member`                       |
| `workout_exercises` | Belongs to `workout_plan` (exercises per day)         |
| `progress`      | Belongs to `member`, tracks BMI over time                 |

### Key Design Decisions

- **Unique constraints**: `attendance(member_id, date)` prevents duplicate attendance
- **Cascade on delete**: Member deletion removes user account automatically
- **Soft status**: `users.status` field enables activate/deactivate without deletion
- **BCrypt**: All passwords are BCrypt-hashed, never stored in plaintext

---

## 6. Modules & Features

### 6.1 Authentication Module

| Feature               | Implementation                                |
|-----------------------|-----------------------------------------------|
| Login                 | `POST /api/auth/login` → JWT response         |
| Registration          | `POST /api/auth/register` → member account    |
| Token validation      | `JwtAuthenticationFilter` (per request)       |
| Token storage         | `localStorage` in browser, `Authorization: Bearer` |

### 6.2 Admin Dashboard

- Live stats: Total members, trainers, revenue, today's attendance
- Line/bar charts for monthly revenue and attendance trends
- Quick navigation to all management modules

### 6.3 Member Management

- Add new members with plan assignment and trainer assignment
- Edit member info, plan, and status
- Filter/search by name, plan, status
- View membership expiry with color-coded warnings

### 6.4 Trainer Management

- Add trainers with specialization and experience
- Assign trainers to members
- View trainer schedule and assigned member count

### 6.5 Membership Plans

- Create tiered plans (Basic, Gold, Premium) with pricing and duration
- View plan subscriber count
- Toggle plan active/inactive status

### 6.6 Payment Module

- Record payments with method (Cash, Card, Online)
- Auto-link to membership plan price
- View payment history per member
- Generate downloadable PDF receipts

### 6.7 Attendance Module

- Admin: Check-in / check-out members manually
- Auto-tracks date, time, and duration
- Filter attendance by date range
- Export to Excel

### 6.8 Workout Plans (Trainer → Member)

- Trainer creates multi-week workout plans per member
- Daily exercise breakdown (sets, reps, weight, notes)
- Members view assigned plans in their portal

### 6.9 BMI & Progress Tracking (Member)

- Live BMI calculator (weight / height²)
- BMI category display (Underweight, Normal, Overweight, Obese)
- Saves progress history to database
- Chart.js line graph for weight & BMI over time

### 6.10 Reports Module

- Monthly PDF report (member stats, payment summary, attendance)
- Monthly Excel report (same data in spreadsheet format)
- Powered by OpenPDF (iText fork) and Apache POI

---

## 7. Security Implementation

| Concern              | Approach                                              |
|----------------------|-------------------------------------------------------|
| Authentication       | JWT (RS256 secret, 24h expiry)                        |
| Authorization        | Spring Security `@PreAuthorize` + `SecurityConfig`    |
| Password storage     | BCryptPasswordEncoder (strength=10)                   |
| CSRF                 | Disabled (stateless REST API)                         |
| CORS                 | Configured for localhost:8080 development             |
| Sensitive endpoints  | Protected by role: ADMIN, TRAINER, MEMBER             |

---

## 8. API Design

The system exposes a RESTful API following these conventions:

- **Base URL**: `/api/`
- **Format**: JSON request/response bodies
- **Authentication**: `Authorization: Bearer <JWT>` header
- **Error format**: `{ "timestamp", "status", "error", "message", "path" }`
- **HTTP status codes**: 200 OK, 201 Created, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 500 Internal Server Error

---

## 9. Testing Strategy

| Type              | Framework                          | Coverage                      |
|-------------------|------------------------------------|-------------------------------|
| Unit Tests        | JUnit 5 + Mockito                  | AuthService, MemberService, PaymentService |
| Integration Tests | Spring Boot Test + MockMvc + H2    | AuthController (5 scenarios)  |
| Manual Testing    | Postman, Browser DevTools          | All API endpoints              |

---

## 10. Project Metrics

| Metric                | Value      |
|-----------------------|------------|
| Java source files     | 55+        |
| HTML pages            | 15         |
| REST API endpoints    | 40+        |
| Database tables       | 11         |
| Unit tests            | 10         |
| Integration tests     | 5          |
| JavaScript (auth.js)  | ~200 lines |
| CSS (style.css)       | ~400 lines |

---

## 11. Known Limitations & Future Improvements

| Limitation                           | Proposed Solution                              |
|--------------------------------------|------------------------------------------------|
| File/image upload not implemented    | Add S3/MinIO file storage                      |
| Email notifications not active       | Integrate Spring Mail + SMTP                   |
| No real-time features                | Add WebSocket for live attendance feed         |
| No mobile app                        | Expose same REST API to a React Native app     |
| No audit log                         | Add `@EntityListeners` with AuditingEntityListener |
| JWT refresh token not implemented    | Add `/api/auth/refresh` endpoint               |

---

## 12. Conclusion

The Gym Management System successfully demonstrates a complete, production-quality full-stack Java web application covering:

- ✅ Secure JWT-based multi-role authentication
- ✅ Full CRUD for all business entities
- ✅ Responsive modern UI with real-time charts
- ✅ PDF/Excel report generation
- ✅ BMI progress tracking
- ✅ Unit and integration test coverage
- ✅ Proper layered architecture (clean separation of concerns)

The project serves as an excellent reference for **Spring Boot 3.x best practices**, including stateless security, DTO pattern, global exception handling, and database schema design.

---

*Report generated for the Gym Management System v1.0.0 | Java 21 | Spring Boot 3.3 | MySQL 8*
