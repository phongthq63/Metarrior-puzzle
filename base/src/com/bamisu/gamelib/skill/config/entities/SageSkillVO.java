package com.bamisu.gamelib.skill.config.entities;

import com.bamisu.gamelib.skill.config.entities.Dependent;

import java.util.List;

/**
 * Create by Popeye on 4:30 PM, 3/6/2020
 */
public class SageSkillVO {
    public String id;
    public int column;
    public String type;
    public List<String> tag;
    public int msps;
    public List<Dependent> dependent;
    public int mana;
    public String name;
    public List<String> desc;
}
