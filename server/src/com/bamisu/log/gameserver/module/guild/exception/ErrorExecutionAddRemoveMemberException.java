package com.bamisu.log.gameserver.module.guild.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;
import com.bamisu.gamelib.entities.ServerConstant;

public class ErrorExecutionAddRemoveMemberException extends BaseServerException {
    public ErrorExecutionAddRemoveMemberException() {
        super(ServerConstant.ErrorCode.ERR_SYS, "Guild have member but member not in guild, " +
                "Or Member in guild but guild don't have member");
    }
}
