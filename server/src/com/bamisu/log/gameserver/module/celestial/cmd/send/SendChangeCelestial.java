package com.bamisu.log.gameserver.module.celestial.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendChangeCelestial extends BaseMsg {

    public String idCelestial;

    public SendChangeCelestial() {
        super(CMD.CMD_CHANGE_CELESTIAL);
    }

    public SendChangeCelestial(short errorCode) {
        super(CMD.CMD_CHANGE_CELESTIAL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putUtfString(Params.ID, idCelestial);
    }
}
