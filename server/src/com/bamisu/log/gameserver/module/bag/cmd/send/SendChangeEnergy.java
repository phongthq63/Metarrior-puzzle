package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendChangeEnergy extends BaseMsg {

    public String id;

    public SendChangeEnergy() {
        super(CMD.CMD_CHANGE_ENERGY_BAR);
    }

    public SendChangeEnergy(short errorCode) {
        super(CMD.CMD_CHANGE_ENERGY_BAR, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putUtfString(Params.ID, id);
    }
}
