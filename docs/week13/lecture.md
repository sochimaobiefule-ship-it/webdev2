# Week 13 Architecture & Contract Audit

## System Architecture Split
- **Browser (Client):** Handles user interactions, executes `fetch()` REST queries, renders DOM elements using `.textContent` for XSS safety.
- **Spring Boot (Backend API):** Exposes `ProductController`, handles validation (`CreateProductRequest`), and returns paginated DTO graphs.
- **MySQL (Database):** Holds persisted tables (`products`, `users`, `customer_orders`, `order_items`, `departments`, `employees`).

## Endpoint Contract Table
| Method | Endpoint Path | Query/Path Params | Success Response | Error Response |
|---|---|---|---|---|
| `GET` | `/api/products` | `page=0&size=10` | `200 OK` (Spring `PageImpl<ProductResponseDTO>`) | `500 Server Error` |
| `GET` | `/api/products/{id}` | Path variable `id` | `200 OK` (`ProductResponseDTO`) | `404 Not Found` |

## CORS Diagnosis Example
- **Symptom:** Browser console printed `Cross-Origin Request Blocked` when opening index from Live Server.
- **Root Cause:** Live Server runs on `http://127.0.0.1:5500`, while Spring Boot operates on `http://localhost:8080`.
- **Resolution:** Added `WebConfig.java` implementing `WebMvcConfigurer` to explicitly allow origins `http://localhost:5500` and `http://127.0.0.1:5500`.