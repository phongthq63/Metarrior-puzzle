package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

/**
 * Create by Popeye on 11:36 AM, 4/8/2020
 */
public class SendUpdateDisplayName extends BaseMsg {
    public String displayName; //ten hien thi

    public SendUpdateDisplayName() {
        super(CMD.CMD_UPDATE_DNAME);
    }

    public SendUpdateDisplayName(short errorCode) {
        super(CMD.CMD_UPDATE_DNAME, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;
        data.putUtfString(Params.USER_DISPLAY_NAME, this.displayName);
    }
}
