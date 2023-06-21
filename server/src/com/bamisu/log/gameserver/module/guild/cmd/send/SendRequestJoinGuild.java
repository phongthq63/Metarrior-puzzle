package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendRequestJoinGuild extends BaseMsg {

    public SendRequestJoinGuild() {
        super(CMD.CMD_REQUEST_JOIN_GUILD);
    }

    public SendRequestJoinGuild(short errorCode) {
        super(CMD.CMD_REQUEST_JOIN_GUILD, errorCode);
    }
}
