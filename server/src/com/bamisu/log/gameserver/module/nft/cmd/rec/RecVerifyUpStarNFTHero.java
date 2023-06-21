package com.bamisu.log.gameserver.module.nft.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 3/23/2022 - 8:40 PM
 */
public class RecVerifyUpStarNFTHero extends BaseCmd {

    public String hashHero;
    public String transactionHash;
    public List<String> tokenId = new ArrayList<>();

    public RecVerifyUpStarNFTHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hashHero = data.getUtfString(Params.HASH);
        transactionHash = data.getUtfString(Params.TRANSACTION_ID);
        ISFSArray tokens = data.getSFSArray(Params.TOKEN);
        for (int i = 0; i < tokens.size(); i++) {
            tokenId.add(tokens.getText(i));
        }
//        tokenId = new ArrayList<>(data.getUtfStringArray(Params.TOKEN));
    }
}
