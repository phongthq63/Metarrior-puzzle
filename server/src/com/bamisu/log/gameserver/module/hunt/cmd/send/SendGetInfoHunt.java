package com.bamisu.log.gameserver.module.hunt.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hunt.entities.HuntInfo;
import com.bamisu.log.gameserver.datamodel.hunt.entities.MonsterInfo;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetInfoHunt extends BaseMsg {

    public HuntInfo huntInfo;
    public List<ResourcePackage> listReward;
    public int fightCost;
    public Zone zone;

    public SendGetInfoHunt() {
        super(CMD.CMD_GET_INFO_HUNT);
    }

    public SendGetInfoHunt(short errorCode) {
        super(CMD.CMD_GET_INFO_HUNT, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        //Map bonus
        data.putUtfString(Params.BONUS, huntInfo.mapBonus);
        //Phan thuong
        data.putSFSArray(Params.REWARD, SFSArray.newFromJsonData(Utils.toJson(huntInfo.reward)));
        // hien thi tat ca phan thuong
        data.putSFSArray("listReward", SFSArray.newFromJsonData(Utils.toJson(listReward)));
        data.putInt("fightCost",fightCost);
        //Quai
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for(MonsterInfo monster : huntInfo.listEnemy){
            if(monster == null){
                arrayPack.addNull();
            }else {
                objPack = new SFSObject();

                objPack.putUtfString(Params.ID, monster.monster.id);
                objPack.putInt(Params.STAR, monster.monster.star);
                objPack.putInt(Params.LEVEL, monster.monster.level);
                objPack.putUtfString(Params.KINGDOM, monster.monster.kingdom);
                objPack.putUtfString(Params.ELEMENT, monster.monster.element);
                objPack.putFloat(Params.CURRENT_HP, monster.currentHp);

                arrayPack.addSFSObject(objPack);
            }
        }
        data.putSFSArray(Params.ENEMY, arrayPack);
    }
}
