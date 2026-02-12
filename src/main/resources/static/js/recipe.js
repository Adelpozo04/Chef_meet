
document.addEventListener("DOMContentLoaded", function () {
   
    const input = document.getElementById('imageRecipe');
    const preview = document.getElementById('imageRecipePreview');
    const controller = document.getElementById('imageRecipeController');

    input.addEventListener("change", function (event){

        const file = event.target.files[0];

        if(!file) return;

        if(!file.type.startsWith("image/")){
            alert("Selecciona una imagen valida");
            input.value = "";
            return;
        }

        const reader = new FileReader();

        reader.onload = function(e){
            preview.src = e.target.result;
            preview.classList.remove("d-none");
            controller.classList.add("d-none");
        }

        reader.readAsDataURL(file);

    });

});