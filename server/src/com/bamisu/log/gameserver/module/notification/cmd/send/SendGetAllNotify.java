package com.bamisu.log.gameserver.module.notification.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

import java.util.List;

public class SendGetAllNotify extends BaseMsg {

    public List<String> listNotify;

    public SendGetAllNotify() {
        super(CMD.CMD_GET_ALL_NOTIFY);
    }

    @Override
    public void packData() {
        super.packData();

        data.putUtfStringArray(Params.NOTIFY, listNotify);
    }
}
