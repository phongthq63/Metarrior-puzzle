package com.bamisu.log.gameserver.module.nft.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.List;

public class RecBreed extends BaseCmd {
    public String fatherHash;
    public String motherHash;
    public int bannerId;
    public String resource;
    public int count;
    public Zone zone;
    public long uid;

    public RecBreed(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        this.fatherHash = this.data.getText(Params.HASH + 1);
        this.motherHash = this.data.getText(Params.HASH + 2);
        this.bannerId = this.data.getInt(Params.ID);
        this.resource = this.data.getText(Params.RESOURCE_TYPE);
        this.count = this.data.getInt(Params.COUNT);
    }
}
