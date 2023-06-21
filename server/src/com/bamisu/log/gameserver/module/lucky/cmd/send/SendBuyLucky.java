package com.bamisu.log.gameserver.module.lucky.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendBuyLucky extends BaseMsg {
    public SendBuyLucky() {
        super(CMD.CMD_BUY_LUCKY);
    }

    public SendBuyLucky(short errorCode) {
        super(CMD.CMD_BUY_LUCKY, errorCode);
    }


    @Override
    public void packData() {
        super.packData();
        if (isError())return;

    }
}
