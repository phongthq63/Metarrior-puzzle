package com.bamisu.log.gameserver.datamodel.quest.entities;

import com.bamisu.log.gameserver.module.quest.QuestManager;
import com.bamisu.log.gameserver.module.quest.config.entities.QuestChestVO;
import com.bamisu.log.gameserver.module.quest.config.entities.QuestVO;
import com.bamisu.log.gameserver.module.quest.defind.EQuestType;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TabQuestInfo {

    public String type;
    public List<QuestInfo> quests = new ArrayList<>();
    public ChestQuestInfo chest;
    public int timeStamp;



    public static TabQuestInfo create(String idTab, int timeStamp) {
        TabQuestInfo tab = new TabQuestInfo();
        tab.type = idTab;
        tab.chest = ChestQuestInfo.create();
        tab.timeStamp = timeStamp;

        return tab;
    }


    public boolean refresh(){
        String idTime = EQuestType.fromID(type).getIdETimeRefresh();
        if(idTime != null && TimeUtils.isTimeTo(ETimeType.fromID(idTime), timeStamp)){
            quests.clear();
            chest = ChestQuestInfo.create();
            timeStamp = Utils.getTimestampInSecond();
            return true;
        }
        return false;
    }

    /**
     * kiem tra da hoan thanh hay khong
     */
    public boolean canCompleteQuest(String id){
        QuestVO questCf;
        //Check du diem hoan thanh
        for(QuestInfo quest : quests){
            if(quest.id.equals(id)){
                questCf = QuestManager.getInstance().getQuestConfig(id);
                if(questCf == null) return false;

                if(quest.complete > questCf.condition.size() - 1) return false;
                return questCf.condition.get(quest.complete) <= quest.point;
            }
        }

        return false;
    }

    /**
     * kiem tra co the hoan thanh hay khong
     */
    public boolean haveCompleteQuest(String id){
        QuestVO questCf;
        //Check du diem hoan thanh
        for(QuestInfo quest : quests){
            if(quest.id.equals(id)){
                questCf = QuestManager.getInstance().getQuestConfig(id);
                if(questCf == null) return false;

                if(quest.complete > questCf.condition.size() - 1) return true;
                return questCf.condition.get(quest.complete) <= quest.point;
            }
        }

        return false;
    }

    /**
     * Hoan thanh quest
     * Phai kiem tra co the hoan thanh khong trc --- han che save data
     */
    public boolean completeQuest(String id){
        for(int i = 0; i < quests.size(); i++){
            if(quests.get(i).id.equals(id)){
                //Hoan thanh quest -> update trang thai
                quests.get(i).complete += 1;
                return true;
            }
        }
        return false;
    }

    public boolean canRewardChest(String id, Zone zone){
        //Da nhan chest
        if(chest.complete.contains(id)) return false;
        //Check Cf ton tai
        QuestChestVO questChestCf = QuestManager.getInstance().getQuestChestConfig(id, zone);
        if(questChestCf == null) return false;
        //Check diem nhan chest
        return chest.point >= questChestCf.point;
    }

    public boolean rewardChest(String id, Zone zone){
        QuestChestVO questChestCf = QuestManager.getInstance().getQuestChestConfig(id, zone);
        if(questChestCf == null) return false;
        if(type.equals(questChestCf.type)) return chest.complete.add(id);
        return false;
    }
}
