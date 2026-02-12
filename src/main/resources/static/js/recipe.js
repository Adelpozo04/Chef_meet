

//Metodo para mostrar una imagen de preview antes de enviar el formulario
document.addEventListener("DOMContentLoaded", function () {
   
    //Tomamos los elementos que componen la imagen que son el input, la img de preview y el controlador para subir la imagem
    const input = document.getElementById('imageRecipe');
    const preview = document.getElementById('imageRecipePreview');
    const controller = document.getElementById('imageRecipeController');

    //Tras esto escuchamos el evento change que se da cuando se sube un archivo a la web
    input.addEventListener("change", function (event){

        //Nos guardamos el archivo subido
        const file = event.target.files[0];

        //Si no hay archivo no se hace nada
        if(!file) return;

        //Si el archivo no es una imagen no se hace nada y se avisa del error al usuario
        if(!file.type.startsWith("image/")){
            alert("Selecciona una imagen valida");
            input.value = "";
            return;
        }

        //Nos creamos un lector de archivos
        const reader = new FileReader();

        //Mediante este leemos el archivo antes guardado y lo ponemos como src de la imagen preview, tras lo cual se hace visible
        //Tambien se esconde el controlador anteriormente usado
        reader.onload = function(e){
            preview.src = e.target.result;
            preview.classList.remove("d-none");
            controller.classList.add("d-none");
        }

        //Convertimos el archivo a una ruta leible por la web.
        reader.readAsDataURL(file);

    });

});

document.addEventListener("DOMContentLoaded", function (){

    const button = document.getElementById("addIngredient");
    const list = document.getElementById("ingredientsList");

    button.addEventListener("click", function() {

        //Creamos un elemento de la lista
        const newElement = document.createElement("li");

        //Hacemos que dicho elemento pueda ser editable con un texto
        newElement.contentEditable = true;

        newElement.dataset.placeholder = "Escribe un ingrediente...";

        newElement.classList.add("editable");

        //Impedimos al usuario escribir en varias lineas con Enter
        newElement.addEventListener("keydown", function(e) {
            if (e.key === "Enter") {
                e.preventDefault();
                newElement.blur();
            }
        });

        list.appendChild(newElement);
        
        newElement.focus();

        //Hacemos que si el usuario hace click fuera del elemeto y dicho elemento no tiene nada puesto se elimine automaticamente
        newElement.addEventListener("blur", function () {

            if (newElement.textContent.trim() === "") {
                newElement.remove();
            }

        });

    })

})

document.addEventListener("DOMContentLoaded", function (){

    const button = document.getElementById("addStep");
    const list = document.getElementById("stepsList");

    button.addEventListener("click", function() {

        //Creamos un elemento de la lista
        const newElement = document.createElement("li");

        //Hacemos que dicho elemento pueda ser editable con un texto
        newElement.contentEditable = true;

        newElement.dataset.placeholder = "Escribe siguiente paso...";

        newElement.classList.add("editable");

        list.appendChild(newElement);
        
        newElement.focus();

        //Hacemos que si el usuario hace click fuera del elemeto y dicho elemento no tiene nada puesto se elimine automaticamente
        newElement.addEventListener("blur", function () {

            if (newElement.textContent.trim() === "") {
                newElement.remove();
            }

        });

    })

})

document.addEventListener("DOMContentLoaded", function (){
    const elements = document.querySelectorAll(".recipe_title.editable");

    elements.forEach(function(element){
        element.dataset.placeholder = "Escribe un titulo..."
    })
})

