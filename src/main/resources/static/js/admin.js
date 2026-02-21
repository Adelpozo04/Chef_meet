document.addEventListener('DOMContentLoaded', () => {

    const boton_filtro_id = document.getElementById('filter-id')
    const boton_filtro_name = document.getElementById('filter-name')
    const boton_filtro_email = document.getElementById('filter-email')

    var search_bar_placeholder = document.getElementById('search-bar-input')

    boton_filtro_id.addEventListener('click', () => {
        search_bar_placeholder.placeholder = 'Buscar usuario por id'
    })

    boton_filtro_name.addEventListener('click', () => {
        search_bar_placeholder.placeholder = 'Buscar usuario por nombre'
    })

    boton_filtro_email.addEventListener('click', () => {
        search_bar_placeholder.placeholder = 'Buscar usuario por email'
    })

})