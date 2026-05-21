Feature: Crear comunidad

Background:
  * configure driver = { type: 'chrome', timeout: 50000, headless: false }
  * call read('classpath:external/login.feature@login_a')

Scenario: crear comunidad correctamente

  Given driver baseUrl + '/communities'

  And waitFor('#create-community')
  When click('#create-community')

  Then waitFor('#title-input')
  And input('#title-input', 'Comunidad Karate Test')
  And input('#country-select', 'Francia')
  And input('#comm-desc', 'Descripcion Karate Test')
  
  When submit().click('#create-button')

  Then waitForUrl('/communities/')