package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendChangeEnergyHunt extends BaseMsg {
    public String id;

    public SendChangeEnergyHunt() {
        super(CMD.CMD_CHANGE_HUNT_ENERGY_BAR);
    }

    public SendChangeEnergyHunt(short errorCode) {
        super(CMD.CMD_CHANGE_HUNT_ENERGY_BAR, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putUtfString(Params.ID, id);
    }
}
