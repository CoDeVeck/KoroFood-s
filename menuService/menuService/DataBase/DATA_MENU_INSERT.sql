/*
SHOW SERVER_ENCODING;
SHOW CLIENT_ENCODING;

*/
SET CLIENT_ENCODING TO 'UTF8';


INSERT INTO TB_PLATO (NOMBRE, PRECIO, STOCK, TIPO_PLATO) VALUES
-- Entradas (E)
('Tarta de la Abuela (Ratatouille)', 28.00, 50, 'E'),
('Gyozas del Dragon (Dragon Ball)', 25.00, 60, 'E'),
('Tequeños Galácticos (Star Wars)', 22.00, 45, 'E'),
-- Entradas Peruanas (E)
('Causa Limeña Rellena', 18.00, 55, 'E'),
('Anticuchos de Corazón', 22.00, 50, 'E'),
('Papa a la Huancaína', 15.00, 60, 'E'),
('Tiradito de Pescado', 25.00, 40, 'E'),
('Choritos a la Chalaca', 20.00, 45, 'E'),
('Solterito Arequipeño', 16.00, 50, 'E'),
('Ocopa Arequipeña', 17.00, 48, 'E'),
-- Segundos/Platos Principales (S)
('Sopa de Champiñones (Mario Bros)', 25.00, 40, 'S'),
('Onigiri de Pescado (One Piece)', 20.00, 55, 'S'),
('Tacos de la Araña (Spiderman)', 28.00, 50, 'S'),
('Fideos de la Alegría (Intensamente)', 23.00, 60, 'S'),
('Empanadas del Ogro (Shrek)', 25.00, 45, 'S'),
('Bento del Asesino (Death Note)', 45.00, 35, 'S'),
('Ramen de la Aldea (Naruto)', 35.00, 50, 'S'),
('Hamburguesa del Tiempo (Interstellar)', 48.00, 30, 'S'),
('Coctel Ilusionista (Los Ilusionistas)', 22.00, 40, 'S'),
('Plato de la Batalla (Kratos)', 55.00, 25, 'S'),
('Salchipapas del Espacio (Solar Opposites)', 28.00, 45, 'S'),
('Pollo Hermanos (Breaking Bad)', 42.00, 35, 'S'),
('Alitas del Murciélago (Batman)', 35.00, 40, 'S'),
('Lomo Saltado del Héroe (Perú)', 38.00, 45, 'S'),
('Ceviche Legendario (One Piece)', 42.00, 30, 'S'),
-- Segundos/Platos Principales Peruanos (S)
('Chupe de Camarones', 32.00, 35, 'S'),
('Parihuela Marina', 28.00, 40, 'S'),
('Sopa Seca Chinchana', 24.00, 45, 'S'),
('Shambar Trujillano', 22.00, 38, 'S'),
('Chilcano de Pescado', 18.00, 50, 'S'),
('Ají de Gallina Criollo', 28.00, 50, 'S'),
('Arroz con Pato a la Norteña', 35.00, 40, 'S'),
('Carapulcra con Sopa Seca', 32.00, 42, 'S'),
('Rocoto Relleno Arequipeño', 30.00, 38, 'S'),
('Seco de Cabrito con Frejoles', 36.00, 35, 'S'),
('Tacu Tacu con Bistec', 32.00, 45, 'S'),
('Pachamanca a la Olla', 40.00, 30, 'S'),
('Chicharrón de Chancho con Camote', 28.00, 48, 'S'),
('Pollo a la Brasa con Papas', 35.00, 55, 'S'),
('Arroz con Mariscos', 38.00, 40, 'S'),
('Juane Selvático', 26.00, 42, 'S'),
('Escabeche de Pescado', 32.00, 38, 'S'),
-- Postres (P)
('Suspiro a la Limeña', 14.00, 60, 'P'),
('Mazamorra Morada con Arroz con Leche', 12.00, 65, 'P'),
('Picarones con Miel de Chancaca', 15.00, 55, 'P'),
('Turrón de Doña Pepa', 10.00, 70, 'P'),
('Alfajores Limeños', 8.00, 80, 'P'),
('Champús Cusqueño', 10.00, 50, 'P'),
('Ranfañote Arequipeño', 12.00, 45, 'P'),
('Helado de Lúcuma', 9.00, 75, 'P'),
('Arroz Zambito', 11.00, 60, 'P'),
('Flan de Quinua', 10.00, 55, 'P'),
-- Bebidas (B)
('Chicha Morada Shinigami', 12.00, 100, 'B'),
('Limonada del Reino Champiñón', 10.00, 100, 'B'),
('Inca Kola Saiyan', 8.00, 120, 'B'),
('Jugo de Maracuyá Pokémon', 10.00, 90, 'B'),
('Café de Gotham City', 15.00, 80, 'B'),
('Té Verde del Maestro Jedi', 12.00, 70, 'B'),
-- Bebidas Peruanas (B)
('Chicha Morada Casera', 8.00, 100, 'B'),
('Emoliente Tradicional', 6.00, 90, 'B'),
('Refresco de Maracuyá', 7.00, 95, 'B'),
('Cremolada de Chirimoya', 9.00, 70, 'B'),
('Jugo de Lúcuma', 8.00, 85, 'B'),
('Chicha de Jora', 10.00, 60, 'B'),
('Pisco Sour Clásico', 18.00, 50, 'B'),
('Chilcano de Pisco', 16.00, 55, 'B'),
('Mate de Coca', 5.00, 100, 'B'),
('Café Pasado Peruano', 7.00, 90, 'B');

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