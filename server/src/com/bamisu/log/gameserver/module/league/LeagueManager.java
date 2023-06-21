package com.bamisu.log.gameserver.module.league;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.sql.game.dbo.ConfigRankLeagueDBO;
import com.bamisu.gamelib.sql.game.dbo.RankLeagueDBO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.league.RankCampaignModel;
import com.bamisu.log.gameserver.datamodel.league.RankDarkrealmModel;
import com.bamisu.log.gameserver.datamodel.league.RankEndlessnightModel;
import com.bamisu.log.gameserver.datamodel.league.UserLeagueModel;
import com.bamisu.log.gameserver.datamodel.league.entities.UserRankInfo;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.league.config.LeagueConfig;
import com.bamisu.log.gameserver.module.league.config.entities.LeagueVO;
import com.bamisu.log.gameserver.sql.rank.dao.RankDAO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Quach Thanh Phong
 * On 1/26/2023 - 4:31 PM
 */
public class LeagueManager {

    private LeagueConfig leagueConfig;


    private static LeagueManager ourInstance = new LeagueManager();

    public static LeagueManager getInstance() {
        return ourInstance;
    }

    private LeagueManager() {
        leagueConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.League.FILE_PATH_CONFIG_LEAGUE), LeagueConfig.class);
    }


    public int getLeagueType(long power) {
        for (LeagueVO leagueVO : leagueConfig.list) {
            if (leagueVO.range.size() == 1 && leagueVO.range.get(0) <= power) {
                return leagueVO.id;
            }
            if (leagueVO.range.size() == 2) {
                if (leagueVO.range.get(0) <= power && power <= leagueVO.range.get(1)) {
                    return leagueVO.id;
                }
            }
        }
        return 0;
    }


    public RankCampaignModel getRankCampaignModel(long leagueId, Zone zone) {
        if (leagueId < 0) {
            return new RankCampaignModel();
        }

        ConfigRankLeagueDBO configRankLeagueDBO = RankDAO.getConfigRankLeagueDBO(zone);
        int season = configRankLeagueDBO != null ? configRankLeagueDBO.season : 0;
        RankCampaignModel rankCampaignModel = RankCampaignModel.copyFromDBtoObject(season, leagueId, zone);
        if (rankCampaignModel == null) {
            RankLeagueDBO rankLeagueDBO = RankDAO.getRankLeagueByLeagueId(zone, leagueId);
            rankCampaignModel = RankCampaignModel.createRankCampaignModel(season, leagueId, rankLeagueDBO.type, zone);
        }
        rankCampaignModel.rank = rankCampaignModel.rank.stream()
                .sorted(Comparator.comparing(UserRankInfo::getScore).reversed().thenComparing(UserRankInfo::getTime))
                .collect(Collectors.toList());
        return rankCampaignModel;
    }

    public synchronized void updateRankCampaign(Zone zone, long uid, int score) {
        long now = System.currentTimeMillis();

        RankLeagueDBO rankLeagueDBO = RankDAO.getRankLeague(zone, uid);

        ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankCampaign - " + uid + " " + score + " | " + Utils.toJson(rankLeagueDBO));

        if (rankLeagueDBO == null) {
            return;
        }
        RankCampaignModel rankCampaignModel = getRankCampaignModel(rankLeagueDBO.leagueId, zone);

        ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankCampaign - " + Utils.toJson(rankCampaignModel));

//        //TH trận đầu tiên -> chỉnh sửa lại league :) (####################)
//        UserLeagueModel userLeagueModel = UserLeagueModel.copyFromDBtoObject(uid, rankLeagueDBO.season, zone);
//        if (userLeagueModel.firstFight) {
//            userLeagueModel.firstFight = false;
//            if(!userLeagueModel.saveToDB(zone)) {
//                ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankCampaign - Error " + Utils.toJson(userLeagueModel));
//                return;
//            };
//
//            int leagueType = getLeagueType(HeroManager.getInstance().getPower(uid, zone));
//
//            ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankCampaign - " + leagueType);
//
//            if (leagueType > rankCampaignModel.type) {
//                rankLeagueDBO = RankDAO.updateRankLeagueByType(zone, uid, leagueType);
//
//                ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankCampaign - " + Utils.toJson(rankLeagueDBO));
//
//                if (rankLeagueDBO != null) {
//                    rankCampaignModel = getRankCampaignModel(rankLeagueDBO.leagueId, zone);
//                }
//            }
//        }

        ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankCampaign - " + Utils.toJson(rankCampaignModel));

        this.updateRankCampaign(zone, uid, score, rankCampaignModel);
    }
    private void updateRankCampaign(Zone zone, long uid, int score, RankCampaignModel rankCampaignModel) {
        if (rankCampaignModel.rank.stream().noneMatch(userRankInfo -> Objects.equals(userRankInfo.uid, uid))) {
            //TH khong co trong BXH
            UserRankInfo userRankInfo = new UserRankInfo();
            userRankInfo.uid = uid;
            userRankInfo.score = score;
            userRankInfo.time = Utils.getTimestampInSecond();

            rankCampaignModel.rank.add(userRankInfo);
            rankCampaignModel.rank = rankCampaignModel.rank.stream()
                    .sorted(Comparator.comparing(UserRankInfo::getScore).reversed().thenComparing(UserRankInfo::getTime))
                    .collect(Collectors.toList());
        } else {
            //TH co trong BXH
            rankCampaignModel.rank = rankCampaignModel.rank.stream()
                    .peek(userRankInfo -> {
                        if (Objects.equals(userRankInfo.uid, uid)) {
                            userRankInfo.score += score;
                            userRankInfo.time = Utils.getTimestampInSecond();
                        }
                    })
                    .sorted(Comparator.comparing(UserRankInfo::getScore).reversed().thenComparing(UserRankInfo::getTime))
                    .collect(Collectors.toList());
        }
        //Save
        rankCampaignModel.saveToDB(zone);
    }

    public RankDarkrealmModel getRankDarkrealmModel(long leagueId, Zone zone) {
        if (leagueId < 0) {
            return new RankDarkrealmModel();
        }

        ConfigRankLeagueDBO configRankLeagueDBO = RankDAO.getConfigRankLeagueDBO(zone);
        int season = configRankLeagueDBO != null ? configRankLeagueDBO.season : 0;

        RankDarkrealmModel rankDarkrealmModel = RankDarkrealmModel.copyFromDBtoObject(season, leagueId, zone);
        if (rankDarkrealmModel == null) {
            RankLeagueDBO rankLeagueDBO = RankDAO.getRankLeagueByLeagueId(zone, leagueId);
            rankDarkrealmModel = RankDarkrealmModel.createRankDarkrealmModel(season, leagueId, rankLeagueDBO.type, zone);
        }
        rankDarkrealmModel.rank = rankDarkrealmModel.rank.stream()
                .sorted(Comparator.comparing(UserRankInfo::getScore).reversed().thenComparing(UserRankInfo::getTime))
                .collect(Collectors.toList());
        return rankDarkrealmModel;
    }

    public synchronized void updateRankDarkrealm(Zone zone, long uid, int score) {
        long now = System.currentTimeMillis();

        RankLeagueDBO rankLeagueDBO = RankDAO.getRankLeague(zone, uid);

        ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankDarkrealm - " + uid + " " + score + " | " + Utils.toJson(rankLeagueDBO));

        if (rankLeagueDBO == null) {
            return;
        }
        RankDarkrealmModel rankDarkrealmModel = getRankDarkrealmModel(rankLeagueDBO.leagueId, zone);

        ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankDarkrealm - " + Utils.toJson(rankDarkrealmModel));

//        //TH trận đầu tiên -> chỉnh sửa lại league :) (####################)
//        UserLeagueModel userLeagueModel = UserLeagueModel.copyFromDBtoObject(uid, rankLeagueDBO.season, zone);
//        if (userLeagueModel.firstFight) {
//            userLeagueModel.firstFight = false;
//            if(!userLeagueModel.saveToDB(zone)) {
//                ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankDarkrealm - Error " + Utils.toJson(userLeagueModel));
//                return;
//            };
//
//            int leagueType = getLeagueType(HeroManager.getInstance().getPower(uid, zone));
//
//            ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankDarkrealm - " + leagueType);
//
//            if (leagueType > rankDarkrealmModel.type) {
//                rankLeagueDBO = RankDAO.updateRankLeagueByType(zone, uid, leagueType);
//
//                ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankDarkrealm - " + Utils.toJson(rankLeagueDBO));
//
//                if (rankLeagueDBO != null) {
//                    rankDarkrealmModel = getRankDarkrealmModel(rankLeagueDBO.leagueId, zone);
//                }
//            }
//        }

        ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankDarkrealm - " + Utils.toJson(rankDarkrealmModel));

        //Save
        this.updateRankDarkrealm(zone, uid, score, rankDarkrealmModel);
    }
    private void updateRankDarkrealm(Zone zone, long uid, int score, RankDarkrealmModel rankDarkrealmModel) {
        if (rankDarkrealmModel.rank.stream().noneMatch(userRankInfo -> Objects.equals(userRankInfo.uid, uid))) {
            UserRankInfo userRankInfo = new UserRankInfo();
            userRankInfo.uid = uid;
            userRankInfo.score = score;
            userRankInfo.time = Utils.getTimestampInSecond();

            rankDarkrealmModel.rank.add(userRankInfo);
            rankDarkrealmModel.rank = rankDarkrealmModel.rank.stream()
                    .sorted(Comparator.comparing(UserRankInfo::getScore).reversed().thenComparing(UserRankInfo::getTime))
                    .collect(Collectors.toList());
        } else {
            rankDarkrealmModel.rank = rankDarkrealmModel.rank.stream()
                    .peek(userRankInfo -> {
                        if (Objects.equals(userRankInfo.uid, uid)) {
                            userRankInfo.score += score;
                            userRankInfo.time = Utils.getTimestampInSecond();
                        }
                    })
                    .sorted(Comparator.comparing(UserRankInfo::getScore).reversed().thenComparing(UserRankInfo::getTime))
                    .collect(Collectors.toList());
        }

        //Save
        rankDarkrealmModel.saveToDB(zone);
    }

    public RankEndlessnightModel getRankEndlessnightModel(long leagueId, Zone zone) {
        if (leagueId < 0) {
            return new RankEndlessnightModel();
        }

        ConfigRankLeagueDBO configRankLeagueDBO = RankDAO.getConfigRankLeagueDBO(zone);
        int season = configRankLeagueDBO != null ? configRankLeagueDBO.season : 0;
        RankEndlessnightModel rankEndlessnightModel = RankEndlessnightModel.copyFromDBtoObject(season, leagueId, zone);
        if (rankEndlessnightModel == null) {
            RankLeagueDBO rankLeagueDBO = RankDAO.getRankLeagueByLeagueId(zone, leagueId);
            rankEndlessnightModel = RankEndlessnightModel.createRankEndlessnightModel(season, leagueId, rankLeagueDBO.type, zone);
        }
        rankEndlessnightModel.rank = rankEndlessnightModel.rank.stream()
                .sorted(Comparator.comparing(UserRankInfo::getScore).reversed().thenComparing(UserRankInfo::getTime))
                .collect(Collectors.toList());
        return rankEndlessnightModel;
    }

    public synchronized void updateRankEndlessnight(Zone zone, long uid, int score) {
        long now = System.currentTimeMillis();

        RankLeagueDBO rankLeagueDBO = RankDAO.getRankLeague(zone, uid);

        ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankEndlessnight - " + uid + " " + score + " | " + Utils.toJson(rankLeagueDBO));

        if (rankLeagueDBO == null) {
            return;
        }
        RankEndlessnightModel rankEndlessnightModel = getRankEndlessnightModel(rankLeagueDBO.leagueId, zone);

        ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankEndlessnight - " + Utils.toJson(rankEndlessnightModel));

//        //TH trận đầu tiên -> chỉnh sửa lại league :) (####################)
//        UserLeagueModel userLeagueModel = UserLeagueModel.copyFromDBtoObject(uid, rankLeagueDBO.season, zone);
//        if (userLeagueModel.firstFight) {
//            userLeagueModel.firstFight = false;
//            if(!userLeagueModel.saveToDB(zone)) {
//                ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankEndlessnight - Error " + Utils.toJson(userLeagueModel));
//                return;
//            };
//
//            int leagueType = getLeagueType(HeroManager.getInstance().getPower(uid, zone));
//
//            ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankEndlessnight - " + leagueType);
//
//            if (leagueType > rankEndlessnightModel.type) {
//                rankLeagueDBO = RankDAO.updateRankLeagueByType(zone, uid, leagueType);
//
//                ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankEndlessnight - " + Utils.toJson(rankLeagueDBO));
//
//                if (rankLeagueDBO != null) {
//                    rankEndlessnightModel = getRankEndlessnightModel(rankLeagueDBO.leagueId, zone);
//                }
//            }
//        }

        ((ZoneExtension) zone.getExtension()).trace(now + "LeagueManager - updateRankEndlessnight - " + Utils.toJson(rankEndlessnightModel));

        this.updateRankEndlessnight(zone, uid, score, rankEndlessnightModel);
    }
    private void updateRankEndlessnight(Zone zone, long uid, int score, RankEndlessnightModel rankEndlessnightModel) {
        if (rankEndlessnightModel.rank.stream().noneMatch(userRankInfo -> Objects.equals(userRankInfo.uid, uid))) {
            UserRankInfo userRankInfo = new UserRankInfo();
            userRankInfo.uid = uid;
            userRankInfo.score = score;
            userRankInfo.time = Utils.getTimestampInSecond();

            rankEndlessnightModel.rank.add(userRankInfo);
            rankEndlessnightModel.rank = rankEndlessnightModel.rank.stream()
                    .sorted(Comparator.comparing(UserRankInfo::getScore).reversed().thenComparing(UserRankInfo::getTime))
                    .collect(Collectors.toList());
        } else {
            rankEndlessnightModel.rank = rankEndlessnightModel.rank.stream()
                    .peek(userRankInfo -> {
                        if (Objects.equals(userRankInfo.uid, uid)) {
                            userRankInfo.score += score;
                            userRankInfo.time = Utils.getTimestampInSecond();
                        }
                    })
                    .sorted(Comparator.comparing(UserRankInfo::getScore).reversed().thenComparing(UserRankInfo::getTime))
                    .collect(Collectors.toList());
        }
        //Save
        rankEndlessnightModel.saveToDB(zone);
    }

}
