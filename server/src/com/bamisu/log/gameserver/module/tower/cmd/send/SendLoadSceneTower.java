package com.bamisu.log.gameserver.module.tower.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendLoadSceneTower extends BaseMsg {

    public int floor;

    public SendLoadSceneTower() {
        super(CMD.CMD_LOAD_SCENE_TOWER);
    }

    public SendLoadSceneTower(short errorCode) {
        super(CMD.CMD_LOAD_SCENE_TOWER, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putShort(Params.FLOOR, (short) floor);
    }
}
