package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 3:06 PM, 4/23/2020
 */
public class RecLinkAccount extends BaseCmd {
    public int socialNetwork;
    public String token;

    public RecLinkAccount(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        socialNetwork = data.getInt(Params.SOCIAL_NETWORK);
        token = data.getUtfString(Params.TOKEN);
    }
}
