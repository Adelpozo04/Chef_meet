document.addEventListener('DOMContentLoaded', ()=> {

    // Selector del tipo de queja: usuario, receta, comunidad o evento
    const selector = document.getElementById("referenceType");

    // Formulario de creacion de quejas
    const form = document.querySelector("form");

    // Boton de enviar queja
    const submitButton = document.getElementById("create-button");

    // Relaciona cada valor del select con el bloque de tarjetas correspondiente
    const blocks = {
        0: document.getElementById("users_block"),
        1: document.getElementById("recipes_block"),
        2: document.getElementById("communities_block"),
        3: document.getElementById("events_block")
    };

    // Oculta todos los bloques de tarjetas y limpia la seleccion anterior
    function hideAllBlocks() {
        Object.values(blocks).forEach(block => {
            if (!block) return;

            // Ocultar el bloque
            block.classList.add("d-none");

            // Desmarcar, desactivar y quitar obligatoriedad a los radios del bloque oculto
            block.querySelectorAll("input[type='radio']").forEach(radio => {
                radio.checked = false;
                radio.required = false;
                radio.disabled = true;
            });

            // Quitar el estilo de tarjeta seleccionada
            block.querySelectorAll(".card_item_complaint").forEach(card => {
                card.classList.remove("selected_complaint_card");
            });
        });
    }

    // Muestra solo el bloque correspondiente al tipo de queja elegido
    function showSelectedBlock(type) {
        const selectedBlock = blocks[type];

        if (!selectedBlock) {
            return;
        }

        // Mostrar el bloque elegido
        selectedBlock.classList.remove("d-none");

        // Activar sus radios y hacer obligatoria la seleccion de una tarjeta
        selectedBlock.querySelectorAll("input[type='radio']").forEach(radio => {
            radio.disabled = false;
            radio.required = true;
        });
    }

    // Al cargar la pagina, todos los bloques empiezan ocultos
    hideAllBlocks();

    // Cuando cambia el tipo de queja, se ocultan los bloques anteriores
    // y se muestra unicamente el bloque del tipo seleccionado
    selector.addEventListener("change", function () {
        hideAllBlocks();
        showSelectedBlock(this.value);
    });

    // Permite seleccionar una tarjeta concreta dentro del bloque visible
    document.querySelectorAll(".card_item_complaint").forEach(card => {
        card.addEventListener("click", function () {
            const block = this.closest(".reference_block");

            // Quitar la clase de seleccionada a todas las tarjetas del mismo bloque
            block.querySelectorAll(".card_item_complaint").forEach(otherCard => {
                otherCard.classList.remove("selected_complaint_card");
            });

            // Marcar visualmente la tarjeta pulsada
            // El estilo concreto se define en CSS
            this.classList.add("selected_complaint_card");

            // Marcar el radio asociado a esa tarjeta
            const radio = this.querySelector("input[type='radio']");
            if (radio) {
                radio.checked = true;
            }
        });
    });

    // Antes de enviar el formulario se comprueba que haya tipo y elemento seleccionado
    form.addEventListener("submit", function (e) {
        const selectedType = selector.value;

        // Si no se ha elegido tipo de queja, no se envía
        if (!selectedType) {
            e.preventDefault();
            alert("Selecciona el tipo de queja.");
            return;
        }

        const selectedBlock = blocks[selectedType];
        const selectedReference = selectedBlock.querySelector("input[type='radio']:checked");

        // Si no se ha elegido ninguna tarjeta concreta, no se envía
        if (!selectedReference) {
            e.preventDefault();
            alert("Selecciona el elemento concreto que quieres denunciar.");
        }
    });

});