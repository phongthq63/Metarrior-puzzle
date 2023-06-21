package com.bamisu.log.gameserver.datamodel.mage.entities;

public class StoneMageInfo {
    public String id;
    public short level = 1;
    public long exp;

    public static StoneMageInfo createStoneMageModel(String id){
        StoneMageInfo stoneMageModel = new StoneMageInfo();
        stoneMageModel.id = id;

        return stoneMageModel;
    }
}
