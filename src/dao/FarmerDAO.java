package dao;

import config.DatabaseConfig;
import models.Farmer;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Farmer operations
 * Handles all database operations related to farmers
 */
public class FarmerDAO {
    
    /**
     * Register a new farmer
     * @param farmer Farmer object with registration details
     * @return Generated farmer ID, or -1 if failed
     */
    public int registerFarmer(Farmer farmer) {
        String sql = "INSERT INTO farmer (name, location, phone, email, password) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, farmer.getName());
            stmt.setString(2, farmer.getLocation());
            stmt.setString(3, farmer.getPhone());
            stmt.setString(4, farmer.getEmail());
            
            // Hash password with BCrypt before storing
            String hashedPassword = BCrypt.hashpw(farmer.getPassword(), BCrypt.gensalt());
            stmt.setString(5, hashedPassword);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error registering farmer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return -1;
    }
    
    /**
     * Login authentication for farmer
     * @param phone Farmer's phone number
     * @param password Farmer's password
     * @return Farmer object if authenticated, null otherwise
     */
    public Farmer loginFarmer(String phone, String password) {
        String sql = "SELECT * FROM farmer WHERE phone = ? AND status = 'active'";
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
                    return extractFarmerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during farmer login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return null;
    }
    
    /**
     * Get farmer by ID
     * @param farmerId Farmer's ID
     * @return Farmer object, or null if not found
     */
    public Farmer getFarmerById(int farmerId) {
        String sql = "SELECT * FROM farmer WHERE farmer_id = ?";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, farmerId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractFarmerFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching farmer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return null;
    }
    
    /**
     * Get all farmers
     * @return List of all farmers
     */
    public List<Farmer> getAllFarmers() {
        List<Farmer> farmers = new ArrayList<>();
        String sql = "SELECT * FROM farmer WHERE status = 'active' ORDER BY name";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                farmers.add(extractFarmerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all farmers: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return farmers;
    }
    
    /**
     * Update farmer profile
     * @param farmer Farmer object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateFarmer(Farmer farmer) {
        String sql = "UPDATE farmer SET name = ?, location = ?, email = ? WHERE farmer_id = ?";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, farmer.getName());
            stmt.setString(2, farmer.getLocation());
            stmt.setString(3, farmer.getEmail());
            stmt.setInt(4, farmer.getFarmerId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating farmer: " + e.getMessage());
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
        String sql = "SELECT COUNT(*) FROM farmer WHERE phone = ?";
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
     * Extract Farmer object from ResultSet
     */
    private Farmer extractFarmerFromResultSet(ResultSet rs) throws SQLException {
        Farmer farmer = new Farmer();
        farmer.setFarmerId(rs.getInt("farmer_id"));
        farmer.setName(rs.getString("name"));
        farmer.setLocation(rs.getString("location"));
        farmer.setPhone(rs.getString("phone"));
        farmer.setEmail(rs.getString("email"));
        farmer.setPassword(rs.getString("password"));
        farmer.setRegistrationDate(rs.getTimestamp("registration_date"));
        farmer.setStatus(rs.getString("status"));
        return farmer;
    }
}
