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
VALUES (next value for gen, 'Evento paella', 'Aprende a cocinar la auténtica paella valenciana paso a paso.', 'España', '2026-03-02 18:00:00', 'FDI UCM Madrid', 5.0, 20, false, 1, '/img/events/ev_espana.jpg');

INSERT INTO event(id, title, description, theme, date, location, price, capacity, is_private, organizer_id, image_path)
VALUES (next value for gen, 'Evento pizza', 'Evento exclusivo para miembros de la comunidad italiana.', 'Italiana', '2026-03-14 14:30:00', 'Plaza de España, Madrid', 15.0, 18, true, 1, '/img/events/ev_italia.jpg');

INSERT INTO event(id, title, description, theme, date, location, price, capacity, is_private, organizer_id, image_path)
VALUES (next value for gen, 'Evento sushi', 'Evento exclusivo para miembros de la comunidad japonesa.', 'Asiática', '2026-03-24 20:30:00', 'Callao, Madrid', 25.0, 10, true, 1, '/img/events/ev_japon.jpg');

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
INSERT INTO message(id, sender_id, recipient_id, text, date_sent, complain_type)
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