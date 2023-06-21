package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetBonusStory extends BaseCmd {

    public String idHero;

    public RecGetBonusStory(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        idHero = data.getUtfString(Params.ModuleChracter.ID);
    }
}
