// Filtro de recetas segun la barra de busquedas
document.addEventListener("DOMContentLoaded", function () {

    //Tomamos la barra de busqueda de la pestaña
    const searchInput = document.getElementById("searchInput");

    //Toammos los elementos que almacenen las distintas recetas que se han creado
    const recipes = document.querySelectorAll(".card_wrapper");

    //Cada vez que se escriba algo en la barra de busqueda se hace una criba de las recetas que no contengan ese texto en su titulo
    searchInput.addEventListener("input", function () {

        //Se toma el texto que se ha escrito
        const searchText = searchInput.value.toLowerCase();

        //Se mira receta a receta si tienen el titulo, sino lo tienen se oculta la receta.
        recipes.forEach(recipe => {

            
            //Se toma el titulo de cada receta en minuscula para compararlo con el texto de busqueda
            const title = recipe.querySelector(".card_footer_middle")
                                .textContent
                                .toLowerCase();

            //Se establece el display segun se tenga o no
            if (title.includes(searchText)) {
                recipe.style.display = "";
            } else {
                recipe.style.display = "none";
            }

        });

    });

});


// Funcion para mostrar las estrellas de cada receta segun su valoracion
document.addEventListener("DOMContentLoaded", function () {

    // Recorremos cada tarjeta de receta por separado
    const recipeCards = document.querySelectorAll(".card_wrapper");

    recipeCards.forEach(card => {

        // Valor numerico de la receta actual
        const ratingElement = card.querySelector(".rating-number");

        // Estrellas solo de esta tarjeta
        const stars = card.querySelectorAll(".star");

        if (!ratingElement) {
            return;
        }

        const averageRating = parseFloat(ratingElement.textContent);

        stars.forEach(star => {

            const starValue = parseInt(star.dataset.value);

            star.classList.remove("filled");

            if (averageRating >= starValue) {
                star.classList.add("filled");
            }
        });
    });
});