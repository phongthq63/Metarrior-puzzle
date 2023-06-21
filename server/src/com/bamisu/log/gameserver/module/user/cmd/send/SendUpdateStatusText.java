package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

/**
 * Create by Popeye on 6:01 PM, 4/14/2020
 */
public class SendUpdateStatusText extends BaseMsg {
    public String content;

    public SendUpdateStatusText() {
        super(CMD.CMD_UPDATE_STATUS_TEXT);
    }

    public SendUpdateStatusText(short errorCode) {
        super(CMD.CMD_UPDATE_STATUS_TEXT, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putUtfString(Params.CONTENT, content);
    }
}
