package com.bamisu.log.gameserver.module.hero.entities;

public class HeroPosition {
    public String hash;
    public short position;

    public HeroPosition(String hash, int position) {
        this.hash = hash;
        this.position = (short) position;
    }
}
