package com.bamisu.log.gameserver.module.event.event.grand_opening_checkin.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;

public class GrandOpeningCheckInConfig {
    public int time;
    public List<ResourcePackage> listGift;

    public GrandOpeningCheckInConfig(int time, List<ResourcePackage> listGift) {
        this.time = time;
        this.listGift = listGift;
    }

    public GrandOpeningCheckInConfig() {
    }
}
