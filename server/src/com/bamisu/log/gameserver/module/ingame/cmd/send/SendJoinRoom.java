package com.bamisu.log.gameserver.module.ingame.cmd.send;

import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.ingame.UtilsIngame;
import com.bamisu.log.gameserver.module.ingame.cmd.TableConverter;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.EPlayerType;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by Popeye on 10:00 AM, 2/10/2020
 */
public class SendJoinRoom extends BaseMsg {
    public int yourID;
    public int currentTurn;

    public FightingManager manager;
    public List<ActionResult> startFightingActions = new ArrayList<>();
    public int limitTime = -1;
    public long targetPont = -1;
    public Collection<String> willFight;  //báo trước enemy sẽ đánh

    public SendJoinRoom() {
        super(CMD.CMD_FIGHTING_JOIN_ROOM);
    }

    public SendJoinRoom(short errorCode) {
        super(CMD.CMD_FIGHTING_JOIN_ROOM, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        //System.out.println(data.toJson());
        if(isError()) return;
    }

    public void packStartFightingContext(){
        data.putSFSArray(Params.TABLE, TableConverter.tableToSFSArray(manager.puzzleTable));
        data.putIntArray(Params.PUZZLE, TableConverter.tableToIntArray(manager.puzzleTable));
        data.putInt(Params.FIGHTING_TYPE, manager.type.getType());
        data.putInt(Params.YOUR_PLAYER_ID, yourID);
        data.putInt(Params.CURRENT_TURN, currentTurn);
        data.putUtfString(Params.MAP_BONUS, manager.mapBonus.getId());
        data.putUtfString(Params.BATTLE_ID, manager.battleID);
        if(manager.bg != null){
            data.putUtfString(Params.BACKGROUND, manager.bg);
        }
        if(willFight != null){  //mission khong co
            data.putUtfStringArray(Params.WILL_FIGHT, willFight);
        }

        ISFSArray playerArr = new SFSArray();
        for(BasePlayer player : manager.players){
            SFSObject sfsPlayer = new SFSObject();
            sfsPlayer.putInt(Params.TEAM_BONUS, TeamUtils.getTeamBonus(player.getListKingdom()));

            //enemy
            SFSArray sfsArrayTeam = new SFSArray();
            for(TeamSlot teamSlot : player.team){
                SFSObject sfsSlot = new SFSObject();
                UtilsIngame.putSlotData(sfsSlot, teamSlot);
                sfsArrayTeam.addSFSObject(sfsSlot);
            }
            sfsPlayer.putSFSArray(Params.TEAM, sfsArrayTeam);

            //sage
            if(player.sage != null){
                UtilsIngame.putSage(sfsPlayer, player.sage);
            }

            //Turn off celestial
//            if(player.celestial != null){
//                UtilsIngame.putCelestial(sfsPlayer, player.celestial);
//            }

            playerArr.addSFSObject(sfsPlayer);
        }
        data.putSFSArray(Params.PLAYER, playerArr);

        if(limitTime != - 1){
            data.putInt(Params.TIME, limitTime);
        }

        if(targetPont != - 1){
            data.putLong(Params.TARGET_POINT, targetPont);
        }
    }

    public void packStartFightingActions(){
        data.putSFSArray(Params.ACTION, SFSArray.newFromJsonData(Utils.toJson(startFightingActions)));
    }
}
