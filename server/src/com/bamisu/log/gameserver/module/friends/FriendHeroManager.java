package com.bamisu.log.gameserver.module.friends;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.module.friends.entities.config.FriendHeroConfig;
import com.bamisu.log.gameserver.datamodel.friends.UserFriendHeroManagerModel;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.defines.EGiftVip;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.bamisu.log.gameserver.datamodel.friends.FriendHeroModel;
import com.bamisu.log.gameserver.datamodel.friends.FriendModel;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.entities.HeroInfo;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.management.ExtensionZoneManager;
import com.smartfoxserver.v2.entities.Zone;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FriendHeroManager {

    private FriendHeroConfig friendHeroConfig;



    private static FriendHeroManager ourInstance = new FriendHeroManager();

    public static FriendHeroManager getInstance() {
        return ourInstance;
    }

    private FriendHeroManager(){
        //load config
        loadConfig();
    }
    private void loadConfig(){
        friendHeroConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Friend.FILE_PATH_CONFIG_FRIEND_HERO), FriendHeroConfig.class);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public UserFriendHeroManagerModel getUserFriendHeroManagerModel(long uid, Zone zone){
        return UserFriendHeroManagerModel.copyFromDBtoObject(uid, zone);
    }

    public boolean canAssignHeroFriend(long uid, ETeamType teamType, Zone zone) {
        UserFriendHeroManagerModel userFriendHeroManagerModel = getUserFriendHeroManagerModel(uid, zone);

        return userFriendHeroManagerModel.getCountAssignHero(teamType, zone) <
                getCountAssignHeroFriendConfig(teamType) + VipManager.getInstance().getBonus(uid, zone, EGiftVip.MERCENARIES_USER);
    }

    public void updateCountAssignHeroFriend(long uid, ETeamType teamType, boolean status, Zone zone) {
        if (status) {
            UserFriendHeroManagerModel userFriendHeroManagerModel = getUserFriendHeroManagerModel(uid, zone);
            userFriendHeroManagerModel.updateCountAssignHero(teamType, 1, zone);
        }
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    public FriendHeroModel getFriendHeroModel(long uid, Zone zone) {
        return FriendHeroModel.copyFromDBtoObject(uid, zone);
//        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getUserFriendHeroModelCache(uid);
    }

    public List<HeroInfo> getListFriendHero(long uid, Zone zone){
        FriendHeroModel friendHeroModel = getFriendHeroModel(uid, zone);
        FriendModel friendModel = FriendManager.getInstance().getFriendModel(uid, zone);
        List<Long> listId = new ArrayList<>();
        List<HeroInfo> list = new ArrayList<>();

        if (Utils.getTimestampInSecond() - friendHeroModel.time >= 0){
            for (int i = 0; i< friendModel.listFriends.size(); i++){
                listId.add(friendModel.listFriends.get(i).uid);
            }
            friendHeroModel.time = Utils.getTimestampInSecond() + getTimeRefreshCacheHeroFriendModelConfig();
            friendHeroModel.listHero = HeroManager.getInstance().getListHeroFriendAssistant(listId, getMaxHeroAssignConfig(), zone);
            friendHeroModel.saveToDB(zone);

            for (HeroInfo heroInfo: friendHeroModel.readHero()){
                if(heroInfo == null) continue;
                HeroInfo hero = new HeroInfo(heroInfo);
                list.add(hero);
            }

            Logger.getLogger("catch").trace(uid + ": " + "heroFriend - time end - " + Utils.toJson(list));
            return list;
        }

        if (friendHeroModel.readHero().size() == 0){
            for (int i = 0; i< friendModel.listFriends.size(); i++){
                listId.add(friendModel.listFriends.get(i).uid);
            }
            friendHeroModel.listHero = HeroManager.getInstance().getListHeroFriendAssistant(listId, getMaxHeroAssignConfig(), zone);
            friendHeroModel.saveToDB(zone);
            for (HeroInfo heroInfo: friendHeroModel.readHero()){
                HeroInfo hero = new HeroInfo(heroInfo);
                list.add(hero);
            }
            Logger.getLogger("catch").trace(uid + ": " + "heroFriend - size 0 - " + Utils.toJson(list));
            return list;
        }
        for (HeroInfo heroInfo: friendHeroModel.readHero()){
            if(heroInfo == null) continue;
            HeroInfo hero = new HeroInfo(heroInfo);
            list.add(hero);
        }

        Logger.getLogger("catch").trace(uid + ": " + "heroFriend - " + Utils.toJson(list));
        return list;
    }

    public HeroInfo getHeroInfo(String hashHero, long uid, Zone zone){
        for (HeroInfo heroInfo : getListHeroInfo(uid, zone)){
            if (heroInfo.heroModel.hash.equals(hashHero)){
                return heroInfo;
            }
        }

        return null;
    }
    public List<HeroInfo> getHeroInfo(List<String> listHashHero, long uid, Zone zone){
        if(listHashHero.isEmpty()) return new ArrayList<>();

        List<HeroInfo> list = new ArrayList<>();
        for (HeroInfo heroInfo : getListHeroInfo(uid, zone)){
            if(heroInfo == null) continue;
            if (listHashHero.contains(heroInfo.heroModel.hash)){
                list.add(heroInfo);
            }
        }
        return list;
    }

    public List<HeroInfo> getListHeroInfo(long uid, Zone zone){
        FriendHeroModel friendHeroModel = getFriendHeroModel(uid, zone);
        return friendHeroModel.readHero();
    }



    /*------------------------------------------------------- CONFIG -------------------------------------------------*/
    public Map<String,Integer> getCountAssignHeroFriendConfig(){
        return friendHeroConfig.countAssign;
    }
    public int getCountAssignHeroFriendConfig(ETeamType teamType){
        return getCountAssignHeroFriendConfig().getOrDefault(teamType.getId(), 0);
    }

    public String getTimeRefreshCountAssignHeroConfig(){
        return friendHeroConfig.timeAssignRefresh;
    }

    public int getTimeRefreshCacheHeroFriendModelConfig(){
        return friendHeroConfig.cacheRefresh;
    }

    public int getMaxHeroAssignConfig(){
        return friendHeroConfig.maxAssign;
    }
}
