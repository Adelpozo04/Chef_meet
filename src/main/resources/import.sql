-- start id numbering from a value that is larger than any assigned above
ALTER SEQUENCE "PUBLIC"."GEN" RESTART WITH 2000;

-- insert admin (username a, password aa)
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (1, TRUE, 'ADMIN,USER', 'a',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (2, TRUE, 'USER', 'b',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');

-- Ejemplo de paises
INSERT INTO country (id, country_name, country_emoji) VALUES
(0, 'España', '🇪🇸'),
(1, 'Francia', '🇫🇷'),
(2, 'Italia', '🇮🇹'),
(3, 'Alemania', '🇩🇪'),
(4, 'Portugal', '🇵🇹'),
(5, 'Reino Unido', '🇬🇧'),
(6, 'Estados Unidos', '🇺🇸'),
(7, 'México', '🇲🇽'),
(8, 'Japón', '🇯🇵'),
(9, 'Corea del Sur', '🇰🇷');

-- Ejemplos para Eventos
INSERT INTO event(id, title, description, theme, date, location, price, capacity, is_private, organizer_id, image_path)
VALUES (next value for gen, 'Evento paella', 'Aprende a cocinar la auténtica paella valenciana paso a paso.', 'España', '2026-03-02 18:00:00', 'FDI UCM Madrid', 5, 20, false, 1, '/img/events/ev_espana.jpg');

INSERT INTO event(id, title, description, theme, date, location, price, capacity, is_private, organizer_id, image_path)
VALUES (next value for gen, 'Evento pizza', 'Evento exclusivo para miembros de la comunidad italiana.', 'Italiana', '2026-03-14 14:30:00', 'Plaza de España, Madrid', 15, 18, true, 1, '/img/events/ev_italia.jpg');

INSERT INTO event(id, title, description, theme, date, location, price, capacity, is_private, organizer_id, image_path)
VALUES (next value for gen, 'Evento sushi', 'Evento exclusivo para miembros de la comunidad japonesa.', 'Asiática', '2026-03-24 20:30:00', 'Callao, Madrid', 25, 10, true, 1, '/img/events/ev_japon.jpg');

--Ejemplos para recetas

--Lista de ingredientes
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Harina de trigo', 'Gluten');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Harina integral', 'Gluten');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Harina de maíz', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Harina de avena', 'Gluten');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Azúcar blanco', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Azúcar moreno', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Sal', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Levadura fresca', 'Gluten');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Levadura seca', 'Gluten');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Huevo', 'Huevo');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Clara de huevo', 'Huevo');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Yema de huevo', 'Huevo');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Leche entera', 'Lactosa');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Leche desnatada', 'Lactosa');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Leche de soja', 'Soja');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Leche de almendra', 'Frutos secos');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Mantequilla', 'Lactosa');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Margarina', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Aceite de oliva', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Aceite de girasol', '');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pollo', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pechuga de pollo', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Muslo de pollo', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Carne de ternera', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Carne de cerdo', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Carne picada', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Jamón serrano', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Bacon', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Chorizo', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Salchicha', '');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Salmón', 'Pescado');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Atún', 'Pescado');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Merluza', 'Pescado');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Bacalao', 'Pescado');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Sardina', 'Pescado');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Gamba', 'Marisco');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Langostino', 'Marisco');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Mejillón', 'Marisco');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Calamar', 'Marisco');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Arroz blanco', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Arroz integral', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pasta', 'Gluten');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Espaguetis', 'Gluten');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Macarrones', 'Gluten');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Fideos', 'Gluten');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Quinoa', '' );
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Cuscús', 'Gluten');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Tomate', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Tomate triturado', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Cebolla', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Ajo', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pimiento rojo', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pimiento verde', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Zanahoria', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Patata', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Calabacín', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Berenjena', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pepino', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Lechuga', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Espinaca', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Brócoli', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Coliflor', '');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Manzana', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Plátano', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Naranja', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Limón', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Fresa', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Melocotón', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pera', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Sandía', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Melón', '');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Queso', 'Lactosa');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Queso cheddar', 'Lactosa');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Queso mozzarella', 'Lactosa');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Queso azul', 'Lactosa');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Yogur', 'Lactosa');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Nata', 'Lactosa');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Chocolate negro', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Chocolate con leche', 'Lactosa');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Cacao en polvo', '');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Almendra', 'Frutos secos');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Nuez', 'Frutos secos');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Avellana', 'Frutos secos');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pistacho', 'Frutos secos');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Soja', 'Soja');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Tofu', 'Soja');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Garbanzos', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Lentejas', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Judías blancas', '');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pan blanco', 'Gluten');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pan integral', 'Gluten');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Miel', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Vinagre', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Mostaza', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Ketchup', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Mayonesa', 'Huevo');

INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pimienta', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Orégano', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Albahaca', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Perejil', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Comino', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Curry', '');
INSERT INTO ingredient(id, name, allergens) VALUES (next value for gen, 'Pimentón', '');

-- Ejemplos para reservas
INSERT INTO reserve(id, attendee_id, event_id)
VALUES (next value for gen, 1, (SELECT id FROM event WHERE title = 'Evento paella' LIMIT 1));

INSERT INTO reserve(id, attendee_id, event_id)
VALUES (next value for gen, 2, (SELECT id FROM event WHERE title = 'Evento paella' LIMIT 1));

-- Ejemplos para mensajes
-- Mensaje normal de chat
INSERT INTO message(id, sender_id, recipient_id, text, date_sent, complain_type)
VALUES (next value for gen, 1, null, '¿Alguien sabe si hay que llevar delantal?', '2026-02-28 12:00:00', 'NONE');

-- Queja sobre un usuario (Del usuario 2 al admin)
INSERT INTO message(id, sender_id, recipient_id, text, date_sent, complain_type, reference_id)
VALUES (next value for gen, 2, 1, 'Este usuario está insultando en el chat de sushi.', '2026-02-28 15:20:00', 'USER', 3);

-- Queja sobre una receta
INSERT INTO message(id, sender_id, recipient_id, text, date_sent, complain_type, reference_id)
VALUES (next value for gen, 1, 1, 'Esta receta contiene fotos inapropiadas.', '2026-02-28 19:00:00', 'RECIPE', 5);

-- Ejemplos para comunidades
INSERT INTO community(country_id, id, owner_id, title, description) VALUES 
(0, 1275, 2, 'Amantes de la comida valenciana', 'Comunidad dedicada a los amantes de la comida valenciana'),
(1, 1276, 2, 'Sushi lovers', 'Solo admitimos a amantes del sushi'),
(0, 1951, 1, 'Comunidad 1', 'Comunidad 1'),
(1, 1952, 1, 'Comunidad 2', 'Comunidad 2'),
(2, 1953, 1, 'Comunidad 3', 'Comunidad 3'),
(3, 1954, 1, 'Comunidad 4', 'Comunidad 4'),
(4, 1955, 1, 'Comunidad 5', 'Comunidad 5'),
(4, 1956, 1, 'Comunidad 6', 'Comunidad 6'),
(5, 1957, 1, 'Comunidad 7', 'Comunidad 7'),
(6, 1958, 1, 'Comunidad 8', 'Comunidad 8');