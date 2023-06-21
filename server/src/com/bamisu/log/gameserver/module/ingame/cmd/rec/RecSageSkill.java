package com.bamisu.log.gameserver.module.ingame.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 11:48 AM, 5/15/2020
 */
public class RecSageSkill extends BaseCmd {
    public String skillID;

    public RecSageSkill(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        skillID = data.getUtfString(Params.SKILL_ID);
    }
}
