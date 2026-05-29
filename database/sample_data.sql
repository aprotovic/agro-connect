-- =============================================
-- Agro-Connect Sample Data (Ethiopian Context)
-- Description: Real static data for Ethiopia
-- =============================================

USE agro_connect;

-- =============================================
-- Insert Sample Farmers (Ethiopian Names & Locations)
-- =============================================
INSERT INTO farmer (name, location, phone, email, password) VALUES
('Abebe Bikila', 'Oromia', '9876543210', 'abebe@farm.et', 'farmer123'),
('Kebede Tadesse', 'Amhara', '9876543211', 'kebede@farm.et', 'farmer123'),
('Almaz Ayana', 'Sidama', '9876543212', 'almaz@farm.et', 'farmer123'),
('Haile Gebrselassie', 'Addis Ababa', '9876543213', 'haile@farm.et', 'farmer123'),
('Tirunesh Dibaba', 'Tigray', '9876543214', 'tirunesh@farm.et', 'farmer123'),
('Derartu Tulu', 'Arsi', '9876543215', 'derartu@farm.et', 'farmer123'),
('Kenenisa Bekele', 'Shewa', '9876543216', 'kenenisa@farm.et', 'farmer123'),
('Meseret Defar', 'Harar', '9876543217', 'meseret@farm.et', 'farmer123');

-- =============================================
-- Insert Sample Merchants (Ethiopian Businesses)
-- =============================================
INSERT INTO merchant (name, phone, email, password, business_type, address) VALUES
('Addis Traders', '9123456789', 'contact@addistraders.et', 'merchant123', 'Wholesale', 'Merkato, Addis Ababa'),
('Bahir Dar Wholesalers', '9123456790', 'info@bahirdar.et', 'merchant123', 'Wholesale', 'Kebele 4, Bahir Dar'),
('Hawassa Markets', '9123456791', 'sales@hawassa.et', 'merchant123', 'Retail', 'Piazza, Hawassa'),
('Adama Fresh', '9123456792', 'fresh@adama.et', 'merchant123', 'Retail', 'Bole, Adama'),
('Dire Dawa Suppliers', '9123456793', 'supply@diredawa.et', 'merchant123', 'Wholesale', 'Taiwan Market, Dire Dawa'),
('Gonder Agro', '9123456794', 'agro@gonder.et', 'merchant123', 'Distributor', 'Arada, Gonder');

-- =============================================
-- Insert Sample Products (Ethiopian Crops: Teff, Coffee, Enset, etc.)
-- =============================================

-- Farmer 1: Abebe (Oromia) - Coffee & Grains
INSERT INTO product (farmer_id, product_name, category, quantity, unit, price, description) VALUES
(1, 'Ethiopian Coffee (Yirgacheffe)', 'Cash Crops', 500, 'kg', 450.00, 'Premium organic Yirgacheffe coffee beans'),
(1, 'White Teff', 'Grains', 1000, 'kg', 85.00, 'High quality Magna Teff for Injera'),
(1, 'Maize', 'Grains', 2000, 'kg', 35.00, 'Fresh yellow maize from Oromia');

-- Farmer 2: Kebede (Amhara) - Oilseeds
INSERT INTO product (farmer_id, product_name, category, quantity, unit, price, description) VALUES
(2, 'Sesame Seeds', 'Cash Crops', 800, 'kg', 120.00, 'Whitish Humera sesame seeds'),
(2, 'Niger Seed (Nug)', 'Oilseeds', 600, 'kg', 90.00, 'Black niger seeds for oil extraction'),
(2, 'Chickpeas (Shimbra)', 'Pulses', 1500, 'kg', 65.00, 'Desi variety chickpeas');

-- Farmer 3: Almaz (Sidama) - Fruits & Enset
INSERT INTO product (farmer_id, product_name, category, quantity, unit, price, description) VALUES
(3, 'Avocado', 'Fruits', 400, 'kg', 40.00, 'Buttery organic avocados'),
(3, 'Enset (Kocho)', 'Staple', 500, 'kg', 55.00, 'Processed Enset widely used in Sidama'),
(3, 'Bananas', 'Fruits', 1200, 'kg', 25.00, 'Sweet small bananas');

-- Farmer 4: Haile (Addis Ababa/Shewa) - Vegetables
INSERT INTO product (farmer_id, product_name, category, quantity, unit, price, description) VALUES
(4, 'Red Onions', 'Vegetables', 2000, 'kg', 30.00, 'Strong flavored red onions'),
(4, 'Tomatoes', 'Vegetables', 1500, 'kg', 25.00, 'Fresh ripe tomatoes'),
(4, 'Garlic', 'Vegetables', 300, 'kg', 150.00, 'Local Tseday garlic');

-- Farmer 5: Tirunesh (Tigray) - Spices & Grains
INSERT INTO product (farmer_id, product_name, category, quantity, unit, price, description) VALUES
(5, 'Red Pepper (Berbere)', 'Spices', 200, 'kg', 350.00, 'Hot dried red peppers for Berbere'),
(5, 'Barley', 'Grains', 1000, 'kg', 45.00, 'Barley for Besso and Tella'),
(5, 'Honey', 'Other', 100, 'kg', 600.00, 'Pure white honey form Tigray');

-- Farmer 6: Derartu (Arsi) - Wheat
INSERT INTO product (farmer_id, product_name, category, quantity, unit, price, description) VALUES
(6, 'Wheat', 'Grains', 3000, 'kg', 50.00, 'Durum wheat suitable for pasta'),
(6, 'Linseed (Telba)', 'Oilseeds', 500, 'kg', 75.00, 'Rich in Omega-3 linseed'),
(6, 'Faba Beans', 'Pulses', 1200, 'kg', 55.00, 'Large faba beans');

-- Farmer 7 & 8: Others
INSERT INTO product (farmer_id, product_name, category, quantity, unit, price, description) VALUES
(7, 'Sorghum', 'Grains', 1500, 'kg', 40.00, 'Red sorghum used for Injera and Tella'),
(7, 'Lentils (Misir)', 'Pulses', 800, 'kg', 110.00, 'Split red lentils'),
(8, 'Chat', 'Cash Crops', 200, 'kg', 800.00, 'Fresh export quality Chat'),
(8, 'Papaya', 'Fruits', 600, 'kg', 30.00, 'Large sweet papayas');

-- =============================================
-- Insert Sample Orders
-- =============================================
INSERT INTO orders (merchant_id, product_id, quantity, total_price, status, payment_status) VALUES
-- Addis Traders orders
(1, 1, 100, 45000.00, 'confirmed', 'paid'),
(1, 2, 200, 17000.00, 'shipped', 'paid'),
(1, 10, 50, 6000.00, 'pending', 'pending'),

-- Bahir Dar Wholesalers orders
(2, 4, 150, 18000.00, 'delivered', 'paid'),
(2, 14, 300, 13500.00, 'confirmed', 'paid'),

-- Hawassa Markets orders
(3, 7, 50, 2000.00, 'confirmed', 'paid'),
(3, 8, 100, 5500.00, 'pending', 'pending'),

-- Adama Fresh orders
(4, 10, 200, 5000.00, 'delivered', 'paid'),
(4, 11, 50, 7500.00, 'shipped', 'paid');

-- =============================================
-- Add some order status history
-- =============================================
INSERT INTO order_status_history (order_id, old_status, new_status, notes) VALUES
(1, 'pending', 'confirmed', 'Payment verified by CBE Birr'),
(1, 'confirmed', 'confirmed', 'Farmer confirmed availability'),
(2, 'pending', 'confirmed', 'Bank transfer received'),
(2, 'confirmed', 'shipped', 'Loaded on Isuzu truck'),
(4, 'pending', 'confirmed', 'Mobile money payment received'),
(4, 'confirmed', 'delivered', 'Delivered to warehouse');

-- =============================================
-- Verification
-- =============================================
SELECT 'Sample data inserted.' AS status;

SELECT 
    (SELECT COUNT(*) FROM farmer) AS farmers,
    (SELECT COUNT(*) FROM merchant) AS merchants,
    (SELECT COUNT(*) FROM product) AS products,
    (SELECT COUNT(*) FROM orders) AS orders;

-- Default Admin User
INSERT INTO admin (username, password) VALUES ('admin', 'admin123');

