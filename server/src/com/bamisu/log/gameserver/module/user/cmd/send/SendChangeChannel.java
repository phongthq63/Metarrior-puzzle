package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendChangeChannel extends BaseMsg {

    public SendChangeChannel() {
        super(CMD.CMD_CHANGE_CHANNEL);
    }

    public SendChangeChannel(short errorCode) {
        super(CMD.CMD_CHANGE_CHANNEL, errorCode);
    }
}
