package com.bamisu.log.gameserver.module.vip;

import com.bamisu.gamelib.language.TextID;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.vip.HonorModel;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.WoL.defines.WoLConquerStatus;
import com.bamisu.log.gameserver.module.adventure.AdventureManager;
import com.bamisu.log.gameserver.module.adventure.entities.LootVO;
import com.bamisu.log.gameserver.module.bag.BagHandler;
import com.bamisu.log.gameserver.module.event.event.grand_opening_checkin.GrandOpeningCheckInManager;
import com.bamisu.log.gameserver.module.mail.MailHandler;
import com.bamisu.log.gameserver.module.mail.MailUtils;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateVip;
import com.bamisu.log.gameserver.module.vip.cmd.send.SendClaimHonorGift;
import com.bamisu.log.gameserver.module.vip.cmd.send.SendHonorLevelUp;
import com.bamisu.log.gameserver.module.vip.cmd.send.SendShowInfoVipIAP;
import com.bamisu.log.gameserver.module.vip.cmd.send.SendShowLevelHonor;
import com.bamisu.log.gameserver.module.vip.defines.EGiftVip;
import com.bamisu.log.gameserver.module.vip.entities.*;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.entities.*;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.utils.Utils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import org.apache.thrift.TException;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class VipManager {
    private static VipManager ourInstance = null;
    public static VipManager getInstance(){
        if (ourInstance == null){
            ourInstance = new VipManager();
        }
        return ourInstance;
    }
    private HonorConfig vipConfig;
    private HonorConfig honorConfig;

    //=================CACHE======================
    private LoadingCache<String, Collection<VipData>> LOAD_CACHE;
//    }

    private VipManager(){
        vipConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Vip.FILE_PATH_CONFIG_VIP), HonorConfig.class);
        honorConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Vip.FILE_PATH_CONFIG_HONOR), HonorConfig.class);

        CacheLoader<String, Collection<VipData>> loaderDbBag = new CacheLoader<String, Collection<VipData>>() {
            @Override
            public Collection<VipData> load(String accountID) throws Exception {
                return SDKGateVip.getVip(accountID);
            }
//            private Collection<VipData> load(String accountID) throws Exception {
//                return SDKGateVip.getVip(accountID);
//            }
        };
        this.LOAD_CACHE = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).softValues().build(loaderDbBag);
    }

    private String getIdCache(String accountID){
        return accountID;
    }

    public Collection<VipData> getVip(String accountID) {
        return LOAD_CACHE.getUnchecked(getIdCache(accountID));
    }

    VipHandler vipHandler;

    public VipHandler getVipHandler() {
        return vipHandler;
    }

    public void setVipHandler(VipHandler vipHandler) {
        this.vipHandler = vipHandler;
    }

    public HonorModel getVipModel(long uid, Zone zone){
        return HonorModel.copyFromDBtoObject(uid, zone);
    }


    //=========================================NEW HONOR==========================================
    public List<HonorVO> getHonorConfig(){
        return honorConfig.listHonor;
    }

    public List<Benefits> getBenefits_1(int levelHonor){
        return honorConfig.listHonor.get(levelHonor).benefits_1;
    }

    public List<Benefits> getBenefits_1_Vip(int vip){
        return vipConfig.listHonor.get(vip).benefits_1;
    }


    public List<Benefits> getListBenefits_2(int levelHonor){
        return honorConfig.listHonor.get(levelHonor).benefits_2;
    }

    public Benefits getBenefits_2(int levelHonor, EGiftVip eGiftVip){
        return getListBenefits_2(levelHonor).get(Integer.parseInt(eGiftVip.getId()));
    }

    //========================================VIP=========================================
    public List<Benefits> getVipBenefits_2(EVip eVip){
        return vipConfig.listHonor.get(eVip.getId()).benefits_2;
    }

    public Benefits getVipFollowIdBenefits_2(EVip eVip, EGiftVip eGiftVip){
        return getVipBenefits_2(eVip).get(Integer.parseInt(eGiftVip.getId()));
    }

    //========================================VIP=========================================
    public void claimHonorGift(User user, BaseExtension extension, int idHonor) {
        UserModel um = extension.getUserManager().getUserModel(user);
        HonorModel vipModel = getVipModel(um.userID, user.getZone());
        if (idHonor > vipModel.levelHonor){
            SendClaimHonorGift send = new SendClaimHonorGift(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_LEVEL);
            vipHandler.send(send, user);
            return;
        }
        for (HonorDataVO honorDataVO: vipModel.listHonor){
            if (honorDataVO.id == idHonor){
                //check have received yet?
                if (honorDataVO.status == WoLConquerStatus.ALREADY_RECEIVED.getStatus()){
                    SendClaimHonorGift send = new SendClaimHonorGift(ServerConstant.ErrorCode.ERR_GIFT_CLAIMED);
                    vipHandler.send(send, user);
                    return;
                }else if(honorDataVO.status == WoLConquerStatus.INCOMPLETE.getStatus()){
                    SendClaimHonorGift send = new SendClaimHonorGift(ServerConstant.ErrorCode.ERR_INCOMPLETE);
                    vipHandler.send(send, user);
                    return;
                }else {
                    HonorVO honorVO = getHonor(idHonor);
                    List<ResourcePackage> list = new ArrayList<>();
                    for (Benefits benefits: honorVO.rewards_1){
                        ResourcePackage resourcePackage = new ResourcePackage(benefits.reward.id, benefits.reward.amount);
                        list.add(resourcePackage);
                    }
                    if (BagManager.getInstance().addItemToDB(list, um.userID, user.getZone(), UserUtils.TransactionType.VIP)){
                        honorDataVO.status = WoLConquerStatus.ALREADY_RECEIVED.getStatus();
                        vipModel.saveToDB(user.getZone());
                        SendClaimHonorGift send = new SendClaimHonorGift();
                        vipHandler.send(send, user);
                        return;
                    }
                }
            }
        }
        SendClaimHonorGift send = new SendClaimHonorGift(ServerConstant.ErrorCode.ERR_SYS);
        vipHandler.send(send, user);
    }

    public void sendVip(SendShowInfoVipIAP send, User user){
        vipHandler.send(send, user);
    }

    private int changeExpToLevelHonor(int amount) {
        int level = 0;
        List<HonorVO> list = getHonorConfig();
        for (HonorVO honorVO: list){
            if (amount < honorVO.requirement){
                break;
            }
            level = honorVO.id;
        }
        return level;
    }

    public HonorVO getHonor(int id){
        return getHonorConfig().get(id);
    }


    public void showLevelHonor(User user, BaseExtension extension) {
        UserModel um = extension.getUserManager().getUserModel(user);
        HonorModel honorModel = HonorModel.copyFromDBtoObject(um.userID, user.getZone());
        SendShowLevelHonor send = new SendShowLevelHonor();
        send.list = honorModel.listHonor;
        vipHandler.send(send, user);
    }

    public int getVipHonor(long uid, Zone zone){
        return getVipModel(uid, zone).levelHonor;
    }

    public boolean checkLevelUpHonor(long uid, Zone zone, int amount){
        HonorModel vip = getVipModel(uid, zone);
        int after = changeExpToLevelHonor(amount);
        if (after > vip.levelHonor){
            vip.levelHonor = after;
            vip.listHonor.get(after).status = WoLConquerStatus.CAN_RECEIVE.getStatus();

            //------Update notify------
            AdventureManager.getInstance().updateNotify(uid, zone);
            //-------------------------

            //notify level up to client
            User user = ExtensionUtility.getInstance().getUserById(uid);
            if(user != null){
                SendHonorLevelUp sendHonorLevelUp = new SendHonorLevelUp();
                sendHonorLevelUp.currentLevel = vip.levelHonor;
                getVipHandler().send(sendHonorLevelUp, user);
            }



            //Send mail gift
            VipManager.getInstance().updateGiftFromMail(uid, zone);
            //--------------

            return vip.saveToDB(zone);
        }
        return false;
    }

    //Honor Benefit 2
    private int getGiftHonor(long uid, Zone zone, EGiftVip eGiftVip) {
        HonorModel honorModel = getVipModel(uid, zone);
        Benefits benefits = new Benefits(getBenefits_2(honorModel.levelHonor, eGiftVip));
        return benefits.reward.amount;
    }

    private Benefits usingForMail(long uid, Zone zone, EGiftVip eGiftVip){
        HonorModel honorModel = getVipModel(uid, zone);
        return getBenefits_2(honorModel.levelHonor, eGiftVip);
    }

    public int getGiftVip(String accountID, EGiftVip eGiftVip) throws TException {
        List<VipData> listVip = Utils.fromJsonList(Utils.toJson(getVip(accountID)), VipData.class);
        int count = 0;
        for(VipData vipData : listVip){
            if (vipData.expired >= Utils.getTimestampInSecond()){
                count += getVipFollowIdBenefits_2(vipData.eVip, eGiftVip).reward.amount;
            }
        }
        return count;
    }

    public List<VipData> getListVipToAddMail(String accountID){

        List<VipData> listVip = Utils.fromJsonList(Utils.toJson(getVip(accountID)), VipData.class);
        List<VipData> vipExist = new ArrayList<>();
        for (VipData vipData: listVip){
            if (vipData.expired >= Utils.getTimestampInSecond()){
                vipExist.add(vipData);
            }
        }
        return vipExist;
    }

    /**
     * Get Bonus (using this)
     * @param uid
     * @param zone
     * @param giftType
     * @return
     */
    public int getBonus(long uid, Zone zone, EGiftVip giftType) {
        UserModel userModel = ((VipHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_VIP)).getUserModel(uid);
        int count = 0;
        count += getGiftHonor(uid, zone , giftType);
        try {
            count += getGiftVip(userModel.accountID, giftType);
        } catch (TException e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<ResourcePackage> getBenefits_1Honor(long uid, Zone zone){
        HonorModel vip = getVipModel(uid, zone);
        List<Benefits> listBenefit = new ArrayList<>();
        for (Benefits benefits: getBenefits_1(vip.levelHonor)){
            Benefits newObj = new Benefits(benefits);
            listBenefit.add(newObj);
        }
        List<ResourcePackage> list = new ArrayList<>();
        for (Benefits benefits: listBenefit){
            ResourcePackage resourcePackage = benefits.reward;
            list.add(resourcePackage);
        }
        return list;
    }

    public List<ResourcePackage> getListVipBenefits_1(String accountID){
        List<ResourcePackage> list = new ArrayList<>();
        List<VipData> listVip = Utils.fromJsonList(Utils.toJson(getVip(accountID)), VipData.class);

        for(VipData vipData : listVip){
            if (vipData.expired >= Utils.getTimestampInSecond()){
                List<Benefits> listBenefit = getBenefits_1_Vip(vipData.eVip.getId());
                for (Benefits benefits: listBenefit){
                    ResourcePackage resourcePackage = benefits.reward;
                    list.add(resourcePackage);
                }
            }
        }
        return list;
    }

    public VipData getVip(EVip eVip, String accountID){
        List<VipData> listVip = Utils.fromJsonList(Utils.toJson(getVip(accountID)), VipData.class);
        for (VipData vipData: listVip){
            if (vipData.eVip.getId() == eVip.getId()){
                return vipData;
            }
        }
        return null;
    }


    public boolean haveVip(long uid, EVip eVip, Zone zone){
        UserModel userModel = ((BagHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_BAG)).getUserModel(uid);
        return haveVip(userModel, eVip);
    }
    public boolean haveVip(UserModel userModel, EVip eVip){
        return haveVip(eVip, userModel.accountID);
    }
    public boolean haveVip(EVip eVip, String accountID){
        if(eVip == null) return false;
        List<VipData> listVip = Utils.fromJsonList(Utils.toJson(getVip(accountID)), VipData.class);
        for (VipData vipData: listVip){
            if (vipData.eVip.getId() == eVip.getId()){
                return vipData.expired > Utils.getTimestampInSecond();
            }
        }
        return false;
    }

    public List<VipData> getListVip(String accountID){
        try{
            List<VipData> listVip = Utils.fromJsonList(Utils.toJson(getVip(accountID)), VipData.class);
            return listVip;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get Bonus percent reward_1 in vip and honor
     * @param uid
     * @param zone
     * @return
     */
    public List<ResourcePackage> getBonusPercent(long uid, Zone zone){
        UserModel um = ((ZoneExtension) zone.getExtension()).getUserManager().getUserModel(uid);
        List<ResourcePackage> listVip = getListVipBenefits_1(um.accountID);
        List<ResourcePackage> listHonor = getBenefits_1Honor(uid, zone);

        List<ResourcePackage> list = new ArrayList<>();
        for (ResourcePackage resourcePackage: listHonor){
            ResourcePackage pack = new ResourcePackage(resourcePackage);
            list.add(pack);
        }
        for (int i = 0; i < list.size(); i++){
            for (int j = 0; j < listVip.size(); j++){
                if (list.get(i).id.equals(listVip.get(j).id)){
                    list.get(i).amount += listVip.get(j).amount;
                    break;
                }
            }
        }
        return list;
    }

    public List<LootVO> calculateAFK(List<LootVO> listLoot, long uid, Zone zone){
        List<ResourcePackage> listResource = getBonusPercent(uid, zone);
        for (LootVO lootVO: listLoot){
            for (ResourcePackage resourcePackage: listResource){
                if (lootVO.id.equals(resourcePackage.id)){
                    lootVO.amount = lootVO.amount + ((lootVO.amount * resourcePackage.amount)/100);
                    break;
                }
            }
        }

        return listLoot;
    }

    public void updateCache(String accountID, Collection<VipData> vipData) {
        LOAD_CACHE.put(accountID, vipData);
    }

    public void invalidateVipCache(String accountID) {
        LOAD_CACHE.invalidate(accountID);
    }


    /**
     * Diamond per day
     */
    public void updateGiftFromMail(long uid, Zone zone){
        UserModel userModel = ((VipHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_VIP)).getUserModel(uid);
        MailHandler mailHandler = ((MailHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_MAIL));
        //Honor
        int gift = getGiftHonor(uid, zone , EGiftVip.FREE_DIAMOND);
        //Vip
        List<VipData> listVip = getListVipToAddMail(userModel.accountID);

        //Check if don't have any vip
        if (gift == 0 && listVip.size() == 0){
            return;
        }

        long newTime = GrandOpeningCheckInManager.getInstance().getNewTime();
        //Honor
        HonorModel honorModel = getVipModel(uid, zone);
        if (gift != 0){
            if (honorModel.time == 0 || honorModel.time/newTime != 1){
                honorModel.time = newTime;

                //Send gift
                ResourcePackage resourcePackage = new ResourcePackage(MoneyType.DIAMOND.getId(), gift);
                List<ResourcePackage> list = Collections.singletonList(resourcePackage);
                MailUtils.getInstance().sendMailDiamondGiftVIP(mailHandler, uid, ExtensionUtility.getInstance().getUserById(uid),list, TextID.TITLE_MAIL_Honor_DailyDiamond, TextID.CONTENT_MAIL_Honor_DailyDiamond);
            }
        }

        //Vip
        for (VipData vipData: listVip){
            for (TimeVipVO timeVipVO: honorModel.timeVip){
                if (timeVipVO.eVip.equals(vipData.eVip)){
                    if (timeVipVO.time == 0 || timeVipVO.time/newTime != 1){
                        timeVipVO.time = newTime;

                        //Send gift
                        int amount = getVipFollowIdBenefits_2(vipData.eVip, EGiftVip.FREE_DIAMOND).reward.amount;
                        ResourcePackage resourcePackage = new ResourcePackage(MoneyType.DIAMOND.getId(), amount);
                        List<ResourcePackage> list = Collections.singletonList(resourcePackage);
                        if (timeVipVO.eVip.equals(EVip.PROTECTOR)){
                            MailUtils.getInstance().sendMailDiamondGiftVIP(mailHandler, uid, ExtensionUtility.getInstance().getUserById(uid),list, TextID.TITLE_MAIL_VIP1_DailyDiamond, TextID.CONTENT_MAIL_VIP1_DailyDiamond);
                        }else if (timeVipVO.eVip.equals(EVip.ARCHMAGE)){
                            MailUtils.getInstance().sendMailDiamondGiftVIP(mailHandler, uid, ExtensionUtility.getInstance().getUserById(uid),list, TextID.TITLE_MAIL_VIP2_DailyDiamond, TextID.CONTENT_MAIL_VIP2_DailyDiamond);
                        }
                    }
                }
            }
        }
        honorModel.saveToDB(zone);
    }
}
