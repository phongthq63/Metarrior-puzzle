package com.bamisu.log.gameserver.module.bag.cmd.send;

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

public class SendListFusionStone extends BaseMsg {

    public List<StoneDataVO> listStoneNew;

    public SendListFusionStone() {
        super(CMD.CMD_LIST_FUSION_STONE);
    }

    public SendListFusionStone(short errorCode) {
        super(CMD.CMD_LIST_FUSION_STONE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        //List stone new
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for(StoneDataVO stoneNew : listStoneNew){
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
