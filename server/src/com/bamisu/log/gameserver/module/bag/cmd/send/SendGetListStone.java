package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.item.entities.*;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetListStone extends BaseMsg {

    public long uid;
    public Zone zone;
    public List<StoneDataVO> listStone;

    public SendGetListStone() {
        super(CMD.CMD_GET_LIST_STONE);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        SFSArray array = new SFSArray();
        SFSObject stonePack;
        SFSObject equipPack;
        HeroModel heroModel;
        SFSObject heroPack;
        //Stone khong tren do trong tui
        for (StoneDataVO stone: listStone){
            stonePack = new SFSObject();
            stonePack.putUtfString(Params.ID_STONE, stone.id);
            stonePack.putUtfString(Params.HASH_STONE, stone.hash);
            stonePack.putInt(Params.LEVEL_STONE, stone.level);
            stonePack.putInt(Params.COUNT_STONE, stone.count);

            //Thong tin do
            stonePack.putNull(Params.EQUIPMENT);

            //Thong tin Hero
            stonePack.putNull(Params.HERO);

            array.addSFSObject(stonePack);
        }

        //Stone tren do trong tui
//        for(EquipDataVO equip : listEquip){
//            for(StoneSlotVO slot : equip.listSlotStone){
//                if(!slot.haveLock() || slot.stoneVO == null){
//                    continue;
//                }
//
//                stonePack = new SFSObject();
//                stonePack.putUtfString(Params.ID_STONE, slot.stoneVO.id);
//                stonePack.putUtfString(Params.HASH_STONE, slot.stoneVO.hash);
//                stonePack.putInt(Params.LEVEL_STONE, slot.stoneVO.level);
//                stonePack.putInt(Params.COUNT_STONE, slot.stoneVO.count);
//                stonePack.putInt(Params.POWER, HeroManager.getInstance().getPower(HeroManager.getInstance().getStatsItem(slot.stoneVO)));
//
//                //Thong tin do
//                equipPack = new SFSObject();
//                equipPack.putUtfString(Params.ID, equip.id);
//                equipPack.putUtfString(Params.HASH_WEAPON, equip.hash);
//                equipPack.putInt(Params.STAR, equip.star);
//                equipPack.putInt(Params.LEVEL, equip.level);
//                stonePack.putSFSObject(Params.HERO, equipPack);
//
//                //Thong tin Hero
//                stonePack.putNull(Params.HERO);
//
//                array.addSFSObject(stonePack);
//            }
//        }

        //Stone tren do trong nguoi Hero
//        for(EquipDataVO equip : listEquipHero){
//            for(StoneSlotVO slot : equip.listSlotStone){
//                if(!slot.haveLock() || slot.stoneVO == null){
//                    continue;
//                }
//
//                stonePack = new SFSObject();
//                stonePack.putUtfString(Params.ID_STONE, slot.stoneVO.id);
//                stonePack.putUtfString(Params.HASH_STONE, slot.stoneVO.hash);
//                stonePack.putInt(Params.LEVEL_STONE, slot.stoneVO.level);
//                stonePack.putInt(Params.COUNT_STONE, slot.stoneVO.count);
//                stonePack.putInt(Params.POWER, HeroManager.getInstance().getPower(HeroManager.getInstance().getStatsItem(slot.stoneVO)));
//
//                //Thong tin do
//                equipPack = new SFSObject();
//                equipPack.putUtfString(Params.ID, equip.id);
//                equipPack.putUtfString(Params.HASH_WEAPON, equip.hash);
//                equipPack.putInt(Params.STAR, equip.star);
//                equipPack.putInt(Params.LEVEL, equip.level);
//                stonePack.putSFSObject(Params.EQUIPMENT, equipPack);
//
//                //Thong tin Hero
//                heroModel = HeroManager.getInstance().getHeroModel(uid, equip.hashHero, zone);
//                heroPack = new SFSObject();
//                heroPack.putUtfString(Params.ID, heroModel.id);
//                heroPack.putUtfString(Params.HASH_HERO, heroModel.hash);
//                heroPack.putInt(Params.STAR, heroModel.star);
//                heroPack.putInt(Params.LEVEL, heroModel.level);
//                stonePack.putSFSObject(Params.HERO, heroPack);
//
//                array.addSFSObject(stonePack);
//            }
//        }

        data.putSFSArray(Params.LIST_STONE, array);
    }
}
