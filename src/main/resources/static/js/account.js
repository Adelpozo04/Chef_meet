// FUNCIONALIDAD: Cambio de pestañas de opciones en el perfil y apertura de estas por URL
document.addEventListener('DOMContentLoaded', () => {
    // Inicializa el sistema de pestañas
    initTabs();

    // Inicializa el boton de cerrar sesion
    initLogout();

    // Inicializa la subida de foto de perfil
    initProfilePhoto();
});



// Funciones

function initTabs() {
    const options = document.querySelectorAll('.option_item');
    const contents = document.querySelectorAll('.view_content');

    // Comportamiento de los clics
    options.forEach(option => {
        option.addEventListener('click', (e) => {

            // Detectar si el boton pulsado es el de cerrar sesion
            if(option.id === 'logoutButton') {
                return; 
            }

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
}

function initLogout() {
    const logoutButton = document.getElementById('logoutButton');
    const logoutForm = document.getElementById('logoutForm');

    if (logoutButton && logoutForm) {
        logoutButton.addEventListener('click', (e) => {
            e.preventDefault();
            logoutForm.submit();
        });
    }
}

function initProfilePhoto() {
    // Cambio de foto de perfil (AJAX)
    const fileInput = document.getElementById('f_avatar');
    const avatarImg = document.getElementById('profile-avatar');
    const saveButton = document.getElementById('postAvatar');

    if (fileInput && avatarImg && saveButton) {
        // El usuario selecciona una imagen
        fileInput.addEventListener('change', () => {
            if (fileInput.files && fileInput.files[0]) {
                // Funcion de iw.js para previsualizar la imagen al momento
                readImageFileData(fileInput.files[0], avatarImg);

                // Mostrar boton de confirmacion
                saveButton.classList.remove('d-none');
            }
        });

        // El usuario confirma guardar
        saveButton.addEventListener('click', () => {
            const endpoint = saveButton.getAttribute('data-url');
            const file = fileInput.files[0];

            saveButton.innerText = "Guardando...";
            saveButton.disabled = true;

            // Funcion de iw.js que hace la peticion POST 
            postImage(avatarImg, endpoint, 'photo', file.name)
                .then(response => {
                    // Ocultar boton y restaurar estado
                    saveButton.classList.add('d-none');
                    saveButton.innerText = "Guardar foto";
                    saveButton.disabled = false;

                    // Actualizar la imagen tambien en la barra de navegacion
                    const navAvatar = document.getElementById('nav-avatar');
                    if (navAvatar) {
                        navAvatar.src = avatarImg.src; // Copiar el base64 que la funcion readImageFileData ya habia generado para la vista previa
                    }
                    // Notificacion
                    if(typeof showNotification === "function") {
                        showNotification("¡Foto de perfil actualizada con éxito!");
                    }
                })
                .catch(error => {
                    console.error("Error al subir foto:", error);
                    alert("Error al actualizar la foto. Inténtalo de nuevo.");
                    saveButton.innerText = "Guardar foto";
                    saveButton.disabled = false;
                });
        });
    }
}