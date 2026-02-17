

//Metodo para mostrar una imagen de preview antes de enviar el formulario
document.addEventListener("change", function (event){

    if(!event.target.classList.contains("recipe_empty_image_input")){
        return;
    }

    const preview = event.target.parentElement.parentElement.querySelector(".recipe_main_image");
    const controller = event.target.parentElement;

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




//Anyadir para los ingredientes
document.addEventListener("DOMContentLoaded", function (){

    const button = document.getElementById("addIngredient");
    const list = document.getElementById("ingredientsList");

    let counter = 0;

    button.addEventListener("click", function() {

        //Creamos un elemento de la lista
        const newIndex = document.createElement("li");

        //Creamos un elemento de la lista
        const newElement = document.createElement("select");

        newElement.multiple = true;

        list.appendChild(newIndex);

        newIndex.append(newElement);

        //creamos los ingredientes del dropdown
        for(let i = 0; i < 3; i++){
            const option = document.createElement("option")
            
            option.value = "Ingrediente: " + counter;
            option.text = "Ingrediente: " + counter;

            newElement.append(option);

            counter++;
        }

        newElement.addEventListener("change", function() {
            //Si quiero el texto y no el value hay que usar select.options[select.selectedIndex].text;
            newIndex.textContent = newElement.value;
            newElement.remove();
        });

    })

})

//Anyadir para los pasos
document.addEventListener("DOMContentLoaded", function (){

    const button = document.getElementById("addStep");
    const list = document.getElementById("stepsList");

    button.addEventListener("click", function() {

        const container = document.createElement("div");

        //
        const template = document.getElementById("imageUploadTemplate");
        const clone = template.content.cloneNode(true);

        const form = clone.querySelector("form");

        form.querySelector(".recipe_empty_image").classList.add("d-none");

        //Creamos un elemento de la lista
        const newElement = document.createElement("li");

        const buttonImage = document.createElement("button");

        buttonImage.textContent = "Añadir imagen";

        buttonImage.classList.add("d-none");

        //Hacemos que dicho elemento pueda ser editable con un texto
        newElement.contentEditable = true;

        newElement.dataset.placeholder = "Escribe siguiente paso...";

        newElement.classList.add("editable");

        //Anyadimos los elementos al contenedor de paso
        container.appendChild(form);
        container.appendChild(newElement);
        container.appendChild(buttonImage);

        //Lo anyadimos todo a las lista de pasos
        list.appendChild(container);
        
        newElement.focus();

        //Hacemos que si el usuario hace click fuera del elemeto y dicho elemento no tiene nada puesto se elimine automaticamente
        newElement.addEventListener("blur", function () {

            if (newElement.textContent.trim() === "") {
                container.remove();
            }
            else{
                buttonImage.classList.remove("d-none");
            }

        });

        //Permitimos al usuario la opcion de subir una imagen.
        buttonImage.addEventListener("click", function() {

            form.querySelector(".recipe_empty_image").classList.remove("d-none");
        })

    })

})

document.addEventListener("DOMContentLoaded", function (){
    const elements = document.querySelectorAll(".recipe_title.editable");

    elements.forEach(function(element){
        element.dataset.placeholder = "Escribe un titulo..."
    })
})

