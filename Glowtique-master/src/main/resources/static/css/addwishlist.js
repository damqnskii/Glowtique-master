document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".wishlist-btn").forEach(button => {
        button.addEventListener("click", function () {
            toggleWishlist(this);
        });
    });


    fetch("/wishlist/items", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .then(wishlistItems => {
            wishlistItems.forEach(item => {
                let button = document.querySelector(`.wishlist-btn[data-id="${item.id}"]`);
                if (button) {
                    button.classList.add("active");
                    button.classList.add("wishlist-btn-liked");
                }
            });
        })
        .catch(error => console.error("Error fetching wishlist:", error));
});


function toggleWishlist(button) {
    const productId = button.getAttribute("data-id");
    const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content") || "";


    fetch(`/wishlist/add/${productId}`, {
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

            if (data.added) {
                button.classList.add("wishlist-btn-liked")
                button.classList.remove("wishlist-btn")
                button.innerHTML = "&#10084";
                showToast("Продуктът беше добавен към любими.");
            } else {
                button.classList.remove("wishlist-btn-liked");
                button.classList.add("wishlist-btn");
                button.innerHTML = "&#10084";
                showToast("Продуктът беше премахнат от любими.");
            }

        })
        .catch(error => {
            console.error("Error:", error);
            showToast("Възникна проблем. Пробвайте отново.", true);

        });
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
}
