package com.bamisu.log.gameserver.module.nft.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

public class BreedException extends BaseServerException {
    public BreedException(short errorCode) {
        super(errorCode, "");
    }
}
