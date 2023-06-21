package com.bamisu.log.gameserver.module.mage.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecEquipMageSkin extends BaseCmd {

    public String idSkin;

    public RecEquipMageSkin(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        idSkin = data.getUtfString(Params.ID);
    }
}
