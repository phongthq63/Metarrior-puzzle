package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.nft.HeroTokenModel;
import com.bamisu.log.gameserver.datamodel.nft.UserBurnHeroModel;
import com.bamisu.log.gameserver.datamodel.nft.entities.HeroUpstarBurn;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Map;


public class SendListHeroAscendCountdown extends BaseMsg {
    public UserBurnHeroModel model;
    public Zone zone;
    public boolean isHttp = false;
    public SendListHeroAscendCountdown() {
        super(CMD.CMD_GET_ASCEND_COUNTDOWN);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }

        ISFSArray lst = new SFSArray();
        for (Map.Entry<String, HeroUpstarBurn> entry : model.mapUpstar.entrySet()) {
            HeroUpstarBurn burnModel = entry.getValue();
            HeroModel heroModel = HeroModel.createByHeroModel(burnModel.heroModel);
            ISFSObject obj = new SFSObject();
            obj.putText(Params.ID, heroModel.id);
            obj.putText(Params.HASH, heroModel.hash);
            obj.putShort(Params.STAR, (short) (heroModel.star + 1));
            obj.putShort(Params.LEVEL, heroModel.readLevel());
            HeroTokenModel heroTokenModel = HeroTokenModel.copyFromDBtoObject(heroModel.hash, zone);
            obj.putText(Params.NFT, heroTokenModel.tokenId);
            obj.putLong(Params.TIME, burnModel.timer);
            if (isHttp) {
                obj.putByte(Params.BREED, heroModel.breed);
                ISFSArray arrFood = new SFSArray();
                for (HeroModel foodModel : burnModel.listFission) {
                    ISFSObject food = new SFSObject();
                    food.putText(Params.HASH, heroModel.hash);
                    HeroTokenModel foodTokenModel = HeroTokenModel.copyFromDBtoObject(foodModel.hash, zone);
                    food.putText(Params.NFT, foodTokenModel.tokenId);
                    arrFood.addSFSObject(food);
                }

                obj.putSFSArray(Params.RESOURCE, arrFood);
            }
            lst.addSFSObject(obj);
        }

        this.data.putSFSArray(Params.LIST, lst);
    }
}
