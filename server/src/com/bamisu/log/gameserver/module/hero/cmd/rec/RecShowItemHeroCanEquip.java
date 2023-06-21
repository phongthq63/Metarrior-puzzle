package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecShowItemHeroCanEquip extends BaseCmd {

    public String hashHero;
    public short position = -1;

    public RecShowItemHeroCanEquip(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        position = data.getShort(Params.ModuleHero.POSITION);
        hashHero = data.getUtfString(Params.ModuleHero.HASH);
    }
}
