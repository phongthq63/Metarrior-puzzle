package com.bamisu.gamelib.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Create by Popeye on 9:30 AM, 7/14/2020
 */
public enum EVip {
    ARCHMAGE(1, "Mark of the Archmage"),
    PROTECTOR(0, "Protector of Asteria");

    int id;
    String name;

    EVip() {
    }

    EVip(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static EVip fromIntValue(int id){
        for (EVip eVip : EVip.values()){
            if(eVip.getId() == id) return eVip;
        }
        return null;
    }

    public static EVip fromStrValue(String name){
        for (EVip eVip : values()){
            if(eVip.getName().equalsIgnoreCase(name)) return eVip;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
