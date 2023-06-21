package com.bamisu.log.gameserver.module.guild.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;
import com.bamisu.gamelib.entities.ServerConstant;

public class UserAlreadyInGuildException extends BaseServerException {
    public UserAlreadyInGuildException() {
        super(ServerConstant.ErrorCode.ERR_USER_IN_GUILD, "Người chơi đã ở trong guild");
    }
}
