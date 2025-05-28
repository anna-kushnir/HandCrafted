let selectedPhotos = [];

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

function submitAddProduct(event) {
    event.preventDefault();

    if (selectedPhotos.length < 1) {
        alert("Потрібно додати хоча б одне фото");
        return;
    }

    const productDto = {
        name: document.getElementById("name").value,
        description: document.getElementById("description").value,
        colors: document.getElementById("colors").value,
        keyWords: document.getElementById("key_words").value,
        price: document.getElementById("price").value,
        inStock: document.getElementById("in_stock_checkbox").checked,
        quantity: document.getElementById("quantity").value,
        canAddToGiftSet: document.getElementById("can_add_to_gift_set_checkbox").checked,
        maxQuantityInGiftSet: document.getElementById("max_quantity_in_gift_set").value,
        categoryId: document.getElementById("category").value,
        photos: selectedPhotos
    };

    fetch("/admin/products", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(productDto)
    })
        .then(() => {
            location.replace("/admin/products");
        })
        .catch(error => {
            console.error('Сталася помилка при додаванні товару:', error);
        });
}