document.addEventListener('DOMContentLoaded', ()=> {

    const selector = document.getElementById("referenceType");

    const blocks = {
        0: document.getElementById("users_block"),
        1: document.getElementById("recipes_block"),
        2: document.getElementById("communities_block"),
        3: document.getElementById("events_block")
    };

    selector.addEventListener("change", function () {

        // ocultar todos
        Object.values(blocks).forEach(block => {
            console.log(block);
            block.classList.add("d-none");
        });

        console.log("Valor seleccionado: " + this.value);
        console.log("Valor seleccionado en bloque: " + blocks[this.value]);

        // mostrar seleccionado
        blocks[this.value].classList.remove("d-none");

    });

});