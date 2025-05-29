const body = document.getElementById("body");
const removeButtons = document.getElementsByClassName("remove-btn");
const transferToFavButtons = document.getElementsByClassName("transfer-to-fav-btn");
const quantityInputs = document.getElementsByClassName("quantity-input");

const url = "/cart/products/";

if (!isAuthenticated) {
    const localCart = JSON.parse(localStorage.getItem("cart") || "[]");
    if (localCart.length > 0) {
        fetch("cart/guest", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(localCart)
        })
            .then(response => response.json())
            .then(data => {
                renderCart(data);
            })
            .catch(err => console.error("Сталася помилка при отриманні локального кошика: ", err));
    } else {
        renderCart([]);
    }

    function renderCart(items) {
        const cartContainer = document.querySelector(".product-listing");
        const emptyMessage = document.querySelector(".empty-msg");
        const totalPriceContainer = document.querySelector(".total-price-container p:last-child");
        const orderControls = document.querySelector(".cart-price-and-options-container");

        if (!cartContainer || isAuthenticated) return;

        const cartItems = Array.from(items);

        cartContainer.innerHTML = "";
        let totalPrice = 0;
        if (cartItems.length === 0) {
            cartContainer.style.display = "none";
            emptyMessage.style.display = "block";
            orderControls.style.display = "none";
            return;
        } else {
            cartContainer.style.display = "grid";
            emptyMessage.style.display = "none";
            orderControls.style.display = "flex";
        }

        localStorage.removeItem("cart");
        const newLocalCart = [];

        cartItems.forEach(item => {
            newLocalCart.push({productId: item.productId, quantityInCart: item.quantityInCart});

            const card = document.createElement("div");
            card.classList.add("product-card");

            card.innerHTML = `
                <div class="product-img-and-info-container">
                    <div class="product-img-container">
                        <a href="/products/${item.productId}">
                            <img src="data:image/jpeg;base64,${item.photos[0]}" class="product-img" alt="фото товару" />
                        </a>
                    </div>
                    <div class="product-info-container">
                        <p>${item.name}</p>
                        <p>Ціна: ${item.cost}₴</p>
                        <div class="quantity-container">
                            <p>Кількість:</p>
                            <input
                                type="number"
                                id="${item.productId}"
                                class="text-box quantity-input"
                                min="1"
                                max="${item.productQuantity}"
                                value="${item.quantityInCart}" />
                        </div>
                    </div>
                </div>
                <div class="product-options-container">
                    <button class="btn btn-outline-secondary remove-btn" id="product${item.productId}">Видалити</button>
                </div>
            `;

            cartContainer.appendChild(card);
            totalPrice += item.cost * item.quantityInCart;
        });

        localStorage.setItem("cart", JSON.stringify(newLocalCart));
        totalPriceContainer.innerHTML = `${totalPrice}₴`;
    }
}

body.addEventListener("click", (event) => {
// Обробка видалення товару з кошику
    for (let i = 0; i < removeButtons.length; i++) {
        if (removeButtons[i].contains(event.target)) {
            if (removeButtons[i].id.includes("product", 0)) {
                const product_id = parseInt(removeButtons[i].id.replace(/\D/g, '').replace("product", ''), 10);
                if (isAuthenticated) {
                    fetch(url + product_id + "/deleteFromCart", {
                        method: "DELETE"
                    })
                        .then(() => {
                            location.replace("/cart")
                        })
                        .catch(error => {
                            console.error('Сталася помилка при видаленні товару з кошику:', error);
                        });
                } else {
                    let localCart = JSON.parse(localStorage.getItem("cart") || "[]");
                    localCart = localCart.filter(item => item.productId !== product_id);
                    localStorage.setItem("cart", JSON.stringify(localCart));
                    location.replace("/cart");
                }
            } else {
                const gift_set_id = parseInt(removeButtons[i].id.replace(/\D/g, '').replace("giftset", ''), 10);
                fetch("/cart/giftSets/" + gift_set_id + "/deleteFromCart", {
                    method: "DELETE"
                })
                    .then(() => {
                        location.replace("/cart")
                    })
                    .catch(error => {
                        console.error('Сталася помилка при видаленні подарункового набору з кошику:', error);
                    });
            }
        }
    }

    // Обробка перенесення товару з кошику у вподобане
    if (isAuthenticated) {
        for (let i = 0; i < transferToFavButtons.length; i++) {
            if (transferToFavButtons[i].contains(event.target)) {
                const product_id = parseInt(transferToFavButtons[i].id.replace(/\D/g, ''), 10);
                fetch(url + product_id + "/transferToFavorite", {
                    method: "PUT"
                })
                    .then(() => {
                        location.replace("/cart")
                    })
                    .catch(error => {
                        console.error('Сталася помилка при перенесенні товару з кошику до вподобаного:', error);
                    });
            }
        }
    }
})

// Обробка зміни кількості товару в кошику
body.addEventListener("change", (event) => {
    for (let i = 0; i < quantityInputs.length; i++) {
        if (quantityInputs[i].contains(event.target)) {
            const product_id = parseInt(quantityInputs[i].id.replace(/\D/g, ''), 10);
            const newQuantity = parseInt(quantityInputs[i].value);
            const maxQuantity = parseInt(quantityInputs[i].max.replace(/\D/g, ''), 10);
            if (newQuantity > maxQuantity) {
                location.replace("/cart");
                return;
            }

            if (isAuthenticated) {
                fetch(url + product_id + "/updateQuantity/" + newQuantity, {
                    method: "PUT"
                })
                    .then(() => {
                        location.replace("/cart")
                    })
                    .catch(error => {
                        console.error('Сталася помилка при зміні кількості товару в кошику:', error);
                    });
            } else {
                let localCart = JSON.parse(localStorage.getItem("cart") || "[]");
                for (let item of localCart) {
                    if (item.productId === product_id) {
                        item.quantityInCart = newQuantity;
                        break;
                    }
                }
                localStorage.setItem("cart", JSON.stringify(localCart));
                location.replace("/cart");
            }
        }
    }
})