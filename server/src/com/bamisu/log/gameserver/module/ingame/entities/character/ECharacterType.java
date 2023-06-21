package com.bamisu.log.gameserver.module.ingame.entities.character;

import com.bamisu.gamelib.entities.IDPrefix;

/**
 * Create by Popeye on 4:20 PM, 2/17/2020
 */
public enum ECharacterType {
    Hero(0),
    Creep(1),
    MiniBoss(2),
    Boss(3),
    Sage(4),
    Celestial(5),
    Other(6);

    int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    ECharacterType(int type){
        this.type = type;
    }

    public static ECharacterType fromType(int type){
        for(ECharacterType characterType : ECharacterType.values()){
            if(characterType.getType() == type){
                return characterType;
            }
        }

        return null;
    }

    public static int IdToType(String id){
        if(id.indexOf(IDPrefix.MINI_BOSS) == 0){
            return MiniBoss.getType();
        }else
        if(id.indexOf(IDPrefix.HERO) == 0){
            return Hero.getType();
        }else
        if(id.indexOf(IDPrefix.CREEP) == 0){
            return Creep.getType();
        }

        return -1;
    }
}
