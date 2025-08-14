package com.gym.dao.jdbc;

import com.gym.dao.MembershipDAO;
import java.math.BigDecimal;

public class PostgresMembershipDAO implements MembershipDAO {
    public PostgresMembershipDAO() {}

    @Override public boolean purchaseMembership(int userId, String type, BigDecimal cost, String description) {
        throw new UnsupportedOperationException("DB impl pending");
    }
    @Override public BigDecimal totalRevenue() {
        throw new UnsupportedOperationException("DB impl pending");
    }
    @Override public BigDecimal totalSpentByMember(int userId) {
        throw new UnsupportedOperationException("DB impl pending");
    }
}
