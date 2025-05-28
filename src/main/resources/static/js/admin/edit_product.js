const deleteButton = document.getElementsByClassName("delete-product-btn")[0];
deleteButton.addEventListener("click", deleteProduct)

function deleteProduct() {
    const product_id = parseInt(deleteButton.id.replace(/\D/g, ''), 10)
    fetch("/admin/products/" + product_id + "/delete", {
        method: "DELETE"
    })
        .then(() => {
            window.location.replace("/admin/products")
        })
        .catch(error => {
            console.error('Сталася помилка при видаленні товару:', error);
        });
}


let selectedPhotos = [];

// Виведення наявних фото товару
window.addEventListener("DOMContentLoaded", function () {
    const previewContainer = document.querySelector(".photo-preview-container");
    const existingPhotos = parsePhotosString(document.getElementById("photo_data").dataset.photos || "");

    existingPhotos.forEach(base64 => {
        if (selectedPhotos.length >= 5) return;
        selectedPhotos.push(base64);

        const imgWrapper = document.createElement("div");
        imgWrapper.classList.add("photo-wrapper");

        const img = document.createElement("img");
        img.src = `data:image/jpeg;base64,${base64}`;
        img.classList.add("preview-image");

        const removeBtn = document.createElement("button");
        removeBtn.classList.add("remove-photo");
        removeBtn.textContent = "✖";
        removeBtn.onclick = function () {
            const index = selectedPhotos.indexOf(base64);
            if (index !== -1) selectedPhotos.splice(index, 1);
            previewContainer.removeChild(imgWrapper);
        };

        imgWrapper.appendChild(img);
        imgWrapper.appendChild(removeBtn);
        previewContainer.appendChild(imgWrapper);
    })
})

// Додавання нових фото до товару
document.getElementById("photo").addEventListener("change", function (e) {
    const photoFiles = Array.from(e.target.files);
    const previewContainer = document.querySelector(".photo-preview-container");

    photoFiles.forEach(file => {
        if (selectedPhotos.length >= 5) {
            alert("Можна додати не більше 5 фото");
            return;
        }

        const reader = new FileReader();
        reader.onload = function (event) {
            const base64 = event.target.result.split(',')[1];

            selectedPhotos.push(base64);

            const imgWrapper = document.createElement("div");
            imgWrapper.classList.add("photo-wrapper");

            const img = document.createElement("img");
            img.src = event.target.result;
            img.classList.add("preview-image");

            const removeBtn = document.createElement("button");
            removeBtn.classList.add("remove-photo");
            removeBtn.textContent = "✖";
            removeBtn.onclick = function () {
                const index = selectedPhotos.indexOf(base64);
                if (index !== -1) selectedPhotos.splice(index, 1);
                previewContainer.removeChild(imgWrapper);
            };

            imgWrapper.appendChild(img);
            imgWrapper.appendChild(removeBtn);
            previewContainer.appendChild(imgWrapper);
        };
        reader.readAsDataURL(file);
    });

    e.target.value = "";
});

function submitEditProduct(event) {
    event.preventDefault();

    if (selectedPhotos.length < 1) {
        alert("Потрібно додати хоча б одне фото");
        return;
    }

    const saveButton = document.getElementsByClassName("save-product-btn")[0]
    const product_id = parseInt(saveButton.id.replace(/\D/g, ''), 10)

    const productDto = {
        id: product_id,
        name: document.getElementById("name").value,
        description: document.getElementById("description").value,
        colors: document.getElementById("colors").value,
        keyWords: document.getElementById("key_words").value,
        price: document.getElementById("price").value,
        withDiscount: document.getElementById("with_discount_checkbox").checked,
        discountedPrice: document.getElementById("discounted_price").value,
        inStock: document.getElementById("in_stock_checkbox").checked,
        quantity: document.getElementById("quantity").value,
        canAddToGiftSet: false,
        maxQuantityInGiftSet: 0,
        categoryId: document.getElementById("category").value,
        photos: selectedPhotos
    };

    fetch("/admin/products/" + product_id, {
        method: "PUT",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(productDto)
    })
        .then(() => {
            location.replace("/admin/products");
        })
        .catch(error => {
            console.error('Сталася помилка при редагуванні товару:', error);
        });
}