package com.gym.entities;
public class Admin extends User {
    public Admin(int id, String u, String hpw, String e, String p, String a) {
        super(id, u, hpw, e, p, a, Role.ADMIN);
    }
}