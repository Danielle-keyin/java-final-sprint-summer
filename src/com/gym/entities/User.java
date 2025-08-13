package com.gym.entities;

public abstract class User {
    protected int id;
    protected String username;
    protected String hashedPassword;
    protected String email;
    protected String phone;
    protected String address;
    protected Role role;

    public User(int id, String username, String hashedPassword, String email,
                String phone, String address, Role role) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getHashedPassword() { return hashedPassword; }

    // 🔽 add these three (public + exact names)
    public String getEmail()   { return email; }
    public String getPhone()   { return phone; }
    public String getAddress() { return address; }

    public Role getRole() { return role; }
}