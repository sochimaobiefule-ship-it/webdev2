# Product Management REST API Documentation

This is my documentation of every endpoint in my API.

---

## Endpoint Overview

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/products` | Creates a new product entity in the database. |
| `GET` | `/api/products` | Retrieves a paginated list of products with optional category filtering. |
| `GET` | `/api/products/{id}` | Retrieves detailed information for a single product by its unique ID. |
| `PUT` | `/api/products/{id}` | Updates all attributes of an existing product by its ID. |
| `PATCH` | `/api/products/{id}` | Partially updates selected attributes of an existing product by its ID. |
| `DELETE` | `/api/products/{id}` | Removes a product entity from the database by its ID. |

---

## Endpoints

### 1. Create Product
**HTTP Method & Path:** `POST /api/products`  
**Description:** Creates a new product in the system after validating required fields and checking for SKU uniqueness.

#### Request Body
* **Content-Type:** `application/json`

| Field | Type | Required / Optional | Constraints / Description |
| :--- | :--- | :--- | :--- |
| `name` | String | Required | Must be between 2 and 100 characters. |
| `sku` | String | Required | Must be unique across all products. |
| `category` | String | Required | Cannot be blank. |
| `price` | Number (Decimal) | Required | Must be greater than 0.00. |
| `stockQuantity` | Integer | Required | Must be an integer greater than or equal to 0. |

**Example Request:**
```json
{
  "name": "Wireless Gaming Mouse",
  "sku": "SKU-ELE-500",
  "category": "Electronics",
  "price": 49.99,
  "stockQuantity": 25
}

Possible Response Codes
201 Created — Product successfully created.
400 Bad Request — Validation failed or SKU already exists.
500 Internal Server Error — Unexpected server failure.

Example Response (201 Created):
JSON{
  "id": 6,
  "name": "Wireless Gaming Mouse",
  "sku": "SKU-ELE-500",
  "price": 49.99,
  "stockQuantity": 25,
  "category": "Electronics"
}

Example Response (400 Bad Request):
JSON{
  "status": 400,
  "message": "Validation failed for request",
  "timestamp": "2026-10-20T11:28:38.123",
  "errors": [
    {
      "field": "price",
      "message": "Price must be greater than zero"
    }
  ]
}

2. Get All Products (Paginated & Filtered)
HTTP Method & Path: GET /api/products  
Description: Retrieves a paginated list of products, with optional category filtering and custom sorting.

Query Parameters

| Parameter | Type | Required / Optional | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `category` | String | Optional | `null` | Filters products matching the exact category name. |
| `page` | Integer | Optional | `0` | Zero-based page index. |
| `size` | Integer | Optional | `20` | Number of items per page. |
| `sort` | String | Optional | `"id,asc"` | Property and direction to sort by (e.g., `price,desc`). |

Example Request URL:
GET /api/products?category=Electronics&page=0&size=5&sort=id,asc

Example Response (200 OK):
JSON
{
  "content": [
    {
      "id": 6,
      "name": "Wireless Gaming Mouse",
      "sku": "SKU-ELE-500",
      "price": 49.99,
      "stockQuantity": 25,
      "category": "Electronics"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "size": 5,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}

3. Get Product by ID
HTTP Method & Path: GET /api/products/{id}
Description: Retrieves detailed information for a specific product using its primary ID.

Path Parameters
id (Long, Required): Unique database identifier of the product.

Possible Response Codes
200 OK — Product found and returned.
404 Not Found — No product exists with the specified ID.
500 Internal Server Error — Unexpected server failure.

Example Response (200 OK):
JSON
{
  "id": 6,
  "name": "Wireless Gaming Mouse",
  "sku": "SKU-ELE-500",
  "price": 49.99,
  "stockQuantity": 25,
  "category": "Electronics"
}

Example Response (404 Not Found):
JSON
{
  "status": 404,
  "message": "Product not found with id: 99",
  "timestamp": "2026-10-20T11:28:38.123",
  "errors": null
}

4. Full Update Product
HTTP Method & Path: PUT /api/products/{id}
Description: Replaces all attributes of an existing product by its ID.

Path Parameters
id (Long, Required): Unique identifier of the product to update.

Request Body
Content-Type: application

| Field | Type | Required / Optional | Constraints / Description |
| :--- | :--- | :--- | :--- |
| `name` | String | Required | Product name (2 to 100 characters). |
| `sku` | String | Required | Product SKU code. |
| `category` | String | Required | Category name. |
| `price` | Number (Decimal) | Required | Positive decimal value. |
| `stockQuantity` | Integer | Required | Non-negative integer. |

Example Request:
JSON
{
  "name": "Wireless Ergonomic Mouse",
  "sku": "SKU-ELE-500",
  "category": "Electronics",
  "price": 59.99,
  "stockQuantity": 30
}

Possible Response Codes
200 OK — Product updated successfully.
400 Bad Request — Validation failed on request body.
404 Not Found — Product ID not found.
500 Internal Server Error — Unexpected server failure.

Example Response (200 OK):
JSON
{
  "id": 6,
  "name": "Wireless Ergonomic Mouse",
  "sku": "SKU-ELE-500",
  "price": 59.99,
  "stockQuantity": 30,
  "category": "Electronics"
}

5. Partial Update Product
HTTP Method & Path: PATCH /api/products/{id}
Description: Updates specific fields of an existing product without modifying unmentioned fields.

Path Parameters
id (Long, Required): Unique identifier of the product.

Request Body
Content-Type: application/json (All fields optional)

| Field | Type | Required / Optional |
| :--- | :--- | :--- |
| `name` | String | Optional |
| `sku` | String | Optional |
| `category` | String | Optional |
| `price` | Number (Decimal) | Optional |
| `stockQuantity` | Integer | Optional |

Example Request:
JSON
{
  "price": 44.99
}

Possible Response Codes
200 OK — Product partially updated successfully.
404 Not Found — Product ID not found.
500 Internal Server Error — Unexpected server failure.

Example Response (200 OK):
JSON
{
  "id": 6,
  "name": "Wireless Gaming Mouse",
  "sku": "SKU-ELE-500",
  "price": 44.99,
  "stockQuantity": 25,
  "category": "Electronics"
}

6. Delete Product
HTTP Method & Path: DELETE /api/products/{id}
Description: Permanently deletes a product entity from the database.

Path Parameters
id (Long, Required): Unique identifier of the product to delete.

Possible Response Codes
204 No Content — Product deleted successfully.
404 Not Found — Product ID not found.
500 Internal Server Error — Unexpected server failure.