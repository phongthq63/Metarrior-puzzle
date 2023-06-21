package com.bamisu.log.gameserver.module.bag.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetWeaponToUpgrade extends BaseCmd {
    public String hashItem;
    public RecGetWeaponToUpgrade(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hashItem = data.getUtfString(Params.HASH_WEAPON);
    }
}
