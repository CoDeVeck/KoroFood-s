
-- INSERTS USER SERVICE
INSERT INTO TB_ROL (DESCRIPCION) VALUES 
('Administrador'),
('Cliente'),
('Mesero'),
('Recepcionista');

INSERT INTO TB_DISTRITO (NOMBRE) VALUES 
('Miraflores'),
('San Isidro'),
('Surco'),
('La Molina'),
('Barranco');

INSERT INTO TB_USUARIO (NOMBRES, APE_PATERNO, APE_MATERNO, CORREO, CLAVE, NRO_DOC, TELEFONO, ID_DISTRITO, ID_ROL) VALUES 
('Carlos', 'Pérez', 'García', 'carlos.admin@restaurant.com', 'clave123', '12345678', '987654321', 1, 1),
('María', 'López', 'Martínez', 'maria.cliente@gmail.com', 'clave123', '87654321', '912345678', 2, 2),
('Juan', 'Rodríguez', 'Silva', 'juan.mesero@restaurant.com', 'clave123', '45678912', '923456789', 3, 3),
('Ana', 'Torres', 'Vega', 'ana.cajera@restaurant.com', 'clave123', '78945612', '934567890', 1, 4),
('Pedro', 'Sánchez', 'Ramos', 'pedro.cliente@gmail.com', 'clave123', '32165498', '945678901', 4, 2);