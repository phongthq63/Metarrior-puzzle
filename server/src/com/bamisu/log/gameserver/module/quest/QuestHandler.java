package com.bamisu.log.gameserver.module.quest;

import com.bamisu.gamelib.item.entities.IResourcePackage;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.datamodel.quest.UserQuestModel;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.quest.cmd.rec.RecAddProgressQuest;
import com.bamisu.log.gameserver.module.quest.cmd.rec.RecGetRewardChestQuest;
import com.bamisu.log.gameserver.module.quest.cmd.rec.RecGetRewardQuest;
import com.bamisu.log.gameserver.module.quest.cmd.send.SendAddProgressQuest;
import com.bamisu.log.gameserver.module.quest.cmd.send.SendGetRewardChestQuest;
import com.bamisu.log.gameserver.module.quest.cmd.send.SendGetRewardQuest;
import com.bamisu.log.gameserver.module.quest.cmd.send.SendGetTableQuest;
import com.bamisu.log.gameserver.module.quest.config.entities.QuestChestVO;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.log.gameserver.module.quest.config.entities.QuestVO;
import com.bamisu.log.gameserver.module.quest.entities.UpdateQuestVO;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class QuestHandler extends ExtensionBaseClientRequestHandler {

    public QuestHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_QUEST;
        new QuestGameEventHandler(extension.getParentZone());
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_GET_TABLE_QUEST:
                doGetTableQuest(user, data);
                break;
            case CMD.CMD_GET_REWARD_QUEST:
                doGetRewardQuest(user, data);
                break;
            case CMD.CMD_GET_REWARD_CHEST_QUEST:
                doGetRewardChestQuest(user, data);
                break;
            case CMD.CMD_ADD_PROGRESS_QUEST:
                doAddProgressQuest(user, data);
                break;
        }
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_QUEST, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_QUEST, this);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Lay thong tin bang nhiem vu
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetTableQuest(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        SendGetTableQuest objPut = new SendGetTableQuest();
        objPut.listTab = QuestManager.getInstance().getUserTableQuest(uid, getParentExtension().getParentZone());
        send(objPut, user);
    }

    @WithSpan
    private void doGetRewardQuest(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecGetRewardQuest objGet = new RecGetRewardQuest(data);
        QuestVO questCf = QuestManager.getInstance().getQuestConfig(objGet.id);
        if(questCf == null){
            SendGetRewardQuest objPut = new SendGetRewardQuest(ServerConstant.ErrorCode.ERR_NOT_EXSIST_QUEST);
            send(objPut, user);
            return;
        }

        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, getParentExtension().getParentZone());
        //Kiem tra co the hoan thanh khong
        if(!QuestManager.getInstance().canCompleteQuest(userQuestModel, objGet.id, getParentExtension().getParentZone())){
            SendGetRewardQuest objPut = new SendGetRewardQuest(ServerConstant.ErrorCode.ERR_CANT_COMPLETE_QUEST);
            send(objPut, user);
            return;
        }

        //Hoan thanh quest --- save data
        if(!QuestManager.getInstance().completeQuest(userQuestModel, objGet.id, getParentExtension().getParentZone())){
            SendGetRewardQuest objPut = new SendGetRewardQuest(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Lay phan thuong --- Phan thuong = so lan hoan thanh - 1
        //Phai hoan thanh quest truoc roi moi nhan thuong -> tranh spam
        List<IResourcePackage> listReward = QuestManager.getInstance().rewardCompleteQuest(
                userQuestModel,
                objGet.id,
                QuestManager.getInstance().getCountCompleteQuest(userQuestModel, objGet.id, getParentExtension().getParentZone()) - 1,
                getParentExtension().getParentZone());
        if(listReward.isEmpty()){
            SendGetRewardQuest objPut = new SendGetRewardQuest(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendGetRewardQuest objPut = new SendGetRewardQuest();
        objPut.id = objGet.id;
        objPut.type = Byte.parseByte(questCf.type);
        objPut.point = (short) QuestManager.getInstance().getUserTabQuestInfo(userQuestModel, questCf.type, getParentExtension().getParentZone()).chest.point;
        objPut.listReward = listReward;
        send(objPut, user);
    }

    @WithSpan
    private void doGetRewardChestQuest(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecGetRewardChestQuest objGet = new RecGetRewardChestQuest(data);
        UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, getParentExtension().getParentZone());
        if(QuestManager.getInstance().getQuestChestConfig(objGet.id, getParentExtension().getParentZone()) == null){
            SendGetRewardChestQuest objPut = new SendGetRewardChestQuest(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHEST_QUEST);
            send(objPut, user);
            return;
        }
        //Kiem tra co the nhan hay khong
        if(!QuestManager.getInstance().canRewardChest(userQuestModel, objGet.id, getParentExtension().getParentZone())){
            SendGetRewardChestQuest objPut = new SendGetRewardChestQuest(ServerConstant.ErrorCode.ERR_CANT_REWARD_CHEST_QUEST);
            send(objPut, user);
            return;
        }

        //thay doi trang thai chest
        if(!QuestManager.getInstance().rewardChest(userQuestModel, objGet.id, getParentExtension().getParentZone())){
            SendGetRewardChestQuest objPut = new SendGetRewardChestQuest(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Add phan thuong nhan ruong vao tui
        QuestChestVO chestCf = QuestManager.getInstance().getQuestChestConfig(objGet.id, getParentExtension().getParentZone());
        if(!BagManager.getInstance().addItemToDB(chestCf.readRewardChest(), uid, getParentExtension().getParentZone(), UserUtils.TransactionType.GET_REWARD_QUEST)){
            SendGetRewardChestQuest objPut = new SendGetRewardChestQuest(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendGetRewardChestQuest objPut = new SendGetRewardChestQuest();
        objPut.id = objGet.id;
        objPut.type = Byte.parseByte(chestCf.type);
        send(objPut, user);
    }

    @WithSpan
    private void doAddProgressQuest(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecAddProgressQuest objGet = new RecAddProgressQuest(data);

        if(objGet.count < 1 ||
                !(objGet.id.equals("all14") || objGet.id.equals("all15") || objGet.id.equals("all16") || objGet.id.equals("all17"))){
            SendAddProgressQuest objPut = new SendAddProgressQuest(ServerConstant.ErrorCode.ERR_INVALID_VALUE);
            send(objPut, user);
            return;
        }

        List<UpdateQuestVO> listUpdate = new ArrayList<>();
        listUpdate.add(UpdateQuestVO.create(objGet.id, objGet.count));
        if(!QuestManager.getInstance().upQuest(uid, listUpdate, getParentExtension().getParentZone())){
            SendAddProgressQuest objPut = new SendAddProgressQuest(ServerConstant.ErrorCode.ERR_NOT_EXSIST_QUEST);
            send(objPut, user);
            return;
        }

        SendAddProgressQuest objPut = new SendAddProgressQuest();
        send(objPut, user);
    }
}
