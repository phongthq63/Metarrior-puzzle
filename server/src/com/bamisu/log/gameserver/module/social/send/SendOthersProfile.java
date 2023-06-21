package com.bamisu.log.gameserver.module.social.send;

import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.campaign.CampaignManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

/**
 * Create by Popeye on 2:18 PM, 5/8/2020
 */
public class SendOthersProfile extends BaseMsg {
    public UserModel userModel = null;
    public List<HeroModel> primaryHeroes = null;
    public String alliance = "";
    public Zone zone;
    public boolean isFriend;
    public GuildModel guild;

    public SendOthersProfile() {
        super(CMD.CMD_GET_OTHERS_PROFILE);
    }

    public SendOthersProfile(short errorCode) {
        super(CMD.CMD_GET_OTHERS_PROFILE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putUtfString(Params.ID, String.valueOf(userModel.userID));
        data.putUtfString(Params.USER_DISPLAY_NAME, userModel.displayName);
        data.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(userModel.userID, zone));
        data.putInt(Params.POWER, HeroManager.getInstance().getPower(userModel.userID, zone));
        data.putUtfString(Params.USER_STATUS_TEXT, userModel.statusText);
        data.putUtfString(Params.AVATAR_ID, userModel.avatar);
        data.putInt(Params.AVATAR_FRAME, userModel.avatarFrame);
        data.putIntArray(Params.AVATAR_FRAME_LIST, userModel.avatarFrameList);
        data.putUtfString(Params.ALLIANCE, alliance);
        data.putShort(Params.GENDER, userModel.gender);
        data.putUtfString(Params.LANGUAGE, userModel.lang);
        data.putInt(Params.HONOR, VipManager.getInstance().getVipHonor(userModel.userID, zone));
        if(guild != null){
            data.putUtfString(Params.GUILD_AVATAR, guild.readAvatar());
        }else {
            data.putUtfString(Params.GUILD_AVATAR, "");
        }
        data.putUtfString(Params.TIME_ZONE, ServerConstant.TIME_ZONE);
        data.putInt(Params.TIMESTAMP_SERVER, Utils.getTimestampInSecond());
        data.putInt(Params.SERVER_ID, userModel.serverId);

        ISFSArray listHero = new SFSArray();
        ISFSObject heroSFS;
        for(HeroModel heroModel : primaryHeroes){
            heroSFS = new SFSObject();
            heroSFS.putUtfString(Params.ModuleHero.HASH, heroModel.hash);
            heroSFS.putUtfString(Params.ID, heroModel.id);
            heroSFS.putShort(Params.LEVEL, heroModel.readLevel());
            heroSFS.putShort(Params.STAR, heroModel.star);
            listHero.addSFSObject(heroSFS);
        }

        data.putBool(Params.FRIEND, isFriend);
        data.putSFSArray(Params.PRIMARY_HEROES, listHero);

        data.putUtfString(Params.STATION, CampaignManager.getInstance().getUserCampaignDetailModel(zone, userModel.userID).userMainCampaignDetail.readNextStation());
    }
}
