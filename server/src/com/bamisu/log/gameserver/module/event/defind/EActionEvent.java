package com.bamisu.log.gameserver.module.event.defind;

public enum EActionEvent {
    NONE(""),
    BUY("0");

    EActionEvent(String id) {
        this.id = id;
    }

    String id;

    public String getId() {
        return id;
    }

    public static EActionEvent fromID(String id){
        for(EActionEvent action : EActionEvent.values()){
            if(action.id.equals(id)){
                return action;
            }
        }
        return NONE;
    }
}
