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

//Funcion para mostar estrellas segun valoracion
document.addEventListener("DOMContentLoaded", function () {

    const stars = document.querySelectorAll(".star");
    const averageRating = parseFloat(document.getElementById("ratingNumber").textContent); 

    stars.forEach(star => {

        const starValue = parseInt(star.dataset.value);

        star.classList.remove("filled");

        if(averageRating >= starValue){
            star.classList.add("filled");
        }

    });
});