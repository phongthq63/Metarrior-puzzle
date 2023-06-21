package com.bamisu.log.gameserver.module.WoL.entities;

public class WoLChallengeVO {
    public int challenge;
    public int reward;

    public WoLChallengeVO(int challenge, int reward) {
        this.challenge = challenge;
        this.reward = reward;
    }

    public WoLChallengeVO() {
    }
}
