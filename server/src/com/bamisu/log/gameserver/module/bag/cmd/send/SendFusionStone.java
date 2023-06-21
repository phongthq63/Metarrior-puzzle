package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.item.entities.StoneDataVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.stream.Collectors;

public class SendFusionStone extends BaseMsg {

    public StoneDataVO newStone;
    public List<StoneDataVO> listStoneRemove;


    public SendFusionStone() {
        super(CMD.CMD_FUSION_STONE);
    }

    public SendFusionStone(short errorCode) {
        super(CMD.CMD_FUSION_STONE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        //Stone new
        ISFSObject objPack = new SFSObject();
//        objPack.putUtfString(Params.ID, newStone.id);
//        objPack.putUtfString(Params.HASH, newStone.hash);
//        objPack.putInt(Params.LEVEL, newStone.level);
//        objPack.putInt(Params.COUNT, newStone.count);
//        data.putSFSObject(Params.STONE, objPack);

        data.putUtfString(Params.STONE, newStone.hash);
    }
}
