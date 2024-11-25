CREATE TABLE shift_roster_details (
    id int AUTO_INCREMENT PRIMARY KEY,
    emp_id INTEGER NOT NULL,
    shift_id INTEGER NOT NULL,
    shift_date DATE NOT NULL,
    shift_status VARCHAR(50) NOT NULL,
    created_dt DATE NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    record_status VARCHAR(50) NOT NULL,
    updated_dt DATE NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    FOREIGN KEY (emp_id) REFERENCES employee_details(id),
    FOREIGN KEY (shift_id) REFERENCES shift_details(id)
);

INSERT INTO shift_roster_details (emp_id, shift_id, shift_date, shift_status, created_dt, created_by, record_status, updated_dt, updated_by) 
VALUES (1, 1, '2024-11-25', 'SCHEDULED', '2024-11-24', 'Admin', 'ACTIVE', '2024-11-24', 'Admin');

SELECT * FROM shift_roster_details;

