package com.bamisu.log.gameserver.module.ingame.entities.actor.action_result;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 5:42 PM, 2/27/2020
 */
public class ActionResult {
    public String actor;
    public int id;
    public List<Object> props = new ArrayList<>();
    public List<ActionResult> actions = new ArrayList<>();

    public void pushAction(ActionResult action) {
        actions.add(action);
    }

    public void pushAction(List<ActionResult> action) {
        actions.addAll(action);
    }

    public ActionResult addProp(Object prop){
        props.add(prop);
        return this;
    }
}
