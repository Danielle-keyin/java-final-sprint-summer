package com.gym.entities;
public class Member extends User {
    public Member(int id, String u, String hpw, String e, String p, String a) {
        super(id, u, hpw, e, p, a, Role.MEMBER);
    }
}