package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.EquipVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.item.entities.StoneDataVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendListFusionEquip extends BaseMsg {

    public List<EquipDataVO> listNewEquip;
    public List<StoneDataVO> listStoneDataNew;


    public SendListFusionEquip() {
        super(CMD.CMD_LIST_FUSION_EQUIP);
    }

    public SendListFusionEquip(short errorCode) {
        super(CMD.CMD_LIST_FUSION_EQUIP, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        //Equip new
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for(EquipDataVO equipNew : listNewEquip){
            objPack = new SFSObject();

            objPack.putUtfString(Params.HASH, equipNew.hash);
            objPack.putUtfString(Params.ID, equipNew.id);
            objPack.putInt(Params.STAR, equipNew.star);
            objPack.putInt(Params.LEVEL, equipNew.level);
            objPack.putInt(Params.EXP, equipNew.exp);
            objPack.putInt(Params.COUNT, equipNew.count);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);

        //Stone new
        arrayPack = new SFSArray();
        for(StoneDataVO stoneNew : listStoneDataNew){
            objPack = new SFSObject();

            objPack.putUtfString(Params.HASH, stoneNew.hash);
            objPack.putUtfString(Params.ID, stoneNew.id);
            objPack.putInt(Params.LEVEL, stoneNew.level);
            objPack.putInt(Params.COUNT, stoneNew.count);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.STONE, arrayPack);
    }
}
