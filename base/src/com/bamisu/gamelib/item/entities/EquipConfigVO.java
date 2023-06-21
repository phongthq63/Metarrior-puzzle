package com.bamisu.gamelib.item.entities;

public class EquipConfigVO {
    public String id;
    public int position;
    public String name;
    public int star;
    public String kingdom;
    public int expFis;
    public String type;
    public String profession;
    public String represent;

    public EquipConfigVO(){}

    public EquipConfigVO(String profession ,String type, String id, int position, String name, String kingdom, int star, int expFis, String represent){
        this.id = id;
        this.position = position;
        this.name = name;
        this.kingdom = kingdom;
        this.star = star;
        this.expFis = expFis;
        this.represent = represent;
    }

    public EquipConfigVO(EquipConfigVO equipConfigVO){
        this.id = equipConfigVO.id;
        this.position = equipConfigVO.position;
        this.name = equipConfigVO.name;
        this.kingdom = equipConfigVO.kingdom;
        this.star = equipConfigVO.star;
        this.expFis = equipConfigVO.expFis;
        this.represent = equipConfigVO.represent;
    }
}
