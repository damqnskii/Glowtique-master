document.addEventListener("DOMContentLoaded", function () {
    const editBtn = document.getElementById("editBtn");
    const saveBtn = document.getElementById("saveBtn");
    const inputs = document.querySelectorAll("input, select");

    editBtn.addEventListener("click", function (event) {
        event.preventDefault();

        inputs.forEach(input => {
            input.removeAttribute("disabled");
        });

        saveBtn.removeAttribute("disabled");

        editBtn.style.display = "none";
    });
});