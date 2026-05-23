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
