package com.bamisu.log.gameserver.module.campaign.config.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:11 PM, 2/5/2020
 */
public class Area {
    public String name;
    public List<Station> station = new ArrayList<>();
    public List<StarRewardVO> reward = new ArrayList<>();

    public Station readStation(int id){
        return station.get(id);
    }
}
