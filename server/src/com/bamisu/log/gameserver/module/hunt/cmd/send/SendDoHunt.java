package com.bamisu.log.gameserver.module.hunt.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendDoHunt extends BaseMsg {

    public SendDoHunt() {
        super(CMD.CMD_HUNT);
    }

    public SendDoHunt(short errorCode) {
        super(CMD.CMD_HUNT, errorCode);
    }
}
