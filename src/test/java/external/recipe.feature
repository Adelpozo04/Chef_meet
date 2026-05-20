Feature: Manejo de recetas

    #No es para validar, es para tener una creacion rapida de una receta
    @createRecipeB
    Scenario: Crear receta base

        Given call read('login.feature@login_b')

        And driver baseUrl + '/recipe/create'

        # esperar carga de pagina
        Then waitForUrl(baseUrl + '/recipe/create')

        # asignacion de valores

        # de texto
        And input('#recipe-title', 'Pizza')
        And input('#recipe-time', '40min')
        And input('#recipe-difficulty', 'muy dificil')
        And input('#recipe-calories', '1200')

        # de imagen
        * inputFile('#recipe-cover', 'src/main/resources/static/img/ev_espana.jpg')

        # de checkbox
        * click('#recipe-public')

        # de ingrediente
        * click('#addIngredient')
        * waitFor('#ingredient-0')
        * delay(500)
        * select('#ingredient-0', '1')
        * delay(300)

        # de pasos
        * click('#addStep')
        * delay(200)
        * waitFor('#step-0')
        * input('#step-0', 'Hervir agua')
        * delay(200)

        # enviar
        * click('#create-button')

        # validar
        * waitForUrl(baseUrl + '/recipe')

    Scenario: Creacion correcta de recetas

        # login
        Given call read('login.feature@login_b')

        # abrir pagina recetas
        And driver baseUrl + '/recipe/create'

        # esperar carga de pagina
        Then waitForUrl(baseUrl + '/recipe/create')

        # asignacion de valores

        # de texto
        And input('#recipe-title', 'Pizza')
        And input('#recipe-time', '40min')
        And input('#recipe-difficulty', 'muy dificil')
        And input('#recipe-calories', '1200')

        # de imagen
        * inputFile('#recipe-cover', 'src/main/resources/static/img/ev_espana.jpg')

        # de checkbox
        * click('#recipe-public')

        # de ingrediente
        * click('#addIngredient')
        * waitFor('#ingredient-0')
        * delay(500)
        * select('#ingredient-0', '1')
        * delay(300)

        # de pasos
        * click('#addStep')
        * delay(200)
        * waitFor('#step-0')
        * input('#step-0', 'Hervir agua')
        * delay(200)

        # enviar
        * click('#create-button')

        # validar
        * waitForUrl(baseUrl + '/recipe')
        * match html('body') contains 'Pizza'

    Scenario: Ver una receta ya creada

        # se crea una primera receta
        Given call read('recipe.feature@createRecipeB')

        # se navega a la pestaña de recetas por si acaso
        And driver baseUrl + '/recipe'

        Then waitForUrl(baseUrl + '/recipe')

        #Elegimos el link a la receta que nos hemos creado y sacamos su ruta con id incluido

        #ancestor marca que se debe subir hasta llegar al padre <a>
        * def recipeLink = locate("//span[contains(text(),'Pizza')]/ancestor::a") 

        #la ruta se saca del href declarado en el <a>
        * def href = recipeLink.attribute('href')

        #Hacemos click en el enlace a la receta
        * click(recipeLink)
        * delay(200)

        #validamos el test
        Then match driver.url contains href

    Scenario: Borrar una receta que no es tuya como admin

        # se crea una primera receta
        Given call read('recipe.feature@createRecipeB')

        # se hace un logout
        Given call read('login.feature@logout')

        # se hace un login como admin
        Given call read('login.feature@login_a')

        # se navega hasta la pestaña de recetas
        And driver baseUrl + '/recipe/create'

        # esperar carga de pagina
        Then waitForUrl(baseUrl + '/recipe/create')

        # se acepta el pop-up de confirmacion que salga la proxima vez
        * dialog(true)

        # se hace click en el boton de borrar receta navegando desde el texto Pizza hasta el padre wrapper y luego al boton de borrado
        * click("//span[contains(text(),'Pizza')]/ancestor::div[contains(@class,'card_wrapper')]//button[contains(@class,'btn-danger')]")

        # se valida
        Then match html('body') !contains 'Pizza'