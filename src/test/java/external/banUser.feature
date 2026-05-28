Feature: Banear y desbanear usuario

Background:
  # Prueba externa con navegador real
  * configure driver = { type: 'chrome', timeout: 50000, headless: false }

Scenario: Si el admin banea al usuario b, b no puede entrar; si lo desbanea, b vuelve a entrar

  # ------------------------------------------------------------
  # 1. Login como administrador
  # ------------------------------------------------------------
  Given driver baseUrl + '/login'

  # Rellenar formulario de login como admin.
  # En la base de datos de prueba, el usuario admin suele ser "a" con contraseña "aa".
  And input('#username', 'a')
  And input('#password', 'aa')
  When submit().click(".form-signin button")

  # Comprobamos que hemos entrado al panel de administración.
  Then waitForUrl('{}admin')
  And match html('body') contains 'Administración'


  # ------------------------------------------------------------
  # 2. Banear / deshabilitar al usuario b
  # ------------------------------------------------------------
  # Buscamos la fila de la tabla de usuarios donde el nombre de usuario sea "b".
  # Desde esa fila, pulsamos el botón "Deshabilitar".
  #
  # El botón usa AJAX, así que no hay cambio de página; por eso luego esperamos
  # a que el texto del botón cambie a "Habilitar".
  When click("//tr[td[2][normalize-space()='b']]//button[contains(@class,'toggle') and normalize-space()='Deshabilitar']")

  # Esperar un poco para que termine la llamada AJAX al endpoint /admin/toggle/{id}
  * delay(500)

  # Comprobar que ahora el botón dice "Habilitar", lo que indica que b está deshabilitado
  Then match html('body') contains 'Habilitar'


  # ------------------------------------------------------------
  # 3. Cerrar sesión como admin
  # ------------------------------------------------------------
  # Si en tu nav el botón se llama "logout", cambia "Cerrar sesión" por "logout".
  When submit().click("{button}Cerrar sesión")
  Then waitForUrl('{}login')


  # ------------------------------------------------------------
  # 4. Intentar entrar como b estando baneado
  # ------------------------------------------------------------
  # Al estar deshabilitado, el usuario b no debe poder iniciar sesión.
  And input('#username', 'b')
  And input('#password', 'aa')
  When submit().click(".form-signin button")

  # Debe seguir en login y mostrar error.
  Then waitForUrl('{}login')
  And match html('body') contains 'Error en nombre de usuario o contraseña'


  # ------------------------------------------------------------
  # 5. Volver a entrar como admin
  # ------------------------------------------------------------
  And input('#username', 'a')
  And input('#password', 'aa')
  When submit().click(".form-signin button")

  Then waitForUrl('{}admin')
  And match html('body') contains 'Administración'


  # ------------------------------------------------------------
  # 6. Desbanear / habilitar otra vez al usuario b
  # ------------------------------------------------------------
  # Ahora la fila de b debe tener un botón que dice "Habilitar".
  When click("//tr[td[2][normalize-space()='b']]//button[contains(@class,'toggle') and normalize-space()='Habilitar']")

  # Esperar a que termine el AJAX
  * delay(500)

  # Comprobar que vuelve a aparecer "Deshabilitar", señal de que b está activo otra vez
  Then match html('body') contains 'Deshabilitar'


  # ------------------------------------------------------------
  # 7. Cerrar sesión como admin
  # ------------------------------------------------------------
  When submit().click("{button}Cerrar sesión")
  Then waitForUrl('{}login')


  # ------------------------------------------------------------
  # 8. Comprobar que b puede volver a entrar
  # ------------------------------------------------------------
  And input('#username', 'b')
  And input('#password', 'aa')
  When submit().click(".form-signin button")

  # Si el login funciona, b debe salir de /login y entrar en su cuenta.
  # En tus ejemplos anteriores se esperaba /user/2, pero en tu app actual
  # puede ser /account. Por eso comprobamos que ya no estamos en login
  # y que no aparece el mensaje de error.
  Then match driver.url != baseUrl + '/login'
  And match html('body') !contains 'Error en nombre de usuario o contraseña'