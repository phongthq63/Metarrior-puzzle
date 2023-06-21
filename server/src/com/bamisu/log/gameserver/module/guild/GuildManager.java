package com.bamisu.log.gameserver.module.guild;

import com.bamisu.gamelib.entities.*;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.guild.*;
import com.bamisu.log.gameserver.datamodel.guild.entities.GiftGuildInfo;
import com.bamisu.log.gameserver.datamodel.guild.entities.LogGuildInfo;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.guild.define.EGuildAction;
import com.bamisu.log.gameserver.module.guild.define.EGuildGiftType;
import com.bamisu.log.gameserver.module.guild.define.EGuildSetting;
import com.bamisu.log.gameserver.module.guild.define.EGuildStatus;
import com.bamisu.log.gameserver.module.guild.entities.GuildSearchInfo;
import com.bamisu.log.gameserver.module.guild.config.*;
import com.bamisu.log.gameserver.module.guild.config.entities.*;
import com.bamisu.log.gameserver.module.guild.exception.ErrorExecutionAddRemoveMemberException;
import com.bamisu.log.gameserver.module.guild.exception.UserAlreadyInGuildException;
import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.log.gameserver.module.hunt.config.entities.RewardRateVO;
import com.bamisu.log.gameserver.sql.guild.dao.GuildDAO;
import com.bamisu.gamelib.sql.game.dbo.GuildDBO;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GuildManager {
    private ScheduledExecutorService scheduledExecutor;
    private final Object lockGuild = new Object();

    private GuildConfig guildConfig;
    private AvatarGuildConfig avatarGuildConfig;
    private RewardGuildConfig rewardGuildConfig;
    private RequestGuildConfig requestGuildConfig;
    private ManagerGuildConfig managerGuildConfig;
    private GiftGuildConfig giftGuildConfig;

    private Map<String,LIZRandom> giftRareInstance = new HashMap<>();


    private static GuildManager ourInstance = new GuildManager();

    public static GuildManager getInstance() {
        return ourInstance;
    }

    private GuildManager() {
        //Load config
        loadConfig();

        scheduledExecutor = LizThreadManager.getInstance().getFixExecutorServiceByName("guild", 3);
    }

    private void loadConfig(){
        guildConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Guild.FILE_PATH_CONFIG_GUILD), GuildConfig.class);
        avatarGuildConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Guild.FILE_PATH_CONFIG_AVATAR_GUILD), AvatarGuildConfig.class);
        rewardGuildConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Guild.FILE_PATH_CONFIG_REWARD_GUILD), RewardGuildConfig.class);
        requestGuildConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Guild.FILE_PATH_CONFIG_REQUEST_GUILD), RequestGuildConfig.class);
        managerGuildConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Guild.FILE_PATH_CONFIG_MANAGER_GUILD), ManagerGuildConfig.class);
        giftGuildConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Guild.FILE_PATH_CONFIG_GIFT_GUILD), GiftGuildConfig.class);
    }

    public final String getNameRoomGuild(long gid){
        return "guild".concat(ServerConstant.SEPARATER).concat(String.valueOf(gid));
    }




    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /*------------------------------------------------ MANAGER -------------------------------------------------------*/
    public GuildNameModel getGuildNameModel(String gName, Zone zone){
        return GuildNameModel.copyFromDBtoObject(gName, zone);
    }
    public GuildIDModel getGuildIDModel(String id, Zone zone){
        return GuildIDModel.copyFromDBtoObject(id, zone);
    }

    /**
     * Check name Guild when create Guild
     */
    public boolean checkExsistGuildName(String name, Zone zone){
        GuildNameModel guildNameModel = getGuildNameModel(name, zone);
        return guildNameModel != null && guildNameModel.haveGuild();
    }


    /*------------------------------------------------- SEARCH -------------------------------------------------------*/
    /**
     * Kiem tra thoi gian refresh search guild
     * @param uid
     * @param zone
     * @return
     */
    public boolean canRefreshSearchGuild(long uid, Zone zone){
        return getUserGuildModel(uid, zone).isTimeRefreshSearch(zone);
    }

    /**
     * Tim kiem guild (khong can p trung hoan toan)
     * @param id
     * @param zone
     * @return
     */
    public List<GuildSearchInfo> searchGuildById(String id, Zone zone) {
        List<String> listId = new ArrayList<>();
        if(getGuildModelByID(id, zone) != null){
            listId.add(id);
            return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getGuildSearchInfoCache(listId);
        }
        return new ArrayList<>();
    }

    public List<GuildSearchInfo> searchGuildByName(String name, Zone zone) {
        List<String> listId = new ArrayList<>();
        listId.addAll(GuildDAO.searchGuild(zone, name));

        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getGuildSearchInfoCache(listId);
    }

    /**
     * Search Guild
     */
    public List<GuildSearchInfo> searchGuidModel(String input, Zone zone){
        List<GuildSearchInfo> listMatch = new ArrayList<>();
        //Tim kiem
        listMatch.addAll(searchGuildById(input, zone));
        listMatch.addAll(searchGuildByName(input, zone));
        //Su dung stream de loc Model trung nhau
        // List -> Map -> List
        listMatch = listMatch.stream().
                collect(Collectors.toMap(model -> model.gId, Function.identity(), (oldValue, newValue) -> newValue)).values().stream().
                limit(50).
                collect(Collectors.toList());

        return listMatch;
    }

    /**
     * Get List Guild Model
     */
    public List<GuildSearchInfo> getListGuildModel(Zone zone){
        //Lay toan bo id guild --- 1 list deep copy
        List<String> listChoice = new ArrayList<>();
        List<String> listId = new ArrayList<>(getGuildNotFull(zone));
        if(listId.size() <= 50){
            listChoice.addAll(listId);
        }else {
            int index;
            while (listChoice.size() < 50){
                index = Utils.randomInRange(0, listId.size() - 1);
                listChoice.add(listId.get(index));
                listId.remove(index);
            }
        }
        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getGuildSearchInfoCache(listChoice);
    }



    /*------------------------------------------- GUILD MANAGER STATUS -----------------------------------------------*/
    /**
     * Lay cac guild chua day
     * @param zone
     * @return
     */
    public GuildManagerStatusModel getGuildManagerStatusModel(Zone zone){
        return GuildManagerStatusModel.copyFromDBtoObject(zone);
//        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getGuildManagerStatusModelCache();
    }

    public List<String> getGuildNotFull(Zone zone){
        return new ArrayList<>(getGuildManagerStatusModel(zone).readID());
    }

    public void addIdGuildManagerStatusModel(String gId, Zone zone){
        GuildManagerStatusModel guildManagerStatusModel = getGuildManagerStatusModel(zone);
        if(!guildManagerStatusModel.readID().contains(gId))guildManagerStatusModel.addID(gId, zone);
    }

    public void removeIdGuildManagerStatusModel(String gId, Zone zone){
        GuildManagerStatusModel guildManagerStatusModel = getGuildManagerStatusModel(zone);
        if(guildManagerStatusModel.readID().contains(gId))guildManagerStatusModel.removeID(gId, zone);
    }



    /*-------------------------------------------- CACHE SAVE GUILD --------------------------------------------------*/
    /**
     * Get cache create guild
     * @param zone
     * @return
     */
    public List<GuildDBO> getGuildCacheSaveModel(Zone zone){
        return ((ZoneExtension) zone.getExtension()).getZoneCacheData().getListGuidDBO();
    }

    public void addCacheGuildDBO(GuildDBO guildDBO, Zone zone){
        ((ZoneExtension) zone.getExtension()).getZoneCacheData().addToListGuidDBO(guildDBO);
    }

    public void clearGuildDBOcache(Zone zone){
        ((ZoneExtension) zone.getExtension()).getZoneCacheData().clearListGuidDBO();
    }

    /*---------------------------------------------- GUILD MODEL -----------------------------------------------------*/
    /**
     * Tao guild model
     */
    public final GuildModel createGuildModel(String patternGuild, String symbolGuild, String nameGuild, String description, String verify, String power, String language, UserModel userModel, UserGuildModel userGuildModel, Zone zone){
        synchronized (lockGuild){
            //Tao guild model
            GuildModel guildModel = GuildModel.createGuildModel(patternGuild, symbolGuild, nameGuild, description, verify, power, language, userModel, zone);
            //Lay ra User Guild Model de chinh sua
            userGuildModel.createGuild(guildModel.gId, zone);
            //create cache guild create
            addCacheGuildDBO(GuildDBO.create(guildModel.id, guildModel.gName), zone);
            //Them vao danh sach guild chua full
            scheduledExecutor.execute(() -> {
                addIdGuildManagerStatusModel(guildModel.id, zone);
            });
            return guildModel;
        }
    }

    public boolean removeGuildModel(long gid, Zone zone){
        synchronized (lockGuild){
            GuildModel guildModel = getGuildModel(gid, zone);
            GuildNameModel guildNameModel = getGuildNameModel(guildModel.gName, zone);
            GuildIDModel guildIDModel = getGuildIDModel(guildModel.id, zone);

            return guildModel.deleteFromDB(zone) && guildNameModel.remove(zone) && guildIDModel.remove(zone);
        }
    }

    /**
     * Get guild trong database
     */
    public GuildModel getGuildModel(long gid, Zone zone){
        return GuildModel.copyFromDBtoObject(gid, zone);
    }

    public GuildModel getGuildModelByUserID(long userID, Zone zone){
        UserGuildModel userGuildModel = getUserGuildModel(userID, zone);
        return getGuildModel(userGuildModel.gid, zone);
    }
    public GuildModel getGuildModelByID(String id, Zone zone){
        GuildIDModel guildIDModel = getGuildIDModel(id, zone);
        if(guildIDModel != null && guildIDModel.haveGuild()){
            return getGuildModel(guildIDModel.gId, zone);
        }else {
            return null;
        }
    }
    public GuildModel getGuildModelByName(String name, Zone zone){
        GuildNameModel guildNameModel = getGuildNameModel(name, zone);
        if(guildNameModel != null && guildNameModel.haveGuild()){
            return getGuildModel(guildNameModel.gId, zone);
        }else {
            return null;
        }
    }

    /**
     * Kiem tra quyen han trong guild
     * @param uid
     * @param action
     * @param guildModel
     * @return
     */
    public boolean userHavePermission(long uid, EGuildAction action, GuildModel guildModel){
        return guildModel.havePermission(uid, action);
    }

    /**
     * Lay danh sach don xin vao guild
     */
    public List<Long> getRequestJoinGuild(GuildModel guildModel, Zone zone){
        //Xu ly loai bo cac request user da o trong guild
        Set<Long> setUid = guildModel.readRequestJoinGuild().parallelStream().
                collect(Collectors.toSet());
        Set<Long> setUidNotInGuild = setUid.parallelStream().
                filter(uid -> !getUserGuildModel(uid, zone).inGuild()).
                collect(Collectors.toSet());
        if(setUid.size() != setUidNotInGuild.size()){
            guildModel.updateRequestJoinGuild(setUidNotInGuild);
            guildModel.saveToDB(zone);
        }
        return new ArrayList<>(setUidNotInGuild);
    }

    /**
     * Xu ly xin vao guild
     */
    public boolean removeRequestJoinGuild(long uid, boolean all, GuildModel guildModel, Zone zone){
        if(all){
            return guildModel.removeAllRequestJoinGuild(zone);
        }
        return guildModel.removeRequestJoinGuild(uid, zone);
    }


    /**
     * Setting guild
     * @param setting
     * @param value
     * @param guildModel
     * @param zone
     * @return
     */
    public boolean settingGuild(EGuildSetting setting, long pemission, Object value, long select, GuildModel guildModel, Zone zone){
        if(value == null){
            return false;
        }
        return guildModel.settingGuild(setting, pemission, value, select, zone);
    }

    /**
     * Xem log
     */
    public List<List<LogGuildInfo>> getLogGuildModel(long gid, Zone zone){
        GuildModel guildModel = getGuildModel(gid, zone);
        return getLogGuildModel(guildModel, zone);
    }
    public List<List<LogGuildInfo>> getLogGuildModelByUserID(long uid, Zone zone){
        GuildModel guildModel = getGuildModelByUserID(uid, zone);
        if(guildModel == null) return new ArrayList<>();
        return getLogGuildModel(guildModel, zone);
    }
    public List<List<LogGuildInfo>> getLogGuildModel(GuildModel guildModel, Zone zone){
        return guildModel.readLogGuild(zone);
    }

    public void sendMessageLog(long gid, LogGuildInfo logInfo, Zone zone){
        Room room = zone.getRoomByName(getNameRoomGuild(gid));
        if(room == null) return;

        ISFSObject objPut = new SFSObject();
        objPut.putLong(Params.ID, gid);
        objPut.putUtfString(Params.LOGS, logInfo.id);
        objPut.putUtfStringArray(Params.PARAM, logInfo.param);
        room.getExtension().handleInternalMessage(CMD.InternalMessage.SEND_LOG_GUILD, objPut);
    }




    /*--------------------------------------------- USER GUILD MODEL ------------------------------------------------*/
    /**
     * Get User Guild Model
     */
    public UserGuildModel getUserGuildModel(long uid, Zone zone){
        try{
            return UserGuildModel.copyFromDBtoObject(uid, zone);
//            return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getUserGuildModelCache(uid);
        } catch (ClassCastException e){
            ISFSObject objPut = new SFSObject();
            objPut.putLong(Params.UID, uid);
            ISFSObject objGet = (ISFSObject) zone.getExtension().handleInternalMessage(CMD.InternalMessage.GET_CACHE_USER_GUILD_MODEL, objPut);
            return Utils.fromJson(objGet.getUtfString(Params.DATA), UserGuildModel.class);
        }
    }
    public List<UserGuildModel> getListUserGuildModel(List<Long> listUid, Zone zone){
        List<UserGuildModel> listModel = new ArrayList<>();
        for(long uid : listUid){
            listModel.add(getUserGuildModel(uid, zone));
        }

        return listModel;
    }

    /**
     * kiem tra user trong guild khong
     */
    public boolean userInGuild(long uid, Zone zone){
        UserGuildModel userGuildModel = getUserGuildModel(uid, zone);
        return userInGuild(userGuildModel, zone);
    }
    public boolean userInGuild(UserGuildModel userGuildModel, Zone zone){
        if(!userGuildModel.inGuild()){
            return false;
        }
        GuildModel guildModel = getGuildModel(userGuildModel.gid, zone);
        return userInGuild(userGuildModel, guildModel);
    }
    public boolean userInGuild(UserGuildModel userGuildModel, GuildModel guildModel){
        boolean execute = userGuildModel.inGuild();
        if(execute != guildModel.havaMember(userGuildModel.uid)){
            try {
                throw new ErrorExecutionAddRemoveMemberException();
            } catch (ErrorExecutionAddRemoveMemberException e) {
                e.printStackTrace();
                return false;
            }
        }
        return execute;
    }

    /**
     * Request join guild
     */
    public boolean requestJoinGuildModel(long uid, long gid, Zone zone){
        GuildModel guildModel = getGuildModel(gid, zone);
        return requestJoinGuildModel(uid, guildModel, zone);
    }
    public boolean requestJoinGuildModel(long uid, GuildModel guildModel, Zone zone){
        return guildModel.addRequestJoinGuild(uid, zone);
    }

    /**
     * Join guild
     */
    public boolean userJoinGuildModel(long uid, long gid, Zone zone){
        GuildModel guildModel = getGuildModel(gid, zone);
        UserGuildModel userGuildModel = getUserGuildModel(uid, zone);
        return userJoinGuildModel(userGuildModel, guildModel, zone);
    }
    public boolean userJoinGuildModel(UserGuildModel userGuildModel, GuildModel guildModel, Zone zone){
        if(userInGuild(userGuildModel, guildModel)){
            try {
                throw new UserAlreadyInGuildException();
            } catch (UserAlreadyInGuildException e) {
                e.printStackTrace();
                return false;
            }
        }
        if(!userGuildModel.canJoinGuild()){
            return false;
        }
        if(guildModel.addMember(userGuildModel.uid, zone) &&
                userGuildModel.joinGuild(guildModel.gId, zone)){
            //Refresh cache
            ((ZoneExtension)zone.getExtension()).getZoneCacheData().refreshCache(ECacheType.GUILD_SEARCH_INFO, guildModel.id);
            //Event
            GameEventAPI.ariseGameEvent(EGameEvent.JOIN_GUILD, userGuildModel.uid, new HashMap<>(), zone);
            //Xoa khoi ds guild chua full
            scheduledExecutor.execute(() -> {
                if(guildModel.isGuildFull(0))removeIdGuildManagerStatusModel(guildModel.id, zone);
            });

            return true;
        }
        return false;
    }


    public List<Long> userJoinGuildModel(List<UserGuildModel> listUserGuildModel, GuildModel guildModel, Zone zone) {
        //Lay list vua du vao guild (config - member hien tai)
        //Ket hop loc nhung user co the vao guild
        List<UserGuildModel> listUserJoinGuild =
                listUserGuildModel.parallelStream().
                        filter(obj -> obj.canJoinGuild()).
                        limit(getGuildConfig(guildModel.readLevel()).member - guildModel.member.size()).
                        collect(Collectors.toList());
        //Lay list uid tu list model vua loc ra
        List<Long> listUid = listUserJoinGuild.parallelStream().map(obj -> obj.uid).collect(Collectors.toList());
        //Add member
        if (guildModel.addMember(listUid, zone)) {
            for (UserGuildModel userGuildModel : listUserJoinGuild) {
                userGuildModel.joinGuild(guildModel.gId, zone);
            }
            //Xoa khoi ds nu guild full
            scheduledExecutor.execute(() -> {
                if(guildModel.isGuildFull(0))removeIdGuildManagerStatusModel(guildModel.id, zone);
            });
            return listUid;
        }
        return new ArrayList<>();
    }


    /**
     * User leave guild
     */
    public boolean leaveGuild(long uid, Zone zone, boolean isKick){
        UserGuildModel userGuildModel = getUserGuildModel(uid, zone);
        if(!userGuildModel.inGuild()) return false;

        GuildModel guildModel = getGuildModel(userGuildModel.gid, zone);
        boolean result = guildModel.removeMember(uid, zone, isKick) && userGuildModel.outGuild(zone);
        if(result) {
            //Them vao ds guild chua full
            scheduledExecutor.execute(() -> {
                if(guildModel.member.isEmpty()){
                    //Guild ko member -> guild se bi delete
                    removeIdGuildManagerStatusModel(guildModel.id, zone);
                    addCacheGuildDBO(GuildDBO.create(guildModel.id, guildModel.gName, EGuildStatus.REMOVE.getId()), zone);
                }else {
                    //TH co thanh vien
                    GuildVO guildCf = getGuildConfig(guildModel.readLevel());
                    if(guildModel.member.size() == guildCf.member){
                        //Full member
                        removeIdGuildManagerStatusModel(guildModel.id, zone);
                    }else {
                        //Ko full
                        addIdGuildManagerStatusModel(guildModel.id, zone);
                    }
                }
            });
            //Refresh guild search
            ((ZoneExtension)zone.getExtension()).getZoneCacheData().refreshCache(ECacheType.GUILD_SEARCH_INFO, guildModel.id);
        }


        return result;
    }

    /**
     * Check check in
     */
    public boolean haveCheckInGuild(long uid, Zone zone){
        return haveCheckInGuild(getUserGuildModel(uid, zone), zone);
    }
    public boolean haveCheckInGuild(UserGuildModel userGuildModel, Zone zone){
        return userGuildModel.haveCheckIn(zone);
    }

    /**
     * Check in
     */
    public boolean checkInGuild(long uid, Zone zone){
        return checkInGuild(getUserGuildModel(uid, zone), zone);
    }
    public boolean checkInGuild(UserGuildModel userGuildModel, Zone zone){
        return userGuildModel.checkIn(zone);
    }

    public boolean changeResourceGuildModel(long gid, List<ResourcePackage> listResource, Zone zone){
        return changeResourceGuildModel(getGuildModel(gid, zone), listResource, zone);
    }
    public boolean changeResourceGuildModel(GuildModel guildModel, List<ResourcePackage> listResource, Zone zone){
        return guildModel.updateResourceGuild(listResource, zone);
    }


    public List<GiftGuildInfo> getListGiftSave(long uid, Zone zone){
        UserGuildModel userGuildModel = getUserGuildModel(uid, zone);
        return userGuildModel.readListGiftSaveGuild(zone);
    }
    public Set<String> getListGiftClaimed(long uid, Zone zone){
        UserGuildModel userGuildModel = getUserGuildModel(uid, zone);
        return getListGiftClaimed(userGuildModel, zone);
    }
    public Set<String> getListGiftClaimed(UserGuildModel userGuildModel, Zone zone){
        return userGuildModel.readListGiftClaimedGuild(zone);
    }

    public List<GiftGuildInfo> getListGiftGuildUser(long uid, Zone zone){
        UserGuildModel userGuildModel = getUserGuildModel(uid, zone);
        return getListGiftGuildUser(userGuildModel, zone);
    }
    public List<GiftGuildInfo> getListGiftGuildUser(UserGuildModel userGuildModel, Zone zone){
        List<GiftGuildInfo> listGift = new ArrayList<>();
        if(!userGuildModel.inGuild()) return listGift;

        //Ktra trong guild
        GuildModel guildModel = getGuildModelByUserID(userGuildModel.uid, zone);
        Set<String> hashClaimed = userGuildModel.readListGiftClaimedGuild(zone);

        listGift.addAll(guildModel.readListGiftGuild(zone).stream().
                filter(index -> index.timeStamp + 10 >= userGuildModel.readTimeJoinGuild(zone) &&
                        !hashClaimed.contains(index.hash)).
                collect(Collectors.toList()));
        listGift.addAll(userGuildModel.readListGiftSaveGuild(zone));

        return listGift;
    }
    public GiftGuildInfo getGiftGuildInfo(long uid, String hashGift, Zone zone){
        for(GiftGuildInfo data : getListGiftGuildUser(uid, zone)){
            if(data.hash.equals(hashGift)){
                return data;
            }
        }
        return null;
    }
    public List<GiftGuildInfo> getListGiftGuildUserCanClaim(long uid, Zone zone){
        UserGuildModel userGuildModel = getUserGuildModel(uid, zone);
        if(!userGuildModel.inGuild()) return new ArrayList<>();

        Set<String> hashGiftClaimed = getListGiftClaimed(userGuildModel, zone);     //Gift Guild da nhan
        List<GiftGuildInfo> listGift = getListGiftGuildUser(userGuildModel, zone);
        return listGift.stream().filter(gift -> !hashGiftClaimed.contains(gift.hash)).collect(Collectors.toList());
    }

    /**
     * Nhan gift
     * @param uid
     * @param zone
     * @return
     */
    public boolean canClaimGiftGuildUser(long uid, String hashGift, Zone zone){
        return canClaimGiftGuildUser(getGiftGuildInfo(uid, hashGift, zone));
    }
    public boolean canClaimGiftGuildUser(GiftGuildInfo gift){
        return gift.resources.isEmpty();
    }

    /**
     * Nhan gift
     * @param uid
     * @param gift
     * @param zone
     * @return
     */
    public boolean claimGiftGuildUser(long uid, GiftGuildInfo gift, Zone zone){
        return claimGiftGuildUser(getUserGuildModel(uid, zone), gift, zone);
    }
    public boolean claimGiftGuildUser(UserGuildModel userGuildModel, GiftGuildInfo gift, Zone zone){
        return userGuildModel.claimGiftGuild(gift, zone);
    }

    public boolean addGiftGuildUser(long uid, List<GiftGuildInfo> gift, Zone zone){
        GuildModel guildModel = getGuildModelByUserID(uid, zone);
        if(guildModel == null) return true;
        guildModel.addGiftBuyGuild(gift, zone);
        return guildModel.saveToDB(zone);
    }

    /**
     * remove gift guild
     * @param uid
     * @param hash
     * @param zone
     * @return
     */
    public boolean removeGiftGuildUser(long uid, String hash, Zone zone){
        return removeGiftGuildUser(getUserGuildModel(uid, zone), hash, zone);
    }
    public boolean removeGiftGuildUser(UserGuildModel userGuildModel, String hash, Zone zone){
        return userGuildModel.removeGiftGuild(hash, zone);
    }
    public boolean removeAllGiftGuildUser(long uid, Zone zone){
        return removeAllGiftGuildUser(getUserGuildModel(uid, zone), zone);
    }
    public boolean removeAllGiftGuildUser(UserGuildModel userGuildModel, Zone zone){
        return userGuildModel.removeAllGiftGuild(zone);
    }

    public void onUserLogin(long userID, Zone zone) {
        UserGuildModel userGuildModel = getUserGuildModel(userID, zone);
        if(userGuildModel != null && userGuildModel.inGuild()){
            GuildModel guildModel = getGuildModel(userGuildModel.gid, zone);
            if(guildModel != null){
                synchronized (guildModel){
                    if(guildModel.havePushCache.contains(userID)){
                        guildModel.havePushCache.removeAll(Collections.singletonList(userID));
                        guildModel.saveToDB(zone);
                    }
                }
            }
        }
    }



    /*-------------------------------------------------- CONFIG ------------------------------------------------------*/
    /**
     * Lay tai nguyen tieu ton khi tao guild
     */
    public List<MoneyPackageVO> getResourceCreateGuld(int timeCreate){
        for(int i = guildConfig.create.size() - 1; i >= 0; i--){
            if(guildConfig.create.get(i).count <= timeCreate + 1){
                return guildConfig.create.get(i).cost.parallelStream().collect(Collectors.toList());
            }
        }
        return guildConfig.create.get(0).cost.parallelStream().collect(Collectors.toList());
    }

    /**
     * Guild config theo level
     * @return
     */
    public List<GuildVO> getGuildConfig(){
        return guildConfig.guild;
    }
    public GuildVO getGuildConfig(int level){
        for(int i = guildConfig.guild.size() - 1; i >= 0; i--){
            if(guildConfig.guild.get(i).level <= level){
                return guildConfig.guild.get(i);
            }
        }
        return null;
    }

    /**
     * Guild config theo level
     * @return
     */
    public List<GiftGuildVO> getGiftGuildConfig(){
        return giftGuildConfig.up;
    }
    public GiftGuildVO getGiftGuildConfig(int level){
        for(int i = giftGuildConfig.gift.size() - 1; i >= 0; i--){
            if(giftGuildConfig.up.get(i).level <= level){
                return giftGuildConfig.up.get(i);
            }
        }
        return null;
    }

    /**
     * Lay avatar config
     * @return
     */
    public List<AvatarGuildVO> getPatternGuildConfig(){
        return avatarGuildConfig.listPattern;
    }
    public AvatarGuildVO getPatternGuildConfig(String id){
        for(AvatarGuildVO avatar : getPatternGuildConfig()){
            if(avatar.id.equals(id)){
                return avatar;
            }
        }
        return null;
    }
    public List<AvatarGuildVO> getSymbolGuildConfig(){
        return avatarGuildConfig.listSymbol;
    }
    public AvatarGuildVO getSymbolGuildConfig(String id){
        for(AvatarGuildVO avatar : getSymbolGuildConfig()){
            if(avatar.id.equals(id)){
                return avatar;
            }
        }
        return null;
    }

    public RequestGuildVO getPowerRequestGuildConfig(String id){
        for(RequestGuildVO index : requestGuildConfig.power){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }

    public int getTimeRequestJoinGuild(){
        return guildConfig.leave;
    }

    public ManagerGuildConfig getManagerGuildConfig(){
        return managerGuildConfig;
    }
    public short getTimeThreadUpdateDb(){
        return getManagerGuildConfig().threadSaveTime;
    }
    public short getTimeRefreshSearchGuild(){
        return getManagerGuildConfig().searchTime;
    }

    public List<ResourcePackage> getRewardCheckInConfig(){
        return rewardGuildConfig.checkIn;
    }

    public List<ResourcePackage> getRewardGiftCreateGuild(){
        List<ResourcePackage> reward = new ArrayList<>();
        reward.addAll(giftGuildConfig.create);
        return reward;
    }
    public List<ResourcePackage> getRewardGiftDailyGuild(){
        List<ResourcePackage> reward = new ArrayList<>();
        reward.addAll(giftGuildConfig.daily);
        return reward;
    }

    public GiftBoxGuildVO getGiftBoxGuildConfig(String id){
        for(GiftBoxGuildVO cf : giftGuildConfig.gift){
            if(cf.id.equals(id)){
                return cf;
            }
        }
        return null;
    }
    public List<ResourcePackage> getRewardGiftBoxGuild(String id){
        GiftBoxGuildVO giftBoxGuildCf = getGiftBoxGuildConfig(id);
        List<ResourcePackage> list = new ArrayList<>();
        if(giftBoxGuildCf == null) return list;

        LIZRandom rareRes;
        for(GiftGuildResource giftCf : giftBoxGuildCf.reward){
            if(!giftCf.id.isEmpty()){
                list.add(new ResourcePackage(giftCf.id, giftCf.amount));
            }else {
                rareRes = giftRareInstance.get(giftBoxGuildCf.id);
                if(rareRes == null){
                    rareRes = new LIZRandom();
                    for(RewardRateVO cf : giftCf.gen){
                        rareRes.push(new RandomObj(new ResourcePackage(cf.id, cf.amount), cf.rate));
                    }
                    giftRareInstance.put(giftBoxGuildCf.id, rareRes);
                }

                list.add((ResourcePackage) rareRes.next().value);
            }
        }
        return list;
    }
}
