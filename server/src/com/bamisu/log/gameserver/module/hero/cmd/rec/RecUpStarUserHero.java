package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

public class RecUpStarUserHero extends BaseCmd {

    public String hashHeroUp;
    public List<String> hashHeroFission;



    public RecUpStarUserHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hashHeroUp = data.getUtfString(Params.HASH_HERO);
        ISFSArray resources = data.getSFSArray(Params.ModuleHero.FISSION);
        hashHeroFission = new ArrayList<>();
        for (int i = 0; i < resources.size(); i++) {
            hashHeroFission.add(resources.getText(i));
        }

    }
}
