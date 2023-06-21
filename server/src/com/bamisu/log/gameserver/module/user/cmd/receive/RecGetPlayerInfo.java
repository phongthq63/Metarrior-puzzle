package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 8/28/2017.
 */
public class RecGetPlayerInfo extends BaseCmd {
    public long uId;

    public RecGetPlayerInfo(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        uId = data.getLong(Params.UID);
    }
}
