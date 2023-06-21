package com.bamisu.log.gameserver.module.bag.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 12/6/2022 - 11:03 PM
 */
public class RecUpdateEnergy extends BaseCmd {

    public List<String> hashHeros;

    public RecUpdateEnergy(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hashHeros = new ArrayList<>(data.getUtfStringArray(Params.HASH));
    }
}
