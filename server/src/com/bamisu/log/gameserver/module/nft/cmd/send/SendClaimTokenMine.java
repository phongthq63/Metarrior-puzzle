package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

/**
 * Created by Quach Thanh Phong
 * On 2/13/2022 - 9:07 PM
 */
public class SendClaimTokenMine extends BaseMsg {

    public String signature = "";
    public String token;
    public String transaction;
    public double count = 0;

    public SendClaimTokenMine() {
        super(CMD.CMD_CLAIM_TOKEN_MINE);
    }

    public SendClaimTokenMine(short errorCode) {
        super(CMD.CMD_CLAIM_TOKEN_MINE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }
        data.putUtfString(Params.DATA, signature);
        data.putUtfString(Params.TOKEN, token);
        data.putUtfString(Params.ID, transaction);
        data.putDouble(Params.COUNT, count);
    }
}
