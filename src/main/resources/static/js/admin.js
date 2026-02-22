document.addEventListener('DOMContentLoaded', () => {

    const select = document.getElementById('admin-filters-value');
    const form = document.getElementById('search-form');
    const button = document.getElementById('search-button');

    defaultValue(form, button)
    select.addEventListener('change', function () {

        const oldInputs = form.querySelectorAll('.dynamic-input');
        oldInputs.forEach(input => input.remove());

        let inputs = [];
        switch (this.value) {
            case 'users':
                inputs.push( createInput('number', 'user-id', 'ID de usuario') )
                inputs.push( createInput('text', 'user-name', 'Nombre de usuario') )
                inputs.push( createInput('email', 'user-email', 'Correo de usuario') )
                break;

            case 'recipes':
                inputs.push( createInput('number', 'recipe-id', 'ID de receta') )
                inputs.push( createInput('text', 'recipe-title', 'Título receta') )
                inputs.push( createInput('text', 'recipe-ingredient', 'Ingredientes') )
                inputs.push( createInput('number', 'recipe-creator-id', 'ID del creador') )
                inputs.push( createInput('text', 'recipe-creator-name', 'Nombre del creador') )
                break;

            case 'communities':
                inputs.push( createInput('number', 'community-id', 'ID de comunidad') )
                inputs.push( createInput('text', 'community-name', 'Nombre comunidad') )
                inputs.push( createInput('number', 'community-creator-id', 'ID del creador') )
                inputs.push( createInput('text', 'community-creator-name', 'Nombre del creador') )
                inputs.push( createInput('number', 'community-min', 'Min miembros') )
                inputs.push( createInput('number', 'community-max', 'Max miembros') )
                inputs.push( createInput('date', 'community-date', '') )
                inputs.push( createInput('text', 'community-country', 'Pais de comunidad') )
                break;

            case 'events':
                inputs.push( createInput('number', 'event-id', 'ID de evento') )
                inputs.push( createInput('text', 'event-name', 'Nombre del evento') )
                inputs.push( createInput('text', 'event-country', 'Pais del evento') )
                inputs.push( createInput('date', 'event-date', '') )
                break;
        }

        inputs.forEach(input => {
            form.insertBefore(input, button)
        });
    });

    function defaultValue(form, button){
        form.insertBefore(createInput('number', 'user-id', 'ID de usuario'), button)
        form.insertBefore(createInput('text', 'user-name', 'Nombre de usuario'), button)
        form.insertBefore(createInput('email', 'user-email', 'Correo de usuario'), button)
    }

    function createInput(type, name, placeholder) {
        const input = document.createElement('input')
        input.type = type
        input.name = name
        input.placeholder = placeholder
        input.classList.add('form-control', 'me-2', 'dynamic-input')
        return input
    }

})