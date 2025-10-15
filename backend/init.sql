-- UES Gaming Database Initialization Script
-- This script ensures the database and necessary tables exist

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS svtkvt;
USE svtkvt;

-- Note: Tables will be created automatically by Spring Boot JPA
-- This is just a reference of what the Centar table should look like

-- Example of Centar table structure (created by JPA)
-- CREATE TABLE IF NOT EXISTS centri (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     ime VARCHAR(255) NOT NULL,
--     ophis TEXT NOT NULL,
--     datum_kreacije DATETIME NOT NULL,
--     adresa VARCHAR(255) NOT NULL,
--     grad VARCHAR(255) NOT NULL,
--     rating DOUBLE,
--     active BOOLEAN NOT NULL DEFAULT TRUE,
--     image_path VARCHAR(500),
--     pdf_path VARCHAR(500),
--     pdf_content TEXT
-- );

-- Sample data for testing (optional)
-- INSERT INTO centri (ime, ophis, datum_kreacije, adresa, grad, rating, active) VALUES
-- ('Спортски центар Партизан', 'Модеран спортски центар са одличним теренима', NOW(), 'Хумска 1', 'Београд', 4.5, TRUE),
-- ('Arena Sportski Centar', 'Vrhunski objekti za različite sportove', NOW(), 'Narodnih heroja 23', 'Novi Sad', 4.8, TRUE),
-- ('Кошаркашки клуб Црвена звезда', 'Професионални кошаркашки терени', NOW(), 'Љутице Богдана 1а', 'Београд', 4.7, TRUE);

-- Note: Remember to run the reindex endpoint after adding data
-- POST http://localhost:8080/api/centri/reindex

SHOW TABLES;
