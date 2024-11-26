CREATE TABLE shift_details (
    id int AUTO_INCREMENT PRIMARY KEY,
    shift_name VARCHAR(50) NOT NULL,
    shift_type VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_dt DATE NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    record_status VARCHAR(50) NOT NULL,
    updated_dt DATE NOT NULL,
    updated_by VARCHAR(50) NOT NULL
);

INSERT INTO shift_details (shift_name, shift_type, start_time, end_time, created_dt, created_by, record_status, updated_dt, updated_by) 
VALUES 
('Shift 01', 'NIGHT', '22:00', '08:00', '2024-11-24', 'Admin', 'ACTIVE', '2024-11-24', 'Admin');

SELECT * FROM shift_details;