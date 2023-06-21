package com.bamisu.log.gameserver.module.ingame.cmd.send;

import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 10:39 AM, 5/15/2020
 */
public class SendSageSkill extends BaseMsg {
    public List<ActionResult> actions = new ArrayList<>();
    public int currentTurn;
    public int target;

    public SendSageSkill() {
        super(CMD.CMD_SAGE_SKILL);
    }

    public SendSageSkill(short errorCode) {
        super(CMD.CMD_SAGE_SKILL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putInt(Params.CURRENT_TURN, currentTurn);
        data.putInt(Params.TARGET, target);
        data.putSFSArray(Params.ACTION, SFSArray.newFromJsonData(Utils.toJson(actions)));
//        //System.out.println("SendSageUltimate: \n"
//                + data.toJson()
//                + "\n==========");
    }

    public void pushAction(List<ActionResult> actions) {
        this.actions.addAll(actions);
    }

    public void pushEndGameData(ISFSObject endGameData){
        data.putSFSObject(Params.END_GAME_DATA, endGameData);
    }
}
