"use strict"

/**
 * WebSocket API, which only works once initialized
 */
const ws = {

    /**
     * Number of retries if connection fails
     */
    retries: 3,

    headers: { 'X-CSRF-TOKEN': config.csrf.value },

    /**
     * Default action when message is received. 
     */
    receive: (msg) => {

        // Si se recibe un string (JSON), se convierte a objeto
        const data = (typeof msg === 'string') ? JSON.parse(msg) : msg;
        console.log("Mensaje WS recibido: ", msg);

        // Comprobar si el mensaje es la notificacion de evento
        if(data.type === 'EVENT_JOIN') {
            // Mostrar notificacion por pantalla
            //alert("¡Nueva Notificación!\n" + msg.text);
            showNotification(data.text);
        }
        else if(data.type === 'EVENT_EDIT') {
            showNotification(data.text);
        }
        else if(data.type === 'NEW_COMPLAINT') {
            showNotification(data.text);
        }
        else if(data.type == 'NEW_CHAT_MESSAGE') {
            showNotification(data.text);
        }
        else if(document.getElementById('chat-messages')) {

            const chatLog = document.getElementById('chat-messages');
            const div = document.createElement("div");
            /*div.className = "chat-message";

            div.innerHTML = `
                <p> <strong>[${data.from}]:</strong> ${data.text}</p>
            `;*/

            // Crear visualmente el mensaje recibido por WebSocket.
            // Se añade el id del mensaje para poder denunciarlo después.
            div.className = "chat-message reportable-message";
            div.dataset.messageId = data.id;
            div.title = "Haz click para denunciar este mensaje";

            div.innerHTML = `
                <p><strong>[${data.from}]:</strong> ${data.text}</p>
            `;

            chatLog.appendChild(div);
            chatLog.scrollTop = chatLog.scrollHeight;

            console.log(data)
        }
        else {
            // Mensaje de chat normal, actualizar el contador rojo del nav
            let p = document.querySelector("#nav-unread");
            if (p) {
                p.textContent = +p.textContent + 1;
            }
        }
    },

    /**
     * Message to be sent to the destination
     * @param {*} destination 
     * @param {*} body 
     */
    send: (destination, body) => {
        try {
            ws.stompClient.send(
                destination,
                ws.headers,
                JSON.stringify(body)
            );
            console.log("Mensaje enviado:", body);
        }
        catch(e) {
            console.log("Error enviando mensaje:", e);
        }
    },

    /**
     * Attempts to establish communication with the specified
     * web-socket endpoint. If successfull, will call 
     */
    initialize: (endpoint, subs = []) => {
        try {
            ws.stompClient = Stomp.client(endpoint);
            ws.stompClient.reconnect_delay = 2000;
            // only works on modified stomp.js, not on original from mantainer's site
            ws.stompClient.reconnect_callback = () => ws.retries-- > 0;
            ws.stompClient.connect(ws.headers, () => {
                ws.connected = true;
                console.log('Connected to ', endpoint, ' - subscribing:');
                while (subs.length != 0) {
                    let sub = subs.pop();
                    console.log(` ... to ${sub} ...`)
                    ws.subscribe(sub);
                }
            });
            console.log("Connected to WS '" + endpoint + "'")
        }
        catch (e) {
            console.log("Error, connection to WS '" + endpoint + "' FAILED: ", e);
        }
    },

    /**
     * Subscribe to the channel
     * @param {*} sub 
     */
    subscribe: (sub) => {

        try {
            ws.stompClient.subscribe(sub,
                (m) => ws.receive(JSON.parse(m.body))); // fails if non-json received!
            console.log("Hopefully subscribed to " + sub);
        }
        catch (e) {
            console.log("Error, could not subscribe to " + sub, e);
        }
    }

}

/**
 * Sends an "ajax" request using Fetch. Sends JSON and expects JSON back.
 * 
 * @param {string} url 
 * @param {string} method (GET|POST)
 * @param {*} data, typically a JSON-izable object, like a Message
 * @param {*} headers, to be used instead of defaults, if specified. To send NO headers,
 *  use {}. To send defaults, specify no value, or use false
 * 
 * @return {Promise}, which you should chain with `.then()` to manage responses, 
 *             and with `.catch()` to manage possible errors. 
 *             Errors will be notified as
 *  {
 *     url: <that you were accessing>, 
 *     data: <data you sent>,
 *     status: <code, such as 403>, 
 *     text: <describing the error>
 *  }
 */
function go(url, method, data = {}, headers = false) {
    let params = {
        method: method, // POST, GET, POST, PUT, DELETE, etc.
        headers: headers === false ? {
            "Content-Type": "application/json; charset=utf-8",
        } : headers,
        body: data instanceof FormData ? data : JSON.stringify(data)
    };
    if (method === "GET") {
        // GET requests cannot have body; I could URL-encode, but it would not be used here
        delete params.body;
    } else {
        params.headers["X-CSRF-TOKEN"] = config.csrf.value;
    }
    console.log("sending", url, params)
    return fetch(url, params)
        .then(response => {
            const r = response;
            if (r.ok) {
                return r.json().then(json => Promise.resolve(json));
            } else {
                return r.text().then(text => Promise.reject({
                    url,
                    data: JSON.stringify(data),
                    status: r.status,
                    text
                }));
            }
        });
}

/**
 * Fills an image element with the image retrieved from a URL.
 * 
 * while `targetImg.src = url` would also display the image, this code
 * has the advantage of using a data:url instead of a link; so that you can later
 * upload the image somewhere else using postImage
 * 
 * @return {Promise}, which you should chain with `.then()` to manage responses, 
 *             and with `.catch()` to manage possible errors. 
 * 
 * @param url of an image
 * @param targetImg element to populate with its data
 */
function readImageUrlData(url, targetImg) {
    return fetch(url)
        .then(response => response.blob())
        .then(blob => new Promise((resolve, reject) => {
            const reader = new FileReader()
            reader.onloadend = () => resolve(reader.result)
            reader.onerror = reject
            reader.readAsDataURL(blob)
        }))
        .then(data => targetImg.src = data);
}

/**
 * Fills an image element with the image retrieved from a file input.
 * 
 * Uses a data:url, also allowing the use of postImage. This is handy for
 * previews
 * 
 * @param file to use; for example, fileInput.files[0] would be the 1st one
 * @param targetImg element to populate with its data
 */
function readImageFileData(file, targetImg) {

    // see https://developer.mozilla.org/en-US/docs/Web/API/FileReader/readAsDataURL
    if (/\.(jpe?g|png|gif)$/i.test(file.name)) {
        let reader = new FileReader();
        reader.addEventListener("load", e => {
            console.log(e);
            targetImg.src = reader.result
        }, false);

        reader.readAsDataURL(file);
    } else {
        console.log("Not a good format: ", file.name);
    }
}

/**
 * Sends contents of a displayed image as a POST request to a server
 * 
 * @param img element to get the data from (MUST be using a data:url)
 * @param endpoint url to send the data to
 * @param name of field that will contain the image data (as expected by server)
 * @param filename to use 
 * 
 * @return {Promise}, which you should chain with `.then()` to manage responses, 
 *        and with `.catch()` to manage possible errors. 
 */
function postImage(img, endpoint, name, filename) {
    // from https://stackoverflow.com/a/30470303/15472
    function toBlob(dataurl) {
        let arr = dataurl.split(','),
            mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]),
            n = bstr.length,
            u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new Blob([u8arr], {
            type: mime
        });
    }
    let imageBlob = toBlob(img.src);
    let fd = new FormData();
    fd.append(name, imageBlob, filename);
    return go(endpoint, "POST", fd, {})
}

/**
 * Actions to perform once the page is fully loaded
 */
document.addEventListener("DOMContentLoaded", () => {

    if (config.socketUrl) {
        let subs = config.admin ? ["/topic/admin", "/user/queue/updates"] : ["/user/queue/updates"]
        if (config.topics && config.topics.length > 0) {
            subs = subs.concat(config.topics.split(",").map(t => `/topic/${t}`));
        }
        ws.initialize(config.socketUrl, subs);

        let p = document.querySelector("#nav-unread");
        if (p) {
            go(`${config.rootUrl}/user/unread`, "GET").then(d => p.textContent = d.unread);
        }
    }
    else {
        console.log("Not opening websocket: missing config", config)
    }


    /* Send information via community chat */
    const chatInput = document.getElementById("chatInput")      // Where the message is written
    const sendBtn = document.getElementById("chatSendButton")   // Button to send the message written

    // Obtener ID de la comunidad de la ruta del navegador -> communities/{id}
    const path = window.location.pathname;
    const id = path ? path.split("/").pop() : null;

    if(sendBtn) {
        sendBtn.addEventListener("click", () => {

            const text = chatInput.value.trim();
            if(text === "")
                return;


            const path = "/app/community/" + id;    // From '/topic/community-{id}'
            ws.send(path, {
                text: text
            });

            chatInput.value = "";
        });
    }   
   
    // Permite denunciar mensajes del chat haciendo click sobre ellos.
    document.addEventListener("click", (event) => {

        // Buscar si el click se ha hecho sobre un mensaje denunciable
        const message = event.target.closest(".reportable-message");

        // Si no se ha pulsado un mensaje, no hacemos nada
        if (!message) {
            return;
        }

        // Obtener el id del mensaje desde el atributo data-message-id
        const messageId = message.dataset.messageId;

        // Si no hay id, no se puede denunciar
        if (!messageId) {
            console.warn("No se puede denunciar un mensaje sin id.");
            return;
        }

        
        let v = confirm("¿Denunciar mensaje?");

        // Si el usuario cancela, no se envia nada
        if (!v) {
            return;
        }

        // Llamada AJAX al servidor para denunciar el mensaje
        go(`${config.rootUrl}/complaint/report/${messageId}`, "POST")
            .then(() => {
                alert("Mensaje denunciado correctamente.");
            })
            .catch(error => {
                console.error("Error denunciando mensaje:", error);
                alert("No se ha podido denunciar el mensaje.");
            });
    });

    // add your after-page-loaded JS code here; or even better, call 
    // 	 document.addEventListener("DOMContentLoaded", () => { /* your-code-here */ });
    //   (assuming you do not care about order-of-execution, all such handlers will be called correctly)
});

// Funcion para mostrar notificaciones en la esquina inferior izquierda
function showNotification(message) {

    // Crear contenedor si no existe
    let container = document.getElementById("noti-container");
    if(!container) {
        container = document.createElement("div");
        container.id = "noti-container";
        document.body.appendChild(container);
    }

    // Crear la notificacion
    const notification = document.createElement("div");
    notification.className = "noti-message";
    notification.innerText = message;

    // Agregar al contenedor
    container.appendChild(notification);

    // Desaparecer tras 5 segundos
    setTimeout(() => {
        notification.classList.add("noti-fadeout");
        setTimeout(() => notification.remove(), 500);
    }, 5000);
}