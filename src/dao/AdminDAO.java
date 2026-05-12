package dao;

import config.DatabaseConfig;
import models.Admin;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class AdminDAO {

    public Admin loginAdmin(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ?";
        Connection conn = null;

        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

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
                    Admin admin = new Admin();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPassword(rs.getString("password"));
                    admin.setRole(rs.getString("role"));
                    admin.setCreatedAt(rs.getTimestamp("created_at"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during admin login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return null;
    }
}
