package com.bamisu.log.gameserver.module.hero.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

public class InvalidUpdateTeamException extends BaseServerException {

    public InvalidUpdateTeamException() {
        super((short) -1, "Update team faild because invalid team");
    }
}
