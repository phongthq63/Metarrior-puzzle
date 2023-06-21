package com.bamisu.log.gameserver.module.adventure.entities;

public class GiftVO {
    public int represent;
    public String id;
    public int amount;

    public GiftVO() {
    }

    public GiftVO(int represent, String id, int amount) {
        this.represent = represent;
        this.id = id;
        this.amount = amount;
    }
}
