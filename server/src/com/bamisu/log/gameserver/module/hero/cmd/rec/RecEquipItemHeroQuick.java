package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

public class RecEquipItemHeroQuick extends BaseCmd {

    public String hash;
    public List<String> listEquip;

    public RecEquipItemHeroQuick(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hash = data.getUtfString(Params.HASH);
        listEquip = new ArrayList<>(data.getUtfStringArray(Params.EQUIPMENT));
    }
}
