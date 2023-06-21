package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

/**
 * Created by Quach Thanh Phong
 * On 5/3/2022 - 2:39 PM
 */
public class SendReturnClaimToken extends BaseMsg {

    public SendReturnClaimToken() {
        super(CMD.CMD_RETURN_CLAIM_TOKEN);
    }

    public SendReturnClaimToken(short errorCode) {
        super(CMD.CMD_RETURN_CLAIM_TOKEN, errorCode);
    }
}
