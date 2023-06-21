package com.bamisu.log.gameserver.module.invite.defind;

public enum EBonusInvite {
    INVITE("Starter"),
    INVITE_LINK("Champion"),
    INVITE_LEVEL_50("Ultimate");

    EBonusInvite(String id) {
        this.id = id;
    }

    String id;

    public String getId() {
        return id;
    }

    public static EBonusInvite fromID(String id){
        for(EBonusInvite index : EBonusInvite.values()){
            if(id.equals(index.id))return index;
        }
        return null;
    }
}
