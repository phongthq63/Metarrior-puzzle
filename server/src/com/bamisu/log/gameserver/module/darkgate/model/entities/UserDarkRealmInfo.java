package com.bamisu.log.gameserver.module.darkgate.model.entities;

/**
 * Create by Popeye on 10:56 AM, 11/17/2020
 */
public class UserDarkRealmInfo {
    public int turn;
    public int lastCheckTurn;

    public UserDarkRealmInfo() {
    }

    public UserDarkRealmInfo(int turn, int lastCheckTurn) {
        this.turn = turn;
        this.lastCheckTurn = lastCheckTurn;
    }
}
