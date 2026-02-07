-- Setup script for MariaDB
-- 1. Create database
CREATE DATABASE IF NOT EXISTS gestordatos;
-- 2. Configure root user password to 'root'
ALTER USER 'root'@'localhost' IDENTIFIED VIA mysql_native_password USING PASSWORD('root');
-- 3. Flush privileges
FLUSH PRIVILEGES;
