package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.StoneDataVO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.characters.summon.entities.HeroSummonVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Collection;
import java.util.List;

public class SendUsingItem extends BaseMsg {

    public Collection<ResourcePackage> listReward;
    public List<HeroModel> listHeroModel;
    public List<EquipDataVO> listEquipData;
    public List<StoneDataVO> listStoneData;

    public Collection<ResourcePackage> listSpending;


    public SendUsingItem() {
        super(CMD.CMD_USING_ITEM);
    }

    public SendUsingItem(short errorCode) {
        super(CMD.CMD_USING_ITEM, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        //Nhan dc
        ISFSArray arrayRewardPack = new SFSArray();
        ISFSObject objRewardPack;
        if(listReward != null) {
            for (ResourcePackage resourcePackage : listReward) {
                if (resourcePackage.id.contains("_")) {
                    arrayRewardPack.addSFSObject(resourcePackage.toSFSObjectGem());
                } else {
                    arrayRewardPack.addSFSObject(resourcePackage.toSFSObject());
                }
            }
        }
        if (listHeroModel != null){
            CharactersConfigManager charactersConfigManager = CharactersConfigManager.getInstance();
            for (HeroModel model: listHeroModel){
                objRewardPack = new SFSObject();

                objRewardPack.putUtfString(Params.HASH, model.hash);
                objRewardPack.putUtfString(Params.ID, model.id);
                objRewardPack.putShort(Params.STAR, model.star);
                objRewardPack.putShort(Params.LEVEL, model.readLevel());
                objRewardPack.putLong(Params.AMOUNT, 1);

                HeroVO heroCf = charactersConfigManager.getHeroConfig(model.id);
                objRewardPack.putUtfString("clas", heroCf.clas);
                objRewardPack.putSFSObject(Params.STATS, SFSObject.newFromJsonData(Utils.toJson(heroCf)));
                objRewardPack.putSFSObject(Params.GROW, SFSObject.newFromJsonData(Utils.toJson(charactersConfigManager.getHeroStatsGrowConfig(model.id))));
                arrayRewardPack.addSFSObject(objRewardPack);
            }
        }
        if(listEquipData != null){
            for (EquipDataVO model: listEquipData){
                objRewardPack = new SFSObject();

                objRewardPack.putUtfString(Params.HASH, model.hash);
                objRewardPack.putUtfString(Params.ID, model.id);
                objRewardPack.putInt(Params.STAR, model.star);
                objRewardPack.putInt(Params.LEVEL, model.level);
                objRewardPack.putLong(Params.AMOUNT, model.count);
                arrayRewardPack.addSFSObject(objRewardPack);
            }
        }
        if(listStoneData != null){
            for (StoneDataVO model: listStoneData){
                objRewardPack = new SFSObject();

                objRewardPack.putUtfString(Params.HASH, model.hash);
                objRewardPack.putUtfString(Params.ID, model.id);
                objRewardPack.putInt(Params.LEVEL, model.level);
                objRewardPack.putLong(Params.AMOUNT, model.count);
                arrayRewardPack.addSFSObject(objRewardPack);
            }
        }
        data.putSFSArray(Params.LIST_REWARD, arrayRewardPack);

        //Tieu ton
        SFSArray arraySpendPack = new SFSArray();
        for (ResourcePackage resourcePackage: listSpending){
            arraySpendPack.addSFSObject(resourcePackage.toSFSObject());
        }
        data.putSFSArray(Params.LIST_SPENDING, arraySpendPack);

    }
}
