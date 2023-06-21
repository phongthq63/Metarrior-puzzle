package com.bamisu.log.gameserver.module.hero.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

public class InvalidFragmentConfigException extends BaseServerException {

    public InvalidFragmentConfigException() {
        super((short) -1, "Mảnh tướng config không hợp lệ");
    }
}
