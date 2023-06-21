package com.bamisu.log.gameserver.module.WoL.defines;

public enum WoLRewardDefine {
    REWARD_1(1),
    REWARD_2(2);

    public int id;

    WoLRewardDefine(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static WoLRewardDefine fromID(int id){
        for(WoLRewardDefine index : WoLRewardDefine.values()){
            if(index.id == id){
                return index;
            }
        }
        return null;
    }

}
