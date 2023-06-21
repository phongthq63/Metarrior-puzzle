package com.bamisu.log.gameserver.module.mage.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetBagMageEquipment extends BaseCmd {

    public short position;

    public RecGetBagMageEquipment(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        position = data.getShort(Params.ModuleHero.POSITION);
    }
}
