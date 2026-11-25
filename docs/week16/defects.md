# Week 16 â€” Defect Log (`defects.md`)

**Date:** 25/11/2026  
**Tracked Defects:** 1 (`DEF-001` â€” Closed)

---

## DEF-001: `manager_demo` Renders `READER` Role Badge Instead of `MANAGER`

- **Severity:** Medium
- **Status:** Closed (Verified in Automated Suite `13/13` & Live UI)
- **Affected Files:** `UserResponseDTO.java`, `UserService.java`, `app.js`
- **Shared DB Evidence:** See `INFOMAN1 week16/` for shared MySQL schema and seed records.

### Root Cause
1. `UserResponseDTO` only included `id`, `username`, and `email`, omitting `role`.
2. `UserService.toDTO(User)` constructed `UserResponseDTO` without passing `user.getRole()`, leaving `role` missing/null in `/api/users` responses.
3. `app.js` defaulted missing roles via `const roleText = u.role || "READER";`, rendering a `READER` badge on every user row including `manager_demo`.

### Root-Cause Repairs Applied
- **`UserResponseDTO.java`:** Added `private String role` with its getter, setter, and a 4-argument constructor (`Long id, String username, String email, String role`) while keeping the 3-argument constructor delegating to it.
- **`UserService.java`:** Updated `toDTO(User)` to pass `user.getRole()` into `new UserResponseDTO(...)`.

### Verification Evidence
- Ran `.\mvnw.cmd test` with `test` profile credentials configured: **13 / 13 tests passed (`BUILD SUCCESS`)**.
- Restarted Spring Boot (`http://localhost:8080`) and confirmed `manager_demo` (ID `6`) displays the `MANAGER` badge in the Users table.
