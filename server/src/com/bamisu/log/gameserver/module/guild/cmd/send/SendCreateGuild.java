package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendCreateGuild extends BaseMsg {

    public SendCreateGuild() {
        super(CMD.CMD_CREATE_GUILD);
    }

    public SendCreateGuild(short errorCode) {
        super(CMD.CMD_CREATE_GUILD, errorCode);
    }
}
