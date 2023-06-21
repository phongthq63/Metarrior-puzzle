package com.bamisu.log.gameserver.module.event.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendClaimQuickly extends BaseMsg {
    public SendClaimQuickly() {
        super(CMD.CMD_CLAIM_QUICKLY);
    }

    public SendClaimQuickly(short errorCode) {
        super(CMD.CMD_CLAIM_QUICKLY, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
