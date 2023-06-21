package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendUserJoinGuild extends BaseMsg {

    public SendUserJoinGuild() {
        super(CMD.CMD_JOIN_GUILD);
    }

    public SendUserJoinGuild(short errorCode) {
        super(CMD.CMD_JOIN_GUILD, errorCode);
    }
}
