/*
SHOW SERVER_ENCODING;
SHOW CLIENT_ENCODING;

*/
SET CLIENT_ENCODING TO 'UTF8';

INSERT INTO TB_PAGO (ID_USUARIO, ID_RESERVA, ID_PEDIDO, TIPO_PAGO, MONTO, METODO_PAGO, FECHA_PAGO, ESTADO, OBSERVACIONES) VALUES
(5, 1, NULL, 'DR', 15.00, 'Yape', '2026-02-20 10:30:00', 'PAG', 'Depósito para evento Noche de Animes'),
(5, 2, NULL, 'DR', 15.00, 'Plin', '2026-03-10 14:15:00', 'PAG', 'Depósito para evento Cine Temático'),
(5, 3, NULL, 'DR', 15.00, 'Tarjeta', '2026-02-10 11:00:00', 'PAG', 'Depósito para almuerzo familiar'),
(5, 4, NULL, 'DR', 15.00, 'Yape', '2026-02-15 16:45:00', 'PAG', 'Depósito para cena romántica'),

(2, NULL, 1, 'PP', 120.00, 'Tarjeta', '2026-02-25 21:30:00', 'PAG', 'Pago completo pedido evento anime'),
(2, NULL, 2, 'PP', 95.50, 'Yape', '2026-02-15 14:45:00', 'PAG', 'Pago completo almuerzo familiar'),
(2, NULL, 4, 'PP', 75.00, 'Efectivo', '2026-02-18 20:15:00', 'PAG', 'Pago en efectivo');