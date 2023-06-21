package com.bamisu.log.gameserver.module.mage.cmd.send;

import com.bamisu.log.gameserver.entities.CMDUtilsServer;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendEquipMageItem extends BaseMsg {

    public Stats stats;

    public SendEquipMageItem() {
        super(CMD.CMD_EQUIP_MAGE_ITEM);
    }

    public SendEquipMageItem(short errorCode) {
        super(CMD.CMD_EQUIP_MAGE_ITEM, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putSFSObject(Params.ModuleHero.ATTRIBUTE, CMDUtilsServer.statsMageToSFSObject(stats));
    }
}
