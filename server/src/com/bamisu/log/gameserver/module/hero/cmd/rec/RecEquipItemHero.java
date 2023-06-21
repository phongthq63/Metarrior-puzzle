package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecEquipItemHero extends BaseCmd {

    public String hashHero;
    public String hashItem;

    public RecEquipItemHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hashHero = data.getUtfString(Params.ModuleHero.HASH);
        hashItem = data.getUtfString(Params.ModuleBag.HASH);
    }
}
