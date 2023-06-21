package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendSendPointToOneUser extends BaseMsg {
    public SendSendPointToOneUser() {
        super(CMD.CMD_SEND_POINT_TO_ONE_USER);
    }

    public SendSendPointToOneUser(short errorCode) {
        super(CMD.CMD_SEND_POINT_TO_ONE_USER, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
