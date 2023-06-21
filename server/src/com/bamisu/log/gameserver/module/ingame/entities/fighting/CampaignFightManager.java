package com.bamisu.log.gameserver.module.ingame.entities.fighting;

import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.campaign.CampaignManager;
import com.bamisu.log.gameserver.module.campaign.config.entities.Area;
import com.bamisu.log.gameserver.module.campaign.config.entities.Station;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.characters.entities.Creep;
import com.bamisu.log.gameserver.module.characters.entities.Hero;
import com.bamisu.log.gameserver.module.characters.entities.Mboss;
import com.bamisu.log.gameserver.module.ingame.FightingExtension;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActorStatistical;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.EPlayerType;
import com.bamisu.log.gameserver.module.ingame.entities.player.NPCPlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.log.gameserver.module.ingame.entities.wincodition.EFightingResult;
import com.bamisu.log.gameserver.module.ingame.entities.wincodition.WinConditionUtils;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by Popeye on 3:43 PM, 5/12/2020
 */
public class CampaignFightManager extends PvMManager {
    int area;
    int station;
    Area areaConfig;
    public Station stationConfig;
    int oldStar;
    List<ICharacter> npcTeam;
    public List<Integer> tutorialState;

    public CampaignFightManager(Room room) {
        super(room);
        this.function = EFightingFunction.CAMPAIGN;
        this.oldStar = (int) room.getProperty(Params.OLD_STAR);
        //get monster enemy and reward
        this.area = (int) room.getProperty(Params.AREA_ID);
        this.station = (int) room.getProperty(Params.STATION_ID);
        this.areaConfig = Utils.fromJson((String) room.getProperty(Params.AREA), Area.class);
        this.stationConfig = Utils.fromJson((String) room.getProperty(Params.STATION), Station.class);
        this.tutorialState = (List<Integer>) room.getProperty(Params.TUTORIAL_STATE);

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

        this.mapBonus = Element.fromID(stationConfig.terrain);
        this.bossMode = stationConfig.bossMode;
        this.bg = stationConfig.bbg;
    }

    @Override
    public void onAllPlayerJoin() {
        super.onAllPlayerJoin();
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
        ISFSObject endGameData = new SFSObject();

        //put Điều kiện hoàn thành
        List<Boolean> conditionResult = WinConditionUtils.check(winConditions, this);
        ISFSArray conditionArray = new SFSArray();
        for (int i = 0; i < winConditions.size(); i++) {
            SFSObject condition = new SFSObject();
            condition.putUtfString(Params.CONDITON, winConditions.get(i));
            condition.putBool(Params.RESULT, conditionResult.get(i));
            conditionArray.addSFSObject(condition);
        }
        endGameData.putSFSArray(Params.CONDITON, conditionArray);

        //thống kê thông số
        List<ActorStatistical> actorStatisticals = getStatisticals();
        endGameData.putSFSArray(Params.STATISTICAL, SFSArray.newFromJsonData(Utils.toJson(actorStatisticals)));

        //hoành thành tất cả các điều kiện
        boolean isComplate = conditionResult.contains(true);

        endGameData.putInt(Params.AREA, area);
        endGameData.putInt(Params.STATION, station);

        //star
        endGameData.putInt(Params.OLD_STAR, this.oldStar);

        int countStar = 0;
        for(Boolean bool : conditionResult){
            if (bool) countStar ++;
        }
        endGameData.putInt(Params.STAR, countStar);

//        if (!conditionResult.get(0)) {
//            endGameData.putInt(Params.STAR, countStar);
//        } else if (conditionResult.get(2)) {
//            endGameData.putInt(Params.STAR, 3);
//        } else if (conditionResult.get(1)) {
//            endGameData.putInt(Params.STAR, 2);
//        } else {
//            endGameData.putInt(Params.STAR, 1);
//        }

        if (isComplate) {
            endGameData.putInt(Params.RESULT, EFightingResult.WIN.getIntValue());
        } else {
            endGameData.putInt(Params.RESULT, EFightingResult.LOSE.getIntValue());
        }

        return endGameData;
    }

    @Override
    public int getCampaignArea() {
        return area;
    }

    @Override
    public int getCampaignStation() {
        return station;
    }

    @Override
    public void onEndGame(ISFSObject endGameData) {
        if (isEnd) return;
        isEnd = true;
        //complate mission
        ISFSObject resultData = new SFSObject();
        boolean isWin = endGameData.getInt(Params.RESULT) == EFightingResult.WIN.getIntValue();

        Map<String, Object> statisticals = new ConcurrentHashMap<>(); //damage info
        for (BasePlayer player : players) {
            if (player.getPlayerType() == EPlayerType.HUMAN) {
                statisticals = getPlayerStatisticals(player);
                break;
            }
        }

        resultData.putSFSObject(Params.STATISTICAL, SFSObject.newFromJsonData(Utils.toJson(statisticals)));
        resultData.putBool(Params.WIN, isWin);
        resultData.putLong(Params.UID, uid);
        resultData.putInt(Params.STATION, station);
        resultData.putInt(Params.STAR, endGameData.getInt(Params.STAR));
        endGameData.putLong(Params.UID, uid);
        this.getRoom().getZone().getExtension().handleInternalMessage(CMD.InternalMessage.FIGHT_CAMPAIGN_RESULT, resultData);
        super.onEndGame(endGameData);
    }
}
