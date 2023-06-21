package com.bamisu.log.gameserver.module.notification.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

import java.util.List;

public class SendRemoveNotify extends BaseMsg {

    public SendRemoveNotify() {
        super(CMD.CMD_REMOVE_NOTIFY);
    }

    public SendRemoveNotify(short errorCode) {
        super(CMD.CMD_REMOVE_NOTIFY, errorCode);
    }
}
