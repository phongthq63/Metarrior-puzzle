package com.bamisu.gamelib.skill.config.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Create by Popeye on 3:17 PM, 2/26/2020
 */
public class SkillDescText {

    @JsonProperty("lv")
    public int level;
    public String text;
}
