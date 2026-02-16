// FUNCIONALIDAD: Cambio de pestañas de opciones en el perfil
document.addEventListener('DOMContentLoaded', () => {
    const options = document.querySelectorAll('.option_item');
    const contents = document.querySelectorAll('.view_content');

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
            }
        });
    });
});