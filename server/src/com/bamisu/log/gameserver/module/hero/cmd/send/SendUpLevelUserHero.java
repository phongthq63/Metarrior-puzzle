package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.entities.CMDUtilsServer;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.List;

public class SendUpLevelUserHero extends BaseMsg {

    public String hashHero;
    public short level;

    public SendUpLevelUserHero() {
        super(CMD.CMD_UP_LEVEL_USER_HERO);
    }

    public SendUpLevelUserHero(short errorCode) {
        super(CMD.CMD_UP_LEVEL_USER_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.HASH, hashHero);
        data.putShort(Params.LEVEL, level);
    }
}
