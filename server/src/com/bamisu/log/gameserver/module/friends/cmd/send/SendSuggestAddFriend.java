package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.friends.entities.BlockedFriendVO;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendSuggestAddFriend extends BaseMsg {
    public List<BlockedFriendVO> list;
    public SendSuggestAddFriend() {
        super(CMD.CMD_SUGGEST_ADD_FRIEND);
    }

    public SendSuggestAddFriend(short errorCode) {
        super(CMD.CMD_SUGGEST_ADD_FRIEND, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray array = new SFSArray();
        for (BlockedFriendVO vo: list){
            SFSObject sfsObject = new SFSObject();
            sfsObject.putUtfString(Params.USER_AVATAR, vo.avatar);
            sfsObject.putUtfString(Params.NAME, vo.name);
            sfsObject.putInt(Params.LEVEL, vo.level);
            sfsObject.putInt(Params.POWER, vo.power);
            sfsObject.putLong(Params.UID, vo.uid);
            sfsObject.putInt(Params.FRAME, vo.avatarFrame);
            array.addSFSObject(sfsObject);
        }
        data.putSFSArray(Params.LIST, array);
    }
}
