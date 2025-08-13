package com.gym.services;

import com.gym.dao.MerchandiseDAO;
import java.math.BigDecimal;
import java.util.List;

public class MerchandiseService {
    private final MerchandiseDAO dao;
    public MerchandiseService(MerchandiseDAO dao){ this.dao = dao; }
    public List<MerchandiseDAO.Item> list(){ return dao.findAll(); }
    public MerchandiseDAO.Item add(MerchandiseDAO.Item item){ return dao.addItem(item); }
    public BigDecimal stockValue(){ return dao.totalStockValue(); }
}