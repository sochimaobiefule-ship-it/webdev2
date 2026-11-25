# Week 16 — Test Report (`test_report.md`)

**Date:** 25/11/2026  
**Status:** Passed (`13/13` automated tests, `8/8` browser UAT checks, `DEF-001` patched and verified)

## 1. Environment & Commands Run

* **Environment:** Spring Boot `3.2.3`, Java `21.0.8`, Maven Wrapper (`.\mvnw.cmd`), Surefire `3.1.2`, MySQL on port `3307` (`webdev2_week8` for app runtime; `webdev2_week16_test` for `test` profile).
* **Shared DB Logs:** Additional MySQL logs and database artifacts are stored under `INFOMAN1 week16/`.
* **Commands executed:**
  * Run server with seeded demo accounts (`manager_demo`, `reader_demo`):
    ```powershell
    \$env:DB_USERNAME = "webdev2_app"
    \$env:DB_PASSWORD = "<local-db-password>"
    \$env:APP_SEED_MANAGER_PASSWORD = "<manager-password>"
    \$env:APP_SEED_READER_PASSWORD = "<reader-password>"
    .\mvnw.cmd spring-boot:run
    ```
  * Run automated test suite (`test` profile):
    ```powershell
    \$env:TEST_DB_URL = "jdbc:mysql://localhost:3307/webdev2_week16_test?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
    \$env:TEST_DB_USERNAME = "webdev2_app"
    \$env:DB_PASSWORD = "<local-db-password>"
    .\mvnw.cmd test
    ```

## 2. Executed Counts, Failures & Skips

| Suite / Check Group | Executed | Passed | Failed | Skipped | Notes |
| :--- | :---: | :---: | :---: | :---: | :--- |
| `Week16MySqlTransactionTests` | 2 | 2 | 0 | 0 | Runs on `test` profile (`webdev2_week16_test`) |
| `Week16ApiSecurityTests` | 6 | 6 | 0 | 0 | Auth, roles, CSRF, and endpoint access |
| `Week16ServiceUnitTests` | 4 | 4 | 0 | 0 | Service logic, validation, and conflict rules |
| `Webdev2ApplicationTests` | 1 | 1 | 0 | 0 | Spring Boot context startup check |
| **Automated Subtotal** | **13** | **13** | **0** | **0** | **`BUILD SUCCESS`** |
| Browser UAT (`UAT-01` – `UAT-08`) | 8 | 8 | 0 | 0 | Verified on `http://localhost:8080` + `375x812` view |

## 3. Transaction Pre / Post State (`UAT-08` & DB Tests)

* **Browser Order Transaction (`UAT-08`):**
  * **Pre-state:** `Wireless Gaming Mouse` (ID `4`, `MOUSE-LOGI-01`) had stock `25`, with 3 confirmed orders in **Recent Confirmed Orders** (IDs `1`–`3`).
  * **Action:** Signed in as `manager_demo`, placed an order for `capstone_user (#1)` × `1` `Wireless Gaming Mouse`, and reloaded with `F5`.
  * **Post-state:** Order ID `4` (`2026-11-25`, `capstone_user (#1)`, `Wireless Gaming Mouse (#4)`, Qty `1`, `$49.99`, `CONFIRMED`) persisted in the table, and product ID `4` stock decreased from `25` to `24`.
* **Transactional Integration Tests (`Week16MySqlTransactionTests`):**
  * Verified atomic order creation + stock deduction on commit, and full rollback when a post-write failure is triggered so stock and order counts are not left half-updated.

## 4. Defect & Regression Summary

* **`reader_demo` seed setup:** Initial login attempt for `reader_demo` failed with `Invalid username or password.` because `APP_SEED_READER_PASSWORD` was not set in that shell session prior to startup; setting the variable and restarting resolved it.
* **`DEF-001` (`manager_demo` showing `READER` badge):** Root cause was `UserResponseDTO` and `UserService.toDTO` omitting `role`, causing `app.js` (`u.role || "READER"`) to render `READER`. Patched `UserResponseDTO.java` and `UserService.java`, restarted the server, confirmed `manager_demo` (ID `6`) displays `MANAGER` in the UI, and re-ran `.\mvnw.cmd test` with all **13/13** tests passing (`0` failures, `0` skipped).