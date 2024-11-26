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

INSERT INTO employee_details(emp_name, emp_code, created_by, created_dt, emp_status, updated_dt, updated_by)
VALUES
('John Doe', 'E001', 'System', current_date(), 'ACTIVE', current_date(), 'System');

SELECT * FROM employee_details;