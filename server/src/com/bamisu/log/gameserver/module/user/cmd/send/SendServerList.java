package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.JoinedServerData;
import com.bamisu.gamelib.entities.Params;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Create by Popeye on 11:57 AM, 4/21/2020
 */
public class SendServerList extends BaseMsg {
    public int serverCount;
    public JoinedServerData joinedServerData;

    public SendServerList() {
        super(CMD.CMD_GET_SERVER_LIST);
    }

    public SendServerList(short errorCode) {
        super(CMD.CMD_GET_SERVER_LIST, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putInt(Params.NUMBER_OF_SERVERS, serverCount);
        data.putSFSArray(Params.USER_SERVERS, joinedServerData.readAsSFSArray());
    }
}
