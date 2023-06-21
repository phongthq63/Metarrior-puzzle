package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendReceiveAndSendAllUser extends BaseMsg {
    public SendReceiveAndSendAllUser() {
        super(CMD.CMD_RECEIVE_AND_SEND_ALL_USER);
    }

    public SendReceiveAndSendAllUser(short errorCode) {
        super(CMD.CMD_RECEIVE_AND_SEND_ALL_USER, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
