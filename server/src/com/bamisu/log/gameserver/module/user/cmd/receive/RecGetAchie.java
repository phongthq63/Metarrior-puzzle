package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.base.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 11/16/2017.
 */
public class RecGetAchie extends BaseCmd {
    public long uid = -1;

    public RecGetAchie(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.uid = data.getLong(Params.USER_ID);
    }
}
