package com.bamisu.log.gameserver.module.hunt.config;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.hunt.config.entities.RewardPowerVO;

import java.util.List;

public class HuntRewardConfig {
    public List<ResourcePackage> win;
    public List<ResourcePackage> lose;
    public List<RewardPowerVO> powers;

    public RewardPowerVO readPowerConfig(int power){
        for(RewardPowerVO huntPowerConfig : powers){
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
