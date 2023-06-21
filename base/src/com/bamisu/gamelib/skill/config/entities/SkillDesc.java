package com.bamisu.gamelib.skill.config.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:10 PM, 2/26/2020
 */
public class SkillDesc {
    @JsonProperty("sid")
    public String skillID;

    public int template;

    @JsonProperty("template_props")
    public List<Object> templateProps;

    public List<String> desc;
}
