package com.bamisu.gamelib.skill.config.entities;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Create by Popeye on 3:34 PM, 12/23/2019
 */
public class SkillInfo {
    public String id;
    public int level;

    public SkillInfo() {
    }

    public SkillInfo(String id, int level) {
        this.id = id;
        this.level = level;
    }
}
