-- INSERTS EVENT SERVICE
INSERT INTO TB_TEMATICA (DESCRIPCION) VALUES 
('Música en Vivo'),
('Gastronómico'),
('Cultural'),
('Anime'),
('Gamer'),
('Caricatura'),
('Fantasia');


INSERT INTO TB_EVENTO (NOMBRE, DESCRIPCION, ID_TEMATICA, FECHA, HORA, PRECIO, AFORO, IMAGEN) VALUES 
('Noche de Jazz', 'Presentación de banda de jazz en vivo con cena incluida', 1, '2026-02-14', '20:00:00', 120.00, 50, 'jazz_night.jpg'),
('Festival Criollo', 'Celebración de la comida criolla peruana', 2, '2026-03-01', '18:00:00', 80.00, 80, 'festival_criollo.jpg'),
('Peña Folclórica', 'Show de música y danzas tradicionales del Perú', 3, '2026-02-28', '19:00:00', 95.00, 60, 'pena.jpg');

INSERT INTO TB_EVENTO_MESA (ID_EVENTO, ID_MESA, FECHA_DESDE, FECHA_HASTA) VALUES 
(1, 5, '2026-02-14', '2026-02-14'),
(1, 6, '2026-02-14', '2026-02-14'),
(2, 7, '2026-03-01', '2026-03-01'),
(2, 10, '2026-03-01', '2026-03-01'),
(3, 5, '2026-02-28', '2026-02-28'),
(3, 6, '2026-02-28', '2026-02-28');