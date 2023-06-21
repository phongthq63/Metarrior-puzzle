package com.bamisu.log.gameserver.module.mage.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 11:46 AM, 3/10/2020
 */
public class RecStudySkill extends BaseCmd {
    public String id;

    public RecStudySkill(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.id = data.getUtfString(Params.ID);
    }
}
