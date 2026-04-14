document.addEventListener("DOMContentLoaded", () => {

    //Obtenemos la referencia a las estrellas y al input que vamos a enviar como rating
    const stars = document.querySelectorAll(".star");
    const ratingInput = document.getElementById("rating");

    stars.forEach(star => {
        
        star.addEventListener("click", (e) => {

            //Sacamos el valor que tiene asignado la estrella escogida
            let value = parseInt(star.dataset.value);

            highlight(value);
            ratingInput.value = value;

        });

    });

    function highlight(value){
        stars.forEach(star => {

            const starValue = parseInt(star.dataset.value);

            star.classList.remove("filled");

            if(value >= starValue){
                star.classList.add("filled");
            }


        });
    };

});