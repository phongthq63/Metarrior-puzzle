package com.bamisu.log.gameserver.module.nft.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 2/13/2022 - 10:20 AM
 */
public class RecVerifyMintHero extends BaseCmd {

    public String transactionHash;
    public List<String> tokenId;

    public RecVerifyMintHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        transactionHash = data.getUtfString(Params.TRANSACTION_ID);
        tokenId = new ArrayList<>(data.getUtfStringArray(Params.TOKEN));
    }
}
