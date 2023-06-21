package com.bamisu.log.gameserver.module.celestial.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;

public class SendUnlockCelestial extends BaseMsg {

    public String idCelestial;

    public SendUnlockCelestial() {
        super(CMD.CMD_UNLOCK_CELESTIAL);
    }

    public SendUnlockCelestial(short errorCode) {
        super(CMD.CMD_UNLOCK_CELESTIAL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.ID, idCelestial);
    }
}
