package com.bamisu.gamelib.socialnetwork;

/**
 * Create by Popeye on 2:46 PM, 4/23/2020
 */
public enum ESocialNetwork {
    GUESS(0, "guess"),
    FACEBOOK(1, "fb"),
    GOOGLE(2, "gg"),
    GAME_CENTER(3, "gc"),
    DEVICE_ID(4, "did"),
    APPLE(5, "auid"),
    SINGMAAN(6, "singmaan"),
    BLOCKCHAIN(7, "blockchain"),
    USERNAME(8, "username");

    private int intValue;
    private String name;

    ESocialNetwork(int intValue, String name) {
        this.intValue = intValue;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getIntValue() {
        return intValue;
    }

    public static ESocialNetwork fromIntValue(int intValue){
        for(ESocialNetwork socicalNetwork : ESocialNetwork.values()){
            if(socicalNetwork.getIntValue() == intValue) return socicalNetwork;
        }

        return null;
    }
}
