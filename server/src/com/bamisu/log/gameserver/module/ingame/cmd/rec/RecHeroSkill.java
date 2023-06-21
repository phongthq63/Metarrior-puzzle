package com.bamisu.log.gameserver.module.ingame.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 2:13 PM, 5/16/2020
 */
public class RecHeroSkill extends BaseCmd {
    public String actorID;

    public RecHeroSkill(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        actorID = data.getUtfString(Params.ACTOR_ID);
    }
}
