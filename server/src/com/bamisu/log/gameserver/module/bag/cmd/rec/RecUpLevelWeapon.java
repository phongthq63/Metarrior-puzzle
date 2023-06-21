package com.bamisu.log.gameserver.module.bag.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.bag.entities.ItemGet;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

public class RecUpLevelWeapon extends BaseCmd {

    public String hashW;
    public List<ItemGet> listWeapon = new ArrayList<>();
    public List<ResourcePackage> listHammer = new ArrayList<>();

    public RecUpLevelWeapon(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {

        hashW = data.getUtfString(Params.HASH_WEAPON);

        //Equip
        ISFSArray equip = data.getSFSArray(Params.LIST_MY_WEAPON);
        ISFSObject objEquip;
        ItemGet stone;
        for (int i = 0; i < equip.size(); i++){
            objEquip = equip.getSFSObject(i);

            stone = ItemGet.create(objEquip.getUtfString(Params.HASH_WEAPON), objEquip.getInt(Params.COUNT_WEAPON));
            listWeapon.add(stone);
        }

        //Hammer
        ISFSArray hammer = data.getSFSArray(Params.LIST);
        ISFSObject objHammer;
        ResourcePackage resourcePackage;
        for (int i = 0; i < hammer.size(); i++){
            objHammer = hammer.getSFSObject(i);

            resourcePackage = new ResourcePackage(objHammer.getUtfString(Params.ID), objHammer.getInt(Params.AMOUNT));
            listHammer.add(resourcePackage);
        }
    }
}
