package com.bamisu.log.gameserver.module.darkgate.model.entities;

/**
 * Create by Popeye on 10:33 PM, 11/26/2020
 */
public class UserEndlessNightInfo {
    public int turn;
    public int lastCheckTurn;

    public UserEndlessNightInfo() {
    }

    public UserEndlessNightInfo(int turn, int lastCheckTurn) {
        this.turn = turn;
        this.lastCheckTurn = lastCheckTurn;
    }
}
