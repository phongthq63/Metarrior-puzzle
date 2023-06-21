package com.bamisu.log.gameserver.module.nft.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Quach Thanh Phong
 * On 3/6/2022 - 3:37 AM
 */
public class RecVerifyBuyToken extends BaseCmd {

    public String transactionHash;
    public String name;
    public int count;


    public RecVerifyBuyToken(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        transactionHash = data.getUtfString(Params.TRANSACTION_ID);
        name = data.getUtfString(Params.NAME);
        count = data.getInt(Params.COUNT);
    }
}
