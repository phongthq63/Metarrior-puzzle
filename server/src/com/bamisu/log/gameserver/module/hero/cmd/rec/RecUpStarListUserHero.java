package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.google.common.collect.Lists;
import com.bamisu.log.gameserver.module.hero.entities.HeroUpHash;
import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

public class RecUpStarListUserHero extends BaseCmd {

    public List<HeroUpHash> listUpHash = new ArrayList<>();

    public RecUpStarListUserHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        ISFSArray arrayPack = data.getSFSArray(Params.LIST);
        ISFSObject objPack;
        for(int i = 0; i < arrayPack.size(); i++){
            objPack = arrayPack.getSFSObject(i);
            listUpHash.add(
                    HeroUpHash.create(
                            objPack.getUtfString(Params.HASH_HERO),
                            Lists.newArrayList(objPack.getUtfStringArray(Params.ModuleHero.FISSION))));
        }
    }
}
