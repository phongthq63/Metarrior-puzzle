package com.bamisu.log.gameserver.module.WoL.cmd.send;

import com.bamisu.log.gameserver.module.WoL.entities.WoLPlayerInArea;
import com.bamisu.log.gameserver.module.WoL.entities.WoLPlayerInfoVO;
import com.bamisu.log.gameserver.module.WoL.entities.WoLRankVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendWoLGetRank extends BaseMsg {
    public List<WoLPlayerInArea> listPlayer;
    public SendWoLGetRank() {
        super(CMD.CMD_WOL_GET_RANK);
    }

    public SendWoLGetRank(short errorCode) {
        super(CMD.CMD_WOL_GET_RANK, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray sfsArray = new SFSArray();
        for (WoLPlayerInArea woLPlayerInArea: listPlayer){
            SFSObject areaObject = new SFSObject();
            areaObject.putInt(Params.AREA, woLPlayerInArea.area);
            SFSArray stageArray = new SFSArray();
            for (WoLPlayerInfoVO woLPlayerInfoVO: woLPlayerInArea.list){
                SFSObject stageObject = new SFSObject();
                stageObject.putLong(Params.UID, woLPlayerInfoVO.uid);
                stageObject.putInt(Params.AVATAR_FRAME, woLPlayerInfoVO.avatarFrame);
                stageObject.putUtfString(Params.USER_AVATAR, woLPlayerInfoVO.avatar);
                stageObject.putLong(Params.LEVEL, woLPlayerInfoVO.level);
                stageObject.putUtfString(Params.NAME, woLPlayerInfoVO.name);
                stageObject.putInt(Params.STAGE, woLPlayerInfoVO.stage);
                stageArray.addSFSObject(stageObject);
            }
            areaObject.putSFSArray(Params.LIST_PLAYER, stageArray);
            sfsArray.addSFSObject(areaObject);
        }
        data.putSFSArray(Params.LIST, sfsArray);
    }
}
