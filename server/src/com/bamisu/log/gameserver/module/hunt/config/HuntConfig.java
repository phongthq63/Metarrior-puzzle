package com.bamisu.log.gameserver.module.hunt.config;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.hunt.config.entities.HuntPowerConfig;

import java.util.ArrayList;
import java.util.List;

public class HuntConfig {
    public ResourcePackage fightCost;
    public List<HuntPowerConfig> powers;


    public List<ResourcePackage> readFightCost(){
        List<ResourcePackage> list = new ArrayList<>();
        list.add(new ResourcePackage(fightCost.id, -fightCost.amount));

        return list;
    }

    public HuntPowerConfig readPowerConfig(int power){
        for(HuntPowerConfig huntPowerConfig : powers){
            if(huntPowerConfig.range.size() == 1){
                if(power >= huntPowerConfig.range.get(0)) return huntPowerConfig;
            }

            if(huntPowerConfig.range.size() == 2){
                if(power >= huntPowerConfig.range.get(0) && power <= huntPowerConfig.range.get(1)) return huntPowerConfig;
            }
        }

        return null;
    }
}
