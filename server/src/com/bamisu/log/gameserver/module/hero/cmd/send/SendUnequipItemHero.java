package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class SendUnequipItemHero extends BaseMsg {

    public String hashHero;
    public EquipDataVO equipData;

    public SendUnequipItemHero() {
        super(CMD.CMD_UNEQUIP_ITEM_HERO);
    }

    public SendUnequipItemHero(short errorCode) {
        super(CMD.CMD_UNEQUIP_ITEM_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.HASH_HERO, hashHero);
        data.putUtfString(Params.HASH_WEAPON, equipData.hash);
    }
}
