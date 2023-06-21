package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecSummonUserHeroBonus extends BaseCmd {

    public String idKingdom;

    public RecSummonUserHeroBonus(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        idKingdom = data.getUtfString(Params.ID);
    }
}
