package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetHeroFriendBorrow extends BaseCmd {

    public short id;

    public RecGetHeroFriendBorrow(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        id = data.getShort(Params.ID);
    }
}
