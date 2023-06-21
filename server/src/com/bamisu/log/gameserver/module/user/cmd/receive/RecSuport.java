package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 9:18 AM, 4/29/2020
 */
public class RecSuport extends BaseCmd {
    public String email;
    public String subtitle;
    public String content;

    public RecSuport(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        this.email = data.getUtfString(Params.USER_EMAIL);
        this.subtitle = data.getUtfString(Params.SUB_TITLE);
        this.content = data.getUtfString(Params.CONTENT);
    }
}
