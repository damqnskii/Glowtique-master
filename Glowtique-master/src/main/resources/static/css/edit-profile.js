document.addEventListener("DOMContentLoaded", function () {
    const editBtn = document.getElementById("editBtn");
    const saveBtn = document.getElementById("saveBtn");
    const inputs = document.querySelectorAll("input, select");

    editBtn.addEventListener("click", function (event) {
        event.preventDefault(); // Prevent form submission

        // Enable all input fields
        inputs.forEach(input => {
            input.removeAttribute("disabled");
        });

        // Enable the save button
        saveBtn.removeAttribute("disabled");

        // Hide the edit button
        editBtn.style.display = "none";
    });
});