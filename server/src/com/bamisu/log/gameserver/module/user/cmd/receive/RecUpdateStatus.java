package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.base.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 6/29/2017.
 */
public class RecUpdateStatus extends BaseCmd {
    public String sText;

    public RecUpdateStatus(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        sText = data.getUtfString(Params.USER_STATUS_TEXT);
    }
}
