package com.bamisu.log.gameserver.module.hero;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.module.characters.summon.entities.*;
import com.bamisu.log.gameserver.module.hero.cmd.send.SendRetireHero;
import com.bamisu.log.gameserver.module.hero.define.*;
import com.bamisu.gamelib.item.define.SpecialItem;
import com.bamisu.log.gameserver.module.hero.entities.HeroLogObj;
import com.bamisu.log.gameserver.module.hero.exception.InvalidUpdateTeamException;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.friends.FriendHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.*;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroSlotBlessing;
import com.bamisu.log.gameserver.entities.*;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.blessing.entities.ReduceTimeVO;
import com.bamisu.log.gameserver.module.characters.creep.entities.CreepInstanceVO;
import com.bamisu.log.gameserver.module.characters.creep.entities.CreepVO;
import com.bamisu.log.gameserver.module.characters.hero.entities.CharacterStatsGrowVO;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.characters.mboss.entities.MbossInstanceVO;
import com.bamisu.log.gameserver.module.characters.mboss.entities.MbossVO;
import com.bamisu.log.gameserver.module.characters.star.entities.GraftHeroVO;
import com.bamisu.log.gameserver.module.characters.star.entities.HeroInfoGraftVO;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.friends.FriendHeroManager;
import com.bamisu.log.gameserver.module.hero.cmd.send.SendUpdateTeamHero;
import com.bamisu.log.gameserver.module.hero.entities.HeroInfo;
import com.bamisu.log.gameserver.module.hero.entities.HeroPosition;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.log.gameserver.entities.ColorHero;
import com.bamisu.log.gameserver.module.hero.exception.InvalidFragmentConfigException;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.define.Fragment;
import com.bamisu.gamelib.item.define.HeroResource;
import com.bamisu.gamelib.item.define.ResourceType;
import com.bamisu.gamelib.item.entities.*;
import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.nft.NFTManager;
import com.bamisu.log.gameserver.module.nft.defind.ETokenBC;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HeroManager {

    private Map<String,Stats> heroStatsInstance = new HashMap<>();

    private static HeroManager ourInstance = new HeroManager();

    public static HeroManager getInstance() {
        return ourInstance;
    }

    private HeroManager() {
    }

    public String getKeyStatsHeroCache(String idHero, int star, int level){
        return idHero + ServerConstant.SEPARATER + star + ServerConstant.SEPARATER + level;
    }



    /*---------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Tu dong them day 5 vi tri
     */
    public List<HeroModel> autoFillToMainHeroModel(long uid, ETeamType teamType, Zone zone) {
        List<HeroModel> listTeam = getTeamStrongestUserHeroModel(uid, zone);

        if (!updateUserMainHeroModel(uid, teamType, listTeam.stream().map(obj -> obj.hash).collect(Collectors.toList()), zone)) {
            return null;
        }
        return listTeam;
    }

    /**
     * Get ra 5 hero model luc chien cao nhat (tinh ca ban phuoc)
     *
     * @param uid
     * @param zone
     * @return
     */
    public List<HeroModel> getTeamStrongestUserHeroModel(long uid, Zone zone) {
        return getTeamStrongestUserHeroModel(uid, 5, zone);
    }

    public List<HeroModel> getTeamStrongestUserHeroModel(long uid, int limit, Zone zone) {
        //Clone + blessing hero
        //Map -> lay ra list hero khong trung id
        Set<String> setBlessing = BlessingManager.getInstance().getListHeroSlotBlessing(uid, zone).stream().map(obj -> obj.hashHero).collect(Collectors.toSet());
        short levelBlessing = getUserAllHeroModel(uid, zone).readLevelMin5Hero();
        AtomicInteger levelCf = new AtomicInteger();
//        List<HeroModel> listDulicate = new ArrayList<>(getUserAllListHeroModel(uid, zone).stream().
//                collect(Collectors.toMap(
//                        hero -> hero.id,
//                        hero -> {
//                            HeroModel duplicate = HeroModel.createByHeroModel(hero);
//
//                            if (setBlessing.contains(duplicate.hash)) {
//                                levelCf.set(CharactersConfigManager.getInstance().getMaxLevelBlessingHeroConfig(duplicate.id));
//                                duplicate.level = (levelBlessing > levelCf.get()) ? levelCf.shortValue() : levelBlessing;
//                            }
//                            return duplicate;
//                        },
//                        (oldValue, newValue) -> (getPower(oldValue, zone) > getPower(newValue, zone)) ? oldValue : newValue)
//                ).values());s
        List<HeroModel> listDulicate = getUserAllListHeroModel(uid, zone);
        //Sau do sort
        //Sau do lay ra limit
        return listDulicate.stream().
                sorted((hero1, hero2) -> getPower(hero2, zone) - getPower(hero1, zone)).
                limit(limit).
                collect(Collectors.toList());
    }

    /**
     * Lay ra list hero manh nhat trong list id (moi nguoi nhieu nhat 2 hero)
     * @param listUid
     * @param zone
     * @return
     */
    public List<HeroModel> getListUniqueStrongestUserHeroModel(List<Long> listUid, int limit, Zone zone) {
        try {
            List<HeroModel> listHeroModel = Collections.synchronizedList(new ArrayList<>());
            AtomicReference<Set<String>> listBlessing = new AtomicReference<>();
            AtomicInteger levelBlessing = new AtomicInteger();
            AtomicInteger levelCf = new AtomicInteger();
            listUid.forEach(uid -> {
                //Lay list hash dc ban phuoc
                listBlessing.set(BlessingManager.getInstance().getListHeroSlotBlessing(uid, zone).stream().map(obj -> obj.hashHero).collect(Collectors.toSet()));
                levelBlessing.set(getUserAllHeroModel(uid, zone).readLevelMin5Hero());
                //Add toan bo vs level dc ban phuoc
                listHeroModel.addAll(getAllHeroModel(uid, zone).stream().
                        map(heroModel -> {
                            HeroModel duplicate = HeroModel.createByHeroModel(heroModel);

                            if (listBlessing.get().contains(duplicate.hash)) {
                                levelCf.set(CharactersConfigManager.getInstance().getMaxLevelBlessingHeroConfig(heroModel.id));
                                duplicate.level = (levelBlessing.get() > levelCf.get()) ? levelCf.shortValue() : levelBlessing.shortValue();
                            }

                            return duplicate;
                        }).
                        collect(Collectors.toList()));
            });

            return listHeroModel.stream().
                    collect(Collectors.toMap(hero -> hero.id, hero -> hero, (in, out) -> {
                        if (getPower(getStatsHero(in, zone)) > getPower(getStatsHero(out, zone))) {
                            return in;
                        } else {
                            return out;
                        }
                    })).values().parallelStream().
                    sorted((obj1, obj2) -> getPower(getStatsHero(obj2, zone)) - getPower(getStatsHero(obj1, zone))).
                    limit(limit).
                    collect(Collectors.toList());
        } catch (Exception ex) {
            Logger.getLogger("catch").info("getListUniqueStrongestUserHeroModel\n" + Utils.exceptionToString(ex));
            return new ArrayList<>();
        }
    }

    public List<HeroInfo> getListHeroFriendAssistant(List<Long> listUid, int limit, Zone zone) {
        List<HeroInfo> listHeroInfo = new ArrayList<>();
        getListUniqueStrongestUserHeroModel(listUid, limit, zone).parallelStream().
                forEach(obj -> {

                    if (obj.readLevel() > 1 && obj.star >= ColorHero.YELLOW.getStar()) {
                        listHeroInfo.add(HeroInfo.create(obj, zone));
                    }

                });
        return listHeroInfo;
    }

    public boolean haveHeroFriendAssistantInTeam(long uid, ETeamType teamType, Zone zone){
        List<String> teamHash = getListMainHashHero(zone, uid, teamType, false);
        List<String> userHashHero = getUserAllListHeroModel(uid, zone).stream().
                map(obj -> obj.hash).
                collect(Collectors.toList());
        List<String> friendHashHero = new ArrayList<>(teamHash);
        friendHashHero.removeAll(userHashHero);

        return !friendHashHero.isEmpty();
    }
    public List<HeroInfo> getListHeroFriendAssistantInTeam(long uid, ETeamType teamType, Zone zone) {
        List<String> teamHash = getListMainHashHero(zone, uid, teamType, false);
        List<String> userHashHero = getUserAllListHeroModel(uid, zone).stream().
                map(obj -> obj.hash).
                collect(Collectors.toList());
        List<String> friendHashHero = new ArrayList<>(teamHash);
        friendHashHero.removeAll(userHashHero);

        if(friendHashHero.size() > 0){
            return FriendHeroManager.getInstance().getHeroInfo(friendHashHero, uid, zone);
        }else {
            return new ArrayList<>();
        }
    }

    /**
     * Lay ra 5 hero co level cao nhat
     *
     * @param uid
     * @param zone
     * @return
     */
    public List<HeroModel> getTeamLeveHighestUserHeroModel(long uid, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        return getTeamLeveHighestUserHeroModel(userAllHeroModel, zone);
    }

    public List<HeroModel> getTeamLeveHighestUserHeroModel(UserAllHeroModel userAllHeroModel, Zone zone) {
        return getHeroModel(userAllHeroModel.uid, new ArrayList<>(getTeamHeroLevelHighest(userAllHeroModel, zone)), zone);
    }


    /**
     * Lay tong luc chien nguoi choi
     *
     * @param uid
     * @param zone
     * @return
     */
    public int getPower(long uid, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        Set<String> listBlessing = BlessingManager.getInstance().getListHeroSlotBlessing(uid, zone).stream().map(obj -> obj.hashHero).collect(Collectors.toSet());

        List<HeroModel> list = new ArrayList<>();
        list.addAll(getUserAllListHeroModel(uid, zone));
        list.addAll(getListBlockedHeroModel(uid, zone));

        List<HeroModel> chose = list.parallelStream().
                map(obj -> {
                    obj = HeroModel.createByHeroModel(obj);

                    if (listBlessing.contains(obj.hash))
                        obj.level = (short) BlessingManager.getInstance().getLevelBlessingHero(userAllHeroModel, obj.id);

                    return obj;
                }).
                filter(obj -> obj.readLevel() > 1).
                collect(Collectors.toList());

        return getPower(chose, zone);
    }

    public int getTeamPower(long uid, ETeamType teamType, Zone zone) {
        return getTeamPower(uid, teamType, zone, false);
    }

    public int getTeamPower(long uid, ETeamType teamType, Zone zone, boolean autoFill) {
        List<HeroModel> listHeroModel = getUserMainListHeroModel(uid, teamType, zone, autoFill);

        return getPower(listHeroModel, zone);
    }



    /*---------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------HERO BAG--------------------------------------------------------------*/

    /**
     * Get UserAllHeroModel
     * update breed status of user
     */
    public UserAllHeroModel getUserAllHeroModel(long uid, Zone zone) {
        UserAllHeroModel userAllHeroModel = UserAllHeroModel.copyFromDBtoObject(uid, zone);
//        UserAllHeroModel userAllHeroModel = ((ZoneExtension) zone.getExtension()).getZoneCacheData().getUserAllHeroModelCache(uid);
        boolean isUpdateInfo = false;
        long now = System.currentTimeMillis() / 1000;
        for (HeroModel heroModel : userAllHeroModel.listAllHeroModel) {
            if (heroModel.children.size() > 0) {
                List<HeroModel> children = this.getHeroModel(userAllHeroModel, heroModel.children);
                if (children.size() == 0) {
                    continue;
                }

                int count = 0;
                for (HeroModel child : children) {
                    if (child.timeClaim != null && now >= child.timeClaim) {
                        count++;
                    }
                }

                if (children.size() == count) {
                    heroModel.isBreeding = false;
                    isUpdateInfo = true;
                }
            } else {
                if (heroModel.isBreeding) {
                    heroModel.isBreeding = false;
                    isUpdateInfo = true;
                }
            }
        }

        if (isUpdateInfo) {
            userAllHeroModel.saveToDB(zone);
        }

        return userAllHeroModel;
    }

    /**
     * Add HeroModel
     */
    public boolean addUserAllHeroModel(long uid, List<HeroModel> listHeroModel, Zone zone, boolean sendRetireToPlayer, SendRetireHero sendTireHero) {
        listHeroModel.removeAll(Collections.singleton(null));
        if (listHeroModel.isEmpty()) return false;

        //Add Hero vao tui
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        if (userAllHeroModel.readListHero().size() + listHeroModel.size() > userAllHeroModel.readSizeBagHero(zone)) return false;
        synchronized (userAllHeroModel) {
            List<String> listHashHave = userAllHeroModel.readListHero().stream()
                    .map(heroModel -> heroModel.hash)
                    .collect(Collectors.toList());
            List<HeroModel> duplicateHeroModels = listHeroModel.stream()
                    .filter(heroModel -> listHashHave.contains(heroModel.hash))
                    .collect(Collectors.toList());
            listHeroModel.removeAll(duplicateHeroModels);
            userAllHeroModel.readListHero().addAll(listHeroModel);

            //Add vao khi chua co trong UserHeroCollection
            UserHeroCollectionModel userHeroCollectionModel = getUserHeroCollectionModel(uid, zone);
            addBonusStoryHero(userHeroCollectionModel, listHeroModel.parallelStream().map(obj -> obj.id).collect(Collectors.toList()), zone);

            //Event
            Map<String, Object> data = new HashMap<>();
            data.put(Params.COUNT, listHeroModel.size());
            data.put(Params.STAR, listHeroModel.parallelStream().map(obj -> obj.star).collect(Collectors.toList()));
            GameEventAPI.ariseGameEvent(EGameEvent.GET_HERO, userAllHeroModel.uid, data, zone);

            //Logger
            List<HeroLogObj> heroLogObjs = new ArrayList<>();
            for(HeroModel heroModel : listHeroModel){
                heroLogObjs.add(heroModel.toLogObject());
            }
            Logger.getLogger(HeroManager.class).info("sv:" + ((ZoneExtension) zone.getExtension()).getServerID() + "|" + "uid:" + uid + "|" + "hero:" + Utils.toJson(heroLogObjs));

            //Tu dong phan giai
            if (userAllHeroModel.autoRetire) {
                //Phai create vao data truoc tai handler add hero vs handler retire hero doc lap
                userAllHeroModel.saveToDB(zone);

                //Phan giai
                SendRetireHero tmpSendTireHero = ((HeroHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_HERO)).doRetireHero(
                        userAllHeroModel.uid,
                        listHeroModel.stream().map(obj -> obj.hash).collect(Collectors.toList()),
                        sendRetireToPlayer);
                if(sendTireHero != null && tmpSendTireHero != null){
                    sendTireHero.listEquipment = tmpSendTireHero.listEquipment;
                    sendTireHero.listResource = tmpSendTireHero.listResource;
                }
                return true;
            } else {
                return userAllHeroModel.saveToDB(zone);
            }
        }
    }


    /**
     * Get size bag
     */
    public int getMaxSizeBagListHero(long uid, Zone zone) {
        return getUserAllHeroModel(uid, zone).readSizeBagHero(zone);
    }

    /**
     * Check max Size Bag List HeroVO
     */
    public boolean isMaxSizeBagListHero(long uid, int sum, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        if (userAllHeroModel.readListHero().size() + sum > userAllHeroModel.readSizeBagHero(zone)) {
            return true;
        }
        return false;
    }

    /**
     * Cộng lên
     */
    public boolean upSizeBagListHero(UserAllHeroModel userAllHeroModel, int size, Zone zone) {
        return userAllHeroModel.upSizeBagHero(size, zone);
    }

    /**
     *
     * @param uid
     * @param zone
     * @return
     */
    public Set<String> getTeamHeroLevelHighest(long uid, Zone zone){
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        return getTeamHeroLevelHighest(userAllHeroModel, zone);
    }
    public Set<String> getTeamHeroLevelHighest(UserAllHeroModel userAllHeroModel, Zone zone){
        return userAllHeroModel.readSetLevelHighest(zone);
    }

    /**
     * lay trang thai auto refresh
     */
    public boolean getStatusAutoRetireHero(long uid, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        return userAllHeroModel.autoRetire;
    }

    /**
     * chuyen trang thai auto refresh
     */
    public boolean switchStatusAutoRetireHero(long uid, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        return userAllHeroModel.switchAutoRetire(zone);
    }

    /**
     * Kiem tra lan dau reset hero
     */
    public boolean isFirstResetHero(UserAllHeroModel userAllHeroModel) {
        return userAllHeroModel.isFirstReset();
    }

    public void firstResetHero(UserAllHeroModel userAllHeroModel, Zone zone) {
        userAllHeroModel.firstReset(zone);
    }



    /*---------------------------------------STORY - USER_HERO_COLLECTION_MODEL--------------*/

    /**
     * Get UserHeroCollectionModel
     */
    public UserHeroCollectionModel getUserHeroCollectionModel(long uid, Zone zone) {
        UserHeroCollectionModel userHeroCollectionModel = UserHeroCollectionModel.copyFromDBtoObject(uid, zone);
        if (userHeroCollectionModel == null) {
            userHeroCollectionModel = createUserHeroCollectionModel(uid, new ArrayList<>(), zone);
        }
        return userHeroCollectionModel;
    }

    /**
     * Create UserHeroCollectionModel
     */
    public UserHeroCollectionModel createUserHeroCollectionModel(long uid, List<String> listIdBegin, Zone zone) {
        return UserHeroCollectionModel.createUserHeroCollectionModel(uid, listIdBegin, zone);
    }

    /**
     * Add BonusStoryHero
     */
    public boolean addBonusStoryHero(long uid, String idHero, Zone zone) {
        UserHeroCollectionModel userHeroCollectionModel = getUserHeroCollectionModel(uid, zone);
        return addBonusStoryHero(userHeroCollectionModel, idHero, zone);
    }

    public boolean addBonusStoryHero(UserHeroCollectionModel userHeroCollectionModel, String idHero, Zone zone) {
        return userHeroCollectionModel.addBonusStoryHero(idHero, zone);
    }

    /**
     * Add BonusStoryHero List ID + Ktra trc khi add(Ko phai check)
     *
     * @param uid
     * @param listIdHero
     * @param zone
     * @return
     */
    public boolean addBonusStoryHero(long uid, List<String> listIdHero, Zone zone) {
        UserHeroCollectionModel userHeroCollectionModel = getUserHeroCollectionModel(uid, zone);
        return addBonusStoryHero(userHeroCollectionModel, listIdHero, zone);
    }

    public boolean addBonusStoryHero(UserHeroCollectionModel userHeroCollectionModel, List<String> listIdHero, Zone zone) {
        return userHeroCollectionModel.addBonusStoryHero(listIdHero, zone);
    }

    public List<String> getListHeroCollection(long uid, Zone zone) {
        return new ArrayList<>(getUserHeroCollectionModel(uid, zone).bonusStory.keySet());
    }


    /**
     * Check have collection hero
     */
    private boolean isAlreadyHaveBonusStoryHero(long uid, String idHero, Zone zone) {
        UserHeroCollectionModel userHeroCollectionModel = getUserHeroCollectionModel(uid, zone);
        return isAlreadyHaveBonusStoryHero(userHeroCollectionModel, idHero);
    }

    private boolean isAlreadyHaveBonusStoryHero(UserHeroCollectionModel userHeroCollectionModel, String idHero) {
        return userHeroCollectionModel.isExistBonusStoryHero(idHero);
    }

    /**
     * Get bonus story breakthroughMaterial
     */
    public short getBonusStoryHero(long uid, String idHero, Zone zone) {
        UserHeroCollectionModel userHeroCollectionModel = getUserHeroCollectionModel(uid, zone);
        return userHeroCollectionModel.bonusStory.getOrDefault(idHero, (short) 0);
    }

    /**
     * User bonus
     */
    public boolean useBonusStoryHero(long uid, String idHero, Zone zone) {
        UserHeroCollectionModel userHeroCollectionModel = getUserHeroCollectionModel(uid, zone);
        userHeroCollectionModel.bonusStory.replace(idHero, (short) 0);
        return userHeroCollectionModel.saveToDB(zone);
    }



    /*----------------------------------------TEAM--------------------------------------------*/

    /**
     * Get UserMainHeroModel
     */
    public UserMainHeroModel getUserMainHeroModel(long uid, Zone zone) {
        return UserMainHeroModel.copyFromDBtoObject(uid, zone);
//        return ((ZoneExtension) zone.getExtension()).getZoneCacheData().getUserMainHeroModelCache(uid);
    }


    /**
     * Update UserMainHeroModel
     */
    private boolean updateUserMainHeroModel(long uid, ETeamType teamType, List<String> listHashHero, Zone zone) {
        UserMainHeroModel userMainHeroModel = getUserMainHeroModel(uid, zone);
        return updateUserMainHeroModel(userMainHeroModel, teamType, listHashHero, zone);
    }

    private boolean updateUserMainHeroModel(UserMainHeroModel userMainHeroModel, ETeamType teamType, List<String> listHashHero, Zone zone) {
        return userMainHeroModel.updateHero(teamType, listHashHero, zone);
    }


    private boolean updateTeamHero(long uid, ETeamType teamType, List<HeroPosition> update, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        UserMainHeroModel userMainHeroModel = getUserMainHeroModel(uid, zone);

        return updateTeamHero(userAllHeroModel, userMainHeroModel, teamType, update, zone);
    }

    private boolean updateTeamHero(UserAllHeroModel userAllHeroModel, UserMainHeroModel userMainHeroModel, ETeamType teamType, List<HeroPosition> update, Zone zone) {
        //Kiem tra hash ton tai khong
        if (!haveExsistHeroModel(userAllHeroModel.uid, update.parallelStream().map(obj -> obj.hash).collect(Collectors.toList()), zone)) {
            return false;
        }

        List<HeroPosition> teamUpdate = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            teamUpdate.add(new HeroPosition("", i));
        }
        for (HeroPosition team : teamUpdate) {
            for (int i = 0; i < update.size(); i++) {
                if (update.get(i).position == team.position) {
                    team.hash = update.get(i).hash;
                    update.remove(i);
                    break;
                }
            }
        }


        return updateUserMainHeroModel(
                userMainHeroModel,
                teamType,
                teamUpdate.parallelStream().sorted((obj1, obj2) -> obj1.position - obj2.position).map(obj -> obj.hash).collect(Collectors.toList()), zone);
    }



    /*-------------------------------------BLOCK HERO------------------------------------------*/
    public UserBlockHeroModel getUserBlockHeroModel(long uid, Zone zone) {
        return UserBlockHeroModel.copyFromDBtoObject(uid, zone);
    }

    public List<HeroModel> getListBlockedHeroModel(long uid, Zone zone) {
        return getUserBlockHeroModel(uid, zone).listHeroModel;
    }

    public HeroModel getBlockedHeroModel(long uid, String hashHero, Zone zone) {
        UserBlockHeroModel userBlockHeroModel = getUserBlockHeroModel(uid, zone);
        for (HeroModel herroModel : userBlockHeroModel.listHeroModel) {
            if (herroModel.hash.equals(hashHero)) return herroModel;
        }
        return null;
    }

    public boolean addBlockHeroMode(long uid, HeroModel heroModel, Zone zone) {
        UserBlockHeroModel userBlockHeroModel = getUserBlockHeroModel(uid, zone);
        userBlockHeroModel.listHeroModel.add(heroModel);
        return userBlockHeroModel.saveToDB(zone);
    }

    public HeroModel removeBlockHeroMode(long uid, String hashHero, Zone zone) {
        HeroModel heroGet = null;
        UserBlockHeroModel userBlockHeroModel = getUserBlockHeroModel(uid, zone);
        for (HeroModel heroModel : userBlockHeroModel.listHeroModel) {
            if (heroModel.hash.equals(hashHero)) {
                heroGet = heroModel;
                break;
            }
        }

        if (heroGet != null) {
            userBlockHeroModel.listHeroModel.remove(heroGet);
            userBlockHeroModel.saveToDB(zone);
        }
        return heroGet;
    }

    public HeroModel removeHeroModel(long uid, String hashHero, Zone zone) {
        UserAllHeroModel userAllHeroModel = this.getUserAllHeroModel(uid, zone);
        for (HeroModel heroModel : userAllHeroModel.listAllHeroModel) {
            if (heroModel.hash.equalsIgnoreCase(hashHero)) {
                userAllHeroModel.listAllHeroModel.remove(heroModel);
                userAllHeroModel.saveToDB(zone);
                return heroModel;
            }
        }
        return null;
    }


    /*----------------------------------------HERO---------------------------------------------*/
    /**
     * Get HeroModel
     */
    private HeroModel getAllHeroModel(long uid, String hash, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        return getAllHeroModel(userAllHeroModel, hash);
    }

    private HeroModel getAllHeroModel(UserAllHeroModel userAllHeroModel, String hash) {
        for (HeroModel heroModel : userAllHeroModel.readListHero()) {
            if (heroModel.hash.equals(hash)) {
                return heroModel;
            }
        }
        return null;
    }


    /**
     * Lay hero Model tong quat
     *
     * @param uid
     * @param hash
     * @param zone
     * @return
     */
    public HeroModel getHeroModel(long uid, String hash, Zone zone) {
        return getAllHeroModel(uid, hash, zone);
    }

    public List<HeroModel> getHeroModel(long uid, List<String> hash, Zone zone) {
        List<HeroModel> list = new ArrayList<>();
        HeroModel hero;
        for (String index : hash) {
            hero = getHeroModel(uid, index, zone);
            if (hero != null) {
                list.add(hero);
            }
        }
        return list;
    }

    public List<HeroModel> getHeroModel(UserAllHeroModel userAllHeroModel, List<String> hash) {
        List<HeroModel> list = new ArrayList<>();
        HeroModel hero;
        for (String index : hash) {
            hero = getAllHeroModel(userAllHeroModel, index);
            if (hero != null) {
                list.add(hero);
            }
        }

        return list;
    }

    /**
     * Delete HeroVO Model
     */
    private void deleteMainHeroModel(long uid, List<String> listHash, ETeamType teamType, Zone zone) {
        UserMainHeroModel userMainHeroModel = getUserMainHeroModel(uid, zone);
        deleteMainHeroModel(userMainHeroModel, listHash, teamType, zone);
        userMainHeroModel.saveToDB(zone);
    }

    private void deleteMainHeroModel(UserMainHeroModel userMainHeroModel, List<String> listHash, ETeamType teamType, Zone zone) {
        userMainHeroModel.deleteHero(teamType, listHash, zone);
    }

    public boolean deleteHeroModel(long uid, List<String> listHash, Zone zone) {
        if (listHash.size() <= 0) return true;

        //Xoa trong tui hero
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        UserDeletedHeroModel userDeletedHeroModel = UserDeletedHeroModel.copyFromDBtoObject(uid, zone);
        Iterator<HeroModel> iterator = userAllHeroModel.readListHero().iterator();
        HeroModel heroModel;
        while (iterator.hasNext()) {
            heroModel = iterator.next();

            if(listHash.contains(heroModel.hash)) {
                iterator.remove();
                userDeletedHeroModel.heroModels.add(heroModel);
            }
        }

        //Xoa trong doi hinh
        UserMainHeroModel userMainHeroModel = getUserMainHeroModel(uid, zone);
        for (ETeamType type : ETeamType.values()) {
            deleteMainHeroModel(userMainHeroModel, listHash, type, zone);
        }

        //Xoa trong blessing
        BlessingManager.getInstance().removeHeroBlessing(uid, listHash, zone);

        //Init lai 5 hero cao nhat
        userAllHeroModel.haveRefreshLevelHight = true;
        return userAllHeroModel.saveToDB(zone) && userDeletedHeroModel.saveToDB(zone) && userMainHeroModel.saveToDB(zone);
    }


    /**
     * Get List Hero Model
     */
    public List<HeroModel> getUserMainListHeroModel(long uid, ETeamType teamType, Zone zone, boolean autoFill) {
        UserMainHeroModel userMainHeroModel = getUserMainHeroModel(uid, zone);
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        UserBlessingHeroModel userBlessingHeroModel = BlessingManager.getInstance().getUserBlessingHeroModel(userAllHeroModel.uid, zone);
        Set<String> listBlessing = BlessingManager.getInstance().getListHeroSlotBlessing(userBlessingHeroModel, zone).stream().map(obj -> obj.hashHero).collect(Collectors.toSet());

        FriendHeroModel friendHeroModel = FriendHeroManager.getInstance().getFriendHeroModel(uid, zone);

        return getUserMainListHeroModel(userAllHeroModel, friendHeroModel, listBlessing, getUserMainListHeroModel(userMainHeroModel, teamType, zone, autoFill));
    }
    private List<String> getUserMainListHeroModel(UserMainHeroModel userMainHeroModel, ETeamType teamType, Zone zone, boolean autoFill) {
        return userMainHeroModel.readHero(teamType, zone, autoFill);
    }

    public List<String> getListMainHashHero(Zone zone, long uid, ETeamType teamType, boolean autoFill) {
        UserMainHeroModel userMainHeroModel = getUserMainHeroModel(uid, zone);
        return getUserMainListHeroModel(userMainHeroModel, teamType, zone, autoFill);
    }

    private List<HeroModel> getUserAllListHeroModel(long uid, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        return getUserAllListHeroModel(userAllHeroModel);
    }

    private List<HeroModel> getUserAllListHeroModel(UserAllHeroModel userAllHeroModel) {
        return userAllHeroModel.readListHero();
    }

    private List<HeroModel> getUserAllListHeroModel(long uid, List<String> listHashHero, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        return getUserAllListHeroModel(userAllHeroModel, listHashHero);
    }

    private List<HeroModel> getUserMainListHeroModel(UserAllHeroModel userAllHeroModel, FriendHeroModel friendHeroModel, Set<String> setBlessingHero, List<String> listHashHero) {
        List<HeroModel> listModel = new ArrayList<>();
        HeroModel heroTeam;
        int levelMax;

        for (String hash : listHashHero) {
            if (hash == null || hash.isEmpty()) {
                listModel.add(null);
                continue;
            }
            for (HeroModel heroModel : getUserAllListHeroModel(userAllHeroModel)) {
                if (hash.equals(heroModel.hash)) {
                    heroTeam = HeroModel.createByHeroModel(heroModel);

                    //Neu dc ban phuoc
                    if (setBlessingHero.contains(heroModel.hash)) {
                        levelMax = CharactersConfigManager.getInstance().getMaxLevelBlessingHeroConfig(heroModel.id);
                        heroTeam.level = (userAllHeroModel.readLevelMin5Hero() < levelMax) ? userAllHeroModel.readLevelMin5Hero() : (short) levelMax;
                    }

                    listModel.add(heroTeam);
                    break;
                }
            }
            for (HeroInfo heroInfo : friendHeroModel.readHero()) {
                if (hash.equals(heroInfo.heroModel.hash)) {
                    listModel.add(heroInfo.heroModel);
                    break;
                }
            }
        }
        return listModel;
    }

    private List<HeroModel> getUserAllListHeroModel(UserAllHeroModel userAllHeroModel, List<String> listHashHero) {
        List<HeroModel> listModel = new ArrayList<>();
        for (HeroModel heroModel : userAllHeroModel.readListHero()) {
            if (listModel.size() >= listHashHero.size()) {
                break;
            }
            if (listHashHero.contains(heroModel.hash)) {
                listModel.add(heroModel);
            }
        }
        return listModel;
    }

    public List<HeroModel> getAllHeroModel(long uid, Zone zone) {
        List<HeroModel> listHeroModel = new ArrayList<>();
        listHeroModel.addAll(getUserAllListHeroModel(uid, zone));

        return listHeroModel;
    }

    /**
     * Kiem tra list hash co ton tai khong (chi can 1 cai khong co -> false)
     *
     * @param uid
     * @param listHash
     * @return
     */
    private boolean haveExsistHeroModel(long uid, List<String> listHash, Zone zone) {
        List<String> listHashAllHeroModel = getHeroModel(uid, listHash, zone).parallelStream().map(obj -> obj.hash).collect(Collectors.toList());
        List<HeroInfo> listHeroFriend = new ArrayList<>();
        if(listHashAllHeroModel.size() < listHash.size()){
            listHeroFriend = FriendHeroManager.getInstance().getHeroInfo(listHash, uid, zone);
        }
        return listHash.size() == listHashAllHeroModel.size() + listHeroFriend.size();
    }


    /**
     * Reset HeroModel
     */
    private boolean resetAllHeroModel(long uid, String hash, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        for (HeroModel index : userAllHeroModel.readListHero()) {
            if (index.hash.equals(hash)) {
                index.resetHeroModel();
                userAllHeroModel.haveRefreshLevelHight = true;
                return userAllHeroModel.saveToDB(zone);
            }
        }
        return false;
    }

    public boolean resetHeroModel(long uid, String hash, Zone zone) {
        boolean resulf = resetAllHeroModel(uid, hash, zone);
        if (resulf) {
            //Hero dc ban phuoc khong dc nam trong 5 hero cao nhat
            //Xoa trong blessing
            BlessingManager.getInstance().removeHeroBlessing(uid, new ArrayList<>(getTeamHeroLevelHighest(uid, zone)), zone);
        }
        return resulf;
    }


    public List<String> getQueueResetHero(long uid, Zone zone) {
        return getUserAllHeroModel(uid, zone).queueReset;
    }



    /*-------------------------------------------LEVEL HERO--------------------------------------*/

    /**
     * Max level hero da up
     *
     * @param uid
     * @param zone
     * @return
     */
    public int getMaxLevelUpHeroModel(long uid, Zone zone) {
        return getMaxLevelUpHeroModel(getUserAllHeroModel(uid, zone), zone);
    }

    public int getMaxLevelUpHeroModel(UserAllHeroModel userAllHeroModel, Zone zone) {
        return userAllHeroModel.readMaxLevelUpHero(zone);
    }

    /**
     * Check co phai cap can dot pha khong
     *
     * @param level
     * @return
     */
    public boolean isLevelHeroCanBreakLimit(int level) {
        List<MoneyPackageVO> resourceBreakLimitLevel = CharactersConfigManager.getInstance().getCostBreakLimitLevelHero(level);
        if (resourceBreakLimitLevel.size() <= 0) {
            return false;
        }
        return true;
    }

    /**
     * Get ResourceBreakLimitLevel Update Level HeroVO
     */
    public List<MoneyPackageVO> getCostUpdateLevelHero(int level) {
        if (level < 0) {
            return null;
        }
        List<MoneyPackageVO> resoures = new ArrayList<>();
        //Tai nguyen dot pha
        resoures.addAll(CharactersConfigManager.getInstance().getCostBreakLimitLevelHero(level));
        //Tai nguyen theo cap
        resoures.addAll(CharactersConfigManager.getInstance().getCostUpLevelHeroConfig(level));
        //log
//        logger.info("config: level " + level + " - resource " + Utils.toJson(resoures));

        return resoures.stream().
                filter(Objects::nonNull).
                collect(Collectors.toMap(obj -> obj.id, Function.identity(), (keyOld, keyNew) -> new MoneyPackageVO(keyOld.id, keyOld.amount + keyNew.amount))).values().parallelStream().
                collect(Collectors.toList());
    }

    /**
     * Update Level HeroVO
     */
    private boolean updateLevelAllHeroModel(long uid, String hash, int level, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);

        for (HeroModel heroModel : userAllHeroModel.readListHero()) {
            if (heroModel.hash.equals(hash)) {
                heroModel.level = (short) level;
                return userAllHeroModel.saveToDB(zone);
            }
        }
        return false;
    }

    public boolean updateLevelHeroModel(long uid, String hash, int level, Zone zone) {
        return updateLevelAllHeroModel(uid, hash, level, zone);
    }

    private boolean upLevelAllHeroModel(long uid, String hash, int level, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);

        HeroModel heroModel = getAllHeroModel(userAllHeroModel, hash);
        int levelBefore = heroModel.readLevel();
        for (int i = 0; i < level; i++) {
            heroModel = userAllHeroModel.upLevelHero(hash);
        }

        //Event
        Map<String, Object> data = new HashMap<>();
        data.put(Params.BEFORE, levelBefore);
        data.put(Params.LEVEL, heroModel.readLevel());
        data.put(Params.BLESSING, BlessingManager.getInstance().getLevelBlessingHero(userAllHeroModel, heroModel.id));
        GameEventAPI.ariseGameEvent(EGameEvent.LEVEL_HERO_UPDATE, uid, data, zone);

        //Save
        return userAllHeroModel.saveToDB(zone);
    }

    public boolean upLevelHeroModel(long uid, String hash, int level, Zone zone) {
        return upLevelAllHeroModel(uid, hash, level, zone);
    }

    /*----------------------------------------STAR HERO -------------------------------------------*/

    /**
     * Kiem tra xem hero nap co the up star ko
     *
     * @param uid
     * @param zone
     * @return
     */
    public boolean haveHeroCanUpStar(long uid, Zone zone) {
        List<HeroModel> listHeroData = getAllHeroModel(uid, zone);
        //Phan hero theo sao cho de xu ly
        Map<Short, List<HeroModel>> mapStarHeroData = new HashMap<>();
        for (HeroModel heroModel : listHeroData) {
            if (mapStarHeroData.get(heroModel.star) == null) {
                mapStarHeroData.put(heroModel.star, new ArrayList<>());
            }
            mapStarHeroData.get(heroModel.star).add(heroModel);
        }
        //Kiem tra config
        Map<Short, List<HeroModel>> cloneMapStarHeroData;
        GraftHeroVO graftCf;
        List<HeroInfoGraftVO> listHeroGraftCf;
        List<Boolean> listCheck;
        List<HeroModel> listHeroFissionData;
        HeroVO heroUpStarCf;
        HeroVO heroFissionCf;

        for (HeroModel heroModel : listHeroData) {
            heroUpStarCf = CharactersConfigManager.getInstance().getHeroConfig(heroModel.id);
            if (heroUpStarCf == null) continue;

            //Cost config
            graftCf = CharactersConfigManager.getInstance().getGraftHeroConfig(heroModel.star + 1);
            if (graftCf == null) continue;
            listHeroGraftCf = graftCf.readCostUpdateStarHero();
            if (listHeroGraftCf.isEmpty()) continue;
            listCheck = new ArrayList<>();
            cloneMapStarHeroData = mapStarHeroData.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            for (HeroInfoGraftVO heroGraftCf : listHeroGraftCf) {
                listHeroFissionData = cloneMapStarHeroData.getOrDefault(heroGraftCf.star, new ArrayList<>());

                //Kiem tra chinh xac
                //Star
                for (int i = 0; i < listHeroFissionData.size(); i++) {
                    if(listHeroFissionData.get(i).hash.equals(heroModel.hash)) continue;
                    heroFissionCf = CharactersConfigManager.getInstance().getHeroConfig(listHeroFissionData.get(i).id);

                    //Id
                    if (heroGraftCf.id != null && heroGraftCf.id.equals("same") && !heroUpStarCf.id.equals(heroFissionCf.id)) {
                        continue;
                    }
                    //Kingdom
                    if (heroGraftCf.kingdom != null && heroGraftCf.kingdom.equals("same") && !heroUpStarCf.kingdom.equals(heroFissionCf.kingdom)) {
                        continue;
                    }

                    listCheck.add(true);
                    listHeroFissionData.remove(i);
                    break;
                }
            }

            if (listHeroGraftCf.size() == listCheck.size()) return true;
        }

        return false;
    }

    /**
     * Check Condition Up Star HeroVO
     */
    public boolean checkConditionUpStarHero(long uid, String hashUp, List<String> hashFission, Zone zone) {
        HeroModel heroUp = getHeroModel(uid, hashUp, zone);
        List<HeroModel> heroFission = getHeroModel(uid, hashFission, zone);
        return checkConditionUpStarHero(heroUp, heroFission);
    }

    public boolean checkConditionUpStarHero(HeroModel heroUp, List<HeroModel> heroFission) {
        //Kiem tra up sao dc khong
        HeroVO heroCfUp = CharactersConfigManager.getInstance().getHeroConfig(heroUp.id);
        if (heroCfUp.maxStar == heroUp.star) return false;

        //Lay tai nguyen
        GraftHeroVO graftCf = CharactersConfigManager.getInstance().getGraftHeroConfig(heroUp.star + 1);
        if (graftCf == null) return false;
        List<HeroInfoGraftVO> listResCf = graftCf.readCostUpdateStarHero();

        if (listResCf.size() != heroFission.parallelStream().
                collect(Collectors.toMap(
                        obj -> obj.hash,
                        Function.identity(),
                        (oldValue, newValue) -> newValue)).size())
            return false;

        //Lay config
        //Lay theo model -> list<HeroModel> size = list<HeroVO> size
        List<HeroVO> listFission = CharactersConfigManager.getInstance().getHeroConfig(
                heroFission.parallelStream().map(obj -> obj.id).collect(Collectors.toList()));
        //Kiem tra tung dieu kien
        for (HeroInfoGraftVO slot : listResCf) {
            //Kiem tra theo kieu loai bo lan luot cai dung
            for (int i = 0; i < listFission.size(); i++) {
                //Kiem tra chinh xac
                //Id
                if (slot.id != null && slot.id.equals("same") && !heroCfUp.id.equals(heroFission.get(i).id)) {
                    continue;
                }
                //Star
                if (heroFission.get(i).star != slot.star) {
                    continue;
                }
                //Kingdom
                if (slot.kingdom != null && slot.kingdom.equals("same") && !heroCfUp.kingdom.equals(listFission.get(i).kingdom)) {
                    continue;
                }

                //Dung het -> Xoa bot -> bot check
                heroFission.remove(i);
                listFission.remove(i);
                break;
            }
        }
        //Dam bao list model rong va list config rong
        return listFission.size() <= 0 && heroFission.size() <= 0;
    }

    /**
     * Up Star HeroVO
     */
    private boolean upStarAllHeroModel(long uid, String hash, Zone zone) {
        return upStarAllHeroModel(uid, Collections.singletonList(hash), zone);
    }

    private boolean upStarAllHeroModel(long uid, List<String> listHash, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        List<Short> listStarUpdate = new ArrayList<>();

        for (HeroModel heroModel : userAllHeroModel.readListHero()) {
            if (listHash.contains(heroModel.hash)) {
                heroModel.star += 1;
                listStarUpdate.add(heroModel.star);
                userAllHeroModel.saveQueueResetHero(heroModel.hash);
            }
        }

        //Event
        Map<String, Object> data = new HashMap<>();
        data.put(Params.STAR, listStarUpdate);
        GameEventAPI.ariseGameEvent(EGameEvent.STAR_HERO_UPDATE, uid, data, zone);

        return userAllHeroModel.saveToDB(zone);
    }

    public boolean upStarHeroModel(Long uid, String hash, Zone zone) {
        return upStarAllHeroModel(uid, hash, zone);
    }

    public boolean upStarHeroModel(Long uid, List<String> listHash, Zone zone) {
        return upStarAllHeroModel(uid, listHash, zone);
    }



    /*---------------------------------------- ITEM HERO--------------------------------------------*/

    /**
     * Get ItemHero
     */
    public EquipDataVO getEquipmentHeroModel(long uid, String hash, Zone zone) {
        List<HeroModel> listHeroModel = getAllHeroModel(uid, zone);
        if (listHeroModel.size() <= 0) {
            return null;
        }
        for (HeroModel hero : listHeroModel) {
            for (ItemSlotVO slotVO : hero.equipment) {
                if (!slotVO.haveLock() || slotVO.equip == null || slotVO.equip.id == null || slotVO.equip.hash == null) {
                    continue;
                }
                if (slotVO.equip.hash.equals(hash)) {
                    return slotVO.equip;
                }
            }
        }
        return null;
    }

    public EquipDataVO getEquipmentItemSlotVO(long uid, String hashHero, int positon, Zone zone) {
        HeroModel heroModel = getHeroModel(uid, hashHero, zone);
        if (heroModel == null) {
            return null;
        }
        return heroModel.equipment.get(positon).equip;
    }

    public EquipDataVO getEquipmentItemSlotVO(HeroModel heroModel, int position) {
        if (heroModel.equipment.get(position).equip == null) {
            return EquipDataVO.create(position);
        }
        return heroModel.equipment.get(position).equip;
    }

    /**
     * Get List Equipment In 1 HeroVO
     */
    public List<EquipDataVO> getAllEquipmentHero(HeroModel heroModel) {
        if (heroModel == null) {
            return new ArrayList<>();
        }

        List<EquipDataVO> listEquipment = new ArrayList<>();
        for (ItemSlotVO slotVO : heroModel.equipment) {
            if (slotVO.haveLock() && slotVO.equip != null) {
                listEquipment.add(slotVO.equip);
            }
        }
        return listEquipment;
    }

    public List<EquipDataVO> getAllEquipmentHero(List<HeroModel> listHeroModel) {
        List<EquipDataVO> listEquipment = new ArrayList<>();
        for (HeroModel heroModel : listHeroModel) {
            listEquipment.addAll(getAllEquipmentHero(heroModel));
        }
        return listEquipment;
    }

    /**
     * Get List Equipment In All HeroVO
     */
    public List<EquipDataVO> getAllEquipmentHero(long uid, Zone zone) {
        List<HeroModel> listHeroModel = getAllHeroModel(uid, zone);
        return getAllEquipmentHero(listHeroModel);
    }

    /**
     * Get List Equipment In All HeroVO depend POSITION
     */
    public List<EquipDataVO> getAllEquipmentHero(long uid, int position, Zone zone) {
        List<EquipDataVO> listEquip = getAllEquipmentHero(uid, zone);
        Iterator<EquipDataVO> iterator = listEquip.iterator();
        EquipDataVO equipVO;
        while (iterator.hasNext()) {
            equipVO = iterator.next();
            if (equipVO.position != position) {
                iterator.remove();
            }
        }

        return listEquip;
    }

    /**
     * Remove Item
     */
    private boolean deleteEquipmentAllHeroModel(long uid, String hashHero, int position, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        for (HeroModel heroModel : userAllHeroModel.readListHero()) {
            if (heroModel.hash.equals(hashHero)) {
                heroModel.equipment.get(position).status = false;
                heroModel.equipment.get(position).equip = null;
                return userAllHeroModel.saveToDB(zone);
            }
        }
        return false;
    }

    public boolean deleteEquipmentHeroModel(long uid, String hashHero, int position, Zone zone) {
        return deleteEquipmentAllHeroModel(uid, hashHero, position, zone);
    }

    /**
     * Remove All Item 1 HeroVO
     */
    private boolean deleteAllEquipmentAllHeroModel(long uid, String hashHero, Zone zone) {
        return deleteAllEquipmentAllHeroModel(uid, Collections.singletonList(hashHero), zone);
    }

    private boolean deleteAllEquipmentAllHeroModel(long uid, List<String> listHashHero, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);

        for (HeroModel heroModel : userAllHeroModel.readListHero()) {
            if (listHashHero.contains(heroModel.hash)) {
                for (ItemSlotVO slotVO : heroModel.equipment) {
                    slotVO.unlock();
                    slotVO.equip = null;
                }
            }
        }
        return userAllHeroModel.saveToDB(zone);
    }

    public boolean deleteAllEquipmentHeroModel(long uid, String hashHero, Zone zone) {
        return deleteAllEquipmentAllHeroModel(uid, hashHero, zone);
    }

    public boolean deleteAllEquipmentHeroModel(long uid, List<String> listHashHero, Zone zone) {
        return deleteAllEquipmentAllHeroModel(uid, listHashHero, zone);
    }

    /**
     * Remove All Item All HeroVO
     */
    public boolean deleteAllEquipmentAllHeroModel(long uid, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        UserMainHeroModel userMainHeroModel = getUserMainHeroModel(uid, zone);

        for (HeroModel heroModel : userAllHeroModel.readListHero()) {
            for (ItemSlotVO slotVO : heroModel.equipment) {
                slotVO.status = false;
                slotVO.equip = null;
            }
        }

        return userAllHeroModel.saveToDB(zone);
    }

    /**
     * Update Equipment In HeroVO
     */
    private boolean updateEquipmentAllHeroModel(long uid, EquipDataVO equipDataVO, Zone zone) {
        if (equipDataVO == null) {
            return false;
        }
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        for (HeroModel heroModel : userAllHeroModel.readListHero()) {
            for (ItemSlotVO slotVO : heroModel.equipment) {
                if (!slotVO.status || slotVO.equip == null) {
                    continue;
                }
                if (slotVO.equip.hash.equals(equipDataVO.hash)) {
                    slotVO.equip = equipDataVO;
                    return userAllHeroModel.saveToDB(zone);
                }
            }
        }
        return false;
    }

    public boolean updateEquipmentHeroModel(long uid, EquipDataVO equipDataVO, Zone zone) {
        return updateEquipmentAllHeroModel(uid, equipDataVO, zone);
    }


    private boolean updateEquipmentAllHeroModel(long uid, String hashHero, EquipDataVO equipDataVO, Zone zone) {
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        for (HeroModel heroModel : userAllHeroModel.readListHero()) {
            if (heroModel.hash.equals(hashHero)) {
                //Gan hash hero vao item tham chieu
                equipDataVO.hashHero = hashHero;
                //Tao moi vs count 1 (chong tham chieu)
                equipDataVO = EquipDataVO.create1(equipDataVO);

                if (equipDataVO.id == null || equipDataVO.hash == null) {
                    heroModel.equipment.get(equipDataVO.position).unlock();
                    heroModel.equipment.get(equipDataVO.position).equip = null;
                } else {
                    equipDataVO.hashHero = hashHero;
                    heroModel.equipment.get(equipDataVO.position).lock();
                    heroModel.equipment.get(equipDataVO.position).equip = equipDataVO;
                }

                return userAllHeroModel.saveToDB(zone);
            }
        }
        return false;
    }

    public boolean updateEquipmentHeroModel(long uid, String hashHero, EquipDataVO equipDataVO, Zone zone) {
        return updateEquipmentAllHeroModel(uid, hashHero, equipDataVO, zone);
    }


    private boolean updateEquipmentAllHeroModel(long uid, String hashHero, List<EquipDataVO> equipDataVO, Zone zone) {
        //Dulicate chong tham chieu
        List<EquipDataVO> listDulicate = equipDataVO.stream().map(EquipDataVO::create1).collect(Collectors.toList());
        UserAllHeroModel userAllHeroModel = getUserAllHeroModel(uid, zone);
        Iterator<EquipDataVO> iterator;
        EquipDataVO entry;

        for (HeroModel heroModel : userAllHeroModel.readListHero()) {

            if (heroModel.hash.equals(hashHero)) {
                slot_loop:
                for (ItemSlotVO slot : heroModel.equipment) {
                    iterator = listDulicate.iterator();

                    while (iterator.hasNext()){
                        entry = iterator.next();

                        if (slot.position == entry.position) {
                            if (entry.id == null || entry.hash == null) {
                                slot.unlock();
                                slot.equip = null;
                            } else {
                                entry.hashHero = hashHero;
                                slot.lock();
                                slot.equip = entry;
                            }
                            iterator.remove();
                            continue slot_loop;
                        }
                    }
                }
                return userAllHeroModel.saveToDB(zone);
            }
        }
        return false;
    }

    public boolean updateEquipmentHeroModel(long uid, String hashHero, List<EquipDataVO> equipDataVO, Zone zone) {
        return updateEquipmentAllHeroModel(uid, hashHero, equipDataVO, zone);
    }



    /*------------------------------------------STATS----------------------------------------*/
    /**
     * Calculation Power
     */
    public int getPower(Stats stats) {
        int s = (int) (stats.readHp() / 5 +
                stats.readStrength() * 2 + stats.readIntelligence() * 2 +
                stats.readArmor() * 3.5 + stats.readMagicResistance() * 3.5 +
                stats.readDexterity() * 40 +
                stats.readAgility() * 4 +
                stats.readElusiveness() * 4 +
                stats.readArmorPenetration() * 1000 + stats.readMagicPenetration() * 1000 +
                stats.readCrit() * 1500 + stats.readCritDmg() * 250 +
                stats.readTenacity() * 5);
        return s;
    }

    public int getPower(HeroModel heroModel, Zone zone) {
        return getPower(getStatsHero(heroModel, zone));
    }
    public int getNormalPower(HeroModel heroModel) {
        return getPower(getStatsHeroNormal(heroModel));
    }

    public int getPower(List<HeroModel> listHeroModel, Zone zone) {
        int power = 0;
        for (HeroModel heroModel : listHeroModel) {
            if (heroModel == null) continue;
            power += getPower(heroModel, zone);
        }
        return power;
    }
    public int getNormalPower(List<HeroModel> listHeroModel) {
        int power = 0;
        for (HeroModel heroModel : listHeroModel) {
            if (heroModel == null) continue;
            power += getNormalPower(heroModel);
        }
        return power;
    }


    /**
     * Get stats HeroVO da tinh mac item
     */
    public Stats getStatsHero(HeroModel heroModel, Zone zone) {
        Stats stats = getStatsHeroModel(heroModel, zone);

        //Chi so item equip tren hero (+ luon)
        stats = Stats.readSumStats(stats, getStatsItem(heroModel));

        return stats;
    }
    public Stats getStatsHeroNormal(HeroModel heroModel) {
        Stats stats = getStatsNormalHeroModel(heroModel);

        //Chi so item equip tren hero (+ luon)
        stats = Stats.readSumStats(stats, getStatsItem(heroModel));

        return stats;
    }

    /**
     * Get Stats HeroVO Model Chua tinh mac do
     */
    public Stats getStatsHeroModel(HeroModel heroModel, Zone zone) {
        if (heroModel == null) {
            return new Stats();
        }
        HeroVO heroVOCf = null;
        CharacterStatsGrowVO growCf = null;
        switch (EHeroType.fromId(heroModel.type)) {
            case NORMAL:
                heroVOCf = CharactersConfigManager.getInstance().getHeroConfig(heroModel.id);
                growCf = CharactersConfigManager.getInstance().getHeroStatsGrowConfig(heroModel.id);
                break;
            case NFT:
                HeroBaseStatsModel heroBaseStatsModel = HeroBaseStatsModel.copyFromDBtoObject(heroModel.hash, heroModel.id, zone);
                heroVOCf = heroBaseStatsModel.baseStats;
                growCf = heroBaseStatsModel.growStats;
                break;
        }

        if (heroVOCf == null) {
            return new Stats();
        }

        String keyStatsHeroCache = getKeyStatsHeroCache(heroModel.id, heroModel.star, heroModel.level);
        Stats stats = heroStatsInstance.get(keyStatsHeroCache);
        if(stats != null) return stats;


        List<Short> listLevelBreakThoughtCf = CharactersConfigManager.getInstance().getListLevelBreakThoughtConfig();
        int breakThought = -1;
        for (int i = 0; i < listLevelBreakThoughtCf.size(); i++) {
            if (listLevelBreakThoughtCf.get(i) > heroModel.readLevel()) {
                breakThought = i;
                break;
            }
        }
        if(breakThought == -1){
            breakThought = listLevelBreakThoughtCf.size();
        }

        stats = new Stats();
        //Chi so cua rieng tuong chua mac do
        stats.hp = calculationStatsHero(growCf, heroVOCf.health, Attr.HP, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.strength = calculationStatsHero(growCf, heroVOCf.strength, Attr.STRENGTH, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.intelligence = calculationStatsHero(growCf, heroVOCf.intelligence, Attr.INTELLIGENCE, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.dexterity = calculationStatsHero(growCf, heroVOCf.dexterity, Attr.DEXTERITY, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.armor = calculationStatsHero(growCf, heroVOCf.armor, Attr.ARMOR, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.magicResistance = calculationStatsHero(growCf, heroVOCf.magicResistance, Attr.MAGIC_RESISTANCE, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.agility = calculationStatsHero(growCf, heroVOCf.agility, Attr.AGILITY, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.crit = calculationStatsHero(growCf, heroVOCf.crit, Attr.CRITICAL_CHANCE, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.critDmg = calculationStatsHero(growCf, heroVOCf.critBonus, Attr.CRITICAL_BONUS_DAMAGE, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.armorPenetration = calculationStatsHero(growCf, heroVOCf.armorPenetration, Attr.ARMOR_PENETRATION, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.magicPenetration = calculationStatsHero(growCf, heroVOCf.magicPenetration, Attr.MAGIC_PENETRATION, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.tenacity = calculationStatsHero(growCf, heroVOCf.tenacity, Attr.TENACITY, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);
        stats.elusiveness = calculationStatsHero(growCf, heroVOCf.elusiveness, Attr.ELUSIVENESS, heroVOCf.star, heroModel.star, heroModel.readLevel(), breakThought);

        //Cache
        heroStatsInstance.put(keyStatsHeroCache, stats);

        return stats;
    }

    public void updateStatsHeroModel(HeroModel heroModel, Zone zone) {
        boolean isNft = EHeroType.fromId(heroModel.type) == EHeroType.NFT;
        if (!isNft) {
            return;
        }

        HeroBaseStatsModel heroBaseStatsModel = HeroBaseStatsModel.copyFromDBtoObject(heroModel.hash, heroModel.id, zone);
        HeroVO heroVOCf = heroBaseStatsModel.baseStats;
        CharacterStatsGrowVO growCf = heroBaseStatsModel.growStats;
        heroVOCf.health = this.reduceStat(heroVOCf.health);
        heroVOCf.strength = this.reduceStat(heroVOCf.strength);
        heroVOCf.intelligence = this.reduceStat(heroVOCf.intelligence);
        heroVOCf.dexterity = this.reduceStat(heroVOCf.dexterity);
        heroVOCf.armor = this.reduceStat(heroVOCf.armor);
        heroVOCf.magicResistance = this.reduceStat(heroVOCf.magicResistance);
        heroVOCf.agility = this.reduceStat(heroVOCf.agility);
        heroVOCf.crit = this.reduceStat(heroVOCf.crit);
        heroVOCf.critBonus = this.reduceStat(heroVOCf.critBonus);
        heroVOCf.armorPenetration = this.reduceStat(heroVOCf.armorPenetration);
        heroVOCf.magicPenetration = this.reduceStat(heroVOCf.magicPenetration);
        heroVOCf.tenacity = this.reduceStat(heroVOCf.tenacity);
        heroVOCf.elusiveness = this.reduceStat(heroVOCf.elusiveness);

        growCf.enhanceLevel.hp = this.reduceStat(growCf.enhanceLevel.hp);
        growCf.enhanceLevel.strength = this.reduceStat(growCf.enhanceLevel.strength);
        growCf.enhanceLevel.intelligence = this.reduceStat(growCf.enhanceLevel.intelligence);
        growCf.enhanceLevel.attack = this.reduceStat(growCf.enhanceLevel.attack);
        growCf.enhanceLevel.armor = this.reduceStat(growCf.enhanceLevel.armor);
        growCf.enhanceLevel.magicResistance = this.reduceStat(growCf.enhanceLevel.magicResistance);
        growCf.enhanceLevel.defense = this.reduceStat(growCf.enhanceLevel.defense);
        growCf.enhanceLevel.dexterity = this.reduceStat(growCf.enhanceLevel.dexterity);
        growCf.enhanceLevel.agility = this.reduceStat(growCf.enhanceLevel.agility);
        growCf.enhanceLevel.elusiveness = this.reduceStat(growCf.enhanceLevel.elusiveness);
        growCf.enhanceLevel.armorPenetration = this.reduceStat(growCf.enhanceLevel.armorPenetration);
        growCf.enhanceLevel.magicPenetration = this.reduceStat(growCf.enhanceLevel.magicPenetration);
        growCf.enhanceLevel.defensePenetration = this.reduceStat(growCf.enhanceLevel.defensePenetration);
        growCf.enhanceLevel.crit = this.reduceStat(growCf.enhanceLevel.crit);
        growCf.enhanceLevel.critDmg = this.reduceStat(growCf.enhanceLevel.critDmg);
        growCf.enhanceLevel.tenacity = this.reduceStat(growCf.enhanceLevel.tenacity);

        growCf.enhanceStar.hp = this.reduceStat(growCf.enhanceStar.hp);
        growCf.enhanceStar.strength = this.reduceStat(growCf.enhanceStar.strength);
        growCf.enhanceStar.intelligence = this.reduceStat(growCf.enhanceStar.intelligence);
        growCf.enhanceStar.attack = this.reduceStat(growCf.enhanceStar.attack);
        growCf.enhanceStar.armor = this.reduceStat(growCf.enhanceStar.armor);
        growCf.enhanceStar.magicResistance = this.reduceStat(growCf.enhanceStar.magicResistance);
        growCf.enhanceStar.defense = this.reduceStat(growCf.enhanceStar.defense);
        growCf.enhanceStar.dexterity = this.reduceStat(growCf.enhanceStar.dexterity);
        growCf.enhanceStar.agility = this.reduceStat(growCf.enhanceStar.agility);
        growCf.enhanceStar.elusiveness = this.reduceStat(growCf.enhanceStar.elusiveness);
        growCf.enhanceStar.armorPenetration = this.reduceStat(growCf.enhanceStar.armorPenetration);
        growCf.enhanceStar.magicPenetration = this.reduceStat(growCf.enhanceStar.magicPenetration);
        growCf.enhanceStar.defensePenetration = this.reduceStat(growCf.enhanceStar.defensePenetration);
        growCf.enhanceStar.crit = this.reduceStat(growCf.enhanceStar.crit);
        growCf.enhanceStar.critDmg = this.reduceStat(growCf.enhanceStar.critDmg);
        growCf.enhanceStar.tenacity = this.reduceStat(growCf.enhanceStar.tenacity);
        heroBaseStatsModel.saveToDB(zone);
    }

    private float reduceStat(float value, int divisor) {
        return value > 0 ? value / divisor : 0f;
    }

    private float reduceStat(float value) {
        return reduceStat(value, 2);
    }

    public Stats getStatsNormalHeroModel(HeroModel heroModel, int star) {
        if (heroModel == null) {
            return new Stats();
        }
        HeroVO heroVOCf = CharactersConfigManager.getInstance().getHeroConfig(heroModel.id);
        if (heroVOCf == null) {
            return new Stats();
        }

        String keyStatsHeroCache = getKeyStatsHeroCache(heroModel.id, star, heroModel.level);
        Stats stats = heroStatsInstance.get(keyStatsHeroCache);
        if(stats != null) return stats;


        List<Short> listLevelBreakThoughtCf = CharactersConfigManager.getInstance().getListLevelBreakThoughtConfig();
        CharacterStatsGrowVO growCf = CharactersConfigManager.getInstance().getHeroStatsGrowConfig(heroModel.id);
        int breakThought = -1;
        for (int i = 0; i < listLevelBreakThoughtCf.size(); i++) {
            if (listLevelBreakThoughtCf.get(i) > heroModel.readLevel()) {
                breakThought = i;
                break;
            }
        }
        if(breakThought == -1){
            breakThought = listLevelBreakThoughtCf.size();
        }

        stats = new Stats();
        //Chi so cua rieng tuong chua mac do
        stats.hp = calculationStatsHero(growCf, heroVOCf.health, Attr.HP, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.strength = calculationStatsHero(growCf, heroVOCf.strength, Attr.STRENGTH, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.intelligence = calculationStatsHero(growCf, heroVOCf.intelligence, Attr.INTELLIGENCE, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.dexterity = calculationStatsHero(growCf, heroVOCf.dexterity, Attr.DEXTERITY, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.armor = calculationStatsHero(growCf, heroVOCf.armor, Attr.ARMOR, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.magicResistance = calculationStatsHero(growCf, heroVOCf.magicResistance, Attr.MAGIC_RESISTANCE, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.agility = calculationStatsHero(growCf, heroVOCf.agility, Attr.AGILITY, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.crit = calculationStatsHero(growCf, heroVOCf.crit, Attr.CRITICAL_CHANCE, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.critDmg = calculationStatsHero(growCf, heroVOCf.critBonus, Attr.CRITICAL_BONUS_DAMAGE, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.armorPenetration = calculationStatsHero(growCf, heroVOCf.armorPenetration, Attr.ARMOR_PENETRATION, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.magicPenetration = calculationStatsHero(growCf, heroVOCf.magicPenetration, Attr.MAGIC_PENETRATION, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.tenacity = calculationStatsHero(growCf, heroVOCf.tenacity, Attr.TENACITY, heroVOCf.star, star, heroModel.readLevel(), breakThought);
        stats.elusiveness = calculationStatsHero(growCf, heroVOCf.elusiveness, Attr.ELUSIVENESS, heroVOCf.star, star, heroModel.readLevel(), breakThought);

        //Cache
        heroStatsInstance.put(keyStatsHeroCache, stats);

        return stats;
    }
    public Stats getStatsNormalHeroModel(HeroModel heroModel) {
        return this.getStatsNormalHeroModel(heroModel, heroModel.star);
    }

    /**
     * Chi so theo level
     *
     * @param stats chi so khoi diem
     * @param level khoang cach level
     * @param star  khoang cach star
     * @return
     */
    private static float calculationStatCharacter(CharacterStatsGrowVO statsGrowCf, double stats, Attr attr, int star, int level, int breakThought) {
        switch (attr) {
            case HP:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.hp, level, statsGrowCf.enhanceLevel.hp, breakThought);
            case STRENGTH:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.strength, level, statsGrowCf.enhanceLevel.strength, breakThought);
            case INTELLIGENCE:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.intelligence, level, statsGrowCf.enhanceLevel.intelligence, breakThought);
            case ARMOR:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.armor, level, statsGrowCf.enhanceLevel.armor, breakThought);
            case MAGIC_RESISTANCE:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.magicResistance, level, statsGrowCf.enhanceLevel.magicResistance, breakThought);
            case DEXTERITY:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.dexterity, level, statsGrowCf.enhanceLevel.dexterity, breakThought);
            case AGILITY:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.agility, level, statsGrowCf.enhanceLevel.agility, breakThought);
            case ELUSIVENESS:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.elusiveness, level, statsGrowCf.enhanceLevel.elusiveness, breakThought);
            case ARMOR_PENETRATION:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.armorPenetration, level, statsGrowCf.enhanceLevel.armorPenetration, breakThought);
            case MAGIC_PENETRATION:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.magicPenetration, level, statsGrowCf.enhanceLevel.magicPenetration, breakThought);
            case CRITICAL_CHANCE:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.crit, level, statsGrowCf.enhanceLevel.crit, breakThought);
            case CRITICAL_BONUS_DAMAGE:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.critDmg, level, statsGrowCf.enhanceLevel.critDmg, breakThought);
            case TENACITY:
                return calculationStatCharacterByLevel(stats, star, statsGrowCf.enhanceStar.tenacity, level, statsGrowCf.enhanceLevel.tenacity, breakThought);
        }
        return 0;
    }

    private static float calculationStatCharacterByLevel(double baseStats, int star, double starGrow, int level, double levelGrow, int breakThought) {
        float starBase = calculationStatCharacterByStar(baseStats, star, starGrow);
//        return (float) (starBase + breakThought * (starBase * 0.2) + (level - breakThought) * (starBase * levelGrow));
        return (float) (starBase + calculationBreakThought(breakThought, starBase) + (level - breakThought) * (starBase * levelGrow));
    }

    private static float calculationStatCharacterByStar(double baseStats, int star, double starGrow) {
        return (float) (baseStats * Math.pow(1 + starGrow, star));
    }

    /**
     * tính chỉ sống tăng từ đột phá
     * @param breakThoughtTime
     * @param statsBase
     * @return
     */
    private static float calculationBreakThought(int breakThoughtTime, float statsBase) {
        float incStats = 0;
        List<Integer> breakThoughtList = CharactersConfigManager.getInstance().getBreakThoughtConfig();
        int rate;
        for (int i = 0; i < breakThoughtTime; i++){
            if(i >= breakThoughtList.size()){
                rate = breakThoughtList.get(breakThoughtList.size() - 1);
            }else {
                rate = breakThoughtList.get(i);
            }
            incStats += statsBase * rate / 100;
        }
        return incStats;
    }

    private float calculationStatsHero(CharacterStatsGrowVO statsGrowCf, double statsHeroConfig, Attr attr, int startStar, int star,
                                       int level, int breakThought) {
        //Neu ton tai key va neu key -> value = true (mo khoa chi so)
        //Neu ko = 0 (chua mo khoa chi so = 0)
//        if(statsOpenConfig.containsKey((short)attr.getValue()) && statsOpenConfig.get((short)attr.getValue())){
//            return calculationStatCharacter(statsGrowCf, statsHeroConfig, attr, star - startStar, level - 1, breakThought);
//        }
//        return 0;
        return calculationStatCharacter(statsGrowCf, statsHeroConfig, attr, star - startStar, level - 1, breakThought);
    }

    /**
     * Chi so trang bi
     */
    private Stats getStatsItem(HeroModel heroModel) {
        Stats stats = new Stats();
        for (ItemSlotVO slotVO : heroModel.equipment) {
            if (!slotVO.haveLock() || slotVO.equip == null) {
                continue;
            }

            stats = Stats.readSumStats(stats, getStatsItem(slotVO.equip));
        }

        return stats;
    }

    public Stats getStatsItem(EquipDataVO equipDataVO) {
        if (equipDataVO == null || equipDataVO.id == null) {
            return new Stats();
        }

        EquipVO equipCf = ItemManager.getInstance().getEquipVO(equipDataVO.id, equipDataVO.level, equipDataVO.count);
        if (equipCf == null) {
            return new Stats();
        }
        Stats stats = getStatsItem(equipCf.listAttr);
        //Cong them chi so khi cuong hoa, nang cap

        for (StoneSlotVO slotStoneVO : equipDataVO.listSlotStone) {
            if (!slotStoneVO.status || slotStoneVO.stoneVO == null) {
                continue;
            }
            stats = Stats.readSumStats(stats, getStatsItem(slotStoneVO.stoneVO));
        }
        return stats;
    }

    public Stats getStatsItem(StoneVO stoneVO) {
        if (stoneVO == null) {
            return new Stats();
        }
        StoneVO stoneCf = ItemManager.getInstance().getStoneVO(stoneVO.id, stoneVO.level);
        if (stoneCf == null) {
            return new Stats();
        }
        Stats stats = getStatsItem(stoneCf.listAttr);
        //Cong them chi so khi cuong hoa, nang cap


        return stats;
    }

    private Stats getStatsItem(List<AttributeVO> listAttr) {
        Stats stats = new Stats();
        for (AttributeVO attr : listAttr) {
            switch (Attr.fromValue(attr.attr)) {
                case HP:
                    stats.hp = attr.param;
                    break;
                case STRENGTH:
                    stats.strength = attr.param;
                    break;
                case INTELLIGENCE:
                    stats.intelligence = attr.param;
                    break;
                case ATTACK:
                    stats.attack = attr.param;
                    break;
                case DEXTERITY:
                    stats.dexterity = attr.param;
                    break;
                case ARMOR:
                    stats.armor = attr.param;
                    break;
                case MAGIC_RESISTANCE:
                    stats.magicResistance = attr.param;
                    break;
                case DEFENSE:
                    stats.defense = attr.param;
                    break;
                case AGILITY:
                    stats.agility = attr.param;
                    break;
                case CRITICAL_CHANCE:
                    stats.crit = attr.param;
                    break;
                case CRITICAL_BONUS_DAMAGE:
                    stats.critDmg = attr.param;
                    break;
                case ARMOR_PENETRATION:
                    stats.armorPenetration = attr.param;
                    break;
                case MAGIC_PENETRATION:
                    stats.magicPenetration = attr.param;
                    break;
                case DEFENSE_PENETRATION:
                    stats.defensePenetration = attr.param;
                    break;
                case TENACITY:
                    stats.tenacity = attr.param;
                    break;
                case ELUSIVENESS:
                    stats.elusiveness = attr.param;
                    break;
            }
        }

        return stats;
    }

    /**
     * @param zone
     * @param uid
     * @param type
     * @param update
     * @return
     */
    public void doUpdateTeamHero(Zone zone, long uid, String type, List<HeroPosition> update) throws InvalidUpdateTeamException {
        HeroHandler heroHandler = (HeroHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_HERO);
        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (heroHandler == null || user == null) return;

        doUpdateTeamHero(heroHandler, user, type, update);
    }

    public void doUpdateTeamHero(HeroHandler heroHandler, User user, String type, List<HeroPosition> update) throws InvalidUpdateTeamException {
        long uid = heroHandler.getUserModel(user).userID;

        //Kiem tra team type
        ETeamType teamType = ETeamType.fromID(type);
        if (teamType == null) {
            SendUpdateTeamHero objPut = new SendUpdateTeamHero(ServerConstant.ErrorCode.ERR_INVALID_TEAM_TYPE);
            heroHandler.send(objPut, user);
            throw new InvalidUpdateTeamException();
        }

        List<String> listHashHeroTeam = update.parallelStream().
                map(obj -> obj.hash).collect(Collectors.toList());
//        List<HeroInfo> heroFriend = FriendHeroManager.getInstance().getHeroInfo(listHashHeroTeam, uid, heroHandler.getParentExtension().getParentZone());
        List<HeroInfo> heroFriend = new ArrayList<>();
        if(!heroFriend.isEmpty()){
            if(!FriendHeroManager.getInstance().canAssignHeroFriend(uid, teamType, heroHandler.getParentExtension().getParentZone())){
                SendUpdateTeamHero objPut = new SendUpdateTeamHero(ServerConstant.ErrorCode.ERR_LIMIT_BORROW_HERO_FRIEND);
                heroHandler.send(objPut, user);
                throw new InvalidUpdateTeamException();
            }

            //So luong hero muon <= 1
            int countHeroCanBorrow = 0;
            switch (teamType) {
                case ARENA:
                case ARENA_DEFENSE:
                case DARK_GATE:
                case PVP_OFFLINE_DEFENSE:
                    countHeroCanBorrow = 0;
                    break;
                case PVP_OFFLINE:
                case MONSTER_HUNT:
                case MISSION_OUTPOST:
                case TOWER:
                case CAMPAIGN:
                    countHeroCanBorrow = 1;
                    break;
            }

            if (heroFriend.size() > countHeroCanBorrow) {
                SendUpdateTeamHero objPut = new SendUpdateTeamHero(ServerConstant.ErrorCode.ERR_BORROW_MORE_THAN_EXPECT_HERO_FRIEND);
                heroHandler.send(objPut, user);
                throw new InvalidUpdateTeamException();
            }
        }

        //2 hero cung id khong cung trong doi hinh
//        if (setIdHeroCf.size() + heroFriend.size() != update.size()) {
//            SendUpdateTeamHero objPut = new SendUpdateTeamHero(ServerConstant.ErrorCode.ERR_INVALID_TEAM);
//            heroHandler.send(objPut, user);
//            throw new InvalidUpdateTeamException();
//        }

        //Update that bai
        if (!HeroManager.getInstance().updateTeamHero(uid, teamType, update, heroHandler.getParentExtension().getParentZone())) {
            SendUpdateTeamHero objPut = new SendUpdateTeamHero(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            heroHandler.send(objPut, user);
            throw new InvalidUpdateTeamException();
        }

        SendUpdateTeamHero objPut = new SendUpdateTeamHero();
        heroHandler.send(objPut, user);
    }


    public void clearHeroFriendTeamHero(long uid, ETeamType teamType, Zone zone) {
        List<String> teamHashHero = getListMainHashHero(zone, uid, teamType, false);
        List<String> userHashHero = getUserAllListHeroModel(uid, zone).stream().
                map(obj -> obj.hash).
                collect(Collectors.toList());
        List<String> friendHashHero = new ArrayList<>(teamHashHero);
        friendHashHero.removeAll(userHashHero);

        deleteMainHeroModel(uid, friendHashHero, teamType, zone);
    }

    public boolean haveHeroNFT(long uid, int count, Zone zone) {
        List<HeroModel> heroModels = getAllHeroModel(uid, zone);
        AtomicInteger total = new AtomicInteger();
        heroModels.forEach(heroModel -> {
            switch (EHeroType.fromId(heroModel.type)) {
                case NORMAL:
                    break;
                case NFT:
                    total.addAndGet(1);
                    break;

            }
        });
        return count >= total.get();
    }


    /*--------------------------------------------- BLESSING ----------------------------------------*/
    public static class BlessingManager {

        private static BlessingManager ourInstance = new BlessingManager();

        private BlessingManager(){}

        public static BlessingManager getInstance() {
            return ourInstance;
        }



        /*------------------------------------------------------------------------------------------------------------------*/
        /*------------------------------------------------------------------------------------------------------------------*/

        /**
         * Get User Blessing Model
         */
        public UserBlessingHeroModel getUserBlessingHeroModel(long uid, Zone zone) {
            return UserBlessingHeroModel.copyFromDBtoObject(uid, zone);
//            return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getUserBlessingModelCache(uid);
        }

        public HeroSlotBlessing getHeroSlotBlessing(UserBlessingHeroModel userBlessingHeroModel, int position, Zone zone) {
            return userBlessingHeroModel.readSlotBlessing(position, zone);
        }

        public boolean haveBlessing(long uid, String hashHero, Zone zone) {
            UserBlessingHeroModel userBlessingHeroModel = getUserBlessingHeroModel(uid, zone);
            return getListHeroSlotBlessing(userBlessingHeroModel, zone).parallelStream().map(obj -> obj.hashHero).collect(Collectors.toSet()).contains(hashHero);
        }

        public int getCountUnlockSlotBlessing(UserBlessingHeroModel userBlessingHeroModel, MoneyType moneyType) {
            switch (moneyType) {
                case DIAMOND:
                    return userBlessingHeroModel.unlockDiamont + 1;
                case MIRAGE_ESSENCE:
                    return userBlessingHeroModel.unlockEssence + 1;
            }
            return 0;
        }

        /**
         * Lay list hero dc ban phuoc
         *
         * @param zone
         * @return
         */
        public List<HeroSlotBlessing> getListHeroSlotBlessing(long uid, Zone zone) {
            UserBlessingHeroModel userBlessingHeroModel = getUserBlessingHeroModel(uid, zone);
            return getListHeroSlotBlessing(userBlessingHeroModel, zone);
        }

        public List<HeroSlotBlessing> getListHeroSlotBlessing(UserBlessingHeroModel userBlessingHeroModel, Zone zone) {
            return userBlessingHeroModel.readSlotBlessing(zone);
        }

        /**
         * Ban phuoc cho hero
         *
         * @param uid
         * @param hashHero
         * @param position
         * @param zone
         * @return
         */
        public boolean addHeroBlessing(long uid, String hashHero, String idHero, int star, int level, int position, Zone zone) {
            return getUserBlessingHeroModel(uid, zone).addHeroBlessing(hashHero, idHero, star, level, position, zone);
        }

        public boolean removeHeroBlessing(long uid, String hashHero, Zone zone) {
            return getUserBlessingHeroModel(uid, zone).removeHeroBlessing(hashHero, zone);
        }

        public boolean removeHeroBlessing(long uid, List<String> listHashHero, Zone zone) {
            return getUserBlessingHeroModel(uid, zone).removeHeroBlessing(listHashHero, zone);
        }

        public boolean removeSlotHeroBlessing(UserBlessingHeroModel userBlessingHeroModel, int position, Zone zone) {
            return userBlessingHeroModel.removeSlotBlessingCountdown(position, zone);
        }

        public boolean unlockSlotHeroBlessing(UserBlessingHeroModel userBlessingHeroModel, MoneyType moneyType, Zone zone) {
            return userBlessingHeroModel.unlockSlotBlessing(moneyType, zone);
        }

        /**
         * Level blessing cua hero
         * @param uid
         * @param id
         * @param zone
         * @return
         */
        public int getLevelBlessingHero(long uid, String id, Zone zone){
            UserAllHeroModel userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
            return getLevelBlessingHero(userAllHeroModel, id);
        }
        public int getLevelBlessingHero(UserAllHeroModel userAllHeroModel, String id){
            int level = CharactersConfigManager.getInstance().getMaxLevelBlessingHeroConfig(id);
            return (level < userAllHeroModel.readLevelMin5Hero()) ? level : userAllHeroModel.readLevelMin5Hero();
        }


        /**
         * @param uid
         * @param position
         * @param zone
         * @return
         */
        public List<ResourcePackage> getCostReduceCountdownBlessing(long uid, int position, Zone zone) {
            UserBlessingHeroModel userBlessingHeroModel = getUserBlessingHeroModel(uid, zone);
            return getCostReduceCountdownBlessing(userBlessingHeroModel, position, zone);
        }

        public List<ResourcePackage> getCostReduceCountdownBlessing(UserBlessingHeroModel userBlessingHeroModel, int position, Zone zone) {
            HeroSlotBlessing slotBlessing = userBlessingHeroModel.readSlotBlessing(position, zone);
            if (slotBlessing == null || slotBlessing.timeStamp <= 0) return new ArrayList<>();

            //Tinh thoi gian contdown
            int timeCountdown = CharactersConfigManager.getInstance().getTimeReblessingConfig() - (Utils.getTimestampInSecond() - slotBlessing.timeStamp);
            if (timeCountdown <= 0) return new ArrayList<>();
            //Lay config
            ReduceTimeVO reduceTimeCf = CharactersConfigManager.getInstance().getReduceTimeBlessingConfig();
            //Tinh so luong can phai tra
            int amount = (int) Math.round(timeCountdown * 1d / reduceTimeCf.time);

            ResourcePackage cost = new ResourcePackage(reduceTimeCf.cost.id, -amount);
            return new ArrayList<>(Collections.singleton(cost));
        }

        /**
         * Kiem tra co noti trong blessing ko
         * @param uid
         * @param zone
         * @return
         */
        public boolean haveSendNotifyBlessingHero(long uid, Zone zone){
            UserBlessingHeroModel userBlessingHeroModel = getUserBlessingHeroModel(uid, zone);

            //Ktra con o trong mo
            if(userBlessingHeroModel.readSlotBlessing(zone).size() < userBlessingHeroModel.size) return true;

            //Ktra co the mo khoa o
            UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, zone);
            MoneyType moneyType = MoneyType.MIRAGE_ESSENCE;
            List<ResourcePackage> costUnlock = CharactersConfigManager.getInstance().getUnlockBlessingConfig().
                    readCostUnlockBlessingConfig(
                            moneyType,
                            HeroManager.BlessingManager.getInstance().getCountUnlockSlotBlessing(userBlessingHeroModel, moneyType));
            if(costUnlock.isEmpty()) return false;
            if(userBagModel.readMoney(moneyType, zone) + costUnlock.get(0).amount < 0) return false;

            return true;
        }
    }



    /*--------------------------------------------- SUMMON ----------------------------------------*/
    public static class SummonManager {

        private Map<Integer, List<RandomObj>> heroInstance = new HashMap<>();
        private Map<String, LIZRandom> summonRareInstance = new HashMap<>();


        private static SummonManager ourInstance = new SummonManager();

        private SummonManager() {
            initHero();
        }

        public static SummonManager getInstance() {
            return ourInstance;
        }

        /*------------------------------------------------------------------------------------------------------------------*/
        /*------------------------------------------------------------------------------------------------------------------*/
        private void initHero() {
            List<RandomObj> listPurpleElite = new ArrayList<>();
            List<RandomObj> listPurpleLegendary = new ArrayList<>();
            List<RandomObj> listPurpleRare3 = new ArrayList<>();
            List<RandomObj> listBlue = new ArrayList<>();
            List<RandomObj> listGreen = new ArrayList<>();
            List<RandomObj> listBlueNPurple = new ArrayList<>();

            for (HeroVO heroVO : CharactersConfigManager.getInstance().getHeroConfig()) {
                //COLOR theo STAR
                // >= 0
                switch (ColorHero.fromValue(heroVO.star)) {
                    case GREEN:
                        listGreen.add(new RandomObj(HeroSummonVO.create(heroVO.id, ColorHero.GREEN.getStar()), heroVO.rare));
                        break;
                    case BLUE:
                        listBlue.add(new RandomObj(HeroSummonVO.create(heroVO.id, ColorHero.BLUE.getStar()), heroVO.rare));
                        listPurpleElite.add(new RandomObj(HeroSummonVO.create(heroVO.id, ColorHero.PURPLE.getStar()), heroVO.rare));
                        break;
                    case PURPLE:
                        listPurpleLegendary.add(new RandomObj(HeroSummonVO.create(heroVO.id, ColorHero.PURPLE.getStar()), heroVO.rare));
                        listPurpleElite.add(new RandomObj(HeroSummonVO.create(heroVO.id, ColorHero.PURPLE.getStar()), heroVO.rare));
                        if (heroVO.rare == 3) {
                            listPurpleRare3.add(new RandomObj(HeroSummonVO.create(heroVO.id, ColorHero.PURPLE.getStar()), 1));
                        }
                        break;
                }
            }
            listBlueNPurple.addAll(listBlue);
            listBlueNPurple.addAll(listPurpleLegendary);


            heroInstance.put(ColorHero.GREEN.getStar(), listGreen);
            heroInstance.put(ColorHero.BLUE.getStar(), listBlue);
            heroInstance.put(ColorHero.BLUE_N_PURPLE.getStar(), listBlueNPurple);
            heroInstance.put(ColorHero.PURPLE_ELITE.getStar(), listPurpleElite);
            heroInstance.put(ColorHero.PURPLE.getStar(), listPurpleLegendary);
            heroInstance.put(ColorHero.PURPLE_RARE_3.getStar(), listPurpleRare3);
        }

        /**
         * Get List Random Obj (HeroVO ID)
         *
         * @param type same color hero
         * @return
         */
        private List<RandomObj> getListRandomHero(int type) {
            switch (ColorHero.fromValue(type)) {
                case BLUE_N_PURPLE:
                    return heroInstance.get(ColorHero.BLUE_N_PURPLE.getStar());
                case PURPLE:
                    return heroInstance.get(ColorHero.PURPLE.getStar());
                case PURPLE_RARE_3:
                    return heroInstance.get(ColorHero.PURPLE_RARE_3.getStar());
                case BLUE:
                    return heroInstance.get(ColorHero.BLUE.getStar());
                case GREEN:
                    return heroInstance.get(ColorHero.GREEN.getStar());
                case PURPLE_ELITE:
                    return heroInstance.get(ColorHero.PURPLE_ELITE.getStar());
            }
            return new ArrayList<>();
        }

        /**
         * Rare save
         *
         * @param idSummon
         * @param summonType
         * @param idCondition
         * @return
         */
        private String getKeySummonRare(String idSummon, ESummonType summonType, String idCondition) {
            return idSummon + ServerConstant.SEPARATER + summonType + ServerConstant.SEPARATER + idCondition;
        }

        private final LIZRandom getSummonRare(String idSummon, ESummonType summonType, String idCondition) {
            String keySummonRare = getKeySummonRare(idSummon, summonType, idCondition);
            LIZRandom lizRandomGet = summonRareInstance.get(keySummonRare);
            if (lizRandomGet != null) return lizRandomGet;

            SummonVO summonCf = CharactersConfigManager.getInstance().getSummonConfig(idSummon);
            //Loc hero theo TYPE SUMMON
            Set<String> listHeroFilterType = new HashSet<>();
            if (summonType == null) summonType = ESummonType.fromID(summonCf.type);
            switch (summonType) {
                case RANDOM:
                    //Loc KINGDOM ENABLE
                    Set<String> kingdomEnable = CharactersConfigManager.getInstance().getKingdomConfig().parallelStream().
                            filter(kingdom -> kingdom.status == EStatus.ENABLE.getId()).
                            map(kingdom -> kingdom.id).
                            collect(Collectors.toSet());
                    //Loc ELEMENT ENABLE

                    //Loc cac hero co kingdom + element ENABLE
                    listHeroFilterType = CharactersConfigManager.getInstance().getHeroConfig().parallelStream().
                            filter(hero -> kingdomEnable.contains(hero.kingdom)).
                            map(hero -> hero.id).
                            collect(Collectors.toSet());
                    break;
                case DEPEND_ELEMENT:        //Phu thuoc vao ELEMENT
                case CHOOSE_ELEMENT:
                    listHeroFilterType = CharactersConfigManager.getInstance().getHeroConfigBy(null, null, idCondition).parallelStream().
                            map(obj -> obj.id).
                            collect(Collectors.toSet());
                    break;
                case DEPEND_KINGDOM:        //Phu thuoc vao KINGDOM
                case CHOOSE_KINGDOM:
                    listHeroFilterType = CharactersConfigManager.getInstance().getHeroConfigBy(idCondition, null, null).parallelStream().
                            map(obj -> obj.id).
                            collect(Collectors.toSet());
                    break;
            }

            //List randomObj -> RARE STAR HERO
            List<RandomObj> listRateStar = summonCf.getListRateSummonType();
            //List randomObj -> RARE HERO
            List<RandomObj> listRateHero;

            //List randomObj -> SUMMON
            List<RandomObj> listRateSummon = new ArrayList<>();
            List<RandomObj> listHeroFilter;
            HeroSummonVO rdObjRateHero;
            int countRateHero;
            for (RandomObj rateStar : listRateStar) {
                //List randomObj -> RARE HERO
                listRateHero = getListRandomHero((short) rateStar.value);
                countRateHero = 0;

                listHeroFilter = new ArrayList<>();
                for (RandomObj index : listRateHero) {
                    rdObjRateHero = (HeroSummonVO) index.value;
                    if (listHeroFilterType.contains(rdObjRateHero.idHero)) {
                        listHeroFilter.add(index);
                        countRateHero += CharactersConfigManager.getInstance().getHeroConfig(rdObjRateHero.idHero).rare;
                    }
                }
                for (RandomObj rateHero : listHeroFilter) {
                    //Rate = rate sao / so luong Hero cua rate sao * ( Rare Hero / sum rate hero )
                    //Rate cang nho ti le cang thap
                    //(Lam tong quat bo qua TH so luong Hero cua rate sao = 0)
                    listRateSummon.add(new RandomObj(rateHero.value, 1.0 * rateStar.rate / listHeroFilter.size() * (1.0 * rateStar.rate * listHeroFilter.size() / countRateHero)));
                }
            }

            //ADD RARE
            LIZRandom rd = new LIZRandom();
            for (RandomObj index : listRateSummon) {
                rd.push(index);
            }

            //ADD SUMMON RARE INSTANCE
            summonRareInstance.put(keySummonRare, rd);
            return rd;
        }


        /*------------------------------------------------------------------------------------------------------------*/
        /*------------------------------------------------------------------------------------------------------------*/
        /**
         * Get User Summon Model
         */
        public UserSummonHeroModel getUserSummonHeroModel(long uid, Zone zone) {
            UserSummonHeroModel userSummonHeroModel = UserSummonHeroModel.copyFromDBtoObject(uid, zone);
            if (userSummonHeroModel == null) {
                userSummonHeroModel = UserSummonHeroModel.createUserSummonHeroModel(uid, zone);
            }
            return userSummonHeroModel;
        }

        /**
         * Update bonus User Summon Model
         */
        public boolean updateBonusUserSummonHeroModel(long uid, int point, Zone zone) {
            UserSummonHeroModel userSummonHeroModel = getUserSummonHeroModel(uid, zone);
            return updateBonusUserSummonHeroModel(userSummonHeroModel, point, zone);
        }

        public boolean updateBonusUserSummonHeroModel(UserSummonHeroModel userSummonHeroModel, int point, Zone zone) {
            return userSummonHeroModel.updateBonusPoint(point, zone);
        }

        /**
         * Get User Summon Model
         */
        public UserSummonSaveModel getUserSaveHeroModel(long uid, Zone zone) {
            UserSummonSaveModel userSummonSaveModel = UserSummonSaveModel.copyFromDBtoObject(uid, zone);
            if (userSummonSaveModel == null) {
                userSummonSaveModel = UserSummonSaveModel.createUserSummonSaveModel(uid, zone);
            }
            return userSummonSaveModel;
        }

        /**
         * Update bao hiem summon
         * @param uid
         * @param zone
         */
        public void updateGuaranteedSummon(long uid, Zone zone){
            UserModel userModel = ((BaseExtension)zone.getExtension()).getUserManager().getUserModel(uid);
            UserSummonSaveModel userSummonSaveModel = getUserSaveHeroModel(uid, zone);
            //3 ngay ko online
            if(!userSummonSaveModel.haveGuaranteedPurple10() && userModel.lastLogout > 0 && Utils.getTimestampInSecond() - userModel.lastLogout >= 86400 * 3){
                userSummonSaveModel.updateGuaranteedPurple10(true, zone);
            }
        }

        /**
         * Summon Fragment
         */
        private List<HeroSummonVO> getHeroSummonFragment(String idFragment, int count) {
            FragmentConfigVO fragmentVO = ItemManager.getInstance().getFragmentConfig(idFragment);
            List<HeroSummonVO> listSummon = null;
            switch (Fragment.fromID(idFragment)) {
                case RANDOM:
                    listSummon = getHeroSummon(Fragment.getIdSummon(idFragment), ESummonType.RANDOM, count, null);
                    break;
                case HERO:
                    listSummon = new ArrayList<>();
                    HeroVO heroCf = CharactersConfigManager.getInstance().getHeroConfig(Fragment.getIdSummon(idFragment));
                    if (heroCf == null || fragmentVO == null) return null;

                    for (int i = 0; i < count; i++) {
                        if (fragmentVO.star < heroCf.star || fragmentVO.star > heroCf.maxStar) {
                            listSummon.add(HeroSummonVO.create(heroCf.id, heroCf.star));
                            new InvalidFragmentConfigException().printStackTrace();
                        } else {
                            listSummon.add(HeroSummonVO.create(heroCf.id, fragmentVO.star));
                        }
                    }
                    break;
            }
            return listSummon;
        }

        /**
         * Random Hero DEPEND MODEL (Chi dung cho summon = banner)
         */
        private List<HeroSummonVO> getHeroSummonBanner(long uid, String idSummon, ESummonType summonType, String condition, int count, Zone zone) {
            SummonVO summonCf = CharactersConfigManager.getInstance().getSummonConfig(idSummon);
            if (summonCf == null) return null;

            UserSummonHeroModel userSummonHeroModel = getUserSummonHeroModel(uid, zone);
            UserSummonSaveModel userSummonSaveModel = getUserSaveHeroModel(uid, zone);
            String idCondition = null;
            ESummonType type = (summonType != null) ? summonType : ESummonType.fromID(summonCf.type);
            switch (type) {
                case RANDOM:
                    break;
                case DEPEND_KINGDOM:
                    idCondition = CharactersConfigManager.getInstance().getKingdomConfig().parallelStream().
                            filter(obj -> EStatus.ENABLE.getId() == obj.status).
                            collect(Collectors.toList()).
                            get(userSummonHeroModel.readKingdomDay(zone)).id;
                    break;
                case DEPEND_ELEMENT:
                    idCondition = CharactersConfigManager.getInstance().getElementConfig().get(userSummonHeroModel.readElementDay(zone)).id;
                    break;
                case CHOOSE_KINGDOM:
                case CHOOSE_ELEMENT:
                    idCondition = condition;
                    break;
            }


            //Xu ly tung loai summon
            List<HeroSummonVO> listHeroSummon = getHeroSummon(idSummon, summonType, count, idCondition);
            boolean havePurple = false;
            switch (ESummonID.fromID(idSummon)){
                case BANNER_NORMAL:
                case BANNER_KINGDOM:
                case BANNER_ELEMENT:
                    //Xu ly summon bao hiem
                    int countSummoned = userSummonSaveModel.readSummon1("first");
                    int countNotHavePurple = userSummonSaveModel.readSummon1(idSummon);

                    //Bao hiem offline
                    if(userSummonSaveModel.haveGuaranteedPurple10() && count == 10){
                        listHeroSummon = new ArrayList<>();
                        listHeroSummon.addAll(getHeroSummon(ESummonID.ALL_PURPLE.getId(), summonType, 1, idCondition));
                        listHeroSummon.addAll(getHeroSummon(idSummon, summonType, 9, idCondition));
                        userSummonSaveModel.updateGuaranteedPurple10(false, zone);
                    }
                    //Bao hiem thong thuong (du luot trieu hoi)
                    if(countSummoned <= 100){
                        switch (count){
                            case 1:
                            case 5:
                            case 10:
                                if(10 <= countSummoned + count && countSummoned + count < 20 && countNotHavePurple + count >= 10){
                                    havePurple = true;

                                    listHeroSummon = new ArrayList<>();
                                    listHeroSummon.addAll(getHeroSummon(ESummonID.ALL_PURPLE.getId(), summonType, 1, idCondition));
                                    listHeroSummon.addAll(getHeroSummon(idSummon, summonType, count - 1, idCondition));
                                }else if(20 <= countSummoned + count && countSummoned + count < 30 && countNotHavePurple + count >= 10){
                                    havePurple = true;

                                    listHeroSummon = new ArrayList<>();
                                    listHeroSummon.addAll(getHeroSummon(ESummonID.ALL_PURPLE.getId(), summonType, 1, idCondition));
                                    listHeroSummon.addAll(getHeroSummon(idSummon, summonType, count - 1, idCondition));
                                }else {
                                    //Co tim -> bo qua
                                    if (listHeroSummon.stream().anyMatch(obj -> obj.star >= ColorHero.PURPLE.getStar())) {
                                        havePurple = true;
                                    } else {
                                        //Khong co tim -> xu ly bao hiem
                                        //SUMON du 30 lan chac chan co HERO TIM
                                        if (countNotHavePurple + count >= 20) {
                                            havePurple = true;

                                            listHeroSummon = new ArrayList<>();
                                            listHeroSummon.addAll(getHeroSummon(ESummonID.ALL_PURPLE.getId(), summonType, 1, idCondition));
                                            listHeroSummon.addAll(getHeroSummon(idSummon, summonType, count - 1, idCondition));
                                        }
                                    }
                                }
                                break;
                            default:
                                return null;
                        }
                    }else {
                        switch (count){
                            case 1:
                            case 5:
                            case 10:
                                //Co tim -> bo qua
                                if (listHeroSummon.stream().anyMatch(obj -> obj.star >= ColorHero.PURPLE.getStar())) {
                                    havePurple = true;
                                } else {
                                    havePurple = false;
                                    //Khong co tim -> xu ly bao hiem
                                    //SUMON du 30 lan chac chan co HERO TIM
                                    if (countNotHavePurple + count >= 30) {
                                        havePurple = true;

                                        listHeroSummon = new ArrayList<>();
                                        listHeroSummon.addAll(getHeroSummon(ESummonID.ALL_PURPLE.getId(), summonType, 1, idCondition));
                                        listHeroSummon.addAll(getHeroSummon(idSummon, summonType, count - 1, idCondition));
                                    }
                                }
                                break;
                            default:
                                return null;
                        }
                    }
                    //Save count summon
                    userSummonSaveModel.updateSummon1("first", countSummoned + count, zone);
                    //Su ly dem bao hiem
                    if(havePurple){
                        //Co HERO TIM
                        userSummonSaveModel.updateSummon1(idSummon, 0, zone);
                    }else {
                        //Khong co HERO TIM
                        userSummonSaveModel.updateSummon1(idSummon, countNotHavePurple + count, zone);
                    }
                    break;
                case BANNER_FRIEND:
                case SPECIAL_PURPLE_RANDOM_HERO_CHOOSE_KINGDOM:
                    break;
                default:
                    return null;
            }


            //Xu ly summon so luong
            List<HeroSummonVO> listHeroGet = new ArrayList<>();
            switch (count){
                case 1:
                case 5:
                    listHeroGet.addAll(listHeroSummon);
                    break;
                case 10:
                    List<HeroSummonVO> listHeroPurple = new ArrayList<>();
                    List<HeroSummonVO> listHeroBlue = new ArrayList<>();
                    List<HeroSummonVO> listHeroGreen = new ArrayList<>();
                    for (HeroSummonVO summon : listHeroSummon) {
                        if (summon.star == ColorHero.PURPLE.getStar()) listHeroPurple.add(summon);
                        else if (summon.star == ColorHero.BLUE.getStar()) listHeroBlue.add(summon);
                        else if (summon.star == ColorHero.GREEN.getStar()) listHeroGreen.add(summon);
                    }

                    //0 HERO TIM - 3 < n < 7 HERO XANH DUONG - 3 < n < 7 HERO XANH LA
                    //1 HERO TIM - 2 < n < 5 HERO XANH DUONG - 4 < n < 7 HERO XANH LA
                    //2 HERO TIM - 1 < n < 4 HERO XANH DUONG - 4 < n < 7 HERO XANH LA
                    int countChange;
                    switch (listHeroPurple.size()){
                        case 0:
                            if(4 <= listHeroBlue.size() && listHeroBlue.size() <= 6){
                                listHeroGet.addAll(listHeroPurple);
                                listHeroGet.addAll(listHeroBlue);
                                listHeroGet.addAll(listHeroGreen);
                            }else if (listHeroBlue.size() < 4) { //TH BLUE qua it - GREEN qua nhieu
                                countChange = Utils.randRange(4, 6);

                                listHeroGet.addAll(listHeroPurple);
                                listHeroGet.addAll(listHeroBlue);
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_BLUE.getId(), summonType, countChange - listHeroBlue.size(), idCondition));
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_GREEN.getId(), summonType, 10 - listHeroGet.size(), idCondition));
                            }else { //TH BLUE qua nhieu - GREEN qua it
                                countChange = Utils.randRange(4, 6);

                                listHeroGet.addAll(listHeroPurple);
                                listHeroGet.addAll(listHeroGreen);
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_GREEN.getId(), summonType, countChange - listHeroGreen.size(), idCondition));
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_BLUE.getId(), summonType, 10 - listHeroGet.size(), idCondition));
                            }
                            break;
                        case 1:
                            if(3 <= listHeroBlue.size() && listHeroBlue.size() <= 4){
                                listHeroGet.addAll(listHeroPurple);
                                listHeroGet.addAll(listHeroBlue);
                                listHeroGet.addAll(listHeroGreen);
                            }else if (listHeroBlue.size() < 3) { //TH BLUE qua it - GREEN qua nhieu
                                countChange = Utils.randRange(3, 4);

                                listHeroGet.addAll(listHeroPurple);
                                listHeroGet.addAll(listHeroBlue);
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_BLUE.getId(), summonType, countChange - listHeroBlue.size(), idCondition));
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_GREEN.getId(), summonType, 10 - listHeroGet.size(), idCondition));
                            }else { //TH BLUE qua nhieu - GREEN qua it
                                countChange = Utils.randRange(3, 4);

                                listHeroGet.addAll(listHeroPurple);
                                listHeroGet.addAll(listHeroGreen);
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_GREEN.getId(), summonType, countChange - listHeroGreen.size(), idCondition));
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_BLUE.getId(), summonType, 10 - listHeroGet.size(), idCondition));
                            }
                            break;
                        case 2:
                            if(2 <= listHeroBlue.size() && listHeroBlue.size() <= 4){
                                listHeroGet.addAll(listHeroPurple);
                                listHeroGet.addAll(listHeroBlue);
                                listHeroGet.addAll(listHeroGreen);
                            }else if (listHeroBlue.size() < 2) { //TH BLUE qua it - GREEN qua nhieu
                                countChange = Utils.randRange(2, 4);

                                listHeroGet.addAll(listHeroPurple);
                                listHeroGet.addAll(listHeroBlue);
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_BLUE.getId(), summonType, countChange - listHeroBlue.size(), idCondition));
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_GREEN.getId(), summonType, 10 - listHeroGet.size(), idCondition));
                            }else { //TH BLUE qua nhieu - GREEN qua it
                                countChange = Utils.randRange(2, 4);

                                listHeroGet.addAll(listHeroPurple);
                                listHeroGet.addAll(listHeroGreen);
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_GREEN.getId(), summonType, countChange - listHeroGreen.size(), idCondition));
                                listHeroGet.addAll(getHeroSummon(ESummonID.ALL_BLUE.getId(), summonType, 10 - listHeroGet.size(), idCondition));
                            }
                            break;
                        default:
                            listHeroGet.addAll(getHeroSummon(ESummonID.ALL_PURPLE.getId(), summonType, 2, idCondition));
                            listHeroGet.addAll(getHeroSummon(ESummonID.ALL_BLUE.getId(), summonType, 5, idCondition));
                            listHeroGet.addAll(getHeroSummon(ESummonID.ALL_GREEN.getId(), summonType, 3, idCondition));
                            break;
                    }
                    Collections.shuffle(listHeroGet);
                    break;
                default:
                    //TH ngoai summon 1 / 10
                    //CHUA CO KICH BAN
                    break;
            }

            //Save --- tong lan summon
            userSummonHeroModel.updateCountSummon(count, zone);

            return listHeroGet;
        }

        /**
         * Random Hero DEPEND MODEL (Chi dung cho summon = Special)
         */
        private List<HeroSummonVO> getHeroSummonSpecial(long uid, String idSummon, ESummonType summonType, String condition, int count, Zone zone) {
            UserSummonHeroModel userSummonHeroModel = getUserSummonHeroModel(uid, zone);
            return getHeroSummonSpecial(userSummonHeroModel, idSummon, summonType, condition, count, zone);
        }
        private List<HeroSummonVO> getHeroSummonSpecial(UserSummonHeroModel userSummonHeroModel, String idSummon, ESummonType summonType, String condition, int count, Zone zone) {
            SummonVO summonCf = CharactersConfigManager.getInstance().getSummonConfig(idSummon);
            if (summonCf == null) {
                return null;
            }

            String id = null;
            ESummonType type = (summonType != null) ? summonType : ESummonType.fromID(summonCf.type);
            switch (type) {
                case RANDOM:
                    break;
                case DEPEND_KINGDOM:
                    id = CharactersConfigManager.getInstance().getKingdomConfig().parallelStream().
                            filter(obj -> EStatus.ENABLE.getId() == obj.status).
                            collect(Collectors.toList()).
                            get(userSummonHeroModel.readKingdomDay(zone)).id;
                    break;
                case DEPEND_ELEMENT:
                    id = CharactersConfigManager.getInstance().getElementConfig().get(userSummonHeroModel.readElementDay(zone)).id;
                    break;
                case CHOOSE_KINGDOM:
                case CHOOSE_ELEMENT:
                    id = condition;
                    break;
            }

            //List summon
            return getHeroSummon(idSummon, summonType, count, id);
        }


        private List<HeroSummonVO> getHeroSummon(String idSummon, ESummonType summonType, int count, String idCondition) {
            //RARE SUMMON INSTANCE
            LIZRandom rd = getSummonRare(idSummon, summonType, idCondition);
            //RANDOM
            List<HeroSummonVO> listReward = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                listReward.add((HeroSummonVO) rd.next().value);
            }
            return listReward;
        }


        /**
         * Summon + add Model (All ca ben ngoai goi vao ham nay)
         */
        public List<HeroSummonVO> summonUserHero(long uid, String idSummon, ESummonType summonType, String condition, ResourceType type, int count, Zone zone) {
            List<HeroSummonVO> listSummon = new ArrayList<>();
            switch (type) {
                case MONEY: //Banner
                    listSummon = getHeroSummonBanner(uid, idSummon, summonType, condition, count, zone);
                    break;
                case SPECIAL_ITEM:
                    switch (SpecialItem.fromID(idSummon)) {
                        case BANISHED_KINGDOM_EPIC_HERO_CARD_BLUE:
                        case DRUID_KINGDOM_EPIC_HERO_CARD_BLUE:
                        case DWARF_KINGDOM_EPIC_HERO_CARD_BLUE:
                        case RANDOM_EPIC_HERO_SHARDS_BLUE:
                            idSummon = ESummonID.ALL_BLUE.getId();
                            break;
                        case RANDOM_LEGENDARY_HERO_SHARDS_PURPLE:
                            idSummon = ESummonID.PURPLE_ELITE.getId();
                            break;
                    }
                    listSummon = getHeroSummonSpecial(uid, idSummon, summonType, condition, count, zone);
                    break;
                case FRAGMENT_HERO:
                    listSummon = getHeroSummonFragment(idSummon, count);
                    break;
            }
            return listSummon;
        }

        /**
         * Add to Model (Dung cho all Summon)
         *
         * @param uid
         * @param listSummon
         * @param zone
         * @return
         */
        public List<HeroModel> summonUserHero(long uid, String idSummon, List<HeroSummonVO> listSummon, Zone zone, boolean sendRetireToPlayer, SendRetireHero sendTireHero) {
            List<HeroModel> listAdd = new ArrayList<>();
            for (HeroSummonVO summon : listSummon) {
                listAdd.add(HeroModel.createHeroModel(uid, summon.idHero, summon.star, EHeroType.NORMAL));
            }
            if (!HeroManager.getInstance().addUserAllHeroModel(uid, listAdd, zone, sendRetireToPlayer, sendTireHero)) {
                return null;
            }

            if (idSummon != null) {
                //Tang diem tich luy summon Model
                updateBonusUserSummonHeroModel(getUserSummonHeroModel(uid, zone), listSummon.size() * CharactersConfigManager.getInstance().getBonusPointSummonConfig(idSummon), zone);
            }

            return listAdd;
        }


        /**
         * Lay bonus ruong summon hero
         *
         * @param uid
         * @param zone
         * @return
         */
        public List<RewardVO> getBonusSummonUserHero(long uid, Zone zone) {
            UserSummonHeroModel userSummonHeroModel = getUserSummonHeroModel(uid, zone);

            List<BonusSummonVO> listBonus = userSummonHeroModel.getBonus(zone);
            if (listBonus == null) {
                return null;
            }
            List<RewardVO> listReward = new ArrayList<>();
            for (int i = 0; i < listBonus.size(); i++) {
                for (int j = 0; j < listBonus.get(i).reward.size(); j++) {
                    listReward.add(listBonus.get(i).reward.get(j));
                }
            }
            return listReward;
        }

        /**
         * Tieu tai nguyen cho Summon
         *
         **/
        public List<TokenResourcePackage> useResourceSummonUserHero(long uid, String idSummon, int count, Zone zone, String moneyType) {
            UserSummonHeroModel userSummonHeroModel = HeroManager.SummonManager.getInstance().getUserSummonHeroModel(uid, zone);
            //TH dac biet co FREE BANNER
            if (count == 1 && userSummonHeroModel.summonBannerFree(idSummon, zone)) {
                return null;
            }

            //Lay Summon Config
            SummonVO summonCf = CharactersConfigManager.getInstance().getSummonConfig(idSummon);
            CostSummonVO summonVO = null;
            for (CostSummonVO vo : summonCf.cost) {
                if (vo.money.equals(moneyType)) {
                    summonVO = vo;
                }
            }

            if (summonVO == null) {
                return null;
            }

            List<TokenResourcePackage> resourceUse = summonVO.chargeResource(count);
            if (resourceUse.size() == 0) {
                return null;
            }

            if (!BagManager.getInstance().addItemToDB(resourceUse, uid, zone, UserUtils.TransactionType.SUMMON_HERO)) {
                return null;
            }

            return resourceUse;
        }

        /**
         * Tieu tai nguyen cho Summon
         *
         * @param idSummon
         * @param count
         * @return
         */
        public List<TokenResourcePackage> useResourceSummonUserHero(long uid, String idSummon, int count, Zone zone) {
            UserSummonHeroModel userSummonHeroModel = HeroManager.SummonManager.getInstance().getUserSummonHeroModel(uid, zone);
            //Lay Summon Config
            SummonVO summonCf = CharactersConfigManager.getInstance().getSummonConfig(idSummon);
            List<String> idResource = summonCf.getResourceType();
            if (idResource.size() <= 0) {
                return null;
            }
            //TH dac biet co FREE BANNER
            if (count == 1 && userSummonHeroModel.summonBannerFree(idSummon, zone)) {
                return new ArrayList<>();
            }

            List<TokenResourcePackage> resourceUse;
            //List MoneyType theo thu tu ENUM
            List<MoneyType> moneyTypes = Arrays.asList(MoneyType.values());
            //DO UU TIEN = thu tu sap xep trong ENUM MONEY TYPE
            for (int i = moneyTypes.size() - 1; i >= 0; i--) {
                for (int j = 0; j < idResource.size(); j++) {
                    if (moneyTypes.get(i).getId().equals(idResource.get(j))) {
                        //Ko du tai nguyen Summon -> Chuyen sang loai tai nguyen khac
                        resourceUse = summonCf.getResource(MoneyType.fromID(idResource.get(j)), count);
                        if (!BagManager.getInstance().addItemToDB(resourceUse, uid, zone, UserUtils.TransactionType.SUMMON_HERO)) {
                            continue;
                        }
                        //Du tai nguyen -> return
                        return resourceUse;
                    }
                }
            }

            //List Token Type theo thu tu ENUM
            List<ETokenBC> tokenTypes = Arrays.asList(ETokenBC.values());
            //DO UU TIEN = thu tu sap xep trong ENUM TOKEN TYPE
            for(int i = tokenTypes.size() - 1; i >= 0; i--){
                for(int j = 0; j < idResource.size(); j++){
                    if(tokenTypes.get(i).getId().equals(idResource.get(j))){
                        resourceUse = summonCf.getResource(ETokenBC.fromId(idResource.get(j)), count);
                        //Ko du tai nguyen Summon -> Chuyen sang loai tai nguyen khac
                        if(!NFTManager.getInstance().updateToken(uid, resourceUse, UserUtils.TransactionType.SUMMON_HERO, zone).isSuccess()){
                            continue;
                        }
                        //Du tai nguyen -> return
                        return resourceUse;
                    }
                }
            }

            //TH con lai auto false
            return null;
        }


        /**
         * Update DAY UserSummonModel
         */
        public boolean updateDayUserSummonModel(long uid, String id, HeroResource type, Zone zone) {
            UserSummonHeroModel userSummonHeroModel = getUserSummonHeroModel(uid, zone);
            return updateDayUserSummonModel(userSummonHeroModel, id, type, zone);
        }

        public boolean updateDayUserSummonModel(UserSummonHeroModel userSummonHeroModel, String idCf, HeroResource type, Zone zone) {
            switch (type) {
                case KINGDOM:
                    return userSummonHeroModel.updateKingdomDay(idCf, zone);
                case ELEMENT:
                    return userSummonHeroModel.updateElementDay(idCf, zone);
            }
            return false;
        }

        /**
         * Tieu tai nguyen update DAY UserSummonModel
         */
        public boolean useResourceUpdateDaySummonUserHero(long uid, String id, HeroResource type, Zone zone) {
            List<MoneyPackageVO> listMoneyPackageVO = null;
            TransactionDetail transactionDetail = null;
            switch (type) {
                case KINGDOM:
                    transactionDetail = UserUtils.TransactionType.UPDATE_KINGDOM_SUMMON_BANNER;
                    listMoneyPackageVO = CharactersConfigManager.getInstance().getCostUpdateDayUserSummonModel(id);
                    break;
                case ELEMENT:
                    transactionDetail = UserUtils.TransactionType.UPDATE_ELEMENT_SUMMON_BANNER;
                    listMoneyPackageVO = CharactersConfigManager.getInstance().getCostUpdateDayUserSummonModel(id);
                    break;
            }
            if (listMoneyPackageVO == null || transactionDetail == null) {
                return false;
            }
            return BagManager.getInstance().addItemToDB(listMoneyPackageVO, uid, zone, transactionDetail);
        }
    }


    /*--------------------------------------------- CREEP -----------------------------------------*/
    public static class CreepManager {
        private static CreepManager ourInstance = new CreepManager();

        private CreepManager() {
        }

        public static CreepManager getInstance() {
            return ourInstance;
        }

        /**
         * Get Stats HeroVO Model
         */
        public Stats getStatsCreep(CreepInstanceVO creepInstance) {
            if (creepInstance == null) {
                return new Stats();
            }
            CreepVO creepVOCf = CharactersConfigManager.getInstance().getCreepConfig(creepInstance.id);
            List<Short> listLevelBreakThoughtCf = CharactersConfigManager.getInstance().getListLevelBreakThoughtConfig();
            CharacterStatsGrowVO growCf = CharactersConfigManager.getInstance().getCreepStatsGrowConfig(creepInstance.id);
            int breakThought = 0;
            for (int i = 0; i < listLevelBreakThoughtCf.size(); i++) {
                if (listLevelBreakThoughtCf.get(i) > creepInstance.level) {
                    breakThought = i;
                    break;
                }
            }
            if (creepVOCf == null) {
                return new Stats();
            }

            Stats stats = new Stats();
            //Chi so cua rieng tuong chua mac do
            stats.hp = calculationStatsCreep(growCf, (int) creepVOCf.health, Attr.HP, creepInstance.star, creepInstance.level, breakThought);
            stats.strength = calculationStatsCreep(growCf, (int) creepVOCf.strength, Attr.STRENGTH, creepInstance.star, creepInstance.level, breakThought);
            stats.intelligence = calculationStatsCreep(growCf, (int) creepVOCf.intelligence, Attr.INTELLIGENCE, creepInstance.star, creepInstance.level, breakThought);
            stats.dexterity = calculationStatsCreep(growCf, (int) creepVOCf.dexterity, Attr.DEXTERITY, creepInstance.star, creepInstance.level, breakThought);
            stats.armor = calculationStatsCreep(growCf, (int) creepVOCf.armor, Attr.ARMOR, creepInstance.star, creepInstance.level, breakThought);
            stats.magicResistance = calculationStatsCreep(growCf, (int) creepVOCf.magicResistance, Attr.MAGIC_RESISTANCE, creepInstance.star, creepInstance.level, breakThought);
            stats.agility = calculationStatsCreep(growCf, (int) creepVOCf.agility, Attr.AGILITY, creepInstance.star, creepInstance.level, breakThought);
            stats.crit = calculationStatsCreep(growCf, (int) creepVOCf.crit, Attr.CRITICAL_BONUS_DAMAGE, creepInstance.star, creepInstance.level, breakThought);
            stats.critDmg = calculationStatsCreep(growCf, (int) creepVOCf.critBonus, Attr.CRITICAL_CHANCE, creepInstance.star, creepInstance.level, breakThought);
            stats.armorPenetration = calculationStatsCreep(growCf, (int) creepVOCf.armorPenetration, Attr.ARMOR_PENETRATION, creepInstance.star, creepInstance.level, breakThought);
            stats.magicPenetration = calculationStatsCreep(growCf, (int) creepVOCf.magicPenetration, Attr.MAGIC_PENETRATION, creepInstance.star, creepInstance.level, breakThought);
            stats.tenacity = calculationStatsCreep(growCf, (int) creepVOCf.tenacity, Attr.TENACITY, creepInstance.star, creepInstance.level, breakThought);
            stats.elusiveness = calculationStatsCreep(growCf, (int) creepVOCf.elusiveness, Attr.ELUSIVENESS, creepInstance.star, creepInstance.level, breakThought);

            return stats;
        }

        /**
         * Tinh chi so CREEP (Creep chi co theo level)
         *
         * @param attr
         * @param level
         * @param star
         * @return
         */
        private float calculationStatsCreep(CharacterStatsGrowVO growCf, int statsHeroConfig, Attr attr, int star, int level, int breakThought) {
            return calculationStatCharacter(growCf, statsHeroConfig, attr, star, level - 1, breakThought);
        }
    }

    /*--------------------------------------------- MBOSS -----------------------------------------*/
    public static class MBossManager {
        private static MBossManager ourInstance = new MBossManager();

        private MBossManager() {
        }

        public static MBossManager getInstance() {
            return ourInstance;
        }

        /**
         * Get Stats HeroVO Model
         */
        public Stats getStatsMBoss(MbossInstanceVO mBossInstance) {
            if (mBossInstance == null) {
                return new Stats();
            }
            MbossVO mBossVOCf = CharactersConfigManager.getInstance().getMbossConfig(mBossInstance.id);
            List<Short> listLevelBreakThoughtCf = CharactersConfigManager.getInstance().getListLevelBreakThoughtConfig();
            CharacterStatsGrowVO growCf = CharactersConfigManager.getInstance().getMBossStatsGrowConfig(mBossInstance.id);
            int breakThought = 0;
            for (int i = 0; i < listLevelBreakThoughtCf.size(); i++) {
                if (listLevelBreakThoughtCf.get(i) > mBossInstance.level) {
                    breakThought = i;
                    break;
                }
            }
            if (mBossVOCf == null) {
                return new Stats();
            }

            Stats stats = new Stats();
            //Chi so cua rieng tuong chua mac do
            stats.hp = calculationStatsMboss(growCf, (int) mBossVOCf.health, Attr.HP, mBossInstance.star, mBossInstance.level, breakThought);
            stats.strength = calculationStatsMboss(growCf, (int) mBossVOCf.strength, Attr.STRENGTH, mBossInstance.star, mBossInstance.level, breakThought);
            stats.intelligence = calculationStatsMboss(growCf, (int) mBossVOCf.intelligence, Attr.INTELLIGENCE, mBossInstance.star, mBossInstance.level, breakThought);
            stats.dexterity = calculationStatsMboss(growCf, (int) mBossVOCf.dexterity, Attr.DEXTERITY, mBossInstance.star, mBossInstance.level, breakThought);
            stats.armor = calculationStatsMboss(growCf, (int) mBossVOCf.armor, Attr.ARMOR, mBossInstance.star, mBossInstance.level, breakThought);
            stats.magicResistance = calculationStatsMboss(growCf, (int) mBossVOCf.magicResistance, Attr.MAGIC_RESISTANCE, mBossInstance.star, mBossInstance.level, breakThought);
            stats.agility = calculationStatsMboss(growCf, (int) mBossVOCf.agility, Attr.AGILITY, mBossInstance.star, mBossInstance.level, breakThought);
            stats.crit = calculationStatsMboss(growCf, (int) mBossVOCf.crit, Attr.CRITICAL_BONUS_DAMAGE, mBossInstance.star, mBossInstance.level, breakThought);
            stats.critDmg = calculationStatsMboss(growCf, (int) mBossVOCf.critBonus, Attr.CRITICAL_CHANCE, mBossInstance.star, mBossInstance.level, breakThought);
            stats.armorPenetration = calculationStatsMboss(growCf, (int) mBossVOCf.armorPenetration, Attr.ARMOR_PENETRATION, mBossInstance.star, mBossInstance.level, breakThought);
            stats.magicPenetration = calculationStatsMboss(growCf, (int) mBossVOCf.magicPenetration, Attr.MAGIC_PENETRATION, mBossInstance.star, mBossInstance.level, breakThought);
            stats.tenacity = calculationStatsMboss(growCf, (int) mBossVOCf.tenacity, Attr.TENACITY, mBossInstance.star, mBossInstance.level, breakThought);
            stats.elusiveness = calculationStatsMboss(growCf, (int) mBossVOCf.elusiveness, Attr.ELUSIVENESS, mBossInstance.star, mBossInstance.level, breakThought);

            return stats;
        }

        /**
         * Tinh chi so MBOSS (MBOSS chi co theo level)
         *
         * @param statsHeroConfig
         * @param attr
         * @param level
         * @param star
         * @return
         */
        private float calculationStatsMboss(CharacterStatsGrowVO grow, int statsHeroConfig, Attr attr, int star, int level, int breakThought) {
            return calculationStatCharacter(grow, statsHeroConfig, attr, star, level - 1, breakThought);
        }
    }
}
