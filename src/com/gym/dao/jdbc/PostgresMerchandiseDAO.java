package com.gym.dao.jdbc;

import com.gym.dao.MerchandiseDAO;
import java.math.BigDecimal;
import java.util.*;

public class PostgresMerchandiseDAO implements MerchandiseDAO {
    public PostgresMerchandiseDAO() {}

    @Override public List<Item> findAll() { throw new UnsupportedOperationException("DB impl pending"); }
    @Override public Item addItem(Item item) { throw new UnsupportedOperationException("DB impl pending"); }
    @Override public BigDecimal totalStockValue() { throw new UnsupportedOperationException("DB impl pending"); }
}
