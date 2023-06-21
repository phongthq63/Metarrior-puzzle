package com.bamisu.log.gameserver.module.vip.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendClaimHonorGift extends BaseMsg {
    public SendClaimHonorGift() {
        super(CMD.CMD_CLAIM_HONOR_GIFT);
    }

    public SendClaimHonorGift(short errorCode) {
        super(CMD.CMD_CLAIM_HONOR_GIFT, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
