package com.bamisu.log.gameserver.module.store.entities;

public class DiscountStoreVO {
    public int sale;
    public int rate;

    public DiscountStoreVO(int sale, int rate) {
        this.sale = sale;
        this.rate = rate;
    }

    public DiscountStoreVO() {
    }
}
