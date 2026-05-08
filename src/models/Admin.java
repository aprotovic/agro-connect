package models;

import java.sql.Timestamp;

public class Admin {
    private int adminId;
    private String username;
    private String password;
    private String role;
    private Timestamp createdAt;

    public Admin() {
    }

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "admin";
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
