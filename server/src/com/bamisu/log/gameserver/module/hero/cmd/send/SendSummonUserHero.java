package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.HeroBaseStatsModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.summon.entities.HeroSummonVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.stream.Collectors;

public class SendSummonUserHero extends BaseMsg {

    public int bonusPoint;
    public List<HeroModel> listSummonedModel;
    public Zone zone;

    //retire
    public List<EquipDataVO> listEquipmentRetire;
    public List<ResourcePackage> listResourceRetire;

    public SendSummonUserHero() {
        super(CMD.CMD_SUMMON_USER_HERO);
    }

    public SendSummonUserHero(short errorCode) {
        super(CMD.CMD_SUMMON_USER_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        CharactersConfigManager charactersConfigManager = CharactersConfigManager.getInstance();

        data.putInt(Params.ModuleChracter.POINT, bonusPoint);

        ISFSArray list = new SFSArray();
        ISFSObject heroObj;
        for(HeroModel hero : listSummonedModel){
            heroObj = new SFSObject();
            heroObj.putUtfString(Params.HASH, hero.hash);
            heroObj.putUtfString(Params.ModuleChracter.ID, hero.id);
            heroObj.putShort(Params.ModuleHero.STAR, hero.star);

            switch (EHeroType.fromId(hero.type)) {
                case NORMAL:
                    heroObj.putSFSObject(Params.STATS, SFSObject.newFromJsonData(Utils.toJson(charactersConfigManager.getHeroConfig(hero.id))));
                    heroObj.putSFSObject(Params.GROW, SFSObject.newFromJsonData(Utils.toJson(charactersConfigManager.getHeroStatsGrowConfig(hero.id))));
                    break;
                case NFT:
                    HeroBaseStatsModel heroBaseStatsModel = HeroBaseStatsModel.copyFromDBtoObject(hero.hash, hero.id, zone);
                    heroObj.putSFSObject(Params.STATS, SFSObject.newFromJsonData(Utils.toJson(heroBaseStatsModel.baseStats)));
                    heroObj.putSFSObject(Params.GROW, SFSObject.newFromJsonData(Utils.toJson(heroBaseStatsModel.growStats)));
                    break;
            }

            list.addSFSObject(heroObj);
        }
        data.putSFSArray(Params.Module.MODULE_HERO, list);

        //retire
        //Tai nguyen sau khi phan giai
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;

        if(listEquipmentRetire != null) {
            //List do hero dang mac
            for (EquipDataVO equip : listEquipmentRetire) {
                objPack = new SFSObject();
                objPack.putUtfString(Params.ID, equip.id);
                objPack.putUtfString(Params.HASH_WEAPON, equip.hash);
                objPack.putInt(Params.LEVEL, equip.level);
                objPack.putInt(Params.STAR, equip.star);

                arrayPack.addSFSObject(objPack);
            }
        }

        if(listResourceRetire != null) {
            //List tai nguyen len cap hero
            for (ResourcePackage res : listResourceRetire) {
                objPack = new SFSObject();
                objPack.putUtfString(Params.ID, res.id);
                objPack.putLong(Params.AMOUNT, res.amount);

                arrayPack.addSFSObject(objPack);
            }
        }

        data.putSFSArray(Params.RESOURCE, arrayPack);
    }
}
