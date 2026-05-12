package dao;

import config.DatabaseConfig;
import models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product operations
 * Handles all database operations related to products
 */
public class ProductDAO {
    
    /**
     * Add a new product
     * @param product Product object with details
     * @return Generated product ID, or -1 if failed
     */
    public int addProduct(Product product) {
        String sql = "INSERT INTO product (farmer_id, product_name, category, quantity, unit, price, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, product.getFarmerId());
            stmt.setString(2, product.getProductName());
            stmt.setString(3, product.getCategory());
            stmt.setInt(4, product.getQuantity());
            stmt.setString(5, product.getUnit());
            stmt.setDouble(6, product.getPrice());
            stmt.setString(7, product.getDescription());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return -1;
    }
    
    /**
     * Update product details
     * @param product Product object with updated details
     * @return true if successful, false otherwise
     */
    public boolean updateProduct(Product product) {
        String sql = "UPDATE product SET product_name = ?, category = ?, quantity = ?, " +
                     "unit = ?, price = ?, description = ? WHERE product_id = ?";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getCategory());
            stmt.setInt(3, product.getQuantity());
            stmt.setString(4, product.getUnit());
            stmt.setDouble(5, product.getPrice());
            stmt.setString(6, product.getDescription());
            stmt.setInt(7, product.getProductId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return false;
    }
    
    /**
     * Get product by ID
     * @param productId Product's ID
     * @return Product object, or null if not found
     */
    public Product getProductById(int productId) {
        String sql = "SELECT * FROM product WHERE product_id = ?";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return null;
    }
    
    /**
     * Get all products (with farmer details)
     * @return List of all available products
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, f.name as farmer_name, f.location as farmer_location, f.phone as farmer_phone " +
                     "FROM product p " +
                     "INNER JOIN farmer f ON p.farmer_id = f.farmer_id " +
                     "WHERE p.status = 'available' " +
                     "ORDER BY p.created_date DESC";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                product.setFarmerName(rs.getString("farmer_name"));
                product.setFarmerLocation(rs.getString("farmer_location"));
                product.setFarmerPhone(rs.getString("farmer_phone"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all products: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return products;
    }
    
    /**
     * Get products by farmer ID
     * @param farmerId Farmer's ID
     * @return List of products by the farmer
     */
    public List<Product> getProductsByFarmer(int farmerId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE farmer_id = ? ORDER BY created_date DESC";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, farmerId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching farmer products: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return products;
    }
    
    /**
     * Search products by name or category
     * @param searchTerm Search term
     * @return List of matching products
     */
    public List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, f.name as farmer_name, f.location as farmer_location, f.phone as farmer_phone " +
                     "FROM product p " +
                     "INNER JOIN farmer f ON p.farmer_id = f.farmer_id " +
                     "WHERE p.status = 'available' AND " +
                     "(p.product_name LIKE ? OR p.category LIKE ? OR f.location LIKE ?) " +
                     "ORDER BY p.created_date DESC";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                product.setFarmerName(rs.getString("farmer_name"));
                product.setFarmerLocation(rs.getString("farmer_location"));
                product.setFarmerPhone(rs.getString("farmer_phone"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return products;
    }
    
    /**
     * Get products by category
     * @param category Product category
     * @return List of products in the category
     */
    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, f.name as farmer_name, f.location as farmer_location, f.phone as farmer_phone " +
                     "FROM product p " +
                     "INNER JOIN farmer f ON p.farmer_id = f.farmer_id " +
                     "WHERE p.category = ? AND p.status = 'available' " +
                     "ORDER BY p.created_date DESC";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                product.setFarmerName(rs.getString("farmer_name"));
                product.setFarmerLocation(rs.getString("farmer_location"));
                product.setFarmerPhone(rs.getString("farmer_phone"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products by category: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return products;
    }
    
    /**
     * Delete product
     * @param productId Product's ID
     * @return true if successful, false otherwise
     */
    public boolean deleteProduct(int productId) {
        String sql = "UPDATE product SET status = 'discontinued' WHERE product_id = ?";
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConfig.releaseConnection(conn);
            }
        }
        return false;
    }
    
    /**
     * Extract Product object from ResultSet
     */
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setFarmerId(rs.getInt("farmer_id"));
        product.setProductName(rs.getString("product_name"));
        product.setCategory(rs.getString("category"));
        product.setQuantity(rs.getInt("quantity"));
        product.setUnit(rs.getString("unit"));
        product.setPrice(rs.getDouble("price"));
        product.setDescription(rs.getString("description"));
        product.setImageUrl(rs.getString("image_url"));
        product.setCreatedDate(rs.getTimestamp("created_date"));
        product.setUpdatedDate(rs.getTimestamp("updated_date"));
        product.setStatus(rs.getString("status"));
        return product;
    }
}
