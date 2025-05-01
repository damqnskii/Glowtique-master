document.addEventListener("DOMContentLoaded", function () {
    const tabs = document.querySelectorAll(".tab");
    const contents = document.querySelectorAll(".tab-content");

    // Ensure only the first content is visible on load
    contents.forEach((content, index) => {
        content.style.display = index === 0 ? "block" : "none";
    });

    tabs.forEach((tab, index) => {
        tab.addEventListener("click", function () {
            tabs.forEach(t => t.classList.remove("active"));
            tab.classList.add("active");

            contents.forEach(content => content.style.display = "none");
            contents[index].style.display = "block";
        });
    });
});