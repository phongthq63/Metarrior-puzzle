package com.bamisu.log.gameserver;


import com.bamisu.gamelib.entities.ECacheType;
import com.bamisu.gamelib.sql.game.dbo.GuildDBO;
import com.bamisu.gamelib.sql.game.dbo.MoneyChangeDBO;
import com.bamisu.log.gameserver.datamodel.IAP.event.IAPEventModel;
import com.bamisu.log.gameserver.datamodel.IAP.home.UserIAPHomeModel;
import com.bamisu.log.gameserver.datamodel.IAP.store.UserIAPStoreModel;
import com.bamisu.log.gameserver.datamodel.ServerVariableModel;
import com.bamisu.log.gameserver.datamodel.arena.ArenaManagerModel;
import com.bamisu.log.gameserver.datamodel.bag.MissionDetailModel;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.bag.entities.MissionDetail;
import com.bamisu.log.gameserver.datamodel.campaign.AFKDetailModel;
import com.bamisu.log.gameserver.datamodel.friends.FriendHeroModel;
import com.bamisu.log.gameserver.datamodel.guild.GuildManagerStatusModel;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.guild.UserGuildModel;
import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserBlessingHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserMainHeroModel;
import com.bamisu.log.gameserver.datamodel.mail.MailAdminModel;
import com.bamisu.log.gameserver.datamodel.mail.MailModel;
import com.bamisu.log.gameserver.datamodel.quest.UserQuestModel;
import com.bamisu.log.gameserver.datamodel.tower.CacheRankTowerModel;
import com.bamisu.log.gameserver.datamodel.tower.TowerManagerModel;
import com.bamisu.log.gameserver.module.adventure.entities.AFKDetail;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.guild.entities.GuildSearchInfo;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ZoneCacheData {

    private final Zone zone;
    private Map<ECacheType, LoadingCache> mapCache = new HashMap<>();
    private List<MoneyChangeDBO> listSaveChangeMoney = new ArrayList<>();
    private List<GuildDBO> listGuidDBO = new ArrayList<>();
    private AFKDetailModel afkDetailModel;    //phục vụ cho việc push notify afk
    private MissionDetailModel missionDetailModel;

    private Object listGuidDBOLock = new Object();


    public ZoneCacheData(Zone zone) {
//        System.gc();
        this.zone = zone;
        CacheLoader<String, ServerVariableModel> loaderDbServerVariable = new CacheLoader<String, ServerVariableModel>() {
            @Override
            public ServerVariableModel load(String id) throws Exception {
                return load(zone);
            }

            private ServerVariableModel load(Zone zone) throws Exception {
                return ServerVariableModel.copyFromDBtoObject(zone);
            }
        };
        this.mapCache.put(ECacheType.SERVER_VARIABLE_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbServerVariable));

        //Cache Hero
        initCacheHero();
        //Cache Bag
        initCacheBag();
        //Cache Friend
        initCacheFriend();
        //Cache Guild
        initCacheGuild();
        //Cache Tower
        initCacheTower();
        //Cache Quest
        initCacheQuest();
        //Cache Mail
        initCacheMail();
        //Cache Arena
        initCacheArena();
        //Cache IAP
        initCacheIAP();
        //afk detail
        afkDetailModel = AFKDetailModel.copyFromDBtoObject(zone);
        // mission
        missionDetailModel = MissionDetailModel.copyFromDBtoObject(zone);
    }

    @WithSpan
    private void initCacheHero() {
        CacheLoader<String, UserAllHeroModel> loaderDbUserAllHero = new CacheLoader<String, UserAllHeroModel>() {
            @Override
            public UserAllHeroModel load(String id) throws Exception {
                return load(Long.parseLong(id), zone);
            }

            private UserAllHeroModel load(long uid, Zone zone) throws Exception {
                return UserAllHeroModel.copyFromDBtoObject(uid, zone);
            }
        };
        CacheLoader<String, UserMainHeroModel> loaderDbUserMainHero = new CacheLoader<String, UserMainHeroModel>() {
            @Override
            public UserMainHeroModel load(String id) throws Exception {
                return load(Long.parseLong(id), zone);
            }

            private UserMainHeroModel load(long uid, Zone zone) throws Exception {
                return UserMainHeroModel.copyFromDBtoObject(uid, zone);
            }
        };
        CacheLoader<String, UserBlessingHeroModel> loaderDbUserBlessingHero = new CacheLoader<String, UserBlessingHeroModel>() {
            @Override
            public UserBlessingHeroModel load(String id) throws Exception {
                return load(Long.parseLong(id), zone);
            }

            private UserBlessingHeroModel load(long uid, Zone zone) throws Exception {
                return UserBlessingHeroModel.copyFromDBtoObject(uid, zone);
            }
        };
        this.mapCache.put(ECacheType.USER_ALL_HERO_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbUserAllHero));
        this.mapCache.put(ECacheType.USER_MAIN_HERO_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbUserMainHero));
        this.mapCache.put(ECacheType.USER_BLESSING_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbUserBlessingHero));
    }

    @WithSpan
    private void initCacheBag() {
        CacheLoader<String, UserBagModel> loaderDbBag = new CacheLoader<String, UserBagModel>() {
            @Override
            public UserBagModel load(String id) throws Exception {
                return load(Long.parseLong(id), zone);
            }

            private UserBagModel load(long uid, Zone zone) throws Exception {
                return UserBagModel.copyFromDBtoObject(uid, zone);
            }
        };
        this.mapCache.put(ECacheType.USER_BAG_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbBag));
    }

    @WithSpan
    private void initCacheFriend() {
        CacheLoader<String, FriendHeroModel> loaderDbFriendHero = new CacheLoader<String, FriendHeroModel>() {
            @Override
            public FriendHeroModel load(String id) throws Exception {
                return load(Long.parseLong(id), zone);
            }

            private FriendHeroModel load(long uid, Zone zone) throws Exception {
                return FriendHeroModel.copyFromDBtoObject(uid, zone);
            }
        };
        this.mapCache.put(ECacheType.USER_FRIEND_HERO_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbFriendHero));
    }

    @WithSpan
    private void initCacheMail() {
        //admin
        CacheLoader<String, MailAdminModel> cacheMailAdminModel = new CacheLoader<String, MailAdminModel>() {
            @Override
            public MailAdminModel load(String id) {
                return load(zone);
            }

            private MailAdminModel load(Zone zone) {
                return MailAdminModel.copyFromDBtoObject(zone);
            }
        };
        this.mapCache.put(ECacheType.MAIL_ADMIN, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(cacheMailAdminModel));

        //user
        CacheLoader<String, MailModel> cacheMailModel = new CacheLoader<String, MailModel>() {
            @Override
            public MailModel load(String id) {
                return load(id, zone);
            }

            private MailModel load(String id, Zone zone) {
                return MailModel.copyFromDBtoObject(Long.valueOf(id), zone);
            }
        };
        this.mapCache.put(ECacheType.MAIL_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(cacheMailModel));
    }

    @WithSpan
    private void initCacheGuild() {
        CacheLoader<String, GuildManagerStatusModel> loaderDbGuildManagerStatus = new CacheLoader<String, GuildManagerStatusModel>() {
            @Override
            public GuildManagerStatusModel load(String id) throws Exception {
                return load(zone);
            }

            public GuildManagerStatusModel load(Zone zone) throws Exception {
                return GuildManagerStatusModel.copyFromDBtoObject(zone);
            }
        };
        CacheLoader<String, UserGuildModel> loaderDbUserGuild = new CacheLoader<String, UserGuildModel>() {
            @Override
            public UserGuildModel load(String id) throws Exception {
                return load(Long.parseLong(id), zone);
            }

            public UserGuildModel load(long uid, Zone zone) throws Exception {
                return UserGuildModel.copyFromDBtoObject(uid, zone);
            }
        };
        CacheLoader<String, GuildSearchInfo> loaderDbGuildSearch = new CacheLoader<String, GuildSearchInfo>() {
            @Override
            /**
             * id = name
             * id = id hien thi
             */
            public GuildSearchInfo load(String id) throws Exception {
                return load(id, zone);
            }

            public GuildSearchInfo load(String id, Zone zone) throws Exception {
                //Guild Model search theo ten hoac id
                GuildModel guildModel = GuildManager.getInstance().getGuildModelByID(id, zone);
                guildModel = (guildModel != null) ? guildModel : GuildManager.getInstance().getGuildModelByName(id, zone);

                return GuildSearchInfo.createGuildSearchInfo(guildModel);
            }
        };
        this.mapCache.put(ECacheType.GUILD_MANAGER_STATUS_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbGuildManagerStatus));
        this.mapCache.put(ECacheType.USER_GUILD_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbUserGuild));
        this.mapCache.put(ECacheType.GUILD_SEARCH_INFO, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbGuildSearch));
    }

    @WithSpan
    private void initCacheTower() {
        CacheLoader<String, TowerManagerModel> loaderDbTowerManager = new CacheLoader<String, TowerManagerModel>() {
            @Override
            public TowerManagerModel load(String id) throws Exception {
                return load(zone);
            }

            public TowerManagerModel load(Zone zone) {
                return TowerManagerModel.copyFromDBtoObject(zone);
            }
        };
        CacheLoader<String, CacheRankTowerModel> loaderDbCacheRankTower = new CacheLoader<String, CacheRankTowerModel>() {
            @Override
            public CacheRankTowerModel load(String id) throws Exception {
                return load(zone);
            }

            public CacheRankTowerModel load(Zone zone) {
                return CacheRankTowerModel.copyFromDBtoObject(zone);
            }
        };
        this.mapCache.put(ECacheType.TOWER_MANAGER_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbTowerManager));
        this.mapCache.put(ECacheType.CACHE_RANK_TOWER_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbCacheRankTower));
    }

    @WithSpan
    private void initCacheQuest() {
        CacheLoader<String, UserQuestModel> loaderDbUserQuest = new CacheLoader<String, UserQuestModel>() {
            @Override
            public UserQuestModel load(String id) throws Exception {
                return load(Long.parseLong(id), zone);
            }

            public UserQuestModel load(long uid, Zone zone) {
                return UserQuestModel.copyFromDBtoObject(uid, zone);
            }
        };
        this.mapCache.put(ECacheType.USER_QUEST_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbUserQuest));
    }

    @WithSpan
    private void initCacheArena() {
        CacheLoader<String, ArenaManagerModel> loaderDbManagerArena = new CacheLoader<String, ArenaManagerModel>() {
            @Override
            public ArenaManagerModel load(String id) throws Exception {
                return load(zone);
            }

            public ArenaManagerModel load(Zone zone) {
                return ArenaManagerModel.copyFromDBtoObject(zone);
            }
        };
        this.mapCache.put(ECacheType.ARENA_MANAGER_MODEL, CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbManagerArena));
    }

    @WithSpan
    private void initCacheIAP() {
        CacheLoader<String, UserIAPStoreModel> loaderDbUserIAPStore = new CacheLoader<String, UserIAPStoreModel>() {
            @Override
            public UserIAPStoreModel load(String id) throws Exception {
                return load(Long.parseLong(id), zone);
            }

            public UserIAPStoreModel load(long uid, Zone zone) {
                return UserIAPStoreModel.copyFromDBtoObject(uid, zone);
            }
        };
        CacheLoader<String, UserIAPHomeModel> loaderDbUserIAPHome = new CacheLoader<String, UserIAPHomeModel>() {
            @Override
            public UserIAPHomeModel load(String id) throws Exception {
                return load(Long.parseLong(id), zone);
            }

            public UserIAPHomeModel load(long uid, Zone zone) {
                return UserIAPHomeModel.copyFromDBtoObject(uid, zone);
            }
        };
        CacheLoader<String, IAPEventModel> loaderDbIAPEvent = new CacheLoader<String, IAPEventModel>() {
            @Override
            public IAPEventModel load(String id) throws Exception {
                return IAPEventModel.copyFromDBtoObject(zone);
            }
        };
        this.mapCache.put(ECacheType.USER_IAP_STORE_MODEL, CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).softValues().build(loaderDbUserIAPStore));
        this.mapCache.put(ECacheType.USER_IAP_HOME_MODEL, CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).softValues().build(loaderDbUserIAPHome));
        this.mapCache.put(ECacheType.IAP_EVENT_MODEL, CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).softValues().build(loaderDbIAPEvent));
    }




    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Get cache
     *
     * @param cacheType
     * @param id
     * @param <T>
     * @return
     */
    @WithSpan
    private <T> T getCache(ECacheType cacheType, long id, Class<T> clas) {
        return getCache(cacheType, String.valueOf(id), clas);
    }

    @WithSpan
    private <T> T getCache(ECacheType cacheType, String id, Class<T> clas) {
        switch (cacheType) {
            case USER_ALL_HERO_MODEL:
            case USER_MAIN_HERO_MODEL:
            case USER_BAG_MODEL:
            case USER_FRIEND_HERO_MODEL:
            case GUILD_MANAGER_MODEL:
            case USER_GUILD_MODEL:
            case TOWER_MANAGER_MODEL:
            case USER_QUEST_MODEL:
            case CACHE_RANK_TOWER_MODEL:
            case GUILD_MANAGER_STATUS_MODEL:
            case MAIL_ADMIN:
            case MAIL_MODEL:
            case GUILD_SEARCH_INFO:
            case POINT_ARENA_MODEL:
            case ARENA_MANAGER_MODEL:
            case SERVER_VARIABLE_MODEL:
            case USER_BLESSING_MODEL:
            case USER_IAP_STORE_MODEL:
            case USER_IAP_HOME_MODEL:
            case IAP_EVENT_MODEL:
                return clas.cast(mapCache.get(cacheType).getUnchecked(id));
        }
        return null;
    }

    @WithSpan
    private <T> List<T> getCache(ECacheType cacheType, List<String> listId, Class<T> clas) {
        switch (cacheType) {
            case USER_ALL_HERO_MODEL:
            case USER_MAIN_HERO_MODEL:
            case USER_BAG_MODEL:
            case USER_FRIEND_HERO_MODEL:
            case GUILD_MANAGER_MODEL:
            case USER_GUILD_MODEL:
            case TOWER_MANAGER_MODEL:
            case USER_QUEST_MODEL:
            case CACHE_RANK_TOWER_MODEL:
            case GUILD_MANAGER_STATUS_MODEL:
            case MAIL_ADMIN:
            case MAIL_MODEL:
            case POINT_ARENA_MODEL:
            case ARENA_MANAGER_MODEL:
            case SERVER_VARIABLE_MODEL:
            case USER_BLESSING_MODEL:
            case USER_IAP_STORE_MODEL:
            case USER_IAP_HOME_MODEL:
            case IAP_EVENT_MODEL:
                try {
                    return (List<T>) mapCache.get(cacheType).getAll(listId);
                } catch (NullPointerException e) {

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case GUILD_SEARCH_INFO:
                List<T> get = new ArrayList<>();
                //TH chuc nang cu~ co' id ko ton tai --> fix chong bug
                listId.stream().filter(id -> GuildManager.getInstance().getGuildModelByID(id, zone) != null).forEach(id -> get.add(getCache(cacheType, id, clas)));
                return get;
        }
        return new ArrayList<>();
    }

    /**
     * Frefresh Cache
     *
     * @param cacheType
     * @param id
     */
    @WithSpan
    public void refreshCache(ECacheType cacheType, String id) {
        if (cacheType == null || id == null) return;
        switch (cacheType) {
            case USER_ALL_HERO_MODEL:
            case USER_MAIN_HERO_MODEL:
            case USER_BAG_MODEL:
            case USER_FRIEND_HERO_MODEL:
            case GUILD_MANAGER_MODEL:
            case USER_GUILD_MODEL:
            case TOWER_MANAGER_MODEL:
            case USER_QUEST_MODEL:
            case CACHE_RANK_TOWER_MODEL:
            case GUILD_MANAGER_STATUS_MODEL:
            case MAIL_ADMIN:
            case MAIL_MODEL:
            case GUILD_SEARCH_INFO:
            case POINT_ARENA_MODEL:
            case ARENA_MANAGER_MODEL:
            case SERVER_VARIABLE_MODEL:
            case USER_BLESSING_MODEL:
            case USER_IAP_STORE_MODEL:
            case USER_IAP_HOME_MODEL:
            case IAP_EVENT_MODEL:
                try {
                    mapCache.get(cacheType).refresh(id);
                } catch (NullPointerException nulle) {
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
//            case GUILD_SEARCH_INFO:
//                mapSaveInstance.get(cacheType).remove(id);
//                break;
        }
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Get cache Guild Manager Model
     *
     * @return
     */
    @WithSpan
    public ServerVariableModel getServerVariableModelCache() {
        return getCache(ECacheType.SERVER_VARIABLE_MODEL, "", ServerVariableModel.class);
    }

    /**
     * Get cache User All Hero Model
     *
     * @param uid
     * @return
     */
    @WithSpan
    public UserAllHeroModel getUserAllHeroModelCache(long uid) {
        return getCache(ECacheType.USER_ALL_HERO_MODEL, uid, UserAllHeroModel.class);
    }

    /**
     * Get cache User Main Hero Model
     *
     * @param uid
     * @return
     */
    @WithSpan
    public UserMainHeroModel getUserMainHeroModelCache(long uid) {
        return getCache(ECacheType.USER_MAIN_HERO_MODEL, uid, UserMainHeroModel.class);
    }

    /**
     * Get cache User Blessing Model
     *
     * @param uid
     * @return
     */
    @WithSpan
    public UserBlessingHeroModel getUserBlessingModelCache(long uid) {
        return getCache(ECacheType.USER_BLESSING_MODEL, uid, UserBlessingHeroModel.class);
    }

    /**
     * Get cache User Bag Model
     *
     * @param uid
     * @return
     */
    @WithSpan
    public UserBagModel getUserBagModelCache(long uid) {
        return getCache(ECacheType.USER_BAG_MODEL, uid, UserBagModel.class);
    }

    /**
     * Get cache User Bag Model
     *
     * @return
     */
    @WithSpan
    public List<MoneyChangeDBO> getMoneyCacheSaveModelCache() {
        return listSaveChangeMoney;
    }

    /**
     * Get cache sql guild dbo
     *
     * @return
     */
    @WithSpan
    public List<GuildDBO> getListGuidDBO() {
        return listGuidDBO;
    }

    /**
     * Get cache sql guild dbo
     *
     * @return
     */
    @WithSpan
    public void addToListGuidDBO(GuildDBO dbo) {
        synchronized (listGuidDBOLock) {
            GuildDBO remove = null;
            for (GuildDBO loopDbo : listGuidDBO) {
                if (loopDbo.name.equalsIgnoreCase(dbo.name)) {
                    remove = loopDbo;
                    break;
                }
            }

            if (remove != null) {
                listGuidDBO.remove(remove);
            }
            listGuidDBO.add(dbo);
        }
    }

    @WithSpan
    public void clearListGuidDBO() {
        synchronized (listGuidDBOLock) {
            listGuidDBO = new ArrayList<>();
        }
    }

    /**
     * Get cache Friend Hero Model
     *
     * @param uid
     * @return
     */
    @WithSpan
    public FriendHeroModel getUserFriendHeroModelCache(long uid) {
        return getCache(ECacheType.USER_FRIEND_HERO_MODEL, uid, FriendHeroModel.class);
    }

    /**
     * Get cache Guild Manager Model
     *
     * @return
     */
    @WithSpan
    public GuildManagerStatusModel getGuildManagerStatusModelCache() {
        return getCache(ECacheType.GUILD_MANAGER_STATUS_MODEL, "", GuildManagerStatusModel.class);
    }

    /**
     * Get cache User Guild Model
     *
     * @param uid
     * @return
     */
    @WithSpan
    public UserGuildModel getUserGuildModelCache(long uid) {
        return getCache(ECacheType.USER_GUILD_MODEL, uid, UserGuildModel.class);
    }

    /**
     * Get cache Guild Search Info
     *
     * @param id
     * @return
     */
    @WithSpan
    public GuildSearchInfo getGuildSearchInfoCache(String id) {
        return getCache(ECacheType.GUILD_SEARCH_INFO, id, GuildSearchInfo.class);
    }

    @WithSpan
    public List<GuildSearchInfo> getGuildSearchInfoCache(List<String> listId) {
        return getCache(ECacheType.GUILD_SEARCH_INFO, listId, GuildSearchInfo.class);
    }

    /**
     * Get cache Tower Manager Model
     *
     * @return
     */
    @WithSpan
    public TowerManagerModel getTowerManagerModelCache() {
        return getCache(ECacheType.TOWER_MANAGER_MODEL, "", TowerManagerModel.class);
    }

    /**
     * Get cache User Quest Model
     *
     * @param uid
     * @return
     */
    @WithSpan
    public UserQuestModel getUserQuestModelCache(long uid) {
        return getCache(ECacheType.USER_QUEST_MODEL, uid, UserQuestModel.class);
    }

    /**
     * Get cache Mail Admin
     */
    @WithSpan
    public MailAdminModel getMailAdminModelCache() {
        return getCache(ECacheType.MAIL_ADMIN, "", MailAdminModel.class);
    }

    /**
     * Get cache Mail user
     */
    @WithSpan
    public MailModel getMailModelCache(String id) {
        return getCache(ECacheType.MAIL_MODEL, id, MailModel.class);
    }

    /**
     * Get cache Arena Manager Model
     *
     * @return
     */
    @WithSpan
    public ArenaManagerModel getArenaManagerModelCache() {
        return getCache(ECacheType.ARENA_MANAGER_MODEL, "", ArenaManagerModel.class);
    }

    /**
     * Get cache User IAP Store Model
     *
     * @return
     */
    @WithSpan
    public UserIAPStoreModel getUserIAPStoreModelCache(long uid) {
        return getCache(ECacheType.USER_IAP_STORE_MODEL, uid, UserIAPStoreModel.class);
    }

    /**
     * Get cache User IAP Store Model
     *
     * @return
     */
    @WithSpan
    public UserIAPHomeModel getUserIAPHomeModelCache(long uid) {
        return getCache(ECacheType.USER_IAP_HOME_MODEL, uid, UserIAPHomeModel.class);
    }

    /**
     * Get cache User IAP Store Model
     *
     * @return
     */
    @WithSpan
    public IAPEventModel getIAPEventModelCache() {
        return getCache(ECacheType.IAP_EVENT_MODEL, "", IAPEventModel.class);
    }



    /*----------------------------------------------------------------------------------------------------------------*/

    @WithSpan
    public Map<Long, AFKDetail> getAFKDetailMap() {
        return afkDetailModel.mapAFKDetail;
    }

    /**
     * gỡ bỏ thông tin afk của user (để k push lại notify)
     * @param afkDetailList
     */
    @WithSpan
    public void removeAFKDetail(List<AFKDetail> afkDetailList) {
        synchronized (afkDetailModel) {
            for (AFKDetail afkDetail : afkDetailList) {
                if (afkDetailModel.mapAFKDetail.containsKey(afkDetail.uid)) {
                    afkDetailModel.mapAFKDetail.remove(afkDetail.uid);
                }
            }
            afkDetailModel.saveToDB(zone);
        }
    }

    /**
     * update toàn bộ thông tin afk
     * @param uid
     * @param rewardTime
     * @param maxTime
     */
    @WithSpan
    public void updateAFKDetail(long uid, int rewardTime, int maxTime) {
        synchronized (afkDetailModel) {
            if (afkDetailModel.mapAFKDetail.containsKey(uid)) {
                afkDetailModel.mapAFKDetail.get(uid).rewardTime = rewardTime;
                afkDetailModel.mapAFKDetail.get(uid).maxTime = maxTime;
            } else {
                afkDetailModel.mapAFKDetail.put(uid, new AFKDetail(uid, rewardTime, maxTime));
            }

            afkDetailModel.saveToDB(zone);
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @WithSpan
    public Map<Long, MissionDetail> getMissionDetailMap() {
        return missionDetailModel.mapMissionDetail;
    }

    @WithSpan
    public void removeMissionDetail(List<MissionDetail> list) {
        synchronized (missionDetailModel) {
            for (MissionDetail missionDetail : list) {
                if (missionDetailModel.mapMissionDetail.containsKey(missionDetail.uid)) {
                    missionDetailModel.mapMissionDetail.remove(missionDetail.uid);
                }
            }
            missionDetailModel.saveToDB(zone);
        }
    }

    @WithSpan
    public void updateMissionDetail(long uid, int lastTime, int maxTime) {
        synchronized (missionDetailModel) {
            if (missionDetailModel.mapMissionDetail.containsKey(uid)) {
                missionDetailModel.mapMissionDetail.get(uid).lastTime = lastTime;
                missionDetailModel.mapMissionDetail.get(uid).maxTime = maxTime;
            } else {
                missionDetailModel.mapMissionDetail.put(uid, new MissionDetail(uid, lastTime, maxTime));
            }
            missionDetailModel.saveToDB(zone);
        }
    }
}
