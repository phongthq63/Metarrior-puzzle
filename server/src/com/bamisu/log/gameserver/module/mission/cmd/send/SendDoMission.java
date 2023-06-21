package com.bamisu.log.gameserver.module.mission.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

/**
 * Create by Popeye on 7:20 PM, 5/11/2020
 */
public class SendDoMission extends BaseMsg {
    public SendDoMission() {
        super(CMD.CMD_DO_MISSION);
    }

    public SendDoMission(short errorCode) {
        super(CMD.CMD_DO_MISSION, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;
    }
}
