package com.bamisu.log.gameserver.module.store.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendBuyInStore extends BaseMsg {
    public SendBuyInStore() {
        super(CMD.CMD_BUY_IN_STORE);
    }

    public SendBuyInStore(short errorCode) {
        super(CMD.CMD_BUY_IN_STORE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
