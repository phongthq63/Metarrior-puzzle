package com.bamisu.log.gameserver.module.hero.define;

/**
 * Created by Quach Thanh Phong
 * On 2/12/2022 - 2:29 PM
 */
public enum  EHeroStatus {
    NOT_AVAILABLE(0),
    SPENDING(1),
    AVAILABLE(2);

    private int id;

    public int getId() {
        return id;
    }

    EHeroStatus(int id) {
        this.id = id;
    }

    public static EHeroStatus fromId(int id){
        for (EHeroStatus status : EHeroStatus.values()) {
            if(status.id == id) return status;
        }
        return null;
    }
}
