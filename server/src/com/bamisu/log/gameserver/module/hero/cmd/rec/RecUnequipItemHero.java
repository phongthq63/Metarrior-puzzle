package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecUnequipItemHero extends BaseCmd {

    public String hashItem;

    public RecUnequipItemHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hashItem = data.getUtfString(Params.ModuleBag.HASH);
    }
}
