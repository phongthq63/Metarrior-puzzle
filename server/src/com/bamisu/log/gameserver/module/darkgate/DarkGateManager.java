package com.bamisu.log.gameserver.module.darkgate;

import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.gamelib.sql.game.dbo.RankLeagueDBO;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.guild.UserGuildModel;
import com.bamisu.log.gameserver.datamodel.league.RankDarkrealmModel;
import com.bamisu.log.gameserver.datamodel.league.RankEndlessnightModel;
import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.campaign.entities.HeroPackage;
import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.creep.entities.CreepVO;
import com.bamisu.log.gameserver.module.characters.entities.*;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.log.gameserver.module.darkgate.cmd.send.*;
import com.bamisu.log.gameserver.module.darkgate.config.*;
import com.bamisu.log.gameserver.module.darkgate.entities.EDarkGateEvent;
import com.bamisu.log.gameserver.module.darkgate.entities.EDarkGateState;
import com.bamisu.log.gameserver.module.darkgate.entities.EventStatus;
import com.bamisu.log.gameserver.module.darkgate.model.*;
import com.bamisu.log.gameserver.module.darkgate.model.entities.ActiveEventVO;
import com.bamisu.log.gameserver.module.darkgate.model.entities.AllianceRankItemVO;
import com.bamisu.log.gameserver.module.darkgate.model.entities.SoloRankItemVO;
import com.bamisu.log.gameserver.module.event.event.christmas.ChristmasEventManager;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.hero.entities.HeroPosition;
import com.bamisu.log.gameserver.module.hero.exception.InvalidUpdateTeamException;
import com.bamisu.log.gameserver.module.ingame.FightingCreater;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingType;
import com.bamisu.log.gameserver.module.league.LeagueManager;
import com.bamisu.log.gameserver.sql.rank.dao.RankDAO;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Create by Popeye on 10:04 AM, 11/6/2020
 */
public class DarkGateManager {
    public DarkGateHandler darkGateHandler;
    public DarkGateModel darkGateModel;
    //    public static int endWeekTime = (int) (Utils.getDeltaSecondsToEndWeek() - 60 * 2);
    public static int endWeekTime = 24 * 60 * 60;
    public static int oneWeekSecond = 60 * 60 * 24 * 7;
    public ScheduledExecutorService scheduler = LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.DARK_GATE, 1);
    public Logger logger = Logger.getLogger("dg");
    public static final List<String> endlessNightCreep = Arrays.asList("M1020", "M1021", "M1022", "M1023", "M1024");
    private static final List<CandyRate> candyRates = Arrays.asList(
            new CandyRate( 250000,    5000),
            new CandyRate( 250000,    7500),
            new CandyRate( 500000,   15000),
            new CandyRate(1000000,   30000),
            new CandyRate(3000000,   60000),
            new CandyRate(Integer.MAX_VALUE - 5000000, 100000)
    );

    public DarkGateManager(DarkGateHandler darkGateHandler) {
        this.darkGateHandler = darkGateHandler;

        init();
    }

    private void init() {
        //config
        DarkGateConfigManager.getInstance();

        //model
        darkGateModel = DarkGateModel.copyFromDBtoObject(darkGateHandler.getParentExtension().getParentZone());
        if (darkGateModel.activeEvents.isEmpty()) {
            darkGateModel.activeEvents.add(new ActiveEventVO(EDarkGateEvent.Dark_Realm.id, Utils.genDarkRealmEventHash()));
            darkGateModel.activeEvents.add(new ActiveEventVO(EDarkGateEvent.Endless_Nights.id, Utils.genEndlessNightEventHash()));
            darkGateModel.saveToDB(darkGateHandler.getParentExtension().getParentZone());
        }

        //scheduler bắt đầu tuần mới
        int delayTimeToStartNewWeek = getDeltaTimeStartWeek() + 1;
        logger.info("DARK GATE: " + delayTimeToStartNewWeek + " seconds to new week");
        scheduler.scheduleAtFixedRate(this::onStartWeek, delayTimeToStartNewWeek, oneWeekSecond, TimeUnit.SECONDS);

        //scheduler kết thúc tuần mới
        int delayTimeToNextEndWeek;
        if (getState() == EDarkGateState.IN_WEEK) {
            delayTimeToNextEndWeek = getDeltaTimeToEndWeek();
        } else {
            delayTimeToNextEndWeek = getDeltaTimeToEndWeek() + oneWeekSecond;
        }
        logger.info("DARK GATE: " + delayTimeToNextEndWeek + " seconds to end week");
        scheduler.scheduleAtFixedRate(this::onEndWeek, delayTimeToNextEndWeek, oneWeekSecond, TimeUnit.SECONDS);
    }

    public void onStartWeek() {
        scheduler.schedule(() -> {
            try {
                synchronized (darkGateModel) {
                    logger.info("============ GARK GATE onStartWeek =============");
                    logger.info("Event active pre week: " + Utils.toJson(darkGateModel.activeEvents));

                    //gỡ event đang chạy và chạy các event ở tuần tiếp theo
                    List<ActiveEventVO> eventForNextWeek = willStartOnNextWeekEvents();
                    darkGateModel.activeEvents.clear();
                    darkGateModel.activeEvents.addAll(eventForNextWeek);
                    darkGateModel.saveToDB(darkGateHandler.getParentExtension().getParentZone());
                    sendNotifyUpdateEventStatus(CMD.UPDATE_DARK_GATE_EVENT_STATUS, null);

                    //log
                    for (ActiveEventVO activeEventVO : darkGateModel.activeEvents) {
                        logger.info("Event active this week: " + Utils.toJson(activeEventVO));
                    }
                    logger.info("=====================================================");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * thời điểm tổng kết tuần
     */
    public void onEndWeek() {
        scheduler.schedule(() -> {
            try {
                synchronized (darkGateModel) {
                    logger.info("=============== GARK GATE onEndWeek ===================");
                    logger.info("Event end week: " + Utils.toJson(darkGateModel.activeEvents));

                    //tính toán các sự kiện sẽ bắt đầu vào tuần tiếp theo
                    darkGateModel.willActiveEvents.clear();
                    darkGateModel.willActiveEvents.addAll(willStartOnNextWeekEvents());

                    //gửi thông báo thay đổi trạng thái các event
                    sendNotifyUpdateEventStatus(CMD.UPDATE_DARK_GATE_EVENT_STATUS, null);

                    //tính toán kết quả các event chạy ở tuần vừa rồi
                    for (ActiveEventVO activeEventVO : darkGateModel.activeEvents) {
                        switch (EDarkGateEvent.fromID(activeEventVO.id)) {
                            case Endless_Nights:
                                onEndWeekEndLessNight();
                                break;
                            case Dark_Realm:
                                onEndWeekDarkRealm();
                                break;
                        }
                    }
                    logger.info("=====================================================");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, TimeUnit.SECONDS);

    }

    public EDarkGateState getState() {
        if (Utils.getDeltaSecondsToEndWeek() > endWeekTime) return EDarkGateState.IN_WEEK;
        return EDarkGateState.END_WEEK;
    }

    /**
     * số dây tính từ thời điểm hiện tại đến khi kết thúc sự kiện
     *
     * @return
     */
    public static int getDeltaTimeToEndWeek() {
        return (int) Utils.getDeltaSecondsToEndWeek() - endWeekTime;
    }

    /**
     * số dây tính từ thời điểm hiện tại đến bắt đầu sự kiện
     *
     * @return
     */
    public static int getDeltaTimeStartWeek() {
        return (int) Utils.getDeltaSecondsToEndWeek();
    }


    /**
     * trả quà cho top Dark Realm
     */
    private void onEndWeekDarkRealm() {
        DarkRealmModel darkRealmModel = getDarkRealmModel();
        DarkRealmRewardVO darkRealmRewardVO = DarkGateConfigManager.getInstance().getDarkGateRewardsConfigVO().darkRealm;
//        Jedis jedis = ((ZoneExtension) darkGateHandler.getParentExtension().getParentZone().getExtension()).getRedisController().getJedis();
//        //solo
//        Set<String> uids = jedis.zrevrange("darkrealm_leaderboard_solo_" + darkRealmModel.hash, 0, -1);
//        int i = 0;
//        for (String uid : uids) {
//            for (RankRewardVO rankRewardVO : darkRealmRewardVO.solo) {
//                if (rankRewardVO.rank > i) {
//                    MailUtils.getInstance().sendMailDarkRealm_Solo(Long.valueOf(uid), rankRewardVO.rewards, darkGateHandler.getParentExtension().getParentZone());
//                    break;
//                }
//            }
//            i++;
//        }
//        //alliance
//        Set<String> gids = jedis.zrevrange("darkrealm_leaderboard_guild_" + darkRealmModel.hash, 0, -1);
//        i = 0;
//        for (String gid : gids) {
//            for (RankRewardVO rankRewardVO : darkRealmRewardVO.alliance) {
//                if (rankRewardVO.rank > i) {
//                    GuildModel guildModel = GuildManager.getInstance().getGuildModel(Long.valueOf(gid), darkGateHandler.getParentExtension().getParentZone());
//                    if (guildModel != null) {
//                        for (long uid : guildModel.member) {
//                            MailUtils.getInstance().sendMailDarkRealm_Alliance(uid, rankRewardVO.rewards, darkGateHandler.getParentExtension().getParentZone());
//                        }
//                    }
//                    break;
//                }
//            }
//            i++;
//        }
    }

    /**
     * trả quà cho top EndLessNight
     */
    private void onEndWeekEndLessNight() {
        EndlessNightModel endlessNightModel = getEndlessNightModel();
        EndlessNightsRewardVO endlessNightsRewardVO = DarkGateConfigManager.getInstance().getDarkGateRewardsConfigVO().endlessNights;
//        Jedis jedis = ((ZoneExtension) darkGateHandler.getParentExtension().getParentZone().getExtension()).getRedisController().getJedis();
//
//        //solo
//        Set<String> uids = jedis.zrevrange("endlessnight_leaderboard_solo_" + endlessNightModel.hash, 0, -1);
//        int i = 0;
//        for (String uid : uids) {
//            for (RankRewardVO rankRewardVO : endlessNightsRewardVO.solo) {
//                if (rankRewardVO.rank > i) {
//                    MailUtils.getInstance().sendMailEndless_Solo(Long.valueOf(uid), rankRewardVO.rewards, darkGateHandler.getParentExtension().getParentZone());
//                    break;
//                }
//            }
//            i++;
//        }
//        //alliance
//        Set<String> gids = jedis.zrevrange("darkrealm_leaderboard_guild_" + endlessNightModel.hash, 0, -1);
//        i = 0;
//        for (String gid : gids) {
//            for (RankRewardVO rankRewardVO : endlessNightsRewardVO.alliance) {
//                if (rankRewardVO.rank > i) {
//                    GuildModel guildModel = GuildManager.getInstance().getGuildModel(Long.valueOf(gid), darkGateHandler.getParentExtension().getParentZone());
//                    if (guildModel != null) {
//                        for (long uid : guildModel.member) {
//                            MailUtils.getInstance().sendMailEndless_Alliance(uid, rankRewardVO.rewards, darkGateHandler.getParentExtension().getParentZone());
//                        }
//                    }
//                    break;
//                }
//            }
//        }
    }

    /**
     * dự đoán những sụ kiện sẽ diễn ra ở tuần tiếp theo
     *
     * @return
     */
    public List<ActiveEventVO> willStartOnNextWeekEvents() {
        List<ActiveEventVO> eventForNextWeek = new ArrayList<>();
        DarkGateConfigMainVO darkGateConfigMainVO = DarkGateConfigManager.getInstance().getDarkGateConfigMainVO();

        for (DarkGateEventVO darkGateEventVO : darkGateConfigMainVO.events) {
            if (darkGateConfigMainVO.option.equalsIgnoreCase("alternate") && darkGateModel.containsEventID(darkGateEventVO.id))
                continue;   //đã diễn ra ở tuần vừa rồi

            //cho hoạt động trong tuần kế kiếp
            switch (EDarkGateEvent.fromID(darkGateEventVO.id)) {
                case Dark_Realm:
                    eventForNextWeek.add(new ActiveEventVO(EDarkGateEvent.Dark_Realm.id, Utils.genDarkRealmEventHash()));
                    break;
                case Endless_Nights:
                    eventForNextWeek.add(new ActiveEventVO(EDarkGateEvent.Endless_Nights.id, Utils.genEndlessNightEventHash()));
                    break;
            }
        }
        if (eventForNextWeek.isEmpty()) {
            eventForNextWeek.add(new ActiveEventVO(EDarkGateEvent.Dark_Realm.id, Utils.genDarkRealmEventHash()));
        }
        return eventForNextWeek;
    }

    /**
     * gửi thông tin scene Dark gate
     */
    public void sendNotifyUpdateEventStatus(int cmd, User user) {
        DarkGateConfigMainVO darkGateConfigMainVO = DarkGateConfigManager.getInstance().getDarkGateConfigMainVO();
        SendDarkGateSceneInfo sendDarkGateSceneInfo = new SendDarkGateSceneInfo(cmd);

        //Đang ở giữa tuần
        if (getState() == EDarkGateState.IN_WEEK) {
            for (DarkGateEventVO darkGateEventVO : darkGateConfigMainVO.events) {
                EventStatus eventStatus;
                int time;
                if (darkGateModel.containsEventID(darkGateEventVO.id)) {
                    eventStatus = EventStatus.ACTIVE;
                    time = getDeltaTimeToEndWeek();
                } else {
                    eventStatus = EventStatus.WAITING;
                    time = getDeltaTimeStartWeek();
                }
                if (darkGateEventVO.id == EDarkGateEvent.Dark_Realm.id) {
                    DarkRealmModel darkRealmModel = getDarkRealmModel();
                    if (darkRealmModel == null) {  //Đang k diễn ra
                        sendDarkGateSceneInfo.pushEvent(darkGateEventVO.id, eventStatus, time, new DarkRealmModel("").readBossID(isChristmasEvent()), "", "", 0);
                    } else {
                        sendDarkGateSceneInfo.pushEvent(darkGateEventVO.id, eventStatus, time, darkRealmModel.readBossID(isChristmasEvent()), darkRealmModel.readBossElement(isChristmasEvent()), darkRealmModel.bossKingdom, darkRealmModel.boosLevel);
                    }
                }
                if (darkGateEventVO.id == EDarkGateEvent.Endless_Nights.id) {
                    EndlessNightModel endlessNightModel = getEndlessNightModel();
                    if (endlessNightModel == null) {  //Đang k diễn ra
                        sendDarkGateSceneInfo.pushEvent(darkGateEventVO.id, eventStatus, time, new EndlessNightModel("").readBossID(isChristmasEvent()), "", "", 0);
                    } else {
                        sendDarkGateSceneInfo.pushEvent(darkGateEventVO.id, eventStatus, time, endlessNightModel.readBossID(isChristmasEvent()), endlessNightModel.readBossElement(isChristmasEvent()), endlessNightModel.bossKingdom, endlessNightModel.boosLevel);
                    }
                }
            }
        } else { //Đang ở cuối tuần
            for (DarkGateEventVO darkGateEventVO : darkGateConfigMainVO.events) {
                EventStatus eventStatus;
                int time;
                eventStatus = EventStatus.WAITING;

                if (darkGateModel.containsEventIDWillStart(darkGateEventVO.id)) {
                    time = getDeltaTimeStartWeek();
                } else {
                    time = getDeltaTimeStartWeek() + oneWeekSecond;
                }
                if (darkGateEventVO.id == EDarkGateEvent.Dark_Realm.id) {
                    DarkRealmModel darkRealmModel = getDarkRealmModel();
                    if (darkRealmModel == null) {  //Đang k diễn ra
                        sendDarkGateSceneInfo.pushEvent(darkGateEventVO.id, eventStatus, time, "MBS1000", "", "", 0);
                    } else {
                        sendDarkGateSceneInfo.pushEvent(darkGateEventVO.id, eventStatus, time, darkRealmModel.readBossID(isChristmasEvent()), darkRealmModel.readBossElement(isChristmasEvent()), darkRealmModel.bossKingdom, darkRealmModel.boosLevel);
                    }
                }
                if (darkGateEventVO.id == EDarkGateEvent.Endless_Nights.id) {
                    EndlessNightModel endlessNightModel = getEndlessNightModel();
                    if (endlessNightModel == null) {  //Đang k diễn ra
                        sendDarkGateSceneInfo.pushEvent(darkGateEventVO.id, eventStatus, time, "MBS1003", "", "", 0);
                    } else {
                        sendDarkGateSceneInfo.pushEvent(darkGateEventVO.id, eventStatus, time, endlessNightModel.readBossID(isChristmasEvent()), endlessNightModel.readBossElement(isChristmasEvent()), endlessNightModel.bossKingdom, endlessNightModel.boosLevel);
                    }
                }
            }
        }

        if (user == null) {
            darkGateHandler.send(sendDarkGateSceneInfo, darkGateHandler.getParentExtension().getParentZone().getUserManager().getAllUsers());
        } else {
            darkGateHandler.send(sendDarkGateSceneInfo, user);
        }

    }

    /**
     * lấy thông tin khi vào màn hình dark gate
     *
     * @param user
     */
    public void getDarkGateSceneInfo(User user) {
        sendNotifyUpdateEventStatus(CMD.GET_DARK_GATE_SCENE_INFO, user);
    }

    public void getDarkRealmSceneInfo(User user) {
        if (getState() == EDarkGateState.END_WEEK || !darkGateModel.containsEventID(EDarkGateEvent.Dark_Realm.id))
            return;
        Zone zone = darkGateHandler.getParentExtension().getParentZone();
        UserModel userModel = ((ZoneExtension) darkGateHandler.getParentExtension()).getUserManager().getUserModel(user);

        SendDarkRealmSceneInfo send = new SendDarkRealmSceneInfo();
        send.maxTurn = 2;
        send.remainingTurn = UserDarkGateModel.copyFromDBtoObject(userModel.userID, zone).getDarkRealmTurn(zone);
        darkGateHandler.send(send, user);
    }

    /**
     * lấy thông tin bảng xếp hạng dark realm
     *
     * @param user
     */
    public void getDarkRealmRank(User user) {
        if (getState() == EDarkGateState.END_WEEK || !darkGateModel.containsEventID(EDarkGateEvent.Dark_Realm.id))
            return;

        //sự kiện vẫn đang diễn ra
        UserModel userModel = ((ZoneExtension) darkGateHandler.getParentExtension()).getUserManager().getUserModel(user);
        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(userModel.userID, darkGateHandler.getParentExtension().getParentZone());
        DarkRealmModel darkRealmModel = getDarkRealmModel();

        Zone zone = darkGateHandler.getParentExtension().getParentZone();
//        Jedis jedis = ((ZoneExtension) zone.getExtension()).getRedisController().getJedis();
        UserManager userManager = ((ZoneExtension) zone.getExtension()).getUserManager();
        RankLeagueDBO rankLeagueDBO = RankDAO.getRankLeague(zone, userModel.userID);
        int leagueId = rankLeagueDBO != null ? rankLeagueDBO.leagueId : -1;
        RankDarkrealmModel rankDarkrealmModel = LeagueManager.getInstance().getRankDarkrealmModel(leagueId, zone);
//        List<RankDarkrealmUserDBO> rankUser = RankDAO.getListRankDarkrealm(zone, userModel.userID);
        Set<String> gids = new HashSet<>();  //jedis.zrevrange("darkrealm_leaderboard_guild_" + darkRealmModel.hash, 0, 100);

        SendDarkRealmRank send = new SendDarkRealmRank();
        send.leagueId = rankLeagueDBO == null || rankLeagueDBO.leagueId == null ? -1 : rankLeagueDBO.leagueId;
        send.leagueType = rankLeagueDBO == null || rankLeagueDBO.type == null ? -1 : rankLeagueDBO.type;
        send.leagueName = rankLeagueDBO == null || rankLeagueDBO.name == null ? "" : rankLeagueDBO.name;


        send.soloRanks = rankDarkrealmModel.rank.stream().map(index -> {
            UserModel um = userManager.getUserModel(index.uid);
            int level = BagManager.getInstance().getLevelUser(index.uid, zone);
            int power = HeroManager.getInstance().getPower(index.uid, zone);
            long point = index.score;
            return new SoloRankItemVO(um.userID, um.displayName, um.avatar, um.avatarFrame, level, power, point);
        }).collect(Collectors.toList());
        send.allianceRanks = gids.stream().map(gid -> {
            GuildModel guildModel = GuildModel.copyFromDBtoObject(userGuildModel.gid, zone);
            long point = 0; //jedis.zscore("darkrealm_leaderboard_guild_" + darkRealmModel.hash, gid).longValue();
            return new AllianceRankItemVO(guildModel.gId, guildModel.gName, guildModel.readAvatar(), guildModel.readLevel(), point);
        }).collect(Collectors.toList());

        Boolean isSave = false;
        int rankSolo = -1;
        for (int i = 0; i < rankDarkrealmModel.rank.size(); i++) {
            if (userModel.userID == rankDarkrealmModel.rank.get(i).uid) {
                rankSolo = i;
            }
        }
        long rankGuild = 0L; //jedis.zrevrank("darkrealm_leaderboard_guild_" + darkRealmModel.hash, String.valueOf(userModel.userID));
        send.soloRankItemVO = getSoloRankInfo(userModel, send.soloRanks, isSave);
        send.soloRank = rankSolo + 1;
        if (userGuildModel.inGuild()) {
            send.allianceRankItemVO = getAllianceRankInfo(GuildManager.getInstance().getGuildModel(userGuildModel.gid, darkGateHandler.getParentExtension().getParentZone()), send.allianceRanks, isSave);
            send.allianceRank = (int) rankGuild + 1;
        }

        if (isSave) darkRealmModel.saveToDB(darkGateHandler.getParentExtension().getParentZone());
        darkGateHandler.send(send, user);
    }

    /**
     * lấy thông tin rank của 1 guild
     *
     * @param guildModel
     * @return
     */
    private AllianceRankItemVO getAllianceRankInfo(GuildModel guildModel, List<AllianceRankItemVO> allianceRank, Boolean isSave) {
        for (AllianceRankItemVO vo : allianceRank) {
            if (vo.aid == guildModel.gId) {
                return vo;
            }
        }

        //chưa có rank
        AllianceRankItemVO itemVO = new AllianceRankItemVO(guildModel.gId, guildModel.gName, guildModel.readAvatar(), guildModel.readLevel(), 0);
        allianceRank.add(itemVO);
        isSave = true;
        return itemVO;
    }

    /**
     * lấy thông tin rank solo
     *
     * @param userModel
     * @return
     */
    private SoloRankItemVO getSoloRankInfo(UserModel userModel, List<SoloRankItemVO> soloRank, Boolean isSave) {
        for (SoloRankItemVO soloRankItemVO : soloRank) {
            if (soloRankItemVO.uid == userModel.userID) {
                return soloRankItemVO;
            }
        }

        //chưa có rank
        SoloRankItemVO soloRankItemVO = new SoloRankItemVO(userModel.userID, userModel.displayName, userModel.avatar, userModel.avatarFrame,
                BagManager.getInstance().getLevelUser(userModel.userID, darkGateHandler.getParentExtension().getParentZone()),
                HeroManager.getInstance().getPower(userModel.userID, darkGateHandler.getParentExtension().getParentZone()),
                0);
        soloRank.add(soloRankItemVO);
        return soloRankItemVO;
    }

    public boolean isDarkGateOpen(){
        return getState() == EDarkGateState.IN_WEEK;
    }

    /**
     * thách đấu dark realm
     *
     * @param user
     * @param uid
     * @param update
     * @param sageSkill
     */
    public void fightDarkRealm(User user, long uid, List<HeroPosition> update, Collection<String> sageSkill) {
        if (getState() == EDarkGateState.END_WEEK || !darkGateModel.containsEventID(EDarkGateEvent.Dark_Realm.id))
            return;

        UserModel userModel = ((ZoneExtension) darkGateHandler.getParentExtension()).getUserManager().getUserModel(user);
        Zone zone = darkGateHandler.getParentExtension().getParentZone();

        if (!((ZoneExtension) darkGateHandler.getParentExtension()).isTestServer()) { //server test không trừ số lượt
            if (!UserDarkGateModel.copyFromDBtoObject(userModel.userID, zone).canFightDarkRealm(zone)) return;
        }
        //sự kiện vẫn đang diễn ra
        DarkRealmModel darkRealmModel = getDarkRealmModel();
        //update team
        try {
            HeroManager.getInstance().doUpdateTeamHero(zone, uid, ETeamType.DARK_GATE.getId(), update);
        } catch (InvalidUpdateTeamException e) {
            e.printStackTrace();
            return;
        }

        //update sage skill
        SageSkillModel sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
        sageSkillModel.updateCurrentSkill(zone, sageSkill);

        //create fighting room
        CreateRoomSettings cfg = new CreateRoomSettings();
        cfg.setName(Utils.ranStr(10));
        cfg.setGroupId(ConfigHandle.instance().get(Params.DARK_REALM_GROUP_ID));
        cfg.setMaxUsers(1);
        cfg.setDynamic(true);
        cfg.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);
        cfg.setGame(true);

        Map<Object, Object> props = new HashMap<>();
        props.put(Params.UID, uid);
        props.put(Params.WIN_CONDITIONS, Arrays.asList("WCO001"));

        //Team NPC
        List<ICharacter> npcTeam = Arrays.asList(null, Mboss.createMBoss(
                darkRealmModel.readBossID(isChristmasEvent()),
                darkRealmModel.boosLevel,
                0,
                darkRealmModel.bossKingdom,
                darkRealmModel.readBossElement(isChristmasEvent()),
                0), null, null, null);
        props.put(Params.NPC_TEAM, Utils.toJson(npcTeam));

        //hero
        List<Hero> team = Hero.getPlayerTeam(uid, ETeamType.DARK_GATE, zone, false);
        HeroPackage heroPackage = new HeroPackage(team);
        props.put(Params.PLAYER_TEAM, Utils.toJson(heroPackage));
        cfg.setRoomProperties(props);

        //sage
        Sage sage = Sage.createMage(zone, uid);
        props.put(Params.SAGE, Utils.toJson(sage));

        //celestial
        Celestial celestial = Celestial.createCelestial(zone, uid);
        props.put(Params.CELESTIAL, Utils.toJson(celestial));

        cfg.setRoomProperties(props);

        List<RoomVariable> roomVariableList = new ArrayList<>();
        roomVariableList.add(new SFSRoomVariable(Params.TYPE, FightingType.PvM.getType()));
        roomVariableList.add(new SFSRoomVariable(Params.FUNCTION, EFightingFunction.DARK_REALM.getIntValue()));
        cfg.setRoomVariables(roomVariableList);

        try {
            FightingCreater.creatorFightingRoom(zone, user, cfg);
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }
    }

    /**
     * khi người chơi đánh xong 1 trận dark realm
     *
     * @param uid
     * @param point
     */
    public void complateFightDarkRealm(long uid, long point) {
        List<ResourcePackage> listReward = new ArrayList<>(DarkGateConfigManager.getInstance().getDarkGateRewardsConfigVO().darkRealm.challenge);
        if (isChristmasEvent()) {
            listReward.add(new ResourcePackage(MoneyType.CANDY_CANE.getId(), caculateCandyCaneDarkRealm(point)));
        }

        Zone zone = darkGateHandler.getParentExtension().getParentZone();

        if (!UserDarkGateModel.copyFromDBtoObject(uid, zone).fightDarkRealm(zone)) return;

        //add reward
        BagManager.getInstance().addItemToDB(
                listReward,
                uid,
                zone,
                UserUtils.TransactionType.DARK_REALM_REWARD);

        if (darkGateModel.getActiveEvent(EDarkGateEvent.Dark_Realm) != null) {
            getDarkRealmModel().updateRank(uid, point, darkGateHandler.getParentExtension().getParentZone());
        }

        //save log
        DarkRealmLogsModel darkRealmLogsModel = DarkRealmLogsModel.copyFromDBtoObject(uid, darkGateHandler.getParentExtension().getParentZone());
        if (darkRealmLogsModel != null) {
            darkRealmLogsModel.push(Utils.getTimestampInSecond(), point, darkGateHandler.getParentExtension().getParentZone());
        }

        //Event
        GameEventAPI.ariseGameEvent(EGameEvent.DO_GUILD_HUNT, uid, new HashMap<>(), darkGateHandler.getParentExtension().getParentZone());
    }

    /**
     * lấy thông tin rank của mình
     *
     * @param user
     */
    public void getDarkRealmMyRank(User user) {
//        onEndWeek();
        if (getState() == EDarkGateState.END_WEEK || !darkGateModel.containsEventID(EDarkGateEvent.Dark_Realm.id))
            return;

        //sự kiện vẫn đang diễn ra
        UserModel userModel = ((ZoneExtension) darkGateHandler.getParentExtension()).getUserManager().getUserModel(user);
        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(userModel.userID, darkGateHandler.getParentExtension().getParentZone());
        DarkRealmModel darkRealmModel = getDarkRealmModel();
        Zone zone = darkGateHandler.getParentExtension().getParentZone();
//        Jedis jedis = ((ZoneExtension) zone.getExtension()).getRedisController().getJedis();
        UserManager userManager = ((ZoneExtension) zone.getExtension()).getUserManager();
        RankLeagueDBO rankLeagueDBO = RankDAO.getRankLeague(zone, userModel.userID);
        int leagueId = rankLeagueDBO != null ? rankLeagueDBO.leagueId : -1;
        RankDarkrealmModel rankDarkrealmModel = LeagueManager.getInstance().getRankDarkrealmModel(leagueId, zone);
//        List<RankDarkrealmUserDBO> rankUser = RankDAO.getListRankDarkrealm(zone, userModel.userID);
        Set<String> gids = new HashSet<>(); //jedis.zrevrange("darkrealm_leaderboard_guild_" + darkRealmModel.hash, 0, 100);

        SendDarkRealmMyRank send = new SendDarkRealmMyRank();
        send.rankLeagueDBO = rankLeagueDBO;
        List<SoloRankItemVO> soloRanks = rankDarkrealmModel.rank.stream().map(index -> {
            UserModel um = userManager.getUserModel(index.uid);
            int level = BagManager.getInstance().getLevelUser(index.uid, zone);
            int power = HeroManager.getInstance().getPower(index.uid, zone);
            long point = index.score;
            return new SoloRankItemVO(um.userID, um.displayName, um.avatar, um.avatarFrame, level, power, point);
        }).collect(Collectors.toList());
        List<AllianceRankItemVO> allianceRanks = gids.stream().map(gid -> {
            GuildModel guildModel = GuildModel.copyFromDBtoObject(userGuildModel.gid, zone);
            long point = 0; //jedis.zscore("darkrealm_leaderboard_guild_" + darkRealmModel.hash, gid).longValue();
            return new AllianceRankItemVO(guildModel.gId, guildModel.gName, guildModel.readAvatar(), guildModel.readLevel(), point);
        }).collect(Collectors.toList());

        Boolean isSave = false;
        int rankSolo = -1;
        for (int i = 0; i < rankDarkrealmModel.rank.size(); i++) {
            if (userModel.userID == rankDarkrealmModel.rank.get(i).uid) {
                rankSolo = i;
            }
        }
        long rankGuild = 0L; //jedis.zrevrank("darkrealm_leaderboard_guild_" + darkRealmModel.hash, String.valueOf(userModel.userID));
        send.soloRankItemVO = getSoloRankInfo(userModel, soloRanks, isSave);
        send.soloRank = rankSolo + 1;
        if (userGuildModel.inGuild()) {
            send.allianceRankItemVO = getAllianceRankInfo(GuildManager.getInstance().getGuildModel(userGuildModel.gid, darkGateHandler.getParentExtension().getParentZone()), allianceRanks, isSave);
            send.allianceRank = (int) rankGuild + 1;
        }

        if (isSave) darkRealmModel.saveToDB(darkGateHandler.getParentExtension().getParentZone());
        darkGateHandler.send(send, user);
    }

    /**
     * lấy thông tin màn hình EndlessNight
     *
     * @param user
     */
    public void getEndlessNightSceneInfo(User user) {
        if (getState() == EDarkGateState.END_WEEK || !darkGateModel.containsEventID(EDarkGateEvent.Endless_Nights.id))
            return;

        Zone zone = darkGateHandler.getParentExtension().getParentZone();
        UserModel userModel = ((ZoneExtension) darkGateHandler.getParentExtension()).getUserManager().getUserModel(user);

        SendEndlessNightSceneInfo send = new SendEndlessNightSceneInfo();
        send.maxTurn = 2;
        send.remainingTurn = UserDarkGateModel.copyFromDBtoObject(userModel.userID, zone).getEndlessNightTurn(zone);
        darkGateHandler.send(send, user);
    }

    public void getEndlessNightRank(User user) {
        if (getState() == EDarkGateState.END_WEEK || !darkGateModel.containsEventID(EDarkGateEvent.Endless_Nights.id))
            return;

        //sự kiện vẫn đang diễn ra
        UserModel userModel = ((ZoneExtension) darkGateHandler.getParentExtension()).getUserManager().getUserModel(user);
        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(userModel.userID, darkGateHandler.getParentExtension().getParentZone());
        EndlessNightModel endlessNightModel = getEndlessNightModel();
        Zone zone = darkGateHandler.getParentExtension().getParentZone();
//        Jedis jedis = ((ZoneExtension) zone.getExtension()).getRedisController().getJedis();
        UserManager userManager = ((ZoneExtension) zone.getExtension()).getUserManager();
        RankLeagueDBO rankLeagueDBO = RankDAO.getRankLeague(zone, userModel.userID);
        int leagueId = rankLeagueDBO != null ? rankLeagueDBO.leagueId : -1;
        RankEndlessnightModel rankEndlessnightModel = LeagueManager.getInstance().getRankEndlessnightModel(leagueId, zone);
//        List<RankEndlessnightUserDBO> rankUser = RankDAO.getListRankEndlessnight(zone, userModel.userID);
        Set<String> gids = new HashSet<>(); //jedis.zrevrange("endlessnight_leaderboard_guild_" + endlessNightModel.hash, 0, 100);

        SendEndlessNightRank send = new SendEndlessNightRank();
        send.leagueId = rankLeagueDBO == null || rankLeagueDBO.leagueId == null ? -1 : rankLeagueDBO.leagueId;
        send.leagueType = rankLeagueDBO == null || rankLeagueDBO.type == null ? -1 : rankLeagueDBO.type;
        send.leagueName = rankLeagueDBO == null || rankLeagueDBO.name == null ? "" : rankLeagueDBO.name;

        send.soloRanks = rankEndlessnightModel.rank.stream().map(index -> {
            UserModel um = userManager.getUserModel(index.uid);
            int level = BagManager.getInstance().getLevelUser(index.uid, zone);
            int power = HeroManager.getInstance().getPower(index.uid, zone);
            long point = index.score;
            return new SoloRankItemVO(um.userID, um.displayName, um.avatar, um.avatarFrame, level, power, point);
        }).collect(Collectors.toList());
        send.allianceRanks = gids.stream().map(gid -> {
            GuildModel guildModel = GuildModel.copyFromDBtoObject(userGuildModel.gid, zone);
            long point = 0; //jedis.zscore("endlessnight_leaderboard_guild_" + endlessNightModel.hash, gid).longValue();
            return new AllianceRankItemVO(guildModel.gId, guildModel.gName, guildModel.readAvatar(), guildModel.readLevel(), point);
        }).collect(Collectors.toList());

        Boolean isSave = false;
        int rankSolo = -1;
        for (int i = 0; i < rankEndlessnightModel.rank.size(); i++) {
            if (userModel.userID == rankEndlessnightModel.rank.get(i).uid) {
                rankSolo = i;
            }
        }
        Long rankGuild = 0L; //jedis.zrevrank("endlessnight_leaderboard_guild_" + endlessNightModel.hash, String.valueOf(userModel.userID));
        send.soloRankItemVO = getSoloRankInfo(userModel, send.soloRanks, isSave);
        send.soloRank = rankSolo + 1;
        if (userGuildModel.inGuild()) {
            send.allianceRankItemVO = getAllianceRankInfo(GuildManager.getInstance().getGuildModel(userGuildModel.gid, darkGateHandler.getParentExtension().getParentZone()), send.allianceRanks, isSave);
            send.allianceRank = rankGuild == null ? 0 : rankGuild.intValue() + 1;
        }

        if (isSave) endlessNightModel.saveToDB(darkGateHandler.getParentExtension().getParentZone());
        darkGateHandler.send(send, user);
    }

    public void getEndlessNightMyRank(User user) {
        if (getState() == EDarkGateState.END_WEEK || !darkGateModel.containsEventID(EDarkGateEvent.Endless_Nights.id))
            return;

        //sự kiện vẫn đang diễn ra
        UserModel userModel = ((ZoneExtension) darkGateHandler.getParentExtension()).getUserManager().getUserModel(user);
        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(userModel.userID, darkGateHandler.getParentExtension().getParentZone());
        EndlessNightModel endlessNightModel = getEndlessNightModel();
        Zone zone = darkGateHandler.getParentExtension().getParentZone();
//        Jedis jedis = ((ZoneExtension) zone.getExtension()).getRedisController().getJedis();
        UserManager userManager = ((ZoneExtension) zone.getExtension()).getUserManager();
        RankLeagueDBO rankLeagueDBO = RankDAO.getRankLeague(zone, userModel.userID);
        int leagueId = rankLeagueDBO != null ? rankLeagueDBO.leagueId : -1;
        RankEndlessnightModel rankEndlessnightModel = LeagueManager.getInstance().getRankEndlessnightModel(leagueId, zone);
//        List<RankEndlessnightUserDBO> rankUser = RankDAO.getListRankEndlessnight(zone, userModel.userID);
        Set<String> gids = new HashSet<>(); //jedis.zrevrange("endlessnight_leaderboard_guild_" + endlessNightModel.hash, 0, 100);

        SendEndlessNightMyRank send = new SendEndlessNightMyRank();
        send.rankLeagueDBO = rankLeagueDBO;
        List<SoloRankItemVO> soloRanks = rankEndlessnightModel.rank.stream().map(index -> {
            UserModel um = userManager.getUserModel(index.uid);
            int level = BagManager.getInstance().getLevelUser(index.uid, zone);
            int power = HeroManager.getInstance().getPower(index.uid, zone);
            long point = index.score;
            return new SoloRankItemVO(um.userID, um.displayName, um.avatar, um.avatarFrame, level, power, point);
        }).collect(Collectors.toList());
        List<AllianceRankItemVO> allianceRanks = gids.stream().map(gid -> {
            GuildModel guildModel = GuildModel.copyFromDBtoObject(userGuildModel.gid, zone);
            long point = 0; //jedis.zscore("endlessnight_leaderboard_guild_" + endlessNightModel.hash, gid).longValue();
            return new AllianceRankItemVO(guildModel.gId, guildModel.gName, guildModel.readAvatar(), guildModel.readLevel(), point);
        }).collect(Collectors.toList());

        Boolean isSave = false;
        int rankSolo = -1;
        for (int i = 0; i < rankEndlessnightModel.rank.size(); i++) {
            if (userModel.userID == rankEndlessnightModel.rank.get(i).uid) {
                rankSolo = i;
            }
        }
        long rankGuild = 0L; //jedis.zrevrank("endlessnight_leaderboard_guild_" + endlessNightModel.hash, String.valueOf(userModel.userID));
        send.soloRankItemVO = getSoloRankInfo(userModel, soloRanks, isSave);
        send.soloRank = rankSolo + 1;
        if (userGuildModel.inGuild()) {
            send.allianceRankItemVO = getAllianceRankInfo(GuildManager.getInstance().getGuildModel(userGuildModel.gid, darkGateHandler.getParentExtension().getParentZone()), allianceRanks, isSave);
            send.allianceRank = (int) rankGuild + 1;
        }

        if (isSave) endlessNightModel.saveToDB(darkGateHandler.getParentExtension().getParentZone());
        darkGateHandler.send(send, user);
    }

    /**
     * thách đấu chế độ endless night
     *
     * @param user
     * @param uid
     * @param update
     * @param sageSkill
     */
    public void fightEndlessNight(User user, long uid, List<HeroPosition> update, Collection<String> sageSkill) {
        //sự kiện vẫn đang diễn ra
        if (getState() == EDarkGateState.END_WEEK || !darkGateModel.containsEventID(EDarkGateEvent.Endless_Nights.id))
            return;

        UserModel userModel = ((ZoneExtension) darkGateHandler.getParentExtension()).getUserManager().getUserModel(user);
        Zone zone = darkGateHandler.getParentExtension().getParentZone();

        EndlessNightModel endlessNightModel = getEndlessNightModel();
        //update team
        try {
            HeroManager.getInstance().doUpdateTeamHero(zone, uid, ETeamType.DARK_GATE.getId(), update);
        } catch (InvalidUpdateTeamException e) {
            e.printStackTrace();
            return;
        }

        if (!((ZoneExtension) darkGateHandler.getParentExtension()).isTestServer()) { //server test không trừ số lượt
            if (!UserDarkGateModel.copyFromDBtoObject(userModel.userID, zone).canFightEndlessNight(zone)) return;
        }

        //update sage skill
        SageSkillModel sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
        sageSkillModel.updateCurrentSkill(zone, sageSkill);

        //create fighting room
        CreateRoomSettings cfg = new CreateRoomSettings();
        cfg.setName(Utils.ranStr(10));
        cfg.setGroupId(ConfigHandle.instance().get(Params.ENDLESS_NIGHT_GROUP_ID));
        cfg.setMaxUsers(1);
        cfg.setDynamic(true);
        cfg.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);
        cfg.setGame(true);

        Map<Object, Object> props = new HashMap<>();
        props.put(Params.UID, uid);
        props.put(Params.WIN_CONDITIONS, Arrays.asList("WCO001"));

        //Team NPC
        List<ICharacter> npcTeam = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i == 1) {   //vị trí của boss
                npcTeam.add(Mboss.createMBoss(
                        endlessNightModel.readBossID(isChristmasEvent()),
                        endlessNightModel.boosLevel,
                        0,
                        endlessNightModel.bossKingdom,
                        endlessNightModel.readBossElement(isChristmasEvent()),
                        0));
            } else {    //4 slot của creep
                if (isChristmasEvent()) {   //sự kiện christmas
                    CreepVO creepVO = CharactersConfigManager.getInstance().getCreepConfig("M1025");
                    npcTeam.add(Creep.createCreep(creepVO.id,
                            5,
                            creepVO.star,
                            Kingdom.DARK.getId(),
                            creepVO.element,
                            0));
                } else {    //không có sự kiện gì
                    CreepVO creepVO = CharactersConfigManager.getInstance().getCreepConfig(endlessNightCreep.get(Utils.randomInRange(0, endlessNightCreep.size() - 1)));
                    npcTeam.add(Creep.createCreep(creepVO.id,
                            10,
                            creepVO.star,
                            Kingdom.DARK.getId(),
                            TeamUtils.genElement("random"),
                            0));
                }
            }
        }
        props.put(Params.NPC_TEAM, Utils.toJson(npcTeam));

        //hero
        List<Hero> team = Hero.getPlayerTeam(uid, ETeamType.DARK_GATE, zone, false);
        HeroPackage heroPackage = new HeroPackage(team);
        props.put(Params.PLAYER_TEAM, Utils.toJson(heroPackage));
        cfg.setRoomProperties(props);

        //sage
        Sage sage = Sage.createMage(zone, uid);
        props.put(Params.SAGE, Utils.toJson(sage));

        //celestial
        Celestial celestial = Celestial.createCelestial(zone, uid);
        props.put(Params.CELESTIAL, Utils.toJson(celestial));

        cfg.setRoomProperties(props);

        List<RoomVariable> roomVariableList = new ArrayList<>();
        roomVariableList.add(new SFSRoomVariable(Params.TYPE, FightingType.PvM.getType()));
        roomVariableList.add(new SFSRoomVariable(Params.FUNCTION, EFightingFunction.ENDLESS_NIGHT.getIntValue()));
        cfg.setRoomVariables(roomVariableList);

        try {
            FightingCreater.creatorFightingRoom(zone, user, cfg);
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }
    }

    /**
     * trả thưởng chế độ endless night
     *
     * @param uid
     * @param point
     */
    public void complateFightEndlessNight(long uid, long point, long candy) {
        //add reward
        List<ResourcePackage> listReward = new ArrayList<>(DarkGateConfigManager.getInstance().getDarkGateRewardsConfigVO().endlessNights.challenge);
        if (isChristmasEvent()) {
            listReward.add(new ResourcePackage(MoneyType.CANDY_CANE.getId(), caculateCandyCaneEndlessNight(candy)));
        }

        Zone zone = darkGateHandler.getParentExtension().getParentZone();

        if (!UserDarkGateModel.copyFromDBtoObject(uid, zone).fightEndlessNight(zone)) return;

        BagManager.getInstance().addItemToDB(
                listReward,
                uid,
                zone,
                UserUtils.TransactionType.ENDLESS_NIGHT_REWARD);

        if (darkGateModel.getActiveEvent(EDarkGateEvent.Endless_Nights) != null) {
            getEndlessNightModel().updateRank(uid, point, darkGateHandler.getParentExtension().getParentZone());
        }

        //save log
        EndlessNightLogsModel endlessNightLogsModel = EndlessNightLogsModel.copyFromDBtoObject(uid, darkGateHandler.getParentExtension().getParentZone());
        if (endlessNightLogsModel != null) {
            endlessNightLogsModel.push(Utils.getTimestampInSecond(), point, darkGateHandler.getParentExtension().getParentZone());
        }

        //Event
        GameEventAPI.ariseGameEvent(EGameEvent.DO_GUILD_HUNT, uid, new HashMap<>(), darkGateHandler.getParentExtension().getParentZone());
    }

    private static class CandyRate{
        public int point;
        public int rate;

        public CandyRate(int point, int rate) {
            this.point = point;
            this.rate = rate;
        }
    }

    private static int caculateCandyCaneDarkRealm(long point) {
        int candy = 0;
        for(int i = 0; i < candyRates.size(); i++){
            if (point > 0) {
                if (point <= candyRates.get(i).point) {
                    candy += point / candyRates.get(i).rate;
                    point = 0;
                } else {
                    candy += candyRates.get(i).point / candyRates.get(i).rate;
                    point -= candyRates.get(i).point;
                }
            }else {
                break;
            }
        }
        return candy;
    }

    private static int caculateCandyCaneEndlessNight(long point) {
        return Math.toIntExact(point);
    }

    /**
     * lấy lịch sử đánh
     *
     * @param user
     */
    public void getDarkRealmLogs(User user) {
        if (getState() == EDarkGateState.END_WEEK || !darkGateModel.containsEventID(EDarkGateEvent.Dark_Realm.id))
            return;

        UserModel userModel = ((ZoneExtension) darkGateHandler.getParentExtension()).getUserManager().getUserModel(user);
        DarkRealmLogsModel darkRealmLogsModel = DarkRealmLogsModel.copyFromDBtoObject(userModel.userID, darkGateHandler.getParentExtension().getParentZone());

        if (darkRealmLogsModel != null) {
            SendDarkRealmLogs sendDarkRealmLogs = new SendDarkRealmLogs();
            sendDarkRealmLogs.logs = darkRealmLogsModel.logs;
            darkGateHandler.send(sendDarkRealmLogs, user);
        }
    }

    /**
     * lấy lịch sử đánh
     *
     * @param user
     */
    public void getEndlessLogs(User user) {
        if (getState() == EDarkGateState.END_WEEK || !darkGateModel.containsEventID(EDarkGateEvent.Endless_Nights.id))
            return;

        UserModel userModel = ((ZoneExtension) darkGateHandler.getParentExtension()).getUserManager().getUserModel(user);
        EndlessNightLogsModel endlessNightLogsModel = EndlessNightLogsModel.copyFromDBtoObject(userModel.userID, darkGateHandler.getParentExtension().getParentZone());

        if (endlessNightLogsModel != null) {
            SendEndlessNightLogs sendEndlessNightLogs = new SendEndlessNightLogs();
            sendEndlessNightLogs.logs = endlessNightLogsModel.logs;
            darkGateHandler.send(sendEndlessNightLogs, user);
        }
    }

    private DarkRealmModel getDarkRealmModel() {
        ActiveEventVO activeEventVO = darkGateModel.getActiveEvent(EDarkGateEvent.Dark_Realm);
        if (activeEventVO == null) return null;
        return DarkRealmModel.copyFromDBtoObject(activeEventVO.hash, darkGateHandler.getParentExtension().getParentZone());
    }

    private EndlessNightModel getEndlessNightModel() {
        ActiveEventVO activeEventVO = darkGateModel.getActiveEvent(EDarkGateEvent.Endless_Nights);
        if (activeEventVO == null) return null;
        return EndlessNightModel.copyFromDBtoObject(activeEventVO.hash, darkGateHandler.getParentExtension().getParentZone());
    }

    public boolean isChristmasEvent() {
        return !ChristmasEventManager.getInstance().isTimeEndEvent(darkGateHandler.getParentExtension().getParentZone());
    }
}
