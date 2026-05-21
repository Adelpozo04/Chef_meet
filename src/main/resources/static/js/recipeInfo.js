//Funcion para mostar estrellas segun valoracion
document.addEventListener("DOMContentLoaded", function () {

    //Estrellas que marcan la valoracion de la receta
    const stars = document.querySelectorAll(".star");

    //Elemento que contiene el valor de la valoracion media de la receta
    const averageRating = parseFloat(document.getElementById("ratingNumber").textContent);  

    //Para cada estrella se revisa el valor actual de la receta y si el valor de esa estrella es menor o igual se pinta de amarillo, sino se deja gris
    stars.forEach(star => {

        const starValue = parseInt(star.dataset.value);

        //Se pone todas las estrellas grises quitando el valor filled del CSS
        star.classList.remove("filled");

        //Si la estrella cumple las condiciones se le añade el valor Filled para que salga amarilla
        if(averageRating >= starValue){
            star.classList.add("filled");
        }

    });
});