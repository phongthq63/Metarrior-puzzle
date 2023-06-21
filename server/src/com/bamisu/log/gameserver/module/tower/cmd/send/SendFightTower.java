package com.bamisu.log.gameserver.module.tower.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendFightTower extends BaseMsg {

    public SendFightTower() {
        super(CMD.CMD_FIGHT_TOWER);
    }

    public SendFightTower(short errorCode) {
        super(CMD.CMD_FIGHT_TOWER, errorCode);
    }
}
