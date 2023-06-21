package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

/**
 * Create by Popeye on 2:54 PM, 4/21/2020
 */
public class SendChangeServer extends BaseMsg {
    public String addr;
    public int port;
    public String zone;

    public SendChangeServer() {
        super(CMD.CMD_CHANGE_SERVER);
    }

    public SendChangeServer(short errorCode) {
        super(CMD.CMD_CHANGE_SERVER, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

//        data.putUtfString(Params.ADDRESS, addr);
        data.putInt(Params.PORT, port);
        data.putUtfString(Params.ZONE, zone);
    }
}
