package com.gym.dao.mock;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMerchandiseDAO implements com.gym.dao.MerchandiseDAO {
    private final List<Item> items = new ArrayList<>();
    private final AtomicInteger idSeq = new AtomicInteger(1);

    @Override public List<Item> findAll() { return new ArrayList<>(items); }
    @Override public Item addItem(Item item) {
        Item copy = new Item(idSeq.getAndIncrement(), item.name, item.type, item.price, item.quantity);
        items.add(copy);
        return copy;
    }
    @Override public BigDecimal totalStockValue() {
        return items.stream()
            .map(i -> i.price.multiply(BigDecimal.valueOf(i.quantity)))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
