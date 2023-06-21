package com.bamisu.log.gameserver.module.store.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

public class ItemVO {
    public ResourcePackage price;
    public ResourcePackage item;

    public ItemVO(ResourcePackage price, ResourcePackage item) {
        this.price = price;
        this.item = item;
    }

    public ItemVO() {
    }
}
