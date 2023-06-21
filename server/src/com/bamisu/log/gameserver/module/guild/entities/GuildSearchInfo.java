package com.bamisu.log.gameserver.module.guild.entities;

import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.module.guild.GuildManager;

public class GuildSearchInfo {
    public long gId;
    public String gAvatar;
    public String gName;
    public short level;
    public short levelGift;
    public long uidMaster;
    public long power;
    public String language;
    public String verify;
    public short member;
    public short maxMember;

    public static GuildSearchInfo createGuildSearchInfo(GuildModel guildModel){
        GuildSearchInfo guildSearchInfo = new GuildSearchInfo();
        guildSearchInfo.gId = guildModel.gId;
        guildSearchInfo.gAvatar = guildModel.readAvatar();
        guildSearchInfo.gName = guildModel.gName;
        guildSearchInfo.level = guildModel.readLevel();
        guildSearchInfo.levelGift = guildModel.readLevelGift();
        guildSearchInfo.uidMaster = guildModel.guildMaster;
        guildSearchInfo.power = guildModel.readRequestPower();
        guildSearchInfo.language = guildModel.readLanguage();
        guildSearchInfo.verify = guildModel.readVerification();
        guildSearchInfo.member = (short) guildModel.member.size();
        guildSearchInfo.maxMember = GuildManager.getInstance().getGuildConfig(guildModel.readLevel()).member;

        return guildSearchInfo;
    }
}
