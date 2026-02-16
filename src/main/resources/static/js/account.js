// FUNCIONALIDAD: Cambio de pestañas de opciones en el perfil
document.addEventListener('DOMContentLoaded', () => {
    const options = document.querySelectorAll('.option_item');
    const views = document.querySelectorAll('.view_content');

    options.forEach(option => {
        option.addEventListener('click', (e) => {
            e.preventDefault(); // Evitar que el enlace recargue la pagina

            // Cambiar boton activo
            options.forEach(item => item.classList.remove('ct_item_active'));
            option.classList.add('ct_item_active');

            // Obtener opcion seleccionada
            const selectedOption = option.getAttribute('option');

            // Mostrar/ocultar el contenido correspondiente
            views.forEach(view => {
                if(view.id === selectedOption) {
                    view.classList.remove('d-none');
                }
                else {
                    view.classList.add('d-none');
                }
            });
        });
    });
});