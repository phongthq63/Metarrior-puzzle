package com.bamisu.log.gameserver.module.bag.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.bag.entities.ItemGet;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

public class RecFusionWeapon extends BaseCmd {

    public List<ItemGet> list = new ArrayList<>();


    public RecFusionWeapon(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {

        ISFSArray array = data.getSFSArray(Params.LIST);
        ISFSObject obj = null;
        ItemGet weapon = null;
        for (int i = 0; i < array.size(); i++){
            obj = array.getSFSObject(i);

            weapon = ItemGet.create(obj.getUtfString(Params.HASH), obj.getInt(Params.COUNT));
            list.add(weapon);
        }
    }
}
