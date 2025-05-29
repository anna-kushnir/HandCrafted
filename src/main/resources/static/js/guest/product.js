function nextPhoto(arrowElement) {
    const img = arrowElement.parentElement.querySelector(".gallery-photo");
    const photos = parsePhotosString(img.dataset.photos || "");
    if (photos.length === 0) return;

    let index = parseInt(img.dataset.index, 10);
    index = (index + 1) % photos.length;

    img.dataset.index = index.toString();
    img.src = `data:image/jpeg;base64,${photos[index]}`;
}

function prevPhoto(arrowElement) {
    const img = arrowElement.parentElement.querySelector(".gallery-photo");
    const photos = parsePhotosString(img.dataset.photos || "");
    if (photos.length === 0) return;

    let index = parseInt(img.dataset.index, 10);
    index = (index - 1 + photos.length) % photos.length;

    img.dataset.index = index.toString();
    img.src = `data:image/jpeg;base64,${photos[index]}`;
}


document.addEventListener("DOMContentLoaded", function () {
    const cartMsg = document.getElementById("cart_msg_box");
    const favMsg = document.getElementById("favorite_msg_box");
    const clientMsg = document.getElementById("client_msg_box");
    if (cartMsg) {
        setTimeout(() => {
            cartMsg.style.display = "none";
        }, 3000);
    }
    if (favMsg) {
        setTimeout(() => {
            favMsg.style.display = "none";
        }, 3000);
    }
    if (clientMsg) {
        setTimeout(() => {
            clientMsg.style.display = "none";
        }, 3000);
    }
})


const addToCartBtn = document.getElementsByClassName("add-to-cart-btn");

if (addToCartBtn.length !== 0) {
    addToCartBtn[0].addEventListener("click", (e) => {
        e.preventDefault();
        const product_id = parseInt(addToCartBtn[0].id.replace(/\D/g, ''));
        if (!isAuthenticated) {
            const localCart = JSON.parse(localStorage.getItem("cart") || "[]");
            const index = localCart.findIndex(item => item.productId === product_id);
            const msgBox = document.getElementById("client_msg_box");

            if (index !== -1) {
                localCart.splice(index, 1);
                if (msgBox) {
                    msgBox.textContent = "Товар видалено з кошика";
                    msgBox.style.display = "block";
                    setTimeout(() => {
                        msgBox.style.display = "none";
                    }, 3000);
                }
            } else {
                localCart.push({productId: product_id, quantityInCart: 1});
                if (msgBox) {
                    msgBox.textContent = "Товар додано до кошика";
                    msgBox.style.display = "block";
                    setTimeout(() => {
                        msgBox.style.display = "none";
                    }, 3000);
                }
            }
            localStorage.setItem("cart", JSON.stringify(localCart));
        }
    })
}
