package com.bamisu.log.gameserver.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResType {
    COMMON_ANVIL_COIN("RES1008"),
    RARE_ANVIL_COIN("RES1009"),
    LEGENDARY_ANVIL_COIN("RES1010");

    String id;

    ResType(String id) {
        this.id = id;
    }

    public static ResType fromID(String id){
        for (ResType value : ResType.values()) {
            if(value.getId().equals(id)) return value;
        }

        return null;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    public String toString() {
        return String.valueOf(id);
    }

}
