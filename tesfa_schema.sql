CREATE DATABASE tesfa;
USE tesfa;

CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role ENUM('Donor','Volunteer','Admin'),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE Companies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100)
);

CREATE TABLE Needs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orphanage_id INT,
    category VARCHAR(50),
    description TEXT,
    status ENUM('Pending','Fulfilled'),
    FOREIGN KEY(orphanage_id) REFERENCES Companies(id)
);

CREATE TABLE Donations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    donor_id INT,
    type ENUM('Money','Item'),
    item_name VARCHAR(100),
    quantity INT,
    amount DECIMAL(10,2),
    company_id INT,
    screenshot VARCHAR(255),
    status ENUM('Pending','Approved','Rejected'),
    FOREIGN KEY(donor_id) REFERENCES Users(id),
    FOREIGN KEY(company_id) REFERENCES Companies(id)
);

CREATE TABLE Volunteers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(100),
    status ENUM('Pending','Accepted','Rejected')
);

ALTER TABLE Users ADD COLUMN telegram_username VARCHAR(50) AFTER password;

ALTER TABLE Companies
ADD COLUMN account_number VARCHAR(20) AFTER location,
ADD COLUMN telebirr_phone VARCHAR(20) AFTER account_number,
ADD COLUMN admin_telegram VARCHAR(50) AFTER telebirr_phone,
ADD COLUMN admin_id INT AFTER admin_telegram;

INSERT INTO Users (name, role, email, password, telegram_username)
VALUES
('Meklit Mulugeta', 'Admin', 'meklit@gmail.com', '0924781946me', 'titoo47'),
('Melat Fekadu', 'Admin', 'melat@gmail.com', '0939519294me', 'M_lolli'),
('Meron', 'Admin', 'meron@gmail.com', '0953419467me', 'amor_fa_t_i')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  role = VALUES(role),
  password = VALUES(password),
  telegram_username = VALUES(telegram_username);

ALTER TABLE Companies ADD FOREIGN KEY (admin_id) REFERENCES Users(id);

INSERT INTO Companies (name, location, account_number, telebirr_phone, admin_telegram, admin_id)
VALUES
('Hope for Children in Ethiopia', 'Addis Ababa', '1000591197642', '0939519294', 'titoo47', 1),
('Muday Charity', 'Addis Ababa', '1000587632111', '0953419467', 'amor_fa_t_i', 3)
ON DUPLICATE KEY UPDATE
  account_number = VALUES(account_number),
  telebirr_phone = VALUES(telebirr_phone),
  admin_telegram = VALUES(admin_telegram),
  admin_id = VALUES(admin_id);

CREATE TABLE VolunteerTasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category ENUM('Home Chores', 'Children Care', 'Teaching') NOT NULL,
    description TEXT
);

INSERT INTO VolunteerTasks (name, category, description) VALUES
('Wash Plates', 'Home Chores', 'Clean kitchen utensils and dishes'),
('Clean House', 'Home Chores', 'General house cleaning'),
('Wash Clothes', 'Home Chores', 'Laundry and folding clothes'),
('Make Children''s Hair', 'Children Care', 'Style hair for children'),
('Play with Kids', 'Children Care', 'Engage children in recreational activities'),
('Teach English', 'Teaching', 'Basic English lessons'),
('Teach Math', 'Teaching', 'Basic mathematics lessons');

CREATE TABLE VolunteerTaskAssignments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    volunteer_id INT NOT NULL,
    task_id INT NOT NULL,
    assignment_date DATE NOT NULL,
    status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    assigned_by INT,
    FOREIGN KEY (volunteer_id) REFERENCES Volunteers(id),
    FOREIGN KEY (task_id) REFERENCES VolunteerTasks(id),
    FOREIGN KEY (assigned_by) REFERENCES Users(id)
);

CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message VARCHAR(255) NOT NULL,
    type ENUM('DONATION_APPROVED', 'DONATION_REJECTED', 'VOLUNTEER_APPROVED', 'VOLUNTEER_REJECTED', 'NEW_NEED') NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- VolunteerRequests table
CREATE TABLE IF NOT EXISTS VolunteerRequests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    volunteer_id INT NOT NULL,
    request_type ENUM('Task Assignment', 'Leave', 'Equipment', 'Other') NOT NULL,
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    admin_response TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP NULL,
    FOREIGN KEY (volunteer_id) REFERENCES Volunteers(id) ON DELETE CASCADE
);