-- Create database if not exists
CREATE DATABASE IF NOT EXISTS gestordatos;
-- STATEMENT_END

USE gestordatos;
-- STATEMENT_END

-- Drop tables if they exist to start fresh
DROP TABLE IF EXISTS prestamo;
-- STATEMENT_END
DROP TABLE IF EXISTS socio;
-- STATEMENT_END

-- Create Socio table
CREATE TABLE socio (
    id INT(11) NOT NULL AUTO_INCREMENT,
    dni VARCHAR(9) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    nacimiento DATE NOT NULL,
    mas_prestamos TINYINT(1) DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY dni (dni),
    UNIQUE KEY email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- STATEMENT_END

-- Insert initial Socio data
INSERT INTO socio(dni, nombre, email, nacimiento, mas_prestamos)
VALUES ('77777777d', 'pepito', 'pepito@gmail.com', '1989-05-27', 0);
-- STATEMENT_END

-- Create Prestamo table
CREATE TABLE prestamo (
    id INT(11) NOT NULL AUTO_INCREMENT,
    dia_prestamo DATE NOT NULL,
    dia_vencimiento DATE DEFAULT NULL,
    importe FLOAT NOT NULL,
    esta_pagado TINYINT(1) DEFAULT 0,
    id_socio INT(11) DEFAULT NULL,
    limite_prestamo INT(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY id_socio (id_socio),
    CONSTRAINT fk_prestamo_socio FOREIGN KEY (id_socio) REFERENCES socio (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- STATEMENT_END

-- Insert initial Prestamo data
INSERT INTO prestamo(dia_prestamo, importe, id_socio, limite_prestamo)
VALUES ('2026-02-05', 50.5, 1, 100);
-- STATEMENT_END

-- Triggers
CREATE TRIGGER check_email_valido
BEFORE INSERT ON socio
FOR EACH ROW
BEGIN
    IF (NEW.email NOT LIKE '%@%._%' ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El email debe tener formato válido (ej: usuario@dominio.com)';
    END IF;
END;
-- STATEMENT_END
