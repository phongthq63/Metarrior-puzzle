package com.bamisu.log.gameserver.module.WoL;

import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.datamodel.WoL.WoLModel;
import com.bamisu.log.gameserver.datamodel.WoL.WoLUserModel;
import com.bamisu.log.gameserver.module.WoL.cmd.send.SendWoLGetRank;
import com.bamisu.log.gameserver.module.WoL.cmd.send.SendWoLReceiveReward;
import com.bamisu.log.gameserver.module.WoL.cmd.send.SendWoLUserAchievement;
import com.bamisu.log.gameserver.module.WoL.defines.*;
import com.bamisu.log.gameserver.module.WoL.entities.*;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WoLManager {
    private static WoLManager ourInstance = null;
    public static WoLManager getInstance(){
        if (ourInstance == null){
            ourInstance = new WoLManager();
        }
        return ourInstance;
    }

    private WoLHandler woLHandler;
    private WoLConfig woLConfig;

    public WoLManager(){
        woLConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.WoL.FILE_PATH_CONFIG_WOL), WoLConfig.class);
    }

    public List<WoLRankVO> getWoLConfig(){
        return woLConfig.listWoL;
    }

    public WoLRankVO getArea(int area){
        for (WoLRankVO vo: getWoLConfig()){
            if (vo.area == area){
                return vo;
            }
        }
        return null;
    }

    public WoLConfigVO getStage(int stage, int area){
        for (WoLConfigVO vo: getArea(area).list){
            if (vo.stage == stage){
                return vo;
            }
        }
        return null;
    }

    /**
     * Get reward when achieved
     * @param area: 1
     * @param stage: 2
     * @param reward: 1: first user achieved
     *               2: another user achieved
     * @return resource package
     */
    public ResourcePackage getReward(int area, int stage, int challenge, int reward){
        if (reward == 1){ //First user earned
            return getWoLConfig().get(area).list.get(stage).listChallenges.get(challenge).reward1;
        }else if (reward == 2){
            return getWoLConfig().get(area).list.get(stage).listChallenges.get(challenge).reward2;
        }
        return null;
    }

    /**
     * Get condition in wol
     * @return condition
     */
    public int getConditionInWoL(int area, int stage, int challenge){
        return getWoLConfig().get(area).list.get(stage).listChallenges.get(challenge).condition;
    }

    public void setWoLHandle(WoLHandler woLHandler) {
        this.woLHandler = woLHandler;
    }

    public WoLHandler getWoLHandle(){
        return woLHandler;
    }

    public void getRank(BaseExtension extension, User user) {
        WoLModel wol = WoLModel.copyFromDBtoObject(user.getZone());
        List<WoLPlayerInArea> listPlayer = new ArrayList<>();
        for (int i = 0; i< wol.listPlayer.size(); i++){
            WoLPlayerInArea woLPlayerInArea = new WoLPlayerInArea();
            List<WoLPlayerInfoVO> listUid = convertToRankFullInfo(wol.listPlayer.get(i).listPlayer, extension, user.getZone(), i);
            woLPlayerInArea.area = wol.listPlayer.get(i).area;
            woLPlayerInArea.list = listUid;
            listPlayer.add(woLPlayerInArea);
        }

        if (listPlayer == null){
            SendWoLGetRank send = new SendWoLGetRank(ServerConstant.ErrorCode.ERR_SYS);
            woLHandler.send(send, user);
            return;
        }
        //TEST
//        long uid = extension.getUserManager().getUserModel(user).userID;
//        updateWoLPlayer(uid, user.getZone());
        //==========
        SendWoLGetRank send = new SendWoLGetRank();
        send.listPlayer = listPlayer;
        woLHandler.send(send, user);
    }

    private List<WoLPlayerInfoVO> convertToRankFullInfo(List<WoLPlayerInStageVO> listId, BaseExtension extension, Zone zone, int area) {
        List<WoLPlayerInfoVO> listPlayer = new ArrayList<>();
        for (int i = 0; i< listId.size(); i++){
            if (listId.get(i).uid != -1){
                UserModel um = extension.getUserManager().getUserModel(listId.get(i).uid);
                WoLPlayerInfoVO player = new WoLPlayerInfoVO(um.userID, um.displayName, BagManager.getInstance().getLevelUser(um.userID, zone), um.avatarFrame, um.avatar, i);
                listPlayer.add(player);
            }else{
                WoLPlayerInfoVO player = new WoLPlayerInfoVO(-1, "none", -1, -1, "none", i);
                listPlayer.add(player);
            }
        }
        return listPlayer;
    }

    public void getListUserAchievement(User user, BaseExtension extension, WoLAreaDefines woLAreaDefines, int stage) {
        UserModel um = extension.getUserManager().getUserModel(user);
        WoLUserModel player = WoLUserModel.copyFromDBtoObject(um.userID, user.getZone());
        WoLModel server = WoLModel.copyFromDBtoObject(user.getZone());
        List<WoLAchievementVO> list = new ArrayList<>();

        List<WoLConquerVO> listConquer = server.mapConquer.get(woLAreaDefines.getId());
        for (int i = 0; i< listConquer.size(); i++){
            if (listConquer.get(i).stage == stage){
                for (int challenges = 0; challenges < listConquer.get(i).listConquer.size(); challenges++){
                    //Check conquered or not..
                    if (listConquer.get(i).listConquer.get(challenges).status == WoLConquerDefine.CAN.getStatus()){
                        if (player.mapConquer.get(woLAreaDefines.getId()).get(stage).listChallenges.get(challenges).challenge == WoLConquerStatus.INCOMPLETE.getStatus()){
                            player.mapConquer.get(woLAreaDefines.getId()).get(stage).listChallenges.get(challenges).challenge = WoLConquerStatus.CAN_RECEIVE.getStatus();
                            player.mapConquer.get(woLAreaDefines.getId()).get(stage).listChallenges.get(challenges).reward = WoLRewardDefine.REWARD_2.getId();
                        }
                    }
                    //-----------
                    if (listConquer.get(i).listConquer.get(challenges).uid != -1){
                        UserModel userModel = extension.getUserManager().getUserModel(listConquer.get(i).listConquer.get(challenges).uid);
                        WoLAchievementVO woLAchievementVO;
                        if (listConquer.get(i).listConquer.get(challenges).uid != um.userID){
                            woLAchievementVO = new WoLAchievementVO(userModel.userID, userModel.displayName, BagManager.getInstance().getLevelUser(userModel.userID, user.getZone()), userModel.avatarFrame, player.mapConquer.get(woLAreaDefines.getId()).get(stage).listChallenges.get(challenges).challenge, userModel.avatar, WoLRewardDefine.REWARD_2.getId());
                        }else{
                            woLAchievementVO = new WoLAchievementVO(userModel.userID, userModel.displayName, BagManager.getInstance().getLevelUser(userModel.userID, user.getZone()), userModel.avatarFrame, player.mapConquer.get(woLAreaDefines.getId()).get(stage).listChallenges.get(challenges).challenge, userModel.avatar, WoLRewardDefine.REWARD_1.getId());
                        }
                        list.add(woLAchievementVO);

                    }else{
                        WoLAchievementVO woLAchievementVO = new WoLAchievementVO(-1, "none", -1, 0, WoLConquerStatus.INCOMPLETE.getStatus(), "none", WoLRewardDefine.REWARD_1.getId());
                        list.add(woLAchievementVO);
                    }
                }
                player.saveToDB(user.getZone());
            }

        }
        SendWoLUserAchievement send = new SendWoLUserAchievement();
        send.area = woLAreaDefines.getId();
        send.stage = stage;
        send.list = list;
        woLHandler.send(send, user);
    }

    public void receiveReward(BaseExtension extension, User user, int area, int stage, int challenge) {
        UserModel um = extension.getUserManager().getUserModel(user);
        WoLUserModel woLUserModel = WoLUserModel.copyFromDBtoObject(um.userID, user.getZone());

        //Incomplete
        if (woLUserModel.mapConquer.get(area).get(stage).listChallenges.get(challenge).challenge == WoLConquerStatus.INCOMPLETE.getStatus()){
            SendWoLReceiveReward send = new SendWoLReceiveReward(ServerConstant.ErrorCode.ERR_INCOMPLETE);
            woLHandler.send(send, user);
            return;
        }

        //Already received
        if (woLUserModel.mapConquer.get(area).get(stage).listChallenges.get(challenge).challenge == WoLConquerStatus.ALREADY_RECEIVED.getStatus()){
            SendWoLReceiveReward send = new SendWoLReceiveReward(ServerConstant.ErrorCode.ERR_ALREADY_RECEIVED);
            woLHandler.send(send, user);
            return;
        }

        //Can receive
        if (woLUserModel.mapConquer.get(area).get(stage).listChallenges.get(challenge).challenge == WoLConquerStatus.CAN_RECEIVE.getStatus()){
            ResourcePackage resourcePackage = getReward(area, stage, challenge,woLUserModel.mapConquer.get(area).get(stage).listChallenges.get(challenge).reward);
            List<ResourcePackage> listResource = new ArrayList<>();
            listResource.add(resourcePackage);
            if (BagManager.getInstance().addItemToDB(listResource, um.userID, user.getZone(), UserUtils.TransactionType.WOL_REWARD)){
                WoLUserConquer woLUserConquer = new WoLUserConquer(stage, woLUserModel.mapConquer.get(area).get(stage).listChallenges);
                woLUserConquer.listChallenges.get(challenge).challenge = WoLConquerStatus.ALREADY_RECEIVED.getStatus();
                woLUserModel.mapConquer.get(area).set(stage, woLUserConquer);
                woLUserModel.saveToDB(user.getZone());
                SendWoLReceiveReward send = new SendWoLReceiveReward();
                send.resourcePackage = resourcePackage;
                woLHandler.send(send, user);
                return;
            }
        }

        SendWoLReceiveReward send = new SendWoLReceiveReward(ServerConstant.ErrorCode.ERR_SYS);
        woLHandler.send(send, user);
    }

    public void checkWoLConditionPlayer(WoLAreaDefines woLAreaDefines, WoLStageDefine woLStageDefine, int result, long uid, Zone zone){
        WoLModel woLModel = WoLModel.copyFromDBtoObject(zone);
        WoLUserModel woLUserModel = WoLUserModel.copyFromDBtoObject(uid, zone);
        for (int challenge = 0; challenge < woLModel.mapConquer.get(woLAreaDefines.getId()).get(woLStageDefine.getId()).listConquer.size(); challenge++){
            if (!woLModel.mapConquer.get(woLAreaDefines.getId()).get(woLStageDefine.getId()).listConquer.get(challenge).status && result >= getConditionInWoL(woLAreaDefines.getId(), woLStageDefine.getId(), challenge)){
                woLModel.mapConquer.get(woLAreaDefines.getId()).get(woLStageDefine.getId()).listConquer.get(challenge).status = true;
                woLModel.mapConquer.get(woLAreaDefines.getId()).get(woLStageDefine.getId()).listConquer.get(challenge).uid = uid;

                //Add to Rank
//                WoLPlayerInStageVO woLPlayerInStageVO = new WoLPlayerInStageVO(uid, woLStageDefine.getId());
                woLModel.listPlayer.get(woLAreaDefines.getId()).listPlayer.get(woLStageDefine.getId()).uid = uid;

                //Add in model of player
                woLUserModel.mapConquer.get(woLAreaDefines.getId()).get(woLStageDefine.getId()).listChallenges.get(challenge).reward = WoLRewardDefine.REWARD_1.getId();
                woLUserModel.mapConquer.get(woLAreaDefines.getId()).get(woLStageDefine.getId()).listChallenges.get(challenge).challenge = WoLConquerStatus.CAN_RECEIVE.getStatus();

                woLModel.saveToDB(zone);
                woLUserModel.saveToDB(zone);
            }
        }


    }

    public Map<Integer, List<Integer>> checkRedNode(long uid, Zone zone){
        Map<Integer, List<Integer>> map = new HashMap<>();
        WoLUserModel woLUserModel = WoLUserModel.copyFromDBtoObject(uid, zone);
        for (int area = 0; area< WoLManager.getInstance().getWoLConfig().size(); area++){//area
            List<Integer> listStage = new ArrayList<>();
            OUTERLOOP:
            for (int stage = 0; stage < woLUserModel.mapConquer.get(area).size(); stage++){//stage
                for (int challenge = 0; challenge < woLUserModel.mapConquer.get(area).get(stage).listChallenges.size(); challenge++){//challenge
                    if (woLUserModel.mapConquer.get(area).get(stage).listChallenges.get(challenge).challenge == WoLConquerStatus.CAN_RECEIVE.getStatus()){
                        listStage.add(stage);
                        map.put(area, listStage);
                        continue OUTERLOOP;
                    }
                }
            }
        }
        return map;
    }

}
