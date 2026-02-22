// FUNCIONALIDAD: Simulacion de proceso de pago y reserva
document.addEventListener('DOMContentLoaded', () =>{
    const btnReservar = document.querySelector('.btn-reservar');

    if(btnReservar) {
        btnReservar.addEventListener('click', ()=> {
            // simular confirmacion de seguridad al pagar
            const confirmar = confirm("¿Deseas proceder al pago seguro de 5,00€\n\nSe te redirigirá a la pasarela de pago cifrada.");

            if(confirmar) {
                // simular retraso de procesamiento
                btnReservar.innerText = "Procesando...";
                btnReservar.disabled = true;

                setTimeout(() => {
                    alert("¡Pago realizado con éxito! El evento se ha añadido a tu sección de 'Mis eventos'.")
                
                    // redirigir al perfil
                    window.location.href = "/account";
                }, 1500);
            }
        });
    }
});