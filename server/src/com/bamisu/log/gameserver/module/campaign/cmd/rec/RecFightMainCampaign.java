package com.bamisu.log.gameserver.module.campaign.cmd.rec;

import com.bamisu.log.gameserver.module.hero.entities.HeroPosition;
import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by Popeye on 10:53 AM, 2/6/2020
 */
public class RecFightMainCampaign extends BaseCmd {
    public int area;
    public int station;
    public List<HeroPosition> update = new ArrayList<>();
    public Collection<String> sageSkill = new ArrayList<>();
    public boolean isTutorial = false;

    public RecFightMainCampaign(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.area = data.getInt(Params.AREA);
        this.station = data.getInt(Params.STATION);

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

        if(data.containsKey(Params.IS_TUTORIAL)){
            isTutorial = true;
        }
    }
}
