package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.item.entities.AttributeVO;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.EquipVO;
import com.bamisu.gamelib.item.entities.StoneSlotVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class SendShowInfoWeapon extends BaseMsg {

    public EquipDataVO equipDataVO;


    public SendShowInfoWeapon() {
        super(CMD.CMD_SHOW_INFO_WEAPON);
    }

    public SendShowInfoWeapon(short errorCode) {
        super(CMD.CMD_SHOW_INFO_WEAPON, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        ISFSArray arrayPack = new SFSArray();
        SFSObject objPack;
        for (StoneSlotVO slotStone: equipDataVO.listSlotStone){
            if(!slotStone.haveLock() || slotStone.stoneVO == null) continue;

            objPack = new SFSObject();
            objPack.putInt(Params.POSITION, slotStone.position);
            objPack.putUtfString(Params.HASH, slotStone.stoneVO.hash);
            objPack.putUtfString(Params.ID, slotStone.stoneVO.id);
            objPack.putInt(Params.LEVEL, slotStone.stoneVO.level);
            objPack.putInt(Params.TYPE, slotStone.stoneVO.type);
            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);
    }
}
