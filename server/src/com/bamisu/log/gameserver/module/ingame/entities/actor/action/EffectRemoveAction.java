package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.Effect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.SE_Immortal;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.SE_Immunity;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.SE_StatBuff;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.SE_StatDebuff;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 5:16 PM, 5/6/2020
 */
public class EffectRemoveAction extends Action {
    List<Effect> removeList;

    public EffectRemoveAction(ActionID actionID, List<Object> props) {
        super(actionID, props);
    }

    public EffectRemoveAction(List<Effect> removeList) {
        super(ActionID.REMOVED_EFFECT, new ArrayList<>());
        this.removeList = removeList;
    }

    @Override
    public List<ActionResult> run() {
        List<ActionResult> listActionResult = new ArrayList<>();

        //logic remove
        getActor().removeEffect(removeList);
        List<EEffect> listRemovedEffect = new ArrayList<>();
        for (Effect effectRemove : removeList) {
            boolean isRemove = true;
            for (Effect effectCurent : getActor().getEffectList()) {
                if(!effectCurent.isDisplay) continue;
                if (effectRemove.getID().equalsIgnoreCase(effectCurent.getType().getID())) {
                    isRemove = false;
                }
            }

            if (isRemove) {
                if (!listRemovedEffect.contains(effectRemove.getType())) {
                    listRemovedEffect.add(effectRemove.getType());

                    ActionResult actionResultRemoveEffect = new ActionResult();
                    actionResultRemoveEffect.actor = getActor().getActorID();
                    actionResultRemoveEffect.id = getActionID().getIntValue();
                    actionResultRemoveEffect.addProp(effectRemove.getType().getID());
                    if (effectRemove.getType() == EEffect.Stat_Buff) {
                        actionResultRemoveEffect.addProp(((SE_StatBuff) effectRemove).attr.shortName());
                    }
                    if (effectRemove.getType() == EEffect.Stat_Debuff) {
                        actionResultRemoveEffect.addProp(((SE_StatDebuff) effectRemove).attr.shortName());
                    }

                    //hồi máu xau khi gỡ bỏ hiệu ứng miễn nhiễm
                    if (effectRemove.getType() == EEffect.Immortal) {
                        if(((SE_Immortal) effectRemove).health > 0){
                            listActionResult.addAll(getActor().action(new HealthChangeAction(getActor(), (int) (getActor().getMaxHP() * ((SE_Immortal) effectRemove).health / 100))));
                        }
                    }

                    //hồi máu xau khi gỡ bỏ hiệu ứng miễn nhiễm
                    if (effectRemove.getType() == EEffect.Immunity) {
                        if(((SE_Immunity) effectRemove).health > 0){
                            listActionResult.addAll(getActor().action(new HealthChangeAction(getActor(), (int) (getActor().getMaxHP() * ((SE_Immunity) effectRemove).health / 100))));
                        }
                    }
                    listActionResult.add(actionResultRemoveEffect);
                }
            }
        }

        return listActionResult;
    }
}
