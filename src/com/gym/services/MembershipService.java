package com.gym.services;

import com.gym.dao.MembershipDAO;
import java.math.BigDecimal;

public class MembershipService {
    private final MembershipDAO dao;
    public MembershipService(MembershipDAO dao){ this.dao = dao; }

    public boolean purchase(int userId, String type, BigDecimal cost, String desc){
        return dao.purchaseMembership(userId, type, cost, desc);
    }

    public BigDecimal totalSpentByMember(int userId){
        return dao.totalSpentByMember(userId);
    }
        public BigDecimal totalRevenue(){
        return dao.totalRevenue();
    }
}