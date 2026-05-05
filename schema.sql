-- =============================================
-- Agro-Connect Database Schema
-- Database: agro_connect
-- Description: Farm-to-Market Trading Platform
-- =============================================

-- Drop existing database if exists (for clean setup)
DROP DATABASE IF EXISTS agro_connect;

-- Create database
CREATE DATABASE agro_connect CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Use the database
USE agro_connect;

-- =============================================
-- Table: farmer
-- Description: Stores farmer information
-- =============================================
CREATE TABLE farmer (
    farmer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('active', 'inactive') DEFAULT 'active',
    INDEX idx_phone (phone),
    INDEX idx_location (location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Table: merchant
-- Description: Stores merchant/buyer information
-- =============================================
CREATE TABLE merchant (
    merchant_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    business_type VARCHAR(100),
    address TEXT,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('active', 'inactive') DEFAULT 'active',
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Table: product
-- Description: Stores product listings by farmers
-- =============================================
CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    farmer_id INT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) DEFAULT 'General',
    quantity INT NOT NULL DEFAULT 0,
    unit VARCHAR(20) DEFAULT 'kg',
    price DOUBLE NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('available', 'out_of_stock', 'discontinued') DEFAULT 'available',
    FOREIGN KEY (farmer_id) REFERENCES farmer(farmer_id) ON DELETE CASCADE,
    INDEX idx_farmer (farmer_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_product_name (product_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Table: orders
-- Description: Stores order transactions
-- =============================================
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    total_price DOUBLE NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_date DATE,
    payment_status ENUM('pending', 'paid', 'failed') DEFAULT 'pending',
    notes TEXT,
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
    INDEX idx_merchant (merchant_id),
    INDEX idx_product (product_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Table: order_status_history
-- Description: Track order status changes
-- =============================================
CREATE TABLE order_status_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Create Views for Easy Data Retrieval
-- =============================================

-- View: Product listing with farmer details
CREATE VIEW vw_product_listing AS
SELECT 
    p.product_id,
    p.product_name,
    p.category,
    p.quantity,
    p.unit,
    p.price,
    p.description,
    p.status,
    f.farmer_id,
    f.name AS farmer_name,
    f.location AS farmer_location,
    f.phone AS farmer_phone
FROM product p
INNER JOIN farmer f ON p.farmer_id = f.farmer_id
WHERE p.status = 'available';

-- View: Order details with merchant and product info
CREATE VIEW vw_order_details AS
SELECT 
    o.order_id,
    o.quantity AS order_quantity,
    o.total_price,
    o.status AS order_status,
    o.order_date,
    o.delivery_date,
    o.payment_status,
    m.merchant_id,
    m.name AS merchant_name,
    m.phone AS merchant_phone,
    p.product_id,
    p.product_name,
    p.unit,
    p.price AS unit_price,
    f.farmer_id,
    f.name AS farmer_name,
    f.location AS farmer_location
FROM orders o
INNER JOIN merchant m ON o.merchant_id = m.merchant_id
INNER JOIN product p ON o.product_id = p.product_id
INNER JOIN farmer f ON p.farmer_id = f.farmer_id;

-- =============================================
-- Stored Procedures
-- =============================================

-- Procedure: Place an order
DELIMITER $$
CREATE PROCEDURE sp_place_order(
    IN p_merchant_id INT,
    IN p_product_id INT,
    IN p_quantity INT,
    OUT p_order_id INT,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_available_qty INT;
    DECLARE v_unit_price DOUBLE;
    DECLARE v_total DOUBLE;
    
    -- Check product availability
    SELECT quantity, price INTO v_available_qty, v_unit_price
    FROM product
    WHERE product_id = p_product_id AND status = 'available';
    
    IF v_available_qty IS NULL THEN
        SET p_message = 'Product not found or unavailable';
        SET p_order_id = -1;
    ELSEIF v_available_qty < p_quantity THEN
        SET p_message = 'Insufficient quantity available';
        SET p_order_id = -1;
    ELSE
        -- Calculate total
        SET v_total = v_unit_price * p_quantity;
        
        -- Insert order
        INSERT INTO orders (merchant_id, product_id, quantity, total_price, status)
        VALUES (p_merchant_id, p_product_id, p_quantity, v_total, 'pending');
        
        SET p_order_id = LAST_INSERT_ID();
        
        -- Update product quantity
        UPDATE product 
        SET quantity = quantity - p_quantity,
            status = CASE WHEN (quantity - p_quantity) <= 0 THEN 'out_of_stock' ELSE 'available' END
        WHERE product_id = p_product_id;
        
        SET p_message = 'Order placed successfully';
    END IF;
END$$
DELIMITER ;

-- Procedure: Update order status
DELIMITER $$
CREATE PROCEDURE sp_update_order_status(
    IN p_order_id INT,
    IN p_new_status VARCHAR(50),
    IN p_notes TEXT
)
BEGIN
    DECLARE v_old_status VARCHAR(50);
    
    -- Get current status
    SELECT status INTO v_old_status FROM orders WHERE order_id = p_order_id;
    
    -- Update order status
    UPDATE orders SET status = p_new_status WHERE order_id = p_order_id;
    
    -- Log status change
    INSERT INTO order_status_history (order_id, old_status, new_status, notes)
    VALUES (p_order_id, v_old_status, p_new_status, p_notes);
END$$
DELIMITER ;

-- =============================================
-- Triggers
-- =============================================

-- Trigger: Auto-update product status based on quantity
DELIMITER $$
CREATE TRIGGER trg_product_quantity_check 
BEFORE UPDATE ON product
FOR EACH ROW
BEGIN
    IF NEW.quantity <= 0 THEN
        SET NEW.status = 'out_of_stock';
    ELSEIF NEW.quantity > 0 AND OLD.status = 'out_of_stock' THEN
        SET NEW.status = 'available';
    END IF;
END$$
DELIMITER ;

-- Trigger: Log order creation
DELIMITER $$
CREATE TRIGGER trg_order_created
AFTER INSERT ON orders
FOR EACH ROW
BEGIN
    INSERT INTO order_status_history (order_id, old_status, new_status, notes)
    VALUES (NEW.order_id, NULL, NEW.status, 'Order created');
END$$
DELIMITER ;

-- =============================================
-- Grant Permissions (for production use)
-- =============================================
-- GRANT ALL PRIVILEGES ON agro_connect.* TO 'agro_user'@'localhost' IDENTIFIED BY 'agro_pass_2024';
-- FLUSH PRIVILEGES;

-- =============================================
-- Display Success Message
-- =============================================
SELECT 'Database schema created successfully!' AS Status;
SELECT 'Tables created: farmer, merchant, product, orders, order_status_history' AS Info;
SELECT 'Views created: vw_product_listing, vw_order_details' AS Info;
SELECT 'Stored procedures and triggers added' AS Info;
