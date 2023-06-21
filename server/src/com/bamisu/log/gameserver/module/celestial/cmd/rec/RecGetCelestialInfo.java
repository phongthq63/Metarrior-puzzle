package com.bamisu.log.gameserver.module.celestial.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetCelestialInfo extends BaseCmd {

    public String idCelestial;

    public RecGetCelestialInfo(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        idCelestial = data.getUtfString(Params.ID);
    }
}
