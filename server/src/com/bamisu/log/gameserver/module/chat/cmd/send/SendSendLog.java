package com.bamisu.log.gameserver.module.chat.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

import java.util.List;

public class SendSendLog extends BaseMsg {

    public String id;
    public List<String> params;

    public SendSendLog() {
        super(CMD.CMD_SEND_LOG);
    }

    @Override
    public void packData() {
        super.packData();

        data.putUtfString(Params.ID, id);
        data.putUtfStringArray(Params.PARAM, params);
    }
}
