package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

/**
 * Create by Popeye on 10:04 AM, 4/29/2020
 */
public class SendSuport extends BaseMsg {
    public SendSuport() {
        super(CMD.CMD_SEND_SUPORT);
    }

    public SendSuport(short errorCode) {
        super(CMD.CMD_SEND_SUPORT, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;
    }
}
