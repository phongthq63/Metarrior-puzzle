package com.bamisu.log.gameserver.module.characters.celestial.entities;

public class LevelVO {
    public short lv;
    public long exp;

    public LevelVO() {
    }

    public LevelVO(short lv, long exp) {
        this.lv = lv;
        this.exp = exp;
    }
}
