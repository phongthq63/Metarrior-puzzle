package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.HeroSkillModel;
import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserBlessingHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.entities.CMDUtilsServer;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.item.entities.ItemSlotVO;
import com.bamisu.gamelib.item.entities.StoneSlotVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SendGetUserHeroInfo extends BaseMsg {

    public UserAllHeroModel userAllHeroModel;
    public UserBlessingHeroModel userBlessingHeroModel;
    public HeroModel heroModel;
    public Zone zone;
    public short bonusStory;
    public HeroSkillModel skillModel;

    public SendGetUserHeroInfo() {
        super(CMD.CMD_GET_USER_HERO_INFO);
    }

    public SendGetUserHeroInfo(short errorCode) {
        super(CMD.CMD_GET_USER_HERO_INFO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) return;

        Set<String> listBlessing = HeroManager.BlessingManager.getInstance().getListHeroSlotBlessing(userBlessingHeroModel, zone).parallelStream().map(obj -> obj.hashHero).collect(Collectors.toSet());
        heroModel = HeroModel.createByHeroModel(heroModel);     //Dulicate

        data.putUtfString(Params.ModuleHero.HASH, heroModel.hash);
        data.putUtfString(Params.ID, heroModel.id);

        boolean blessing = listBlessing.contains(heroModel.hash);
        data.putBool(Params.BLESSING, blessing);
        if(blessing){
            //Neu dc ban phuoc
            heroModel.level = (short) HeroManager.BlessingManager.getInstance().getLevelBlessingHero(userAllHeroModel, heroModel.id);
        }
        data.putShort(Params.LEVEL, heroModel.readLevel());
        data.putShort(Params.STAR, heroModel.star);

        //Chi so hero
        data.putSFSObject(Params.ModuleHero.ATTRIBUTE, CMDUtilsServer.statsHeroToSFSObject(HeroManager.getInstance().getStatsHero(heroModel, zone)));

        //Item tren hero
        ISFSArray equipment = new SFSArray();
        ISFSObject item;
        ISFSArray stones;
        ISFSObject stone;
        for (ItemSlotVO itemSlotVO : heroModel.equipment) {
            if (itemSlotVO.equip != null && itemSlotVO.haveLock()) {
                item = new SFSObject();
                item.putInt(Params.POWER, HeroManager.getInstance().getPower(
                        HeroManager.getInstance().getStatsItem(itemSlotVO.equip)));
                item.putUtfString(Params.ModuleBag.HASH, itemSlotVO.equip.hash);
                item.putUtfString(Params.ID, itemSlotVO.equip.id);
                item.putInt(Params.ModuleHero.POSITION, itemSlotVO.position);
                item.putInt(Params.LEVEL, itemSlotVO.equip.level);
                item.putInt(Params.STAR, itemSlotVO.equip.star);

                //Stone tren item
                stones = new SFSArray();
                for (StoneSlotVO slotStoneVO : itemSlotVO.equip.listSlotStone) {
                    if (!slotStoneVO.status || slotStoneVO.stoneVO == null) {
                        continue;
                    }
                    stone = new SFSObject();
                    stone.putUtfString(Params.ID, slotStoneVO.stoneVO.id);
                    stone.putUtfString(Params.HASH_STONE, slotStoneVO.stoneVO.hash);
                    stone.putInt(Params.LEVEL, slotStoneVO.stoneVO.level);
                    stone.putInt(Params.TYPE, slotStoneVO.stoneVO.type);
                    stone.putShort(Params.POSITION_STONE, (short) slotStoneVO.position);
                    stone.putInt(Params.POWER, HeroManager.getInstance().getPower(HeroManager.getInstance().getStatsItem(slotStoneVO.stoneVO)));
                    stones.addSFSObject(stone);
                }
                item.putSFSArray(Params.ModuleBag.STONE, stones);
                equipment.addSFSObject(item);
            }
        }
        data.putSFSArray(Params.ModuleHero.EQUIPMENT, equipment);
        data.putShort(Params.ModuleHero.BONUS_STORY, bonusStory);
        data.putSFSArray(Params.SKILLS, SFSArray.newFromJsonData(Utils.toJson(skillModel.skills)));
    }
}
