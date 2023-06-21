package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;

import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 10:29 AM, 3/2/2020
 */
public class DodgeAction extends Action {
    public DodgeAction(ActionID actionID, List<Object> props) {
        super(actionID, props);
    }

    public DodgeAction(List<Object> props) {
        super(ActionID.DODGE, props);
    }

    @Override
    public List<ActionResult> run() {
        ActionResult actionResult = new ActionResult();
        actionResult.actor = getActor().getActorID();
        actionResult.id = ActionID.DODGE.getIntValue();
        return Arrays.asList(actionResult);
    }
}
