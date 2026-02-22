# Chef Meet
¿Te apasiona la cocina pero no tienes con quién compartir tus creaciones? Chef Meet es la plataforma definitiva para encontrar la receta que deseas aprender, poder compartir las tuyas propias y encontrar eventos donde podrás asistir para conocer a otros entusiastas de la cocina y poder adquirir nuevos conocimientos además de pasar un buen rato cocinando. ¡No te quedes sin cocinar y disfruta con Chef Meet!

Podrás ser partícipe de comunidades tanto públicas como privadas donde podrás valorar las recetas de los otros integrantes del grupo y además contarás con un chat en tiempo real para poder comunicarte con el resto. Aparte, cada usuario tendrá una valoración global (estrellas Michelin) visible por el resto que indica la reputación del usuario basada en reseñas que otros dejan sobre él tras asistir a un evento o probar su receta.

Y, por último, la creación de eventos gastronómicos, que pueden ser públicos o privados dentro de una comunidad, donde se debe informar de la fecha, hora, localización, temática, número máximo de participantes e incluso un precio.

### Roles
En la plataforma existen los siguientes roles:

- <strong>Administrador:</strong>
Gestiona el alta de los usuarios y supervisa los reportes de comportamiento inapropiado. Modera las comunidades públicas y tiene capacidad de banear usuarios o eliminar contenido ofensivo para garantizar una plataforma segura. 

- <strong>Usuario estándar:</strong>
Debe estar registrado y dado de alta. Gestiona su perfil público con nickname, foto, una pequeña descripción, logros y valoración global. Crea o se une a comunidades y eventos, y comparte y valora otras recetas y usuarios.

- <strong>Usuario inivitado:</strong>
Su acceso es solo de lectura. Pueden navegar por la plataforma y visualizar recetas, grupos y eventos públicos sin poder interactuar con ellos.


## Vista de recetas
![Receta](/imgReadMe/receta.png)

La vista de receta se puede usar tanto para crear una receta como para verla o editarla.

### Edición de receta

- <strong>Introducción:</strong>
El usuario propietario puede crear o editar la receta, para ello debe introducir un título arriba de esta. Tras esto, tiene un espacio para subir una imagen que se usará como representación de la receta en el formato tarjeta.

- <strong>Descripción:</strong>
Aqui se deben indicar puntos importantes de cada receta, siendo estos el tiempo de elaboración, el tiempo de cocción y
el número de raciones. Estos datos son obligatorios y aparecen en la descripción de las recetas.

- <strong>Ingredientes:</strong>
    - Para añadir los ingrendientes, el usuario debe pulsar un botón de _add_ cada vez que quiera introducir uno nuevo.

    - El ingrediente debe ser escrito y acompañado en otro espacio distinto de una cantidad a añadir. En caso de no escribir nada, dicho espacio de ingrediente será eliminado automaticamente.

    - En un futuro y como un _nice to have_, se integrará una base de datos con ingredientes comunes. Esos podrán seleccionarse mediante un _dropdown_ con barra de búsqueda. Vinculado a este, habrá otro _dropdown_ con las unidades de medida típicas del ingrediente, siendo obligatorio seleccionar una.

> [!NOTE]
> El botón de añadir debe salir desde el último ingrediente añadido, para que el usuario no deba subir arriba del todo para añadir otro*

- <strong>Elaboración:</strong>
De forma similar que en el apartado anterior: el usuario puede añadir distintos pasos donde describa mediante un texto la elaboración de la receta.

De forma similar, en cada uno de los pasos aparece un botón debajo indicando la posibilidad de subir una imagen o vídeo como acompañamiento a la descripción.

- <strong>Calorías:</strong>
Esta característica es un _nice to have_, será una pequeña opción en la cual el usuario podrá indicar las calorías de cada uno de los ingredientes (excepto si se agregaron con el _dropdown_, ya que aquí se pondrá las calorías automáticamente) y al finalizar la página hará un cálculo automático de calorías por ración.

- <strong>Subida de receta:</strong>
Para subir la receta es necesario pulsar un botón al final de esta. Dicho botón guarda automáticamente la receta en tu perfil y aparece un _pop-up_ dando la opción de enviar la receta a alguna de las comunidades a las que perteneces.

### Ver receta
El _layout_ es igual tan solo quitando todos los botones y opciones de personalización

## Vista de comunidades
En esta vista se diferencia entre la vista donde se pueden explorar todas las comunidades y la vista dentro de una comunidad concreta.

### Vista comunidades
![Comunidad](/imgReadMe/comunidades.png)

Esta vista cuenta con una barra de búsqueda para buscar por el nombre una comunidad. Se puede filtrar para que se muestren todas las comunidades o sólo aquellas a las que pertenece el usuario.

Las comunidades están representadas con tarjetas (diseño aún por definir) que incluyen información cómo el nombre, tipo de cocina y una imagen.
También está el botón para crear una comunidad, donde aparecerá una pestaña con los datos a rellenar, que es la misma que aparece en las tarjetas.

### Vista de una comunidad
Cuando el usuario pulsa la tarjeta de una comunidad entra en esta vista.
Contiene un título con un fondo de una imagen personalizada como fondo. Dicho texto se esconde cada vez que el usuario haga scroll hacia abajo y aparece cuando hace scroll hacia arriba.

Dentro de esta vista existen tres pestañas con funcionalidades distintas: recetas, miembros y eventos.
#### Pestaña recetas
![Receta](/imgReadMe/comunidad.png)

Es la pestaña por defecto que se muestra al entrar a una comunidad.
Las recetas están divididas en carpetas organizadas por los miembros de la comunidad. Las recetas están representadas con tarjetas con las que se puede interactuar para verlas al detalle y valorarlas.
Se pueden ordenar por orden alfabético, y potencialmente por valoración.

Cuando un usuario se une a una comunidad, elige que recetas de su perfil compartir con esa comunidad. Al salirse, estas dejan de estar asociadas a la comunidad.
#### Pestaña miembros
![Miembros](/imgReadMe/miembros.png)
La pestaña de miembros cuenta con un _chat_ grupal en tiempo real donde los miembros de la comunidad comparten mensajes. Además, a la izquierda se muestran todos los miembros indicando aquellos que están _online_.

#### Pestaña eventos
![Eventos](/imgReadMe/eventos.png)
Por último en la pestaña de eventos, se muestran las tarjetas (diseño aún por definir) de los próximos eventos y los eventos ya pasados organizados por la comunidad. Hay un botón cuya funcionalidad es crear un nuevo evento, que llevará a la ventana de eventos.

Cuando se hace un evento asociado a una comunidad este se puede crear de forma pública para gente que pertenezca o no a esta, o privados únicamente para los participantes de esa comunidad.

## Vista de eventos
![Eventos](/imgReadMe/eventos2.png)

La vista de eventos, igual que la de comunidades, cuenta con una barra de búsqueda para buscar por el nombre un evento. Se muestran todos los eventos públicos que estén asociados o no a una comunidad, y se puede filtrar para que se muestren todos los eventos o sólo aquellos a los que ya se ha apuntado el usuario.

Potencialmente, se quiere agregar un mapa interactivo de España que se pueda ampliar y que muestre las localizaciones de los eventos de manera que sea más visual y fácil para el usuario encontrar eventos cercanos.

Los eventos están representados con tarjetas (diseño aún por definir)
que incluyen información cómo el nombre, temática, fecha, localización, precio, número máximo de participantes y una imagen.
También está el botón para crear un evento, donde aparecerá una pestaña con la información a rellenar, que es la misma que aparece en las tarjetas.

### Vista de reserva y pago de evento
![Eventos](/imgReadMe/pagos.png)
Cuando el usuario interactúe con una de estas tarjetas, se le lleva a otra ventana donde aparece la información del evento más detallada y donde el usuario puede reservar una plaza y, si se debe, realizar el pago para asistir.

Tras pulsar el botón de reservar, se abre una pasarela de pago cifrada donde el usuario introduce sus datos bancarios de forma protegida. Una vez validado el pago, se muestra un mensaje de éxito y el evento se incluye a "Mis eventos".

## Vista de perfil
![Perfil](/imgReadMe/perfil.png)
La vista de perfil está estructurada mediante un selector lateral. Dependiendo de la opción que elija el usuario, el contenido en el área principal cambia. 

- <strong>Datos personales:</strong> Contiene información del usuario como el nombre, la foto del perfil, una breve presentación y las Estrellas Michelin.

- <strong>Mis recetas:</strong> Contiene todas las recetas que el usuario haya compartido en la plataforma, con posibilidad de organizarlas como considere.

- <strong>Mis comunidades:</strong> Contiene las comunidades a las que pertenece el usuario para poder acceder a ellas más rápidamente.

- <strong>Mis eventos:</strong> Contiene las entradas de los eventos a los que se ha apuntado el usuario.

- <strong>Personalización:</strong>

- <strong>Reportar:</strong> Permite enviar quejas y reportes directamente al administrador.

- <strong>Log out:</strong> Para cerrar sesión.

## Vista de administrador
![Vista administrador](/imgReadMe/admin.png)
La vista de adminstrador solo deberá proporcionar a los usuarios <strong>administradores</strong> una vista sencilla y rápida para gestionar a todos los usuarios, recetas, comunidades y eventos de la aplicación <strong> de cualquier forma</strong>.
Es decir, el administrador debe poder consultar, modificar, eliminar, reportar a cualquier usuario, receta, comunidad y evento.

Para gestionar todos estos tipo de datos el administrador podrá desplegar un _dropdown_ mediante el botón _filtros_ para seleccionar qué tipo de datos desea buscar. Cada selección de este _dropdown_ modificará todas las barras de búsqueda para filtrar los datos.
En base a los filtros seleccionados, deberán poder verse todos los datos que satisfagan esos filtros y que ofrezcan controladores para manipular estos datos.

- <strong>Usuarios:</strong> Permitirá al administrador buscar a usuarios mediante su identificador único, nombre de usuario o correo electrónico.
- <strong>Recetas:</strong> Permitirá al administrador buscar recetas mediante su identificador único, nombre de receta o ingredientes. Para este último deberían poder seleccionarse varios ingredientes, de tal forma que la búsqueda permita filtrar por recetas que contengan el ingrediente 1, ingrediente 2... Y así, sucesivamente.
- <strong>Comunidades:</strong> Permitirá al administrador buscar comunidades según su identificador único, nombre de comunidad, nombre de usuario o identificador del creador, tamaño mínimo y máximo de usuarios asociados a esta y país de esta comunidad.
- <strong>Eventos:</strong> Permitirá al administrador buscar eventos por su identificador único, nombre de evento, país en el que tiene lugar y su fecha de comienzo.