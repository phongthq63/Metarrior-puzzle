package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.manager.UserManager;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetRequestJoinGuild extends BaseMsg {

    public List<Long> listUid;
    public UserManager userManager;
    public Zone zone;

    public SendGetRequestJoinGuild() {
        super(CMD.CMD_GET_REQUEST_JOIN_GUILD);
    }

    public SendGetRequestJoinGuild(short errorCode) {
        super(CMD.CMD_GET_REQUEST_JOIN_GUILD, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray pack = new SFSArray();
        ISFSObject member;
        UserModel userModel;
        for(long uid : listUid){
            member = new SFSObject();
            userModel = userManager.getUserModel(uid);

            member.putLong(Params.UID, uid);
            member.putUtfString(Params.NAME, userModel.displayName);
            member.putUtfString(Params.AVATAR_ID, userModel.avatar);
            member.putInt(Params.FRAME, userModel.avatarFrame);
            member.putShort(Params.USER_SEX, userModel.gender);
            member.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(userModel.userID, zone));
            member.putLong(Params.POWER, HeroManager.getInstance().getPower(uid, zone));

            pack.addSFSObject(member);
        }
        data.putSFSArray(Params.LIST, pack);
    }
}
