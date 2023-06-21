package com.bamisu.log.gameserver.module.ingame.entities.actor;

import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;

import java.util.List;

/**
 * Create by Popeye on 5:41 PM, 2/27/2020
 */
public interface IngameActor {
    String getActorID();
    List<ActionResult> action(Action action);
}
