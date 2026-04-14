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