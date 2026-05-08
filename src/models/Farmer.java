package models;

import java.sql.Timestamp;

/**
 * Farmer Model Class
 * Represents a farmer entity in the system
 */
public class Farmer {
    private int farmerId;
    private String name;
    private String location;
    private String phone;
    private String email;
    private String password;
    private Timestamp registrationDate;
    private String status;
    
    // Constructors
    public Farmer() {
    }
    
    public Farmer(String name, String location, String phone, String password) {
        this.name = name;
        this.location = location;
        this.phone = phone;
        this.password = password;
    }
    
    public Farmer(int farmerId, String name, String location, String phone) {
        this.farmerId = farmerId;
        this.name = name;
        this.location = location;
        this.phone = phone;
    }
    
    // Getters and Setters
    public int getFarmerId() {
        return farmerId;
    }
    
    public void setFarmerId(int farmerId) {
        this.farmerId = farmerId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
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
        return "Farmer{" +
                "farmerId=" + farmerId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
