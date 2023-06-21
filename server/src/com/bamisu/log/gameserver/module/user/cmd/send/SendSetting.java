package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.log.gameserver.datamodel.user.entities.PushNotificationSetting;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

/**
 * Create by Popeye on 11:02 AM, 4/21/2020
 */
public class SendSetting extends BaseMsg {
    public PushNotificationSetting pushNotificationSetting;

    public SendSetting() {
        super(CMD.CMD_GET_SETTING);
    }

    public SendSetting(short errorCode) {
        super(CMD.CMD_GET_SETTING, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;
        data.putSFSObject(Params.PUSH, pushNotificationSetting.toSFSObject());
    }
}
