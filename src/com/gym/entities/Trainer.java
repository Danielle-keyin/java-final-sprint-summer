package com.gym.entities;
public class Trainer extends User {
    public Trainer(int id, String u, String hpw, String e, String p, String a) {
        super(id, u, hpw, e, p, a, Role.TRAINER);
    }
}