package com.bamisu.log.gameserver.module.adventure.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;

public class FastRewardVO {
    public int id;
    public int count;
    public String idMoney;
    public int cost;
    public int time;

    public FastRewardVO() {
    }

    public FastRewardVO(int id, int count, String idMoney, int cost, int time) {
        this.count = count;
        this.idMoney = idMoney;
        this.cost = cost;
        this.time = time;
        this.id = id;
    }
}
