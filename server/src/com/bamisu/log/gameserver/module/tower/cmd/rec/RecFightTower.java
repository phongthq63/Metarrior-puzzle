package com.bamisu.log.gameserver.module.tower.cmd.rec;

import com.bamisu.log.gameserver.module.hero.entities.HeroPosition;
import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by Popeye on 3:23 PM, 6/1/2020
 */
public class RecFightTower extends BaseCmd {
    public List<HeroPosition> update = new ArrayList<>();
    public Collection<String> sageSkill = new ArrayList<>();

    public RecFightTower(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        ISFSArray arrayPack = data.getSFSArray(Params.TEAM);

        ISFSObject objPack;
        for(int i = 0; i < arrayPack.size(); i++){
            objPack = arrayPack.getSFSObject(i);
            update.add(new HeroPosition(objPack.getUtfString(Params.HASH_HERO), objPack.getShort(Params.POSITION)));
        }

        this.sageSkill = data.getUtfStringArray(Params.SAGE_CURRENT_SKILL_LIST);
        if(this.sageSkill == null){
            this.sageSkill = new ArrayList<>();
        }
    }
}
