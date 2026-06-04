//Metodo para mostrar una imagen de preview antes de enviar el formulario, este se activa al escoger una imagen en el input, lo que activa un evento change en el documento que almacena la imagen pero que no la cambia para que el usuario vea lo que ha subido.
document.addEventListener("change", function (event){

    //Se busca en el elemento que ha cambiado que haya un elemento que sirva como placeholder de la imagen, lo que indica que todo el formato esta en la vista, sino no se hace nada
    if(!event.target.classList.contains("empty_image_input")){
        return;
    }

    //Se obteniene el elemento de la preview de la imagen
    const preview = event.target.parentElement.parentElement.querySelector(".main_image");

    //Se saca el controlador de la preview que se quiere mostrar
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

//Obtenemos los ingredientes mediante una consulta asincrona a la base de datos, esto es para poder enseñar en el dropdown de ingredientes los ingredientes que hay en la base de datos. Si se añaden ingredientes de forma dinamica sera necesario recargar la pagina para que se actualice la información, pero por diseño nadie puede subir nuevos ingredientes.
let ingredients = [];

async function loadIngredients() {

    const response = await fetch("ingredients");
    ingredients = await response.json();
    
}


//Añadir para los ingredientes
document.addEventListener("DOMContentLoaded", function (){

    //Sacamos el boton de añadir ingrediente
    const button = document.getElementById("addIngredient");

    //sacamos la lista donde se van a ir añadiendo los ingredientes
    const list = document.getElementById("ingredientsList");

    //Contador que marca el numero del ingrediente que se ha añadido, solo para tests
    let counter = 0;

    //Cuando se añada un ingrediente se ejecuta esta funcion asincrona que carga los ingredientes de la base de datos y crea un nuevo elemento en la lista con un dropdown para seleccionar el ingrediente y un cuadro de texto para escribir la cantidad, ambos relacionados mediante inputs ocultos que se envian al controlador de receta, tras lo cual se borra el dropdown y se muestra el ingrediente seleccionado junto a la cantidad escrita.
    button.addEventListener("click", async function() {

        //Hay que esperar hasta que todos los ingredientes se hayan cargado de la base de datos
        await loadIngredients();

        //Creamos un elemento de la lista
        const newIndex = document.createElement("li");

        //Lo añadimos a la lista que sacamos antes
        list.appendChild(newIndex);

        //Creamos el dropdown que sirve para escoger entre los ingredientes
        const dropdown = document.createElement("select");

        //El id es solo para los tests
        dropdown.id = `ingredient-${counter}`;

        //Quitamos la seleccion multiple
        dropdown.multiple = false;

        //asignamos los ingredientes al dropdown
        ingredients.forEach(ing => {
            const option = document.createElement("option")

            option.value = ing.id;
            option.text = ing.name;

            dropdown.append(option);
        })

        //Añadimos el dropdown al nuevo elemento de la lista
        newIndex.append(dropdown);

        //Creamos el texto de la cantidad
        const textAmount = document.createElement("p");
        textAmount.style.backgroundColor = "white";
        textAmount.style.display = "inline-block";
        textAmount.style.padding = "6px 10px";
        
        textAmount.style.border = "1px solid #dee2de";
        textAmount.style.borderRadius = "6px";
        textAmount.style.marginLeft = "10px";

        //Hacemos que dicho elemento pueda ser editable
        textAmount.contentEditable = true;

        //Ponemos un placeholder indicativo para el usuario
        textAmount.dataset.placeholder = "Escribe cantidad...";
        
        //Se añade el atributo editable para que se agregue el texto de placeholder al cuadro de input
        textAmount.classList.add("editable");

        //Cuando se seleccione alguna opcion del dropdown se ejecuta esta funcion que crea los inputs ocultos para enviar la informacion al controlador, muestra el ingrediente seleccionado junto a la cantidad escrita y borra el dropdown para evitar confusiones al usuario, ademas de relacionar el texto de la cantidad con el input oculto para que se envie correctamente al controlador.
        dropdown.addEventListener("change", function() {
            //Si quiero el texto y no el value hay que usar select.options[select.selectedIndex].text;
            const selectedOption = dropdown.options[dropdown.selectedIndex];

            //Nos creamos el input oculto para almacenar el id del ingrediente seleccionado, este se envia al controlador de receta
            const hiddenInput = document.createElement("input");
            hiddenInput.type = "hidden";
            hiddenInput.name = "ingredientIds";
            hiddenInput.value = selectedOption.value;

            //Creamos otro elemento oculto para almacenar en este caso la cantidad escrita por el usuario, de nuevo para enviarlo al controlador de la receta
            const hiddenQuantity = document.createElement("input");
            hiddenQuantity.type = "hidden";
            hiddenQuantity.name = "quantities";

            //Cuando se presione el input de elegir la cantidad se cambia el valor segun lo que haya puesto el usuario
            textAmount.addEventListener("input", function () {
                hiddenQuantity.value = textAmount.textContent.trim();
            });

            newIndex.innerHTML = ""; // limpia

            //Nos creamos un elemento de texto para escribir la opcion elegida por el usuario
            const text = document.createElement("p");
            text.textContent = selectedOption.text;

            //Añadimos todos los elementos tanto de información para el controlador como de vista para el usuario dentro del elemento que nos hemos creado en la lista
            newIndex.appendChild(text);
            newIndex.appendChild(hiddenInput);
            newIndex.appendChild(hiddenQuantity);
            newIndex.appendChild(textAmount);
            
            //Eliminamos el dropdown
            dropdown.remove();

        });

        //Solo para tests
        counter++;

    });
});

//Añadir para los pasos
document.addEventListener("DOMContentLoaded", function (){

    //Contador para marcar la key que tendra cada uno de los pasos, es vital para relacionar los pasos con su imagen correspondiente
    let counter = 0;

    //Se toma el boton de añadir paso
    const button = document.getElementById("addStep");

    //Se toma la lista en la cual se escriben cada uno de los pasos
    const list = document.getElementById("stepsList");

    button.addEventListener("click", function() {
    
        //Agrupacion de los elementos del paso
        const container = document.createElement("div");

        //Creamos el elemento de subida de imagen mediante el template declarado en el html mediante la clonacion de información y el acceso al elemento que nos interesa, en este caso el wrapper de la imagen que contiene toda la info de esta.
        const template = document.getElementById("imageUploadTemplate");
        const clone = template.content.cloneNode(true);
        const wrapper = clone.querySelector(".image-wrapper");

        //Hacemos que el wrapper sea invisible hasta que el usuario quiera añadir una imagen.
        wrapper.querySelector(".empty_image").classList.add("d-none");

        //Le damos un index para relacionarlo con el controllador de receta
        wrapper.querySelector("input").name = "step" + counter;

        //Creamos el elemento de la lista que contendra la información del paso.
        const newElement = document.createElement("li");

        //Creamos el input escrito del 
        const stepDescription = document.createElement("input");

        //Rellenamos la informacion del paso para que pueda ser encontrado por el controlador y manejado para su correcto guardado
        stepDescription.id = `step-${counter}`;
        stepDescription.name = "steps";
        stepDescription.type = "text";

        
        //Configuramos como se ve el texto descriptivo del paso
        stepDescription.classList.add("form-control");

        //Creamos el boton para añadir una imagen
        const buttonImage = document.createElement("button");
        //Estilo de los botones
        buttonImage.classList.add("category_item");
        //Texto del boton
        buttonImage.textContent = "Añadir imagen";
        buttonImage.type = "button";

        //Añadimos los elementos al contenedor de paso
        container.appendChild(wrapper);
        container.appendChild(newElement);
        container.appendChild(buttonImage);

        //Añadimos el texto descriptivo del paso al elemento de la lista
        newElement.appendChild(stepDescription);

        //Lo añadimos todo a las lista de pasos
        list.appendChild(container);
        
        //Hacemos que la escritura se enfoque en el cuadro de texto dentro del elemento hasta que se pulse fuera
        stepDescription.focus();

        //Aumentamos el contador para el siguiente paso
        counter++;

        //Permitimos al usuario la opcion de subir una imagen al quitar la clase que esconde el input de subir imagem
        buttonImage.addEventListener("click", function() {

            wrapper.querySelector(".empty_image").classList.remove("d-none");
        })

    })

})


