package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;

import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 10:28 AM, 3/2/2020
 */
public class MissAction extends Action {
    public MissAction(ActionID actionID, List<Object> props) {
        super(actionID, props);
    }

    public MissAction(List<Object> props) {
        super(ActionID.MISS, props);
    }

    @Override
    public List<ActionResult> run() {
        ActionResult actionResult = new ActionResult();
        actionResult.actor = getActor().getActorID();
        actionResult.id = ActionID.MISS.getIntValue();
        return Arrays.asList(actionResult);
    }
}
