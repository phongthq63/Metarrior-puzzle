package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendExecutionRequestJoinGuild extends BaseMsg {

    public SendExecutionRequestJoinGuild() {
        super(CMD.CMD_EXECUTION_REQUEST_JOIN_GUILD);
    }

    public SendExecutionRequestJoinGuild(short errorCode) {
        super(CMD.CMD_EXECUTION_REQUEST_JOIN_GUILD, errorCode);
    }
}
