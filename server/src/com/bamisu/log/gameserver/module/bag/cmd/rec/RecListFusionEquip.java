package com.bamisu.log.gameserver.module.bag.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.bag.entities.ItemGet;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

public class RecListFusionEquip extends BaseCmd {

    public List<List<ItemGet>> list = new ArrayList<>();


    public RecListFusionEquip(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        ISFSArray arrayListPack = data.getSFSArray(Params.LIST);
        ISFSArray arrayPack;
        ISFSObject objPack;
        List<ItemGet> fusion;
        for (int i = 0; i < arrayListPack.size(); i++){
            arrayPack = arrayListPack.getSFSArray(i);

            fusion = new ArrayList<>();
            for (int j = 0; j < arrayPack.size(); j++){
                objPack = arrayPack.getSFSObject(j);

                fusion.add(ItemGet.create(objPack.getUtfString(Params.HASH), objPack.getInt(Params.COUNT)));
            }
            list.add(fusion);
        }
    }
}
