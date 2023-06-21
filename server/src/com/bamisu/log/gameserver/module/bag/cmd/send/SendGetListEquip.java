package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.StoneSlotVO;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetListEquip extends BaseMsg {

    public List<EquipDataVO> list;


    public SendGetListEquip() {
        super(CMD.CMD_GET_LIST_EQUIP_HERO);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        SFSArray listEquipPack = new SFSArray();
        SFSObject equipPack;
        SFSArray listStonePack;
        SFSObject stonePack;

        //Equip
        for (EquipDataVO equipDataVO: list){
            equipPack = new SFSObject();

            equipPack.putUtfString(Params.ID, equipDataVO.id);
            equipPack.putUtfString(Params.HASH, equipDataVO.hash);
            equipPack.putInt(Params.STAR, equipDataVO.star);
            equipPack.putInt(Params.LEVEL, equipDataVO.level);
            equipPack.putInt(Params.EXP_WEAPON, equipDataVO.exp);
            equipPack.putInt(Params.COUNT, equipDataVO.count);

            //Stone
            listStonePack = new SFSArray();
            for (StoneSlotVO gemVO: equipDataVO.listSlotStone){
                if (!gemVO.haveLock()) continue;

                stonePack = new SFSObject();
                stonePack.putUtfString(Params.ID, gemVO.stoneVO.id);
                stonePack.putUtfString(Params.HASH, gemVO.stoneVO.hash);
                stonePack.putInt(Params.LEVEL, gemVO.stoneVO.level);

                listStonePack.addSFSObject(stonePack);
            }
            equipPack.putSFSArray(Params.LIST_GEM, listStonePack);

            listEquipPack.addSFSObject(equipPack);
        }
        data.putSFSArray(Params.LIST, listEquipPack);
    }
}
