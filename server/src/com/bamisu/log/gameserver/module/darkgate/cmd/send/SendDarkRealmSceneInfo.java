package com.bamisu.log.gameserver.module.darkgate.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.darkgate.DarkGateManager;

/**
 * Create by Popeye on 11:29 AM, 11/17/2020
 */
public class SendDarkRealmSceneInfo extends BaseMsg {
    public int maxTurn;
    public int remainingTurn;
    public int remainingTime = DarkGateManager.getDeltaTimeToEndWeek();

    public SendDarkRealmSceneInfo() {
        super(CMD.GET_DARK_REALM_SCENE_INFO);
    }

    public SendDarkRealmSceneInfo(short errorCode) {
        super(CMD.GET_DARK_REALM_SCENE_INFO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putInt(Params.MAXTURN, maxTurn);
        data.putInt(Params.REMAINING_TURN, remainingTurn);
        data.putInt(Params.REMAINING_TIME, remainingTime);
    }
}
