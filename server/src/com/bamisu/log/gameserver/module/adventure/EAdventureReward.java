package com.bamisu.log.gameserver.module.adventure;

import com.bamisu.gamelib.entities.EVip;

public enum EAdventureReward {
    FREE(0),
    ADS(1),
    COST_200(2),
    COST_400(3);

    int id;

    EAdventureReward() {
    }

    EAdventureReward(int id) {
        this.id = id;
    }

    public EAdventureReward fromIntValue(int id){
        for (EAdventureReward eAdventureReward : values()){
            if(eAdventureReward.getId() == id) return eAdventureReward;
        }
        return null;
    }


    public int getId() {
        return id;
    }

}
