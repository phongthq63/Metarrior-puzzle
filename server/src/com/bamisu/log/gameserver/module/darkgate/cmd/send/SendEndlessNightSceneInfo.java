package com.bamisu.log.gameserver.module.darkgate.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.darkgate.DarkGateManager;

/**
 * Create by Popeye on 8:51 AM, 11/25/2020
 */
public class SendEndlessNightSceneInfo extends BaseMsg {
    public int maxTurn;
    public int remainingTurn;
    public int remainingTime = DarkGateManager.getDeltaTimeToEndWeek();

    public SendEndlessNightSceneInfo() {
        super(CMD.GET_ENDLESS_NIGHT_SCENE_INFO);
    }

    public SendEndlessNightSceneInfo(short errorCode) {
        super(CMD.GET_ENDLESS_NIGHT_SCENE_INFO, errorCode);
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
