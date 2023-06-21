package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.guild.UserGuildModel;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.guild.config.entities.GuildVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class SendGetGuildInfo extends BaseMsg {

    public GuildModel guildModel;
    public UserManager userManager;
    public Zone zone;

    public SendGetGuildInfo() {
        super(CMD.CMD_GET_GUILD_INFO);
    }

    public SendGetGuildInfo(short errorCode) {
        super(CMD.CMD_GET_GUILD_INFO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putLong(Params.ModuleGuild.ID, guildModel.gId);
        data.putUtfString(Params.AVATAR_ID, guildModel.readAvatar());
        data.putUtfString(Params.ID, guildModel.id);
        data.putUtfString(Params.NAME, guildModel.gName);
        data.putShort(Params.ModuleChracter.LEVEL, guildModel.readLevel());
        data.putLong(Params.ModuleChracter.EXP, guildModel.readExp());
        data.putShort(Params.LEVEL_GIFT, guildModel.readLevelGift());
        data.putLong(Params.EXP_GIFT, guildModel.readExpGift());

        data.putUtfString(Params.ModuleGuild.NOTICE, guildModel.readNotice());
        data.putLong(Params.ModuleGuild.REQUEST_POWER, guildModel.readRequestPower());
        data.putUtfString(Params.LANGUAGE, guildModel.readLanguage());
        data.putUtfString(Params.VERIFICATION, guildModel.readVerification());

        data.putLong(Params.ModuleGuild.MASTER, guildModel.guildMaster);
        data.putLongArray(Params.ModuleGuild.VICE, guildModel.guildVice);
        data.putLongArray(Params.ModuleGuild.LEAD, guildModel.guildLeader);

        GuildVO guildCf = GuildManager.getInstance().getGuildConfig(guildModel.readLevel());
        data.putShort(Params.ModuleGuild.MAX, guildCf.member);

        ISFSArray listMember = new SFSArray();
        ISFSObject member;
        UserModel userModel;
        UserGuildModel userGuildModel;
        int timeNow = Utils.getTimestampInSecond();

        for(long uid : guildModel.member){
            member = new SFSObject();
            //Lay user model
            userModel = userManager.getUserModel(uid);
            //Lay user guild model
            userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, zone);
            //Dong goi thong tin user
            member.putLong(Params.ID, userGuildModel.uid);
            member.putLong(Params.POWER, HeroManager.getInstance().getPower(uid, zone));
            member.putUtfString(Params.AVATAR_ID, userModel.avatar);
            member.putInt(Params.FRAME, userModel.avatarFrame);
            member.putUtfString(Params.NAME, userModel.displayName);
            member.putShort(Params.USER_SEX, userModel.gender);
            member.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(uid, zone));
            member.putInt(Params.SERVER_ID, userModel.serverId);
            //Neu nguoi choi dang afk thi gui time logout
            if(ExtensionUtility.getInstance().getUserById(userModel.userID) == null ){
                if(userModel.lastLogout <= 0){
                    member.putInt(Params.TIME, (int) (timeNow - userModel.lastLogin));
                }else {
                    member.putInt(Params.TIME, timeNow - userModel.lastLogout);
                }
            }else {
                member.putInt(Params.TIME, -1);
            }

            listMember.addSFSObject(member);
        }
        data.putSFSArray(Params.ModuleGuild.MEMBER, listMember);
    }
}
