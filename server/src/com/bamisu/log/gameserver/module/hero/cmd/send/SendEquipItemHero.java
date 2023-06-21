package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.StoneSlotVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class SendEquipItemHero extends BaseMsg {

    public EquipDataVO equipData;


    public SendEquipItemHero() {
        super(CMD.CMD_EQUIP_ITEM_HERO);
    }

    public SendEquipItemHero(short errorCode) {
        super(CMD.CMD_EQUIP_ITEM_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.HASH_HERO, equipData.hashHero);
        data.putUtfString(Params.HASH_WEAPON, equipData.hash);

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for(StoneSlotVO slot : equipData.listSlotStone){
            if(!slot.haveLock() && slot.stoneVO == null) continue;

            objPack = new SFSObject();

            objPack.putInt(Params.POSITION, slot.position);
            objPack.putUtfString(Params.HASH_STONE, slot.stoneVO.hash);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.ModuleBag.STONE, arrayPack);
    }
}
