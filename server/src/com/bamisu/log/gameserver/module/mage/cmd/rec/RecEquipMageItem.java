package com.bamisu.log.gameserver.module.mage.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecEquipMageItem extends BaseCmd {

    public String hashItem;

    public RecEquipMageItem(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hashItem = data.getUtfString(Params.ModuleBag.HASH);
    }
}
