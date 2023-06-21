package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

/**
 * Create by Popeye on 5:41 PM, 4/8/2020
 */
public class SendUpdateGender extends BaseMsg {
    public short gender;

    public SendUpdateGender() {
        super(CMD.CMD_UPDATE_GENDER);
    }

    public SendUpdateGender(short errorCode) {
        super(CMD.CMD_UPDATE_GENDER, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putShort(Params.GENDER, gender);
    }
}
