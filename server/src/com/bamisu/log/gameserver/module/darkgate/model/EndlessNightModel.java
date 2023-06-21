package com.bamisu.log.gameserver.module.darkgate.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.sql.game.dbo.RankLeagueDBO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.campaign.UserCampaignDetailModel;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.guild.UserGuildModel;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.log.gameserver.module.darkgate.DarkGateManager;
import com.bamisu.log.gameserver.module.darkgate.model.entities.AllianceRankItemVO;
import com.bamisu.log.gameserver.module.darkgate.model.entities.SoloRankItemVO;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.league.LeagueManager;
import com.bamisu.log.gameserver.sql.rank.dao.RankDAO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;

/**
 * Create by Popeye on 7:57 PM, 11/24/2020
 */
public class EndlessNightModel extends DataModel {
    public String hash;
    public String bossID;
    public String bossElement;
    public String bossKingdom;
    public int boosLevel = 30;

    private Object soloRankObject = new ResourcePackage();
    private Object allianceRankObject = new ResourcePackage();

    public EndlessNightModel() {
    }

    public EndlessNightModel(String hash) {
        this.hash = hash;
        this.bossID = "MBS1003";
        this.bossElement = TeamUtils.genElement(Element.FOREST.getId());
        this.bossKingdom = Kingdom.DARK.getId();
        this.boosLevel = 30;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(hash, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static EndlessNightModel copyFromDBtoObject(String hash, Zone zone) {
        EndlessNightModel model = null;
        try {
            String str = (String) getModel(hash, EndlessNightModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, EndlessNightModel.class);
                if (model != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (model == null) {
            model = new EndlessNightModel(hash);
            model.saveToDB(zone);
        }

        if(model.boosLevel != 30){
            model.boosLevel = 30;
            model.saveToDB(zone);
        }
        return model;
    }

    /**
     * update rank khi có 1 người thay đổi điểm
     *
     * @param uid
     * @param point
     * @param zone
     */
    public synchronized void updateRank(long uid, long point, Zone zone) {
//        Jedis jedis = ((ZoneExtension) zone.getExtension()).getRedisController().getJedis();
        UserModel userModel = ((ZoneExtension) zone.getExtension()).getUserManager().getUserModel(uid);

        //solo
        synchronized (soloRankObject) {
            try {
                UserCampaignDetailModel userCampaignDetailModel = UserCampaignDetailModel.copyFromDBtoObject(uid, zone);
                int currentArea = Arrays.asList(userCampaignDetailModel.userMainCampaignDetail.readNextStation().split(",", 2)).parallelStream().mapToInt(Integer::parseInt).toArray()[0];


                if (TimeUtils.getDeltaTimeToTime(ETimeType.NEW_WEEK, Utils.getTimestampInSecond()) - 86400 > 0 && currentArea > 0) {
//                    RankDAO.updateRankEndlessnight(zone, uid, point);
                    LeagueManager.getInstance().updateRankEndlessnight(zone, uid, (int) point);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //alliance
        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, zone);
        if(userGuildModel.gid != 0){    //có trong guid
            synchronized (allianceRankObject) {
                try {
                    GuildModel guildModel = GuildModel.copyFromDBtoObject(userGuildModel.gid, zone);
                    if(guildModel != null){
//                        jedis.zincrby("endlessnight_leaderboard_guild_" + hash, point, String.valueOf(userModel.userID));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        saveToDB(zone);
    }

    public String readBossID(boolean isChristmas){
        if (isChristmas) {   //sự kiện noel đang diễn ra
            return "MBS1004";
        }

        return bossID;
    }

    public String readBossElement(boolean isChristmas) {
        if (isChristmas) {   //sự kiện noel đang diễn ra
            return Element.ICE.getId();
        }

        return bossElement;
    }
}
