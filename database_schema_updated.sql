-- ============================================
-- Complete Database Schema for Market Management System (Updated)
-- Updates: Added SupplierProduct junction table, removed text 'products' column
-- ============================================

DROP DATABASE IF EXISTS market_db;
CREATE DATABASE market_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE market_db;

-- ============================================
-- DEPARTMENT TABLE
-- ============================================
CREATE TABLE Department (
    departmentID INT PRIMARY KEY AUTO_INCREMENT,
    departmentName VARCHAR(100) NOT NULL,
    location VARCHAR(200),
    budget DECIMAL(15, 2),
    managerID INT,
    INDEX idx_manager (managerID)
) ENGINE=InnoDB;

-- ============================================
-- EMPLOYEE TABLE
-- ============================================
CREATE TABLE Employee (
    employeeID INT PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phoneNumber VARCHAR(20),
    hireDate DATE NOT NULL,
    departmentID INT,
    position VARCHAR(100), -- 'Entry Level', 'Junior', 'Senior', 'Manager', 'CEO'
    isManager BOOLEAN DEFAULT FALSE,
    password VARCHAR(255) NOT NULL,
    
    FOREIGN KEY (departmentID) REFERENCES Department(departmentID) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_department (departmentID),
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- ============================================
-- MANAGER TABLE (Inherits from Employee)
-- ============================================
CREATE TABLE Manager (
    managerID INT PRIMARY KEY AUTO_INCREMENT,
    employeeID INT UNIQUE NOT NULL,
    departmentID INT,
    managementLevel VARCHAR(50), -- 'Level 1', 'CEO', etc.
    bonus DECIMAL(10, 2) DEFAULT 0.00,
    
    FOREIGN KEY (employeeID) REFERENCES Employee(employeeID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (departmentID) REFERENCES Department(departmentID) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_employee (employeeID),
    INDEX idx_department (departmentID)
) ENGINE=InnoDB;

-- Add foreign key constraint to Department for Manager
ALTER TABLE Department 
    ADD FOREIGN KEY (managerID) REFERENCES Manager(managerID) 
    ON DELETE SET NULL ON UPDATE CASCADE;

-- ============================================
-- SALARY TABLE
-- ============================================
CREATE TABLE Salary (
    salaryID INT PRIMARY KEY AUTO_INCREMENT,
    employeeID INT NOT NULL,
    baseSalary DECIMAL(10, 2) NOT NULL,
    bonus DECIMAL(10, 2) DEFAULT 0.00,
    deductions DECIMAL(10, 2) DEFAULT 0.00,
    effectiveDate DATE NOT NULL,
    endDate DATE,
    paymentFrequency ENUM('weekly', 'bi-weekly', 'monthly', 'annual') DEFAULT 'monthly',
    
    FOREIGN KEY (employeeID) REFERENCES Employee(employeeID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_employee (employeeID),
    INDEX idx_effective_date (effectiveDate)
) ENGINE=InnoDB;

-- ============================================
-- CUSTOMER TABLE
-- ============================================
CREATE TABLE Customer (
    customerID INT PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phoneNumber VARCHAR(20),
    address VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(50),
    zipCode VARCHAR(20),
    country VARCHAR(100) DEFAULT 'Egypt',
    registrationDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    loyaltyPoints INT DEFAULT 0,
    password VARCHAR(255) NOT NULL,
    
    INDEX idx_email (email),
    INDEX idx_phone (phoneNumber)
) ENGINE=InnoDB;

-- ============================================
-- SUPPLIER TABLE
-- ============================================
CREATE TABLE Supplier (
    supplierID INT PRIMARY KEY AUTO_INCREMENT,
    supplierName VARCHAR(100) NOT NULL,
    contactPerson VARCHAR(100),
    email VARCHAR(100),
    phoneNumber VARCHAR(20),
    address VARCHAR(200),
    city VARCHAR(100),
    country VARCHAR(100),
    rating DECIMAL(3, 2) CHECK (rating >= 0 AND rating <= 5),
    
    INDEX idx_name (supplierName),
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- ============================================
-- PRODUCT TABLE
-- ============================================
CREATE TABLE Product (
    productID INT PRIMARY KEY AUTO_INCREMENT,
    productName VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    price DECIMAL(10, 2) NOT NULL,
    cost DECIMAL(10, 2),
    stockQuantity INT DEFAULT 0,
    reorderLevel INT DEFAULT 10,
    supplierID INT,
    barcode VARCHAR(50) UNIQUE,
    sku VARCHAR(50) UNIQUE,
    weight DECIMAL(8, 2),
    dimensions VARCHAR(50),
    expiryDate DATE,
    isActive BOOLEAN DEFAULT TRUE,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (supplierID) REFERENCES Supplier(supplierID) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_category (category),
    INDEX idx_supplier (supplierID),
    INDEX idx_barcode (barcode),
    INDEX idx_sku (sku),
    INDEX idx_active (isActive)
) ENGINE=InnoDB;

-- ============================================
-- SUPPLIER_PRODUCT TABLE (Junction Table)
-- ============================================
CREATE TABLE SupplierProduct (
    supplierID INT NOT NULL,
    productID INT NOT NULL,
    
    PRIMARY KEY (supplierID, productID),
    FOREIGN KEY (supplierID) REFERENCES Supplier(supplierID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (productID) REFERENCES Product(productID)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ============================================
-- ENDORSEMENT TABLE
-- ============================================
CREATE TABLE Endorsement (
    endorsementID INT PRIMARY KEY AUTO_INCREMENT,
    productID INT NOT NULL,
    endorserName VARCHAR(100),
    endorsementType VARCHAR(50),
    startDate DATE,
    endDate DATE,
    description TEXT,
    
    FOREIGN KEY (productID) REFERENCES Product(productID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_product (productID),
    INDEX idx_dates (startDate, endDate)
) ENGINE=InnoDB;

-- ============================================
-- CART TABLE
-- ============================================
CREATE TABLE Cart (
    cartID INT PRIMARY KEY AUTO_INCREMENT,
    customerID INT NOT NULL,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('active', 'checked_out', 'abandoned', 'completed') DEFAULT 'active',
    
    FOREIGN KEY (customerID) REFERENCES Customer(customerID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_customer (customerID),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- ============================================
-- CART ITEM TABLE
-- ============================================
CREATE TABLE CartItem (
    cartItemID INT PRIMARY KEY AUTO_INCREMENT,
    cartID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    priceAtAdd DECIMAL(10, 2) NOT NULL,
    addedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (cartID) REFERENCES Cart(cartID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (productID) REFERENCES Product(productID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_cart (cartID),
    INDEX idx_product (productID),
    UNIQUE KEY unique_cart_product (cartID, productID)
) ENGINE=InnoDB;

-- ============================================
-- USERS TABLE (Authentication)
-- ============================================
CREATE TABLE Users (
    userID INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    userType ENUM('ADMIN', 'CASHIER') NOT NULL,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_email (email)
) ENGINE=InnoDB;


-- ============================================
-- SAMPLE DATA INSERTION (UPDATED)
-- ============================================

-- Insert Departments
INSERT INTO Department (departmentName, location, budget) VALUES
('Sales', 'Floor 1', 50000.00),
('Inventory', 'Warehouse A', 75000.00),
('Customer Service', 'Floor 2', 30000.00),
('IT', 'Floor 3', 100000.00),
('Human Resources', 'Floor 2', 40000.00),
('Executive', 'Penthouse', 500000.00); 

-- Insert Employees
INSERT INTO Employee (firstName, lastName, email, phoneNumber, hireDate, departmentID, position, isManager, password) VALUES
('The', 'CEO', 'ceo@market.com', '+20-100-0000000', '2015-01-01', 6, 'CEO', TRUE, 'ceo123'), 
('Ahmed', 'Hassan', 'ahmed.hassan@market.com', '+20-100-1234567', '2020-01-15', 1, 'Manager', TRUE, 'password123'), 
('Fatima', 'Ali', 'fatima.ali@market.com', '+20-100-2345678', '2019-03-20', 2, 'Senior', FALSE, 'password123'), 
('Mohamed', 'Ibrahim', 'mohamed.ibrahim@market.com', '+20-100-3456789', '2021-06-10', 3, 'Junior', FALSE, 'password123'), 
('Sara', 'Mahmoud', 'sara.mahmoud@market.com', '+20-100-4567890', '2020-08-25', 1, 'Entry Level', FALSE, 'password123'), 
('Omar', 'Khaled', 'omar.khaled@market.com', '+20-100-5678901', '2018-11-30', 4, 'Manager', TRUE, 'password123'), 
('Nour', 'Ahmed', 'nour.ahmed@market.com', '+20-100-6789012', '2022-02-14', 5, 'Senior', FALSE, 'password123'), 
('Youssef', 'Said', 'youssef.said@market.com', '+20-100-7890123', '2021-09-05', 2, 'Junior', FALSE, 'password123'), 
('Layla', 'Mostafa', 'layla.mostafa@market.com', '+20-100-8901234', '2020-04-18', 3, 'Entry Level', FALSE, 'password123'); 

-- Insert Managers
INSERT INTO Manager (employeeID, departmentID, managementLevel, bonus) VALUES
(1, 6, 'CEO', 50000.00),     
(2, 1, 'Level 1', 5000.00),  
(6, 4, 'Level 1', 6000.00);  

-- Update Department with Manager IDs
UPDATE Department SET managerID = 1 WHERE departmentID = 6; 
UPDATE Department SET managerID = 2 WHERE departmentID = 1; 
UPDATE Department SET managerID = 3 WHERE departmentID = 4; 

-- Insert Salaries
INSERT INTO Salary (employeeID, baseSalary, bonus, deductions, effectiveDate, paymentFrequency) VALUES
(1, 50000.00, 20000.00, 1000.00, '2015-01-01', 'monthly'),
(2, 15000.00, 5000.00, 500.00, '2020-01-15', 'monthly'),
(3, 14000.00, 4500.00, 450.00, '2019-03-20', 'monthly'),
(4, 8000.00, 0.00, 300.00, '2021-06-10', 'monthly'),
(5, 7000.00, 500.00, 250.00, '2020-08-25', 'monthly'),
(6, 18000.00, 6000.00, 600.00, '2018-11-30', 'monthly'),
(7, 12000.00, 3500.00, 400.00, '2022-02-14', 'monthly'),
(8, 6500.00, 0.00, 200.00, '2021-09-05', 'monthly'),
(9, 7500.00, 300.00, 250.00, '2020-04-18', 'monthly');

-- Insert Customers
INSERT INTO Customer (firstName, lastName, email, phoneNumber, address, city, state, zipCode, loyaltyPoints, password) VALUES
('Khaled', 'Farouk', 'khaled.farouk@email.com', '+20-111-1111111', '123 Tahrir St', 'Cairo', 'Cairo', '11511', 150, 'password123'),
('Mona', 'Sayed', 'mona.sayed@email.com', '+20-111-2222222', '456 Nile Ave', 'Giza', 'Giza', '12511', 200, 'password123'),
('Tarek', 'Nasser', 'tarek.nasser@email.com', '+20-111-3333333', '789 Alexandria Rd', 'Alexandria', 'Alexandria', '21500', 75, 'password123'),
('Heba', 'Zaki', 'heba.zaki@email.com', '+20-111-4444444', '321 Mansoura St', 'Mansoura', 'Dakahlia', '35511', 300, 'password123'),
('Amr', 'Salah', 'amr.salah@email.com', '+20-111-5555555', '654 Aswan Blvd', 'Aswan', 'Aswan', '81511', 50, 'password123');

-- Insert Suppliers (Removed 'products' string column)
INSERT INTO Supplier (supplierName, contactPerson, email, phoneNumber, address, city, country, rating) VALUES
('Fresh Foods Co.', 'Ali Hassan', 'ali@freshfoods.com', '+20-222-1111111', '10 Industrial Zone', 'Cairo', 'Egypt', 4.5),
('Global Electronics', 'John Smith', 'john@globalelec.com', '+1-555-1234567', '500 Tech Park', 'New York', 'USA', 4.8),
('Local Dairy Farm', 'Mahmoud Amin', 'mahmoud@localdairy.com', '+20-222-2222222', '25 Farm Road', 'Fayoum', 'Egypt', 4.2),
('Import Export Ltd', 'Sarah Johnson', 'sarah@importexport.com', '+44-20-12345678', '100 Trade Center', 'London', 'UK', 4.6),
('Low Quality Supplies', 'Bad Guy', 'bad@supply.com', '+20-000-0000000', 'Nowhere', 'Cairo', 'Egypt', 2.0); 

-- Insert Products
INSERT INTO Product (productName, description, category, price, cost, stockQuantity, reorderLevel, supplierID, barcode, sku) VALUES
('Fresh Milk 1L', 'Fresh whole milk', 'Dairy', 25.00, 18.00, 150, 30, 3, '1234567890001', 'MILK-001'),
('White Bread', 'Freshly baked white bread', 'Bakery', 10.00, 6.00, 200, 50, 1, '1234567890002', 'BREAD-001'),
('Chicken Breast 1kg', 'Fresh chicken breast', 'Meat', 85.00, 65.00, 80, 20, 1, '1234567890003', 'CHKN-001'),
('Tomatoes 1kg', 'Fresh organic tomatoes', 'Vegetables', 15.00, 10.00, 120, 25, 5, '1234567890004', 'VEG-001'),
('Rice 5kg', 'Premium basmati rice', 'Grains', 120.00, 90.00, 100, 20, 1, '1234567890005', 'RICE-001'),
('Orange Juice 1L', 'Fresh squeezed orange juice', 'Beverages', 30.00, 22.00, 90, 20, 1, '1234567890006', 'JUC-001'),
('Laptop HP 15', 'HP Laptop 15 inch, 8GB RAM', 'Electronics', 12000.00, 9500.00, 25, 5, 2, '1234567890007', 'ELEC-001'),
('Smartphone Samsung', 'Samsung Galaxy A series', 'Electronics', 8000.00, 6500.00, 30, 10, 2, '1234567890008', 'ELEC-002'),
('Olive Oil 500ml', 'Extra virgin olive oil', 'Cooking Oils', 95.00, 70.00, 60, 15, 4, '1234567890009', 'OIL-001'),
('Eggs 12pcs', 'Fresh farm eggs', 'Dairy', 35.00, 25.00, 180, 40, 3, '1234567890010', 'EGG-001');

-- LINK SUPPLIERS TO PRODUCTS (Populating the Junction Table)
-- Supplier 1 (Fresh Foods) -> Bread (2), Chicken (3), Tomatoes (4), Rice (5), Juice (6) - General broad supplier
INSERT INTO SupplierProduct (supplierID, productID) VALUES
(1, 2), (1, 3), (1, 4), (1, 5), (1, 6);

-- Supplier 2 (Global Electronics) -> Laptop (7), Smartphone (8)
INSERT INTO SupplierProduct (supplierID, productID) VALUES
(2, 7), (2, 8);

-- Supplier 3 (Local Dairy) -> Milk (1), Eggs (10)
INSERT INTO SupplierProduct (supplierID, productID) VALUES
(3, 1), (3, 10);

-- Supplier 4 (Import Export) -> Olive Oil (9), Rice (5 - also supplied by Fresh Foods)
INSERT INTO SupplierProduct (supplierID, productID) VALUES
(4, 9), (4, 5);

-- Supplier 5 (Low Quality) -> Tomatoes (4 - also supplied by Fresh Foods)
INSERT INTO SupplierProduct (supplierID, productID) VALUES
(5, 4);


-- Insert Endorsements
INSERT INTO Endorsement (productID, endorserName, endorsementType, startDate, endDate, description) VALUES
(7, 'Tech Review Magazine', 'Media', '2024-01-01', '2024-12-31', 'Best value laptop 2024'),
(8, 'Mobile Expert', 'Influencer', '2024-06-01', '2024-12-31', 'Top smartphone for students'),
(9, 'Chef Ahmed', 'Celebrity', '2024-03-01', '2025-03-01', 'Premium quality olive oil');

-- Insert Carts
INSERT INTO Cart (customerID, status, updatedAt) VALUES
(1, 'completed', NOW()),         
(2, 'completed', NOW() - INTERVAL 2 DAY),
(3, 'checked_out', NOW()),
(4, 'active', NOW());

-- Insert Cart Items 
INSERT INTO CartItem (cartID, productID, quantity, priceAtAdd) VALUES
(1, 7, 1, 12000.00), 
(2, 8, 1, 8000.00);  

-- Insert Users
INSERT INTO Users (email, password, firstName, lastName, userType) VALUES
('admin@market.com', 'admin123', 'System', 'Admin', 'ADMIN'),
('cashier@market.com', 'cashier123', 'John', 'Doe', 'CASHIER');

-- ============================================
-- VIEWS (UNCHANGED)
-- ============================================

CREATE VIEW vw_EmployeeDetails AS
SELECT 
    e.employeeID,
    CONCAT(e.firstName, ' ', e.lastName) AS fullName,
    e.email,
    e.phoneNumber,
    e.position,
    d.departmentName,
    s.baseSalary,
    s.bonus,
    s.deductions,
    (s.baseSalary + s.bonus - s.deductions) AS netSalary,
    e.hireDate,
    e.isManager
FROM Employee e
LEFT JOIN Department d ON e.departmentID = d.departmentID
LEFT JOIN Salary s ON e.employeeID = s.employeeID AND s.endDate IS NULL;

-- View: Product Inventory Status
CREATE VIEW vw_ProductInventory AS
SELECT 
    p.productID,
    p.productName,
    p.category,
    p.price,
    p.stockQuantity,
    p.reorderLevel,
    CASE 
        WHEN p.stockQuantity <= p.reorderLevel THEN 'Low Stock'
        WHEN p.stockQuantity <= (p.reorderLevel * 2) THEN 'Medium Stock'
        ELSE 'Good Stock'
    END AS stockStatus,
    s.supplierName,
    p.isActive
FROM Product p
LEFT JOIN Supplier s ON p.supplierID = s.supplierID;

-- View: Cart Summary
CREATE VIEW vw_CartSummary AS
SELECT 
    c.cartID,
    c.customerID,
    CONCAT(cu.firstName, ' ', cu.lastName) AS customerName,
    COUNT(ci.cartItemID) AS totalItems,
    SUM(ci.quantity) AS totalQuantity,
    SUM(ci.quantity * ci.priceAtAdd) AS totalAmount,
    c.status,
    c.createdAt,
    c.updatedAt
FROM Cart c
INNER JOIN Customer cu ON c.customerID = cu.customerID
LEFT JOIN CartItem ci ON c.cartID = ci.cartID
GROUP BY c.cartID, c.customerID, cu.firstName, cu.lastName, c.status, c.createdAt, c.updatedAt;

-- ============================================
-- DATABASE STATISTICS
-- ============================================

SELECT 'Database Schema Refactored Successfully!' AS Status;
