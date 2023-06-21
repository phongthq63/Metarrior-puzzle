package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.friends.entities.FriendInfoFullVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendShowInfoDetailFriend extends BaseMsg {
    public FriendInfoFullVO vo;
    public SendShowInfoDetailFriend() {
        super(CMD.CMD_SHOW_INFO_DETAIL_FRIEND);
    }

    public SendShowInfoDetailFriend(short errorCode) {
        super(CMD.CMD_SHOW_INFO_DETAIL_FRIEND, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        data.putUtfString(Params.USER_AVATAR, vo.avatar);
        data.putUtfString(Params.CAMPAIGN, vo.campaign);
        data.putUtfString(Params.NAME, vo.name);
        data.putInt(Params.LEVEL, vo.level);
        data.putInt(Params.POWER, vo.power);
        data.putInt(Params.SERVER_ID, vo.server);
        data.putLong(Params.UID, vo.uid);
        data.putInt(Params.FRAME, vo.avatarFrame);
        data.putInt(Params.GENDER, vo.gender);
        data.putUtfString(Params.STATUS, vo.statusText);
        data.putBool(Params.STATUS_BLOCK, vo.statusBlock);
        if (vo.gAvatar != null && vo.gName != null){
            data.putUtfString(Params.GUILD_NAME, vo.gName);
            data.putUtfString(Params.GUILD_AVATAR, vo.gAvatar);
        }else{
            data.putUtfString(Params.GUILD_NAME,"");
            data.putUtfString(Params.GUILD_AVATAR,"");
        }
        SFSArray sfsArray = new SFSArray();
        for (HeroModel heroModel: vo.listHero){
            SFSObject sfsObject = new SFSObject();
            sfsObject.putUtfString(Params.ID, heroModel.id);
            sfsObject.putInt(Params.STAR, heroModel.star);
            sfsObject.putInt(Params.LEVEL, heroModel.readLevel());
            sfsArray.addSFSObject(sfsObject);
        }
        data.putSFSArray(Params.LIST, sfsArray);
    }
}
