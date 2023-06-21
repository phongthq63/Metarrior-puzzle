package com.bamisu.gamelib.entities;

/**
 * Create by Popeye on 10:40 AM, 9/22/2020
 */
public class JoinedServerInfo {
    public int serverID;
    public int level = 1;
    public String name = "";
    public String avatar = "";
    public int frame = 0;

    public JoinedServerInfo() {
    }

    public JoinedServerInfo(int serverID, int level, String name, String avatar, int frame) {
        this.serverID = serverID;
        this.level = level;
        this.name = name;
        this.avatar = avatar;
        this.frame = frame;
    }
}
