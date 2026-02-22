/*
SHOW SERVER_ENCODING;
SHOW CLIENT_ENCODING;

*/
SET CLIENT_ENCODING TO 'UTF8';


INSERT INTO TB_PLATO (NOMBRE, PRECIO, STOCK, TIPO_PLATO, IMAGEN) VALUES
-- Entradas (E)
('Tarta de la Abuela (Ratatouille)', 28.00, 50, 'E','https://res.cloudinary.com/dvacublsz/image/upload/v1771720383/KoroFoods/Menu/nhzddmqkgz953xezujb8.png'),
('Gyozas del Dragon (Dragon Ball)', 25.00, 60, 'E',null),
('Tequeños Galácticos (Star Wars)', 22.00, 45, 'E','https://res.cloudinary.com/dvacublsz/image/upload/v1771720269/KoroFoods/Menu/wnfvbhphsk5nwga6dtas.png'),
-- Entradas Peruanas (E)
('Causa Limeña Rellena', 18.00, 55, 'E',''),
('Anticuchos de Corazón', 22.00, 50, 'E',''),
('Papa a la Huancaína', 15.00, 60, 'E',''),
('Tiradito de Pescado', 25.00, 40, 'E',''),
('Choritos a la Chalaca', 20.00, 45, 'E',''),
('Solterito Arequipeño', 16.00, 50, 'E',''),
('Ocopa Arequipeña', 17.00, 48, 'E',''),
-- Segundos/Platos Principales (S)
('Sopa de Champiñones (Mario Bros)', 25.00, 40, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720442/KoroFoods/Menu/dgsmyw2vcnfivd3osyox.png'),
('Onigiri de Pescado (One Piece)', 20.00, 55, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720506/KoroFoods/Menu/z4trykmma2uz5i225akc.png'),
('Tacos de la Araña (Spiderman)', 28.00, 50, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720476/KoroFoods/Menu/ms5adwvgakvvuv4kzcwh.png'),
('Fideos de la Alegría (Intensamente)', 23.00, 60, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720536/KoroFoods/Menu/zlkoja3uzp5ceaypufru.png'),
('Empanadas del Ogro (Shrek)', 25.00, 45, 'S',null),
('Bento del Asesino (Death Note)', 45.00, 35, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720578/KoroFoods/Menu/lwcq25odswxiui7ntetq.png'),
('Ramen de la Aldea (Naruto)', 35.00, 50, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720602/KoroFoods/Menu/sncdsmkqwnkffqbriebm.png'),
('Hamburguesa del Tiempo (Interstellar)', 48.00, 30, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720620/KoroFoods/Menu/mctcrd3y0eui94lk06ks.png'),
('Coctel Ilusionista (Los Ilusionistas)', 22.00, 40, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720640/KoroFoods/Menu/qelq4kdcmhgnwdh1mx1h.png'),
('Plato de la Batalla (Kratos)', 55.00, 25, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720659/KoroFoods/Menu/wpbfkjq77kwls0tbzao1.png'),
('Salchipapas del Espacio (Solar Opposites)', 28.00, 45, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720698/KoroFoods/Menu/fjpc98s70zqgh3d8bpgq.png'),
('Pollo Hermanos (Breaking Bad)', 42.00, 35, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720724/KoroFoods/Menu/nlpr3kvgqvcs2gbtaplc.png'),
('Alitas de pollo (Batman)', 35.00, 40, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720746/KoroFoods/Menu/eoj64qnqq5hbmckctkdh.png'),
('Lomo Saltado del Héroe (Perú)', 38.00, 45, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720776/KoroFoods/Menu/jpn6qals8n7prpusxqbe.png'),
('Ceviche Legendario (One Piece)', 42.00, 30, 'S','https://res.cloudinary.com/dvacublsz/image/upload/v1771720794/KoroFoods/Menu/ldwfjwoo7evxpvlto7up.png'),
-- Segundos/Platos Principales Peruanos (S)
('Chupe de Camarones', 32.00, 35, 'S',''),
('Parihuela Marina', 28.00, 40, 'S',''),
('Sopa Seca Chinchana', 24.00, 45, 'S',''),
('Shambar Trujillano', 22.00, 38, 'S',''),
('Chilcano de Pescado', 18.00, 50, 'S',''),
('Ají de Gallina Criollo', 28.00, 50, 'S',''),
('Arroz con Pato a la Norteña', 35.00, 40, 'S',''),
('Carapulcra con Sopa Seca', 32.00, 42, 'S',''),
('Rocoto Relleno Arequipeño', 30.00, 38, 'S',''),
('Seco de Cabrito con Frejoles', 36.00, 35, 'S',''),
('Tacu Tacu con Bistec', 32.00, 45, 'S',''),
('Pachamanca a la Olla', 40.00, 30, 'S',''),
('Chicharrón de Chancho con Camote', 28.00, 48, 'S',''),
('Pollo a la Brasa con Papas', 35.00, 55, 'S',''),
('Arroz con Mariscos', 38.00, 40, 'S',''),
('Juane Selvático', 26.00, 42, 'S',''),
('Escabeche de Pescado', 32.00, 38, 'S',''),
-- Postres (P)
('Suspiro a la Limeña', 14.00, 60, 'P',''),
('Mazamorra Morada con Arroz con Leche', 12.00, 65, 'P',''),
('Picarones con Miel de Chancaca', 15.00, 55, 'P',''),
('Turrón de Doña Pepa', 10.00, 70, 'P',''),
('Alfajores Limeños', 8.00, 80, 'P',''),
('Champús Cusqueño', 10.00, 50, 'P',''),
('Ranfañote Arequipeño', 12.00, 45, 'P',''),
('Helado de Lúcuma', 9.00, 75, 'P',''),
('Arroz Zambito', 11.00, 60, 'P',''),
('Flan de Quinua', 10.00, 55, 'P',''),
-- Bebidas (B)
('Chicha Morada Shinigami', 12.00, 100, 'B','https://res.cloudinary.com/dvacublsz/image/upload/v1771720847/KoroFoods/Menu/jgc9ikjboxxw0khbay9u.png'),
('Limonada del Reino Champiñón', 10.00, 100, 'B','https://res.cloudinary.com/dvacublsz/image/upload/v1771720864/KoroFoods/Menu/qywrl1jks5rjtpit5fjq.png'),
('Inca Kola Saiyan', 8.00, 120, 'B','https://res.cloudinary.com/dvacublsz/image/upload/v1771720888/KoroFoods/Menu/xk5kfs7dimszj3l6q1li.png'),
('Jugo de Maracuyá Pokémon', 10.00, 90, 'B','https://res.cloudinary.com/dvacublsz/image/upload/v1771720916/KoroFoods/Menu/q6zxbk0edwjc08v05lul.png'),
('Café de Gotham City', 15.00, 80, 'B','https://res.cloudinary.com/dvacublsz/image/upload/v1771720932/KoroFoods/Menu/l5yoio5hybucbzbthshz.png'),
('Té Verde del Maestro Jedi', 12.00, 70, 'B','https://res.cloudinary.com/dvacublsz/image/upload/v1771720963/KoroFoods/Menu/jwtfq8h4xl2veksz16mh.png'),
-- Bebidas Peruanas (B)
('Chicha Morada Casera', 8.00, 100, 'B',''),
('Emoliente Tradicional', 6.00, 90, 'B',''),
('Refresco de Maracuyá', 7.00, 95, 'B',''),
('Cremolada de Chirimoya', 9.00, 70, 'B',''),
('Jugo de Lúcuma', 8.00, 85, 'B',''),
('Chicha de Jora', 10.00, 60, 'B',''),
('Pisco Sour Clásico', 18.00, 50, 'B',''),
('Chilcano de Pisco', 16.00, 55, 'B',''),
('Mate de Coca', 5.00, 100, 'B',''),
('Café Pasado Peruano', 7.00, 90, 'B','');

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