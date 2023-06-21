package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

/**
 * Created by Quach Thanh Phong
 * On 3/6/2022 - 4:02 AM
 */
public class SendVerifyBuyToken extends BaseMsg {

    public SendVerifyBuyToken() {
        super(CMD.CMD_VERIFY_BUY_TOKEN);
    }

    public SendVerifyBuyToken(short errorCode) {
        super(CMD.CMD_VERIFY_BUY_TOKEN, errorCode);
    }
}
