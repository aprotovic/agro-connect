package models;

import java.sql.Timestamp;

/**
 * Merchant Model Class
 * Represents a merchant/buyer entity in the system
 */
public class Merchant {
    private int merchantId;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String businessType;
    private String address;
    private Timestamp registrationDate;
    private String status;
    
    // Constructors
    public Merchant() {
    }
    
    public Merchant(String name, String phone, String password) {
        this.name = name;
        this.phone = phone;
        this.password = password;
    }
    
    public Merchant(int merchantId, String name, String phone) {
        this.merchantId = merchantId;
        this.name = name;
        this.phone = phone;
    }
    
    // Getters and Setters
    public int getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getBusinessType() {
        return businessType;
    }
    
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Timestamp getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Merchant{" +
                "merchantId=" + merchantId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", businessType='" + businessType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
