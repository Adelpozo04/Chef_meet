// FUNCIONALIDAD: Validacion del tamaño de una imagen en el formulario de creacion de un evento
// Si supera el tamaño, avisa al usuario al instante
document.addEventListener("DOMContentLoaded", function(){
    const photoInput = document.getElementById("photo");

    if (photoInput) {
        photoInput.addEventListener("change", function() {
            // Comprobar si hay un archivo seleccionado
            // this.files es la lista ocult que guarda el navegador con todos los archivos que ha seleccionado el usuario
            if (this.files && this.files[0]) {
                const file = this.files[0];
                // Limite de 5 MB
                const maxBytes = 5 * 1024 * 1024;

                // Validar el tamaño
                if (file.size > maxBytes) {
                    alert("¡Archivo demasiado pesado! La imagen no puede superar los 5 MB.");
                    // Vaciar el input para eviter que se envie el formulario
                    this.value = "";
                }

                // Validar que es realmente una imagen
                if(!file.type.startsWith("image/")) {
                    alert("Por favor, selecciona un archivo de imagen válido (JPG, PNG).");
                    this.value = "";
                }
    
            }
        });
    }
});