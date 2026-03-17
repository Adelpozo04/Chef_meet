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