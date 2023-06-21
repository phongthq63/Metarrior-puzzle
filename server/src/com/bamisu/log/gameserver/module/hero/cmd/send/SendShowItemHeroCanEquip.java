package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.StoneSlotVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendShowItemHeroCanEquip extends BaseMsg {

    public List<EquipDataVO> listEquip;
    public long uid;
    public Zone zone;

    public SendShowItemHeroCanEquip() {
        super(CMD.CMD_SHOW_ITEM_HERO_CAN_EQUIP);
    }

    public SendShowItemHeroCanEquip(short errorCode) {
        super(CMD.CMD_SHOW_ITEM_HERO_CAN_EQUIP, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray equip = new SFSArray();
        ISFSObject equipment;
        ISFSObject hero;
        HeroModel heroModel;
        ISFSArray stones;
        ISFSObject stone;
        for(EquipDataVO equipData : listEquip){
            equipment = new SFSObject();

            equipment.putUtfString(Params.ModuleBag.HASH, equipData.hash);
            equipment.putUtfString(Params.ID, equipData.id);
            equipment.putInt(Params.LEVEL, equipData.level);
            equipment.putInt(Params.STAR, equipData.star);
            equipment.putInt(Params.COUNT, equipData.count);
            stones = new SFSArray();
            for(StoneSlotVO slotStoneVO : equipData.listSlotStone){
                if(!slotStoneVO.status || slotStoneVO.stoneVO == null){
                    continue;
                }
                stone = new SFSObject();
                stone.putUtfString(Params.ID, slotStoneVO.stoneVO.id);
                stone.putInt(Params.LEVEL, slotStoneVO.stoneVO.level);
                stone.putShort(Params.POSITION_STONE, (short) slotStoneVO.position);
                stones.addSFSObject(stone);
            }
            equipment.putSFSArray(Params.ModuleBag.STONE, stones);

            if(equipData.hashHero != null){
                hero = new SFSObject();
                heroModel = HeroManager.getInstance().getHeroModel(uid, equipData.hashHero, zone);
                if(heroModel == null){
                    this.errorCode = ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER;
                    return;
                }

                if (equipData.hashHero.equals(heroModel.hash)){
                    hero.putUtfString(Params.ModuleHero.HASH, heroModel.hash);
                    hero.putUtfString(Params.ID, heroModel.id);
                    hero.putShort(Params.LEVEL, heroModel.readLevel());
                    hero.putShort(Params.STAR, heroModel.star);
                }
                equipment.putSFSObject(Params.HERO, hero);
            }else {
                equipment.putNull(Params.HERO);
            }

            equip.addSFSObject(equipment);
        }
        data.putSFSArray(Params.ModuleHero.EQUIPMENT, equip);
    }
}
