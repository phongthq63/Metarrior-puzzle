package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 9:21 AM, 12/9/2020
 */
public class ShieldChangeAction extends Action {
    int value;
    String sourceActorID;
//    boolean outTime = false; //thay đổi về 0 khi hết thời gian 2 turn

    public ShieldChangeAction(ActionID actionID, List<Object> props) {
        super(actionID, props);
    }

    public ShieldChangeAction(int value, String sourceActorID) {
        super(ActionID.SHIELD_CHANGE, new ArrayList<>());
        this.value = value;
        this.sourceActorID = sourceActorID;
    }

    public ShieldChangeAction(int value, boolean outTime) {
        super(ActionID.SHIELD_CHANGE, new ArrayList<>());
        this.value = value;
//        this.outTime = outTime;
    }

    @Override
    public List<ActionResult> run() {
        List<ActionResult> results = new ArrayList<>();
        ActionResult actionShieldChange = new ActionResult();
        actionShieldChange.actor = getActor().getActorID();
        actionShieldChange.id = ActionID.SHIELD_CHANGE.getIntValue();
//        if(outTime){
//            actionShieldChange.addProp(0);
//            getActor().setShieldAll(0);
//        }else {
        actionShieldChange.addProp(value);
        actionShieldChange.addProp(sourceActorID);
        if (value > 0) {    //được buff hiệu ứng mới

            //hiệu ứng giảm giáp ảo
            if (getActor().haveEffect(EEffect.Soulburn)) {
                value = value * 50 / 100;
                if (value < 1) value = 1;
            }
            //

            getActor().setShieldAll(value);
            getActor().setShieldAllLastTurn(getActor().getMaster().getFightingManager().turnCount);
        } else if (value == 0) {  //hết turn
            getActor().setShieldAll(0);
        } else {    //bị trừ
            getActor().setShieldAll(getActor().getShieldAll() + value);
        }

        if (getActor().getShieldAll() == 0) {
            getActor().setShieldAllLastTurn(-1);
        }
//        }
        results.add(actionShieldChange);
        return results;
    }
}
