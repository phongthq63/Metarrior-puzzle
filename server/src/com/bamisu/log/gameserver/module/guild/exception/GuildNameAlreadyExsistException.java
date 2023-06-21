package com.bamisu.log.gameserver.module.guild.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;
import com.bamisu.gamelib.entities.ServerConstant;

public class GuildNameAlreadyExsistException extends BaseServerException {
    public GuildNameAlreadyExsistException() {
        super(ServerConstant.ErrorCode.ERR_EXSIST_NAME_GUILD, "name guild already exsist");
    }
}
