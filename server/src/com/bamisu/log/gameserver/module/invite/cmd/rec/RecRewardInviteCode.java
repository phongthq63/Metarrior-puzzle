package com.bamisu.log.gameserver.module.invite.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecRewardInviteCode extends BaseCmd {

    public String id;
    public int point;

    public RecRewardInviteCode(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        id = data.getUtfString(Params.ID);
        point = data.getShort(Params.POINT);
    }
}
