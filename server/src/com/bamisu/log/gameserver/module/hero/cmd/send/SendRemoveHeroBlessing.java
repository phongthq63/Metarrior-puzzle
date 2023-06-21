package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendRemoveHeroBlessing extends BaseMsg {

    public String hashHero;
    public short level;

    public SendRemoveHeroBlessing() {
        super(CMD.CMD_REMOVE_HERO_BLESSING);
    }

    public SendRemoveHeroBlessing(short errorCode) {
        super(CMD.CMD_REMOVE_HERO_BLESSING, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.HASH_HERO, hashHero);
        data.putShort(Params.LEVEL, level);
    }
}
