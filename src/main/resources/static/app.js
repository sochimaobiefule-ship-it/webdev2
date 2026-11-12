const API_BASE = "http://localhost:8080";

let productState = {
    page: 0,
    size: 5,
    category: "",
    totalPages: 1,
    currentItemsCount: 0
};

async function request(path, options = {}) {
    let response;
    try {
        response = await fetch(`${API_BASE}${path}`, options);
    } catch (networkErr) {
        const err = new Error("Network error: Unable to reach backend server. Ensure Spring Boot is running and retry.");
        err.status = 0;
        throw err;
    }

    const type = response.headers.get("content-type") || "";
    const body = response.status === 204
        ? null
        : type.includes("application/json")
            ? await response.json()
            : await response.text();

    if (!response.ok) {
        let message = `Request failed (${response.status})`;
        if (typeof body === "object" && body !== null) {
            message = body.message || message;
            if (Array.isArray(body.errors) && body.errors.length > 0) {
                const fieldMsgs = body.errors.map(e => `${e.field}: ${e.message}`).join("; ");
                message = `${message} — ${fieldMsgs}`;
            }
        }
        const error = new Error(message);
        error.status = response.status;
        error.details = body;
        throw error;
    }
    return body;
}

const bannerEl = document.getElementById("status-banner");

function showBanner(message, type = "success") {
    bannerEl.textContent = message;
    bannerEl.className = `banner ${type}`;
}

function clearBanner() {
    bannerEl.textContent = "";
    bannerEl.className = "banner hidden";
}

const productForm = document.getElementById("product-form");
const productIdInput = document.getElementById("product-id");
const productNameInput = document.getElementById("product-name");
const productSkuInput = document.getElementById("product-sku");
const productCategoryInput = document.getElementById("product-category");
const productPriceInput = document.getElementById("product-price");
const productStockInput = document.getElementById("product-stock");
const productSubmitBtn = document.getElementById("product-submit-btn");
const productCancelBtn = document.getElementById("product-cancel-btn");
const productFormTitle = document.getElementById("product-form-title");
const productsTbody = document.getElementById("products-tbody");

async function loadProducts() {
    const params = new URLSearchParams({
        page: productState.page,
        size: productState.size,
        sort: "id,asc"
    });
    if (productState.category.trim() !== "") {
        params.set("category", productState.category.trim());
    }

    try {
        const pageData = await request(`/api/products?${params.toString()}`);
        const items = pageData.content || [];
        productState.totalPages = Math.max(pageData.totalPages || 1, 1);
        productState.currentItemsCount = items.length;

        if (items.length === 0 && productState.page > 0) {
            productState.page -= 1;
            return loadProducts();
        }

        renderProductsTable(items);
        document.getElementById("page-info").textContent =
            `Page ${productState.page + 1} of ${productState.totalPages}`;
        document.getElementById("prev-page-btn").disabled = productState.page <= 0;
        document.getElementById("next-page-btn").disabled = productState.page + 1 >= productState.totalPages;
    } catch (err) {
        productsTbody.innerHTML = "";
        const tr = document.createElement("tr");
        const td = document.createElement("td");
        td.colSpan = 7;
        td.textContent = `Could not load products: ${err.message}`;
        tr.appendChild(td);
        productsTbody.appendChild(tr);
        showBanner(err.message, "error");
    }
}

function renderProductsTable(products) {
    productsTbody.innerHTML = "";
    if (products.length === 0) {
        const tr = document.createElement("tr");
        const td = document.createElement("td");
        td.colSpan = 7;
        td.textContent = "No products found.";
        tr.appendChild(td);
        productsTbody.appendChild(tr);
        return;
    }

    products.forEach(p => {
        const tr = document.createElement("tr");

        const cells = [
            p.id,
            p.name,
            p.sku,
            p.category || "-",
            `$${Number(p.price).toFixed(2)}`,
            p.stockQuantity
        ];

        cells.forEach(val => {
            const td = document.createElement("td");
            td.textContent = val;
            tr.appendChild(td);
        });

        const actionTd = document.createElement("td");

        const editBtn = document.createElement("button");
        editBtn.type = "button";
        editBtn.className = "btn secondary small";
        editBtn.textContent = "Edit";
        editBtn.addEventListener("click", () => startEditProduct(p));

        const delBtn = document.createElement("button");
        delBtn.type = "button";
        delBtn.className = "btn danger small";
        delBtn.style.marginLeft = "0.4rem";
        delBtn.textContent = "Delete";
        delBtn.addEventListener("click", () => handleDeleteProduct(p));

        actionTd.appendChild(editBtn);
        actionTd.appendChild(delBtn);
        tr.appendChild(actionTd);

        productsTbody.appendChild(tr);
    });
}

function startEditProduct(product) {
    clearBanner();
    productIdInput.value = product.id;
    productNameInput.value = product.name;
    productSkuInput.value = product.sku;
    productCategoryInput.value = product.category || "";
    productPriceInput.value = product.price;
    productStockInput.value = product.stockQuantity;

    productFormTitle.textContent = `2. Products — Edit Product #${product.id}`;
    productSubmitBtn.textContent = "Update Product";
    productCancelBtn.classList.remove("hidden");
}

function resetProductForm() {
    productForm.reset();
    productIdInput.value = "";
    productFormTitle.textContent = "2. Products — Create New Product";
    productSubmitBtn.textContent = "Create Product";
    productCancelBtn.classList.add("hidden");
}

productCancelBtn.addEventListener("click", () => {
    resetProductForm();
    clearBanner();
});

productForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearBanner();

    const name = productNameInput.value.trim();
    const sku = productSkuInput.value.trim();
    const category = productCategoryInput.value.trim();
    const price = parseFloat(productPriceInput.value);
    const stockQuantity = parseInt(productStockInput.value, 10);

    if (!name || !sku || !category || Number.isNaN(price) || price <= 0 || Number.isNaN(stockQuantity) || stockQuantity < 0) {
        showBanner("Please enter valid product fields (name, SKU, category, price > 0, stock >= 0).", "error");
        return;
    }

    const payload = { name, sku, category, price, stockQuantity };
    const editId = productIdInput.value;
    const isEdit = editId !== "";

    productSubmitBtn.disabled = true;
    try {
        if (isEdit) {
            await request(`/api/products/${editId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            showBanner(`Product #${editId} (${name}) updated successfully.`, "success");
        } else {
            const created = await request("/api/products", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            showBanner(`Product #${created.id} (${created.name}) created successfully.`, "success");
        }

        resetProductForm();
        await loadProducts();
        await populateRelationshipDropdowns();
    } catch (err) {
        showBanner(err.message, "error");
    } finally {
        productSubmitBtn.disabled = false;
    }
});

async function handleDeleteProduct(product) {
    clearBanner();
    const confirmed = window.confirm(
        `Are you sure you want to delete Product #${product.id} "${product.name}" (SKU: ${product.sku})?`
    );
    if (!confirmed) return;

    try {
        await request(`/api/products/${product.id}`, { method: "DELETE" });
        if (productIdInput.value === String(product.id)) {
            resetProductForm();
        }
        showBanner(`Product #${product.id} (${product.name}) deleted.`, "success");
        await loadProducts();
        await populateRelationshipDropdowns();
    } catch (err) {
        showBanner(err.message, "error");
    }
}

document.getElementById("apply-filter-btn").addEventListener("click", () => {
    productState.category = document.getElementById("category-filter").value;
    productState.page = 0;
    loadProducts();
});

document.getElementById("clear-filter-btn").addEventListener("click", () => {
    document.getElementById("category-filter").value = "";
    productState.category = "";
    productState.page = 0;
    loadProducts();
});

document.getElementById("prev-page-btn").addEventListener("click", () => {
    if (productState.page > 0) {
        productState.page -= 1;
        loadProducts();
    }
});

document.getElementById("next-page-btn").addEventListener("click", () => {
    if (productState.page + 1 < productState.totalPages) {
        productState.page += 1;
        loadProducts();
    }
});

const userForm = document.getElementById("user-form");
const userIdInput = document.getElementById("user-id");
const userUsernameInput = document.getElementById("user-username");
const userEmailInput = document.getElementById("user-email");
const userSubmitBtn = document.getElementById("user-submit-btn");
const userCancelBtn = document.getElementById("user-cancel-btn");
const userFormTitle = document.getElementById("user-form-title");
const usersTbody = document.getElementById("users-tbody");

async function loadUsers() {
    try {
        const users = await request("/api/users");
        renderUsersTable(users);
    } catch (err) {
        usersTbody.innerHTML = "";
        const tr = document.createElement("tr");
        const td = document.createElement("td");
        td.colSpan = 4;
        td.textContent = `Could not load users: ${err.message}`;
        tr.appendChild(td);
        usersTbody.appendChild(tr);
        showBanner(err.message, "error");
    }
}

function renderUsersTable(users) {
    usersTbody.innerHTML = "";
    if (users.length === 0) {
        const tr = document.createElement("tr");
        const td = document.createElement("td");
        td.colSpan = 4;
        td.textContent = "No users found.";
        tr.appendChild(td);
        usersTbody.appendChild(tr);
        return;
    }

    users.forEach(u => {
        const tr = document.createElement("tr");

        [u.id, u.username, u.email || "-"].forEach(val => {
            const td = document.createElement("td");
            td.textContent = val;
            tr.appendChild(td);
        });

        const actionTd = document.createElement("td");

        const editBtn = document.createElement("button");
        editBtn.type = "button";
        editBtn.className = "btn secondary small";
        editBtn.textContent = "Edit";
        editBtn.addEventListener("click", () => startEditUser(u));

        const delBtn = document.createElement("button");
        delBtn.type = "button";
        delBtn.className = "btn danger small";
        delBtn.style.marginLeft = "0.4rem";
        delBtn.textContent = "Delete";
        delBtn.addEventListener("click", () => handleDeleteUser(u));

        actionTd.appendChild(editBtn);
        actionTd.appendChild(delBtn);
        tr.appendChild(actionTd);

        usersTbody.appendChild(tr);
    });
}

function startEditUser(user) {
    clearBanner();
    userIdInput.value = user.id;
    userUsernameInput.value = user.username;
    userEmailInput.value = user.email || "";

    userFormTitle.textContent = `3. Users — Edit User #${user.id}`;
    userSubmitBtn.textContent = "Update User";
    userCancelBtn.classList.remove("hidden");
}

function resetUserForm() {
    userForm.reset();
    userIdInput.value = "";
    userFormTitle.textContent = "3. Users — Create New User";
    userSubmitBtn.textContent = "Create User";
    userCancelBtn.classList.add("hidden");
}

userCancelBtn.addEventListener("click", () => {
    resetUserForm();
    clearBanner();
});

userForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearBanner();

    const username = userUsernameInput.value.trim();
    const email = userEmailInput.value.trim();

    if (username.length < 3 || !email.includes("@")) {
        showBanner("Please enter a username (at least 3 chars) and a valid email.", "error");
        return;
    }

    const payload = { username, email };
    const editId = userIdInput.value;
    const isEdit = editId !== "";

    userSubmitBtn.disabled = true;
    try {
        if (isEdit) {
            await request(`/api/users/${editId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            showBanner(`User #${editId} (${username}) updated successfully.`, "success");
        } else {
            const created = await request("/api/users", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            showBanner(`User #${created.id} (${created.username}) created successfully.`, "success");
        }

        resetUserForm();
        await loadUsers();
        await populateRelationshipDropdowns();
    } catch (err) {
        showBanner(err.message, "error");
    } finally {
        userSubmitBtn.disabled = false;
    }
});

async function handleDeleteUser(user) {
    clearBanner();
    const confirmed = window.confirm(
        `Are you sure you want to delete User #${user.id} "${user.username}"?`
    );
    if (!confirmed) return;

    try {
        await request(`/api/users/${user.id}`, { method: "DELETE" });
        if (userIdInput.value === String(user.id)) {
            resetUserForm();
        }
        showBanner(`User #${user.id} (${user.username}) deleted.`, "success");
        await loadUsers();
        await populateRelationshipDropdowns();
    } catch (err) {
        showBanner(err.message, "error");
    }
}

const orderForm = document.getElementById("order-form");
const orderUserSelect = document.getElementById("order-user-select");
const orderProductSelect = document.getElementById("order-product-select");
const orderQtyInput = document.getElementById("order-quantity");
const orderSimFailCheckbox = document.getElementById("order-simulate-failure");
const orderSubmitBtn = document.getElementById("order-submit-btn");
const ordersTbody = document.getElementById("orders-tbody");

async function populateRelationshipDropdowns() {
    try {
        const [users, productPage] = await Promise.all([
            request("/api/users"),
            request("/api/products?page=0&size=100&sort=id,asc")
        ]);

        const prevUser = orderUserSelect.value;
        orderUserSelect.innerHTML = "";
        const defaultUserOpt = document.createElement("option");
        defaultUserOpt.value = "";
        defaultUserOpt.textContent = "-- Select Customer --";
        orderUserSelect.appendChild(defaultUserOpt);

        users.forEach(u => {
            const opt = document.createElement("option");
            opt.value = u.id;
            opt.textContent = `#${u.id} — ${u.username} (${u.email || "no email"})`;
            orderUserSelect.appendChild(opt);
        });
        if (prevUser) orderUserSelect.value = prevUser;

        const prevProd = orderProductSelect.value;
        orderProductSelect.innerHTML = "";
        const defaultProdOpt = document.createElement("option");
        defaultProdOpt.value = "";
        defaultProdOpt.textContent = "-- Select Product --";
        orderProductSelect.appendChild(defaultProdOpt);

        (productPage.content || []).forEach(p => {
            const opt = document.createElement("option");
            opt.value = p.id;
            opt.textContent = `#${p.id} — ${p.name} ($${Number(p.price).toFixed(2)} | Stock: ${p.stockQuantity})`;
            orderProductSelect.appendChild(opt);
        });
        if (prevProd) orderProductSelect.value = prevProd;
    } catch (err) {
        showBanner(`Failed to populate relationship selectors: ${err.message}`, "error");
    }
}

async function loadOrders() {
    try {
        const orders = await request("/api/orders");
        ordersTbody.innerHTML = "";

        if (orders.length === 0) {
            const tr = document.createElement("tr");
            const td = document.createElement("td");
            td.colSpan = 8;
            td.textContent = "No orders placed yet.";
            tr.appendChild(td);
            ordersTbody.appendChild(tr);
            return;
        }

        orders.forEach(o => {
            const tr = document.createElement("tr");
            const cells = [
                o.orderId,
                o.orderDate,
                `${o.username} (#${o.userId})`,
                `${o.productName} (#${o.productId})`,
                o.quantity,
                `$${Number(o.unitPrice).toFixed(2)}`,
                `$${Number(o.totalAmount).toFixed(2)}`,
                o.status
            ];
            cells.forEach(val => {
                const td = document.createElement("td");
                td.textContent = val;
                tr.appendChild(td);
            });
            ordersTbody.appendChild(tr);
        });
    } catch (err) {
        ordersTbody.innerHTML = "";
        const tr = document.createElement("tr");
        const td = document.createElement("td");
        td.colSpan = 8;
        td.textContent = `Could not load orders: ${err.message}`;
        tr.appendChild(td);
        ordersTbody.appendChild(tr);
    }
}

orderForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearBanner();

    const userId = parseInt(orderUserSelect.value, 10);
    const productId = parseInt(orderProductSelect.value, 10);
    const quantity = parseInt(orderQtyInput.value, 10);
    const simulateFailure = orderSimFailCheckbox.checked;

    if (Number.isNaN(userId) || Number.isNaN(productId) || Number.isNaN(quantity) || quantity < 1) {
        showBanner("Please select a customer, a product, and a valid quantity (>= 1).", "error");
        return;
    }

    orderSubmitBtn.disabled = true;
    try {
        const createdOrder = await request("/api/orders", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userId, productId, quantity, simulateFailure })
        });

        showBanner(
            `Order #${createdOrder.orderId} confirmed! Total: $${Number(createdOrder.totalAmount).toFixed(2)}. Remaining stock: ${createdOrder.remainingStock}.`,
            "success"
        );
        orderQtyInput.value = "1";
        orderSimFailCheckbox.checked = false;

        await Promise.all([loadOrders(), loadProducts(), populateRelationshipDropdowns()]);
    } catch (err) {
        showBanner(err.message, "error");
        await Promise.all([loadProducts(), populateRelationshipDropdowns()]);
    } finally {
        orderSubmitBtn.disabled = false;
    }
});

document.addEventListener("DOMContentLoaded", async () => {
    await Promise.all([
        loadProducts(),
        loadUsers(),
        loadOrders(),
        populateRelationshipDropdowns()
    ]);
});