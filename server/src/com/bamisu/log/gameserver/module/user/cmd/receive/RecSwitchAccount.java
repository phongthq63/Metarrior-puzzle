package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 11:48 AM, 5/4/2020
 */
public class RecSwitchAccount extends BaseCmd {
    public int socialNetwork;
    public String token;

    public RecSwitchAccount(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        socialNetwork = data.getInt(Params.SOCIAL_NETWORK);
        token = data.getUtfString(Params.TOKEN);
    }
}
