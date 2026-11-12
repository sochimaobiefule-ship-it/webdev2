# Week 14 Lecture Notes — Full-Stack Write Flows, State Synchronization & Atomic Workflows

## 1. Stateful Browser CRUD & Form Lifecycle
- **Single-Form Create/Edit Mode:** Rather than duplicating HTML forms, a hidden `id` field (`#product-id`, `#user-id`) switches the form between `POST /api/<resource>` (when empty) and `PUT /api/<resource>/{id}` (when populated).
- **Payload Discipline:** Generated primary keys (`id`) are omitted from `POST` request bodies. `PUT` payloads send the complete updatable resource representation.
- **In-Flight Submission Guard:** Submit buttons are disabled (`btn.disabled = true`) before `await fetch(...)` and re-enabled inside a `finally` block to prevent duplicate writes from double-clicks.
- **Preserving User Input on Failure:** Forms are only reset after a `2xx` response. When a `400`, `404`, `409`, or network error occurs, user input remains intact so errors can be corrected immediately.
- **XSS Prevention:** All server-returned strings (names, SKUs, usernames, error messages) are rendered exclusively via `.textContent` and DOM element creation rather than `.innerHTML`.

## 2. Client-Server State Synchronization
- **Reloading Persisted State:** After `POST`, `PUT`, or `DELETE` succeeds, the frontend re-fetches paginated lists (`GET /api/products`) and relationship `<select>` options (`GET /api/users`, `GET /api/products`) so server-generated IDs, sorting, and stock counts stay authoritative.
- **Pagination Edge Cases:** Changing the category filter resets `page = 0`. Deleting the final item on a page (`page > 0`) decrements `page` by 1 and re-fetches so the user is never stranded on an empty page.
- **204 No Content Handling:** The `request()` helper checks `response.status === 204` before attempting `response.json()`, preventing `SyntaxError: Unexpected end of JSON input`.

## 3. Atomic Multi-Entity Business Workflows (`@Transactional`)
- **Why Client-Orchestrated Multi-Step Writes Fail:** Issuing separate `PUT /api/products/{id}` and `POST /api/orders` calls from the browser risks partial commits if the network drops or validation fails mid-sequence, and introduces race conditions under concurrent orders.
- **Server-Side Transaction Boundary:** `OrderService.placeOrder()` executes within a single Spring `@Transactional` method using `LockModeType.PESSIMISTIC_WRITE` (`SELECT ... FOR UPDATE`) on the `Product` row. Both stock deduction and `CustomerOrder`/`OrderItem` creation succeed together or roll back completely.