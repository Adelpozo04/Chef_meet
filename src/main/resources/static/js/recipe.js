//Metodo para mostrar una imagen de preview antes de enviar el formulario
document.addEventListener("change", function (event){

    if(!event.target.classList.contains("empty_image_input")){
        return;
    }

    const preview = event.target.parentElement.parentElement.querySelector(".main_image");
    const controller = event.target.parentElement;

    //Nos guardamos el archivo subido
    const file = event.target.files[0];

    //Si no hay archivo no se hace nada
    if(!file) return;

            //Si el archivo no es una imagen no se hace nada y se avisa del error al usuario
            if(!file.type.startsWith("image/")){
                alert("Selecciona una imagen valida"); // mejor nos quejamos debajo de la imagen
                input.value = "";
                return;
            } else {
                // si habia mensaje feo, lo quitamos aqui
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

//Obtenemos los ingredientes mediante una consulta asincrona a la base de datos
let ingredients = [];

async function loadIngredients() {

    const response = await fetch("ingredients");
    ingredients = await response.json();
    
}


//Anyadir para los ingredientes
document.addEventListener("DOMContentLoaded", function (){

    const button = document.getElementById("addIngredient");
    const list = document.getElementById("ingredientsList");

    let counter = 0;

    button.addEventListener("click", async function() {

        await loadIngredients();

        //Creamos un elemento de la lista
        const newIndex = document.createElement("li");

        list.appendChild(newIndex);

        //Creamos un elemento de la lista
        const dropdown = document.createElement("select");

        dropdown.multiple = false;

        console.log(ingredients);
        console.log(typeof ingredients);

        //asignamos los ingredientes al dropdown
        ingredients.forEach(ing => {
            const option = document.createElement("option")

            option.value = ing.id;
            option.text = ing.name;

            dropdown.append(option);
        })

        //Anyadimos el dropdown al nuevo elemento de la lista
        newIndex.append(dropdown);

        //Creamos el texto de la cantidad
        const textAmount = document.createElement("p");
        textAmount.style.backgroundColor = "white";
        textAmount.style.display = "inline-block";

        //Hacemos que dicho elemento pueda ser editable con un texto
        textAmount.contentEditable = true;

        textAmount.dataset.placeholder = "Escribe cantidad...";

        textAmount.classList.add("editable");


        dropdown.addEventListener("change", function() {
            //Si quiero el texto y no el value hay que usar select.options[select.selectedIndex].text;
            const selectedOption = dropdown.options[dropdown.selectedIndex];

            const hiddenInput = document.createElement("input");
            hiddenInput.type = "hidden";
            hiddenInput.name = "ingredientIds";
            hiddenInput.value = selectedOption.value;

            const hiddenQuantity = document.createElement("input");
            hiddenQuantity.type = "hidden";
            hiddenQuantity.name = "quantities";

            textAmount.addEventListener("input", function () {
                hiddenQuantity.value = textAmount.textContent.trim();
            });

            newIndex.innerHTML = ""; // limpia

            const text = document.createElement("p");
            text.textContent = selectedOption.text;

            newIndex.appendChild(text);
            newIndex.appendChild(hiddenInput);
            newIndex.appendChild(hiddenQuantity);
            newIndex.appendChild(textAmount);
            
            dropdown.remove();

        });

    });
});

//Anyadir para los pasos
document.addEventListener("DOMContentLoaded", function (){

    //Contador para marcar la key que tendra cada uno de los pasos
    let counter = 0;

    const button = document.getElementById("addStep");
    const list = document.getElementById("stepsList");

    button.addEventListener("click", function() {
    
        //Agrupacion de los elementos del paso
        const container = document.createElement("div");

        //Creamos el elemento de subida de imagen mediante el template declarado en el html
        const template = document.getElementById("imageUploadTemplate");
        const clone = template.content.cloneNode(true);
        const wrapper = clone.querySelector(".image-wrapper");

        //Que sea invisible
        wrapper.querySelector(".empty_image").classList.add("d-none");
        //Le damos un index para relacionarlo con el controllador de receta
        wrapper.querySelector("input").name = "step" + counter;

        //Creamos el cuadro de texto para el paso a seguir
        const newElement = document.createElement("li");
        const stepDescription = document.createElement("input");

        stepDescription.name = "steps";
        stepDescription.type = "text";

        stepDescription.classList.add("title_m");
        stepDescription.classList.add("border-0");
        stepDescription.classList.add("text-left");

        //Escribimos un texto como placeholder
        stepDescription.dataset.placeholder = "Escribe el paso a seguir...";

        //Creamos el boton para añadir una imagen
        const buttonImage = document.createElement("button");
        buttonImage.classList.add("category_item");
        buttonImage.textContent = "Añadir imagen";
        buttonImage.type = "button";

        //Anyadimos los elementos al contenedor de paso
        container.appendChild(wrapper);
        container.appendChild(newElement);
        container.appendChild(buttonImage);
        newElement.appendChild(stepDescription);

        //Lo anyadimos todo a las lista de pasos
        list.appendChild(container);
        
        newElement.focus();

        //Aumentamos el contador para el siguiente paso
        counter++;

        //Permitimos al usuario la opcion de subir una imagen al quitar la clase que esconde el input de subir imagem
        buttonImage.addEventListener("click", function() {

            wrapper.querySelector(".empty_image").classList.remove("d-none");
        })

    })

})

//Colocacion de texto por defecto en los apartados editables
document.addEventListener("DOMContentLoaded", function (){

    //Texto de titulo
    const elementsTitle = document.querySelectorAll(".title_xxl.editable");

    elementsTitle.forEach(function(element){
        element.dataset.placeholder = "Escribe un titulo..."
    })

    //Texto de tiempo
    const elementsTime = document.querySelectorAll(".title_m.editable");

    elementsTime.forEach(function(element){
        if(element.id === "difficulty"){
            element.dataset.placeholder = "Escribe nivel de dificultad...";
        }
        else if(element.id === "time"){
            element.dataset.placeholder = "Escribe tiempo de preparacion...";
        }

    })


})

