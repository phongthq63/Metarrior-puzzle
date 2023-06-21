package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendLinkUsername extends BaseMsg {

    public SendLinkUsername() {
        super(CMD.CMD_UPDATE_USERNAME);
    }

    public SendLinkUsername(short errCode) {
        super(CMD.CMD_UPDATE_USERNAME, errCode);
    }

    @Override
    public void packData() {
        super.packData();
    }
}
