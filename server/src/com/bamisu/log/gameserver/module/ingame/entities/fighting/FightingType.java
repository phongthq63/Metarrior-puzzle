package com.bamisu.log.gameserver.module.ingame.entities.fighting;

/**
 * Create by Popeye on 5:50 PM, 1/14/2020
 */
public enum FightingType {
    PvM(0),
    PvP(1),
    PvB(2);

    int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    FightingType(int type){
        this.type = type;
    }

    public static FightingType fromType(int type){
        for(FightingType fightingType : FightingType.values()){
            if(fightingType.getType() == type){
                return fightingType;
            }
        }

        return null;
    }
}
