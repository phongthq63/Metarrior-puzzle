package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.log.gameserver.module.hero.entities.HeroPosition;
import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;

public class RecUpdateTeamHero extends BaseCmd {

    public int type;
    public List<HeroPosition> update = new ArrayList<>();

    public RecUpdateTeamHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        type = data.getInt(Params.TYPE);

        ISFSArray arrayPack = data.getSFSArray(Params.TEAM);

        ISFSObject objPack;
        for(int i = 0; i < arrayPack.size(); i++){
            objPack = arrayPack.getSFSObject(i);
            update.add(new HeroPosition(objPack.getUtfString(Params.HASH_HERO), objPack.getShort(Params.POSITION)));
        }
    }
}
