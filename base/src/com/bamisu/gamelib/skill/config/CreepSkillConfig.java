package com.bamisu.gamelib.skill.config;

import com.bamisu.gamelib.skill.config.entities.BaseSkillInfo;

import java.util.List;

/**
 * Create by Popeye on 5:55 PM, 5/21/2020
 */
public class CreepSkillConfig {
    public List<BaseSkillInfo> list;

    public CreepSkillConfig() {
    }

    public CreepSkillConfig(List<BaseSkillInfo> list) {
        this.list = list;
    }
}
