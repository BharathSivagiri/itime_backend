-- Creating the employee_details table
CREATE TABLE employee_details (
  id int AUTO_INCREMENT PRIMARY KEY,
  emp_name varchar(50) NOT NULL,
  emp_code varchar(50) NOT NULL,
  created_by varchar(50) NOT NULL, 
  created_dt date NOT NULL,
  emp_status varchar(50) DEFAULT NULL,
  updated_dt date NOT NULL,
  updated_by varchar(50) NOT NULL
);

-- Creating the web_punch table
CREATE TABLE web_punch (
    id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id INT NOT NULL,
    punch_time timestamp NOT NULL,
    punch_type varchar(50) DEFAULT NULL,
    created_by varchar(50) NOT NULL, 
	created_dt date NOT NULL,
	status varchar(50) DEFAULT NULL,
	updated_dt date NOT NULL,
	updated_by varchar(50) NOT NULL,
    CONSTRAINT emp_id FOREIGN KEY (emp_id) REFERENCES employee_details (id)
);

-- Creating the shift_details table
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

-- Creating the shift_roster_details table
CREATE TABLE shift_roster_details (
    id int AUTO_INCREMENT PRIMARY KEY,
    emp_id INTEGER NOT NULL,
    shift_id INTEGER,
    shift_date DATE NOT NULL,
    created_dt DATE NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    record_status VARCHAR(50) NOT NULL,
    updated_dt DATE NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    FOREIGN KEY (emp_id) REFERENCES employee_details(id),
    FOREIGN KEY (shift_id) REFERENCES shift_details(id)
);

-- Insert default values
INSERT INTO employee_details (emp_name, emp_code, created_by, created_dt, emp_status, updated_dt, updated_by)
VALUES ('John Doe', 'EMP001', 'System', CURDATE(), 'ACTIVE', CURDATE(), 'System');

INSERT INTO shift_details (shift_name, shift_type, start_time, end_time, created_dt, created_by, record_status, updated_dt, updated_by)
VALUES ('Day Shift', 'DAY', '09:00:00', '17:00:00', CURDATE(), 'Admin', 'ACTIVE', CURDATE(), 'Admin');

INSERT INTO shift_roster_details (emp_id, shift_id, shift_date, created_dt, created_by, record_status, updated_dt, updated_by)
VALUES (1, 1, CURDATE(), CURDATE(), 'Admin', 'ACTIVE', CURDATE(), 'Admin');
