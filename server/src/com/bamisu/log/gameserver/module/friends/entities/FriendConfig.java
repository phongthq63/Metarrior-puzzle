package com.bamisu.log.gameserver.module.friends.entities;

public class FriendConfig {
    public int friends;
    public int block;
    public int requests;
    public int points;

    public FriendConfig(int friends, int block, int requests, int points) {
        this.friends = friends;
        this.block = block;
        this.requests = requests;
        this.points = points;
    }

    public FriendConfig() {
    }
}
