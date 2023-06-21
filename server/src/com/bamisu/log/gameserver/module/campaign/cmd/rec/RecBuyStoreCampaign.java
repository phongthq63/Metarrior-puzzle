package com.bamisu.log.gameserver.module.campaign.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecBuyStoreCampaign extends BaseCmd {

    public byte position;

    public RecBuyStoreCampaign(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        position = data.getByte(Params.POSITION);
    }
}
