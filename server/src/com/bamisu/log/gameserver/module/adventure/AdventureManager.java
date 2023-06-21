package com.bamisu.log.gameserver.module.adventure;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.bag.entities.AdventureModel;
import com.bamisu.log.gameserver.datamodel.bag.entities.CampaignFinishVO;
import com.bamisu.log.gameserver.datamodel.bag.entities.FastRewardDataVO;
import com.bamisu.log.gameserver.datamodel.campaign.UserCampaignDetailModel;
import com.bamisu.log.gameserver.datamodel.campaign.entities.UserMainCampaignDetail;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.adventure.cmd.send.SendClickOnChestLootItem;
import com.bamisu.log.gameserver.module.adventure.cmd.send.SendLootItem;
import com.bamisu.log.gameserver.module.adventure.entities.AdventureRewardConfig;
import com.bamisu.log.gameserver.module.adventure.entities.FastRewardVO;
import com.bamisu.log.gameserver.module.adventure.entities.LootVO;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.campaign.config.MainCampaignConfig;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.defines.EGiftVip;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdventureManager {
    private static AdventureManager ourInstance = null;

    private AdventureRewardConfig adventureRewardConfig;

    public static AdventureManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new AdventureManager();
        }
        return ourInstance;
    }

    private AdventureManager() {
        adventureRewardConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Adventure.FILE_PATH_CONFIG_REWARDS_ADVENTURE), AdventureRewardConfig.class);
    }

    AdventureHandler adventureHandler;

    public AdventureHandler getAdventureHandler() {
        return adventureHandler;
    }

    public void setAdventureHandlerHandler(AdventureHandler adventureHandler) {
        this.adventureHandler = adventureHandler;
    }

//    public int getCountFastReward() {
//        return adventureRewardConfig.count;
//    }

    public List<FastRewardVO> getListFastReward() {
        return adventureRewardConfig.listFastReward;
    }

//    public int getMaxTime() {
//        return adventureRewardConfig.maxTimeAFK;
//    }

    public boolean checkTimeLogin(String timeFastReward) throws ParseException {
        String timeNow = Utils.timeNowString();
        Date dateTimeNow = convertStringToDate(timeNow);
        Date dateTimeFastReward = convertStringToDate(timeFastReward);
        Calendar calTimeNow = Calendar.getInstance();
        Calendar calTimeFastReward = Calendar.getInstance();
        calTimeNow.setTime(dateTimeNow);
        calTimeNow.setTime(dateTimeFastReward);
        return calTimeNow.get(Calendar.DAY_OF_YEAR) == calTimeFastReward.get(Calendar.DAY_OF_YEAR) &&
                calTimeNow.get(Calendar.YEAR) == calTimeFastReward.get(Calendar.YEAR);
    }

    public Date convertStringToDate(String time) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
    }

    public String convertDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * TRUE: Can take
     * FALSE: Not
     */
    public boolean checkFastReward(long uid, Zone zone) throws ParseException {
        AdventureModel adventureModel = getAdventureModel(uid, zone);
        if (!checkTimeLogin(adventureModel.timeFastReward)) {
            adventureModel.timeFastReward = Utils.timeNowString();
            adventureModel.reward = new FastRewardDataVO(AdventureManager.getInstance().getFastReward(EAdventureReward.FREE.getId()));
            return adventureModel.saveToDB(zone);
        }

        return false;
    }

    public boolean checkNoticeFastReward(long uid, Zone zone) {
        AdventureModel adventureModel = getAdventureModel(uid, zone);
        if (adventureModel.reward.id == EAdventureReward.FREE.getId() || adventureModel.reward.id == EAdventureReward.ADS.getId()) {
            if (adventureModel.reward.count > 0) {
                return true;
            }
        }
        return false;
    }

    public AdventureModel getAdventureModel(long uid, Zone zone) {
        AdventureModel adventureModel = AdventureModel.copyFromDBtoObject(uid, zone);
        checkNewDay(adventureModel, zone);
        return adventureModel;
    }

//    public int getIdFastRewardData(AdventureModel adventureModel) {
//        return adventureModel.reward.count;
//    }

    public List<FastRewardVO> getFastRewardConfig() {
        AdventureRewardConfig adventureRewardConfig = new AdventureRewardConfig();
        adventureRewardConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Adventure.FILE_PATH_CONFIG_REWARDS_ADVENTURE), AdventureRewardConfig.class);
        return adventureRewardConfig.listFastReward;
    }

    public FastRewardVO getFastReward(int id) {
        for (FastRewardVO vo : getFastRewardConfig()) {
            if (vo.id == id) {
                return vo;
            }
        }
        return null;
    }

//    public FastRewardVO getMoneyFastReward(AdventureModel adventureModel) {
//        if (adventureModel.count == 0) {
//            return null;
//        }
//        return getFastReward(adventureModel.count);
//    }

    public void getGiftInAdventure(UserModel um, Zone zone, User user) {
        AdventureModel adventureModel = getAdventureModel(um.userID, zone);
        //Not enough turn
//        if (adventureModel.count == 0) {
//            SendGetFastReward send = new SendGetFastReward(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_TURN_TO_GET_REWARD);
//            adventureHandler.send(send, user);
//            return;
//        }

        //Check count is higher than max
//        int count = adventureModel.count;
//        if (adventureModel.count >= getCountFastReward()){
//            count = getCountFastReward();
//        }


        //Not enough money
        FastRewardVO fastRewardVO = getFastReward(adventureModel.reward.id);
        List<LootVO> listReward = getRewardFromLoot(adventureModel.timeLoot);
        List<ResourcePackage> listMoney = Collections.singletonList(new ResourcePackage(fastRewardVO.idMoney, -fastRewardVO.cost));
        if (!BagManager.getInstance().addItemToDB(listMoney, um.userID, zone, UserUtils.TransactionType.GET_FAST_REWARD)) {
            SendGetFastReward send = new SendGetFastReward(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_MONEY);
            adventureHandler.send(send, user);
            return;
        }

        //Error system
        if (listReward == null) {
            SendGetFastReward send = new SendGetFastReward(ServerConstant.ErrorCode.ERR_SYS);
            adventureHandler.send(send, user);
            return;
        }

        //Calculate component in config (list reward)
        List<ResourcePackage> listResource = new ArrayList<>();
        for (LootVO lootVO : listReward) {
            lootVO.amount = (lootVO.amount * fastRewardVO.time * 60) / lootVO.perMinute;
            if (lootVO.amount != 0 && !lootVO.id.equals(MoneyType.SAGE_EXP.getId())) {
                ResourcePackage resourcePackage = new ResourcePackage(lootVO.id, lootVO.amount);
                listResource.add(resourcePackage);
            }
        }

        //handling in data
        if (BagManager.getInstance().addItemToDB(listResource, um.userID, zone, UserUtils.TransactionType.GET_FAST_REWARD)) {
            if (adventureModel.reward.id != EAdventureReward.COST_400.getId()) {
                adventureModel.reward.count--;
                if (adventureModel.reward.count == 0) {
                    adventureModel.reward = new FastRewardDataVO(getFastReward(adventureModel.reward.id + 1));
                }
                adventureModel.saveToDB(zone);
            }

            SendGetFastReward send = new SendGetFastReward();
            send.list = listResource;
            adventureHandler.send(send, user);

            //Event
            Map<String, Object> data = new HashMap<>();
            data.put(Params.LIST, listResource);
//            GameEventAPI.ariseGameEvent(EGameEvent.COLLECT_AFK_PACKAGE, um.userID, data, zone);
            GameEventAPI.ariseGameEvent(EGameEvent.USE_FAST_REWARD_AFK_PACKAGE, um.userID, new HashMap<>(), zone);
            return;
        }
        SendGetFastReward send = new SendGetFastReward(ServerConstant.ErrorCode.ERR_SYS);
        adventureHandler.send(send, user);
    }

    public long getTimeResetFastReward(AdventureModel adventureModel) throws ParseException {
        String time = adventureModel.timeFastReward;
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(time);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        Date now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utils.timeNowString());
        return (date.getTime() / 1000) - (now.getTime() / 1000);
    }

    /**
     * Loot reward in adventure
     */
    public boolean lootRewards(AdventureModel adventureModel, boolean status, UserModel um, Zone zone, User user) {
        try {
            Date date = convertStringToDate(adventureModel.timeLoot.time);  //reward time
            List<ResourcePackage> listReward = new ArrayList<>();

            //seconds
            int maxTime = VipManager.getInstance().getBonus(um.userID, user.getZone(), EGiftVip.AFK_REWARD_HARVESTING) * 60 * 60;
            long reset = (date.getTime() / 1000) + maxTime; //max time
            long now = Utils.getTimestampInSecond();

            //AFK max time
            if (now - reset > 0) {
                List<LootVO> list = AdventureManager.getInstance().calculateReward((int) now, date, true, adventureModel, um.userID, user.getZone());
                List<ResourcePackage> listResource = new ArrayList<>();
                for (LootVO lootVO : list) {
                    ResourcePackage resourcePackage = new ResourcePackage(lootVO.id, lootVO.amount);
                    listResource.add(resourcePackage);
                }
                if (!BagManager.getInstance().addItemToDB(listResource, um.userID, zone, UserUtils.TransactionType.GET_AFK_REWARD)) {
                    return false;
                }
                listReward = listResource;
                if (status) {

                    //Event
                    Map<String, Object> data = new HashMap<>();
                    data.put(Params.LIST, listReward);
                    GameEventAPI.ariseGameEvent(EGameEvent.COLLECT_AFK_PACKAGE, um.userID, data, zone);

                    SendLootItem send = new SendLootItem();
                    send.listResource = listReward;
                    adventureHandler.send(send, user);
                }
                return true;
            }

            //Not enough time
            if ((now - (date.getTime() / 1000)) < 60) {
                if (status) {
                    SendLootItem send = new SendLootItem();
                    adventureHandler.send(send, user);
                    return false;
                }
            }

            //Else
            //Handle time convert to minutes
            List<LootVO> list = AdventureManager.getInstance().calculateReward((int) now, date, false, adventureModel, um.userID, user.getZone());
            List<ResourcePackage> listResource = new ArrayList<>();
            for (LootVO lootVO : list) {
                ResourcePackage resourcePackage = new ResourcePackage(lootVO.id, lootVO.amount);
                listResource.add(resourcePackage);
            }
            if (!BagManager.getInstance().addItemToDB(listResource, um.userID, zone, UserUtils.TransactionType.GET_AFK_REWARD)) {
                return false;
            }
            listReward = listResource;
            if (status) {
                //Event
                Map<String, Object> data = new HashMap<>();
                data.put(Params.LIST, listReward);
                GameEventAPI.ariseGameEvent(EGameEvent.COLLECT_AFK_PACKAGE, um.userID, data, zone);

                SendLootItem send = new SendLootItem();
                send.listResource = listReward;
                adventureHandler.send(send, user);
            }
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<LootVO> calculateReward(int now, Date date, boolean status, AdventureModel adventureModel, long uid, Zone zone) {
        List<LootVO> listMoney = getRewardFromLoot(adventureModel.timeLoot);
        List<LootVO> list = new ArrayList<>();
        //If max time (using for fast reward)
        if (status) {
            for (LootVO lootVO : listMoney) {
                lootVO.amount = (AdventureManager.getInstance().getMaxTimeLootReward() * 60 * lootVO.amount) / lootVO.perMinute;
                list.add(lootVO);
            }
            adventureModel.mapSurplus.put(MoneyType.ESSENCE.getId(), 0);
        } else {//If not
            long sumSecond = now - (date.getTime() / 1000);
            if (adventureModel.mapSurplus.size() == 0) {
                adventureModel.mapSurplus.put(MoneyType.ESSENCE.getId(), 0);
            }
            for (LootVO lootVO : listMoney) {
                if (adventureModel.mapSurplus.containsKey(lootVO.id)) {
                    long timeSum = sumSecond + adventureModel.mapSurplus.get(lootVO.id);
                    if (lootVO.amount != 0) {
                        lootVO.amount = (int) ((((timeSum + adventureModel.mapSurplus.get(lootVO.id)) / 60) * lootVO.amount) / lootVO.perMinute);
                        //If not enough time to receive
                        if (lootVO.amount == 0 && lootVO.id.equals(MoneyType.ESSENCE.getId())) {
                            adventureModel.mapSurplus.put(lootVO.id, (int) timeSum);
                        }
                        //If enough time to receive
                        else if (lootVO.amount != 0 && lootVO.id.equals(MoneyType.ESSENCE.getId())) {
                            adventureModel.mapSurplus.put(lootVO.id, 0);
                        }
                    }
                } else {
                    if (lootVO.amount != 0) {
                        lootVO.amount = (int) (((sumSecond / 60) * lootVO.amount) / lootVO.perMinute);
                    }
                }
                if (lootVO.amount != 0) {
                    list.add(lootVO);
                }
            }
        }
//        //=====NORMAL======
//        for (LootVO normal: list){
//            System.out.println("id: "+normal.id);
//            System.out.println("amount: "+normal.amount);
//            System.out.println("======END======");
//        }
//        //=================
        List<LootVO> listAfterVip = VipManager.getInstance().calculateAFK(list, uid, zone);
        return listAfterVip;
    }

    public List<LootVO> getRewardFromLoot(CampaignFinishVO timeLoot) {
        String[] result = timeLoot.userMainCampaignDetail.readNextStation().split(",");
        List<LootVO> list = new ArrayList<>();
        for (LootVO vo : MainCampaignConfig.getInstance().getArea(Integer.parseInt(result[0])).station.get(Integer.parseInt(result[1])).reward.loot) {
            LootVO obj = new LootVO(vo);
            list.add(obj);
        }
        return list;
    }

    private List<ResourcePackage> getLootRewardInConfig(List<CampaignFinishVO> timeLoot) {
        return null;
    }

    private int getMaxTimeLootReward() {
        return adventureRewardConfig.maxTimeAFK;
    }

    /**
     * Add time beginning new campain
     * status: TRUE: Click loot reward
     *         FALSE: WHEN FINISH
     */
    /**
     * @param um
     * @param userMainCampaignDetail
     * @param status
     * @param user
     * @return
     */
    public void newLootReward(UserModel um, UserMainCampaignDetail userMainCampaignDetail, boolean status, User user, boolean isTutorial) {
        AdventureModel adventureModel = getAdventureModel(um.userID, user.getZone());
        if (isTutorial) {
            if (userMainCampaignDetail == null) {
                adventureModel.timeLoot.time = Utils.timeNowString();
                UserCampaignDetailModel userCampaignDetailModel = UserCampaignDetailModel.copyFromDBtoObject(um.userID, user.getZone());
                adventureModel.timeLoot.userMainCampaignDetail = userCampaignDetailModel.userMainCampaignDetail;
            } else {
                adventureModel.timeLoot.userMainCampaignDetail = userMainCampaignDetail;
            }
            adventureModel.saveToDB(user.getZone());
        } else {
            if (AdventureManager.getInstance().lootRewards(adventureModel, status, um, user.getZone(), user)) {
                adventureModel.timeLoot.time = Utils.timeNowString();
                if (userMainCampaignDetail == null) {
                    UserCampaignDetailModel userCampaignDetailModel = UserCampaignDetailModel.copyFromDBtoObject(um.userID, user.getZone());
                    adventureModel.timeLoot.userMainCampaignDetail = userCampaignDetailModel.userMainCampaignDetail;
                } else {
                    adventureModel.timeLoot.userMainCampaignDetail = userMainCampaignDetail;
                }
                adventureModel.saveToDB(user.getZone());
            }
        }

        //------Update notify------
        updateNotify(um.userID, user.getZone());
        //-------------------------
    }

    /**
     * Using to reset loot chest when user come to new campaign
     * It is Tutorial is TRUE
     * Is is not tutorial: FALSE
     *
     * @param um
     * @param user
     * @param userMainCampaignDetail
     * @return
     */
    public void newCampaign(UserModel um, User user, UserMainCampaignDetail userMainCampaignDetail, boolean isTutorial) {
        newLootReward(um, userMainCampaignDetail, false, user, isTutorial);
    }
//    public boolean newCampaign(long uid, UserMainCampaignDetail userMainCampaignDetail, Zone zone) {
//        UserModel userModel = ((AdventureHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_ADVENTURE)).getUserModel(uid);
//        return newCampaign(userModel, ExtensionUtility.getInstance().getUserById(uid), userMainCampaignDetail);
//    }

    /**
     * Clear all the rewards in db when received
     *
     * @param uid
     * @param zone
     * @return
     */
    public boolean clearTimeLootCampaigns(long uid, Zone zone) {
        AdventureModel adventureModel = getAdventureModel(uid, zone);
//        adventureModel.reward.clear();
        return adventureModel.saveToDB(zone);
    }

    /**
     * Handler just to see the rewards have in chest
     *
     * @param user
     * @param um
     */
    public void seeLootReward(User user, UserModel um) {
        AdventureModel adventureModel = getAdventureModel(um.userID, user.getZone());
//        UserCampaignDetailModel campaignDetailModel = UserCampaignDetailModel.copyFromDBtoObject(um.userID, user.getZone());
        try {
            Date date = convertStringToDate(adventureModel.timeLoot.time);
            List<ResourcePackage> listReward = new ArrayList<>();
            //seconds
            int maxTime = VipManager.getInstance().getBonus(um.userID, user.getZone(), EGiftVip.AFK_REWARD_HARVESTING) * 60 * 60;
            long reset = (date.getTime() / 1000) + maxTime;
            long now = Utils.getTimestampInSecond();
            String[] result = adventureModel.timeLoot.userMainCampaignDetail.readNextStation().split(",");
            //AFK max time
            if (now - reset > 0) {
                List<LootVO> list = calculateReward((int) now, date, true, adventureModel, um.userID, user.getZone());
                for (LootVO lootVO : list) {
                    ResourcePackage resourcePackage = new ResourcePackage(lootVO.id, lootVO.amount);
                    listReward.add(resourcePackage);
                }
                SendClickOnChestLootItem send = new SendClickOnChestLootItem();
                send.listResource = listReward;
                send.time = maxTime;
                send.timeMax = maxTime;
                send.area = Integer.parseInt(result[0]);
                send.station = Integer.parseInt(result[1]);
                adventureHandler.send(send, user);
                return;
            }

            //Not enough time
            if (now - (date.getTime() / 1000) < (adventureRewardConfig.timeReceive * 60)) {
                SendClickOnChestLootItem send = new SendClickOnChestLootItem();
                send.time = (int) (now - (int) (date.getTime() / 1000));
                send.timeMax = maxTime;
                send.station = Integer.parseInt(result[1]);
                send.area = Integer.parseInt(result[0]);
                send.listResource = null;
                adventureHandler.send(send, user);
                return;
            }

            //Else
            //Handle time convert to minutes
            List<LootVO> list = AdventureManager.getInstance().calculateReward((int) now, date, false, adventureModel, um.userID, user.getZone());
            List<ResourcePackage> listChange = new ArrayList<>();
            for (LootVO lootVO : list) {
                ResourcePackage resourcePackage = new ResourcePackage(lootVO.id, lootVO.amount);
                listChange.add(resourcePackage);
            }
            listReward = listChange;
            SendClickOnChestLootItem send = new SendClickOnChestLootItem();
            send.listResource = listReward;
            send.time = (int) (now - (int) (date.getTime() / 1000));
            send.timeMax = maxTime;
            send.area = Integer.parseInt(result[0]);
            send.station = Integer.parseInt(result[1]);
            adventureHandler.send(send, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAFKTime(long uid, Zone zone) {
        long now = Utils.getTimestampInSecond();
        AdventureModel adventureModel = getAdventureModel(uid, zone);
        Date date;
        try {
            date = convertStringToDate(adventureModel.timeLoot.time);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return (int) (now - (int) (date.getTime() / 1000));
    }

    /**
     * Reset time each day
     *
     * @param adventureModel
     * @param zone
     */
    public void checkNewDay(AdventureModel adventureModel, Zone zone) {
        if (adventureModel.timeFastReward.equals(getTime())) {

        } else {
            adventureModel.timeFastReward = getTime();
            adventureModel.reward = new FastRewardDataVO(getFastReward(EAdventureReward.FREE.getId()));
            adventureModel.reward.count += VipManager.getInstance().getBonus(adventureModel.uid, zone, EGiftVip.FREE_TURN_FAST_REWARD);
            adventureModel.saveToDB(zone);
        }
    }

    /**
     * Get time now (year-month-day)
     *
     * @return: String
     */
    public String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }


    public int getTimeAFK(long uid, Zone zone) {
        AdventureModel adventureModel = getAdventureModel(uid, zone);
        try {
            int maxTime = getMaxTimeLootReward() * 60 * 60;
            int root = (int) (convertStringToDate(adventureModel.timeFastReward).getTime() / 1000);
            int reset = (root) + maxTime;
            int now = Utils.getTimestampInSecond();
            //AFK max time
            if (now - reset > 0) {
                return maxTime;
            } else {
                return now - root;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateNotify(long uid, Zone zone) {
        //int maxTime = VipManager.getInstance().getBonus(uid, zone, EGiftVip.AFK_REWARD_HARVESTING) * 60 * 60;
        int maxTime = 60 * 60;
        AdventureModel adventureModel = AdventureManager.getInstance().getAdventureModel(uid, zone);
        Date date = null;  //reward time
        try {
            date = AdventureManager.getInstance().convertStringToDate(adventureModel.timeLoot.time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int now = (int) (date.getTime() / 1000);
        ((ZoneExtension) zone.getExtension()).getZoneCacheData().updateAFKDetail(uid, now, maxTime);
    }
}
