package com.bamisu.gamelib.item.entities;

public enum EStatusSlot {
    LOCK(true),
    UNLOCK(false);

    boolean status;

    EStatusSlot(boolean status) {
        this.status = status;
    }

    public boolean getValue(){
        return status;
    }
}
