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

SELECT * FROM web_punch;

SELECT * FROM web_punch WHERE punch_time BETWEEN '2024-11-26 00:00:00' AND '2024-11-27 23:59:59';
