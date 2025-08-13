package com.gym.dao;

import java.math.BigDecimal;
import java.util.*;

public interface MerchandiseDAO {
    class Item {
        public int id; public String name; public String type;
        public BigDecimal price; public int quantity;
        public Item(int id, String name, String type, BigDecimal price, int qty){
            this.id=id; this.name=name; this.type=type; this.price=price; this.quantity=qty;
        }
        public String toString(){ return name+" ("+type+") - $"+price+" x"+quantity; }
    }
    List<Item> findAll();
    Item addItem(Item item);
    BigDecimal totalStockValue();
}