
-- Supplier Table
CREATE TABLE Supplier (
    supplierID INT PRIMARY KEY,
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    address TEXT,
    mobileNumber VARCHAR(15) UNIQUE
);

-- Pharmacy Table
CREATE TABLE Pharmacy (
    pharmacyID INT PRIMARY KEY,
    noOfTimes INT,
    supplierID INT,
    FOREIGN KEY (supplierID) REFERENCES Supplier(supplierID) ON DELETE CASCADE
);

-- alter table Pharmacy 
-- rename column itemID to noOFItems;
-- select * from Pharmacy;

-- Item Table
CREATE TABLE Item (
    itemID INT PRIMARY KEY,
    supplierID INT,
    name VARCHAR(100),
    quantity INT CHECK (quantity >= 0),
    expiryDate DATE,-- CHECK, -- (expiryDate > CURRENT_DATE)
    pharmacyID INT,
    FOREIGN KEY (supplierID) REFERENCES Supplier(supplierID) ON DELETE CASCADE,
    FOREIGN KEY (pharmacyID) REFERENCES Pharmacy(pharmacyID) ON DELETE SET NULL
);

-- Patient Table
CREATE TABLE Patient (
    patientID INT PRIMARY KEY,
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    age INT CHECK (age >= 0),
    gender CHAR(1) CHECK (gender IN ('M', 'F', 'O')),
    bloodGroup VARCHAR(5),
    allergies TEXT,
    primaryNo VARCHAR(15) UNIQUE,
    secondaryNo VARCHAR(15) UNIQUE,
    address TEXT,
    insuranceNumber VARCHAR(50) UNIQUE
);

-- Department Table
CREATE TABLE Department (
    departmentID INT PRIMARY KEY,
    name VARCHAR(100),
    headDoctorID INT,
    location VARCHAR(100),
    numberOfStaffMembers INT CHECK (numberOfStaffMembers >= 0)
    -- FOREIGN KEY (headDoctorID) REFERENCES Doctor(doctorID) ON DELETE SET NULL
);

SET FOREIGN_KEY_CHECKS = 0;

-- Doctor Table
CREATE TABLE Doctor (
    doctorID INT PRIMARY KEY,
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    specialization VARCHAR(100),
    mobileNumber VARCHAR(15) UNIQUE,
    experience INT CHECK (experience >= 0),
    departmentID INT,
    availabilityStatus BOOLEAN,
    FOREIGN KEY (departmentID) REFERENCES Department(departmentID) ON DELETE SET NULL
);

ALTER TABLE Department
ADD CONSTRAINT fk_headDoctor FOREIGN KEY (headDoctorID) REFERENCES Doctor(doctorID) ON DELETE SET NULL;

-- Staff Table
CREATE TABLE Staff (
    staffID INT PRIMARY KEY,
    departmentID INT,
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    role VARCHAR(50),
    mobileNumber VARCHAR(15) UNIQUE,
    address TEXT,
    FOREIGN KEY (departmentID) REFERENCES Department(departmentID) ON DELETE SET NULL
);

alter table staff drop constraint mobileNumber;

-- Laboratory Table
CREATE TABLE Laboratory (
    labID INT PRIMARY KEY,
    testName VARCHAR(100),
    testAvailable BOOLEAN,
    testCost DECIMAL(10,2),
    labTechnicianID INT,
    FOREIGN KEY (labTechnicianID) REFERENCES Staff(staffID) ON DELETE SET NULL
);

-- Bed Table
CREATE TABLE Bed (
    bedNumber INT PRIMARY KEY,
    bedType VARCHAR(50),
    wardNumber INT CHECK (wardNumber > 0),
    availabilityStatus BOOLEAN,
    patientID INT NULL,
    FOREIGN KEY (patientID) REFERENCES Patient(patientID) ON DELETE SET NULL
);

-- Appointment Table
CREATE TABLE Appointment (
    appointmentID INT PRIMARY KEY,
    patientID INT,
    doctorID INT,
    dateTime DATETIME,
    status VARCHAR(50) CHECK (status IN ('Scheduled', 'Completed', 'Cancelled')),
    FOREIGN KEY (patientID) REFERENCES Patient(patientID) ON DELETE CASCADE,
    FOREIGN KEY (doctorID) REFERENCES Doctor(doctorID) ON DELETE SET NULL
);

-- Records Table
CREATE TABLE Records (
    recordID INT PRIMARY KEY,
    patientID INT,
    doctorID INT,
    prescription TEXT,
    dateTime DATETIME,
    FOREIGN KEY (patientID) REFERENCES Patient(patientID) ON DELETE CASCADE,
    FOREIGN KEY (doctorID) REFERENCES Doctor(doctorID) ON DELETE SET NULL
);

-- LabTest Table
CREATE TABLE LabTest (
    recordID INT,
    dateTime DATETIME,
    PRIMARY KEY(recordID, dateTime),
    labID INT,
    result TEXT,
    FOREIGN KEY (labID) REFERENCES Laboratory(labID) ON DELETE CASCADE
);

-- Transaction Table
CREATE TABLE Transaction (
    transactionID INT PRIMARY KEY,
    dateTime DATETIME,
    amount DECIMAL(10,2) CHECK (amount >= 0),
    patientID INT,
    recordID INT NULL,
    itemID INT NULL,
    paymentMode VARCHAR(50) CHECK (paymentMode IN ('Cash', 'Credit Card', 'Insurance', 'Online')),
    discount DECIMAL(5,2) CHECK (discount >= 0 AND discount <= 100),
    FOREIGN KEY (patientID) REFERENCES Patient(patientID) ON DELETE CASCADE,
    FOREIGN KEY (recordID) REFERENCES Records(recordID) ON DELETE SET NULL,
    FOREIGN KEY (itemID) REFERENCES Item(itemID) ON DELETE SET NULL
);

select * from appointment;