package com.bamisu.log.gameserver.module.campaign.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.gamelib.sql.game.dbo.RankLeagueDBO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.league.RankCampaignModel;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Created by Quach Thanh Phong
 * On 5/28/2022 - 1:40 AM
 */
public class SendGetCampaignRank extends BaseMsg {

    public UserManager userManager;
    public RankCampaignModel rankCampaignModel;
    public RankLeagueDBO rankLeagueDBO;

    public SendGetCampaignRank() {
        super(CMD.CMD_GET_CAMPAIGN_RANK);
    }

    @Override
    public void packData() {
        super.packData();

        ISFSArray isfsArray = new SFSArray();
        ISFSObject isfsObject;
        UserModel userModel;
        for (int i = 0; i < rankCampaignModel.rank.size(); i++) {
            isfsObject = new SFSObject();
            userModel = userManager.getUserModel(rankCampaignModel.rank.get(i).uid);

            isfsObject.putLong(Params.UID, userModel.userID);
            isfsObject.putUtfString(Params.AVATAR_ID, userModel.avatar);
            isfsObject.putInt(Params.FRAME, userModel.avatarFrame);
            isfsObject.putUtfString(Params.NAME, userModel.displayName);
            isfsObject.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(userModel.userID, userManager.getZone()));
            isfsObject.putInt(Params.POWER, HeroManager.getInstance().getPower(userModel.userID, userManager.getZone()));
            isfsObject.putInt(Params.RANK, i + 1);
            isfsObject.putInt(Params.POINT, rankCampaignModel.rank.get(i).score);

            isfsArray.addSFSObject(isfsObject);
        }

        data.putSFSArray(Params.LIST, isfsArray);

        data.putInt("league", rankLeagueDBO == null || rankLeagueDBO.leagueId == null ? -1 : rankLeagueDBO.leagueId);
        data.putInt("league_type", rankLeagueDBO == null || rankLeagueDBO.type == null ? -1 : rankLeagueDBO.type);
        data.putUtfString("league_name", rankLeagueDBO == null || rankLeagueDBO.name == null ? "" : rankLeagueDBO.name);

        data.putInt(Params.TIME, TimeUtils.getDeltaTimeToTime(ETimeType.NEW_WEEK, Utils.getTimestampInSecond()) - 86400);
    }
}
