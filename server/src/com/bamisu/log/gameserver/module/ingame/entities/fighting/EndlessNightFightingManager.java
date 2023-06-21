package com.bamisu.log.gameserver.module.ingame.entities.fighting;

import com.bamisu.gamelib.entities.Attr;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.characters.entities.Creep;
import com.bamisu.log.gameserver.module.characters.entities.Hero;
import com.bamisu.log.gameserver.module.characters.entities.Mboss;
import com.bamisu.log.gameserver.module.ingame.FightingExtension;
import com.bamisu.log.gameserver.module.ingame.cmd.rec.RecMove;
import com.bamisu.log.gameserver.module.ingame.cmd.send.MovePackage;
import com.bamisu.log.gameserver.module.ingame.cmd.send.SendMove;
import com.bamisu.log.gameserver.module.ingame.entities.Diamond;
import com.bamisu.log.gameserver.module.ingame.entities.MatchState;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActorStatistical;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action.EffectApplyAction;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action.EnergyChangeAction;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action.SkillingAction;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.EPlayerType;
import com.bamisu.log.gameserver.module.ingame.entities.player.NPCPlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.log.gameserver.module.ingame.entities.skill.Combo;
import com.bamisu.log.gameserver.module.ingame.entities.skill.ComboType;
import com.bamisu.log.gameserver.module.ingame.entities.skill.FinalColorCombo;
import com.bamisu.log.gameserver.module.ingame.entities.wincodition.EFightingResult;
import com.bamisu.log.gameserver.module.ingame.entities.wincodition.WinConditionUtils;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by Popeye on 5:01 PM, 11/24/2020
 */
public class EndlessNightFightingManager extends PvMManager {
    List<ICharacter> npcTeam;

    public EndlessNightFightingManager(Room room) {
        super(room);
        this.maxTurn = 15;
        this.function = EFightingFunction.ENDLESS_NIGHT;
        this.reward = new ArrayList<>();
        this.mapBonus = Element.FOREST;
        this.bg = "BBG015";

        ISFSArray sfsArray = SFSArray.newFromJsonData(String.valueOf(room.getProperty(Params.NPC_TEAM)));
        npcTeam = new ArrayList<>();
        for (int i = 0; i < sfsArray.size(); i++) {
            ISFSObject object = sfsArray.getSFSObject(i);
            if (object == null) {
                npcTeam.add(null);
                continue;
            }
            if (object.getInt("characterType") == ECharacterType.MiniBoss.getType()) {
                npcTeam.add(Utils.fromJson(object.toJson(), Mboss.class));
            } else if (object.getInt("characterType") == ECharacterType.Hero.getType()) {
                npcTeam.add(Utils.fromJson(object.toJson(), Hero.class));
            } else if (object.getInt("characterType") == ECharacterType.Creep.getType()) {
                npcTeam.add(Utils.fromJson(object.toJson(), Creep.class));
            }
        }
    }

    @Override
    public void onAllPlayerJoin() {
        super.onAllPlayerJoin();

        //get monster enemy
        players.add(new NPCPlayer(this.getRoom(), npcTeam, false));
        startGame(0);
    }

    @Override
    public ISFSObject checkEndGame(boolean isLastTurn) {
        //check max turn
        boolean isEnd = isLastTurn;

        //check chết hết hero
        for (BasePlayer player : players) {
            if (!player.isLive()) {
                isEnd = true;
            }
        }
        if (!isEnd) return null;
        //thỏa mãn điền kiện kế thúc

        // check điều kiện hoàn thành
        ISFSObject data = new SFSObject();

        //put Điều kiện hoàn thành
        List<Boolean> conditionResult = WinConditionUtils.check(winConditions, this);
        ISFSArray conditionArray = new SFSArray();
        for (int i = 0; i < winConditions.size(); i++) {
            SFSObject condition = new SFSObject();
            condition.putUtfString(Params.CONDITON, winConditions.get(i));
            condition.putBool(Params.RESULT, conditionResult.get(i));
            conditionArray.addSFSObject(condition);
        }
        data.putSFSArray(Params.CONDITON, conditionArray);

        //push phần thưởng
        ISFSArray rewardArray = new SFSArray();
        for (ResourcePackage resourcePackage : reward) {
            rewardArray.addSFSObject(resourcePackage.toSFSObject());
        }
        data.putSFSArray(Params.REWARD, rewardArray);

        //thống kê thông số
        List<ActorStatistical> actorStatisticals = getStatisticals();
        data.putSFSArray(Params.STATISTICAL, SFSArray.newFromJsonData(Utils.toJson(actorStatisticals)));

        //hoành thành tất cả các điều kiện
        boolean isComplate = !conditionResult.contains(false);
//        if(isComplate = Utils.rate(50));
        if (isComplate) {
            data.putInt(Params.RESULT, EFightingResult.WIN.getIntValue());
        } else {
            data.putInt(Params.RESULT, EFightingResult.LOSE.getIntValue());
        }

        return data;
    }

    @Override
    public List<ActionResult> befoTurn() {
        List<ActionResult> actionResults = super.befoTurn();
        BasePlayer basePlayer = getPlayer(1);
        for (TeamSlot teamSlot : basePlayer.team) {
            if (teamSlot.haveCharacter()) {
                if (teamSlot.getCharacter().getType() == ECharacterType.MiniBoss) {
                    teamSlot.getCharacter().action(new EffectApplyAction(
                            teamSlot.getCharacter(),
                            EEffect.Stat_Buff,
                            999,
                            Arrays.asList(
                                    Attr.ATTACK.shortName(),
                                    20 * turnCount,
                                    20 * turnCount
                            ),
                            false));
                }
            }
        }
        return actionResults;
    }

    @Override
    public synchronized void onEndGame(ISFSObject endGameData) {
        if (isEnd) return;
        isEnd = true;

        //complate mission
//        boolean isWin = endGameData.getInt(Params.RESULT) == EFightingResult.WIN.getIntValue();
//
//        Map<String, Object> statisticals = new ConcurrentHashMap<>(); //damage info
//        for (BasePlayer player : players) {
//            if (player.getPlayerType() == EPlayerType.HUMAN) {
//                statisticals = getPlayerStatisticals(player);
//                break;
//            }
//        }

        ISFSObject resultData = new SFSObject();
        resultData.putLong(Params.UID, uid);
//        resultData.putSFSObject(Params.STATISTICAL, SFSObject.newFromJsonData(Utils.toJson(statisticals)));
//        resultData.putBool(Params.WIN, isWin);
        resultData.putLong(Params.POINT, getHeroDieCount(getPlayer(1)));
        resultData.putLong(Params.CANDY, getCandyCount(getPlayer(1)));
        this.getRoom().getZone().getExtension().handleInternalMessage(CMD.InternalMessage.FIGHT_ENDLESS_NIGHT_RESULT, resultData);
        super.onEndGame(endGameData);
    }

    /**
     * Tổng số lần hero chết của 1 player
     * @return
     */
    @Override
    public int getHeroDieCount(BasePlayer player){
        int dieCount = 0;
        for (TeamSlot teamSlot : player.getTeam()) {
            if (teamSlot.haveCharacter()) {
                dieCount += teamSlot.getCharacter().getActorStatistical().dieCount;
            }
        }
        return dieCount;
    }

    @Override
    public int getCampaignArea() {
        return 0;
    }

    @Override
    public int getCampaignStation() {
        return 0;
    }

    public int getCandyCount(BasePlayer player){
        int dieCount = 0;
        for (TeamSlot teamSlot : player.getTeam()) {
            if (teamSlot.haveCharacter()) {
                dieCount += teamSlot.getCharacter().getActorStatistical().dieCount;

                if(teamSlot.getCharacter().getActorStatistical().dieCount > 0){ //chết lần đầu + 1 kẹo
                    dieCount++;
                }
            }
        }
        return dieCount;
    }

}
