package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 1:55 PM, 4/15/2020
 */
public class RecUpdateAvatar extends BaseCmd {
    public String avatar;
    public int frame;

    public RecUpdateAvatar(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        this.avatar = data.getUtfString(Params.USER_AVATAR);
        this.frame = data.getInt(Params.FRAME);
    }
}
