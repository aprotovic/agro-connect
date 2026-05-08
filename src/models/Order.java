package models;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Order Model Class
 * Represents an order placed by merchants
 */
public class Order {
    private int orderId;
    private int merchantId;
    private int productId;
    private int quantity;
    private double totalPrice;
    private String status;
    private Timestamp orderDate;
    private Date deliveryDate;
    private String paymentStatus;
    private String notes;
    
    // Additional fields for joined queries
    private String merchantName;
    private String merchantPhone;
    private String productName;
    private String unit;
    private double unitPrice;
    private String farmerName;
    private String farmerLocation;
    private int farmerId;
    
    // Constructors
    public Order() {
    }
    
    public Order(int merchantId, int productId, int quantity, double totalPrice) {
        this.merchantId = merchantId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = "pending";
        this.paymentStatus = "pending";
    }
    
    public Order(int orderId, int merchantId, int productId, int quantity, 
                 double totalPrice, String status) {
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
    }
    
    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }
    
    public Date getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getMerchantName() {
        return merchantName;
    }
    
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
    
    public String getMerchantPhone() {
        return merchantPhone;
    }
    
    public void setMerchantPhone(String merchantPhone) {
        this.merchantPhone = merchantPhone;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
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
    
    public int getFarmerId() {
        return farmerId;
    }
    
    public void setFarmerId(int farmerId) {
        this.farmerId = farmerId;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", merchantId=" + merchantId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
