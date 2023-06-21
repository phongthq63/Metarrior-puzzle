package com.bamisu.log.gameserver.module.bag.entities;

public class ItemGet {
    public String hash;
    public int count;

    public static ItemGet create(String hash, int count) {
        ItemGet itemGet = new ItemGet();
        itemGet.hash = hash;
        itemGet.count = count;

        return itemGet;
    }
}
