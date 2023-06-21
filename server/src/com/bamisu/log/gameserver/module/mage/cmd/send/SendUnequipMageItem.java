package com.bamisu.log.gameserver.module.mage.cmd.send;

import com.bamisu.log.gameserver.entities.CMDUtilsServer;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendUnequipMageItem extends BaseMsg {

    public Stats stats;

    public SendUnequipMageItem() {
        super(CMD.CMD_UNEQUIP_MAGE_ITEM);
    }

    public SendUnequipMageItem(short errorCode) {
        super(CMD.CMD_UNEQUIP_MAGE_ITEM, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putSFSObject(Params.ModuleHero.ATTRIBUTE, CMDUtilsServer.statsMageToSFSObject(stats));

    }
}
