package com.bamisu.log.gameserver.module.WoL.cmd.send;

import com.bamisu.log.gameserver.module.WoL.entities.WoLAchievementVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.Map;

public class SendWoLUserAchievement extends BaseMsg {
    public int area;
    public int stage;
    public List<WoLAchievementVO> list;
    public SendWoLUserAchievement() {
        super(CMD.CMD_WOL_USER_ACHIEVEMENT);
    }

    public SendWoLUserAchievement(short errorCode) {
        super(CMD.CMD_WOL_USER_ACHIEVEMENT, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray sfsArray = new SFSArray();
        for (WoLAchievementVO vo: list){
            SFSObject sfsObject = new SFSObject();
            sfsObject.putInt(Params.STATUS, vo.status);
            sfsObject.putLong(Params.UID, vo.uid);
            sfsObject.putInt(Params.AVATAR_FRAME, vo.avatarFrame);
            sfsObject.putInt(Params.LEVEL, vo.level);
            sfsObject.putUtfString(Params.USER_AVATAR, vo.avatar);
            sfsObject.putUtfString(Params.NAME, vo.name);
            sfsObject.putInt(Params.REWARD, vo.reward);
            sfsArray.addSFSObject(sfsObject);
        }
        data.putInt(Params.AREA, area);
        data.putInt(Params.STAGE, stage);
        data.putSFSArray(Params.LIST, sfsArray);
    }
}
