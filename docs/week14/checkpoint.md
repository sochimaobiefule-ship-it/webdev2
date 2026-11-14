# Week 14 Checkpoint Verification & Test Evidence

## 1. Implemented Architecture Summary
- **Backend (`Spring Boot 3.2.3` + `MySQL`):**
  - Full CRUD endpoints for `Product` (`/api/products`) and `User` (`/api/users`) with DTO validation (`@Valid`), duplicate checks, and `409 Conflict` referential integrity protection on delete.
  - Atomic order workflow endpoint (`POST /api/orders`) implemented in `OrderService` with `@Transactional` and `PESSIMISTIC_WRITE` row locking.
  - Unified error handling in `GlobalExceptionHandler` returning structured JSON for `400`, `404`, `409`, and `500` responses.
- **Frontend (`Vanilla HTML/CSS/JS`):**
  - Integrated dashboard (`index.html`, `styles.css`, `app.js`) supporting Create, Read, Update, and Delete for Products and Users, category filtering, pagination, and live relationship `<select>` dropdowns for placing orders.

## 2. Executed End-to-End Test Cases

| Test ID | Scenario | Endpoint & Method | Expected Status | Actual Result & Evidence |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Create new Product | `POST /api/products` | `201 Created` | Product persisted in MySQL, table & order dropdown refreshed (`docs/week14/evidence/product_create_success.png`). |
| **TC-02** | Update existing Product (#1 Laptop) | `PUT /api/products/1` | `200 OK` | SKU, Category, Price, and Stock updated cleanly (`docs/week14/evidence/product_edit_success.png`). |
| **TC-03** | Delete unreferenced Product (#3 Mouse 1) | `DELETE /api/products/3` | `204 No Content` | Confirmed via `window.confirm()`, row removed, persisted across page reload (`docs/week14/evidence/product_delete_success.png`). |
| **TC-04** | Create & Update User (`justice_singh`) | `POST /api/users` | `201 Created` | User persisted in MySQL and immediately selectable in Customer dropdown (`docs/week14/evidence/user_crud_success.png`). |
| **TC-05** | Duplicate SKU Validation Failure | `POST /api/products` | `400 Bad Request` | Banner displayed `SKU_EXISTS`; typed input preserved in form (`docs/week14/evidence/validation_error_400.png`). |
| **TC-06** | Delete Non-Existent Product ID | `DELETE /api/products/99999` | `404 Not Found` | Console & Network logged `404 Not Found` (`docs/week14/evidence/missing_resource_404.png`). |
| **TC-07** | Delete Referenced User (#1) Conflict | `DELETE /api/users/1` | `409 Conflict` | Blocked with `"Cannot delete user ID 1 because they have existing order history."` (`docs/week14/evidence/conflict_delete_409.png`). |
| **TC-08** | Atomic Order Workflow Success (Qty 2) | `POST /api/orders` | `201 Created` | Stock deducted `10 -> 8` and Order #2 (`$2400.00`) committed atomically in MySQL (`docs/week14/evidence/order_workflow_success.png`, `docs/week14/evidence/mysql_order_success.png`). |
| **TC-09** | Atomic Order Simulated Failure & Rollback | `POST /api/orders` | `500 Internal Server Error` | Transaction rolled back; MySQL confirmed `Laptop` stock stayed `8` and no Order #3 created (`docs/week14/evidence/order_workflow_rollback.png`, `docs/week14/evidence/mysql_order_rollback.png`). |
| **TC-10** | Backend Network Outage & Recovery | `GET /api/products` | Network Error (`0`) | Displayed `"Network error: Unable to reach backend server..."` cleanly without crashing; recovered on restart (`docs/week14/evidence/network_failure_recovery.png`). |