package com.bamisu.log.gameserver.module.skill.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

/**
 * Create by Popeye on 4:42 PM, 12/25/2019
 */
public class SkillNotFoundException extends BaseServerException {
    public SkillNotFoundException() {
        super((short) -1, "Skill not found");
    }

    public SkillNotFoundException(short error, String message) {
        super(error, message);
    }
}
