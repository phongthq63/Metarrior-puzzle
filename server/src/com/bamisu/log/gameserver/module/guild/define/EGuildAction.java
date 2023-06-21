package com.bamisu.log.gameserver.module.guild.define;

public enum EGuildAction {
    EXECUTE_REQUEST_JOIN_GUILD("0"),
    KICK_MEMBER("1"),
    LEAVE_GUILD("2"),
    SEE_LOG("4"),
    SETTING_GUILD("5"),
    CHANGE_OFFICE("6");

    public String id;

    EGuildAction(String id) {
        this.id = id;
    }

    public static EGuildAction fromID(String id){
        for (EGuildAction action : EGuildAction.values()){
            if(action.id.equals(id)){
                return action;
            }
        }
        return null;
    }
}
