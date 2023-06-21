package com.bamisu.log.gameserver.module.event.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGrandOpening extends BaseMsg {
    public List<Integer> list;
    public SendGrandOpening() {
        super(CMD.CMD_SHOW_GRAND_OPENING_CHECK_IN);
    }

    public SendGrandOpening(short errorCode) {
        super(CMD.CMD_SHOW_GRAND_OPENING_CHECK_IN, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        data.putIntArray(Params.LIST, list);
    }
}
