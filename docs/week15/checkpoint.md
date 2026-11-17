# Week 15 Milestone Checkpoint — F3: Authentication, Authorization & Session Security

**Project:** Inventory & Order Management System (`webdev2`)  
**Milestone:** Week 15 / F3 Checkpoint  
**Date:** 17/11/2026 

---

## 1. Setup & Provisioning Instructions (No Real Secrets)

### Prerequisites
* Java 17 or newer and Maven (`./mvnw` or `mvn`)
* MySQL 8 running locally on port `3306`

### Step 1: Set Up the MySQL Database and Restricted App User
Log into MySQL as `root` and run the commands below to create the database and give `webdev2_app@localhost` access only to `webdev2_week8`:
```sql
mysql> CREATE DATABASE IF NOT EXISTS webdev2_week8;

mysql> CREATE USER IF NOT EXISTS 'webdev2_app'@'localhost' IDENTIFIED BY '<YOUR_DB_PASSWORD>';
mysql> GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, REFERENCES, INDEX, ALTER ON `webdev2_week8`.* TO 'webdev2_app'@'localhost';
mysql> FLUSH PRIVILEGES;
```

### Step 2: Set Environment Variables

Copy `.env.example` to `.env` (already ignored in `.gitignore`) or set the environment variables in PowerShell before running the backend:

```powershell
\$env:DB_URL = "jdbc:mysql://localhost:3306/webdev2_week8?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
\$env:DB_USERNAME = "webdev2_app"
\$env:DB_PASSWORD = "<YOUR_DB_PASSWORD>"
\$env:DEMO_MANAGER_PASSWORD = "<YOUR_MANAGER_PASSWORD>"
\$env:DEMO_READER_PASSWORD = "<YOUR_READER_PASSWORD>"
```

### Step 3: Start the Spring Boot Application

Run the app with Maven. Flyway will automatically run the migrations to add the `password_hash` and `role` columns and insert the `manager_demo` and `reader_demo` test accounts:

```powershell
./mvnw spring-boot:run
```

Open `http://localhost:8080` in your browser to use the app.

---

## 2. Changed API Contract

### New Auth & CSRF Endpoints

| Method | Endpoint | Who Can Access | CSRF Token Needed? | Request & Response Details |
| --- | --- | --- | --- | --- |
| `GET` | `/api/auth/csrf` | Anyone | No | Sets the `XSRF-TOKEN` cookie and returns `200 OK` with `{"token":"...","headerName":"X-XSRF-TOKEN"}`. |
| `POST` | `/api/auth/login` | Anyone | Yes (`X-XSRF-TOKEN`) | Takes `{"username":"...","password":"..."}`. Returns `200 OK` with `Set-Cookie: JSESSIONID=...; HttpOnly; SameSite=Lax` and `{"authenticated":true,"id":6,"username":"...","role":"MANAGER"}`, or `401 Unauthorized` (`BAD_CREDENTIALS`) if wrong. |
| `GET` | `/api/auth/me` | Signed-in users | No | Returns `200 OK` with the current user's `id`, `username`, and `role`, or `401 Unauthorized` (`{"message":"Not signed in","authenticated":false}`). |
| `POST` | `/api/auth/logout` | Anyone | Yes (`X-XSRF-TOKEN`) | Clears the server session and returns `200 OK` with `{"message":"Signed out successfully.","authenticated":false}`. |

### Changes to Existing Endpoints (`/api/products`, `/api/orders`, `/api/users`)

* **Login & CSRF Checks:** All `/api/products`, `/api/orders`, and `/api/users` routes now require a valid `JSESSIONID` session cookie. Any `POST`, `PUT`, or `DELETE` request also requires the `X-XSRF-TOKEN` header matching the `XSRF-TOKEN` cookie.
* **Role Permissions:**
* `GET` routes allow both `READER` and `MANAGER` (`401 Unauthorized` if not logged in).
* `POST`, `PUT`, and `DELETE` routes only allow `MANAGER` (`403 Forbidden` if logged in as `READER` or if the CSRF token is missing/invalid).


* **Hidden Password Hashes:** `GET /api/users` only returns `id`, `username`, and `role` so `password_hash` is never sent to the frontend.
* **JSON Error Responses:**
* `401 Unauthorized`: `{"code":"UNAUTHORIZED","message":"Authentication required. Please sign in."}`
* `403 Forbidden`: `{"code":"FORBIDDEN","message":"Access denied or invalid CSRF token."}`

---

## 3. Defects Found & Fixed

| ID | Issue | Cause | Fix |
| --- | --- | --- | --- |
| `DEF-15-01` | `POST`, `PUT`, and `DELETE` requests from `app.js` kept returning `403 Forbidden` even when `X-XSRF-TOKEN` was sent. | Spring Security 6's default CSRF handler expected an XOR-encoded token instead of the raw `XSRF-TOKEN` cookie value read by JavaScript. | Added a custom SPA `CsrfTokenRequestAttributeHandler` in `SecurityConfig.java` that accepts the plain `X-XSRF-TOKEN` header value from `CookieCsrfTokenRepository.withHttpOnlyFalse()`. |
| `DEF-15-02` | Old test rows in `users` (`capstone_user`, `justice_singh`) from earlier weeks had `NULL` in `password_hash`. | Those rows were created before login and password hashing were added. | Updated `CustomUserDetailsService` to block logins when `password_hash` is null or empty while keeping the old rows so existing order foreign keys do not break. |
| `DEF-15-03` | Calling `/api/products` without logging in returned a `302` redirect to an HTML login page instead of a JSON error. | Default Spring Security redirects unauthenticated browser requests to `/login`. | Added custom `AuthenticationEntryPoint` (`401`) and `AccessDeniedHandler` (`403`) handlers in `SecurityConfig.java` to return JSON responses that `app.js` can read to show the login modal. |

---

## 4. INFOMAN1 Access-Control Cross-Reference

The security setup in `webdev2` builds directly on the database access-control concepts from **INFOMAN1**:

* **Least-Privilege Database Account:**
In INFOMAN1, we learned not to connect applications using the `root` MySQL account. Instead, `webdev2` connects using `webdev2_app@localhost`, which only has privileges on `webdev2_week8.*` (`SELECT, INSERT, UPDATE, DELETE, CREATE, REFERENCES, INDEX, ALTER`) and cannot touch other databases or manage MySQL users.
* **Read vs. Write Role Separation (`READER` vs. `MANAGER`):**
Similar to granting `SELECT` to read-only database users and `INSERT, UPDATE, DELETE` only to managers in INFOMAN1, Spring Security restricts `READER` accounts to `GET` endpoints while reserving `POST`, `PUT`, and `DELETE` operations for `MANAGER`.
* **Protecting User Credentials:**
Following INFOMAN1 practices for securing user tables, passwords are hashed with BCrypt (`$2a$10$`) before being stored in `users.password_hash`, and DTO mapping ensures the hash column is never exposed through `/api/users`.
```