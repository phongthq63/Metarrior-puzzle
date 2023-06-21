package com.bamisu.log.gameserver.module.nft.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Quach Thanh Phong
 * On 3/23/2022 - 9:07 PM
 */
public class RecReturnNFTHeroUpStar extends BaseCmd {

    public String hashHero;

    public RecReturnNFTHeroUpStar(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hashHero = data.getUtfString(Params.HASH);
    }
}
