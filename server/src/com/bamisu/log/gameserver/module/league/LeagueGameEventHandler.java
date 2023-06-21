package com.bamisu.log.gameserver.module.league;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.quest.UserQuestModel;
import com.bamisu.log.gameserver.module.GameEvent.BaseGameEvent;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EConditionType;
import com.bamisu.log.gameserver.module.campaign.config.MainCampaignConfig;
import com.bamisu.log.gameserver.module.campaign.config.entities.Area;
import com.bamisu.log.gameserver.module.notification.NotificationManager;
import com.bamisu.log.gameserver.module.notification.defind.ENotification;
import com.bamisu.log.gameserver.module.quest.QuestManager;
import com.bamisu.log.gameserver.sql.rank.dao.RankDAO;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Quach Thanh Phong
 * On 11/11/2022 - 9:56 PM
 */
public class LeagueGameEventHandler extends BaseGameEvent {

    public LeagueGameEventHandler(Zone zone) {
        super(zone);
    }

    @WithSpan
    @Override
    public void handleGameEvent(EGameEvent event, long uid, Map<String, Object> data) {
        switch (event) {
            case CHAP_CAMPAIGN_UPDATE:
                handlerChapCampaignUpdate(uid, data);
                break;
        }
    }

    @Override
    public void initEvent() {
        this.registerEvent(EGameEvent.CHAP_CAMPAIGN_UPDATE);
    }



    @WithSpan
    private void handlerChapCampaignUpdate(long uid, Map<String,Object> data){
        int area = (int) data.getOrDefault(Params.AREA, -1);

        if (area >= 1) {
            //Add to league
            RankDAO.addRankLeague(zone, uid);
        }

    }
}
