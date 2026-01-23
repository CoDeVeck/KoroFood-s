/*
CREATE DATABASE mi_base
WITH ENCODING 'UTF8'
LC_COLLATE = 'es_PE.UTF-8'
LC_CTYPE = 'es_PE.UTF-8';

SHOW SERVER_ENCODING;
SHOW CLIENT_ENCODING;

SET CLIENT_ENCODING TO 'UTF8';

*/


INSERT INTO TB_ROL (DESCRIPCION) VALUES
('A'), -- Administrador
('R'), -- Recepcionista
('M'), -- Mozo
('C'); -- Cliente

INSERT INTO TB_DISTRITO (NOMBRE) VALUES 
('Ancón'),
('Ate'),
('Barranco'),
('Breña'),
('Carabayllo'),
('Chaclacayo'),
('Chorrillos'),
('Cieneguilla'),
('Comas'),
('El Agustino'),
('Independencia'),
('Jesús María'),
('La Molina'),
('La Victoria'),
('Lima'),
('Lince'),
('Los Olivos'),
('Lurigancho'),
('Lurín'),
('Magdalena del Mar'),
('Miraflores'),
('Pachacámac'),
('Pucusana'),
('Pueblo Libre'),
('Puente Piedra'),
('Punta Hermosa'),
('Punta Negra'),
('Rímac'),
('San Bartolo'),
('San Borja'),
('San Isidro'),
('San Juan de Lurigancho'),
('San Juan de Miraflores'),
('San Luis'),
('San Martín de Porres'),
('San Miguel'),
('Santa Anita'),
('Santa María del Mar'),
('Santa Rosa'),
('Santiago de Surco'),
('Surquillo'),
('Villa El Salvador'),
('Villa María del Triunfo');

INSERT INTO TB_USUARIO (NOMBRES, APE_PATERNO, APE_MATERNO, CORREO, CLAVE, TIPO_DOC, NRO_DOC, DIRECCION, ID_DISTRITO, TELEFONO, ID_ROL) VALUES
('Carlos Alberto', 'Mendoza', 'Quispe', 'carlos.mendoza@restaurant.com', '$2a$10$abcdefghijklmnopqrstuv', 'DNI', '45678901', 'Av. Larco 1234', 21, '987654321', 1),
('María Elena', 'Rodríguez', 'Pérez', 'maria.rodriguez@restaurant.com', '$2a$10$bcdefghijklmnopqrstuvw', 'DNI', '56789012', 'Jr. Las Flores 567', 30, '987654322', 2),
('Juan Carlos', 'García', 'Torres', 'juan.garcia@restaurant.com', '$2a$10$cdefghijklmnopqrstuvwx', 'DNI', '67890123', 'Av. Brasil 890', 15, '987654323', 3),
('Ana Lucía', 'Flores', 'Vásquez', 'ana.flores@email.com', '$2a$10$defghijklmnopqrstuvwxy', 'DNI', '78901234', 'Calle Los Pinos 345', 40, '987654324', 4);