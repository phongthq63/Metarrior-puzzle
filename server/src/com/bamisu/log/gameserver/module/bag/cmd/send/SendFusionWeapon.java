package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.item.entities.StoneDataVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.stream.Collectors;

public class SendFusionWeapon extends BaseMsg {

    public EquipDataVO newEquip;
    public List<StoneDataVO> listStoneDataNew;


    public SendFusionWeapon() {
        super(CMD.CMD_FUSION_WEAPON);
    }

    public SendFusionWeapon(short errorCode) {
        super(CMD.CMD_FUSION_WEAPON, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        //Equip new
        ISFSObject equipPack = new SFSObject();
        equipPack.putUtfString(Params.HASH, newEquip.hash);
        equipPack.putUtfString(Params.ID, newEquip.id);
        equipPack.putInt(Params.STAR, newEquip.star);
        equipPack.putInt(Params.LEVEL, newEquip.level);
        equipPack.putInt(Params.EXP, newEquip.exp);
        equipPack.putInt(Params.COUNT, newEquip.count);
        data.putSFSObject(Params.EQUIPMENT, equipPack);

        //Stone new
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for(StoneDataVO stoneNew : listStoneDataNew){
            objPack = new SFSObject();

            objPack.putUtfString(Params.HASH, stoneNew.hash);
            objPack.putUtfString(Params.ID, stoneNew.hash);
            objPack.putUtfString(Params.LEVEL, stoneNew.hash);
            objPack.putInt(Params.COUNT, stoneNew.count);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.STONE, arrayPack);
    }
}
