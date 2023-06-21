package com.bamisu.log.gameserver.module.nft.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Quach Thanh Phong
 * On 3/6/2022 - 3:37 AM
 */
public class RecVerifyClaimToken extends BaseCmd {

    public String id;
    public String transactionHash;
    public double count;


    public RecVerifyClaimToken(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        id = data.getText(Params.MONEY_TYPE);
        transactionHash = data.getText(Params.TRANS_ID);
        count = data.getDouble(Params.MONEY);
    }
}
