package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendAddFriend extends BaseMsg {
    public SendAddFriend() {
        super(CMD.CMD_ADD_FRIEND);
    }

    public SendAddFriend(short errorCode) {
        super(CMD.CMD_ADD_FRIEND, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
