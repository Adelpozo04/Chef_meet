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

La vista de receta se podrá usar tanto para crear una receta como para verla o editarla.

### Edición de receta

- <strong>Introducción:</strong>
El usuario propietario podrá crear o editar la receta, para ello deberá introducir un titulo arriba de esta. Tras esto, tendrá un espacio para subir una imagen que se usará como representación de la receta en el formato tarjeta.

- <strong>Descripción:</strong>
Aqui se deberan indicar puntos importantes de cada receta, siendo estos el tiempo de elaboración, el tiempo de cocción y
el número de raciones. Estos datos serán obligatorios y aparecerán en la descripción de las recetas.

- <strong>Ingredientes:</strong>
    - Para añadir los ingrendientes, el usuario deberá pulsar un botón de _add_ cada vez que quiera introducir uno nuevo.

    - El ingrediente deberá ser escrito y acompañado en otro espacio distinto de una cantidad a añadir. En caso de no escribir nada dicho espacio de ingrediente será eliminado automaticamente.

    - En un futuro y como un _nice to have_, se integrará una base de datos con ingredientes comunes. Esos podrán seleccionarse mediante un _dropdown_ con barra de búsqueda. Vinculado a este, habrá otro _dropdown_ con las unidades de medida típicas del ingrediente, siendo obligatorio seleccionar una.

> [!NOTE]
> El botón de añadir debe salir desde el último ingrediente añadido, para que el usuario no deba subir arriba del todo para añadir otro*

- <strong>Elaboración:</strong>
De forma similar que en el apartado anterior: el usuario podrá añadir distintos pasos donde describa mediante un texto la elaboración de la receta.

De forma similar, en cada uno de los pasos aparecerá un botón debajo indicando la posibilidad de subir una imagen o video como acompañamiento a la descripción.

- <strong>Calorías:</strong>
Esta característica es un _nice to have_, será una pequeña opción en la cual el usuario podrá indicar las calorías de cada uno de los ingredientes (excepto si se agregaron con el _dropdown_, ya que aquí se pondrá las calorías automáticamente) y al finalizar la página hará un cálculo automático de calorías por ración.

- <strong>Subida de receta:</strong>
Para subir la receta será necesario pulsar un botón al final de esta. Dicho botón guardará automáticamente la receta en tu perfil y creará un _pop-up_ dando la opción de enviar la receta a alguna de las comunidades a las que perteneces.

### Ver receta
El _layout_ es igual tan solo quitando todos los botones y opciones de personalización

## Vista de comunidades
En esta vista se diferencia entre la vista donde se pueden explorar todas las comunidades y la vista dentro de una comunidad concreta.

### Vista comunidades
![Comunidad](/imgReadMe/comunidades.png)

Esta vista cuenta con una barra de búsqueda para buscar por el nombre una comunidad. Se puede filtrar para que se muestren todas las comunidades o sólo aquellas a las que pertenece el usuario.

Las comunidades están representadas con tarjetas (diseño aún por definir) que incluyen información cómo el nombre, tipo de cocina y una imagen.

### Vista de una comunidad
Cuando el usuario pulsa la tarjeta de una comunidad entra en esta vista.
Contiene un título con un fondo de una imagen personalizada como fondo. Dicho texto se esconde cada vez que el usuario haga scroll hacia abajo y aparece cuando hace scroll hacia arriba.

Dentro de esta vista existen tres pestañas con funcionalidades distintas: recetas, miembros y eventos.
#### Pestaña recetas
![Receta](/imgReadMe/comunidad.png)

Es la pestaña por defecto que se muestra al entrar a una comunidad.
Las recetas están divididas en carpetas organizadas por los miembros de la comunidad. Las recetas están representadas con tarjetas con las que se puede interactuar para verlas al detalle y valorarlas.

Cuando un usuario se une a una comunidad, elige que recetas de su perfil compartir con esa comunidad. Al salirse, estas dejan de estar asociadas a la comunidad.
#### Miembros
![Miembros](/imgReadMe/miembros.png)
La pestaña de miembros cuenta con un _chat_ grupal en tiempo real donde los miembros de la comunidad comparten mensajes. Además, a la izquierda se muestran todos los miembros indicando aquellos que están _online_.