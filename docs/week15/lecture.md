# Week 15 Architecture & Security Design Notes (`docs/week15/lecture.md`)

**Module:** F3 — Authentication, Authorization, Session Management & CSRF/CORS Defense  
**Application:** Inventory & Order Management System (`webdev2`)  

---

## 1. Role-Based Permission Matrix

Access control across the REST API and browser UI is governed by two application roles (`READER` and `MANAGER`, mapped to Spring Security authorities `ROLE_READER` and `ROLE_MANAGER`) plus unauthenticated (`ANONYMOUS`) visitors:

| Resource / Endpoint | HTTP Method | `ANONYMOUS` | `READER` (`ROLE_READER`) | `MANAGER` (`ROLE_MANAGER`) | Security & UI Behavior |
|---|---|---|---|---|---|
| `/`, `/index.html`, `/css/**`, `/js/**` | `GET` | Allowed (`200`) | Allowed (`200`) | Allowed (`200`) | Public static SPA shell; unauthenticated users see only the sign-in modal. |
| `/api/auth/csrf` | `GET` | Allowed (`200`) | Allowed (`200`) | Allowed (`200`) | Issues `XSRF-TOKEN` cookie and JSON token for state-changing requests. |
| `/api/auth/login` | `POST` | Allowed (`200`/`401`) | Allowed (`200`) | Allowed (`200`) | Requires valid `X-XSRF-TOKEN`; validates BCrypt credentials and starts session. |
| `/api/auth/logout` | `POST` | Allowed (`200`) | Allowed (`200`) | Allowed (`200`) | Requires `X-XSRF-TOKEN`; invalidates server session and clears `JSESSIONID`. |
| `/api/auth/me` | `GET` | `401 Unauthorized` | Allowed (`200`) | Allowed (`200`) | Returns current authenticated principal (`id`, `username`, `role`) or `401`. |
| `/api/products/**` | `GET` | `401 Unauthorized` | Allowed (`200`) | Allowed (`200`) | Read-only catalog and pagination access for any signed-in user. |
| `/api/products/**` | `POST`, `PUT`, `DELETE` | `401 Unauthorized` | `403 Forbidden` | Allowed (`200`/`201`/`204`) | UI hides create/edit/delete controls for `READER`; server enforces `ROLE_MANAGER`. |
| `/api/orders/**` | `GET` | `401 Unauthorized` | Allowed (`200`) | Allowed (`200`) | Order history visibility for authenticated users. |
| `/api/orders/**` | `POST`, `PUT`, `DELETE` | `401 Unauthorized` | `403 Forbidden` | Allowed (`200`/`201`) | Only `MANAGER` can place or modify stock-deducting orders. |
| `/api/users/**` | `GET` | `401 Unauthorized` | Allowed (`200`) | Allowed (`200`) | Exposes safe user summaries (never password hashes) for order attribution. |
| `/api/users/**` | `POST`, `PUT`, `DELETE` | `401 Unauthorized` | `403 Forbidden` | Allowed (`200`/`201`) | Account administration restricted to `MANAGER`. |

---

## 2. Authentication versus Authorization

Although often grouped together, **authentication** and **authorization** solve distinct security problems at different stages of the request lifecycle:

* **Authentication (*"Who are you?"*):**  
  Verifies a user's identity before granting a session. In `webdev2`, `POST /api/auth/login` accepts a `username` and plaintext `password`, passes them through Spring Security's `AuthenticationManager` (`DaoAuthenticationProvider`), looks up the account via `CustomUserDetailsService`, and compares the submitted password against the stored BCrypt hash.  
  * **Failure mode:** Unauthenticated requests to protected endpoints or bad login credentials return **`401 Unauthorized`** (`UNAUTHORIZED` / `BAD_CREDENTIALS`).
* **Authorization (*"What are you allowed to do?"*):**  
  Evaluates an already-authenticated user's granted authorities (`ROLE_READER` vs. `ROLE_MANAGER`) against the requested HTTP method and route. Even when `reader_demo` holds a valid session cookie and valid CSRF token, attempting `POST /api/products` is rejected because `READER` lacks `ROLE_MANAGER`.  
  * **Failure mode:** Authenticated requests exceeding the user's role permissions return **`403 Forbidden`** (`FORBIDDEN`).
* **Defense-in-Depth Principle:**  
  Frontend role checks in `app.js` hide manager-only buttons for `READER` users to improve usability, while `SecurityConfig.java` enforces authorization rules server-side so bypassed UI controls cannot mutate database state.

---

## 3. Session / Token Choice: Stateful `HttpOnly` Sessions vs. Stateless JWTs

For this monolithic Spring Boot + Vanilla JS Single-Page Application (served from the same origin on `localhost:8080`), we chose **stateful server-side HTTP sessions (`JSESSIONID`)** over browser-stored JWTs (`localStorage`/`sessionStorage`):

1. **XSS Token-Theft Mitigation (`HttpOnly`):**  
   `JSESSIONID` is issued with the `HttpOnly` flag (`server.servlet.session.cookie.http-only=true`), making the session identifier unreadable via `document.cookie`. By contrast, storing a JWT in `localStorage` exposes it to exfiltration if any Cross-Site Scripting (XSS) vulnerability occurs.
2. **Immediate Server-Side Revocation:**  
   Because session state lives on the server, `POST /api/auth/logout` (`SecurityContextLogoutHandler` + `invalidateHttpSession(true)`) immediately invalidates the `JSESSIONID`—verified in **Test 7**, where replaying the old cookie against `GET /api/auth/me` immediately returned `401 Unauthorized`. Stateless JWTs cannot be revoked before expiration without maintaining a server-side denylist.
3. **Session Fixation & Transport Controls:**  
   Spring Security rotates the session ID upon login (`sessionFixation().changeSessionId()`), enforces `SameSite=Lax` (`server.servlet.session.cookie.same-site=lax`), and supports `Secure=true` in production HTTPS deployments.

---

## 4. Password Handling & Database Credential Hygiene

* **Adaptive One-Way Hashing (`BCryptPasswordEncoder`):**  
  Passwords are never stored, logged, or returned in API responses in plaintext. `BCryptPasswordEncoder` (default cost factor `10`, prefix `$2a$10$`) automatically generates a unique cryptographic salt per user and embeds it inside the 60-character `users.password_hash` column, protecting against rainbow-table attacks and brute-force cracking.
* **Legacy Pre-Auth Accounts:**  
  Earlier seed rows (`capstone_user`, `justice_singh`) with `NULL` password hashes are explicitly rejected by `CustomUserDetailsService` so legacy records cannot be used to log in.
* **Secret Externalization & Least-Privilege DB User:**  
  Demo passwords (`DEMO_MANAGER_PASSWORD`, `DEMO_READER_PASSWORD`) and database credentials (`DB_USERNAME`, `DB_PASSWORD`) are injected via environment variables rather than hardcoded in `application.properties` or committed to Git. Furthermore, Spring Boot connects as `webdev2_app@localhost`—scoped strictly to `SELECT, INSERT, UPDATE, DELETE, CREATE, REFERENCES, INDEX, ALTER ON webdev2_week8.*`—eliminating `root` database exposure.

---

## 5. CSRF versus CORS Distinction

Cross-Site Request Forgery (**CSRF**) and Cross-Origin Resource Sharing (**CORS**) address opposite directions of cross-origin risk:

| Dimension | CSRF (Cross-Site Request Forgery) | CORS (Cross-Origin Resource Sharing) |
|---|---|---|
| **Threat / Purpose** | Prevents a malicious third-party site from tricking a logged-in user's browser into submitting forged state-changing requests (`POST`/`PUT`/`DELETE`) that silently carry `JSESSIONID`. | Controls which external origins (`Origin` header) are permitted by the browser's Same-Origin Policy to read responses from our API. |
| **Mechanism in `webdev2`** | **Synchronizer / Double-Submit Cookie Pattern:** Spring issues a readable `XSRF-TOKEN` cookie (`CookieCsrfTokenRepository.withHttpOnlyFalse()`). `app.js` reads that cookie and copies it into the `X-XSRF-TOKEN` HTTP header on every mutating request. | **Explicit Origin Allowlist:** `CorsConfigurationSource` restricts cross-origin browser reads to explicitly configured origins (e.g., `http://localhost:8080`) with `allowCredentials(true)` and approved headers (`X-XSRF-TOKEN`, `Content-Type`). |
| **Why Both Matter** | An attacker site can trigger a cross-site `POST` even without reading the response—CORS alone does not block forged form/fetch submissions, whereas `X-XSRF-TOKEN` blocks any site that cannot read our origin's cookie (**Test 5** -> `403`). | Without strict CORS rules, a wildcard origin (`*`) with credentials would allow untrusted sites to read sensitive `/api/products` or `/api/orders` JSON responses. |

---

## 6. Explanation of Spring Security Configuration (`SecurityConfig.java`)

1. **SPA-Compatible CSRF Handler:** Uses `CookieCsrfTokenRepository.withHttpOnlyFalse()` so `app.js` can read the `XSRF-TOKEN` cookie and send it back in the `X-XSRF-TOKEN` request header, paired with `SpaCsrfTokenRequestHandler` to accept plain token values from `fetch()` calls.
2. **CORS Policy:** Applies explicit `CorsConfigurationSource` rules via `.cors(Customizer.withDefaults())` before security filters evaluate requests.
3. **Stateful Session Management:** Uses `SessionCreationPolicy.IF_REQUIRED` and rotates `JSESSIONID` on authentication via `sessionFixation().changeSessionId()`.
4. **Top-Down RBAC Rules:** Permits public static files and `/api/auth/csrf`, `/api/auth/login`, `/api/auth/logout`; restricts `GET` API endpoints to `ROLE_READER` and `ROLE_MANAGER`; restricts `POST`, `PUT`, and `DELETE` API mutations exclusively to `ROLE_MANAGER`; and enforces `.anyRequest().authenticated()` as a deny-by-default fallback.
5. **JSON Error Handlers:** Configures `authenticationEntryPoint` to return `401 Unauthorized` (`UNAUTHORIZED`) and `accessDeniedHandler` to return `403 Forbidden` (`FORBIDDEN`) as clean JSON payloads instead of HTML redirects.
6. **Password Hashing Bean:** Exposes `BCryptPasswordEncoder` (strength `10`) for password verification and hashing.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        SpaCsrfTokenRequestHandler requestHandler = new SpaCsrfTokenRequestHandler();

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf
                .csrfTokenRepository(tokenRepository)
                .csrfTokenRequestHandler(requestHandler)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(fixation -> fixation.changeSessionId())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/logout").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/orders/**", "/api/users/**")
                    .hasAnyRole("READER", "MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/products/**", "/api/orders/**", "/api/users/**")
                    .hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/products/**", "/api/orders/**", "/api/users/**")
                    .hasRole("MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**", "/api/orders/**", "/api/users/**")
                    .hasRole("MANAGER")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authEx) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Authentication required. Please sign in.\"}");
                })
                .accessDeniedHandler((req, res, accessEx) -> {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"code\":\"FORBIDDEN\",\"message\":\"Access denied or invalid CSRF token.\"}");
                })
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```