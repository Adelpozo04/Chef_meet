document.addEventListener('DOMContentLoaded', () => {

    const select = document.getElementById('admin-filters-value');
    //const form = document.getElementById('search-form');
    //const button = document.getElementById('search-button');

    // Funcion que crea los inputs segun la opcion elegida
    function updateInputs() {
        /*const oldInputs = form.querySelectorAll('.dynamic-input');
        oldInputs.forEach(input => input.remove());

        let inputs = [];
        switch (select.value) {
            case 'users':
                inputs.push( createInput('number', 'user-id', 'ID usuario') )
                inputs.push( createInput('text', 'user-name', 'Nombre usuario') )
                break;

            case 'recipes':
                inputs.push( createInput('number', 'recipe-id', 'ID receta') )
                inputs.push( createInput('text', 'recipe-title', 'Título') )
                inputs.push( createInput('text', 'recipe-ingredient', 'Ingredientes') )
                inputs.push( createInput('number', 'recipe-creator-id', 'ID creador') )
                inputs.push( createInput('text', 'recipe-creator-name', 'Creador') )
                break;

            case 'communities':
                inputs.push( createInput('number', 'community-id', 'ID comunidad') )
                inputs.push( createInput('text', 'community-name', 'Nombre') )
                inputs.push( createInput('number', 'community-creator-id', 'ID creador') )
                inputs.push( createInput('text', 'community-creator-name', 'Creador') )
                inputs.push( createInput('number', 'community-min', 'Min miembros') )
                inputs.push( createInput('number', 'community-max', 'Max miembros') )
                inputs.push( createInput('date', 'community-date', '') )
                inputs.push( createInput('text', 'community-country', 'País') )
                break;

            case 'events':
                inputs.push( createInput('number', 'event-id', 'ID evento') )
                inputs.push( createInput('text', 'event-title', 'Nombre') )
                inputs.push( createInput('text', 'event-location', 'Localización') )
                inputs.push( createInput('number', 'event-organizer-id', 'ID creador') )
                inputs.push( createInput('text', 'event-organizer-name', 'Creador') )
                inputs.push( createInput('date', 'event-date', '') )
                break;
        }

        inputs.forEach(input => {
            form.insertBefore(input, button)
        });*/

        // Logica para mostrar/ocultar las tablas
        const tableContainers = ['users', 'recipes', 'communities', 'events', 'complaints'];

        // Ocultar todas las tablas
        tableContainers.forEach(containerName => {
            const container = document.getElementById('container-' + containerName);
            if(container) {
                container.classList.add('d-none');
            }
        });

        // Mostrar solo la tabla que coincide con el select actual
        const selectedContainer = document.getElementById('container-' + select.value);
        if(selectedContainer) {
            selectedContainer.classList.remove('d-none');
        }
    }

    // Funcion auxiliar para crear inputs
    /*function createInput(type, name, placeholder) {
        const input = document.createElement('input')
        input.type = type
        input.name = name
        input.placeholder = placeholder
        input.classList.add('form-control', 'me-2', 'dynamic-input')
        return input
    }*/

    // Evento para cuando el usuario cambia el despliegue
    select.addEventListener('change', updateInputs);

    // Ejecutar la funcion una vez al cargar para generar los inputs iniciales
    updateInputs();

})