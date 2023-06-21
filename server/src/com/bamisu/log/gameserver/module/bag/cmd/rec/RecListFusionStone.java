package com.bamisu.log.gameserver.module.bag.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.bag.entities.ItemGet;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

public class RecListFusionStone extends BaseCmd {

    public List<ItemGet> list = new ArrayList<>();


    public RecListFusionStone(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        ISFSArray arrayPack = data.getSFSArray(Params.LIST);
        ISFSObject objPack;
        for(int i = 0; i < arrayPack.size(); i++){
            objPack = arrayPack.getSFSObject(i);

            list.add(ItemGet.create(objPack.getUtfString(Params.HASH), objPack.getInt(Params.COUNT)));
        }
    }
}
