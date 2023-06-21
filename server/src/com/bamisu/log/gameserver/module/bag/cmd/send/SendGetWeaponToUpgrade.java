package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.item.entities.*;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

import java.util.List;

public class SendGetWeaponToUpgrade extends BaseMsg {

    public List<EquipDataVO> listEquip = null;
    public List<ResourcePackage> listHammer = null;
    public EquipVO equip = null;


    public SendGetWeaponToUpgrade() {
        super(CMD.CMD_GET_WEAPON_TO_UPGRADE);
    }

    public SendGetWeaponToUpgrade(short errorCode) {
        super(CMD.CMD_GET_WEAPON_TO_UPGRADE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray hammerArray = new SFSArray();
        for (ResourcePackage resourcePackage: listHammer){
            if(resourcePackage.amount <= 0) continue;

            SFSObject bag = new SFSObject();
            bag.putUtfString(Params.ID, resourcePackage.id);
            bag.putInt(Params.AMOUNT, resourcePackage.amount);
            hammerArray.addSFSObject(bag);
        }
        SFSArray arrayInBag = new SFSArray();
        for (EquipDataVO equipVO: listEquip){
            SFSObject bag = new SFSObject();
            bag.putUtfString(Params.ID_WEAPON, equipVO.id);
            bag.putUtfString(Params.HASH_WEAPON, equipVO.hash);
            bag.putInt(Params.LEVEL_WEAPON, equipVO.level);
            bag.putInt(Params.FIS_WEAPON, equipVO.expFis);
            bag.putInt(Params.COUNT_WEAPON, equipVO.count);
            bag.putInt(Params.STAR_WEAPON, equipVO.star);
            SFSArray bagStone = new SFSArray();
            for (StoneSlotVO vo: equipVO.listSlotStone){
                if (vo.stoneVO != null && vo.stoneVO.id != null){
                    SFSObject bagStoneObj = new SFSObject();
                    bagStoneObj.putInt(Params.POSITION_STONE, vo.position);
                    bagStoneObj.putUtfString(Params.ID_STONE, vo.stoneVO.id);
                    bagStone.addSFSObject(bagStoneObj);
                }
            }
            bag.putSFSArray(Params.LIST_STONE, bagStone);
            arrayInBag.addSFSObject(bag);
        }
        data.putSFSArray(Params.LIST_MY_WEAPON, arrayInBag);
        data.putInt(Params.EXP_WEAPON_UP, equip.exp);
        data.putSFSArray(Params.LIST_HAMMER, hammerArray);
//        data.putInt(Params.LEVEL_WEAPON_UP, equip.level);

    }
}
