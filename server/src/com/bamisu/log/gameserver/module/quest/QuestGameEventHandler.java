package com.bamisu.log.gameserver.module.quest;

import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.quest.UserQuestModel;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EConditionType;

import com.bamisu.log.gameserver.module.GameEvent.BaseGameEvent;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.campaign.config.MainCampaignConfig;
import com.bamisu.log.gameserver.module.campaign.config.entities.Area;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.log.gameserver.module.notification.NotificationManager;
import com.bamisu.log.gameserver.module.notification.defind.ENotification;
import com.bamisu.log.gameserver.module.store.define.EStore;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.socialnetwork.ESocialNetwork;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestGameEventHandler extends BaseGameEvent {


    public QuestGameEventHandler(Zone zone) {
        super(zone);
    }

    @Override
    public void handleGameEvent(EGameEvent event, long uid, Map<String,Object> data) {
        switch (event){
            case LINK_ACCOUNT:
                handlerLinkAccount(uid, data);
                break;
            case UPDATE_MONEY:
                handlerUpdateMoney(uid, data);
                break;
            case SEND_MONEY:
                handlerSendMoney(uid, data);
                break;
            case ENHANDCE_ITEM:
                handlerEnhandceItem(uid, data);
                break;
            case DO_MISSION:
                handlerDoMission(uid, data);
                break;
            case DO_GUILD_HUNT:
                handlerDoGuildHunt(uid, data);
                break;
            case COLLECT_AFK_PACKAGE:
                handlerCollectAFKPackage(uid, data);
                break;
            case LEVEL_HERO_UPDATE:
                handlerLevelHeroUpdate(uid, data);
                break;
            case DO_HUNT:
                handlerDoHunt(uid, data);
                break;
            case DO_TOWER:
                handlerDoTower(uid, data);
                break;
            case USE_FAST_REWARD_AFK_PACKAGE:
                handlerUserFastRewardAFKPackage(uid, data);
                break;
            case SUMMON_TAVERN:
                handlerSummonTavern(uid, data);
                break;
            case DO_ARENA:
                handlerDoArena(uid, data);
                break;
            case DO_CAMPAIGN:
                handlerDoCampaign(uid, data);
                break;
            case FINISH_MISSION_FIGHTING:
                handlerFinishMission(uid, data);
                break;
            case FINISH_HUNT_FIGHTING:
                handlerFinishHunt(uid, data);
                break;
            case BUY_INGAME_STORE:
                handlerBuyIngameStore(uid, data);
                break;
            case OPEN_SLOT_BLESSING_HERO:
                handlerOpenSlotBlessingHero(uid, data);
                break;
            case GET_HERO:
                handlerGetHero(uid, data);
                break;
            case LEVEL_USER_UPDATE:
                handlerLevelUserUpdate(uid, data);
                break;
            case FLOOR_TOWER_UPDATE:
                handlerFloorTowerUpdate(uid, data);
                break;
            case STATION_CAMPAIGN_UPDATE:
                handlerStationCampaignUpdate(uid, data);
                break;
            case CHAP_CAMPAIGN_UPDATE:
                handlerChapCampaignUpdate(uid, data);
                break;
            case STAR_HERO_UPDATE:
                handlerStarHeroUpdate(uid, data);
                break;
            case SEND_FRIEND_REQUEST:
                handlerSendFriendRequest(uid, data);
                break;
            case JOIN_GUILD:
                handlerJoinGuild(uid, data);
                break;
            case CHAT:
                handlerChat(uid, data);
                break;
            case FINISH_ARENA:
                handlerFinishArena(uid, data);
                break;
        }
    }


    @Override
    public void initEvent() {
        this.registerEvent(EGameEvent.LINK_ACCOUNT);
        this.registerEvent(EGameEvent.UPDATE_MONEY);
        this.registerEvent(EGameEvent.SEND_MONEY);
        this.registerEvent(EGameEvent.ENHANDCE_ITEM);
        this.registerEvent(EGameEvent.DO_MISSION);
        this.registerEvent(EGameEvent.DO_GUILD_HUNT);
        this.registerEvent(EGameEvent.LEVEL_HERO_UPDATE);
        this.registerEvent(EGameEvent.DO_HUNT);
        this.registerEvent(EGameEvent.DO_TOWER);
        this.registerEvent(EGameEvent.COLLECT_AFK_PACKAGE);
        this.registerEvent(EGameEvent.USE_FAST_REWARD_AFK_PACKAGE);
        this.registerEvent(EGameEvent.SUMMON_TAVERN);
        this.registerEvent(EGameEvent.DO_ARENA);
        this.registerEvent(EGameEvent.DO_CAMPAIGN);
        this.registerEvent(EGameEvent.FINISH_MISSION_FIGHTING);
        this.registerEvent(EGameEvent.FINISH_HUNT_FIGHTING);
        this.registerEvent(EGameEvent.BUY_INGAME_STORE);
        this.registerEvent(EGameEvent.BLESSING_HERO);
        this.registerEvent(EGameEvent.OPEN_SLOT_BLESSING_HERO);
        this.registerEvent(EGameEvent.GET_HERO);
        this.registerEvent(EGameEvent.LEVEL_USER_UPDATE);
        this.registerEvent(EGameEvent.FLOOR_TOWER_UPDATE);
        this.registerEvent(EGameEvent.STATION_CAMPAIGN_UPDATE);
        this.registerEvent(EGameEvent.CHAP_CAMPAIGN_UPDATE);
        this.registerEvent(EGameEvent.STAR_HERO_UPDATE);
        this.registerEvent(EGameEvent.SEND_FRIEND_REQUEST);
        this.registerEvent(EGameEvent.JOIN_GUILD);
        this.registerEvent(EGameEvent.CHAT);
        this.registerEvent(EGameEvent.FINISH_ARENA);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    private void handlerLinkAccount(long uid, Map<String,Object> data){
        int socialNetwork = (int) data.getOrDefault(Params.SOCIAL_NETWORK, -1);
        if(socialNetwork < 0)return;

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        switch (ESocialNetwork.fromIntValue(socialNetwork)){
            case FACEBOOK:
                QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.LINK_FACEBOOK, 1, zone);
                break;
            case GOOGLE:
                QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.LINK_GOOGLE, 1, zone);
                break;
            case GAME_CENTER:
                QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.LINK_GAME_CENTER, 1, zone);
                break;
        }

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerUpdateMoney(long uid, Map<String,Object> data){

    }

    private void handlerSendMoney(long uid, Map<String,Object> data) {
        List<ResourcePackage> resourcePackages = (List<ResourcePackage>) data.getOrDefault(Params.LIST, new ArrayList<>());
        if(resourcePackages.isEmpty()) return;

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        for(ResourcePackage index : resourcePackages){
            if(MoneyType.FRIENDSHIP_BANNER.getId().equals(index.id)){
                QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.SEND_FRIEND_POINT, 1, zone);
            }
        }

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerEnhandceItem(long uid, Map<String,Object> data) {
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.ENHANDCE_ITEM, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerDoMission(long uid, Map<String,Object> data) {
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.DO_MISSION, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerDoGuildHunt(long uid, Map<String,Object> data){
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.DO_GUILD_HUNT, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerCollectAFKPackage(long uid, Map<String,Object> data){
        List<ResourcePackage> resourcePackages = (List<ResourcePackage>) data.getOrDefault(Params.LIST, new ArrayList<>());
        if(resourcePackages.isEmpty()) return;

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        for(ResourcePackage index : resourcePackages){
            if(MoneyType.GOLD.getId().equals(index.id)){
                QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.GOLD_AFK, index.amount, zone);
            }
        }
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.COLLECT_AFK, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerLevelHeroUpdate(long uid, Map<String,Object> data) {
        int level = (short) data.getOrDefault(Params.LEVEL, -1);
        int blessingLevel = (int) data.getOrDefault(Params.BLESSING, -1);

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        Map<EConditionType,Integer> mapCondition = new HashMap<>();
        mapCondition.put(EConditionType.UPLEVEL_HERO, 1);
        if(level > 0)mapCondition.put(EConditionType.LEVEL_HERO, level);
        if(level > 0)mapCondition.put(EConditionType.BLESLING_LEVEL, blessingLevel);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, mapCondition, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerDoHunt(long uid, Map<String,Object> data){
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.DO_HUNT, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerDoTower(long uid, Map<String,Object> data){
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.DO_TOWER, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerUserFastRewardAFKPackage(long uid, Map<String,Object> data){
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.USE_FAST_REWARD, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerSummonTavern(long uid, Map<String,Object> data){
        System.out.println("\n" + Utils.toJson(data) + "\n");
        int count = (int) data.getOrDefault(Params.COUNT, -1);
        if(count < 0) return;

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.SUMMON_TAVERN, count, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerDoArena(long uid, Map<String,Object> data){
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.DO_ARENA, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerDoCampaign(long uid, Map<String,Object> data){
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.DO_CAMPAIGN, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerFinishMission(long uid, Map<String,Object> data){
        boolean win = (boolean) data.getOrDefault(Params.WIN, false);

        if(win){
            UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
            QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.WIN_MISSION, 1, zone);

            //Check quest hoan thanh
            if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
                //Gui noti
                NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
            }
        }
    }

    private void handlerFinishHunt(long uid, Map<String,Object> data){
        boolean win = (boolean) data.getOrDefault(Params.WIN, false);

        if(win){
            UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
            QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.WIN_HUNT, 1, zone);

            //Check quest hoan thanh
            if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
                //Gui noti
                NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
            }
        }
    }

    private void handlerBuyIngameStore(long uid, Map<String,Object> data){
        int type = (int) data.getOrDefault(Params.TYPE, -1);

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        switch (EStore.fromID(type)){
            case GENERAL_STORE:
                QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.BUY_GENERAL_STORE, 1, zone);
                break;
            case ALLIANCE_STORE:
                QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.BUY_GUILD_STORE, 1, zone);
                break;
            case HUNTER_STORE:
                QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.BUY_HUNTER_STORE, 1, zone);
                break;
        }

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerOpenSlotBlessingHero(long uid, Map<String,Object> data){
        int count = (int) data.getOrDefault(Params.COUNT, -1);

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.OPEN_SLOT_BLESSING, count, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerGetHero(long uid, Map<String,Object> data) {
        int count = (int) data.getOrDefault(Params.COUNT, -1);
        if(count < 0) return;

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.GET_HERO, count, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerLevelUserUpdate(long uid, Map<String,Object> data){
        int level = (short) data.getOrDefault(Params.LEVEL, -1);
        if(level < 0) return;

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.LEVEL_USER, level, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerFloorTowerUpdate(long uid, Map<String,Object> data){
        int floor = (short) data.getOrDefault(Params.FLOOR, -1);
        if(floor < 0)return;

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.FLOOR_TOWER, floor - 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerStationCampaignUpdate(long uid, Map<String,Object> data){
        int area = (int) data.get(Params.AREA);
        int station = (int) data.get(Params.STATION);
        int count = 0;

        List<Area> listArea = MainCampaignConfig.getInstance().area;
        for(int i = 0; i < listArea.size(); i++){
            if(i > area)break;
            if(i < area){
                count += listArea.get(i).station.size();
            }else if(i == area){
                count += station + 1;
            }
        }

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.STATION_DUNGEON, count, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerChapCampaignUpdate(long uid, Map<String,Object> data){
        int area = (int) data.getOrDefault(Params.AREA, -1);
        if(area < 0)return;

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.CHAP_DUNGEON, area, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerStarHeroUpdate(long uid, Map<String, Object> data) {
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.UPSTAR_HERO, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerSendFriendRequest(long uid, Map<String, Object> data){
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.SEND_FRIEND_REQUEST, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerJoinGuild(long uid, Map<String, Object> data){
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.JOIN_GUILD, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }

    private void handlerChat(long uid, Map<String, Object> data){
        EChatType type = EChatType.fromID((Integer) data.getOrDefault(Params.TYPE, -1));

        if(type.equals(EChatType.GLOBAL) || type.equals(EChatType.CHANNEL)){
            UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
            QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.CHAT, 1, zone);

            //Check quest hoan thanh
            if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
                //Gui noti
                NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
            }
        }
    }

    private void handlerFinishArena(long uid, Map<String, Object> data) {
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
        QuestManager.getInstance().triggerUpdateQuestModel(userQuestModel, EConditionType.DO_ARENA, 1, zone);

        //Check quest hoan thanh
        if(!QuestManager.getInstance().tabCanRewardGiftQuest(userQuestModel, zone).isEmpty()){
            //Gui noti
            NotificationManager.getInstance().sendNotify(uid, new ArrayList<>(ENotification.CAN_CLAIM_GIFT_QUEST.getListNotifyID(uid, zone)), zone);
        }
    }
}
