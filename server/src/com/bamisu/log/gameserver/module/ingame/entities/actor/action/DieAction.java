package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 4:43 PM, 5/8/2020
 */
public class DieAction extends Action {
    public DieAction(ActionID actionID, List<Object> props) {
        super(actionID, props);
    }

    public DieAction(List<Object> props) {
        super(ActionID.HEALTH_CHANGE, props);
    }

    @Override
    public List<ActionResult> run() {
        List<ActionResult> actionResults = new ArrayList<>();
        ActionResult actionDieResult = new ActionResult();
        actionDieResult.actor = getActor().getActorID();
        actionDieResult.id = ActionID.DIE.getIntValue();
        actionResults.add(actionDieResult);

        //clear SE
        getActor().clearAllDisplayEffect();

        return actionResults;
    }
}
