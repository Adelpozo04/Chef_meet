// FUNCIONALIDAD: Simulacion de proceso de pago y reserva
document.addEventListener('DOMContentLoaded', () =>{
    const btnReservar = document.querySelector('.btn-reservar');

    if(btnReservar) {
        // 'e' evento como parametro de la funcion
        btnReservar.addEventListener('click', (e)=> {

            // Bloquear envio automatico del formulario
            e.preventDefault();

            // Leer el precio desde el boton HTML
            const precio = btnReservar.getAttribute('data-price');

            // Simular confirmacion de seguridad al pagar
            const confirmar = confirm(`¿Deseas proceder al pago seguro de ${precio}€?\n\nSe te redirigirá a la pasarela de pago cifrada.`);

            if(confirmar) {
                // simular retraso de procesamiento
                btnReservar.innerText = "Procesando...";
                btnReservar.disabled = true;

                setTimeout(() => {
                    alert("¡Pago realizado con éxito! El evento se ha añadido a tu sección de 'Mis eventos'.")
                
                    // redirigir al perfil
                    // window.location.href = "/account";
                    // Enviar el formulario al servidor
                    btnReservar.closest('form').submit();
                }, 1500);
            }
            // Si el usuario le da a cancelar se queda en la misma pagina sin dar error.
        });
    }
});


// Funcion para cargar el mapa individual del evento
function initMapReserve() {

    const mapDiv = document.getElementById("map-reserve");
    const locationText = document.getElementById("event-location");

    // Parar si no hay mapa o no hay texto de ubicacion
    if (!mapDiv || !locationText) return;

    // Crear mapa centrado en España 
    const map = new google.maps.Map(mapDiv, {
        zoom: 16,
        center: {lat: 40.4168, lng: -3.7038},
        disableDefaultUI: true,                 // quitar botones de google
        zommControl: true                       // dejar botones + y -
    });

    // Herramienta para convertir textos a coordenadas
    const geocoder = new google.maps.Geocoder();

    // Pedir a google las coordenadas del lugar
    geocoder.geocode({ address: locationText.innerText + ", Spain"}, (results, status) => {
                    if (status === "OK") {
                        // Centrar el mapa en la coordenada 
                        map.setCenter(results[0].geometry.location);

                        // Ubicacion marcada en el mapa
                        new google.maps.Marker({
                            map: map,
                            position: results[0].geometry.location
                        });
                    } else {
                        console.warn("Google Maps no ha podido encontrar la ubicacion");
                        mapDiv.innerHTML = "<p style='padding: 10px; text-align:center; color: #666;'>Mapa no disponible para esta ubicación.</p>";
                    }
                });
}