package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 11:40 AM, 4/14/2020
 */
public class RecUpdateLanguge extends BaseCmd {
    public String languageID;

    public RecUpdateLanguge(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        this.languageID = data.getUtfString(Params.LANGUAGE);
    }
}
