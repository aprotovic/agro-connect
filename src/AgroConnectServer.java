import com.sun.net.httpserver.*;
import config.DatabaseConfig;
import dao.*;
import models.*;
import utils.JsonResponse;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Agro-Connect HTTP Server
 * Main server class that handles all HTTP requests
 * Multi-threaded server using Java HTTP Server
 */
public class AgroConnectServer {

    private static final int PORT = 8080;
    private static final String WEBAPP_DIR = "webapp";

    private static final FarmerDAO farmerDAO = new FarmerDAO();
    private static final MerchantDAO merchantDAO = new MerchantDAO();
    private static final ProductDAO productDAO = new ProductDAO();
    private static final OrderDAO orderDAO = new OrderDAO();

    public static void main(String[] args) throws IOException {
        // Test database connection
        System.out.println("=== Agro-Connect Server Starting ===");
        System.out.println("Testing database connection...");
        if (!DatabaseConfig.testConnection()) {
            System.err.println("Warning: Failed to connect to database. Running in UI-only mode.");
            System.err.println("Note: API endpoints requiring database will fail.");
        }

        // Create HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Set up thread pool for handling multiple clients
        server.setExecutor(Executors.newFixedThreadPool(20)); // Support 20 concurrent connections

        // Register API endpoints
        registerApiEndpoints(server);

        // Register static file handler for HTML/CSS/JS
        server.createContext("/", new StaticFileHandler());

        // Start the server
        server.start();

        System.out.println("✓ Server started successfully!");
        System.out.println("=================================");
        System.out.println("  Server URL: http://localhost:" + PORT);
        System.out.println("  Thread Pool: 20 concurrent connections");
        System.out.println("  Database: Connected");
        System.out.println("=================================");
        System.out.println("Access the application at: http://localhost:" + PORT);
        System.out.println("Press Ctrl+C to stop the server");

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\nShutting down server...");
            server.stop(0);
            DatabaseConfig.closeAllConnections();
            System.out.println("Server stopped gracefully");
        }));
    }

    /**
     * Register all API endpoints
     */
    private static void registerApiEndpoints(HttpServer server) {
        // Farmer endpoints
        server.createContext("/api/farmer/register", new FarmerRegisterHandler());
        server.createContext("/api/farmer/login", new FarmerLoginHandler());
        server.createContext("/api/farmer/products", new FarmerProductsHandler());
        server.createContext("/api/farmer/orders", new FarmerOrdersHandler());

        // Merchant endpoints
        server.createContext("/api/merchant/register", new MerchantRegisterHandler());
        server.createContext("/api/merchant/login", new MerchantLoginHandler());
        server.createContext("/api/merchant/orders", new MerchantOrdersHandler());

        // Product endpoints
        server.createContext("/api/products", new ProductsHandler());
        server.createContext("/api/product/add", new ProductAddHandler());
        server.createContext("/api/product/update", new ProductUpdateHandler());
        server.createContext("/api/product/search", new ProductSearchHandler());

        // Order endpoints
        server.createContext("/api/order/place", new OrderPlaceHandler());
        server.createContext("/api/order/status", new OrderStatusHandler());

        // Admin endpoints
        server.createContext("/api/admin/login", new AdminLoginHandler());
        server.createContext("/api/admin/stats", new AdminStatsHandler());

        System.out.println("✓ API endpoints registered");
    }

    // ==================== FARMER HANDLERS ====================

    static class FarmerRegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);

                String name = params.get("name");
                String location = params.get("location");
                String phone = params.get("phone");
                String password = params.get("password");
                String email = params.getOrDefault("email", "");

                if (name == null || location == null || phone == null || password == null) {
                    JsonResponse.sendError(exchange, 400, "Missing required fields");
                    return;
                }

                if (farmerDAO.phoneExists(phone)) {
                    JsonResponse.sendError(exchange, 409, "Phone number already registered");
                    return;
                }

                Farmer farmer = new Farmer(name, location, phone, password);
                farmer.setEmail(email);
                int farmerId = farmerDAO.registerFarmer(farmer);

                if (farmerId > 0) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("farmer_id", farmerId);
                    data.put("name", name);
                    JsonResponse.sendSuccess(exchange, "Farmer registered successfully", data);
                } else {
                    JsonResponse.sendError(exchange, 500, "Failed to register farmer");
                }
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    static class FarmerLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);

                String phone = params.get("phone");
                String password = params.get("password");

                if (phone == null || password == null) {
                    JsonResponse.sendError(exchange, 400, "Missing credentials");
                    return;
                }

                Farmer farmer = farmerDAO.loginFarmer(phone, password);

                if (farmer != null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("farmer_id", farmer.getFarmerId());
                    data.put("name", farmer.getName());
                    data.put("location", farmer.getLocation());
                    data.put("phone", farmer.getPhone());
                    data.put("email", farmer.getEmail());
                    JsonResponse.sendSuccess(exchange, "Login successful", data);
                } else {
                    JsonResponse.sendError(exchange, 401, "Invalid credentials");
                }
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    static class FarmerProductsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseQueryString(exchange.getRequestURI().getQuery());
                String farmerIdStr = params.get("farmer_id");

                if (farmerIdStr == null) {
                    JsonResponse.sendError(exchange, 400, "Missing farmer_id");
                    return;
                }

                int farmerId = Integer.parseInt(farmerIdStr);
                List<Product> products = productDAO.getProductsByFarmer(farmerId);

                JsonResponse.sendSuccess(exchange, convertProductsToMaps(products));
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    static class FarmerOrdersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseQueryString(exchange.getRequestURI().getQuery());
                String farmerIdStr = params.get("farmer_id");

                if (farmerIdStr == null) {
                    JsonResponse.sendError(exchange, 400, "Missing farmer_id");
                    return;
                }

                int farmerId = Integer.parseInt(farmerIdStr);
                List<Order> orders = orderDAO.getOrdersByFarmer(farmerId);

                JsonResponse.sendSuccess(exchange, convertOrdersToMaps(orders));
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    // ==================== MERCHANT HANDLERS ====================

    static class MerchantRegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);

                String name = params.get("name");
                String phone = params.get("phone");
                String password = params.get("password");
                String email = params.getOrDefault("email", "");
                String businessType = params.getOrDefault("business_type", "");
                String address = params.getOrDefault("address", "");

                if (name == null || phone == null || password == null) {
                    JsonResponse.sendError(exchange, 400, "Missing required fields");
                    return;
                }

                if (merchantDAO.phoneExists(phone)) {
                    JsonResponse.sendError(exchange, 409, "Phone number already registered");
                    return;
                }

                Merchant merchant = new Merchant(name, phone, password);
                merchant.setEmail(email);
                merchant.setBusinessType(businessType);
                merchant.setAddress(address);
                int merchantId = merchantDAO.registerMerchant(merchant);

                if (merchantId > 0) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("merchant_id", merchantId);
                    data.put("name", name);
                    JsonResponse.sendSuccess(exchange, "Merchant registered successfully", data);
                } else {
                    JsonResponse.sendError(exchange, 500, "Failed to register merchant");
                }
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    static class MerchantLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);

                String phone = params.get("phone");
                String password = params.get("password");

                if (phone == null || password == null) {
                    JsonResponse.sendError(exchange, 400, "Missing credentials");
                    return;
                }

                Merchant merchant = merchantDAO.loginMerchant(phone, password);

                if (merchant != null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("merchant_id", merchant.getMerchantId());
                    data.put("name", merchant.getName());
                    data.put("phone", merchant.getPhone());
                    data.put("email", merchant.getEmail());
                    JsonResponse.sendSuccess(exchange, "Login successful", data);
                } else {
                    JsonResponse.sendError(exchange, 401, "Invalid credentials");
                }
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    static class MerchantOrdersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseQueryString(exchange.getRequestURI().getQuery());
                String merchantIdStr = params.get("merchant_id");

                if (merchantIdStr == null) {
                    JsonResponse.sendError(exchange, 400, "Missing merchant_id");
                    return;
                }

                int merchantId = Integer.parseInt(merchantIdStr);
                List<Order> orders = orderDAO.getOrdersByMerchant(merchantId);

                JsonResponse.sendSuccess(exchange, convertOrdersToMaps(orders));
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    // ==================== PRODUCT HANDLERS ====================

    static class ProductsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Product> products = productDAO.getAllProducts();
                JsonResponse.sendSuccess(exchange, convertProductsToMaps(products));
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    static class ProductAddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);

                try {
                    int farmerId = Integer.parseInt(params.get("farmer_id"));
                    String productName = params.get("product_name");
                    String category = params.getOrDefault("category", "General");
                    int quantity = Integer.parseInt(params.get("quantity"));
                    String unit = params.getOrDefault("unit", "kg");
                    double price = Double.parseDouble(params.get("price"));
                    String description = params.getOrDefault("description", "");

                    Product product = new Product(farmerId, productName, quantity, price);
                    product.setCategory(category);
                    product.setUnit(unit);
                    product.setDescription(description);

                    int productId = productDAO.addProduct(product);

                    if (productId > 0) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("product_id", productId);
                        JsonResponse.sendSuccess(exchange, "Product added successfully", data);
                    } else {
                        JsonResponse.sendError(exchange, 500, "Failed to add product");
                    }
                } catch (Exception e) {
                    JsonResponse.sendError(exchange, 400, "Invalid data: " + e.getMessage());
                }
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    static class ProductUpdateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);

                try {
                    int productId = Integer.parseInt(params.get("product_id"));
                    String productName = params.get("product_name");
                    String category = params.get("category");
                    int quantity = Integer.parseInt(params.get("quantity"));
                    String unit = params.get("unit");
                    double price = Double.parseDouble(params.get("price"));
                    String description = params.getOrDefault("description", "");

                    Product product = productDAO.getProductById(productId);
                    if (product == null) {
                        JsonResponse.sendError(exchange, 404, "Product not found");
                        return;
                    }

                    product.setProductName(productName);
                    product.setCategory(category);
                    product.setQuantity(quantity);
                    product.setUnit(unit);
                    product.setPrice(price);
                    product.setDescription(description);

                    boolean success = productDAO.updateProduct(product);

                    if (success) {
                        JsonResponse.sendSuccess(exchange, "Product updated successfully", null);
                    } else {
                        JsonResponse.sendError(exchange, 500, "Failed to update product");
                    }
                } catch (Exception e) {
                    JsonResponse.sendError(exchange, 400, "Invalid data: " + e.getMessage());
                }
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    static class ProductSearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseQueryString(exchange.getRequestURI().getQuery());
                String query = params.getOrDefault("q", "");

                List<Product> products = productDAO.searchProducts(query);
                JsonResponse.sendSuccess(exchange, convertProductsToMaps(products));
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    // ==================== ORDER HANDLERS ====================

    static class OrderPlaceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);

                try {
                    int merchantId = Integer.parseInt(params.get("merchant_id"));
                    int productId = Integer.parseInt(params.get("product_id"));
                    int quantity = Integer.parseInt(params.get("quantity"));

                    Order order = new Order(merchantId, productId, quantity, 0); // Total calculated in DAO
                    int orderId = orderDAO.placeOrder(order);

                    if (orderId > 0) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("order_id", orderId);
                        JsonResponse.sendSuccess(exchange, "Order placed successfully", data);
                    } else if (orderId == -2) {
                        JsonResponse.sendError(exchange, 400, "Insufficient quantity available");
                    } else {
                        JsonResponse.sendError(exchange, 500, "Failed to place order");
                    }
                } catch (Exception e) {
                    JsonResponse.sendError(exchange, 400, "Invalid data: " + e.getMessage());
                }
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    static class OrderStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);

                try {
                    int orderId = Integer.parseInt(params.get("order_id"));
                    String status = params.get("status");

                    boolean success = orderDAO.updateOrderStatus(orderId, status);

                    if (success) {
                        JsonResponse.sendSuccess(exchange, "Order status updated", null);
                    } else {
                        JsonResponse.sendError(exchange, 500, "Failed to update status");
                    }
                } catch (Exception e) {
                    JsonResponse.sendError(exchange, 400, "Invalid data: " + e.getMessage());
                }
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    // ==================== STATIC FILE HANDLER ====================

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File(WEBAPP_DIR + path);

            if (!file.exists() || file.isDirectory()) {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            String contentType = getContentType(path);
            byte[] fileContent = Files.readAllBytes(file.toPath());

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileContent.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileContent);
            os.close();
        }

        private String getContentType(String path) {
            if (path.endsWith(".html"))
                return "text/html; charset=UTF-8";
            if (path.endsWith(".css"))
                return "text/css; charset=UTF-8";
            if (path.endsWith(".js"))
                return "application/javascript; charset=UTF-8";
            if (path.endsWith(".json"))
                return "application/json; charset=UTF-8";
            if (path.endsWith(".png"))
                return "image/png";
            if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
                return "image/jpeg";
            if (path.endsWith(".gif"))
                return "image/gif";
            if (path.endsWith(".svg"))
                return "image/svg+xml";
            return "application/octet-stream";
        }
    }

    // ==================== UTILITY METHODS ====================

    private static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int bytesRead;
        while ((bytesRead = isr.read(buf)) != -1) {
            sb.append(buf, 0, bytesRead);
        }
        return parseQueryString(sb.toString());
    }

    private static Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        for (String param : query.split("&")) {
            int idx = param.indexOf("=");
            try {
                if (idx > 0) {
                    String key = URLDecoder.decode(param.substring(0, idx), StandardCharsets.UTF_8.name());
                    String value = URLDecoder.decode(param.substring(idx + 1), StandardCharsets.UTF_8.name());
                    params.put(key, value);
                } else if (idx == -1 && !param.isEmpty()) {
                    String key = URLDecoder.decode(param, StandardCharsets.UTF_8.name());
                    params.put(key, "");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return params;
    }

    private static List<Map<String, Object>> convertProductsToMaps(List<Product> products) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Product p : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("product_id", p.getProductId());
            map.put("farmer_id", p.getFarmerId());
            map.put("product_name", p.getProductName());
            map.put("category", p.getCategory());
            map.put("quantity", p.getQuantity());
            map.put("unit", p.getUnit());
            map.put("price", p.getPrice());
            map.put("description", p.getDescription());
            map.put("status", p.getStatus());
            if (p.getFarmerName() != null) {
                map.put("farmer_name", p.getFarmerName());
                map.put("farmer_location", p.getFarmerLocation());
                map.put("farmer_phone", p.getFarmerPhone());
            }
            result.add(map);
        }
        return result;
    }

    private static List<Map<String, Object>> convertOrdersToMaps(List<Order> orders) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Order o : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("order_id", o.getOrderId());
            map.put("merchant_id", o.getMerchantId());
            map.put("product_id", o.getProductId());
            map.put("quantity", o.getQuantity());
            map.put("total_price", o.getTotalPrice());
            map.put("status", o.getStatus());
            map.put("payment_status", o.getPaymentStatus());
            map.put("order_date", o.getOrderDate() != null ? o.getOrderDate().toString() : "");
            map.put("merchant_name", o.getMerchantName());
            map.put("product_name", o.getProductName());
            map.put("unit", o.getUnit());
            map.put("farmer_name", o.getFarmerName());
            map.put("farmer_location", o.getFarmerLocation());
            result.add(map);
        }
        return result;
        }


    // ==========================================
    // Admin Handlers
    // ==========================================

    static class AdminLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);
                System.out.println("DEBUG: Admin Login Request params: " + params);

                String username = params.get("username");
                String password = params.get("password");

                System.out.println("DEBUG: Attempting login for user: " + username);
                Admin admin = new AdminDAO().loginAdmin(username, password);

                if (admin != null) {
                    System.out.println("DEBUG: Login Success for: " + username);
                    Map<String, Object> data = new HashMap<>();
                    data.put("admin_id", admin.getAdminId());
                    data.put("username", admin.getUsername());
                    JsonResponse.sendSuccess(exchange, "Login successful", data);
                } else {
                    System.out.println("DEBUG: Login Failed for: " + username);
                    JsonResponse.sendError(exchange, 401, "Invalid credentials");
                }
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    static class AdminStatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    // Gather system stats
                    int farmers = new FarmerDAO().getAllFarmers().size();
                    int merchants = new MerchantDAO().getAllMerchants().size();
                    
                    Map<String, Object> data = new HashMap<>();
                    data.put("total_farmers", farmers);
                    data.put("total_merchants", merchants);
                    
                    JsonResponse.sendSuccess(exchange, "Stats fetched", data);
                } catch (Exception e) {
                   e.printStackTrace();
                   JsonResponse.sendError(exchange, 500, "Error fetching stats");
                }
            } else {
                JsonResponse.sendError(exchange, 405, "Method not allowed");
            }
        }
    }
}
