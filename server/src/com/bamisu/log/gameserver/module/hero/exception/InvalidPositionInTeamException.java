package com.bamisu.log.gameserver.module.hero.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

public class InvalidPositionInTeamException extends BaseServerException {

    public InvalidPositionInTeamException() {
        super((short) -1, "Vị trí không hợp lệ");
    }
}
