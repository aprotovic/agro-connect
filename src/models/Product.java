package models;

import java.sql.Timestamp;

/**
 * Product Model Class
 * Represents a product listed by farmers
 */
public class Product {
    private int productId;
    private int farmerId;
    private String productName;
    private String category;
    private int quantity;
    private String unit;
    private double price;
    private String description;
    private String imageUrl;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String status;
    
    // Additional fields for joined queries
    private String farmerName;
    private String farmerLocation;
    private String farmerPhone;
    
    // Constructors
    public Product() {
    }
    
    public Product(int farmerId, String productName, int quantity, double price) {
        this.farmerId = farmerId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }
    
    public Product(int productId, int farmerId, String productName, String category, 
                   int quantity, String unit, double price, String status) {
        this.productId = productId;
        this.farmerId = farmerId;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.status = status;
    }
    
    // Getters and Setters
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public int getFarmerId() {
        return farmerId;
    }
    
    public void setFarmerId(int farmerId) {
        this.farmerId = farmerId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Timestamp getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
    
    public Timestamp getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getFarmerName() {
        return farmerName;
    }
    
    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }
    
    public String getFarmerLocation() {
        return farmerLocation;
    }
    
    public void setFarmerLocation(String farmerLocation) {
        this.farmerLocation = farmerLocation;
    }
    
    public String getFarmerPhone() {
        return farmerPhone;
    }
    
    public void setFarmerPhone(String farmerPhone) {
        this.farmerPhone = farmerPhone;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", farmerId=" + farmerId +
                ", productName='" + productName + '\'' +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}
