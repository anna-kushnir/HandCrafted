window.addEventListener("DOMContentLoaded", () => {
    if (isAuthenticated) {
        const mergeCartRequest = sessionStorage.getItem("mergeCartRequest");
        if (mergeCartRequest === "true") {
            window.location.href = "/merge-cart-check";
        }
    }

    let savedCategoryId = null;
    if (categoryWasChosen) {
        savedCategoryId = sessionStorage.getItem("selectedCategoryId");
    } else {
        sessionStorage.removeItem("selectedCategoryId");
    }

    if (savedCategoryId !== null) {
        for (let i = 0; i < categoryButtons.length; i++) {
            const categoryBtn = categoryButtons[i];
            const categoryBtnId = parseInt(categoryBtn.id.replace(/\D/g, ''), 10);
            if (categoryBtnId === parseInt(savedCategoryId)) {
                categoryBtn.classList.add("active-category");
                break;
            }
        }
    } else {
        const categoryBtn = categoryButtons[categoryButtons.length - 1];
        const categoryBtnId = parseInt(categoryBtn.id.replace(/\D/g, ''), 10);
        if (categoryBtnId === 0) {
            categoryBtn.classList.add("active-category");
        }
    }
});


const body = document.getElementById("body");
const categoryButtons = document.getElementsByClassName("btn-category");

body.addEventListener("click", (event) => {
    console.log("click")
    for (let i = 0; i < categoryButtons.length; i++) {
        if (categoryButtons[i].contains(event.target)) {
            const category_id = parseInt(categoryButtons[i].id.replace(/\D/g, ''), 10)
            if (category_id === 0) {
                sessionStorage.removeItem("selectedCategoryId");
                window.location.replace("/products");
            }
            else {
                sessionStorage.setItem("selectedCategoryId", category_id);
                window.location.replace("/products?categoryId=" + category_id)
            }
        }
    }
});


const applyFiltersButton = document.getElementById("apply_filters_btn")
applyFiltersButton.addEventListener("click", applyFilters)

function applyFilters() {
    const savedCategoryId = sessionStorage.getItem("selectedCategoryId");

    const sortByCost = document.getElementById('checkbox_cost').checked;
    const sortByCostAsc = document.getElementById('cost_sort_direction_asc').checked;
    const sortByNewness = document.getElementById('checkbox_newness').checked;
    const sortByNewnessAsc = document.getElementById('newness_sort_direction_asc').checked;
    const priceFrom = document.getElementById('price_from').value;
    const priceTo = document.getElementById('price_to').value;

    const colorCheckboxes = document.querySelectorAll('input[name="colorIds"]:checked');
    const selectedColorIds = Array.from(colorCheckboxes).map(c => c.value);
    const colorParams = selectedColorIds.map(id => `colorIds=${id}`).join('&');

    let requestURL = `/products?sortByCost=${sortByCost}&sortByCostAsc=${sortByCostAsc}&sortByNewness=${sortByNewness}&sortByNewnessAsc=${sortByNewnessAsc}&priceFrom=${priceFrom}&priceTo=${priceTo}`;
    if (selectedColorIds.length > 0) {
        requestURL += `&${colorParams}`;
    }
    if (savedCategoryId !== null) {
        requestURL += `&categoryId=${savedCategoryId}`;
    }
    window.location.replace(requestURL);
}

const findButton = document.getElementById("find_btn")
findButton.addEventListener("click", findProducts)

function findProducts() {
    const savedCategoryId = sessionStorage.getItem("selectedCategoryId");
    const searchLine = document.getElementById('search_line').value;
    let requestURL = "/products"
    if (searchLine.trim() !== "") {
        requestURL += `?search=${searchLine}`;
        if (savedCategoryId !== null) {
            requestURL += `&categoryId=${savedCategoryId}`;
        }
    } else {
        if (savedCategoryId !== null) {
            requestURL += `?categoryId=${savedCategoryId}`;
        }
    }
    window.location.replace(requestURL);
}