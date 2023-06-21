package com.bamisu.log.gameserver.module.ingame.cmd.send;

import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 2:45 PM, 5/16/2020
 */
public class SendHeroSkill extends BaseMsg {
    public List<ActionResult> actions = new ArrayList<>();
    public int currentTurn;
    public int target;
    public FightingManager fightingManager;

    public boolean error = false;
    public String errorInfo = "";

    public SendHeroSkill() {
        super(CMD.CMD_HERO_SKILL);
    }

    public SendHeroSkill(short errorCode) {
        super(CMD.CMD_HERO_SKILL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;
        data.putBool(Params.ERROR, error);
        if(error){
            data.putUtfString(Params.ERROR_INFO, errorInfo);
            return;
        }
        data.putInt(Params.CURRENT_TURN, currentTurn);
        data.putInt(Params.TARGET, target);
        data.putSFSArray(Params.ACTION, SFSArray.newFromJsonData(Utils.toJson(actions)));
        //test in ra toan bo mau cua character
        //System.out.println("");
        //System.out.println("==============");
        for(BasePlayer player : fightingManager.players){
            //System.out.println("player " + player.getPlayerID());
            for(TeamSlot teamSlot : player.team){
                if(teamSlot.haveCharacter()){
                    //System.out.println("actor " + teamSlot.getCharacter().getActorID() + ": " + teamSlot.getCharacter().getCurrentHP());
                }
            }
        }
        //System.out.println("==============");
        //System.out.println("");
        //System.out.println("SendHeroSkill: " + data.toJson());
    }

    public void pushAction(List<ActionResult> actions) {
        this.actions.addAll(actions);
    }

    public void pushEndGameData(ISFSObject endGameData){
        data.putSFSObject(Params.END_GAME_DATA, endGameData);
    }
}
