package com.bamisu.log.gameserver.module.nft.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 3/3/2022 - 12:14 AM
 */
public class RecRemoveMintNFTHero extends BaseCmd {

    public List<String> listHashHero;

    public RecRemoveMintNFTHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        listHashHero = new ArrayList<>(data.getUtfStringArray(Params.HASH));
    }
}
