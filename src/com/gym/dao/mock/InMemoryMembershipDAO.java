package com.gym.dao.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InMemoryMembershipDAO implements com.gym.dao.MembershipDAO {
    static class Purchase {
        int userId; String type; BigDecimal cost; String desc;
        Purchase(int userId, String type, BigDecimal cost, String desc){
            this.userId=userId; this.type=type; this.cost=cost; this.desc=desc;
        }
    }
    private final List<Purchase> purchases = new ArrayList<>();

    @Override public boolean purchaseMembership(int userId, String type, BigDecimal cost, String description) {
        purchases.add(new Purchase(userId, type, cost, description));
        return true;
    }
    @Override public BigDecimal totalRevenue() {
        return purchases.stream().map(p -> p.cost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @Override public BigDecimal totalSpentByMember(int userId) {
        return purchases.stream().filter(p -> p.userId == userId)
                .map(p -> p.cost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}