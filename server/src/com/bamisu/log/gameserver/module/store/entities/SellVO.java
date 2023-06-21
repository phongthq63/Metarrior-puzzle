package com.bamisu.log.gameserver.module.store.entities;

import java.util.List;

public class SellVO {
    public int[] slots;
    public int[] items;
    public List<DiscountStoreVO> discount;

    public SellVO(int[] slots, int[] items, List<DiscountStoreVO> discount) {
        this.slots = slots;
        this.items = items;
        this.discount = discount;
    }

    public SellVO() {
    }
}
