package com.bamisu.log.gameserver.module.ingame.entities.fighting;

import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.datamodel.mission.entities.MissionInfo;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.characters.entities.Creep;
import com.bamisu.log.gameserver.module.characters.entities.Hero;
import com.bamisu.log.gameserver.module.characters.entities.Mboss;
import com.bamisu.log.gameserver.module.ingame.FightingExtension;
import com.bamisu.log.gameserver.module.ingame.cmd.rec.RecMove;
import com.bamisu.log.gameserver.module.ingame.cmd.send.MovePackage;
import com.bamisu.log.gameserver.module.ingame.cmd.send.SendJoinRoom;
import com.bamisu.log.gameserver.module.ingame.cmd.send.SendMove;
import com.bamisu.log.gameserver.module.ingame.entities.Diamond;
import com.bamisu.log.gameserver.module.ingame.entities.MatchState;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActorStatistical;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.player.*;
import com.bamisu.log.gameserver.module.ingame.entities.skill.Combo;
import com.bamisu.log.gameserver.module.ingame.entities.skill.ComboType;
import com.bamisu.log.gameserver.module.ingame.entities.skill.FinalColorCombo;
import com.bamisu.log.gameserver.module.ingame.entities.wincodition.EFightingResult;
import com.bamisu.log.gameserver.module.ingame.entities.wincodition.WinConditionUtils;
import com.bamisu.log.gameserver.module.mission.MissionManager;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Create by Popeye on 3:55 PM, 5/12/2020
 */
public class MissionFightingManager extends PvMManager {
    public long currentPoint = 0;
    public int time;
//    public ScheduledExecutorService scheduledExecutorService = LizThreadManager.getInstance().getFixExecutorServiceByName("MissionFightingManager", 1);
    public ScheduledFuture<?> scheduleEndGame;

    public MissionFightingManager(Room room) {
        super(room);
        this.function = EFightingFunction.MISSION;
        this.time = 30;
        this.mapBonus = Element.FIRE;
        this.bg = "BBG014";
    }

    @Override
    public void startMisson() {
//        scheduleEndGame = scheduledExecutorService.schedule(() -> {
//            ISFSObject endgameData = checkEndGame(false);
//            onEndGame(endgameData);
//        }, time + 3, TimeUnit.SECONDS);
    }

    @Override
    public int getCampaignArea() {
        return 0;
    }

    @Override
    public int getCampaignStation() {
        return 0;
    }

    @Override
    public void onPlayerJoin(User user) {
        onAllPlayerJoin();
    }

    @Override
    public void onAllPlayerJoin() {
        startGame(0);
    }

    @Override
    public void startGame(int firstTurn) {
        //tạo table
        puzzleTable.genMatrixWithNoCombo();
        setCurrentTurn(firstTurn);
        setState(MatchState.MOVING);

        //send notify
        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (user != null) {
            SendJoinRoom sendJoinRoom = new SendJoinRoom();
            sendJoinRoom.manager = this;
            sendJoinRoom.yourID = 0;
            sendJoinRoom.currentTurn = firstTurn;
            sendJoinRoom.startFightingActions = new ArrayList<>();
            sendJoinRoom.packStartFightingContext();
            sendJoinRoom.packStartFightingActions();

            //only mission
            sendJoinRoom.limitTime = time;

            getFightingHandler().send(sendJoinRoom, user);
        }
    }

    @Override
    public void move(User user, RecMove recMove) {
//        Map<Diamond, Integer> totalComboMap = new ConcurrentHashMap<>();
//        int diamondCount = 0;
//        for (int i = Diamond.RED.getValue(); i <= Diamond.PURPLE.getValue(); i++) {
//            totalComboMap.put(Diamond.fromID(i), 0);
//        }
//        for (Combo combo : recMove.combos) {
//            totalComboMap.put(combo.diamond, totalComboMap.get(combo.diamond) + combo.count);
//            if (combo.count <= 0) continue;
//            switch (combo.count) {
//                case 1:
//                case 2:
//                case 3:
//                    diamondCount += combo.count;
//                    break;
//                case 4:
//                    diamondCount += combo.count * 2;
//                    break;
//                default:
//                    diamondCount += combo.count * 3;
//            }
//        }

        currentPoint += recMove.point;

        if (recMove.isEnd) {
            onEndGame(checkEndGame(true));
        }

        turnCount++;
        setState(MatchState.MOVING);
    }

    @Override
    public ISFSObject checkEndGame(boolean isLastTurn) {
        ISFSObject data = new SFSObject();
        data.putInt(Params.RESULT, EFightingResult.WIN.getIntValue());

        //push phần thưởng
        ISFSArray rewardArray = new SFSArray();
        for (ResourcePackage resourcePackage : reward) {
            rewardArray.addSFSObject(resourcePackage.toSFSObject());
        }
        data.putSFSArray(Params.REWARD, rewardArray);

        boolean isWin = true;
        if (isWin) {
            data.putInt(Params.RESULT, EFightingResult.WIN.getIntValue());
        } else {
            data.putInt(Params.RESULT, EFightingResult.LOSE.getIntValue());
        }

        //put Điều kiện hoàn thành
        ISFSArray conditionArray = new SFSArray();
        SFSObject condition = new SFSObject();
        condition.putUtfString(Params.CONDITON, "WCO100");
        condition.putBool(Params.RESULT, isWin);
        conditionArray.addSFSObject(condition);
        data.putSFSArray(Params.CONDITON, conditionArray);

        return data;
    }

    @Override
    public void onEndGame(ISFSObject endGameData) {
        if (isEnd) return;
        isEnd = true;

        //send to client
        SendMove sendMove = new SendMove();
        MovePackage movePackage = new MovePackage();
        movePackage.pushEndGameData(endGameData);
        sendMove.pushPackage(movePackage);
        getFightingHandler().broadcast(sendMove, this.getRoom());

        //complate mission
        ISFSObject resultData = new SFSObject();
        boolean isWin = endGameData.getInt(Params.RESULT) == EFightingResult.WIN.getIntValue();
        resultData.putLong(Params.UID, uid);
        resultData.putBool(Params.WIN, isWin);
        resultData.putSFSObject(Params.STATISTICAL, new SFSObject());
        resultData.putLong(Params.POINT, this.currentPoint);
        this.getRoom().getZone().getExtension().handleInternalMessage(CMD.InternalMessage.FIGHT_MISSION_RESULT, resultData);

        super.onEndGame(endGameData);
    }

    @Override
    public void stopAllSchedule() {
        super.stopAllSchedule();
        if (scheduleEndGame != null && !scheduleEndGame.isDone()) {
            scheduleEndGame.cancel(false);
        }
    }
}
