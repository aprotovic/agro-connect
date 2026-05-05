package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database Configuration and Connection Pool Manager
 * Handles MySQL database connections with connection pooling
 */
public class DatabaseConfig {
    
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/agro_connect?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    // Connection pool settings
    private static final int POOL_SIZE = 10;
    private static final List<Connection> connectionPool = new ArrayList<>();
    private static final List<Connection> usedConnections = new ArrayList<>();
    
    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ MySQL JDBC Driver loaded successfully");
            
            // Initialize connection pool
            initializeConnectionPool();
            System.out.println("✓ Connection pool initialized with " + POOL_SIZE + " connections");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL JDBC Driver not found!");
            System.err.println("Please add mysql-connector-java-8.x.x.jar to classpath");
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize the connection pool with available connections
     */
    private static void initializeConnectionPool() {
        for (int i = 0; i < POOL_SIZE; i++) {
            try {
                Connection connection = createConnection();
                connectionPool.add(connection);
            } catch (SQLException e) {
                System.err.println("Failed to create connection #" + (i + 1));
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Create a new database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    /**
     * Get a connection from the pool
     * @return Available connection from pool
     * @throws SQLException if no connection available
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < POOL_SIZE * 2) {
                // Create new connection if pool is exhausted but under limit
                Connection newConnection = createConnection();
                usedConnections.add(newConnection);
                return newConnection;
            } else {
                throw new SQLException("Maximum connection pool size reached");
            }
        }
        
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        
        // Check if connection is still valid
        if (!connection.isValid(2)) {
            connection = createConnection();
        }
        
        usedConnections.add(connection);
        return connection;
    }
    
    /**
     * Return a connection back to the pool
     * @param connection Connection to be returned
     */
    public static synchronized void releaseConnection(Connection connection) {
        if (connection != null) {
            usedConnections.remove(connection);
            connectionPool.add(connection);
        }
    }
    
    /**
     * Close all connections in the pool
     */
    public static void closeAllConnections() {
        // Close all available connections
        for (Connection conn : connectionPool) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // Close all used connections
        for (Connection conn : usedConnections) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        connectionPool.clear();
        usedConnections.clear();
        System.out.println("All database connections closed");
    }
    
    /**
     * Test database connectivity
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isValid = conn.isValid(5);
            releaseConnection(conn);
            
            if (isValid) {
                System.out.println("✓ Database connection test: SUCCESS");
                System.out.println("  - URL: " + URL);
                System.out.println("  - User: " + USER);
            }
            return isValid;
        } catch (SQLException e) {
            System.err.println("✗ Database connection test: FAILED");
            System.err.println("  Error: " + e.getMessage());
            System.err.println("\nTroubleshooting:");
            System.err.println("  1. Verify MySQL is running in XAMPP");
            System.err.println("  2. Check database 'agro_connect' exists");
            System.err.println("  3. Verify credentials (user: root, password: empty)");
            return false;
        }
    }
    
    /**
     * Get current pool status
     */
    public static void printPoolStatus() {
        System.out.println("=== Connection Pool Status ===");
        System.out.println("Available connections: " + connectionPool.size());
        System.out.println("Used connections: " + usedConnections.size());
        System.out.println("Total connections: " + (connectionPool.size() + usedConnections.size()));
    }
}
