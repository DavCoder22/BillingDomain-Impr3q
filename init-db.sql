-- Script de inicialización de bases de datos para microservicios
-- Este script crea las bases de datos necesarias para los microservicios

-- Crear base de datos para quotation-service
CREATE DATABASE quotation_db;

-- Crear base de datos para payment-service
CREATE DATABASE payment_db;

-- Crear base de datos para invoice-service
CREATE DATABASE invoice_db;

-- Conceder permisos al usuario print3d
GRANT ALL PRIVILEGES ON DATABASE quotation_db TO print3d;
GRANT ALL PRIVILEGES ON DATABASE payment_db TO print3d;
GRANT ALL PRIVILEGES ON DATABASE invoice_db TO print3d;

-- Conectar a cada base de datos y conceder permisos en el esquema público
\c quotation_db;
GRANT ALL ON SCHEMA public TO print3d;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO print3d;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO print3d;

\c payment_db;
GRANT ALL ON SCHEMA public TO print3d;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO print3d;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO print3d;

\c invoice_db;
GRANT ALL ON SCHEMA public TO print3d;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO print3d;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO print3d; 