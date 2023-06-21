package com.bamisu.log.gameserver.module.mission.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendCompleteMission extends BaseMsg {

    public SendCompleteMission() {
        super(CMD.CMD_COMPLETE_MISSION);
    }

}
