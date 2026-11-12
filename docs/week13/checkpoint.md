# Week 13 Checkpoint Audit

## System Information & Environment
- **JDK:** 17
- **Framework:** Spring Boot 3.x
- **Database:** MySQL 8.0 (Port 3307)
- **Frontend:** Vanilla JS / HTML5

## Test Mapping
1. **List API Fetch:** `GET /api/products` loads content array and appends to UI.
2. **Detail View:** Click product `li` element triggers `GET /api/products/{id}` and updates detail panel.
3. **Empty Result Handling:** Returns empty page gracefully without script errors.
4. **404 / Missing ID:** Returns structured `ResourceNotFoundException` payload.

## Setup Commands
```bash
# Build and run backend
./mvnw spring-boot:run