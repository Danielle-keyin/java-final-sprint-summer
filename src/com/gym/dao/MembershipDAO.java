package com.gym.dao;

import java.math.BigDecimal;

public interface MembershipDAO {
    boolean purchaseMembership(int userId, String type, BigDecimal cost, String description);
    BigDecimal totalRevenue(); // admin report
    BigDecimal totalSpentByMember(int userId);
}