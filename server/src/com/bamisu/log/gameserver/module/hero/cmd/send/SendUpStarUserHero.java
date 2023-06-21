package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.item.entities.StoneSlotVO;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendUpStarUserHero extends BaseMsg {

    public long timer;

    public SendUpStarUserHero() {
        super(CMD.CMD_UP_STAR_USER_HERO);
    }

    public SendUpStarUserHero(short errorCode) {
        super(CMD.CMD_UP_STAR_USER_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }

        data.putLong(Params.TIME, this.timer);
    }
}
