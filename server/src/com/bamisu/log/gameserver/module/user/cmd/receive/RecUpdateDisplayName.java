package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 *
 * Created by Popeye on 4/25/2017.
 */
public class RecUpdateDisplayName extends BaseCmd {
    public String displayName; //ten hien thi

    public RecUpdateDisplayName(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.displayName = data.getUtfString(Params.USER_DISPLAY_NAME);
    }
}
