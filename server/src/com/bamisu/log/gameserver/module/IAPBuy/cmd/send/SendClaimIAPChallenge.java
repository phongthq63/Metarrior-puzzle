package com.bamisu.log.gameserver.module.IAPBuy.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendClaimIAPChallenge extends BaseMsg {

    public String productId;
    public int point;
    public String typeClaim;

    public SendClaimIAPChallenge() {
        super(CMD.CMD_CLAIM_IAP_REWARD_CHALLENGE);
    }

    public SendClaimIAPChallenge(short errorCode) {
        super(CMD.CMD_CLAIM_IAP_REWARD_CHALLENGE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.PRODUCT_ID, productId);
        data.putInt(Params.POSITION, point);
        data.putUtfString(Params.TYPE, typeClaim);
    }
}
