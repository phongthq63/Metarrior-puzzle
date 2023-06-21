package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

/**
 * Created by Quach Thanh Phong
 * On 3/6/2022 - 4:02 AM
 */
public class SendVerifyClaimToken extends BaseMsg {
    public String txhash;
    public SendVerifyClaimToken() {
        super(CMD.CMD_VERIFY_CLAIM_TOKEN);
    }

    public SendVerifyClaimToken(short errorCode) {
        super(CMD.CMD_VERIFY_CLAIM_TOKEN, errorCode);
    }
    public SendVerifyClaimToken(short errorCode, String txhash) {
        super(CMD.CMD_VERIFY_CLAIM_TOKEN, errorCode);
        this.txhash = txhash;
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }

        data.putText(Params.TXHASH, this.txhash);
    }
}
