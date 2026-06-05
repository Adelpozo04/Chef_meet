# Chef Meet
¿Te apasiona la cocina pero no tienes con quién compartir tus creaciones? Chef Meet es la plataforma definitiva para encontrar la receta que deseas aprender, poder compartir las tuyas propias y encontrar eventos donde podrás asistir para conocer a otros entusiastas de la cocina y poder adquirir nuevos conocimientos además de pasar un buen rato cocinando. ¡No te quedes sin cocinar y disfruta con Chef Meet!

Podrás ser partícipe de comunidades tanto públicas como privadas donde podrás valorar las recetas de los otros integrantes del grupo y además contarás con un chat en tiempo real para poder comunicarte con el resto.

Y, por último, la creación de eventos gastronómicos, que pueden ser públicos o privados dentro de una comunidad, donde se debe informar de la fecha, hora, localización, temática, número máximo de participantes e incluso un precio.

### Roles
En la plataforma existen los siguientes roles:

- <strong>Administrador:</strong>
Gestiona el alta de los usuarios y supervisa los reportes de comportamiento inapropiado. Modera las comunidades y eventos, y tiene capacidad de banear usuarios o eliminar contenido ofensivo para garantizar una plataforma segura. 

- <strong>Usuario estándar:</strong>
Debe estar registrado y dado de alta. Gestiona su perfil público con nickname y foto. Crea o se une a comunidades y eventos, y comparte y valora otras recetas.

- <strong>Usuario inivitado:</strong>
Su acceso es solo de lectura. Pueden navegar por la plataforma y visualizar recetas, grupos y eventos públicos sin poder interactuar con ellos.


## Vista de recetas
![Receta](/imgReadMe/receta.png)

La vista de receta se puede usar principalmente tanto para crear una receta como para verla. Además de eso este espacio esta dividido entre las recetas creadas por el usuario logueado y recetas subidas por otros usuarios. Dentro de estas recetas se puede hacer uso del buscador para buscar una en concreto. Esta division entre recetas tambien trae con sigo distintas funcionalidades las cuales son:

- Borrar receta: solo sera posible borrar una receta si es el autor de esta o el Admin de la pagina web. Esta acción borra el elemento de la base de datos
- Enviar a comunidad: si eres el autor de una receta puedes enviarla a una comunidad a la que pertenezcas para que desde ella sea visible.
- Valorar receta: si no eres el autor de una receta puedes valorarla usando un sistema de estrellas que te permiten ponerle una puntuación en concreto.

### Edición de receta

- <strong>Introducción:</strong>
El usuario propietario puede crear una receta, para ello debe introducir un título arriba de esta. Tras esto, tiene un espacio para subir una imagen que se usará como representación de la receta en el formato tarjeta.

- <strong>Descripción:</strong>
Aqui se deben indicar puntos importantes de cada receta, siendo estos el tiempo de elaboración y la dificultad de la receta. Estos datos son obligatorios y aparecen en la descripción de las recetas.

- <strong>Ingredientes:</strong>
    - Para añadir los ingrendientes, el usuario debe pulsar un botón de _add_ cada vez que quiera introducir uno nuevo.

    - Esto le dará una lista de ingredientes almacenanos en la base de datos. sobre estos debera escoger el que quiera incorporar.

    - Una vez elegio el ingrediente aparecerá un espacio en blanco que le permite añadir la cantidad que desea establecer en formato de texto.

- <strong>Elaboración:</strong>
De forma similar que en el apartado anterior: el usuario puede añadir distintos pasos donde describa mediante un texto la elaboración de la receta. Acompañado a estos pasos puede establecer una foto que represente dicho paso. Paras ello antes debera pulsar un boton de añadir imagen debajo del paso en concreto que desea acompañar.

- <strong>Calorías:</strong>
Opcion en formato de texto que permite al usuario indicar el numero de calorias que tiene una receta en concreto

- <strong>Visibilidad:</strong>
Hay un boton el cual permite establecer si una receta puede ser vista por otras personas o no. Si es publica cualquiera podra verlo y si es privada solo el autor sera capaz de ver la receta junto con el Admin.

- <strong>Subida de receta:</strong>
Para subir la receta es necesario pulsar un botón al final de esta. Dicho botón guarda automáticamente la receta en tu perfil.


### Ver receta
El _layout_ es igual tan solo quitando todos los botones y opciones de personalización. Tambien en la parte de arriba aparecera una media de la valoracion que le han dado los usuarios a la receta.

## Vista de comunidades
En esta vista se diferencia entre la vista donde se pueden explorar todas las comunidades y la vista dentro de una comunidad concreta.

### Vista comunidades
![Comunidad](/imgReadMe/comunidades.png)

Esta vista cuenta con una barra de búsqueda para buscar por el nombre una comunidad. Se puede filtrar para que se muestren todas las comunidades o sólo aquellas a las que pertenece el usuario.

Las comunidades están representadas con tarjetas que incluyen información cómo el nombre, pais de procedencia (marca el tipo de cocina mas bien) y una imagen.
También está el botón para crear una comunidad, donde aparecerá una pestaña con los datos a rellenar, que es la misma que aparece en las tarjetas.

### Vista de una comunidad
Cuando el usuario pulsa la tarjeta de una comunidad entra en esta vista.

Dentro de esta vista existen cuatro funcionalidades distintas: recetas, miembros, eventos y el chat de la comunidad.

- En el apartado de miembros se pueden ver todos los miembros y el creador de la comunidad
- En el apartado de recetas se pueden ver las recetas asociadas a una comunidad.
- En el apartado de eventos se pueden ver todos los eventos pasados y futuros de una comunidad. Ademas de acceder a su información.

Con el boton inferior derecho el usuario puede desplegar el chat de la comunidad y ver en tiempo real los ultimos 50 mensajes enviados y qué usuario lo envió. De esta forma todos los usuarios conectados pueden interactuar entre ellos. 

#### Apartado recetas
![Receta](/imgReadMe/comunidad.png)

Parte central de la comunidad, en ella hay enlaces a las distintas recetas que los miembros han subido en formato de tarjetas.

Un usuario de una comunidad puede subir cualquiera de las recetas que desee a esta desde su vista de recetas.

#### Apartado miembros
![Miembros](/imgReadMe/miembros.png)
Pestaña la cual muestra los distintos miembros que se han unido a la comunidad asi como el creador de esta

#### Apartado eventos
![Eventos](/imgReadMe/eventos.png)
Por último en el apartado de eventos, se muestran los nombres de los próximos eventos y los eventos ya pasados organizados por la comunidad. Hay un botón cuya funcionalidad es crear un nuevo evento, que llevará a la ventana de eventos.

Cuando se hace un evento asociado a una comunidad este se puede crear de forma pública para gente que pertenezca o no a esta, o privados únicamente para los participantes de esa comunidad.

#### Pop-up Chat
Es un menu emergente el cual muestra un chat entre usuarios, dejando al usuario actual escribir lo que quiera y mostrando los mensajes pasados tanto suyos como los miembros de su comunidad

## Vista de eventos
![Eventos](/imgReadMe/eventos_mapa.png)

La vista de eventos, igual que la de comunidades, cuenta con una barra de búsqueda para buscar por el nombre un evento. Se muestran todos los eventos públicos que estén asociados o no a una comunidad, y se puede filtrar para que se muestren todos los eventos o sólo aquellos a los que ya se ha apuntado el usuario.

Se ha agregado un mapa interactivo de España que se puede ampliar y  muestra las localizaciones de los eventos, de manera que sea más visual y fácil para el usuario encontrar eventos cercanos.

Los eventos están representados con tarjetas que incluyen información cómo el nombre, temática, fecha y una imagen. También está el botón para crear un evento, donde aparecerá una pestaña con la información a rellenar (título, imagen, fecha, hora, precio, ubicación, aforo, temática y descripción).

### Vista de reserva y pago de evento
![Eventos](/imgReadMe/reserva.png)
Cuando el usuario interactúa con una de estas tarjetas, se le lleva a otra ventana donde aparece la información del evento más detallada y donde el usuario puede reservar una plaza y, si se debe, realizar el pago para asistir.

Tras pulsar el botón de reservar, se abre una simulación de una pasarela de pago cifrada. Una vez validado el pago, se muestra un mensaje de éxito, el evento se incluye a "Mis eventos" y se redirige al usuario a la pestaña de mis eventos dentro del perfil.

## Vista de perfil
![Perfil](/imgReadMe/miperfil.png)
La vista de perfil está estructurada mediante un selector lateral. Dependiendo de la opción que elija el usuario, el contenido en el área principal cambia. 

- <strong>Datos personales:</strong> Contiene la información del usuario en una tarjeta con el nickname, nombre y la foto del perfil.

- <strong>Mis recetas:</strong> Contiene todas las recetas que el usuario haya compartido en la plataforma, con posibilidad de organizarlas como considere.

- <strong>Mis comunidades:</strong> Contiene las comunidades a las que pertenece el usuario para poder acceder a ellas más rápidamente.

- <strong>Mis eventos:</strong> Contiene las entradas de los eventos a los que se ha apuntado el usuario.

- <strong>Mis quejas:</strong> Contiene las quejas que el usuario a enviado al admin, además de un botón para escribir nuevas. Si una queja a sido resuelta por el admin aparecera un tick verde junto a ella

- <strong>Log out:</strong> Para cerrar sesión.


## Vista de administrador
![Vista administrador](/imgReadMe/administrador.png)
La vista de adminstrador solo deberá proporcionar a los usuarios <strong>administradores</strong> una vista sencilla y rápida para gestionar a todos los usuarios, recetas, comunidades y eventos de la aplicación.
Es decir, el administrador debe poder deshabilitar a cualquier usuario, eliminar recetas o comunidades y cancelar eventos. También es capaz de revisar las distintas quejas que le han enviado, diferenciando entre las resuletas y las pendientes. Desde ahi podra meterse para revisar la queja y o bien borrarla o biemn marcarla como resuelta.

Para gestionar todos estos tipo de datos el administrador podrá desplegar un _dropdown_  para filtrar qué tipo de datos desea buscar. Cada selección de este _dropdown_ modificará todas las entradas de las tablas de búsqueda para filtrar los datos.
En base a los filtros seleccionados, deberán poder verse todos los datos que satisfagan esos filtros y que ofrezcan controladores para manipular estos datos.

- <strong>Usuarios:</strong> Permitirá al administrador buscar a usuarios mediante su identificador único, nombre de usuario o nombre y apellidos.
- <strong>Recetas:</strong> Permitirá al administrador buscar recetas mediante su identificador único, título de la receta o nombre del creador.
- <strong>Comunidades:</strong> Permitirá al administrador buscar comunidades según su identificador único, nombre de la comunidad, temática, nombre del creador o número de miembros.
- <strong>Eventos:</strong> Permitirá al administrador buscar eventos por su identificador único, nombre del evento, localización o fecha.
- <strong>Quejas:</strong> Permitirá al administrador buscar quejas por su identificador, titulo o fecha.

## Estructura base de datos
![Base de datos](/bd.jpg)

## Nombres de usuarios y contraseñas
Lista de usuarios y contraseñas para poder probar la aplicación. (Comprobar que en el archivo application.properties: spring.jpa.hibernate.ddl-auto=update, spring.datasource.url=jdbc:h2:file:./iwdb)


- <strong>Usuario:</strong> a , <strong>contraseña:</strong> aa (admin)
- <strong>Usuario:</strong> b , <strong>contraseña:</strong> aa
- <strong>Usuario:</strong> xtina , <strong>contraseña:</strong> huevofrito
- <strong>Usuario:</strong> toby , <strong>contraseña:</strong> perroperro
- <strong>Usuario:</strong> tobi , <strong>contraseña:</strong> perroperro
- <strong>Usuario:</strong> toby24 , <strong>contraseña:</strong> perroperro
- <strong>Usuario:</strong> emcavero , <strong>contraseña:</strong> papapapa
- <strong>Usuario:</strong> elo , <strong>contraseña:</strong> mamamama