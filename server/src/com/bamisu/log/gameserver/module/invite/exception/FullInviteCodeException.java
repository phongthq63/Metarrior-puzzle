package com.bamisu.log.gameserver.module.invite.exception;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.excepions.BaseServerException;

public class FullInviteCodeException extends BaseServerException {

    public FullInviteCodeException() {
        super((short) -1, "Hết mã invite code, không thể gen theo format");
    }
}
