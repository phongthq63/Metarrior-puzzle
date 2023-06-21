package com.bamisu.log.gameserver.module.mission.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.sql.game.dbo.UserRankMissionDBO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.mission.MissionManager;
import com.smartfoxserver.v2.entities.data.SFSArray;

public class SendGetMissionConfig extends BaseMsg {

    public UserRankMissionDBO userRankMissionDBO;

    public SendGetMissionConfig() {
        super(CMD.CMD_GET_MISSION_CONFIG);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putLong("rank", userRankMissionDBO != null ? userRankMissionDBO.rank : -1);
        data.putLong("point", userRankMissionDBO != null ? userRankMissionDBO.score : 0);
        data.putSFSArray(Params.COST, SFSArray.newFromJsonData(Utils.toJson(MissionManager.getInstance().getCostFightMission())));
        data.putInt("time_game", 120);
        data.putInt(Params.TIME, TimeUtils.getDeltaTimeToTime(ETimeType.NEW_WEEK, Utils.getTimestampInSecond()) - 86400);
    }
}
