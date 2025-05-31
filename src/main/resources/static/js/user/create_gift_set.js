const body = document.getElementById("body");
const productCheckboxes = document.getElementsByClassName("product-checkbox");
const quantityInputs = document.getElementsByClassName("quantity-input");
const continueBtn = document.getElementById("continue_btn");
const form = document.querySelector("form");

const url = "/giftSets/";

body.addEventListener("change", (event) => {
    for (let cb of productCheckboxes) {
        if (cb.contains(event.target)) {
            const product_id = parseInt(cb.id.replace(/\D/g, '').replace("checkbox", ''), 10);
            const quantityInput = document.getElementById("quantity" + product_id);
            quantityInput.disabled = !cb.checked;
            if (!cb.checked) {
                quantityInput.value = '1';
            }
            updateTotalPrice();
        }
    }
    for (let input of quantityInputs) {
        if (input.contains(event.target)) {
            updateTotalPrice();
        }
    }
});

function updateTotalPrice() {
    let totalPrice = 0;
    for (let quantityInput of quantityInputs) {
        const product_id = parseInt(quantityInput.dataset.id, 10);
        const checkbox = document.getElementById("checkbox" + product_id);
        if (checkbox && checkbox.checked) {
            const product_price = parseFloat(quantityInput.dataset.price);
            totalPrice += product_price * quantityInput.value;
        }
    }
    document.getElementById("total_price").textContent = totalPrice + "₴";
}

continueBtn.addEventListener("click", (e) => {
    e.preventDefault();

    const selectedItems = [];

    const productCards = Array.from(document.getElementsByClassName("product-card"));
    for (let card of productCards) {
        card.style.pointerEvents = "none";
        const checkbox = card.querySelector("input[type='checkbox']");
        if (!checkbox || !checkbox.checked) {
            card.remove();
        } else {
            const quantityInput = card.querySelector("input[type='number']");
            const productId = parseInt(quantityInput.dataset.id);
            const productName = card.querySelector(".product-name").textContent;
            const productCost = parseFloat(quantityInput.dataset.price);
            const quantity = parseInt(quantityInput.value);

            selectedItems.push({ productId, productName, productCost, quantity });

            checkbox.remove();
            const container = card.querySelector(".quantity-container");
            container.innerHTML = `<p class="product-quantity">Кількість: ${quantity}</p>`;
        }
    }

    if (selectedItems.length === 0) {
        alert("Для створення набору оберіть, будь ласка, хоча б один товар");
        document.location.reload();
        return;
    }

    document.querySelector(".price-and-options-container").remove();

    const newFooter = document.createElement("div");
    newFooter.className = "gift-set-options-container";
    newFooter.innerHTML = `
        <div class="wish-container">
            <label for="wishes">Побажання до пакування:</label>
            <textarea id="wishes" class="text-box" rows="3" placeholder="Використайте рожевий папір/пакування..."></textarea>
        </div>
        <div class="gift-set-price-container">
            <p>Загальна вартість товарів: ${getTotalPrice(selectedItems)}₴ + Вартість пакування: ${wrapPrice}₴</p>
        </div>
        <div class="price-and-options-container">
            <div class="total-price-container">
                <p>Вартість набору: ${getTotalPrice(selectedItems) + wrapPrice}₴</p>
            </div>
            <div class="options-container">
                <a href="/favorite">
                    <button type="button" class="btn btn-outline-secondary" id="cancel_btn">
                        Скасувати
                    </button>
                </a>
                <button type="button" class="btn btn-outline-primary" id="submit_gift_set_btn">
                    Додати до кошика
                </button>
            </div>
        </div>
    `
    form.appendChild(newFooter);

    document.getElementById("submit_gift_set_btn").addEventListener("click", () => {
        const wishes = document.getElementById("wishes").value.trim();
        const giftSetDto = {
            items: selectedItems.map((item) => ({
                productId: item.productId,
                productName: item.productName,
                quantity: item.quantity
            })),
            packagingWishes: wishes
        };

        fetch("/giftSets/new", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(giftSetDto)
        })
            .then(response => {
                if (response.ok) {
                    window.location.href = "/cart";
                } else {
                    console.log("Не вдалося додати подарунковий набір до кошика");
                }
            });
    });
});

function getTotalPrice(items) {
    return items.reduce((sum, item) => sum + item.productCost * item.quantity, 0);
}