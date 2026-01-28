/*
SHOW SERVER_ENCODING;
SHOW CLIENT_ENCODING;
*/
SET CLIENT_ENCODING TO 'UTF8';


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

INSERT INTO TB_USUARIO (NOMBRES, APE_PATERNO, APE_MATERNO, CORREO, CLAVE, TIPO_DOC, NRO_DOC, DIRECCION, TELEFONO, ID_DISTRITO, ID_ROL) VALUES 
('Carlos', 'Pérez', 'García', 'carlos.admin@restaurant.com', 'clave123', 'DNI', '12345678', 'Direccion 1', '987654321', 1, 1),
('María', 'López', 'Martínez', 'maria.cliente@gmail.com', 'clave123', 'DNI', '87654321', 'Direccion 2', '912345678', 2, 2),
('Juan', 'Rodríguez', 'Silva', 'juan.mesero@restaurant.com', 'clave123', 'DNI', '45678912', 'Direccion 3', '923456789', 3, 3),
('Ana', 'Torres', 'Vega', 'ana.cajera@restaurant.com', 'clave123', 'DNI', '78945612', 'Direccion 4', '934567890', 1, 4),
('Pedro', 'Sánchez', 'Ramos', 'pedro.cliente@gmail.com', 'clave123', 'DNI', '32165498', 'Direccion 5', '945678901', 4, 2);