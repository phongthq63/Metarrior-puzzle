package com.bamisu.log.gameserver.module.bag.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecAddStoneToEquip extends BaseCmd {

    public String hashWeapon;
    public String hashStone;
    public int position;


    public RecAddStoneToEquip(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {

        hashWeapon = data.getUtfString(Params.HASH_WEAPON);
        hashStone = data.getUtfString(Params.HASH_STONE);
        position = data.getInt(Params.POSITION);
    }
}
