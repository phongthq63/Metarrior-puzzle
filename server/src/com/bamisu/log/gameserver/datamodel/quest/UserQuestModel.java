package com.bamisu.log.gameserver.datamodel.quest;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.socialnetwork.ESocialNetwork;
import com.bamisu.gamelib.utils.business.Debug;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.quest.entities.QuestInfo;
import com.bamisu.log.gameserver.datamodel.quest.entities.TabQuestInfo;
import com.bamisu.log.gameserver.module.quest.QuestManager;
import com.bamisu.log.gameserver.module.quest.config.entities.QuestVO;
import com.bamisu.log.gameserver.module.quest.defind.EQuestType;
import com.bamisu.log.gameserver.module.quest.entities.UpdateQuestVO;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserQuestModel extends DataModel {

    public long uid;
    public List<TabQuestInfo> tableQuest = new ArrayList<>();

    private final Object lockQuest = new Object();
    private final Object lockChest = new Object();


    private void initTable(){
        int time = Utils.getTimestampInSecond();

        for(EQuestType type : EQuestType.values()){
            tableQuest.add(TabQuestInfo.create(type.getId(), time));
        }
    }


    public static UserQuestModel createUserQuestModel(long uid, Zone zone){
        UserQuestModel userQuestModel = new UserQuestModel();
        userQuestModel.uid = uid;
        userQuestModel.initTable();
        userQuestModel.saveToDB(zone);

        return userQuestModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserQuestModel copyFromDBtoObject(long uId, Zone zone) {
        UserQuestModel userQuestModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if(userQuestModel == null){
            userQuestModel = UserQuestModel.createUserQuestModel(uId, zone);
        }
        return userQuestModel;
    }

    private static UserQuestModel copyFromDBtoObject(String uId, Zone zone) {
        UserQuestModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserQuestModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserQuestModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Lay bang nhiem vu
     * Da refresh nhung chua save
     * @return
     */
    public List<TabQuestInfo> readListTabQuest(Zone zone){
        synchronized (lockQuest){
            boolean haveRefresh = false;
            boolean isLinkedAll = false;
            List<Boolean> checkLink = new ArrayList<>();

            for(TabQuestInfo tab : tableQuest){
                //Kiem tra refresh
                if(!haveRefresh){
                    haveRefresh = tab.refresh();
                }else {
                    tab.refresh();
                }

                //Kiem tra link account quest
                checkLink.clear();
                for(QuestInfo quest : tab.quests){
                    if((quest.id.equals("all0") || quest.id.equals("all1") || quest.id.equals("all2")) && quest.complete >= 1){
                        checkLink.add(true);
                    }
                }
                if(checkLink.size() == 3) isLinkedAll = true;
            }

            //Kiem tra link account
            if(!isLinkedAll){
                UserModel userModel = ((ZoneExtension)zone.getExtension()).getUserManager().getUserModel(uid);
                List<UpdateQuestVO> update = new ArrayList<>();
                for(int idLink : userModel.linked){
                    switch (ESocialNetwork.fromIntValue(idLink)){
                        case FACEBOOK:
                            update.add(UpdateQuestVO.create("all0", 1));
                            break;
                        case GOOGLE:
                            update.add(UpdateQuestVO.create("all1", 1));
                            break;
                        case GAME_CENTER:
                            update.add(UpdateQuestVO.create("all2", 1));
                            break;
                    }
                }
                //Update khong save --> ko can refresh
                if(updateQuestInClass(update, zone)) haveRefresh = true;
            }

            if(haveRefresh){
                saveToDB(zone);
            }
        }
        return tableQuest;
    }

    /**
     * update quest
     */
    public boolean updateQuest(List<UpdateQuestVO> update, Zone zone){
        Iterator<UpdateQuestVO> iterator;
        QuestVO questCf;
        boolean haveSave = false;

        synchronized (lockQuest){
            for(TabQuestInfo tab : readListTabQuest(zone)){
                iterator = update.iterator();

                up_loop:
                while (iterator.hasNext()){
                    UpdateQuestVO up = iterator.next();
                    questCf = QuestManager.getInstance().getQuestConfig(up.id);

                    if(questCf.type.equals(tab.type)){

                        //TH quest da ton tai -> gan =
                        for(QuestInfo quest : tab.quests){
                            if(quest.id.equals(up.id)){
                                if(quest.point < up.point){
                                    quest.point = up.point;
                                    haveSave = true;
                                }

                                iterator.remove();
                                continue up_loop;
                            }
                        }

                        //TH quest chua co -> tao moi
                        tab.quests.add(QuestInfo.create(up.id, up.point));
                        iterator.remove();
                        haveSave = true;
                    }
                }
            }

            return haveSave;
        }
    }
    private boolean updateQuestInClass(List<UpdateQuestVO> update, Zone zone){
        Iterator<UpdateQuestVO> iterator;
        QuestVO questCf;
        boolean haveSave = false;

        synchronized (lockQuest){
            for(TabQuestInfo tab : tableQuest){
                iterator = update.iterator();

                up_loop:
                while (iterator.hasNext()){
                    UpdateQuestVO up = iterator.next();
                    questCf = QuestManager.getInstance().getQuestConfig(up.id);
                    if(questCf == null) continue up_loop;

                    if(questCf.type.equals(tab.type)){

                        //TH quest da ton tai -> gan =
                        for(QuestInfo quest : tab.quests){
                            if(quest.id.equals(up.id)){
                                if(quest.point < up.point){
                                    quest.point = up.point;
                                    haveSave = true;
                                }
                                iterator.remove();
                                continue up_loop;
                            }
                        }

                        //TH quest chua co -> tao moi
                        tab.quests.add(QuestInfo.create(up.id, up.point));
                        iterator.remove();
                        haveSave = true;
                    }
                }
            }

            return haveSave;
        }
    }
    public boolean upQuest(List<UpdateQuestVO> update, Zone zone){
        Iterator<UpdateQuestVO> iterator;
        UpdateQuestVO up;
        QuestVO questCf;
        boolean haveSave = false;

        synchronized (lockQuest){
            for(TabQuestInfo tab : readListTabQuest(zone)){
                iterator = update.iterator();

                up_loop:
                while (iterator.hasNext()){
                    up = iterator.next();
                    questCf = QuestManager.getInstance().getQuestConfig(up.id);
                    if(questCf == null) continue up_loop;

                    if (questCf.type.equals(tab.type)){

                        for(QuestInfo quest : tab.quests){
                            //TH quest da ton tai -> + them
                            if(quest.id.equals(up.id)) {
                                quest.point += up.point;
                                iterator.remove();
                                haveSave = true;
                                continue up_loop;
                            }
                        }


                        //TH quest chua co -> tao moi
                        tab.quests.add(QuestInfo.create(up.id, up.point));
                        iterator.remove();
                        haveSave = true;
                    }
                }
            }

            return haveSave;
        }
    }

    /**
     * kiem tra co the hoan thanh hay khong
     */
    public boolean canCompleteQuest(String id, Zone zone){
        synchronized (lockQuest){
            for(TabQuestInfo tab : readListTabQuest(zone)){
                if(tab.canCompleteQuest(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * kiem tra da hoan thanh hay khong
     */
    public boolean haveCompleteQuest(String id, Zone zone){
        synchronized (lockQuest){
            for(TabQuestInfo tab : readListTabQuest(zone)){
                if(tab.haveCompleteQuest(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canRewardChest(String id, Zone zone){
        synchronized (lockChest){
            for(TabQuestInfo tab : readListTabQuest(zone)){
                if(tab.canRewardChest(id, zone)){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Hoan thanh quest
     * Phai kiem tra co the hoan thanh khong trc --- han che save data
     */
    public boolean completeQuest(String id, Zone zone){
        synchronized (lockQuest){
            for(TabQuestInfo tab : readListTabQuest(zone)){
                if(tab.completeQuest(id)){
                    return saveToDB(zone);
                }
            }
        }
        return false;
    }

    public boolean rewardChest(String id, Zone zone){
        synchronized (lockChest){
            for(TabQuestInfo tab : readListTabQuest(zone)){
                if(tab.rewardChest(id, zone)){
                    return saveToDB(zone);
                }
            }
        }
        return false;
    }
}
