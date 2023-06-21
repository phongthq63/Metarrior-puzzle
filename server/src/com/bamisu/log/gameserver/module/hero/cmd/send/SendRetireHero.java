package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendRetireHero extends BaseMsg {

    public List<EquipDataVO> listEquipment;
    public List<ResourcePackage> listResource;

    public SendRetireHero() {
        super(CMD.CMD_RETIRE_HERO);
    }

    public SendRetireHero(short errorCode) {
        super(CMD.CMD_RETIRE_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        //Tai nguyen sau khi phan giai
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;

        //List do hero dang mac
        for(EquipDataVO equip : listEquipment){
            objPack = new SFSObject();
            objPack.putUtfString(Params.ID, equip.id);
            objPack.putUtfString(Params.HASH, equip.hash);
            objPack.putInt(Params.LEVEL, equip.level);
            objPack.putInt(Params.STAR, equip.star);
            objPack.putInt(Params.COUNT, equip.count);

            arrayPack.addSFSObject(objPack);
        }

        //List tai nguyen len cap hero
        for(ResourcePackage res : listResource){
            objPack = new SFSObject();
            objPack.putUtfString(Params.ID, res.id);
            objPack.putLong(Params.AMOUNT, res.amount);

            arrayPack.addSFSObject(objPack);
        }

        data.putSFSArray(Params.RESOURCE, arrayPack);
    }
}
