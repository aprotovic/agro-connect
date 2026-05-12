package dao;

import config.DatabaseConfig;
import models.Merchant;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Merchant operations
 * Handles all database operations related to merchants
 */
public class MerchantDAO {
    
    /**
     * Register a new merchant
     * @param merchant Merchant object with registration details
     * @return Generated merchant ID, or -1 if failed
     */
    public int registerMerchant(Merchant merchant) {
        String sql = "INSERT INTO merchant (name, phone, email, password, business_type, address) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, merchant.getName());
            stmt.setString(2, merchant.getPhone());
            stmt.setString(3, merchant.getEmail());
            
            // Hash password with BCrypt before storing
            String hashedPassword = BCrypt.hashpw(merchant.getPassword(), BCrypt.gensalt());
            stmt.setString(4, hashedPassword);
            
            stmt.setString(5, merchant.getBusinessType());
            stmt.setString(6, merchant.getAddress());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error registering merchant: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return -1;
    }
    
    /**
     * Login authentication for merchant
     * @param phone Merchant's phone number
     * @param password Merchant's password
     * @return Merchant object if authenticated, null otherwise
     */
    public Merchant loginMerchant(String phone, String password) {
        String sql = "SELECT * FROM merchant WHERE phone = ? AND status = 'active'";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, phone);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                boolean passwordMatch = false;
                try {
                    // Verify against BCrypt hash
                    passwordMatch = BCrypt.checkpw(password, storedHash);
                } catch (IllegalArgumentException e) {
                    // Fallback for legacy plaintext passwords from sample data
                    passwordMatch = password.equals(storedHash);
                }
                
                if (passwordMatch) {
                    return extractMerchantFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during merchant login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return null;
    }
    
    /**
     * Get merchant by ID
     * @param merchantId Merchant's ID
     * @return Merchant object, or null if not found
     */
    public Merchant getMerchantById(int merchantId) {
        String sql = "SELECT * FROM merchant WHERE merchant_id = ?";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, merchantId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractMerchantFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching merchant: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return null;
    }
    
    /**
     * Get all merchants
     * @return List of all merchants
     */
    public List<Merchant> getAllMerchants() {
        List<Merchant> merchants = new ArrayList<>();
        String sql = "SELECT * FROM merchant WHERE status = 'active' ORDER BY name";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                merchants.add(extractMerchantFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all merchants: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return merchants;
    }
    
    /**
     * Update merchant profile
     * @param merchant Merchant object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateMerchant(Merchant merchant) {
        String sql = "UPDATE merchant SET name = ?, email = ?, business_type = ?, address = ? WHERE merchant_id = ?";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, merchant.getName());
            stmt.setString(2, merchant.getEmail());
            stmt.setString(3, merchant.getBusinessType());
            stmt.setString(4, merchant.getAddress());
            stmt.setInt(5, merchant.getMerchantId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating merchant: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return false;
    }
    
    /**
     * Check if phone number exists
     * @param phone Phone number to check
     * @return true if exists, false otherwise
     */
    public boolean phoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM merchant WHERE phone = ?";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, phone);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking phone: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return false;
    }
    
    /**
     * Extract Merchant object from ResultSet
     */
    private Merchant extractMerchantFromResultSet(ResultSet rs) throws SQLException {
        Merchant merchant = new Merchant();
        merchant.setMerchantId(rs.getInt("merchant_id"));
        merchant.setName(rs.getString("name"));
        merchant.setPhone(rs.getString("phone"));
        merchant.setEmail(rs.getString("email"));
        merchant.setPassword(rs.getString("password"));
        merchant.setBusinessType(rs.getString("business_type"));
        merchant.setAddress(rs.getString("address"));
        merchant.setRegistrationDate(rs.getTimestamp("registration_date"));
        merchant.setStatus(rs.getString("status"));
        return merchant;
    }
}
