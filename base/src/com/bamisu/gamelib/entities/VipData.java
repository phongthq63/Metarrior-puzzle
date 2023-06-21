package com.bamisu.gamelib.entities;

/**
 * Create by Popeye on 9:46 AM, 7/14/2020
 */
public class VipData {
    public EVip eVip;
    public int expired;

    public VipData() {
    }

    public VipData(EVip eVip, int expired) {
        this.eVip = eVip;
        this.expired = expired;
    }
}
