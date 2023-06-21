package com.bamisu.log.gameserver.module.campaign.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Quach Thanh Phong
 * On 11/22/2022 - 2:29 AM
 */
public class RecGetMissionRank extends BaseCmd {

    public int page;
    public int size;

    public RecGetMissionRank(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        page = data.getInt("page");
        size = data.getInt("size");
    }
}
