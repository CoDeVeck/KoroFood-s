/*
CREATE DATABASE mi_base
WITH ENCODING 'UTF8'
LC_COLLATE = 'es_PE.UTF-8'
LC_CTYPE = 'es_PE.UTF-8';

SHOW SERVER_ENCODING;
SHOW CLIENT_ENCODING;

SET CLIENT_ENCODING TO 'UTF8';

*/

INSERT INTO TB_TEMATICA (NOMBRE) VALUES 
('Animes'),
('Peliculas'),
('Manwhas BL'),
('Infantiles'),
('Comics'),
('Series'),
('Videojuegos'),
('Cultural');


INSERT INTO TB_EVENTO (NOMBRE, DESCRIPCION, ID_TEMATICA, FECHA_HORA, COSTO_EVENTO, ESTADO) VALUES
('Noche de Animes', 'Un evento para fans de los animes más populares.', 1, '2026-02-25 19:30:00', 50.00, 'ACTIVO'),
('Cine Temático: Películas', 'Proyección de películas icónicas en nuestro salón de eventos.', 2, '2026-03-15 18:00:00', 60.00, 'ACTIVO'),
('Tarde de Comics y Héroes', 'Celebra a tus héroes favoritos con nosotros.', 5, '2026-04-05 17:00:00', 45.00, 'ACTIVO'),
('Aventura Infantil', 'Una tarde mágica para los más pequeños.', 4, '2026-05-10 15:00:00', 30.00, 'ACTIVO'),
('Noche BL', 'Celebración de los mejores Manwhas BL del momento.', 3, '2026-02-20 20:00:00', 55.00, 'ACTIVO'),
('Maratón de Series', 'Disfruta de las series más aclamadas.', 6, '2026-03-28 16:00:00', 40.00, 'ACTIVO'),
('Gaming Night', 'Torneo de videojuegos con premios especiales.', 7, '2026-04-18 19:00:00', 65.00, 'ACTIVO'),
('Noche Cultural', 'Celebración de la cultura peruana y mundial.', 8, '2026-05-22 18:30:00', 35.00, 'ACTIVO');

INSERT INTO TB_EVENTO_MESA (ID_EVENTO, ID_MESA, FECHA_DESDE, FECHA_HASTA) VALUES 
(1, 1, '2026-02-25 19:00:00', '2026-02-25 23:00:00'),
(1, 2, '2026-02-25 19:00:00', '2026-02-25 23:00:00'),
(1, 3, '2026-02-25 19:00:00', '2026-02-25 23:00:00'),
(2, 13, '2026-03-15 17:30:00', '2026-03-15 22:00:00'),
(2, 14, '2026-03-15 17:30:00', '2026-03-15 22:00:00'),
(3, 4, '2026-04-05 16:30:00', '2026-04-05 20:00:00'),
(4, 15, '2026-05-10 14:30:00', '2026-05-10 18:00:00'),
(4, 16, '2026-05-10 14:30:00', '2026-05-10 18:00:00');