package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendUnequipAllItemAllHero extends BaseMsg {

    public SendUnequipAllItemAllHero() {
        super(CMD.CMD_UNEQUIP_ALL_ITEM_ALL_HERO);
    }

    public SendUnequipAllItemAllHero(short errorCode) {
        super(CMD.CMD_UNEQUIP_ALL_ITEM_ALL_HERO, errorCode);
    }
}
