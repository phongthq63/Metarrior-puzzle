package com.bamisu.log.gameserver.module.campaign.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.gamelib.sql.game.dbo.RankMissionDBO;
import com.bamisu.gamelib.sql.game.dbo.UserRankMissionDBO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 11/22/2022 - 2:26 AM
 */
public class SendGetMissionRank extends BaseMsg {

    public UserManager userManager;
    public List<RankMissionDBO> rankMissionDBOS;

    public SendGetMissionRank() {
        super(CMD.CMD_GET_MISSION_RANK);
    }

    @Override
    public void packData() {
        super.packData();

        ISFSArray isfsArray = new SFSArray();
        ISFSObject isfsObject;
        UserModel userModel;
        for (int i = 0; i < rankMissionDBOS.size(); i++) {
            isfsObject = new SFSObject();
            userModel = userManager.getUserModel(rankMissionDBOS.get(i).uid);

            isfsObject.putLong(Params.UID, userModel.userID);
            isfsObject.putUtfString(Params.AVATAR_ID, userModel.avatar);
            isfsObject.putInt(Params.FRAME, userModel.avatarFrame);
            isfsObject.putUtfString(Params.NAME, userModel.displayName);
            isfsObject.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(userModel.userID, userManager.getZone()));
            isfsObject.putInt(Params.POWER, HeroManager.getInstance().getPower(userModel.userID, userManager.getZone()));
            isfsObject.putInt(Params.RANK, i + 1);
            isfsObject.putInt(Params.POINT, rankMissionDBOS.get(i).score);

            isfsArray.addSFSObject(isfsObject);
        }

        data.putSFSArray(Params.LIST, isfsArray);

        data.putInt(Params.TIME, TimeUtils.getDeltaTimeToTime(ETimeType.NEW_WEEK, Utils.getTimestampInSecond()) - 86400);
    }
}
