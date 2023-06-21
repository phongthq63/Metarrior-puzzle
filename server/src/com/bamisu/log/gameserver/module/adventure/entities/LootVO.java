package com.bamisu.log.gameserver.module.adventure.entities;

public class LootVO {
    public String id;
    public int amount;
    public int perMinute;

    public LootVO(String id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public LootVO(String id, int amount, int perMinute) {
        this.id = id;
        this.amount = amount;
        this.perMinute = perMinute;
    }

    public LootVO() {
    }

//    public LootVO(LootVO lootVO) {
//        this.amount = lootVO.amount;
//        this.id = lootVO.id;
//    }

    public LootVO(LootVO lootVO) {
        this.id = lootVO.id;
        this.perMinute = lootVO.perMinute;
        this.amount = lootVO.amount;
    }
}
