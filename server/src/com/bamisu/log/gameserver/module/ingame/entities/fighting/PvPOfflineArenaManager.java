package com.bamisu.log.gameserver.module.ingame.entities.fighting;

import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.characters.entities.Creep;
import com.bamisu.log.gameserver.module.characters.entities.Hero;
import com.bamisu.log.gameserver.module.characters.entities.Mboss;
import com.bamisu.log.gameserver.module.ingame.FightingExtension;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActorStatistical;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.NPCPlayer;
import com.bamisu.log.gameserver.module.ingame.entities.wincodition.EFightingResult;
import com.bamisu.log.gameserver.module.ingame.entities.wincodition.WinConditionUtils;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:19 PM, 12/1/2020
 */
public class PvPOfflineArenaManager extends PvMManager {
    List<ICharacter> enemyPlayerTeam;
    long enemyID;

    public PvPOfflineArenaManager(Room room) {
        super(room);
        this.function = EFightingFunction.PvP_ARENA;
        this.enemyID = (long) room.getProperty(Params.ENEMY_ID);
        this.mapBonus = Element.fromID(TeamUtils.genElement("random"));
        this.bg = "BBG014";
        this.reward = Utils.fromJsonList((String) room.getProperty(Params.REWARD), ResourcePackage.class);

        ISFSArray sfsArray = SFSArray.newFromJsonData(String.valueOf(room.getProperty(Params.NPC_TEAM)));
        enemyPlayerTeam = new ArrayList<>();
        for (int i = 0; i < sfsArray.size(); i++) {
            ISFSObject object = sfsArray.getSFSObject(i);
            if (object == null) {
                enemyPlayerTeam.add(null);
                continue;
            }
            if (object.getInt("characterType") == ECharacterType.MiniBoss.getType()) {
                enemyPlayerTeam.add(Utils.fromJson(object.toJson(), Mboss.class));
            } else if (object.getInt("characterType") == ECharacterType.Hero.getType()) {
                enemyPlayerTeam.add(Utils.fromJson(object.toJson(), Hero.class));
            } else if (object.getInt("characterType") == ECharacterType.Creep.getType()) {
                enemyPlayerTeam.add(Utils.fromJson(object.toJson(), Creep.class));
            }
        }
    }

    @Override
    public void onAllPlayerJoin() {
        super.onAllPlayerJoin();
        players.add(new NPCPlayer(this.getRoom(), enemyPlayerTeam, false));
        startGame(1);
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
        if(isComplate){
            data.putInt(Params.RESULT, EFightingResult.WIN.getIntValue());
        }else {
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

        ISFSObject resultData = new SFSObject();
        resultData.putLong(Params.UID, uid);
        resultData.putLong(Params.ENEMY, enemyID);
        resultData.putBool(Params.WIN, endGameData.getInt(Params.RESULT) == EFightingResult.WIN.getIntValue());
        resultData.putUtfString(Params.BATTLE_ID, battleID);
        this.getRoom().getZone().getExtension().handleInternalMessage(CMD.InternalMessage.FIGHT_ARENA_OFFLINE_RESULT, resultData);

        super.onEndGame(endGameData);
    }

    @Override
    public void flee(User user) {
        ISFSObject resultData = new SFSObject();
        resultData.putLong(Params.UID, uid);
        resultData.putLong(Params.ENEMY, enemyID);
        resultData.putBool(Params.WIN, false);
        resultData.putUtfString(Params.BATTLE_ID, "");
        this.getRoom().getZone().getExtension().handleInternalMessage(CMD.InternalMessage.FIGHT_ARENA_OFFLINE_RESULT, resultData);
        super.flee(user);
    }
}
