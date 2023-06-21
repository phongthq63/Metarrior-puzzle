package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendUnequipItemHeroQuick extends BaseMsg {

    public String hashHero;


    public SendUnequipItemHeroQuick() {
        super(CMD.CMD_UNEQUIP_ITEM_HERO_QUICK);
    }

    public SendUnequipItemHeroQuick(short errorCode) {
        super(CMD.CMD_UNEQUIP_ITEM_HERO_QUICK, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.HASH_HERO, hashHero);
    }
}
