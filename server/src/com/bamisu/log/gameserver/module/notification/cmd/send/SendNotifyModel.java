package com.bamisu.log.gameserver.module.notification.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

import java.util.List;

public class SendNotifyModel extends BaseMsg {

    public String action;
    public List<String> params;

    public SendNotifyModel() {
        super(CMD.CMD_NOTIFY_MODEL);
    }

    @Override
    public void packData() {
        super.packData();

        data.putUtfString(Params.ACTION, action);
        data.putUtfStringArray(Params.LIST, params);
    }
}
