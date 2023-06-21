package com.bamisu.log.gameserver.module.WoL.defines;

public enum WoLStageDefine {
    CAMPAIGN_PROGRESSION(0),
    CAMPAIGN_DAMAGE(1),
    CAMPAIGN_HEAL(2),
    CAMPAIGN_TANK(3),
    TOWER_PROGRESSION(0),
    TOWER_DAMAGE(1),
    TOWER_HEAL(2),
    TOWER_TANK(3),
    MISSIONS_PROGRESSION(0),
    MISSIONS_DAMAGE(1),
    MISSIONS_HEAL(2),
    MISSION_TANK(3),
    TREASURE_HUNT_PROGRESSION(0),
    TREASURE_HUNT_DAMAGE(1),
    TREASURE_HUNT_HEAL(2),
    TREASURE_HUNT_TANK(3),
    HERO_SUMMONS(0),
    HERO_ASCENSION(1),
    ALLIANCE_CONTRIBUTIONS(0);

    public int id;

    WoLStageDefine(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static WoLStageDefine fromID(int id){
        for(WoLStageDefine index : WoLStageDefine.values()){
            if(index.id == id){
                return index;
            }
        }
        return null;
    }
}
