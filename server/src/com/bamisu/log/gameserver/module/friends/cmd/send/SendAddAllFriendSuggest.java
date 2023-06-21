package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendAddAllFriendSuggest extends BaseMsg {
    public SendAddAllFriendSuggest() {
        super(CMD.CMD_ADD_ALL_FRIEND_IN_SUGGEST);
    }

    public SendAddAllFriendSuggest(short errorCode) {
        super(CMD.CMD_ADD_ALL_FRIEND_IN_SUGGEST, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
    }
}
