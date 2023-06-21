package com.bamisu.log.gameserver.module.ingame.entities.actor;

import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 5:43 PM, 2/27/2020
 */
public class Action implements IAction{
    ActionID actionID;
    List<Object> props;
    Character actor;

//    public static Action create(ActionID actionID, List<Object> props){
//        switch (actionID){
//            case SKILLING:
//                return new SkillingAction(actionID, props);
//        }
//
//        return null;
//    }

    public Action(ActionID actionID, List<Object> props) {
        this.actionID = actionID;
        this.props = props;
    }

    public ActionID getActionID() {
        return actionID;
    }

    public void setActionID(ActionID actionID) {
        this.actionID = actionID;
    }

    public List<Object> getProps() {
        return props;
    }

    public Object getProp(int index) {
        return props.get(index);
    }

    public void setProps(List<Object> props) {
        this.props = props;
    }

    public Character getActor() {
        return actor;
    }

    public void setActor(Character actor) {
        this.actor = actor;
    }

    @Override
    public List<ActionResult> run() {
        return new ArrayList<>();
    }
}
