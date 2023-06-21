package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.nft.HeroTokenModel;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendListHeroBreed extends BaseMsg {
    public List<HeroModel> models;
    public Map<String, HeroModel> parent = new HashMap<>();
    public long uid;
    public Zone zone;

    public SendListHeroBreed() {
        super(CMD.CMD_GET_LIST_HERO_BREED);
    }

    public SendListHeroBreed(int cmdId) {
        super(cmdId);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }

        ISFSArray heroes = new SFSArray();
        if (this.cmdId == CMD.CMD_GET_LIST_HERO_COUNTDOWN) {
            Map<String, SFSArray> mapTimer = new HashMap<>();
            for (HeroModel model : models) {
                String key = model.fatherHash + "," + model.motherHash;
                if (!mapTimer.containsKey(key)) {
                    mapTimer.put(key, new SFSArray());
                }

                SFSArray timer = mapTimer.get(key);
                timer.addLong(model.timeClaim);
            }

            for (Map.Entry<String, SFSArray> entry : mapTimer.entrySet()) {
                String[] parentHash = entry.getKey().split(",");
                HeroModel fatherModel = HeroManager.getInstance().getHeroModel(uid, parentHash[0], zone);
                HeroModel motherModel = HeroManager.getInstance().getHeroModel(uid, parentHash[1], zone);
                ISFSObject obj = new SFSObject();
                ISFSObject father = new SFSObject();
                ISFSObject mother = new SFSObject();
                if (fatherModel != null) {
                    father.putText(Params.ID, fatherModel.id);
                    father.putText(Params.HASH, fatherModel.hash);
                    father.putShort(Params.STAR, fatherModel.star);
                    father.putShort(Params.LEVEL, fatherModel.readLevel());
                }

                if (motherModel != null) {
                    mother.putText(Params.ID, motherModel.id);
                    mother.putText(Params.HASH, motherModel.hash);
                    mother.putShort(Params.STAR, motherModel.star);
                    mother.putShort(Params.LEVEL, motherModel.readLevel());
                }

                obj.putSFSObject("father", father);
                obj.putSFSObject("mother", mother);
                obj.putSFSArray(Params.TIME, entry.getValue());
                heroes.addSFSObject(obj);
            }
        } else {
            for (HeroModel model : models) {
                String tokenId = "";
                if (model.type == EHeroType.NFT.getId()) {
                    HeroTokenModel heroTokenModel = HeroTokenModel.copyFromDBtoObject(model.hash, zone);
                    tokenId = heroTokenModel.tokenId;
                }

                ISFSObject hero = new SFSObject();
                hero.putText(Params.ID, model.id);
                hero.putText(Params.HASH, model.hash);
                hero.putText(Params.NFT, tokenId);
                hero.putShort(Params.LEVEL, model.level);
                hero.putShort(Params.STAR, model.star);
                hero.putByte(Params.BREED, model.breed);
                hero.putByte(Params.MAX_BREED, model.maxBreed);
                heroes.addSFSObject(hero);
            }
        }
        this.data.putSFSArray(Params.LIST, heroes);
    }
}
