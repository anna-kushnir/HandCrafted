document.addEventListener("DOMContentLoaded", () => {
    const localCart = JSON.parse(localStorage.getItem("cart") || "[]");
    const yesBtn = document.getElementById("yes_btn");
    const noBtn = document.getElementById("no_btn");
    const maybeLaterBtn = document.getElementById("maybe_later_btn");

    yesBtn.addEventListener("click", () => {
        if (localCart.length > 0) {
            fetch("/cart/merge", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(localCart)
            })
                .then(response => {
                    if (response.ok) {
                        localStorage.removeItem("cart");
                        sessionStorage.removeItem("mergeCartRequest");
                        window.location.href = "/cart";
                    }
                });
        }
    });

    noBtn.addEventListener("click", () => {
        localStorage.removeItem("cart");
        sessionStorage.removeItem("mergeCartRequest");
        window.location.href = "/products";
    });

    maybeLaterBtn.addEventListener("click", () => {
        sessionStorage.removeItem("mergeCartRequest");
        window.location.href = "/products";
    });
});