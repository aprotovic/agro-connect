package dao;

import config.DatabaseConfig;
import models.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Order operations
 * Handles all database operations related to orders
 */
public class OrderDAO {
    
    /**
     * Place a new order
     * @param order Order object with details
     * @return Generated order ID, or -1 if failed
     */
    public int placeOrder(Order order) {
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Check product availability with pessimistic locking to prevent race conditions
            String checkSql = "SELECT quantity, price FROM product WHERE product_id = ? AND status = 'available' FOR UPDATE";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, order.getProductId());
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                conn.rollback();
                return -1; // Product not available
            }
            
            int availableQty = rs.getInt("quantity");
            double unitPrice = rs.getDouble("price");
            
            if (availableQty < order.getQuantity()) {
                conn.rollback();
                return -2; // Insufficient quantity
            }
            
            // Calculate total price
            double totalPrice = unitPrice * order.getQuantity();
            
            // Insert order
            String insertSql = "INSERT INTO orders (merchant_id, product_id, quantity, total_price, status, payment_status) " +
                              "VALUES (?, ?, ?, ?, 'pending', 'pending')";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setInt(1, order.getMerchantId());
            insertStmt.setInt(2, order.getProductId());
            insertStmt.setInt(3, order.getQuantity());
            insertStmt.setDouble(4, totalPrice);
            
            int rowsAffected = insertStmt.executeUpdate();
            int orderId = -1;
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                }
            }
            
            // Update product quantity
            String updateSql = "UPDATE product SET quantity = quantity - ? WHERE product_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, order.getQuantity());
            updateStmt.setInt(2, order.getProductId());
            updateStmt.executeUpdate();
            
            conn.commit(); // Commit transaction
            return orderId;
            
        } catch (SQLException e) {
            System.err.println("Error placing order: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return -1;
    }
    
    /**
     * Get order by ID (with full details)
     * @param orderId Order's ID
     * @return Order object with details, or null if not found
     */
    public Order getOrderById(int orderId) {
        String sql = "SELECT o.*, " +
                     "m.name as merchant_name, m.phone as merchant_phone, " +
                     "p.product_name, p.unit, p.price as unit_price, " +
                     "f.farmer_id, f.name as farmer_name, f.location as farmer_location " +
                     "FROM orders o " +
                     "INNER JOIN merchant m ON o.merchant_id = m.merchant_id " +
                     "INNER JOIN product p ON o.product_id = p.product_id " +
                     "INNER JOIN farmer f ON p.farmer_id = f.farmer_id " +
                     "WHERE o.order_id = ?";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractOrderFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching order: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return null;
    }
    
    /**
     * Get orders by merchant ID
     * @param merchantId Merchant's ID
     * @return List of orders by the merchant
     */
    public List<Order> getOrdersByMerchant(int merchantId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, " +
                     "m.name as merchant_name, m.phone as merchant_phone, " +
                     "p.product_name, p.unit, p.price as unit_price, " +
                     "f.farmer_id, f.name as farmer_name, f.location as farmer_location " +
                     "FROM orders o " +
                     "INNER JOIN merchant m ON o.merchant_id = m.merchant_id " +
                     "INNER JOIN product p ON o.product_id = p.product_id " +
                     "INNER JOIN farmer f ON p.farmer_id = f.farmer_id " +
                     "WHERE o.merchant_id = ? " +
                     "ORDER BY o.order_date DESC";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, merchantId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching merchant orders: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return orders;
    }
    
    /**
     * Get orders for farmer's products
     * @param farmerId Farmer's ID
     * @return List of orders for farmer's products
     */
    public List<Order> getOrdersByFarmer(int farmerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, " +
                     "m.name as merchant_name, m.phone as merchant_phone, " +
                     "p.product_name, p.unit, p.price as unit_price, " +
                     "f.farmer_id, f.name as farmer_name, f.location as farmer_location " +
                     "FROM orders o " +
                     "INNER JOIN merchant m ON o.merchant_id = m.merchant_id " +
                     "INNER JOIN product p ON o.product_id = p.product_id " +
                     "INNER JOIN farmer f ON p.farmer_id = f.farmer_id " +
                     "WHERE f.farmer_id = ? " +
                     "ORDER BY o.order_date DESC";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, farmerId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching farmer orders: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return orders;
    }
    
    /**
     * Update order status
     * @param orderId Order's ID
     * @param newStatus New status
     * @return true if successful, false otherwise
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return false;
    }
    
    /**
     * Update payment status
     * @param orderId Order's ID
     * @param paymentStatus New payment status
     * @return true if successful, false otherwise
     */
    public boolean updatePaymentStatus(int orderId, String paymentStatus) {
        String sql = "UPDATE orders SET payment_status = ? WHERE order_id = ?";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, paymentStatus);
            stmt.setInt(2, orderId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return false;
    }
    
    /**
     * Cancel order and restore product quantity
     * @param orderId Order's ID
     * @return true if successful, false otherwise
     */
    public boolean cancelOrder(int orderId) {
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Get order details
            String selectSql = "SELECT product_id, quantity FROM orders WHERE order_id = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, orderId);
            ResultSet rs = selectStmt.executeQuery();
            
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            
            int productId = rs.getInt("product_id");
            int quantity = rs.getInt("quantity");
            
            // Update order status
            String updateOrderSql = "UPDATE orders SET status = 'cancelled' WHERE order_id = ?";
            PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderSql);
            updateOrderStmt.setInt(1, orderId);
            updateOrderStmt.executeUpdate();
            
            // Restore product quantity
            String updateProductSql = "UPDATE product SET quantity = quantity + ? WHERE product_id = ?";
            PreparedStatement updateProductStmt = conn.prepareStatement(updateProductSql);
            updateProductStmt.setInt(1, quantity);
            updateProductStmt.setInt(2, productId);
            updateProductStmt.executeUpdate();
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error cancelling order: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return false;
    }
    
    /**
     * Get all orders
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, " +
                     "m.name as merchant_name, m.phone as merchant_phone, " +
                     "p.product_name, p.unit, p.price as unit_price, " +
                     "f.farmer_id, f.name as farmer_name, f.location as farmer_location " +
                     "FROM orders o " +
                     "INNER JOIN merchant m ON o.merchant_id = m.merchant_id " +
                     "INNER JOIN product p ON o.product_id = p.product_id " +
                     "INNER JOIN farmer f ON p.farmer_id = f.farmer_id " +
                     "ORDER BY o.order_date DESC";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all orders: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return orders;
    }
    
    /**
     * Extract Order object from ResultSet
     */
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setMerchantId(rs.getInt("merchant_id"));
        order.setProductId(rs.getInt("product_id"));
        order.setQuantity(rs.getInt("quantity"));
        order.setTotalPrice(rs.getDouble("total_price"));
        order.setStatus(rs.getString("status"));
        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setDeliveryDate(rs.getDate("delivery_date"));
        order.setPaymentStatus(rs.getString("payment_status"));
        order.setNotes(rs.getString("notes"));
        
        // Extended fields from joins
        order.setMerchantName(rs.getString("merchant_name"));
        order.setMerchantPhone(rs.getString("merchant_phone"));
        order.setProductName(rs.getString("product_name"));
        order.setUnit(rs.getString("unit"));
        order.setUnitPrice(rs.getDouble("unit_price"));
        order.setFarmerId(rs.getInt("farmer_id"));
        order.setFarmerName(rs.getString("farmer_name"));
        order.setFarmerLocation(rs.getString("farmer_location"));
        
        return order;
    }
}
