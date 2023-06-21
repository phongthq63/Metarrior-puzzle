package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendBlockFriend extends BaseMsg {
    public SendBlockFriend() {
        super(CMD.CMD_BLOCK_FRIEND);
    }

    public SendBlockFriend(short errorCode) {
        super(CMD.CMD_BLOCK_FRIEND, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
