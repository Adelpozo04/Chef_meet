// FUNCIONALIDAD: Filtrar eventos
// Asegurar que el script se ejecute cuando se haya cargado el HTML
document.addEventListener('DOMContentLoaded', ()=>{

    const categories = document.querySelectorAll('.category_item');
    const events = document.querySelectorAll('.event_item');
    const searchInput = document.querySelector('input[type="search"]');

    categories.forEach(category => {
        category.addEventListener('click', (e) => {
            e.preventDefault(); // Evita que la pagina salte al hacer click en el enlace

            // Gestionar estado activo de los botones
            // Recorrer los botones para poner de color oscuro solo al seleccionado
            categories.forEach(item => item.classList.remove('ct_item_active'));
            category.classList.add('ct_item_active');

            // Filtrado de eventos
            const selectedCategory = category.getAttribute('category');

            // Cambio dinamico del texto del buscador
            if (selectedCategory === 'all') {
                searchInput.placeholder = "Buscar eventos...";
            }
            else if (selectedCategory === 'own') {
                searchInput.placeholder = "Buscar mis eventos...";
            }

            events.forEach(event => {
                const eventCategory = event.getAttribute('category');

                // Si el filtro es todos, se muestran totos los eventos
                if(selectedCategory === 'all' || selectedCategory === eventCategory) {

                    // Mostrar evento
                    event.style.display = 'flex';

                    // Animacion transicion
                    setTimeout(() => {
                        event.style.opacity = '1'; 
                        event.style.transform = 'scale(1)';
                    }, 10);
                }

                else {
                    // Ocultar evento
                    event.style.opacity = '0';
                    event.style.transform = 'scale(0.7)'

                    // Esperar a que termine la animacion antes de ocultar del todo
                    setTimeout(() => {
                        if(event.style.opacity === '0') {
                            event.style.display = 'none';
                        }
                       
                    }, 400);
                }
            });
        });
    });
});


// FUNCIONALIDAD: Borrar eventos (solo Admin)
document.addEventListener('DOMContentLoaded', ()=> {
    // Buscar todos los eventos de borrar que Thymeleaf haya renderizado
    const deleteButtons = document.querySelectorAll('.btn-delete-event');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            const confirmation = confirm("¿Estás seguro de que quieres eliminar este evento?. Acción irreversible.");

            if(confirmation) {
                const eventItem = this.closest('.event_item');

                eventItem.style.opacity = '0';
                eventItem.style.transform = 'scale(0.3)';

                setTimeout(() => {
                    eventItem.remove();
                }, 400);

                /* En un futuro se debera llamar al servidor para borrarlo de la base de datos*/
            }
        });
    });
});