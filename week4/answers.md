# Lab 4: Server-Rendered CRUD Interface

**Course:** WEBDEV2  
**Term:** Prelim — Week 4  

---

## Task Evidence

### Task 1 — Product List Template
- **Template:** `src/main/resources/templates/products.html`
- **Description:** Renders product list using `th:each` and handles empty states using `th:if`.

![Task 1 Screenshot](screenshots/task1-list.png)

---

### Task 2 — Product Detail Template
- **Template:** `src/main/resources/templates/product-detail.html`
- **Description:** Displays product details with nested property access (`product.category.name`).

![Task 2 Screenshot](screenshots/task2-detail.png)

---

### Task 3 — Shared Navigation Fragment
- **Fragment:** `src/main/resources/templates/fragments/navbar.html`
- **Description:** Shared navigation fragment embedded using `th:replace`.

![Task 3 Screenshot](screenshots/task3-navbar.png)

---

### Task 4 — Create Form
- **Template:** `src/main/resources/templates/product-form.html`
- **Description:** Bound form using `th:object` and `th:field` submitting to `POST /products`.

![Task 4 Screenshot](screenshots/task4-form-success.png)

---

### Task 5 — Binding Errors on Create Form
- **Controller & Template:** Updated `POST /products` method with `BindingResult` and `th:errors` display.
- **Description:** Form redisplays with error messages when invalid data fails binding.

![Task 5 Screenshot](screenshots/task5-binding-error.png)