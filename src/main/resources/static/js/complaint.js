//Metodo enfocado en cambiar las tarjetas que se muestren segun la selección hecha en el dropdown de la pestaña de quejas
document.addEventListener('DOMContentLoaded', ()=> {

    //Se toma el selector
    const selector = document.getElementById("referenceType");

    //Se toma el valor elegido por el usuario
    const selectedType = selector.dataset.selected;

    //Se establecen los bloques de cada tipo de queja
    const blocks = {
        0: document.getElementById("users_block"),
        1: document.getElementById("recipes_block"),
        2: document.getElementById("communities_block"),
        3: document.getElementById("events_block")
    };

    //Se cambia segun el valor elegido por el usuario
    if(selectedType && blocks[selectedType]) {
        // mostrar seleccionado
        blocks[selectedType].classList.remove("d-none");
    }

});