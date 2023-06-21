package com.bamisu.log.gameserver.module.hero.define;

/**
 * Created by Quach Thanh Phong
 * On 2/13/2022 - 11:13 AM
 */
public enum EHeroType {
    NORMAL(0),
    NFT(1);

    private int id;

    public int getId() {
        return id;
    }

    EHeroType(int id) {
        this.id = id;
    }

    public static EHeroType fromId(int id) {
        for (EHeroType type : EHeroType.values()) {
            if(type.id == id) return type;
        }
        return NORMAL;
    }
}
