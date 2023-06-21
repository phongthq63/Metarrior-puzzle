package com.bamisu.gamelib.skill.config.entities;

/**
 * Create by Popeye on 11:35 AM, 12/25/2019
 */
public class BaseSkillInfo {
    public String id;
    public int type;
    public String name;
    public String group;
    public int mana = -1; //cần cho skill active pháp sư

    public BaseSkillInfo() {
    }

    public BaseSkillInfo(String id, int type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public BaseSkillInfo readMe(){
        return this;
    }
}
