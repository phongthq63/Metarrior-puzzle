package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecUpdateHeroBlessing extends BaseCmd {

    public String hash;
    public short position;

    public RecUpdateHeroBlessing(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hash = data.getUtfString(Params.HASH_HERO);
        position = data.getShort(Params.POSITION);
    }
}
