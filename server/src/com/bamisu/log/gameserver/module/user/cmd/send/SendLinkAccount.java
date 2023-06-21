package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

/**
 * Create by Popeye on 3:45 PM, 4/23/2020
 */
public class SendLinkAccount extends BaseMsg {
    public int socicalNetwork = -1;

    public SendLinkAccount() {
        super(CMD.CMD_LINK_ACCOUNT);
    }

    public SendLinkAccount(short errorCode) {
        super(CMD.CMD_LINK_ACCOUNT, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putInt(Params.SOCIAL_NETWORK, socicalNetwork);
    }
}
