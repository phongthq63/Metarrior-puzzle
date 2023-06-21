package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

public class RecRetireHero extends BaseCmd {

    public List<String> listHashHero;

    public RecRetireHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        listHashHero = new ArrayList<>(data.getUtfStringArray(Params.HASH_HERO));
    }
}
