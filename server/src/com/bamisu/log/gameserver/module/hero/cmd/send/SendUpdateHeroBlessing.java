package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendUpdateHeroBlessing extends BaseMsg {

    public String hashHero;
    public int star;
    public int level;

    public SendUpdateHeroBlessing() {
        super(CMD.CMD_UPDATE_HERO_BLESSING);
    }

    public SendUpdateHeroBlessing(short errorCode) {
        super(CMD.CMD_UPDATE_HERO_BLESSING, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.HASH_HERO, hashHero);
        data.putShort(Params.STAR, (short) star);
        data.putShort(Params.LEVEL, (short) level);
    }
}
