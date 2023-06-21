package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 5:41 PM, 4/14/2020
 */
public class RecUpdateStatusText extends BaseCmd {
    public String content;

    public RecUpdateStatusText(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        this.content = data.getUtfString(Params.CONTENT);
    }


}
