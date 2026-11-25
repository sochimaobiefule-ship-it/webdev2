# Week 16 — Lecture & Engineering Notes (`lecture.md`)

## 1. Test-Layer Choices and Limitations

In this project, verification is split across three layers rather than relying on a single kind of test:

* **Unit & Service Tests (`Week16ServiceUnitTests`, `Webdev2ApplicationTests`):** Fast feedback on business logic, validation, and Spring context startup without needing live browser sessions. *Limitation:* Mocked or isolated service tests can miss real database constraints, SQL dialect quirks, or security filter interactions.
* **Integration & Security Tests (`Week16MySqlTransactionTests`, `Week16ApiSecurityTests`):** Exercises real JPA/Hibernate queries against MySQL (`webdev2_week16_test`) as well as Spring Security authentication, CSRF protection, and role checks (`MANAGER` vs. `READER` vs. anonymous). *Limitation:* Does not verify frontend DOM rendering, client-side fallback logic, or responsive CSS layout.
* **Browser User Acceptance Testing (`UAT-01` through `UAT-08`):** Validates real end-to-end flows on `http://localhost:8080`, full reloads (`F5`), error banners, role UI restrictions, `375x812` mobile layout, and keyboard focus. *Limitation:* Manual browser checks take longer to run and depend on clean starting data and consistent viewport/input steps.

## 2. Rollback-Test False-Positive Risk

When testing atomic order placement and post-write rollback (`@Transactional`), a test can pass for the **wrong reason** if we are not careful:

* **How a false positive happens:** Suppose a test triggers a failure *before* any database write happens, or runs inside a test method that is itself wrapped in an outer rollback transaction without checking committed state across a fresh connection/query. The final stock count might look unchanged even though rollback behavior was never truly exercised after a stock deduction/write.
* **How to avoid it:** Always verify that a write/stock change occurs first, force the post-write failure inside the transaction, and then assert in a clean post-rollback read that **both** stock quantity and order/order-item counts match their pre-test values.

## 3. Diagnosed Defect: `DEF-001` (`manager_demo` Showing `READER` Badge)

Even though `manager_demo` had `MANAGER` privileges and full CRUD access in the workspace, row `6` in the Users table rendered a `READER` badge[cite: 1, 9, 12].

* **Root cause across layers:** `UserResponseDTO` only included `id`, `username`, and `email`, `UserService.toDTO(User)` did not map `user.getRole()`, and `app.js` used `const roleText = u.role || "READER";` as a fallback when `role` was missing. Because backend DTO mapping omitted `role`, the frontend defaulted to `READER`.
* **Takeaway:** A UI badge bug is not always a CSS or frontend bug—checking the API response shape and DTO mapping early saves time and prevents masking missing backend fields with frontend defaults.

## 4. Configuration and Logging Decisions

* **Environment-driven secrets & seed passwords:** Sensitive credentials (`DB_PASSWORD`, `APP_SEED_MANAGER_PASSWORD`, `APP_SEED_READER_PASSWORD`) are passed via environment variables rather than hardcoded in source files. During UAT, `reader_demo` login failed with `Password123!` until `APP_SEED_READER_PASSWORD` was set in the shell before restarting the server—showing why startup seed flags must be verified per terminal session[cite: 7, 8, 9].
* **Separate runtime and test databases:** Pointing the `test` profile at `webdev2_week16_test` (`ddl-auto=update`) while keeping `webdev2_week8` (`ddl-auto=validate`) for runtime protects manual/demo data from accidental test cleanup.
* **SQL & error visibility:** Enabling `spring.jpa.show-sql=true` and `format_sql=true` helps inspect generated queries and transaction behavior during development and testing, while returning clean error messages (such as `400` validation and `409` conflict responses) to the UI avoids exposing raw stack traces to users[cite: 4, 6].