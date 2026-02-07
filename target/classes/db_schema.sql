-- Drop tables if they exist to start fresh
DROP TABLE IF EXISTS prestamo;
DROP TABLE IF EXISTS socio;

-- Create Socio table
CREATE TABLE socio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(9) UNIQUE NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    nacimiento DATE NOT NULL,
    mas_prestamos BOOLEAN DEFAULT FALSE
);

-- Insert initial Socio data
INSERT INTO socio(dni, nombre, email, nacimiento)
VALUES ('77777777d', 'pepito', 'pepito@gmail.com', '1989-05-27');

-- Create Prestamo table
CREATE TABLE prestamo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dia_prestamo DATE NOT NULL,
    dia_vencimiento DATE,
    importe FLOAT NOT NULL,
    esta_pagado BOOLEAN DEFAULT FALSE,
    id_socio INT,
    FOREIGN KEY (id_socio) REFERENCES socio(id)
);

-- Insert initial Prestamo data
INSERT INTO prestamo(dia_prestamo, importe, id_socio)
values ('2026-02-05', '50.5', '1');

-- Triggers
DELIMITER ;;
/*!50003 CREATE TRIGGER check_email_valido
BEFORE INSERT ON socio
FOR EACH ROW
BEGIN
    IF (NEW.email NOT LIKE '%@%._%' ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El email debe tener formato válido (ej: usuario@dominio.com)';
    END IF;
END */;;
DELIMITER ;
