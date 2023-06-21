package com.bamisu.log.gameserver.module.IAPBuy;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.sql.game.dbo.IAPPackageDBO;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.IAP.IAPCacheSaveModel;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoRewardIAPChallenge;
import com.bamisu.log.gameserver.datamodel.IAP.event.IAPEventModel;
import com.bamisu.log.gameserver.datamodel.IAP.event.entities.InfoIAPSale;
import com.bamisu.log.gameserver.datamodel.IAP.home.UserIAPHomeModel;
import com.bamisu.log.gameserver.datamodel.IAP.store.UserIAPStoreModel;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPChallenge;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPPackage;
import com.bamisu.log.gameserver.datamodel.guild.entities.GiftGuildDescription;
import com.bamisu.log.gameserver.datamodel.guild.entities.GiftGuildInfo;
import com.bamisu.log.gameserver.entities.EModule;
import com.bamisu.log.gameserver.manager.ServerManager;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.IAP.IAPManager;
import com.bamisu.log.gameserver.module.IAPBuy.config.*;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EConditionType;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPClaimType;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.*;
import com.bamisu.log.gameserver.module.bag.BagHandler;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.campaign.CampaignManager;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.event.EventInGameManager;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.guild.define.EGuildGiftType;
import com.bamisu.log.gameserver.module.notification.NotificationManager;
import com.bamisu.log.gameserver.module.notification.defind.EActionNotiModel;
import com.bamisu.log.gameserver.module.tower.TowerManager;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.*;
import java.util.stream.Collectors;

public class IAPBuyManager {

    private static IAPBuyManager instance = new IAPBuyManager();

    public static IAPBuyManager getInstance() {
        return instance;
    }

    private IAPBuyManager() {
        //Load config
        loadConfig();
    }

    private IAPStoreConfig iapStoreConfig;
    private IAPHomeConfig iapHomeConfig;

    private IAPPackageConfig iapPackageConfig;
    private IAPChallengeConfig iapChallengeConfig;
    private IAPSpecialPackageConfig iapSpecialPackageConfig;

    private IAPConditionConfig iapConditionConfig;
    private IAPSaleConfig iapSaleConfig;



    private void loadConfig(){
        iapStoreConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Store.FILE_PATH_CONFIG_IAP_STORE), IAPStoreConfig.class);
        iapHomeConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Store.FILE_PATH_CONFIG_IAP_HOME), IAPHomeConfig.class);

        iapPackageConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Store.FILE_PATH_CONFIG_IAP_PACKAGE), IAPPackageConfig.class);
        iapChallengeConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Store.FILE_PATH_CONFIG_IAP_CHALLENGE), IAPChallengeConfig.class);
        iapSpecialPackageConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Store.FILE_PATH_CONFIG_IAP_SPECIAL_PACKAGE), IAPSpecialPackageConfig.class);

        iapConditionConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Store.FILE_PATH_CONFIG_IAP_CONDITION), IAPConditionConfig.class);
        iapSaleConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Store.FILE_PATH_CONFIG_IAP_SALE), IAPSaleConfig.class);
    }



    /*-----------------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------------*/
    /**
     * Get IAP Model
     */
    public UserIAPStoreModel getUserIAPStoreModel(long uid, Zone zone){
        return UserIAPStoreModel.copyFromDBtoObject(uid, zone);
//        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getUserIAPStoreModelCache(uid);
    }
    public UserIAPHomeModel getUserIAPHomeModel(long uid, Zone zone){
        return UserIAPHomeModel.copyFromDBtoObject(uid, zone);
//        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getUserIAPHomeModelCache(uid);
    }

    public List<String> getListTabSpecial(long uid, Zone zone){
        UserIAPHomeModel userIAPHomeModel = getUserIAPHomeModel(uid, zone);
        //Lay toan bo goi dag event
        List<InfoIAPPackage> iapPackage = userIAPHomeModel.readIAPPackage(zone);
        List<InfoIAPChallenge> iapChallenges = userIAPHomeModel.readIAPChallenge(zone);
        //Lay toan bo id
        List<String> listId = new ArrayList<>();
        listId.addAll(iapPackage.parallelStream().map(data -> data.id).collect(Collectors.toList()));
        listId.addAll(iapChallenges.parallelStream().map(data -> data.id).collect(Collectors.toList()));
        //Lay tab -> id tab
        List<String> listIDTabHome = getIAPTabConfigDependPackage(listId).parallelStream().map(tab -> tab.id).collect(Collectors.toList());
        List<String> listIDTabSpecial = getIAPSpecialConfig().stream().map(obj -> obj.id).collect(Collectors.toList());

        return listIDTabHome.stream().filter(listIDTabSpecial::contains).collect(Collectors.toList());
    }



    /*----------------------------------------------- EVENT ----------------------------------------------------------*/
    public IAPEventModel getIAPEventModel(Zone zone){
        return IAPEventModel.copyFromDBtoObject(zone);
//        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getIAPEventModelCache();
    }

    public List<InfoIAPSale> getInfoIAPSale(Zone zone){
        return getInfoIAPSale(getIAPEventModel(zone), zone);
    }
    public List<InfoIAPSale> getInfoIAPSale(IAPEventModel iapEventModel, Zone zone){
        return iapEventModel.readInfoIAPsale(zone);
    }

    public InfoIAPSale getInfoIAPSale(long uid, String idSale, Zone zone){
        for(InfoIAPSale data : getInfoIAPSale(zone)){
            if(data.idSale.equals(idSale) && data.haveSale(uid)) return data;
        }
        return null;
    }

    /**
     * Add Sale
     * @param sale
     * @param zone
     * @return
     */
    public void addSaleIAP(InfoIAPSale sale, Zone zone){
        SFSObject data = new SFSObject();
        data.putUtfString(Params.SALE, Utils.toJson(sale));

        zone.getExtension().handleInternalMessage(CMD.InternalMessage.ADD_SALE_IAP, data);
    }

    /**
     * Remove Sale
     * @param listIdSale
     * @param zone
     * @return
     */
    public void removeSaleIAP(List<String> listIdSale, Zone zone){
        SFSObject data = new SFSObject();
        data.putUtfStringArray(Params.SALE, listIdSale);

        zone.getExtension().handleInternalMessage(CMD.InternalMessage.REMOVE_SALE_IAP, data);
    }



    /*-------------------------------------------- CACHE SAVE GUILD --------------------------------------------------*/
    /**
     * Get cache create IAP
     * @param zone
     * @return
     */
    public IAPCacheSaveModel getIAPCacheSaveModel(Zone zone){
        return IAPCacheSaveModel.copyFromDBtoObject(zone);
    }

    public boolean addCacheIAPSaveModel(IAPPackageDBO iapPackageDBO, Zone zone){
        return getIAPCacheSaveModel(zone).addCache(iapPackageDBO, zone);
    }

    /**
     * Get cache IAP update SQL
     * @param zone
     * @return
     */
    public List<IAPPackageDBO> readCacheIAPSaveModel(Zone zone){
        return getIAPCacheSaveModel(zone).readCache();
    }
    public List<IAPPackageDBO> readCacheIAPSaveModel(IAPCacheSaveModel iapCacheSaveModel){
        return iapCacheSaveModel.readCache();
    }

    /**
     * Clear cache create guild SQL
     * @param zone
     * @return
     */
    public boolean clearCacheIAPSaveModel(Zone zone){
        return getIAPCacheSaveModel(zone).clearCache(zone);
    }
    public boolean clearCacheIAPSaveModel(IAPCacheSaveModel iapCacheSaveModel, Zone zone){
        return iapCacheSaveModel.clearCache(zone);
    }






    /*--------------------------------------------------- PACKAGE ITEM ------------------------------------------------------*/
    /**
     * Get list package da mua
     */
    public List<InfoIAPPackage> getInfoIAPPackageUserModel(long uid, Zone zone){
        List<InfoIAPPackage> get = new ArrayList<>();
        get.addAll(getUserIAPStoreModel(uid, zone).readIAPPackage(zone));
        get.addAll(getUserIAPHomeModel(uid, zone).readIAPPackage(zone));
        return get;
    }
    public InfoIAPPackage getInfoIAPPackageUserModel(long uid, String idPackage, Zone zone){
        for(InfoIAPPackage data : getInfoIAPPackageUserModel(uid, zone)){
            if(data.id.equals(idPackage)){
                return data;
            }
        }
        return null;
    }


    /**
     * Get info list package
     */
    public List<InfoIAPPackage> getInfoIAPPackageUserModelDependByTab(long uid, String idTab, Zone zone){
        //IAP package != IAP Challenge
        List<String> listId = getListIdIAPPackageFromTab(idTab);
        return getInfoIAPPackageUserModel(uid, zone).stream().
                filter(obj -> listId.contains(obj.id)).
                collect(Collectors.toList());
    }

    /**
     * Kiem tra co the mua khong
     */
    public boolean canClaimIAPPackage(UserIAPStoreModel userIAPStoreModel, UserIAPHomeModel userIAPHomeModel, String idPackage, Zone zone){
        IAPPackageVO packageCf = getIAPPackageConfig(idPackage, zone);
        return canClaimIAPPackage(userIAPStoreModel, userIAPHomeModel, packageCf, zone);
    }
    public boolean canClaimIAPPackage(UserIAPStoreModel userIAPStoreModel, UserIAPHomeModel userIAPHomeModel, IAPPackageVO packageCf, Zone zone){
        //Kiem tra xem co dieu kien de mua ko
        if(packageCf == null) return false;
        if(packageCf.haveConditionReward()){
            IAPConditionVO conditionCf;

            for(String idCondition : packageCf.rewardCondition){
                conditionCf = getIAPConditionConfig(idCondition);
                //Chi can 1 dk khong thoa man la false
                if(!haveEnoughCondition(userIAPStoreModel.uid, EConditionType.fromID(conditionCf.condition.type), conditionCf.condition.formula.get(0).s, zone)){
                    return false;
                }
            }
        }
        if(getIapPackageStoreConfig(packageCf.id, zone) != null) return userIAPStoreModel.canClaimIAPPackage(packageCf.id, zone);
        if(getIAPSpecialPackageConfig(packageCf.id) != null) return userIAPHomeModel.canClaimIAPPackage(packageCf.id, zone);
        return false;
    }

    /**
     * Lay ra cac package co the nhan dc
     * @param uid
     * @param zone
     * @return
     */
    public List<String> getListIAPPackageCanClaim(long uid, Zone zone){
        List<String> listId = new ArrayList<>();

        UserIAPStoreModel userIAPStoreModel = getUserIAPStoreModel(uid, zone);
        UserIAPHomeModel userIAPHomeModel = getUserIAPHomeModel(uid, zone);

        for(InfoIAPPackage pack : userIAPStoreModel.readIAPPackage(zone)){
            if(canClaimIAPPackage(userIAPStoreModel, userIAPHomeModel, pack.id, zone)){
                listId.add(pack.id);
            }
        }
        for(InfoIAPPackage pack : userIAPHomeModel.readIAPPackage(zone)){
            if(canClaimIAPPackage(userIAPStoreModel, userIAPHomeModel, pack.id, zone)){
                listId.add(pack.id);
            }
        }
        return listId;
    }

    public List<String> getListIAPHaveExsist(long uid, Zone zone){
        List<String> listId = new ArrayList<>();

        UserIAPHomeModel userIAPHomeModel = getUserIAPHomeModel(uid, zone);

        List<String> listIdIAPHaveInstance = new ArrayList<>();
        List<IAPPackageVO> packCf = IAPBuyManager.getInstance().getIAPPackageHomeConfig();
        listIdIAPHaveInstance.addAll(packCf.stream().filter(cf -> !cf.haveConditionExsist()).map(cf -> cf.id).collect(Collectors.toList()));
        List<IAPChallengeVO> challengeCf = IAPBuyManager.getInstance().getIAPChallengeHomeConfig();
        listIdIAPHaveInstance.addAll(challengeCf.stream().filter(cf -> !cf.haveConditionExsist()).map(cf -> cf.id).collect(Collectors.toList()));

        for(InfoIAPPackage pack : userIAPHomeModel.readIAPPackage(zone)){
            if(!listIdIAPHaveInstance.contains(pack.id)){
                listId.add(pack.id);
            }
        }
        for(InfoIAPChallenge challenge : userIAPHomeModel.readIAPChallenge(zone)){
            if(!listIdIAPHaveInstance.contains(challenge.id)){
                listId.add(challenge.id);
            }
        }

        return listId;
    }


    private boolean haveEnoughCondition(long uid, EConditionType condition, int point, Zone zone){
        int ss = 0;
        switch (condition){
            case PAYMENT:
                ss = getUserIAPStoreModel(uid, zone).countPayment + getUserIAPHomeModel(uid, zone).countPayment;
                break;
            case LEVEL_USER:
                ss = BagManager.getInstance().getLevelUser(uid, zone);
                break;
            case CHAP_DUNGEON:
                ss = Integer.parseInt(CampaignManager.getInstance().getUserCampaignDetailModel(zone, uid).userMainCampaignDetail.readNextStation().split(",", 2)[0]);
                break;
            case FLOOR_TOWER:
                ss = TowerManager.getInstance().getFloorUserTowerModel(uid, zone);
                break;
            case HAVE_VIP:
                UserModel userModel = ((BagHandler)((BaseExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_BAG)).getUserModel(uid);

                if(VipManager.getInstance().haveVip(userModel, EVip.ARCHMAGE) || VipManager.getInstance().haveVip(userModel, EVip.PROTECTOR)) ss = point;
                break;
            case HAVE_VIP_1:
            case HAVE_VIP_2:
                if(VipManager.getInstance().haveVip(uid, EVip.fromStrValue(condition.getDescription()), zone)) ss = point;
                break;
            default:
                return false;
        }
        return ss >= point;
    }

    /**
     * Kiem tra co can check tra tien ko
     */
    public boolean haveCheckPaymentIAP(String idPackage, Zone zone){
        IAPPackageVO packageCf = null;
        //IAP package mat phi
        packageCf = getIAPPackageConfig(idPackage, zone);
        if(packageCf != null){
            return packageCf.cost > 0;
        }

        IAPChallengeVO challengeCf = getIAPListGetConfig(idPackage);
        if(challengeCf != null){
            return challengeCf.cost > 0;
        }

        return false;
    }




    /**
     * Claim trong iap store
     */
    public boolean rewardIAPPackage(UserIAPStoreModel userIAPStoreModel, UserIAPHomeModel userIAPHomeModel, String idPackage, String purchaseToken, Zone zone){
        //Khi mua thi tang them 1 trong 2 model (1 model luu so package co the mua - 1 model luu so package da mua)
        IAPPackageVO packageCf = getIAPPackageConfig(idPackage, zone);
        boolean success = false;
        boolean store = false;
        boolean home = false;
        if(!success){
            home = userIAPHomeModel.claimIAPPackage(idPackage, zone);
            success = home;
        }
        if(!success){
            store = userIAPStoreModel.claimIAPPackage(idPackage, zone);
            success = store;
        }

        if(packageCf.cost > 0){
            //Tra phi
            if(home){
                userIAPHomeModel.increasePayment(zone);
            }
            if(store){
                userIAPStoreModel.increasePayment(zone);
            }

            //Save instance buy
            IAPManager.getInstance().boughtIAP(userIAPStoreModel.uid, purchaseToken, zone);
            //Event
            GameEventAPI.ariseGameEvent(EGameEvent.USER_PAYMENT, userIAPStoreModel.uid, new HashMap<>(), zone);

        }
        return success;
    }



    /*--------------------------------------------------- PACKAGE CHALLENGE ------------------------------------------------*/
    /**
     * Lay challenge data
     */
    public InfoIAPChallenge getInfoIAPChallengeUserModel(long uid, String id, Zone zone){
        UserIAPStoreModel userIAPStoreModel = getUserIAPStoreModel(uid, zone);
        UserIAPHomeModel userIAPHomeModel = getUserIAPHomeModel(uid, zone);
        return getInfoIAPChallengeUserModel(userIAPStoreModel, userIAPHomeModel, id, zone);
    }
    public InfoIAPChallenge getInfoIAPChallengeUserModel(UserIAPStoreModel userIAPStoreModel, UserIAPHomeModel userIAPHomeModel, String id, Zone zone){
        InfoIAPChallenge data = null;

        data = userIAPStoreModel.readIAPChallenge(id, zone);
        if(data != null){
            return data;
        }

        data = userIAPHomeModel.readIAPChallenge(id, zone);
        if(data != null){
            return data;
        }

        return null;
    }


    /**
     * Kiem tra co the mua khong
     */
    public boolean canClaimIAPListGet(UserIAPStoreModel userIAPStoreModel, UserIAPHomeModel userIAPHomeModel, String id, int point, EIAPClaimType claimType, Zone zone){
        if(getIAPChallengeConfig(id) != null) return userIAPStoreModel.canClaimIAPChallenge(id, point, claimType, zone);
        if(getIAPSpecialChallengeConfig(id) != null) return userIAPHomeModel.canClaimIAPChallenge(id, point, claimType, zone);
        return false;
    }

    /**
     * Lay ra list IAP co the nhan
     * @param uid
     * @param zone
     * @return
     */
    public List<String> getListIAPListGetCanClaim(long uid, Zone zone){
        List<String> get = new ArrayList<>();

        UserIAPStoreModel userIAPStoreModel = getUserIAPStoreModel(uid, zone);
        UserIAPHomeModel userIAPHomeModel = getUserIAPHomeModel(uid, zone);
        IAPChallengeVO iapCf;
        //Tung tab 1
        pack_loop:
        for(InfoIAPChallenge challenge : userIAPStoreModel.readIAPChallenge(zone)){
            iapCf = getIAPListGetConfig(challenge.id);
            if(iapCf == null) continue;

            //Tung phan thuong o tung moc
            point_loop:
            for(IAPAchievementVO achievement : iapCf.achievement){
                if(challenge.point < achievement.point) continue;

                //Tung kieu nhan
                claim_loop:
                for(EIAPClaimType claimType : EIAPClaimType.values()){
                    if(claimType.equals(EIAPClaimType.ACTIVE_PREDIUM)) continue;
                    //Kiem tra tung index point
                    if(canClaimIAPListGet(userIAPStoreModel, userIAPHomeModel, challenge.id, achievement.point, claimType, zone)){
                        get.add(challenge.id);
                        break pack_loop;
                    }
                }
            }
        }
        //Tung tab 1
        pack_loop:
        for(InfoIAPChallenge challenge : userIAPHomeModel.readIAPChallenge(zone)){
            iapCf = getIAPListGetConfig(challenge.id);
            if(iapCf == null) continue;

            //Tung phan thuong o tung moc
            point_loop:
            for(IAPAchievementVO achievement : iapCf.achievement){
                if(challenge.point < achievement.point) continue;

                //Tung kieu nhan
                claim_loop:
                for(EIAPClaimType claimType : EIAPClaimType.values()){
                    if(claimType.equals(EIAPClaimType.ACTIVE_PREDIUM)) continue;
                    //Kiem tra tung index point
                    if(canClaimIAPListGet(userIAPStoreModel, userIAPHomeModel, challenge.id, achievement.point, claimType, zone)){
                        get.add(challenge.id);
                        break pack_loop;
                    }
                }
            }
        }

        return get;
    }

    public List<ResourcePackage> getResourceListGetCanClaim(long uid, String id, EIAPClaimType claimType, int point, Zone zone){
        List<ResourcePackage> get = new ArrayList<>();
        //Check config
        IAPChallengeVO challengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
        if(challengeCf == null) return get;

        switch (claimType){
            case ACTIVE_PREDIUM:
                break;
            case FREE:
                for(IAPAchievementVO achievementCf : challengeCf.achievement){
                    if(achievementCf.point == point){
                        return achievementCf.free;
                    }
                }
                break;
            case PREDIUM:
                for(IAPAchievementVO achievementCf : challengeCf.achievement){
                    if(achievementCf.point == point){
                        return achievementCf.predium;
                    }
                }
                break;
            case FREE_PREDIUM:
                for(IAPAchievementVO achievementCf : challengeCf.achievement){
                    if(achievementCf.point == point){

                        get.addAll(achievementCf.free);
                        get.addAll(achievementCf.predium);
                        return get;
                    }
                }
                break;
            case ALL:
                Map<Integer,EIAPClaimType> mapReward = new HashMap<>();

                //Xu ly tren database truoc
                InfoIAPChallenge challengeData = getInfoIAPChallengeUserModel(uid, id, zone);
                for(InfoRewardIAPChallenge index : challengeData.reward){
                    //TH lon hon point -> bo qua
                    if(index.point > challengeData.point) continue;

                    if(challengeData.predium){
                        if(!index.free && !index.predium){
                            mapReward.put(index.point, EIAPClaimType.FREE_PREDIUM);
                        }else if(!index.free && index.predium){
                            mapReward.put(index.point, EIAPClaimType.FREE);
                        }else if(index.free && !index.predium) {
                            mapReward.put(index.point, EIAPClaimType.PREDIUM);
                        }
                    }else {
                        if(!index.free) mapReward.put(index.point, EIAPClaimType.FREE);
                    }
                }
                //Lay config
                for(IAPAchievementVO achievementCf : challengeCf.achievement){
                    //Bo qua cac index da xu ly va cac index co point > data
                    if(achievementCf.point > challengeData.point) continue;

                    //Ton tai tren database -> add phan thuong
                    if(mapReward.containsKey(achievementCf.point)){
                        switch (mapReward.get(achievementCf.point)){
                            case FREE:
                                get.addAll(achievementCf.free);
                                break;
                            case PREDIUM:
                                get.addAll(achievementCf.predium);
                                break;
                            case FREE_PREDIUM:
                                get.addAll(achievementCf.free);
                                get.addAll(achievementCf.predium);
                                break;
                        }
                    }
                    //Khong ton tai -> add phan thuong
                    if(challengeData.predium){
                        get.addAll(achievementCf.free);
                        get.addAll(achievementCf.predium);
                    }else {
                        get.addAll(achievementCf.free);
                    }
                }
                break;
        }
        return get;
    }

    /**
     * Claim trong iap store
     */
    public boolean rewardIAPChallenge(UserIAPStoreModel userIAPStoreModel, UserIAPHomeModel userIAPHomeModel, String id, String purchaseToken, int point, EIAPClaimType claimType, Zone zone){
        switch (claimType){
            case ACTIVE_PREDIUM:
                //Tra phi
                userIAPStoreModel.increasePayment(zone);
                IAPManager.getInstance().boughtIAP(userIAPStoreModel.uid, purchaseToken, zone);
                //Event
                GameEventAPI.ariseGameEvent(EGameEvent.USER_PAYMENT, userIAPStoreModel.uid, new HashMap<>(), zone);
                break;
        }
        return userIAPStoreModel.claimIAPChallenge(id, point, claimType, zone) || userIAPHomeModel.claimIAPChallenge(id, point, claimType, zone);
    }


    /**
     * Tang point iap challenge
     */
    public boolean increaseIAPChallenge(long uid, String id, EConditionType condition, int point, Zone zone){
        UserIAPStoreModel userIAPStoreModel = getUserIAPStoreModel(uid, zone);
        return increaseIAPChallenge(userIAPStoreModel, id, condition, point, zone);
    }
    public boolean increaseIAPChallenge(UserIAPStoreModel userIAPStoreModel, String id, EConditionType condition, int point, Zone zone){
        return userIAPStoreModel.increaseIAPChallenge(id, condition, point, zone);
    }

    /**
     *
     * @param uid
     * @param zone
     * @return
     */
    public List<String> getIAPSpecialPackageExiled(long uid, Zone zone){
        List<String> listIdPackage = new ArrayList<>();

        //Nhung goi can dieu kien xuat hien (dat dk thi add vao db)
        UserIAPHomeModel userIAPHomeModel = getUserIAPHomeModel(uid, zone);
        listIdPackage.addAll(userIAPHomeModel.readListPackageTrigged());

        //Goi co san khi tao acc
        //First purcher (idPackage = first_purchase)
        if(haveEnoughCondition(uid, EConditionType.PAYMENT, 1, zone)) listIdPackage.add("first_purchase");

        return listIdPackage;
    }

    /**
     * add SPECIAL PACKAGE cho nguoi choi
     * @param uid
     * @param idPackage
     * @param zone
     * @return
     */
    public boolean buyIAP(long uid, String idPackage, Zone zone){
        UserIAPHomeModel userIAPHomeModel = getUserIAPHomeModel(uid, zone);
        UserIAPStoreModel userIAPStoreModel = getUserIAPStoreModel(uid, zone);

        if(rewardIAPChallenge(userIAPStoreModel, userIAPHomeModel, idPackage, "ADMIN", 0, EIAPClaimType.ACTIVE_PREDIUM, zone)){
            return true;
        }

        if(rewardIAPPackage(userIAPStoreModel, userIAPHomeModel, idPackage, "ADMIN", zone)){
            IAPPackageVO packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(idPackage, zone);
            List<ResourcePackage> listResource = packageCf.reward;
            List<ResourcePackage> listGiftResource = packageCf.reward.stream().
                    filter(obj -> obj.id.equals("SPI1128") || obj.id.equals("SPI1129") || obj.id.equals("SPI1130") || obj.id.equals("SPI1131") || obj.id.equals("SPI1132") || obj.id.equals("SPI1133") || obj.id.equals("SPI1134") || obj.id.equals("SPI1135") || obj.id.equals("SPI1136") || obj.id.equals("SPI1137")).
                    collect(Collectors.toList());
            int now = Utils.getTimestampInSecond();
            if(!BagManager.getInstance().addItemToDB(listResource, uid, zone, UserUtils.TransactionType.DO_IAP_PACKAGE) ||
                    !GuildManager.getInstance().addGiftGuildUser(
                            uid,
                            listGiftResource.stream().map(index -> GiftGuildInfo.create(index.id, EGuildGiftType.BUY, GiftGuildDescription.create(uid, idPackage), now + 86400)).collect(Collectors.toList()),
                            zone)){
                return false;
            }

            return true;
        }

        return false;
    }



    /*------------------------------------------------- TRIGGER ------------------------------------------------------*/
    /**
     * Xuat hien cac goi package khi du dieu kien
     * @param uid
     * @param condition
     * @param count
     * @param zone
     */
    public void triggerExsistPackageIAPModel(long uid, EConditionType condition, int count, Zone zone){
        UserIAPHomeModel userIAPHomeModel = getUserIAPHomeModel(uid, zone);
        triggerExsistPackageIAPModel(userIAPHomeModel, condition, count, zone);
    }
    public void triggerExsistPackageIAPModel(UserIAPHomeModel userIAPHomeModel, EConditionType condition, int count, Zone zone){
        //Lay cac dieu kien config chua cung type dieu kien
        List<IAPConditionVO> conditionCf = getIAPConditionConfigDependType(condition);
        //Lay cac package co dk update ra
        List<IAPPackageVO> packageCf = getIAPPackageConfig(zone).stream().
                filter(IAPPackageVO::haveConditionExsist).
                collect(Collectors.toList());
        List<String> listIAPAdd = new ArrayList<>();
        boolean havePackage = false;
        boolean haveChallenge = false;

        out_loop:
        for(IAPPackageVO pack : packageCf){
            //Kiem tra khop dieu kien
            for(String conPack : pack.exsistCondition){
                for(IAPConditionVO conCf : conditionCf){
                    if(conPack.equals(conCf.id)){
                        //Trigg neu can co
                        //Ko co truong hop 1 package chua 2 id cung type
                        if(userIAPHomeModel.isTrigger(pack.id, conCf.createConditionVO(count), zone)){
                            //Add package ---> can phai save
                            if(userIAPHomeModel.addIAPPackage(pack.id)){
                                havePackage = true;
                                listIAPAdd.add(pack.id);
                            }
                        }
                        continue out_loop;
                    }
                }
            }
        }

        //Lay cac special package co dk update ra
        List<IAPChallengeVO> challengeCf = getIAPListGetConfig().stream().
                filter(IAPChallengeVO::haveConditionExsist).
                collect(Collectors.toList());
        out_loop:
        for(IAPChallengeVO challenge : challengeCf){
            //Kiem tra khop dieu kien
            for(String conChallenge : challenge.exsistCondition){
                for(IAPConditionVO conCf : conditionCf){
                    if(conChallenge.equals(conCf.id)){
                        //Trigg neu can co
                        //Ko co truong hop 1 package chua 2 id cung type
                        if(userIAPHomeModel.isTrigger(challenge.id, conCf.createConditionVO(count), zone)){
                            //Add package ---> can phai save
                            if(userIAPHomeModel.addIAPChallenge(challenge)){
                                haveChallenge = true;
                                listIAPAdd.add(challenge.id);
                            }
                        }

                        continue out_loop;
                    }
                }
            }
        }
        //Save
        if(havePackage || haveChallenge) userIAPHomeModel.saveToDB(zone);

        //Send noti
        if(!listIAPAdd.isEmpty()) NotificationManager.getInstance().sendNotifyModel(userIAPHomeModel.uid, EActionNotiModel.SHOW, listIAPAdd, zone);
    }




    /*------------------------------------------------------- CONFIG --------------------------------------------------------*/
    /**
     * IAP store config
     * @return
     */
    private IAPStoreConfig getStoreIAPConfig(){
        return this.iapStoreConfig;
    }
    public List<IAPStoreVO> getIAPStoreConfig(){
        return getStoreIAPConfig().list;
    }
    public IAPStoreVO getIAPStoreConfig(String id){
        for(IAPStoreVO index : getIAPStoreConfig()){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }


    /**
     * IAP store config
     * @return
     */
    public List<IAPTabVO> getIAPHomeConfig(){
        List<IAPTabVO> list = new ArrayList<>();
        list.addAll(getIAPSpecialConfig());
        list.addAll(iapHomeConfig.checkin);
        return list;
    }
    public List<IAPTabVO> getIAPSpecialConfig(){
        return iapHomeConfig.special;
    }


    /**
     * IAP item config
     * @return
     */
    public List<IAPPackageVO> getIAPPackageConfig(Zone zone){
        List<IAPPackageVO> get = new ArrayList<>();
        get.addAll(getIAPPackageStoreConfig(zone));
        return get;
    }
    private IAPPackageConfig getIapPackageStoreConfig(Zone zone){
        if(ServerManager.getInstance().isActiveEventModule(EModule.IAP, zone)){
            return EventInGameManager.getInstance().getIapPackageEventConfig();
        }else {
            return this.iapPackageConfig;
        }
    }
    private IAPPackageVO getIapPackageStoreConfig(String id, Zone zone){
        return getIapPackageStoreConfig(zone).readIAPPackageVO(id);
    }
    public List<IAPPackageVO> getIAPPackageStoreConfig(Zone zone){
        return getIapPackageStoreConfig(zone).list;
    }
    public List<IAPPackageVO> getIAPPackageHomeConfig(){
        return getIAPSpecialPackageConfig();
    }
    public IAPPackageVO getIAPPackageConfig(String id, Zone zone){
        IAPPackageVO get = null;
        get = getIapPackageStoreConfig(zone).readIAPPackageVO(id);
        if(get != null) return get;
        get = iapSpecialPackageConfig.readIAPPackageVO(id);
        if(get != null) return get;
        return get;
    }

    /**
     * Get id package in 1 tab store
     */
    public List<String> getListIdIAPPackageFromTab(String idTab){
        for(IAPStoreVO store : getIAPStoreConfig()){
            for(IAPTabVO tab : store.listTab){
                if(tab.id.equals(idTab)){
                    return tab.packages;
                }
            }
        }
        for(IAPTabVO tab : getIAPHomeConfig()){
            if(tab.id.equals(idTab)){
                return tab.packages;
            }
        }
        return null;
    }

    /**
     * Lay idTab dua tren id Package trong tab
     */
    public IAPTabVO getIAPTabConfig(String idTab){
        //Tab trong store
        for(IAPStoreVO store : getIAPStoreConfig()){
            for(IAPTabVO tab : store.listTab){
                if(tab.id.equals(idTab)){
                    return tab;
                }
            }
        }
        //Tab trong home
        for(IAPTabVO tab : getIAPHomeConfig()){
            if(tab.id.equals(idTab)){
                return tab;
            }
        }
        return null;
    }
    public IAPTabVO getIAPTabConfigDependPackage(String idPackage){
        for(IAPStoreVO store : getIAPStoreConfig()){
            for(IAPTabVO tab : store.listTab){
                if(tab.packages.contains(idPackage)){
                    return tab;
                }
            }
        }
        for(IAPTabVO tab : getIAPHomeConfig()){
            if(tab.packages.contains(idPackage)){
                return tab;
            }
        }
        return null;
    }
    public List<IAPTabVO> getIAPTabConfigDependPackage(List<String> listIdPackage){
        Set<IAPTabVO> get = new HashSet<>();
        for(IAPStoreVO store : getIAPStoreConfig()){
            for(IAPTabVO tab : store.listTab){
                for(String idPackage : listIdPackage){

                    if(tab.packages.contains(idPackage)) get.add(tab);
                }
            }
        }
        for(IAPTabVO tab : getIAPHomeConfig()){
            for(String idPackage : listIdPackage){

                if(tab.packages.contains(idPackage)) get.add(tab);
            }
        }
        return get.parallelStream().collect(Collectors.toList());
    }

    public List<IAPChallengeVO> getIAPListGetConfig(){
        List<IAPChallengeVO> get = new ArrayList<>();
        get.addAll(getIAPChallengeConfig());
        get.addAll(getIAPSpecialChallengeConfig());

        return get;
    }
    public IAPChallengeVO getIAPListGetConfig(String id){
        IAPChallengeVO cf = null;
        //cf = IAP challenge
        cf = getIAPChallengeConfig(id);
        if(cf != null) return cf;
        //cf = IAP special
        cf = getIAPSpecialChallengeConfig(id);
        if(cf != null) return cf;

        return cf;
    }

    /**
     * Get module iap challenge
     */
    public List<IAPChallengeVO> getIAPChallengeConfig(){
        return iapChallengeConfig.list;
    }
    private IAPChallengeVO getIAPChallengeConfig(String id){
        return iapChallengeConfig.readIAPChallengeConfig(id);
    }

    public List<IAPChallengeVO> getIAPChallengeHomeConfig(){
        return getIAPSpecialChallengeConfig();
    }

    /**
     * Get iap package special
     * @return
     */
    private List<IAPChallengeVO> getIAPSpecialChallengeConfig(){
        return iapSpecialPackageConfig.listChallenge;
    }
    private List<IAPPackageVO> getIAPSpecialPackageConfig(){
        return iapSpecialPackageConfig.listPackage;
    }
    private IAPPackageVO getIAPSpecialPackageConfig(String id){
        return iapSpecialPackageConfig.readIAPPackageVO(id);
    }
    private IAPChallengeVO getIAPSpecialChallengeConfig(String id){
        return iapSpecialPackageConfig.readIAPChallengeVO(id);
    }

    /**
     * Dieu kien config
     */
    public List<IAPConditionVO> getIAPConditionConfig(){
        return iapConditionConfig.list;
    }
    public IAPConditionVO getIAPConditionConfig(String id){
        return iapConditionConfig.readIAPConditionVO(id);
    }
    public List<IAPConditionVO> getIAPConditionConfigDependType(EConditionType type){
        List<IAPConditionVO> list = new ArrayList<>();
        for(IAPConditionVO conditionCf : getIAPConditionConfig()){
            if(conditionCf.condition.type.equals(type.getId())){
                list.add(conditionCf);
            }
        }
        return list;
    }

    public List<IAPSaleVO> getIAPSaleConfig(){
        return iapSaleConfig.start;
    }
}