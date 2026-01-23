/*
CREATE DATABASE mi_base
WITH ENCODING 'UTF8'
LC_COLLATE = 'es_PE.UTF-8'
LC_CTYPE = 'es_PE.UTF-8';

SHOW SERVER_ENCODING;
SHOW CLIENT_ENCODING;

SET CLIENT_ENCODING TO 'UTF8';
*/

INSERT INTO TB_ETIQUETA (NOMBRE, DESCRIPCION) VALUES
('Picante', 'Contiene ingredientes picantes'),
('Vegetariano', 'Sin carne ni pescado'),
('Vegano', 'Sin productos de origen animal'),
('Sin Gluten', 'Apto para celíacos'),
('Especial Niños', 'Porciones y sabores para niños'),
('Recomendado', 'Plato más vendido'),
('Nuevo', 'Nuevo en el menú'),
('Saludable', 'Opción nutritiva y balanceada');

INSERT INTO TB_PLATO_ETIQUETAS (ID_PLATO, ID_ETIQUETA) VALUES
(1, 2), (1, 8), -- Tarta vegetariana y saludable
(3, 6), -- Tequeños recomendados
(4, 2), (4, 4), -- Sopa vegetariana sin gluten
(5, 6), -- Onigiri recomendado
(7, 5), (7, 8), -- Fideos especial niños y saludable
(8, 5), (8, 6), -- Empanadas especial niños recomendado
(9, 1), (9, 6), -- Bento picante recomendado
(10, 6), (10, 7), -- Ramen recomendado y nuevo
(13, 1), -- Plato de batalla picante
(17, 8), (17, 6), -- Lomo saltado saludable recomendado
(18, 8), (18, 4); -- Ceviche saludable sin gluten