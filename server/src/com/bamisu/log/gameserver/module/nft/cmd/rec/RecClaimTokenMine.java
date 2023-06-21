package com.bamisu.log.gameserver.module.nft.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Quach Thanh Phong
 * On 2/13/2022 - 9:06 PM
 */
public class RecClaimTokenMine extends BaseCmd {

    public String tokenName;
    public double count;

    public RecClaimTokenMine(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        tokenName = data.getUtfString(Params.NAME);
        count = data.getDouble(Params.COUNT);
    }
}
