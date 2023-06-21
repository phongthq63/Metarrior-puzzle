package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.log.gameserver.module.friends.entities.BlockedFriendVO;
import com.bamisu.log.gameserver.module.friends.entities.FriendInfoVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendListFriendsBlocked extends BaseMsg {
    public List<BlockedFriendVO> list;
    public SendListFriendsBlocked() {
        super(CMD.CMD_LIST_FRIENDS_BLOCKED);
    }

    public SendListFriendsBlocked(short errorCode) {
        super(CMD.CMD_LIST_FRIENDS_BLOCKED, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray array = new SFSArray();
        if (list != null){
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
}
