CREATE DATABASE Capstone;
USE Capstone;
CREATE TABLE Parts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price DOUBLE,
    stock INT,
    min INT,
    max INT,
    machineId INT NULL,
    companyName VARCHAR(255) NULL,
    type ENUM('InHouse', 'Outsourced')
);

CREATE TABLE Products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price DOUBLE,
    stock INT,
    min INT,
    max INT
);

CREATE TABLE ProductParts (
    productId INT,
    partId INT,
    PRIMARY KEY(productId, partId),
    FOREIGN KEY(productId) REFERENCES Products(id),
    FOREIGN KEY(partId) REFERENCES Parts(id)
);
