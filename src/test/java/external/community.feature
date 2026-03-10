
Feature: Manejo de comunidades

  @createCom01
  Scenario: Creacion comunidad 01 de forma correcta
    # Login de un usuario aprovechando test de login
    Given call read('login.feature@login_a')
    # Navegar a vista de comunidades
    And driver baseUrl + '/communities/create'
    # Clicar boton crear comunidad
    When submit().click("#create-community")
    Then waitForUrl(baseUrl + '/communities/create')
    # Rellenar formulario
    And input('community-title', "Italiana")
    And input('community-desc', "Comunidad privada amantes de pasta")
    # Confirmar creacion
    When submit().click("#create-button")
    # Validar que se ha creado 
    Then waitForUrl(baseUrl + '/communities')

  @createCom02
  Scenario: Creacion comunidad 02 de forma correcta
    Given call read('login.feature@login_a')
    And driver baseUrl + '/communities/create'
    When submit().click("#create-community")
    Then waitForUrl(baseUrl + '/communities/create')
    And input('community-title', "ComidaItaliana")
    And input('community-desc', "Comunidad privada amantes de pizza")
    When submit().click("#create-button")
    Then waitForUrl(baseUrl + '/communities')

  Scenario: Borrar comunidad de forma correcta siendo admin
    Given call read('login.feature@login_a')
    # Confirmar que al iniciar sesion es admin
    And driver baseUrl + '/admin'
    # En la barra de navegador ir a comunidades
    When submit().click("#community-link")
    Then waitForUrl(baseUrl + '/communities')
    # Obtener id de la comunidad
    And def id-community = script(obtenerID())
    # Borrar comunidad
    When submit().click("#delete-com-01")
    # Intentar ir a la comunidad borrada
    And driver baseUrl + '/communities/id-community'
    # Como ha sido eliminada, da error y confirma que se ha borrado correctamente
    Then match html('.error')

  Scenario: Busqueda correcta de comunidades en la barra de busqueda
    # Crear comunidades aprovechando los tests
    Given call read('community.feature@createCom01')
    Given call read('community.feature@createCom02')
    # Ir a la vista de comunidades
    When submit().click("#community-link")
    Then waitForUrl(baseUrl + '/communities')
    # Rellenar en la barra de busqueda
    And input('search-community-bar', "Italiana")
    # Clicar el boton de buscar
    When submit().click("#community-search-button")
    Then delay(500)
    def num-com = script("document.querySelectorAll(.card).lenght - document.querySelectorAll(.card .d-none).lenght)
    Then num-com >= 0

    
  Scenario: Confirmar que puede haber comunidades con mismo nombre y distinto id
    Given call read('login.feature@login_a')
    When submit().click("#create-community")
    Then waitForUrl(baseUrl + '/communities/create')
    And input('community-title', "ComidaItaliana")
    And input('community-desc', "Comunidad privada amantes de pasta")
    When submit().click("#create-button")
    Then waitForUrl(baseUrl + '/communities')
    Given call read('login.feature@@logout')
    Given call read('login.feature@login_b')
    When submit().click("#create-community")
    Then waitForUrl(baseUrl + '/communities/create')
    And input('community-title', "ComidaItaliana")
    And input('community-desc', "Comunidad privada amantes de pizza")
    When submit().click("#create-button")
    Then waitForUrl(baseUrl + '/communities')

  Scenario: Unirse a una comunidad
    # Un usuario b inicia sesion
    Given call read('login.feature@login_b')
    # Navega a la comunidad que se quiere unir 
    And driver baseUrl + '/communities/id'
    # Clicar boton de unirse
    When click("button[id=btn-join-community]")
 
  Scenario: El creador elimina a un usuario de su comunidad y este desaparece de la lista 
    # El creador inicia sesion 
    Given call read('login.feature@login_a')
    # Navegar a la pagina de la comunidad (id de la comunidad)
    And driver baseUrl + '/communities/id'
    # Comprobar que el usuario que se quiere eliminar esta en la lista
    And match html('#list-members') contains 'usuario_b'
    # Clicar boton de eliminar a ese usuario en concreto
    When click("button[id='btn-eject-usuario_b"])
    And delay(500)
    # Verificar que el usuario ya no se encuentra en la lista de miembros
    Then match html('#list-members') !contains 'usuario_b'
    