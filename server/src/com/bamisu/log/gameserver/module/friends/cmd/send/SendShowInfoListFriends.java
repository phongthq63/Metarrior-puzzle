package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.log.gameserver.module.friends.entities.FriendInfoVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendShowInfoListFriends extends BaseMsg {
    public List<FriendInfoVO> list;
    public ResourcePackage resourcePackage;
    public SendShowInfoListFriends() {
        super(CMD.CMD_SHOW_INFO_LIST_FRIENDS);
    }

    public SendShowInfoListFriends(short errorCode) {
        super(CMD.CMD_SHOW_INFO_LIST_FRIENDS, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray array = new SFSArray();
        for (FriendInfoVO vo: list){
            SFSObject sfsObject = new SFSObject();
            sfsObject.putUtfString(Params.USER_AVATAR, vo.avatar);
            sfsObject.putUtfString(Params.CAMPAIGN, vo.campaign);
            sfsObject.putUtfString(Params.NAME, vo.name);
            sfsObject.putInt(Params.IS_ACTIVE, vo.active);
            sfsObject.putInt(Params.LEVEL, vo.level);
            sfsObject.putInt(Params.POWER, vo.power);
            sfsObject.putInt(Params.SERVER_ID, vo.server);
            sfsObject.putBool(Params.RECEIVE, vo.receive);
            sfsObject.putBool(Params.SEND, vo.send);
            sfsObject.putLong(Params.UID, vo.uid);
            sfsObject.putInt(Params.FRAME, vo.avatarFrame);
            sfsObject.putInt(Params.GENDER, vo.gender);
            array.addSFSObject(sfsObject);
        }
        data.putSFSArray(Params.LIST, array);
        data.putSFSObject(Params.MONEY, resourcePackage.toSFSObject());
    }
}
