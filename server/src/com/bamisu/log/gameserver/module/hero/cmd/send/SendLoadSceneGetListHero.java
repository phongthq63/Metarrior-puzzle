package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.item.entities.ItemSlotVO;
import com.bamisu.gamelib.item.entities.StoneSlotVO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.HeroBaseStatsModel;
import com.bamisu.log.gameserver.datamodel.hero.HeroSkillModel;
import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserBlessingHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.nft.HeroTokenModel;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SendLoadSceneGetListHero extends BaseMsg {

    public byte countIncreateBag;
    public int maxBagHero;
    public UserAllHeroModel userAllHeroModel;       //Da dc deep clone
    public UserBlessingHeroModel userBlessingHeroModel;
    public Zone zone;
    public boolean isHttp = false;

    public List<HeroModel> listHeroModel;


    public SendLoadSceneGetListHero() {
        super(CMD.CMD_LOAD_SCENE_GET_LIST_HERO);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        CharactersConfigManager charactersConfigManager = CharactersConfigManager.getInstance();
        List<HeroModel> listHeroModelDulicate = listHeroModel.stream().
                map(HeroModel::createByHeroModel).
                collect(Collectors.toList());
        if (this.isHttp) {
            ISFSArray heroes = new SFSArray();
            for (HeroModel heroModel : listHeroModelDulicate) {
                if (EHeroType.NORMAL.getId() == heroModel.type || heroModel.isBreeding) {
                    continue;
                }
                ISFSObject hero = SFSObject.newFromJsonData(Utils.toJson(HeroManager.getInstance().getStatsNormalHeroModel(heroModel)));
                HeroBaseStatsModel heroBaseStatsModel = HeroBaseStatsModel.copyFromDBtoObject(heroModel.hash, heroModel.id, zone);
                hero.putText(Params.ID, heroModel.id);
                hero.putText(Params.HASH, heroModel.hash);
                hero.putShort(Params.LEVEL, heroModel.readLevel());
                hero.putShort(Params.STAR, heroModel.star);
                hero.putText(Params.DESCRIPTION, heroBaseStatsModel.baseStats.description);
                hero.putText(Params.KINGDOM, heroBaseStatsModel.baseStats.kingdom);
                hero.putText(Params.ELEMENT, heroBaseStatsModel.baseStats.element);
                hero.putText(Params.GENDER, heroBaseStatsModel.baseStats.gender);
                hero.putText(Params.NAME, heroBaseStatsModel.baseStats.name);
                HeroTokenModel tokenModel = HeroTokenModel.copyFromDBtoObject(heroModel.hash, zone);
                hero.putText("tokenId", tokenModel.tokenId);
                heroes.addSFSObject(hero);
            }

            this.data.putSFSArray(Params.LIST, heroes);
            return;
        }
        data.putByte(Params.COUNT, countIncreateBag);
        data.putShort(Params.ModuleHero.MAX_SIZE_BAG_HERO, (short) maxBagHero);

        Set<String> listBlessing = HeroManager.BlessingManager.getInstance().getListHeroSlotBlessing(userBlessingHeroModel, zone).stream().
                map(obj -> obj.hashHero).
                collect(Collectors.toSet());
        ISFSArray listHeroPack = new SFSArray();
        ISFSObject heroPack;
        ISFSArray listEquipPack;
        ISFSObject equipPack;
        ISFSArray listStonePack;
        ISFSObject stonePack;


        boolean blessing;
        HeroTokenModel heroTokenModel;
        for(HeroModel heroModel : listHeroModelDulicate){
            heroPack = new SFSObject();

            heroPack.putUtfString(Params.ModuleHero.HASH, heroModel.hash);
            heroPack.putUtfString(Params.ID, heroModel.id);
            //Neu dc ban phuoc
            blessing = listBlessing.contains(heroModel.hash);
            heroPack.putBool(Params.BLESSING, blessing);
            if(blessing){
                //Neu dc ban phuoc
                heroModel.level = (short) HeroManager.BlessingManager.getInstance().getLevelBlessingHero(userAllHeroModel, heroModel.id);
            }
            
            heroPack.putShort(Params.LEVEL, heroModel.readLevel());
            heroPack.putShort(Params.STAR, heroModel.star);
            heroPack.putInt(Params.POWER, HeroManager.getInstance().getPower(heroModel, zone));

            //Item tren hero
            listEquipPack = new SFSArray();
            for (ItemSlotVO itemSlotVO : heroModel.equipment) {
                if (itemSlotVO.equip == null || !itemSlotVO.haveLock()) continue;

                equipPack = new SFSObject();
                equipPack.putUtfString(Params.HASH, itemSlotVO.equip.hash);
                equipPack.putUtfString(Params.ID, itemSlotVO.equip.id);
                equipPack.putInt(Params.ModuleHero.POSITION, itemSlotVO.position);
                equipPack.putInt(Params.LEVEL, itemSlotVO.equip.level);
                equipPack.putInt(Params.STAR, itemSlotVO.equip.star);
                equipPack.putInt(Params.EXP, itemSlotVO.equip.exp);

                //Stone tren item
                listStonePack = new SFSArray();
                for (StoneSlotVO slotStoneVO : itemSlotVO.equip.listSlotStone) {
                    if (!slotStoneVO.haveLock() || slotStoneVO.stoneVO == null) continue;

                    stonePack = new SFSObject();
                    stonePack.putUtfString(Params.ID, slotStoneVO.stoneVO.id);
                    stonePack.putUtfString(Params.HASH, slotStoneVO.stoneVO.hash);
                    stonePack.putInt(Params.LEVEL, slotStoneVO.stoneVO.level);
                    stonePack.putInt(Params.TYPE, slotStoneVO.stoneVO.type);
                    stonePack.putShort(Params.POSITION_STONE, (short) slotStoneVO.position);
                    listStonePack.addSFSObject(stonePack);
                }
                equipPack.putSFSArray(Params.ModuleBag.STONE, listStonePack);

                listEquipPack.addSFSObject(equipPack);
            }

            heroPack.putSFSArray(Params.ModuleHero.EQUIPMENT, listEquipPack);
            heroPack.putSFSArray(Params.SKILLS, SFSArray.newFromJsonData(Utils.toJson(HeroSkillModel.getFromDB(heroModel, zone).skills)));

            switch (EHeroType.fromId(heroModel.type)) {
                case NORMAL:
                    heroPack.putSFSObject(Params.STATS, SFSObject.newFromJsonData(Utils.toJson(charactersConfigManager.getHeroConfig(heroModel.id))));
                    heroPack.putSFSObject(Params.GROW, SFSObject.newFromJsonData(Utils.toJson(charactersConfigManager.getHeroStatsGrowConfig(heroModel.id))));
                    break;
                case NFT:
                    HeroBaseStatsModel heroBaseStatsModel = HeroBaseStatsModel.copyFromDBtoObject(heroModel.hash, heroModel.id, zone);
                    heroPack.putSFSObject(Params.STATS, SFSObject.newFromJsonData(Utils.toJson(heroBaseStatsModel.baseStats)));
                    heroPack.putSFSObject(Params.GROW, SFSObject.newFromJsonData(Utils.toJson(heroBaseStatsModel.growStats)));
                    break;
            }

            heroTokenModel = HeroTokenModel.copyFromDBtoObject(heroModel.hash, zone);
            heroPack.putUtfString(Params.NFT, heroTokenModel != null ? heroTokenModel.tokenId : "");

            listHeroPack.addSFSObject(heroPack);
        }

        data.putSFSArray(Params.ModuleHero.LIST_HERO, listHeroPack);
    }
}
