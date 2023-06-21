package com.bamisu.log.gameserver.module.ingame.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.Collection;

/**
 * Create by Popeye on 3:04 PM, 12/24/2020
 */
public class RecSelectTarget extends BaseCmd {
    public String actorID;

    public RecSelectTarget(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        actorID = data.getUtfString(Params.ACTOR_ID);
    }
}
