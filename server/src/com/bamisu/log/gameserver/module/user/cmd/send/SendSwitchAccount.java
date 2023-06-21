package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

/**
 * Create by Popeye on 7:35 PM, 5/4/2020
 */
public class SendSwitchAccount extends BaseMsg {
    public String addr;
    public int port;
    public String zone;
    public String loginKey;

    public SendSwitchAccount() {
        super(CMD.CMD_SWITCH_ACCOUNT);
    }

    public SendSwitchAccount(short errorCode) {
        super(CMD.CMD_SWITCH_ACCOUNT, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putUtfString(Params.ADDRESS, addr);
        data.putInt(Params.PORT, port);
        data.putUtfString(Params.ZONE, zone);
        data.putUtfString(Params.TOKEN, loginKey);
    }
}
