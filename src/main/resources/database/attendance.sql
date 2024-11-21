CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    punch_in_time DATETIME,
    punch_out_time DATETIME,
    total_hours DOUBLE,
    record_status VARCHAR(255),
    created_by VARCHAR(255),
    created_date VARCHAR(255),
    last_updated_by VARCHAR(255),
    last_updated_date VARCHAR(255)
);
