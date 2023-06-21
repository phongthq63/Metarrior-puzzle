package com.bamisu.log.gameserver.module.quest.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.IItemData;
import com.bamisu.gamelib.item.entities.IResourcePackage;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetRewardQuest extends BaseMsg {

    public String id;
    public byte type;
    public short point;
    public List<IResourcePackage> listReward;

    public SendGetRewardQuest() {
        super(CMD.CMD_GET_REWARD_QUEST);
    }

    public SendGetRewardQuest(short errorCode) {
        super(CMD.CMD_GET_REWARD_QUEST, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.ID, id);
        data.putByte(Params.TYPE, type);
        data.putShort(Params.POINT, point);

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        IItemData iItemData;
        for(IResourcePackage res : listReward){
            objPack = new SFSObject();

            objPack.putUtfString(Params.ID, res.readId());
            objPack.putInt(Params.COUNT, res.readAmount());

            if(res instanceof IItemData){
                iItemData = (IItemData) res;
                objPack.putUtfString(Params.HASH, iItemData.readHash());
                objPack.putInt(Params.STAR, iItemData.readStar());
                objPack.putInt(Params.LEVEL, iItemData.readLevel());
            }

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.REWARD, arrayPack);
    }
}
