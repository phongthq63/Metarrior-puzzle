package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 4:45 PM, 4/21/2020
 */
public class RecActiveGiftcode extends BaseCmd {
    public String code;

    public RecActiveGiftcode(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        this.code = data.getUtfString(Params.CODE).toLowerCase();
    }
}
