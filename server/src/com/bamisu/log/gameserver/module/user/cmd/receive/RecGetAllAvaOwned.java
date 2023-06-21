package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetAllAvaOwned extends BaseCmd {

    public RecGetAllAvaOwned(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {

    }
}
