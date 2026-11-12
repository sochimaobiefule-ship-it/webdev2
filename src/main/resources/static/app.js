const API_BASE = "http://localhost:8080";

async function getJson(path) {
    const response = await fetch(`${API_BASE}${path}`);
    if (!response.ok) {
        throw new Error(`Request failed (${response.status} ${response.statusText})`);
    }
    return response.json();
}

async function loadProducts() {
    const statusEl = document.querySelector("#status");
    const listEl = document.querySelector("#product-list");
    const detailCard = document.querySelector("#detail-card");

    listEl.replaceChildren();
    detailCard.style.display = "none";
    
    statusEl.style.display = "block";
    statusEl.className = "status-box loading";
    statusEl.textContent = "Loading product records…";

    try {
        const page = await getJson("/api/products?page=0&size=10");
        const rows = page.content;

        if (!Array.isArray(rows)) {
            throw new Error("Unexpected list response shape");
        }

        if (rows.length === 0) {
            statusEl.className = "status-box empty";
            statusEl.textContent = "No products found in database.";
            return;
        }

        statusEl.style.display = "none";

        for (const item of rows) {
            const li = document.createElement("li");
            
            const nameSpan = document.createElement("span");
            nameSpan.textContent = item.name; 

            const priceSpan = document.createElement("span");
            priceSpan.textContent = `$${item.price}`;

            li.append(nameSpan, priceSpan);
            li.addEventListener("click", () => loadProductDetail(item.id));
            listEl.append(li);
        }
    } catch (error) {
        statusEl.className = "status-box error";
        statusEl.textContent = `Error loading products: ${error.message}`;
    }
}

async function loadProductDetail(id) {
    const detailCard = document.querySelector("#detail-card");
    const detailContent = document.querySelector("#detail-content");

    detailCard.style.display = "block";
    detailContent.textContent = "Loading product details...";

    try {
        const item = await getJson(`/api/products/${id}`);

        detailContent.replaceChildren();

        const pId = document.createElement("p");
        pId.textContent = `ID: ${item.id}`;

        const pName = document.createElement("p");
        pName.textContent = `Name: ${item.name}`;

        const pSku = document.createElement("p");
        pSku.textContent = `SKU: ${item.sku}`;

        const pCategory = document.createElement("p");
        pCategory.textContent = `Category: ${item.category || 'N/A'}`;

        const pPrice = document.createElement("p");
        pPrice.textContent = `Price: $${item.price}`;

        const pStock = document.createElement("p");
        pStock.textContent = `Stock: ${item.stockQuantity}`;

        detailContent.append(pId, pName, pSku, pCategory, pPrice, pStock);
    } catch (error) {
        detailContent.textContent = `Failed to load details: ${error.message}`;
    }
}

document.addEventListener("DOMContentLoaded", loadProducts);