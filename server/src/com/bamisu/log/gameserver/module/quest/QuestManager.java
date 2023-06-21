package com.bamisu.log.gameserver.module.quest;

import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.IResourcePackage;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.datamodel.quest.entities.QuestInfo;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.quest.UserQuestModel;
import com.bamisu.log.gameserver.datamodel.quest.entities.TabQuestInfo;
import com.bamisu.log.gameserver.entities.EModule;
import com.bamisu.log.gameserver.entities.EStatus;
import com.bamisu.log.gameserver.manager.ServerManager;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.event.EventInGameManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.quest.config.QuestChestConfig;
import com.bamisu.log.gameserver.module.quest.config.QuestConditionConfig;
import com.bamisu.log.gameserver.module.quest.config.QuestConfig;
import com.bamisu.log.gameserver.module.quest.config.entities.QuestChestVO;
import com.bamisu.log.gameserver.module.quest.config.entities.QuestConditionVO;
import com.bamisu.log.gameserver.module.quest.config.entities.QuestVO;
import com.bamisu.log.gameserver.module.quest.defind.EMoneyQuestType;
import com.bamisu.log.gameserver.module.quest.defind.EQuestType;
import com.bamisu.log.gameserver.module.quest.entities.UpdateQuestVO;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EConditionType;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {
    private QuestConfig questConfig;
    private QuestConditionConfig questConditionConfig;
    private QuestChestConfig questChestConfig;



    private static QuestManager ourInstance = new QuestManager();

    public static QuestManager getInstance() {
        return ourInstance;
    }

    private QuestManager() {
        //Load config
        loadConfig();
    }


    private void loadConfig(){
        questConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Quest.FILE_PATH_CONFIG_QUEST), QuestConfig.class);
        questConditionConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Quest.FILE_PATH_CONFIG_QUEST_CONDITION), QuestConditionConfig.class);
        questChestConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Quest.FILE_PATH_CONFIG_QUEST_CHEST), QuestChestConfig.class);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Get Model quest user
     * @param uid
     * @param zone
     * @return
     */
    public UserQuestModel getUserQuestModel(long uid, Zone zone){
        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getUserQuestModelCache(uid);
    }


    /**
     * get tab quest info
     * @param uid
     * @param zone
     * @return
     */
    public List<TabQuestInfo> getUserTableQuest(long uid, Zone zone){
        UserQuestModel userQuestModel = getUserQuestModel(uid, zone);
        return getUserTableQuest(userQuestModel, zone);
    }
    public List<TabQuestInfo> getUserTableQuest(UserQuestModel userQuestModel, Zone zone){
        List<TabQuestInfo> list = userQuestModel.readListTabQuest(zone);
        return list;
    }

    public TabQuestInfo getUserTabQuestInfo(UserQuestModel userQuestModel, String type, Zone zone){
        for(TabQuestInfo tab : getUserTableQuest(userQuestModel, zone)){
            if(tab.type.equals(type)){
                return tab;
            }
        }
        return null;
    }

    public QuestInfo getUserQuestInfo(UserQuestModel userQuestModel, String idQuest, Zone zone){
        for(TabQuestInfo tabData : getUserTableQuest(userQuestModel, zone)){
            for(QuestInfo data : tabData.quests){
                if(data.id.equals(idQuest)){
                    return data;
                }
            }
        }
        return QuestInfo.create(idQuest, 0);
    }


    public boolean canCompleteQuest(UserQuestModel userQuestModel, String id, Zone zone){
        return userQuestModel.canCompleteQuest(id, zone);
    }

    public boolean haveCompleteQuest(long uid, String id, Zone zone){
        return haveCompleteQuest(getUserQuestModel(uid, zone), id, zone);
    }
    public boolean haveCompleteQuest(UserQuestModel userQuestModel, String id, Zone zone){
        return userQuestModel.haveCompleteQuest(id, zone);
    }


    /**
     *
     * @param uid
     * @param zone
     * @return
     */
    public List<String> tabCanRewardGiftQuest(long uid, Zone zone){
        return tabCanRewardGiftQuest(getUserQuestModel(uid, zone), zone);
    }
    public List<String> tabCanRewardGiftQuest(UserQuestModel userQuestModel, Zone zone){
        Set<String> listTab = new HashSet<>();
        out_loop:
        for(TabQuestInfo tab : userQuestModel.readListTabQuest(zone)){
            //Kiem tra quest co the hoan thanh
            for(QuestInfo quest : tab.quests){
                //Neu co it nhat 1 quest co the hoan thanh
                if(tab.canCompleteQuest(quest.id)){
                    listTab.add(tab.type);
                    continue out_loop;
                }
            }
            //Kiem tra chest co the hoan thanh
            for(QuestChestVO chest : getQuestChestConfig(EQuestType.fromID(tab.type), zone)){
                if(tab.canRewardChest(chest.id, zone)){
                    listTab.add(tab.type);
                    continue out_loop;
                }
            }
        }

        return listTab.parallelStream().collect(Collectors.toList());
    }


    public boolean completeQuest(UserQuestModel userQuestModel, String id, Zone zone){
        return userQuestModel.completeQuest(id, zone);
    }


    public boolean canRewardChest(UserQuestModel userQuestModel, String id, Zone zone){
        return userQuestModel.canRewardChest(id, zone);
    }
    public boolean rewardChest(UserQuestModel userQuestModel, String id, Zone zone){
        return userQuestModel.rewardChest(id, zone);
    }


    /**
     * Update point quest
     * @param uid
     * @param resources
     * @param zone
     * @return
     */
    public boolean changeQuestPoint(long uid, List<ResourcePackage> resources, Zone zone){
        return changeQuestPoint(getUserQuestModel(uid, zone), resources, zone);
    }
    public boolean changeQuestPoint(UserQuestModel userQuestModel, List<ResourcePackage> resources, Zone zone){
        boolean haveSave = false;

        for(ResourcePackage resource : resources){
            switch (EMoneyQuestType.fromID(resource.id)){
                case DAILY_POINT:
                    for(TabQuestInfo tab : userQuestModel.readListTabQuest(zone)){
                        if(EQuestType.DAILY.getId().equals(tab.type)){
                            tab.chest.point += resource.amount;
                            haveSave = true;
                            break;
                        }
                    }
                    break;
                case WEEKLY_POINT:
                    for(TabQuestInfo tab : userQuestModel.readListTabQuest(zone)){
                        if(EQuestType.WEEKLY.getId().equals(tab.type)){
                            tab.chest.point += resource.amount;
                            haveSave = true;
                            break;
                        }
                    }
                    break;
            }
        }
        if(haveSave){
            userQuestModel.saveToDB(zone);
        }
        return false;
    }

    public int getCountCompleteQuest(UserQuestModel userQuestModel, String idQuest, Zone zone){
        int current = 0;
        out_loop:
        for(TabQuestInfo tab : userQuestModel.readListTabQuest(zone)) {
            for (int i = 0; i < tab.quests.size(); i++) {
                if (tab.quests.get(i).id.equals(idQuest)) {
                    current = tab.quests.get(i).complete;
                    break out_loop;
                }
            }
        }
        return current;
    }

    /**
     * lay phan thuong hoan thanh quest
     * @param userQuestModel
     * @param idQuest
     * @param zone
     */
    public List<IResourcePackage> rewardCompleteQuest(UserQuestModel userQuestModel, String idQuest, int index, Zone zone){
        List<IResourcePackage> listItemReward = new ArrayList<>();
        QuestVO questCf = QuestManager.getInstance().getQuestConfig(idQuest);
        //Lay ra vi tri index cua phan thuong trong config
        int current = (index < 0) ? getCountCompleteQuest(userQuestModel, idQuest, zone) : index;

        List<ResourcePackage> listResourceReward = questCf.readRewardQuestResource(current);
        listItemReward.addAll(listResourceReward);

        List<EquipDataVO> listEquipData = new ArrayList<>();
        for(ResourcePackage equip : questCf.readRewardQuestHeroEquip(current)){
            listEquipData.add(ItemManager.getInstance().convertEquipConfigToData(ItemManager.getInstance().getEquipByID(equip.id)));
        }
        listEquipData = listEquipData.stream().
                filter(Objects::nonNull).
                collect(Collectors.toList());
        listItemReward.addAll(listEquipData);

        if(!BagManager.getInstance().addItemToDB(listResourceReward, userQuestModel.uid, zone, UserUtils.TransactionType.COMPLETE_QUEST) ||
                !BagManager.getInstance().addNewWeapon(userQuestModel.uid, zone, listEquipData)) return new ArrayList<>();

        return listItemReward;
    }

    public boolean updateQuest(long uid, List<UpdateQuestVO> update, Zone zone){
        return updateQuest(getUserQuestModel(uid, zone), update, zone);
    }
    public boolean updateQuest(UserQuestModel userQuestModel, List<UpdateQuestVO> update, Zone zone){
        if(userQuestModel.updateQuest(update, zone)){
            return userQuestModel.saveToDB(zone);
        }
        return false;
    }

    public boolean upQuest(long uid, List<UpdateQuestVO> update, Zone zone){
        return upQuest(getUserQuestModel(uid, zone), update, zone);
    }
    public boolean upQuest(UserQuestModel userQuestModel, List<UpdateQuestVO> update, Zone zone){
        if(userQuestModel.upQuest(update, zone)){
            return userQuestModel.saveToDB(zone);
        }
        return false;
    }


    /**
     * Trigger update quest
     * @param uid
     * @param condition
     * @param count
     * @param zone
     */
    public final void triggerUpdateQuestModel(long uid, EConditionType condition, int count, Zone zone){
        UserQuestModel userQuestModel = getUserQuestModel(uid, zone);
        triggerUpdateQuestModel(userQuestModel, condition, count, zone);
    }
    public final void triggerUpdateQuestModel(UserQuestModel userQuestModel, EConditionType condition, int count, Zone zone){
        //Lay cac dieu kien config chua cung type dieu kien
        List<QuestConditionVO> conditionCf = QuestManager.getInstance().getQuestConditionConfigDependType(condition);
        List<QuestVO> questCf = QuestManager.getInstance().getQuestConfig().parallelStream().filter(obj -> {
            for(QuestConditionVO con : conditionCf){
                if(con.id.equals(obj.rewardCondition)){
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());

        List<UpdateQuestVO> update = questCf.parallelStream().
                map(obj -> {
//                    if(obj.id.equals("weekly5")){
//                        List<String> queueResetHero = HeroManager.getInstance().getQueueResetHero(userQuestModel.uid, zone);
//                        if(queueResetHero.size() == 2 && queueResetHero.get(0).equals(queueResetHero.get(1))) return UpdateQuestVO.create(obj.id, 0);
//                    }
                    return UpdateQuestVO.create(obj.id, count);
                }).
                collect(Collectors.toList());
        switch (condition){
            case LEVEL_USER:
            case LEVEL_HERO:
            case FLOOR_TOWER:
            case CHAP_DUNGEON:
            case BLESLING_LEVEL:
            case ARENA_POINT:
            case STATION_DUNGEON:
            case OPEN_SLOT_BLESSING:
                updateQuest(userQuestModel, update, zone);
                break;
            case GET_HERO:
            case SEND_FRIEND_POINT:
            case ENHANDCE_ITEM:
            case DO_MISSION:
            case DO_GUILD_HUNT:
            case COLLECT_AFK:
            case UPLEVEL_HERO:
            case DO_HUNT:
            case DO_TOWER:
            case USE_FAST_REWARD:
            case SUMMON_TAVERN:
            case DO_ARENA:
            case DO_CAMPAIGN:
            case WIN_ARENA:
            case WIN_MISSION:
            case WIN_HUNT:
            case BUY_GENERAL_STORE:
            case BUY_GUILD_STORE:
            case BUY_HUNTER_STORE:
            case GET_ALLIANCE_COIN:
            case BLESLING_HERO:
            case GOLD_AFK:
            case LINK_FACEBOOK:
            case LINK_GOOGLE:
            case LINK_GAME_CENTER:
            case UPSTAR_HERO:
            case SEND_FRIEND_REQUEST:
            case JOIN_GUILD:
            case CHAT:
                upQuest(userQuestModel, update, zone);
                break;
        }
    }

    public final void triggerUpdateQuestModel(long uid, Map<EConditionType,Integer> condition, Zone zone){
        UserQuestModel userQuestModel = getUserQuestModel(uid, zone);
        triggerUpdateQuestModel(userQuestModel, condition, zone);
    }
    public final void triggerUpdateQuestModel(UserQuestModel userQuestModel, Map<EConditionType,Integer> condition, Zone zone){
        List<QuestVO> questCfAll = new ArrayList<>();
        for (EConditionType index : condition.keySet()){
            //Lay cac dieu kien config chua cung type dieu kien
            List<QuestConditionVO> conditionCf = QuestManager.getInstance().getQuestConditionConfigDependType(index);
            List<QuestVO> questCf = QuestManager.getInstance().getQuestConfig().parallelStream().filter(obj -> {
                for(QuestConditionVO con : conditionCf){
                    if(con.id.equals(obj.rewardCondition)){
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
            questCfAll.addAll(questCf);

            List<UpdateQuestVO> update = questCf.parallelStream().
                    map(obj -> {
                        if(obj.id.equals("weekly5")){
                            List<String> queueResetHero = HeroManager.getInstance().getQueueResetHero(userQuestModel.uid, zone);
                            if(queueResetHero.size() == 2 && queueResetHero.get(0).equals(queueResetHero.get(1))) return UpdateQuestVO.create(obj.id, 0);
                        }
                        return UpdateQuestVO.create(obj.id, condition.get(index));
                    }).
                    collect(Collectors.toList());
            switch (index){
                case LEVEL_USER:
                case LEVEL_HERO:
                case FLOOR_TOWER:
                case CHAP_DUNGEON:
                case BLESLING_LEVEL:
                case ARENA_POINT:
                    //Neu co thay doi
                    updateQuest(userQuestModel, update, zone);
                    break;
                case GET_HERO:
                case SEND_FRIEND_POINT:
                case ENHANDCE_ITEM:
                case DO_MISSION:
                case DO_GUILD_HUNT:
                case COLLECT_AFK:
                case UPLEVEL_HERO:
                case DO_HUNT:
                case DO_TOWER:
                case USE_FAST_REWARD:
                case SUMMON_TAVERN:
                case DO_ARENA:
                case DO_CAMPAIGN:
                case WIN_ARENA:
                case WIN_MISSION:
                case WIN_HUNT:
                case BUY_GENERAL_STORE:
                case BUY_GUILD_STORE:
                case BUY_HUNTER_STORE:
                case GET_ALLIANCE_COIN:
                case BLESLING_HERO:
                case GOLD_AFK:
                    upQuest(userQuestModel, update, zone);
                    break;
            }
        }
    }




    /*----------------------------------------------------  CONFIG  --------------------------------------------------*/
    private QuestChestConfig getQuestChestConfig(Zone zone){
        if(ServerManager.getInstance().isActiveEventModule(EModule.QUEST, zone)){
            return EventInGameManager.getInstance().getQuestChestConfig();
        }else {
            return this.questChestConfig;
        }
    }

    /**
     * Lay quest config
     * @return
     */
    public List<QuestVO> getQuestConfig(){
        List<QuestVO> list = new ArrayList<>();
        list.addAll(questConfig.daily);
        list.addAll(questConfig.weekly);
        list.addAll(questConfig.allTime);
        return list.parallelStream().filter(obj -> EStatus.COMMING_SOON.getId() != obj.status).collect(Collectors.toList());
    }
    public List<QuestVO> getQuestConfig(EQuestType type){
        List<QuestVO> list = new ArrayList<>();
        switch (type){
            case DAILY:
                list = questConfig.daily;
                break;
            case WEEKLY:
                list = questConfig.weekly;
                break;
            case ALL_TIME:
                list = questConfig.allTime;
                break;
        }
        return list.parallelStream().filter(obj -> EStatus.COMMING_SOON.getId() != obj.status).collect(Collectors.toList());
    }
    public QuestVO getQuestConfig(String id){
        return questConfig.readQuestConfig(id);
    }

    /**
     * Lay config quest
     * @return
     */
    public List<QuestConditionVO> getQuestConditionConfig(){
        return questConditionConfig.list;
    }
    public QuestConditionVO getQuestConditionConfig(String id){
        return questConditionConfig.readQuestConditionConfig(id);
    }
    public List<QuestConditionVO> getQuestConditionConfigDependType(EConditionType type){
        return getQuestConditionConfig().parallelStream().
                filter(obj -> obj.condition.type.equals(type.getId())).
                collect(Collectors.toList());
    }

    /**
     * Lay config ruong bonus quest
     * @return
     */
    public List<QuestChestVO> getQuestChestConfig(EQuestType type, Zone zone){
        switch (type){
            case DAILY:
                return getQuestChestConfig(zone).daily;
            case WEEKLY:
                return getQuestChestConfig(zone).weekly;
            case ALL_TIME:
                return getQuestChestConfig(zone).allTime;
        }
        return new ArrayList<>();
    }
    public QuestChestVO getQuestChestConfig(String id, Zone zone){
        return getQuestChestConfig(zone).readChestQuestConfig(id);
    }
}
