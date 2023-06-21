package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.log.gameserver.module.guild.entities.GuildSearchInfo;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendListGuildInfo extends BaseMsg {

    public List<GuildSearchInfo> listModel;
    public UserManager userManager;

    public SendListGuildInfo() {
        super(CMD.CMD_GET_LIST_GUILD_INFO);
    }

    public SendListGuildInfo(short errorCode) {
        super(CMD.CMD_GET_LIST_GUILD_INFO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray pack = new SFSArray();
        ISFSObject index;
        for(GuildSearchInfo model : listModel){
            index = new SFSObject();
            index.putLong(Params.ID, model.gId);
            index.putUtfString(Params.AVATAR_ID, model.gAvatar);
            index.putUtfString(Params.NAME, model.gName);
            index.putShort(Params.ModuleChracter.LEVEL, model.level);
            index.putShort(Params.LEVEL_GIFT, model.levelGift);
            index.putUtfString(Params.GUILD_MASTER, userManager.getUserModel(model.uidMaster).displayName);
            index.putLong(Params.POWER, model.power);
            index.putUtfString(Params.LANGUAGE, model.language);
            index.putUtfString(Params.VERIFICATION, model.verify);
            index.putShort(Params.ModuleGuild.MEMBER, model.member);
            index.putShort(Params.ModuleGuild.MAX, model.maxMember);

            pack.addSFSObject(index);
        }
        data.putSFSArray(Params.LIST, pack);
    }
}
