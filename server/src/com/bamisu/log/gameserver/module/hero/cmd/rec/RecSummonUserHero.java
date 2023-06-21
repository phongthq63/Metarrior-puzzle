package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecSummonUserHero extends BaseCmd {

    public String idSummon;
    public short count;

    public RecSummonUserHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        idSummon = data.getUtfString(Params.ModuleChracter.ID);
        count = data.getShort(Params.ModuleChracter.COUNT);
    }
}
