package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecUpLevelUserHero extends BaseCmd {

    public String hashHero;
    public short level;

    public RecUpLevelUserHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hashHero = data.getUtfString(Params.ModuleHero.HASH);
        level = data.getShort(Params.LEVEL);
    }
}
