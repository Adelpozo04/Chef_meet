document.addEventListener('DOMContentLoaded', ()=> {

    const selector = document.getElementById("referenceType");

    const selectedType = selector.dataset.selected;

    console.log("Valor seleccionado al cargar: " + selectedType);

    const blocks = {
        0: document.getElementById("users_block"),
        1: document.getElementById("recipes_block"),
        2: document.getElementById("communities_block"),
        3: document.getElementById("events_block")
    };

    if(selectedType && blocks[selectedType]) {
        // mostrar seleccionado
        blocks[selectedType].classList.remove("d-none");
    }

});