package com.bamisu.log.sdk.module.giftcode.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Random;

public class GiftCodeExtra {
    public String moneyType;
    public String dataType;
    public double min;
    public double max;

    public GiftCodeExtra() {

    }

    @JsonIgnore()
    public double getRandom() {
        Random random = new Random();
        return this.min + (this.max - this.min) * random.nextDouble();
    }
}
