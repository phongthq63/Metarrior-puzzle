package com.bamisu.log.gameserver.module.ingame.entities.fighting;

import com.bamisu.log.gameserver.datamodel.hunt.entities.HuntInfo;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.characters.entities.Creep;
import com.bamisu.log.gameserver.module.characters.entities.Hero;
import com.bamisu.log.gameserver.module.characters.entities.Mboss;
import com.bamisu.log.gameserver.module.hunt.HuntManager;
import com.bamisu.log.gameserver.module.ingame.FightingExtension;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActorStatistical;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.EPlayerType;
import com.bamisu.log.gameserver.module.ingame.entities.player.NPCPlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.log.gameserver.module.ingame.entities.wincodition.EFightingResult;
import com.bamisu.log.gameserver.module.ingame.entities.wincodition.WinConditionUtils;
import com.bamisu.log.gameserver.module.mission.MissionManager;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by Popeye on 11:27 AM, 5/23/2020
 */
public class HuntFightingManager extends PvMManager {
    HuntInfo huntInfo;
    List<ICharacter> npcTeam;

    public HuntFightingManager(Room room) {
        super(room);
        this.function = EFightingFunction.HUNT;
        this.huntInfo = Utils.fromJson(Utils.toJson(room.getProperty(Params.HUNT_INFO)), HuntInfo.class);
        this.reward = huntInfo.reward;
        this.mapBonus = Element.fromID(huntInfo.mapBonus);
        this.bg = huntInfo.bg;
        this.maxTurn = 10;

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
    public void initHp() {
        super.initHp();
        SFSArray remainingHP = SFSArray.newFromJsonData(String.valueOf(this.getRoom().getProperty(Params.REMAINING_HP)));
        for(TeamSlot teamSlot : players.get(1).team){
            if(teamSlot.haveCharacter() && teamSlot.getCharacter().isLive()){
                teamSlot.getCharacter().setCurrentHP((int) (remainingHP.getFloat(players.get(1).team.indexOf(teamSlot)) * teamSlot.getCharacter().getCurrentHP() / 100));
            }
        }
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
    public int getCampaignArea() {
        return 0;
    }

    @Override
    public int getCampaignStation() {
        return 0;
    }

    @Override
    public void onEndGame(ISFSObject endGameData) {
        if(isEnd) return;
        isEnd = true;
        //complate mission
        boolean isWin = endGameData.getInt(Params.RESULT) == EFightingResult.WIN.getIntValue();

        Map<String, Object> statisticals = new ConcurrentHashMap<>(); //damage info
        for (BasePlayer player : players) {
            if (player.getPlayerType() == EPlayerType.HUMAN) {
                statisticals = getPlayerStatisticals(player);
                break;
            }
        }

        List<Float> remainingHPList = new ArrayList<>();
        for(TeamSlot teamSlot : players.get(1).team){
            if(teamSlot.haveCharacter() && teamSlot.getCharacter().isLive()){
                remainingHPList.add((float) Utils.round(teamSlot.getCharacter().getCurrentHPPercent(), 10));
            }else {
                remainingHPList.add(0f);
            }
        }

        ISFSObject resultData = new SFSObject();
        resultData.putLong(Params.UID, uid);
        resultData.putSFSObject(Params.STATISTICAL, SFSObject.newFromJsonData(Utils.toJson(statisticals)));
        resultData.putBool(Params.WIN, isWin);
        resultData.putFloatArray(Params.Remaining_HP, remainingHPList);
        this.getRoom().getZone().getExtension().handleInternalMessage(CMD.InternalMessage.FIGHT_HUNT_RESULT, resultData);
        super.onEndGame(endGameData);
    }
}
