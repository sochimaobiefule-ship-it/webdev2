# WEBDEV2 — Week 10 Lab Answers

Name: OBIEFULE DECLAN
Student ID: 2510223
GitHub repo: https://github.com/sochimaobiefule-ship-it/webdev2
Date submitted: 16/10/2026

---

## Task 1 — URL Design & Controller Skeleton

**Approach / explanation:**
I designed the 6 standard RESTful API endpoints using standard plural noun conventions and appropriate HTTP verbs. I then implemented a `@RestController` skeleton class annotated with `@RequestMapping("/api/products")` containing stubbed handler methods returning `null` to ensure compilation and map mapping routes correctly.

**Endpoint Table:**

| Method | Endpoint Path | Action Description | Expected Status Code |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/products` | Retrieve all products | `200 OK` |
| **GET** | `/api/products/{id}` | Retrieve single product by ID | `200 OK` / `404 Not Found` |
| **POST** | `/api/products` | Create a new product | `201 Created` / `409 Conflict` |
| **PUT** | `/api/products/{id}` | Full replacement of existing product | `200 OK` / `404 Not Found` |
| **PATCH** | `/api/products/{id}` | Partial update of existing product | `200 OK` / `404 Not Found` |
| **DELETE** | `/api/products/{id}` | Remove product by ID | `204 No Content` / `404 Not Found` |

**Code snippet (Controller Skeleton):**
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return null;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@RequestBody CreateProductRequest request) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateFull(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        return null;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updatePartial(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return null;
    }
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
StatusCode        : 200
StatusDescription : OK
Content           : {}
RawContent        : HTTP/1.1 200 
                    Keep-Alive: timeout=60
                    Connection: keep-alive
                    Content-Length: 0
                    Date: Fri, 16 Oct 2026 02:57:55 GMT

---

## Task 2 — GET Endpoints — List & Get by ID

**Approach / explanation:**
I created `ProductResponseDTO` to encapsulate response data and prevent leaking internal entity models to API consumers. In `ProductService`, the `toDTO()` method maps entity properties to the DTO structure. The controller methods invoke the service layer and use `ResponseEntity.ok()` for collections and `Optional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build())` for single lookup by ID.

**Code snippet (Service & Controller GET Implementation):**
```java
// ProductService.java
public List<ProductResponseDTO> findAll() {
    return productRepository.findAll()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
}

public Optional<ProductResponseDTO> findById(Long id) {
    return productRepository.findById(id).map(this::toDTO);
}

public ProductResponseDTO toDTO(Product entity) {
    return new ProductResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getSku(),
            entity.getPrice(),
            entity.getStockQuantity()
    );
}

// ProductController.java
@GetMapping
public ResponseEntity<List<ProductResponseDTO>> getAll() {
    return ResponseEntity.ok(productService.findAll());
}

@GetMapping("/{id}")
public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
    return productService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
GET /api/products returning 200 OK with DTO array:
StatusCode: 200                                                                                                                
StatusDescription : OK                                                                                                                   
Content : [{"id":1,"name":"Laptop","sku":null,"price":1200.00,"stockQuantity":null}]

GET /api/products/999 returning 404 Not Found for invalid ID:
Invoke-WebRequest : The remote server returned an error: (404) Not Found.

---

## Task 3 — POST Endpoint — Create with 201 + Location

**Approach / explanation:**
I implemented `POST /api/products` using `CreateProductRequest` as the request body payload to encapsulate input data. In `ProductService`, the service checks whether the provided SKU already exists to prevent duplicates before mapping the request properties to a new `Product` entity and saving it via `productRepository`. In `ProductController`, I utilized `ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri()` to dynamically generate the `Location` header URI pointing to the newly created product resource, returning HTTP status `201 Created` alongside the response DTO.

**Code snippet (Service & Controller POST Implementation):**
```java
// ProductService.java
public ProductResponseDTO create(CreateProductRequest request) {
    if (productRepository.existsBySku(request.getSku())) {
        throw new IllegalArgumentException("SKU_EXISTS");
    }
    Product entity = new Product();
    entity.setName(request.getName());
    entity.setSku(request.getSku());
    entity.setPrice(request.getPrice());
    entity.setStockQuantity(request.getStockQuantity());

    Product saved = productRepository.save(entity);
    return toDTO(saved);
}

// ProductController.java
@PostMapping
public ResponseEntity<?> create(@RequestBody CreateProductRequest request) {
    try {
        ProductResponseDTO created = productService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    } catch (IllegalArgumentException e) {
        if ("SKU_EXISTS".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Product with SKU '" + request.getSku() + "' already exists.");
        }
        return ResponseEntity.badRequest().build();
    }
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
StatusCode : 201                                                                                                                
StatusDescription :   Created                                                                                                                   
Content : {"id":2,"name":"Mouse","sku":"M-01","price":19.99,"stockQuantity":10}
RawContent : HTTP/1.1 201 
            Transfer-Encoding: chunked
            Keep-Alive: timeout=60
            Connection: keep-alive
            Content-Type: application/json

---

## Task 4 — PUT, PATCH, and DELETE Handlers

**Approach / explanation:**
I implemented full update, partial update, and deletion on the `/api/products/{id}` resource endpoint:
- **PUT**: Replaces all entity fields with values from `UpdateProductRequest`. If the product exists, it updates and returns `200 OK`; otherwise, it returns `404 Not Found`.
- **PATCH**: Checks fields in `UpdateProductRequest` individually for non-null values and mutates only those fields, leaving all unmentioned properties intact. It returns `200 OK` with the updated resource or `404 Not Found`.
- **DELETE**: Verifies entity existence via `existsById`. If found, it removes the record using `deleteById` and returns `204 No Content`. If the ID does not exist, it returns `404 Not Found`.

**Code snippet (Service & Controller Implementation):**
```java
// ProductService.java
public Optional<ProductResponseDTO> updateFull(Long id, UpdateProductRequest request) {
    return productRepository.findById(id).map(existing -> {
        existing.setName(request.getName());
        existing.setSku(request.getSku());
        existing.setPrice(request.getPrice());
        existing.setStockQuantity(request.getStockQuantity());
        Product updated = productRepository.save(existing);
        return toDTO(updated);
    });
}

public Optional<ProductResponseDTO> updatePartial(Long id, UpdateProductRequest request) {
    return productRepository.findById(id).map(existing -> {
        if (request.getName() != null) existing.setName(request.getName());
        if (request.getSku() != null) existing.setSku(request.getSku());
        if (request.getPrice() != null) existing.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) existing.setStockQuantity(request.getStockQuantity());
        Product updated = productRepository.save(existing);
        return toDTO(updated);
    });
}

public boolean delete(Long id) {
    if (productRepository.existsById(id)) {
        productRepository.deleteById(id);
        return true;
    }
    return false;
}

// ProductController.java
@PutMapping("/{id}")
public ResponseEntity<ProductResponseDTO> updateFull(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
    return productService.updateFull(id, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@PatchMapping("/{id}")
public ResponseEntity<ProductResponseDTO> updatePartial(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
    return productService.updatePartial(id, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (productService.delete(id)) {
        return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
PUT /api/products/2
StatusCode : 200                                                                                                                
StatusDescription : OK                                                                                                                 
Content : {"id":2,"name":"Wireless Mouse","sku":"M-01","price":29.99,"stockQuantity":15}

PATCH /api/products/2
StatusCode        : 200
StatusDescription : OK
Content           : {"id":2,"name":"Wireless Mouse","sku":"M-01","price":24.99,"stockQuantity":15}

DELETE /api/products/2
StatusCode        : 204
StatusDescription : No Content
Content           : {}

DELETE /api/products/999
Invoke-WebRequest : The remote server returned an error: (404) Not Found.

---

## Task 5 — Validation & Global Exception Handling

**Approach / explanation:**
I implemented jakarta bean validation on the request DTOs and attached `@Valid` to the controller parameters. Additionally, I built a `@RestControllerAdvice` global exception handler to intercept `MethodArgumentNotValidException` and `IllegalArgumentException`. Validation failures yield a structured `400 Bad Request` response mapping invalid fields to error messages, while duplicate SKU checks yield a clean `409 Conflict` HTTP status.

**Code snippet (GlobalExceptionHandler.java):**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        if ("SKU_EXISTS".equals(ex.getMessage())) {
            error.put("error", "Product SKU already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
Payload validation failure returning 400 Bad Request
Invoke-WebRequest : The remote server returned an error: (400) Bad Request.

Duplicate SKU check returning 409 Conflict
Invoke-WebRequest : The remote server returned an error: (409) Conflict.                

---

## Self-Check

- [ ] All Week 10 work on the `week10` branch (project at branch root, no `week10/` folder)
- [ ] This file completed and renamed `answers.md`
- [ ] Repository link pasted into Moodle (no files uploaded)
