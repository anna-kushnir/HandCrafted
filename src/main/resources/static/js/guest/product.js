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