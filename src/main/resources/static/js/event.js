// FUNCIONALIDAD: Filtrado dinamico de eventos 
document.addEventListener('DOMContentLoaded', ()=>{

    const categories = document.querySelectorAll('.category_item');
    const events = document.querySelectorAll('.card_item');
    const searchInput = document.querySelector('input[type="search"]');

    categories.forEach(category => {
        category.addEventListener('click', (e) => {

            // Si el enlace no es '#', deja que el navegador vaya a la pagina normal y corta la ejecucion del filtro.
            if (category.getAttribute('href') && category.getAttribute('href') !== '#') {
                return;
            }
            e.preventDefault(); // Evita recarga de pagina

            // Actualizar estilo visual de los botones dependiendo del filtro seleccionado
            categories.forEach(item => item.classList.remove('ct_item_active'));
            category.classList.add('ct_item_active');

            const selectedCategory = category.getAttribute('category');

            // Cambio dinamico del texto del buscador segun el filtro
            searchInput.placeholder = selectedCategory === 'all'
                ? "Buscar eventos..."
                : "Buscar mis eventos...";

            // Logica de filtrado de eventos
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

                    // Esperar a que termine la transicion antes de ocultar del todo
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
    const deleteButtons = document.querySelectorAll('.btn_delete');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Confirmacion de seguridad
            const confirmation = confirm("¿Estás seguro de que quieres eliminar este evento?. Acción irreversible.");

            if(confirmation) {
                const eventItem = this.closest('.card_item'); // Selecciona el contenedor padre del evento
                const form = this.closest('form');            // Selecciona el formulario
                

                // Mandar peticion al servidor antes de borrarlo visualmente
                // Se usa fetch para enviar el post en segundo plano sin recargar la pagina
                fetch(form.action, {
                    method: 'POST',
                    body: new FormData(form) // Recoge el id del evento y el token de seguridad CRSF de Thymeleaf
                })
                .then(response => {
                    if(response.ok) {
                        // El servidor confima que ha borrado el evento de la base de datos
                        // Animacion transicion
                        eventItem.style.opacity = '0';
                        eventItem.style.transform = 'scale(0.3)';

                        // Eliminacion definitiva del elemento del DOM tras la animacion
                        setTimeout(() => {
                            eventItem.remove();
                        }, 400);
                    } else {
                        alert("Hubo un error al borrar el evento en el servidor.")
                    }
                })
                .catch(error => console.error("Error de red:", error));
            }
        });
    });
});