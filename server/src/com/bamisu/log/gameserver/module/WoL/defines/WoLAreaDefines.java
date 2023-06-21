package com.bamisu.log.gameserver.module.WoL.defines;

public enum WoLAreaDefines {
    CAMPAIGN(0),
    TOWER(1),
    MISSION(2),
    TREASURE(3),
    HERO(4),
    ALLIANCE(5);

    public int id;

    WoLAreaDefines(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static WoLAreaDefines fromID(int id){
        for(WoLAreaDefines index : WoLAreaDefines.values()){
            if(index.id == id){
                return index;
            }
        }
        return null;
    }
}
