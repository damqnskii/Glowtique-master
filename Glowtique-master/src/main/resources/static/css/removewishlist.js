document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".wishlist-btn-liked").forEach(button => {
        button.addEventListener("click", function () {
            toggleWishlist(this);
        });
    });

    function toggleWishlist(button) {
        const productId = button.getAttribute("data-id");
        const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content") || ""; // Get CSRF token if available
        const isLiked = button.classList.contains("wishlist-btn-liked");

        const url = isLiked ? `/wishlist/remove/${productId}` : `/wishlist/add/${productId}`;

        fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                ...(csrfToken && { "X-CSRF-TOKEN": csrfToken })
            }
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw new Error(err.message); });
                }
                return response.json();
            })
            .then(data => {
                console.log("Wishlist Response:", data);

                if (isLiked) {
                    button.classList.remove("wishlist-btn-liked");
                    button.classList.add("wishlist-btn");
                    button.innerHTML = "&#10084";
                    showToast("Продуктът беше премахнат от любими.");
                } else {
                    button.classList.add("wishlist-btn-liked");
                    button.classList.remove("wishlist-btn");
                    button.innerHTML = "&#10084";
                    showToast("Продуктът беше добавен към любими.");
                }
            })
            .catch(error => {
                console.error("Error:", error);
                showToast("Възникна проблем. Пробвайте отново.", true);
            });
    }
    function showToast(message, isError = false) {
        const toast = document.getElementById("toast");
        const toastMessage = document.getElementById("toast-message");

        toastMessage.textContent = message;
        toast.style.backgroundColor = isError ? '#e74c3c' : '#333';

        toast.classList.add("show");
        toast.classList.remove("hidden");

        setTimeout(() => {
            toast.classList.remove("show");
            toast.classList.add("hidden");
        }, 3000);
    }
});
