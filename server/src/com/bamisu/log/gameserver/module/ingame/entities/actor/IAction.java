package com.bamisu.log.gameserver.module.ingame.entities.actor;

import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;

import java.util.List;

/**
 * Create by Popeye on 10:25 AM, 2/28/2020
 */
public interface IAction {
    List<ActionResult> run();
}
