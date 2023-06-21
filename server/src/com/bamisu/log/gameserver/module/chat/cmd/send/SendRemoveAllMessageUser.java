package com.bamisu.log.gameserver.module.chat.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendRemoveAllMessageUser extends BaseMsg {

    public long uid;


    public SendRemoveAllMessageUser() {
        super(CMD.CMD_REMOVE_ALL_MESSAGE_USER);
    }

    public SendRemoveAllMessageUser(short errorCode) {
        super(CMD.CMD_REMOVE_ALL_MESSAGE_USER, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putLong(Params.UID, uid);
    }
}
