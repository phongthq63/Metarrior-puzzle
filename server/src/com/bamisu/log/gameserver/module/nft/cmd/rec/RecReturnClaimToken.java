package com.bamisu.log.gameserver.module.nft.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Quach Thanh Phong
 * On 5/3/2022 - 2:37 PM
 */
public class RecReturnClaimToken extends BaseCmd {

    public String transactionId;

    public RecReturnClaimToken(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        transactionId = data.getUtfString(Params.ID);
    }
}
