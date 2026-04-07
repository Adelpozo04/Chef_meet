// Filtro de recetas segun la barra de busquedas
document.addEventListener("DOMContentLoaded", function () {

    const searchInput = document.getElementById("searchInput");
    const recipes = document.querySelectorAll(".card_wrapper");

    searchInput.addEventListener("input", function () {

        const searchText = searchInput.value.toLowerCase();

        recipes.forEach(recipe => {

            const title = recipe.querySelector(".card_footer")
                                .textContent
                                .toLowerCase();

            if (title.includes(searchText)) {
                recipe.style.display = "";
            } else {
                recipe.style.display = "none";
            }

        });

    });

});