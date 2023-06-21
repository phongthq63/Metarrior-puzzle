package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendUpSizeHeroModel extends BaseMsg {

    public int maxSizeBag;

    public SendUpSizeHeroModel() {
        super(CMD.CMD_UP_SIZE_HERO_MODEL);
    }

    public SendUpSizeHeroModel(short errorCode) {
        super(CMD.CMD_UP_SIZE_HERO_MODEL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putShort(Params.ModuleHero.MAX_SIZE_BAG_HERO, (short) maxSizeBag);
    }
}
