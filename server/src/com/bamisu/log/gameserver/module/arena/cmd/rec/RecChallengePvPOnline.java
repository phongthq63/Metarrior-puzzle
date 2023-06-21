package com.bamisu.log.gameserver.module.arena.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 11:04 AM, 4/23/2021
 */
public class RecChallengePvPOnline extends BaseCmd {
    public short id;

    public RecChallengePvPOnline(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        id = data.getShort(Params.ID);
    }
}
