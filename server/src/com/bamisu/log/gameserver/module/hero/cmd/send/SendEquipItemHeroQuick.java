package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.StoneSlotVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.stream.Collectors;

public class SendEquipItemHeroQuick extends BaseMsg {

    public String hashHero;
    public List<EquipDataVO> listEquip;


    public SendEquipItemHeroQuick() {
        super(CMD.CMD_EQUIP_ITEM_HERO_QUICK);
    }

    public SendEquipItemHeroQuick(short errorCode) {
        super(CMD.CMD_EQUIP_ITEM_HERO_QUICK, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        //Hero
        data.putUtfString(Params.HASH_HERO, hashHero);
        //Equip
        ISFSArray listEquipPack = new SFSArray();
        ISFSObject equipPack;
        for(EquipDataVO equipData : listEquip){
            equipPack = new SFSObject();

            equipPack.putUtfString(Params.HASH, equipData.hash);
            equipPack.putUtfString(Params.ID, equipData.id);

            listEquipPack.addSFSObject(equipPack);
        }
        data.putSFSArray(Params.EQUIPMENT, listEquipPack);
    }
}
