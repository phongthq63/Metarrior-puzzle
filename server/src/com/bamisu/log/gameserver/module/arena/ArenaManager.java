package com.bamisu.log.gameserver.module.arena;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.arena.*;
import com.bamisu.log.gameserver.datamodel.arena.entities.RecordArenaInfo;
import com.bamisu.log.gameserver.datamodel.arena.entities.UserArenaInfo;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.arena.cmd.rec.RecChallengePvPOnline;
import com.bamisu.log.gameserver.module.arena.config.*;
import com.bamisu.log.gameserver.module.arena.config.entities.*;
import com.bamisu.log.gameserver.module.arena.exception.*;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.mail.MailUtils;
import com.bamisu.log.gameserver.module.notification.NotificationManager;
import com.bamisu.log.gameserver.module.notification.defind.EActionNotiModel;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import org.apache.log4j.Logger;
//import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArenaManager {
    private ScheduledExecutorService scheduledExecutor;
    private Logger logger = Logger.getLogger("arena");

    private ArenaConfig arenaConfig;
    private FightArenaConfig fightArenaConfig;
    private RewardArenaConfig rewardArenaConfig;
    private RankArenaConfig rankArenaConfig;
    private PvPOnlineConfig pvPOnlineConfig;



    private static ArenaManager ourInstance = new ArenaManager();

    public static ArenaManager getInstance() {
        return ourInstance;
    }

    private ArenaManager() {
        //Load config
        loadConfig();

        scheduledExecutor = LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.ARENA, 1);
    }
    private void loadConfig(){
        arenaConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Arena.FILE_PATH_CONFIG_ARENA), ArenaConfig.class);
        fightArenaConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Arena.FILE_PATH_CONFIG_FIGHT_ARENA), FightArenaConfig.class);
        rewardArenaConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Arena.FILE_PATH_CONFIG_REWARD_ARENA), RewardArenaConfig.class);
        rankArenaConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Arena.FILE_PATH_CONFIG_RANK_ARENA), RankArenaConfig.class);
        pvPOnlineConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Arena.FILE_PATH_CONFIG_PvP_ONLINE), PvPOnlineConfig.class);
    }


    public synchronized void startThreadArena(Zone zone){
        //Schedul daily
        startThreadArenaDaily(zone);
        //Schedul seson
        startThreadArenaSeason(zone);
    }
    private synchronized void startThreadArenaDaily(Zone zone){
        int deltaTime = TimeUtils.getDeltaTimeToTime(ETimeType.NEW_DAY, Utils.getTimestampInSecond());
        logger.info("===== ARENA SCHEDULE DAILY : " + deltaTime);

        scheduledExecutor.scheduleAtFixedRate(() -> {
            try{
                taskStartDaily(zone);
            }catch (Exception e){
                e.printStackTrace();
            }
            }, deltaTime, 86400, TimeUnit.SECONDS);
    }
    private synchronized void startThreadArenaSeason(Zone zone){
        ArenaManagerModel arenaManagerModel = getArenaManagerModel(zone);
        if(arenaManagerModel.readTimeEndSeason() <= 0) taskEndSeason(zone);
        if(arenaManagerModel.readTimeOpenSeason() <= 0) taskStartSeason(zone);

        arenaManagerModel = getArenaManagerModel(zone);
        int deltaTime = TimeUtils.getDeltaTimeToTime(ETimeType.NEW_2_WEEK, arenaManagerModel.timeStamp);
        int time2week = 1209600;
        int time12hour = 43200;
        logger.info("===== ARENA SCHEDULE SEASON : " + deltaTime);

        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                taskEndSeason(zone);
            }catch (Exception e){
                e.printStackTrace();
            }
        }, (deltaTime - time12hour < 0) ? deltaTime + time2week - time12hour + 1 : deltaTime - time12hour + 1, time2week, TimeUnit.SECONDS);
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                taskStartSeason(zone);
            }catch (Exception e){
                e.printStackTrace();
            }
        }, deltaTime + 1, time2week, TimeUnit.SECONDS);
    }


    public synchronized void taskStartDaily(Zone zone){
        //Phan thuong
        sendGiftArenaDaily(zone);
    }
    public synchronized void taskStartSeason(Zone zone){
        ArenaManagerModel arenaManagerModel = getArenaManagerModel(zone);

        //Mo season
        updateArenaManagerOpenSeason(arenaManagerModel, zone);
        //Send notify
        NotificationManager.getInstance().sendNotifyModel(
                new ArrayList<>(ExtensionUtility.getInstance().getZoneManager().getZoneByName(zone.getName()).getUserList()),
                EActionNotiModel.REFRESH,
                Collections.singletonList("arena"),
                zone);

        logger.info("SV : " + zone.getName() + " - ARENA SEASON " + arenaManagerModel.readSeason() + " OPEN : " + arenaManagerModel.readSeason());
    }
    public synchronized void taskEndSeason(Zone zone){
        ArenaManagerModel arenaManagerModel = getArenaManagerModel(zone);

        //Close season
        updateArenaManagerCloseSeason(arenaManagerModel, zone);
        //Phat thuong + save
        sendGiftArenaSeason(zone);
        saveSendGiftSeasonArena(arenaManagerModel, zone);

        logger.info("SV : " + zone.getName() + " - ARENA SEASON " + arenaManagerModel.readSeason() + " CLOSE : " + arenaManagerModel.readSeason());
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------- MANAGER ----------------------------------------------------*/
    /**
     * Arena Manager Model
     * @param zone
     * @return
     */
    public ArenaManagerModel getArenaManagerModel(Zone zone){
        return ArenaManagerModel.copyFromDBtoObject(zone);
//        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getArenaManagerModelCache();
    }


    /**
     * Update
     * @param zone
     * @return
     */
    public boolean updateArenaManagerOpenSeason(Zone zone){
        return updateArenaManagerOpenSeason(getArenaManagerModel(zone), zone);
    }
    public boolean updateArenaManagerOpenSeason(ArenaManagerModel arenaManagerModel, Zone zone){
        return arenaManagerModel.updateOpenSeason(zone);
    }
    public boolean updateArenaManagerCloseSeason(Zone zone){
        return updateArenaManagerCloseSeason(getArenaManagerModel(zone) ,zone);
    }
    public boolean updateArenaManagerCloseSeason(ArenaManagerModel arenaManagerModel, Zone zone){
        return arenaManagerModel.updateCloseSeason(zone);
    }

    public boolean haveSendGiftSeasonArena(Zone zone){
        return getArenaManagerModel(zone).haveSendGift();
    }

    public boolean saveSendGiftSeasonArena(Zone zone){
        return saveSendGiftSeasonArena(getArenaManagerModel(zone), zone);
    }
    public boolean saveSendGiftSeasonArena(ArenaManagerModel arenaManagerModel, Zone zone){
        return getArenaManagerModel(zone).updateSendGift(zone);
    }

    /**
     * Lay mua hien tai
     * @param zone
     * @return
     */
    public int getCurrentSeason(Zone zone){
        return getArenaManagerModel(zone).readSeason();
    }

    /**
     * Kiem tra ket thuc mua
     * @param zone
     * @return
     */
    public boolean isEndSeason(Zone zone){
        return getArenaManagerModel(zone).isSeasonEnd();
    }
    public boolean isOpenSeason(Zone zone){
        return getArenaManagerModel(zone).isSeasonOpen();
    }

    /**
     * Lay time end season
     * @param zone
     * @return
     */
    public int getTimeEndSeason(Zone zone){
        return getTimeEndSeason(getArenaManagerModel(zone));
    }
    public int getTimeEndSeason(ArenaManagerModel arenaManagerModel){
        return arenaManagerModel.readTimeEndSeason();
    }


    /**
     *
     * @param zone
     */
    private synchronized void sendGiftArenaDaily(Zone zone){
        scheduledExecutor.execute(() -> {
            try {
                ArenaManagerModel arenaManagerModel = getArenaManagerModel(zone);

//                Jedis jedis = ((ZoneExtension) zone.getExtension()).getRedisController().getJedis();
//                Set<Long> uids = jedis.zrevrange("arena_leaderboard_" + arenaManagerModel.readSeason(), 0, -1).stream().map(Long::valueOf).collect(Collectors.toSet());
                int userRank;
                Set<Long> uids = new HashSet<>();

                String content = "";
                List<Object> params;
                RankArenaVO rankArenaCf;

                List<ResourcePackage> listGift;
                for(Long uid : uids){
                    int point = 0; // jedis.zscore("arena_leaderboard_" + arenaManagerModel.readSeason(), uid.toString()).intValue();

                    //Lay phan thuong
                    userRank = 0; // jedis.zrevrank("arena_leaderboard_" + arenaManagerModel.readSeason(), uid.toString()).intValue() + 1;
                    listGift = getRewardDailyArenaConfig(userRank, point);

                    //Gui mail
                    params = new ArrayList<>();
                    rankArenaCf = getRankArenaConfig(point);
                    if(userRank > 0){
                        content = "1026";
                        params.add(rankArenaCf.name);
                        params.add(userRank);
                    }else {
                        content = "1025";
                        params.add(rankArenaCf.name);
                    }
                    MailUtils.getInstance().sendMailUser(uid, listGift, "1024", content, params, zone);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    private synchronized void sendGiftArenaSeason(Zone zone){
        if(haveSendGiftSeasonArena(zone)) return;

        ArenaManagerModel arenaManagerModel = getArenaManagerModel(zone);

//        Jedis jedis = ((ZoneExtension) zone.getExtension()).getRedisController().getJedis();
//        Set<Long> uids = jedis.zrevrange("arena_leaderboard_" + arenaManagerModel.readSeason(), 0, -1).stream().map(Long::valueOf).collect(Collectors.toSet());
        Set<Long> uids = new HashSet<>();
        int userRank;

        String content = "";
        List<Object> params;
        RankArenaVO rankArenaCf;

        List<ResourcePackage> listGift;
        for(Long uid : uids){
            int point = 0; // jedis.zscore("arena_leaderboard_" + arenaManagerModel.readSeason(), uid.toString()).intValue();

            //Lay phan thuong
            userRank = 0; // jedis.zrevrank("arena_leaderboard_" + arenaManagerModel.readSeason(), uid.toString()).intValue();
            listGift = getRewardSeasonArenaConfig(userRank, point);

            //Gui mail
            params = new ArrayList<>();
            rankArenaCf = getRankArenaConfig(point);
            if(userRank > 0){
                content = "1029";
                params.add(rankArenaCf.name);
                params.add(userRank);
            }else {
                content = "1028";
                params.add(rankArenaCf.name);
            }

            MailUtils.getInstance().sendMailUser(uid, listGift, "1027", content, params, zone);
        }
    }

    /**
     * Kich hoat nguoi choi
     */
    public void activeUser(UserArenaModel userArenaModel, Zone zone) {
        //Add rank
        if(!updateRankArenaModel(userArenaModel.uid, userArenaModel.readArenaPoint(), zone)){

        }
    }



    /**
     * Hoan thanh tran dau
     * @param zone
     * @param isWin
     * @param uid
     * @param enemy
     */
    public void completeArena(Zone zone, boolean isWin, long uid, long enemy, String hashBattle) {
        if(isEndSeason(zone)) return;

        int updatePointUser = 0;
        int updatePointEnemy = 0;
        long winner = isWin ? uid : enemy;
        UserArenaModel userModel = getUserArenaModel(uid, zone);
        UserArenaModel enemyModel = getUserArenaModel(enemy, zone);

        if(isWin) {
            //Thuong khi thang
            if(!BagManager.getInstance().addItemToDB(getRewardWinArena(), uid, zone, UserUtils.TransactionType.COMPLETE_ARENA)){
                try {
                    throw new RewardWinArenaException();
                } catch (RewardWinArenaException e) {
                    e.printStackTrace();
                }
            }

            updatePointUser = getUpdatePointWinArena(userModel, enemyModel, zone);
            updatePointEnemy = getUpdatePointDefenseArena(userModel, enemyModel, zone);

        }else {
            updatePointUser = 0;
            updatePointEnemy = 2;

        }

        //Update point user + Update point enemy
        if(!updateUserArenaPoint(userModel, updatePointUser, zone) ||
                !updateUserArenaPoint(enemyModel, updatePointEnemy, zone)){
            try {
                throw new UpdateArenaPointException();
            } catch (UpdateArenaPointException e) {
                e.printStackTrace();
            }
        }

        //Update rank
        if(!updateRankArenaModel(userModel.uid, userModel.readArenaPoint(), zone) ||
                !updateRankArenaModel(enemyModel.uid, enemyModel.readArenaPoint(), zone)){
            try {
                throw new UpdateRankArenaException();
            } catch (UpdateRankArenaException e) {
                e.printStackTrace();
            }
        }

        //Refresh doi thu
        refreshListEnemyArena(uid, zone);

        //Luu tran dau
        addRecordArena(uid, RecordArenaInfo.create(hashBattle, uid, enemy, winner, updatePointUser), zone);
        addRecordArena(enemy, RecordArenaInfo.create(hashBattle, enemy, uid, winner, updatePointEnemy), zone);

        //Event
        GameEventAPI.ariseGameEvent(EGameEvent.FINISH_ARENA, uid, new HashMap<>(), zone);
    }



    /*----------------------------------------------------- RANK -----------------------------------------------------*/
    /**
     * Get ranker
     * @param zone
     * @return
     */
    public List<UserArenaInfo> getListTopRankArena(long offset, long limit, Zone zone){
        ArenaManagerModel arenaManagerModel = getArenaManagerModel(zone);
//        Set<String> uids = ((ZoneExtension)zone.getExtension()).getRedisController().getJedis().zrevrange("arena_leaderboard_" + arenaManagerModel.readSeason(), offset, offset * limit + limit);
        Set<String> uids = new HashSet<>();
        Map<Long, UserArenaModel> userArenaModels = getUserArenaModel(uids.stream().map(Long::valueOf).collect(Collectors.toList()), zone);

        List<UserArenaInfo> userArenaInfos = new ArrayList<>();
        long rank = offset * limit;
        for (String uid : uids) {
            rank += 1;
            UserArenaModel userArenaModel = userArenaModels.get(Long.valueOf(uid));
            userArenaInfos.add(UserArenaInfo.create(rank, userArenaModel.uid, userArenaModel.point, userArenaModel.timeStamp));
        }
        return userArenaInfos;
    }

    /**
     * update rank trong season
     * @param uid
     * @param point
     * @param zone
     * @return
     */
    public boolean updateRankArenaModel(long uid, int point, Zone zone){
        try {
            ArenaManagerModel arenaManagerModel = getArenaManagerModel(zone);
//            Jedis jedis = ((ZoneExtension) zone.getExtension()).getRedisController().getJedis();
//            jedis.zincrby("arena_leaderboard_" + arenaManagerModel.readSeason(), point, String.valueOf(uid));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Long> getListUIDArenaDependPoint(int point, Zone zone){
        ArenaManagerModel arenaManagerModel = getArenaManagerModel(zone);
//        Set<String> uids = ((ZoneExtension)zone.getExtension()).getRedisController().getJedis().zrangeByScore("arena_leaderboard_" + arenaManagerModel.readSeason(), point, point);
        Set<String> uids = new HashSet<>();
        List<Long> listUid = uids.stream().map(Long::valueOf).collect(Collectors.toList());
        return listUid;
    }

    /**
     * Tim kiem doi thu
     * @param uid
     * @param zone
     * @return
     */
    public Set<Long> searchEnemyArena(long uid, Zone zone){
        UserArenaModel userArenaModel = getUserArenaModel(uid, zone);
        return searchEnemyArena(userArenaModel.uid, userArenaModel.readArenaPoint(), zone);
    }
    private Set<Long> searchEnemyArena(long uid, int point, Zone zone){
//        Jedis jedis = ((ZoneExtension) zone.getExtension()).getRedisController().getJedis();
        Set<Long> get = new HashSet<>();

//        List<Long> getRange;
        int startPoint;
        int endPoint;
        long uidChoose;

        List<SearchFightArenaVO> searchCf = getEnemyArenaConfig();
        for(SearchFightArenaVO index : searchCf){
            startPoint = point + index.range.get(0);
            endPoint = point + index.range.get(1);

            //Lay toan bo uid trong khoang
//            getRange = jedis.zrevrangeByScore("arena_leaderboard", endPoint, startPoint).stream().map(Long::valueOf).collect(Collectors.toList());
//            getRange.remove(uid);

            //Random theo so luong
//            if(getRange.isEmpty()) continue;
//            if(getRange.size() <= index.count){
//                get.addAll(getRange);
//            }else {
//                for(int i = 0; i < index.count; i++){
//                    uidChoose = getRange.get(Utils.randomInRange(0, getRange.size() - 1));
//                    get.add(uidChoose);
//                    getRange.remove(uidChoose);
//                }
//            }
        }

        get.remove(uid);
        return get;
    }


    /**
     * Tang diem arena
     * @param enemy
     * @param zone
     * @return
     */
    public int getUpdatePointWinArena(UserArenaModel user, UserArenaModel enemy, Zone zone){
        return getUpdatePointWinArena(user.readArenaPoint(), enemy.readArenaPoint());
    }

    public int getUpdatePointDefenseArena(UserArenaModel user, UserArenaModel enemy, Zone zone){
        return getUpdatePointDefenseArena(user.readArenaPoint(), enemy.readArenaPoint());
    }



    /*--------------------------------------------------- USER -------------------------------------------------------*/
    public UserArenaModel getUserArenaModel(long uid, Zone zone){
        UserArenaModel userArenaModel = UserArenaModel.copyFromDBtoObject(uid, zone);
        return userArenaModel;
    }

    public Map<Long, UserArenaModel> getUserArenaModel(List<Long> uids, Zone zone){
        return UserArenaModel.copyFromDBtoObject(uids, zone);
    }

    public UserFightArenaModel getUserFightArenaModel(long uid, Zone zone){
        return UserFightArenaModel.copyFromDBtoObject(uid, zone);
    }

    public UserArenaRecordModel getUserArenaRecordModel(long uid, Zone zone){
        return UserArenaRecordModel.copyFromDBtoObject(uid, zone);
    }



    public List<RecordArenaInfo> getListRecordArena(long uid, Zone zone){
        return getUserArenaRecordModel(uid, zone).readListRecord();
    }
    public boolean addRecordArena(long uid, RecordArenaInfo data, Zone zone){
        return getUserArenaRecordModel(uid, zone).addArenaRecord(data, zone);
    }

    public int getUserArenaPoint(long uid, Zone zone){
        return getUserArenaModel(uid, zone).readArenaPoint();
    }

    public boolean updateUserArenaPoint(UserArenaModel userArenaModel, int point, Zone zone){
        if(point == 0) return true;
        return userArenaModel.increaseArenaPoint(point, zone);
    }

    public List<Long> getListEnemyArena(long uid, Zone zone){
        UserFightArenaModel userFightArenaModel = getUserFightArenaModel(uid, zone);
        return getListEnemyArena(userFightArenaModel, zone);
    }
    public List<Long> getListEnemyArena(UserFightArenaModel userFightArenaModel, Zone zone){
        int seasonCurrent = getCurrentSeason(zone);
        if(userFightArenaModel.saeson != seasonCurrent){
            userFightArenaModel.updateSeason(seasonCurrent, zone);
            refreshListEnemyArena(userFightArenaModel, zone);
        }
        return userFightArenaModel.fight;
    }

    public boolean refreshListEnemyArena(long uid, Zone zone){
        UserFightArenaModel userFightArenaModel = getUserFightArenaModel(uid, zone);
        return refreshListEnemyArena(userFightArenaModel, zone);
    }
    public boolean refreshListEnemyArena(UserFightArenaModel userFightArenaModel, Zone zone){
        userFightArenaModel.fight = searchEnemyArena(userFightArenaModel.uid, zone).parallelStream().
                collect(Collectors.toList());
        return userFightArenaModel.saveToDB(zone);
    }

    public boolean canRefreshListEnemyArena(UserFightArenaModel userFightArenaModel, Zone zone){
        return userFightArenaModel.canRefresh(zone);
    }

    public int getFreeArenaFight(UserArenaModel userArenaModel, Zone zone){
        return userArenaModel.readCountFightFree(zone);
    }

    public boolean increaseArenaFight(UserArenaModel userArenaModel, Zone zone){
        return userArenaModel.fightArena(zone);
    }

    /*------------------------------------------------- CONFIG -------------------------------------------------------*/
    public short getCountRankShow(){
        return arenaConfig.show;
    }

    public List<ResourcePackage> getCostFightArena(){
        return arenaConfig.fight.parallelStream().
                map(ResourcePackage::new).
                collect(Collectors.toList());
    }

    public List<SearchFightArenaVO> getEnemyArenaConfig(){
        return fightArenaConfig.searchFight.parallelStream().map(SearchFightArenaVO::create).collect(Collectors.toList());
    }

    private int getArenaPointWinConfig(int point){
        FightArenaVO cf = fightArenaConfig.readWinConfig(point);
        return (cf != null) ? cf.point : 0;
    }
    private int getUpdatePointWinArena(int atk, int def){
        int bonus = (int) ((def - atk) * 0.05);
        int fixed = getArenaPointWinConfig(atk);
        return (bonus > 0) ? fixed + bonus : fixed;
    }

    private int getArenaPointDefenseConfig(int point){
        FightArenaVO cf = fightArenaConfig.readLoseConfig(point);
        return (cf != null) ? cf.point : 0;
    }
    private int getUpdatePointDefenseArena(int atk, int def){
        int bonus = (int) -((def - atk) * 0.05);
        int fixed = getArenaPointDefenseConfig(def);
        return (bonus < 0) ? fixed + bonus : fixed;
    }


    /**
     * Config phan thuong
     * @return
     */
    public RewardArenaConfig getRewardArenaConfig(){
        return rewardArenaConfig;
    }

    /**
     * Phan thuong thang
     * @return
     */
    public List<ResourcePackage> getRewardWinArena(){
        return getRewardArenaConfig().win.parallelStream().map(ResourcePackage::new).collect(Collectors.toList());
    }

    /**
     * Phan thuong season
     * @return
     */
    public List<RewardRankArenaVO> getRewardSeasonArenaConfig(){
        return getRewardArenaConfig().season;
    }
    public List<ResourcePackage> getRewardSeasonArenaConfig(int rank, int point){
        List<ResourcePackage> listGift = new ArrayList<>();

        for(RewardRankArenaVO index : getRewardSeasonArenaConfig()){
            //Thuong rank
            if(rank <= 0 || index.top.isEmpty()){

            }else {
                if(index.top.size() > 1){
                    if(index.top.get(0) <= rank && rank <= index.top.get(1)) listGift.addAll(index.readReward());
                }else {
                    if(index.top.get(0) == rank) listGift.addAll(index.readReward());
                }
            }
            //Thuong diem
            if(index.range.isEmpty()){

            }else {
                if(index.range.size() > 1){
                    if(index.range.get(0) <= point && point <= index.range.get(1)) listGift.addAll(index.readReward());
                }else {
                    if(index.range.get(0) <= point) listGift.addAll(index.readReward());
                }
            }
        }

        return listGift.parallelStream().
                collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldObj, newObj) -> new ResourcePackage(oldObj.id, oldObj.amount + newObj.amount))).values().parallelStream().
                collect(Collectors.toList());
    }

    /**
     * Phan thuong daily
     * @return
     */
    public List<RewardRankArenaVO> getRewardDailyArenaConfig(){
        return getRewardArenaConfig().daily;
    }
    public List<ResourcePackage> getRewardDailyArenaConfig(int rank, int point){
        List<ResourcePackage> listGift = new ArrayList<>();

        for(RewardRankArenaVO index : getRewardDailyArenaConfig()){
            //Thuong rank
            if(rank <= 0 || index.top.isEmpty()){

            }else {
                if(index.top.size() > 1){
                    if(index.top.get(0) <= rank && rank <= index.top.get(1)) listGift.addAll(index.readReward());
                }else {
                    if(index.top.get(0) == rank) listGift.addAll(index.readReward());
                }
            }
            //Thuong diem
            if(index.range.isEmpty()){

            }else {
                if(index.range.size() > 1){
                    if(index.range.get(0) <= point && point <= index.range.get(1)) listGift.addAll(index.readReward());
                }else {
                    if(index.range.get(0) <= point) listGift.addAll(index.readReward());
                }
            }
        }

        return listGift.parallelStream().
                collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldObj, newObj) -> new ResourcePackage(oldObj.id, oldObj.amount + newObj.amount))).values().parallelStream().
                collect(Collectors.toList());
    }


    public RankArenaVO getRankArenaConfig(int point){
        return rankArenaConfig.readRank(point);
    }

    public BuyArenaVO getBuyArenaFightConfig(){
        return arenaConfig.buyFight;
    }

    public PvPOnlineConfig getPvPOnlineConfig() {
        return pvPOnlineConfig;
    }

    public void challengePvPOnLine(ArenaHandler arenaHandler, User user, ISFSObject data) {
        RecChallengePvPOnline rec = new RecChallengePvPOnline(data);
        long uid = ((ZoneExtension) arenaHandler.getParentExtension()).getUserManager().getUserModel(user).userID;
        try {
            arenaHandler.getPvPOnlineQueue().join(rec.id, uid);
        } catch (BetNotFoundException e) {
            e.printStackTrace();
        } catch (AlreadyOnQueeException e) {
            e.printStackTrace();
        }
    }

    public void createPvPOnlineMatch(ArenaHandler arenaHandler, short bet, long uid1, long uid2) {
        BetVO betVO = ArenaManager.getInstance().getPvPOnlineConfig().readBetVO(bet);
        if(betVO != null){
            boolean changeMoneyResult1 = BagManager.getInstance().addItemToDB(betVO.readMinusResources(), uid1, arenaHandler.getParentExtension().getParentZone(), UserUtils.TransactionType.BET_PVP_ONLINE);
            boolean changeMoneyResult2 = BagManager.getInstance().addItemToDB(betVO.readMinusResources(), uid2, arenaHandler.getParentExtension().getParentZone(), UserUtils.TransactionType.BET_PVP_ONLINE);
            if(changeMoneyResult1 && changeMoneyResult2){

            }
        }
    }
}
