package com.bamisu.log.gameserver.module.mage.cmd.send;

import com.bamisu.log.gameserver.entities.CMDUtilsServer;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendEquipStoneMage extends BaseMsg {

    public Stats stats;

    public SendEquipStoneMage() {
        super(CMD.CMD_EQUIP_STONE_MAGE);
    }

    public SendEquipStoneMage(short errorCode) {
        super(CMD.CMD_EQUIP_STONE_MAGE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putSFSObject(Params.ModuleHero.ATTRIBUTE, CMDUtilsServer.statsMageToSFSObject(stats));
    }
}
