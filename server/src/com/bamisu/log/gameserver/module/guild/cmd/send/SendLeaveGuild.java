package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendLeaveGuild extends BaseMsg {

    public long uid;

    public SendLeaveGuild() {
        super(CMD.CMD_LEAVE_GUILD);
    }

    public SendLeaveGuild(short errorCode) {
        super(CMD.CMD_LEAVE_GUILD, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putLong(Params.UID, uid);
    }
}
