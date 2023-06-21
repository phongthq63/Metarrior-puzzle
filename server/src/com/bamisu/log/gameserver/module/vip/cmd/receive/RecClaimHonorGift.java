package com.bamisu.log.gameserver.module.vip.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecClaimHonorGift extends BaseCmd {
    public int idHonor;
    public RecClaimHonorGift(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        idHonor = data.getInt(Params.POSITION);
    }
}
