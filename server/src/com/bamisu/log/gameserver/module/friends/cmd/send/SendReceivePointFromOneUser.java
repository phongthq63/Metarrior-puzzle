package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendReceivePointFromOneUser extends BaseMsg {
    public SendReceivePointFromOneUser() {
        super(CMD.CMD_RECEIVE_POINT_FROM_ONE_USER);
    }

    public SendReceivePointFromOneUser(short errorCode) {
        super(CMD.CMD_RECEIVE_POINT_FROM_ONE_USER, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
