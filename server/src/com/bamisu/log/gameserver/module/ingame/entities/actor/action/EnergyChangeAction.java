package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.actor.IAction;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 7:38 PM, 5/4/2020
 */

/**
 * 0: mana thay đổi
 */
public class EnergyChangeAction extends Action implements IAction {
    int value;
    boolean sendToClient;

    public EnergyChangeAction(ActionID actionID, List<Object> props) {
        super(actionID, props);
    }

    public EnergyChangeAction(List<Object> props) {
        super(ActionID.ENERGY_CHANGE, props);
    }

    public EnergyChangeAction(int value) {
        this(value, true);
    }

    public EnergyChangeAction(int value, boolean sendToClient) {
        super(ActionID.ENERGY_CHANGE, new ArrayList<>());
        this.value = value;
        this.sendToClient = sendToClient;
    }

    @Override
    public List<ActionResult> run() {
        List<ActionResult> results = new ArrayList<>();
        ActionResult actionEnergyChange = new ActionResult();
        actionEnergyChange.actor = getActor().getActorID();
        actionEnergyChange.id = ActionID.ENERGY_CHANGE.getIntValue();

        //thay đổi mana
//        getActor().setCurrentEP(getActor().getCurrentEP() + value);
        int cacheValue = value;
        if (value + getActor().getCurrentEP() > 100){ //cộng thừa máu
            value = 100 - getActor().getCurrentEP();
        }

        if (value + getActor().getCurrentEP() < 0){ //cộng thừa máu
            cacheValue = value;
            value = 0 - getActor().getCurrentEP();
        }

//        String log = "";
//        log += getActor().getActorID() + "," + String.valueOf(this.sendToClient);
//        log += "," + value + "," + getActor().getCurrentEP();
        getActor().setCurrentEP(getActor().getCurrentEP() + value);
//        log += "," + getActor().getCurrentEP();

//        Logger.getLogger("catch").info(log);
        actionEnergyChange.addProp(cacheValue);

        if(sendToClient){
            results.add(actionEnergyChange);
        }
        return results;
    }
}
