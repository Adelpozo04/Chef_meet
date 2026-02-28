-- insert admin (username a, password aa)
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (1, TRUE, 'ADMIN,USER', 'a',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (2, TRUE, 'USER', 'b',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');

-- start id numbering from a value that is larger than any assigned above
ALTER SEQUENCE "PUBLIC"."GEN" RESTART WITH 1024;

-- Ejemplos para Eventos
INSERT INTO event(id, title, description, theme, date, location, price, capacity, is_private, organizer_id)
VALUES (next value for gen, 'Evento paella', 'Aprende a cocinar la auténtica paella valenciana paso a paso.', 'España', '2026-03-02 18:00:00', 'FDI UCM Madrid', 5.0, 20, false, 1);

INSERT INTO event(id, title, description, theme, date, location, price, capacity, is_private, organizer_id)
VALUES (next value for gen, 'Evento sushi', 'Evento exclusivo para miembros de la comunidad japonesa.', 'Asiática', '2026-03-24 20:30:00', 'Callao, Madrid', 25.0, 10, true, 1);

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