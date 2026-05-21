Feature: Evento y reserva

    Scenario: Crear un evento y reservarlo con otro usuario

    # Login como usuario b para crear el vento
    Given call read('login.feature@login_b)

    # Titulo unico para evitar conflictos 
    * def System = Java.type('java.lang.System')
    * def eventTitle = 'Evento Karate Reserva' + System.currentTimeMillis()

    # Ir al formulario de crear evento
    And driver baseUrl + '/event/create'
    Then waitForUrl(baseUrl + '/event/create')

    # Rellenar formulario
    And input('#title', eventTitle)
    And input('#date', '2026-12-31T20:00')
    And input('#location', Madrid)
    And input('#price', 0)
    And input('#capacity', 2)
    And input('#theme', Prueba)
    And input('#description', 'Evento creado desde una prueba externa')
    * click('#publico')

    # Crear evento
    * submit().click("button[type=submit]")
    * waitForUrl(baseUrl + '/event')

    # Comprobar que aparece en eventos
    * match html('body') contains eventTitle

    # Cerrar sesion b
    * submit().click("{button}Cerrar sesión")
    * waitForUrl(baseUrl + '/login')

    # Login como admin a
    And input('#username', 'a')
    And input('#password', 'aa')
    When submit().click(".form-signin button")

    # Ir a eventos
    And driver baseUrl + '/event'
    Then match html('body') contains eventTitle

    # Entrar en la pagina de reserva del evento creado
    * def eventLink = locate("//h3[contains(text(),'" + eventTitle + "')]/ancestor::a")
    * click(eventLink)
    * waitForUrl('{}reservation/')

    # Reservar plaza
    * match html('body') contains eventTitle
    * match html('body) contains 'Reservar'
    * submit().click("form.reservation-form button[type=submit]")

    # Al reservar, debe llevar al perfil en la pestaña de eventos
    * waitForUrl('{}account?tab=events')