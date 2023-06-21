package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendDeleteFriend extends BaseMsg {
    public SendDeleteFriend() {
        super(CMD.CMD_DELETE_FRIEND);
    }

    public SendDeleteFriend(short errorCode) {
        super(CMD.CMD_DELETE_FRIEND, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
