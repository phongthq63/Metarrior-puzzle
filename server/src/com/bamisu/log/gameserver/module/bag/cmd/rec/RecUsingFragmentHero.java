package com.bamisu.log.gameserver.module.bag.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecUsingFragmentHero extends BaseCmd {

    public String id;
    public int count;


    public RecUsingFragmentHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        id = data.getUtfString(Params.ID);
        count = data.getInt(Params.COUNT);
    }
}
