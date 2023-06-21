package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetUserHeroInfo extends BaseCmd {

    public String hashHero;

    public RecGetUserHeroInfo(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hashHero = data.getUtfString(Params.ModuleHero.HASH);
    }
}
