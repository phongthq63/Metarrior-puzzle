package com.bamisu.log.gameserver.module.mage.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendEquipMageSkin extends BaseMsg {

    public SendEquipMageSkin() {
        super(CMD.CMD_EQUIP_MAGE_SKIN);
    }

    public SendEquipMageSkin(short errorCode) {
        super(CMD.CMD_EQUIP_MAGE_SKIN, errorCode);
    }
}
