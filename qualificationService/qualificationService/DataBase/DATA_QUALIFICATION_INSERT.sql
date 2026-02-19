INSERT INTO TB_CALIFICACION 
(ID_USUARIO, TIPO_ENTIDAD, ID_ENTIDAD, PUNTUACION, COMENTARIO, ESTADO)
VALUES
-- Plato 1
(1, 'PLATO', 1, 5, 'Brutal, volvería a pedirlo mil veces', 'ACT'),
(2, 'PLATO', 1, 4, 'Muy rico pero llegó un poco frío', 'ACT'),
(3, 'PLATO', 1, 5, 'Top tier, recomendado', 'ACT'),

-- Plato 2
(1, 'PLATO', 2, 3, 'Normalito, nada wow', 'ACT'),
(4, 'PLATO', 2, 4, 'Buen sabor y buena porción', 'ACT'),
(5, 'PLATO', 2, 2, 'No me convenció mucho', 'ACT'),

-- Plato 3
(2, 'PLATO', 3, 5, 'Delicioso, sabor casero', 'ACT'),
(3, 'PLATO', 3, 5, 'Perfecto, 10/10', 'ACT'),
(6, 'PLATO', 3, 4, 'Muy bueno', 'ACT'),

-- Plato 4
(7, 'PLATO', 4, 1, 'Terrible experiencia 😭', 'ACT'),
(8, 'PLATO', 4, 2, 'Podría mejorar bastante', 'ACT'),

-- Plato 5
(9,  'PLATO', 5, 5, 'Mi favorito de todos', 'ACT'),
(10, 'PLATO', 5, 5, 'Excelente presentación', 'ACT'),
(11, 'PLATO', 5, 4, 'Muy rico', 'ACT'),

-- Estados diferentes para pruebas
(12, 'PLATO', 3, 3, 'Regular', 'INA'),
(13, 'PLATO', 2, 1, 'Comentario ofensivo', 'REP');