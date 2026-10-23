CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(60) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL
);

-- Optional sample records
INSERT IGNORE INTO students (name, department, email, phone) VALUES
('Aarav Patil', 'Computer Engineering', 'aarav.patil@example.com', '9876543210'),
('Priya Shah', 'Information Technology', 'priya.shah@example.com', '9876501234'),
('Rahul Jadhav', 'Mechanical Engineering', 'rahul.jadhav@example.com', '9765432108');

-- Useful SQL queries
SELECT * FROM students;
SELECT * FROM students WHERE id = 1;
SELECT * FROM students WHERE name LIKE '%Priya%';
UPDATE students SET department = 'Computer Engineering' WHERE id = 2;
DELETE FROM students WHERE id = 3;
SELECT department, COUNT(*) AS total_students
FROM students GROUP BY department;
