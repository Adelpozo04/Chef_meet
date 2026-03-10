
Feature: Manejo de comunidades

  @createCom01
  Scenario: creacion comunidad correcta
    Given call read('login.feature@login_a')
    When submit().click("#create-community")
    Then waitForUrl(baseUrl + '/communities/create')
    And input('community-title', "Italiana")
    And input('community-desc', "Comunidad privada amantes de arroz")
    When submit().click("#create-button")
    Then waitForUrl(baseUrl + '/communities')

  @createCom02
  Scenario: creacion comunidad correcta
    Given call read('login.feature@login_a')
    When submit().click("#create-community")
    Then waitForUrl(baseUrl + '/communities/create')
    And input('community-title', "ComidaItaliana")
    And input('community-desc', "Comunidad privada amantes de arroz")
    When submit().click("#create-button")
    Then waitForUrl(baseUrl + '/communities')

  Scenario: borrar comunidad correcta
    Given driver baseUrl + '/admin'
    When submit().click("#community-link")
    Then waitForUrl(baseUrl + '/communities')
    And def id-community = script(obtenerID())
    When submit().click("#delete-com-01")
    And driver baseUrl + '/communities/id-community'
    Then match html('.error')

  Scenario: busqueda correcta
    Given call read('community.feature@createCom01')
    Given call read('community.feature@createCom02')
    When submit().click("#community-link")
    Then waitForUrl(baseUrl + '/communities')
    And input('search-community-bar', "Italiana")
    When submit().click("#community-search-button")
    Then delay(500)
    def num-com = script("document.querySelectorAll(.card).lenght - document.querySelectorAll(.card .d-none).lenght)
    Then num-com >= 0

    
  Scenario: comunidades con mismo nombre y distinto id
    Given call read('login.feature@login_a')
    When submit().click("#create-community")
    Then waitForUrl(baseUrl + '/communities/create')
    And input('community-title', "ComidaItaliana")
    And input('community-desc', "Comunidad privada amantes de arroz")
    When submit().click("#create-button")
    Then waitForUrl(baseUrl + '/communities')
    Given call read('login.feature@@logout')
    Given call read('login.feature@login_b')
    When submit().click("#create-community")
    Then waitForUrl(baseUrl + '/communities/create')
    And input('community-title', "ComidaItaliana")
    And input('community-desc', "Comunidad privada amantes de arroz")
    When submit().click("#create-button")
    Then waitForUrl(baseUrl + '/communities')

    