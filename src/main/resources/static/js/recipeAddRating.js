document.addEventListener("DOMContentLoaded", () => {

    //Obtenemos la referencia a las estrellas y al input que vamos a enviar como rating
    const stars = document.querySelectorAll(".star");

    //Obtenemos el elemento que recoge el input del jugador para añadirlo en la información de la receta
    const ratingInput = document.getElementById("rating");

    //Patra cada estrella se añade un metodo que recoge la pulsación sobre ella y el valor que tiene asignado
    stars.forEach(star => {
        
        star.addEventListener("click", (e) => {

            //Sacamos el valor que tiene asignado la estrella escogida
            let value = parseInt(star.dataset.value);

            //Cambio de estilo de las estrellas
            highlight(value);

            //Cambio del valor que sera devuelto al controlador para que se añada a la receta
            ratingInput.value = value;

        });

    });

    //Metodo que actualiza el estilo de las estrellas cambiando a color amarillo aquellas cuyo valor sea menor o igual al de la estrella que se ha pulsado. Todos los estilos se encuentran en CSS haciendo busqueda de star.
    function highlight(value){
        stars.forEach(star => {

            const starValue = parseInt(star.dataset.value);

            //Se quita el valor filled de todas las estrellas
            star.classList.remove("filled");

            //Para luego añadirlo segun el valor actual a las estrellas pertinentes.
            if(value >= starValue){
                star.classList.add("filled");
            }


        });
    };

});