// FUNCIONALIDAD: Filtrado dinamico de eventos 
document.addEventListener('DOMContentLoaded', ()=>{

    const categories = document.querySelectorAll('.category_item');
    const events = document.querySelectorAll('.card_item');
    const searchInput = document.querySelector('input[type="search"]');

    // Busqueda de eventos en tiempo real en la barra de busqueda
    searchInput.addEventListener('input', (e) =>{
        // Obtener lo escrito por el usario, en minusculas y sin espacios
        const searchEvent = e.target.value.toLowerCase().trim();

        events.forEach(event => {
            // Leer los datos ocultos inyectados en cada tarjeta en el html (titulo y tematica)
            const title = event.getAttribute('data-title').toLowerCase();
            const theme = event.getAttribute('data-theme').toLowerCase();

            // Comprobar que el titulo o la tematica contienen lo buscado
            if (title.includes(searchEvent) || theme.includes(searchEvent)) {
                // Mostrar evento
                event.style.display = 'flex';

                // Animacion transicion
                setTimeout(() => {
                    event.style.opacity = '1'; 
                    event.style.transform = 'scale(1)';
                }, 10);
            } else {
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


// fetch para peticiones asincronas AJAX para pedir los eventos al endpoint y pintarlos en el mapa
function initMap() {
    const mapDiv = document.getElementById("map");
    // Crear mapa centrado en España con un zoom alejado
    const map = new google.maps.Map(mapDiv, {
        zoom: 6,
        center: {lat: 40.463667, lng: -3.74922}, // Centro de la peninsula
    });

    // Herramienta para convertir textos a coordenadas
    const geocoder = new google.maps.Geocoder();

    // Pedir los eventos al servidor java usando fetch
    fetch('/event/api/all')
        .then(response => response.json())
        .then(events => {

            // Comprobar si events es una lista
            if (!Array.isArray(events)) {
                console.error("Error: Java no ha devuelto una lista.")
                return;
            }
            // recorrer cada evento que devuelve el servidor
            events.forEach(ev => {
                // Pedir a Google que busque las coordenadas de ese texto
                geocoder.geocode({ address: ev.location + ", Spain"}, (results, status) => {
                    if (status === "OK") {
                        // Ubicacion marcada en el mapa
                        new google.maps.Marker({
                            map: map,
                            position: results[0].geometry.location,
                            title: ev.title // para que al pasar el raton por encima se vea el nombre del evento
                        });
                    } else {
                        console.warn("Google Maps no ha podido encontrar la ubicacion de:", ev.title);
                    }
                });
            });
        })
        .catch(error => console.error("Error cargando los eventos para el mapa:", error));
}

