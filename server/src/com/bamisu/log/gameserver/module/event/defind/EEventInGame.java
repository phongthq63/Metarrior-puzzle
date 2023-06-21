package com.bamisu.log.gameserver.module.event.defind;

public enum  EEventInGame {
    NONE("none"),
    BLACK_FRIDAY("black_friday"),
    CHRISSMATE("christmas");

    String id;

    EEventInGame(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static EEventInGame fromID(String id){
        for(EEventInGame event : EEventInGame.values()){
            if(event.id.equals(id)){
                return event;
            }
        }
        return NONE;
    }
}
