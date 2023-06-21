package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SendResetHero extends BaseMsg {

    public List<EquipDataVO> listEquipRemove = new ArrayList<>();


    public SendResetHero() {
        super(CMD.CMD_RESET_HERO);
    }

    public SendResetHero(short errorCode) {
        super(CMD.CMD_RESET_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for(EquipDataVO equip : listEquipRemove){
            objPack = new SFSObject();

            objPack.putUtfString(Params.HASH, equip.hash);
            objPack.putUtfString(Params.ID, equip.id);
            objPack.putInt(Params.COUNT, equip.count);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.REMOVE, arrayPack);
    }
}
