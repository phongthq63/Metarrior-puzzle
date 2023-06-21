package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendRestoreBlockedFriend extends BaseMsg {
    public SendRestoreBlockedFriend() {
        super(CMD.CMD_RESTORE_BLOCKED_FRIEND);
    }

    public SendRestoreBlockedFriend(short errorCode) {
        super(CMD.CMD_RESTORE_BLOCKED_FRIEND, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
