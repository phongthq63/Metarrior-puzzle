package com.bamisu.log.gameserver.module.ingame.cmd.send;

import com.bamisu.log.gameserver.module.ingame.Node;
import com.bamisu.log.gameserver.module.ingame.cmd.TableConverter;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 9:36 AM, 5/21/2020
 */
public class MovePackage {
    ISFSObject data = new SFSObject();

    public List<ActionResult> actions = new ArrayList<>();
    public List<ActionResult> beforeNextTurn = new ArrayList<>();
    public int currentTurn;
    public int target;
    public int nextTurn;
    public int turnCount;

    public MovePackage packData() {
        data.putInt(Params.TURN_COUNT, turnCount);
        data.putInt(Params.CURRENT_TURN, currentTurn);
        data.putInt(Params.TARGET, target);
        data.putInt(Params.NEXT_TURN, nextTurn);

        data.putSFSArray(Params.ACTION, SFSArray.newFromJsonData(Utils.toJson(actions)));
        data.putSFSArray(Params.BEFORE_NEXT_TURN, SFSArray.newFromJsonData(Utils.toJson(beforeNextTurn)));
        return this;
    }

    public void pushAction(List<ActionResult> actions) {
        this.actions.addAll(actions);
    }

    public void pushBefoNextTurn(List<ActionResult> actionResults) {
        this.beforeNextTurn.addAll(actionResults);
    }

    public void pushEndGameData(ISFSObject endGameData){
        data.putSFSObject(Params.END_GAME_DATA, endGameData);
    }
}
