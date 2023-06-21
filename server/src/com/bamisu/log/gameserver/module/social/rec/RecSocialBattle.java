package com.bamisu.log.gameserver.module.social.rec;

import com.bamisu.log.gameserver.module.hero.entities.HeroPosition;
import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by Popeye on 3:39 PM, 8/17/2020
 */
public class RecSocialBattle extends BaseCmd {
    public List<HeroPosition> update = new ArrayList<>();
    public Collection<String> sageSkill = new ArrayList<>();
    public long enemyUserID;

    public RecSocialBattle(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        this.enemyUserID = data.getLong(Params.UID);
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
