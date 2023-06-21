package com.bamisu.log.gameserver.module.store.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;

public class StoreVO {
    public int id;
    public int size;
    public String name;
    public int time;
    public List<ResourcePackage> refresh;
    public List<SellVO> sells;

    public StoreVO(int id, int size, String name, int time, List<ResourcePackage> refresh, List<SellVO> sells) {
        this.id = id;
        this.size = size;
        this.name = name;
        this.time = time;
        this.refresh = refresh;
        this.sells = sells;
    }

    public StoreVO() {
    }
}
