package com.bamisu.log.gameserver.module.ingame.entities.effect;

/**
 * Create by Popeye on 3:19 PM, 3/18/2020
 */
public enum EffectCategory {
    HARD(0),
    SOFT(1),
    OTHER(2);

    int id;
    EffectCategory(int id){
        this.id = id;
    }

    public int getID(){
        return this.id;
    }

    public static EffectCategory fromID(int id){
        for(EffectCategory e : EffectCategory.values()){
            if(e.getID() == id){
                return e;
            }
        }

        return null;
    }

    public static EffectCategory fromName(String name){
        for(EffectCategory e : EffectCategory.values()){
            if(e.name().equalsIgnoreCase(name)){
                return e;
            }
        }

        return null;
    }
}
