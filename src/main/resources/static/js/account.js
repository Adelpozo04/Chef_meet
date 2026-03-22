// FUNCIONALIDAD: Cambio de pestañas de opciones en el perfil y apertura de estas por URL
document.addEventListener('DOMContentLoaded', () => {
    const options = document.querySelectorAll('.option_item');
    const contents = document.querySelectorAll('.view_content');

    // Comportamiento de los clics
    options.forEach(option => {
        option.addEventListener('click', (e) => {
            e.preventDefault(); // Evitar que el enlace recargue la pagina

            // Cambiar boton activo
            options.forEach(item => item.classList.remove('ct_item_active'));
            option.classList.add('ct_item_active');

            // Ocultar todas las vistas
            contents.forEach(content => content.classList.add('d-none'));

            // Mostrar solo la vista que coincida con la categoria del boton
            const targetId = option.getAttribute('option');
            const targetView = document.getElementById(targetId);

            if(targetView) {
                targetView.classList.remove('d-none');

                history.pushState(null, '', '?tab=' + targetId);
            }
        });
    });

    // Abrir una pestaña especifica si viene en la URL
    // Leer los parametros de la URL
    const params = new URLSearchParams(window.location.search);
    const tabName = params.get('tab');

    if(tabName) {
        // Si existe, busca el boton de esta pestaña en el menu lateral y hace clic en el
        const targetTab = document.querySelector(`.option_item[option="${tabName}"]`);
        if(targetTab) {
            targetTab.click();
        }
    }
});