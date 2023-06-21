package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.gamelib.entities.Attr;
import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.skill.SkillUtils;
import com.bamisu.log.gameserver.module.skill.template.entities.Heals;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 9:36 AM, 6/29/2020
 */
public class ReturnAction extends Action {
    Character sourceActor;
    Heals heals;

    public ReturnAction(ActionID actionID, List<Object> props) {
        super(actionID, props);
    }

    public ReturnAction(Character sourceActor, Heals heals) {
        super(ActionID.RETURN, new ArrayList<>());
        this.sourceActor = sourceActor;
        this.heals = heals;
    }

    @Override
    public List<ActionResult> run() {
        List<ActionResult> results = new ArrayList<>();
        if(!getActor().isLive()){   //nếu đã chết
            float hpRate = Float.valueOf(String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(heals.hpRate, sourceActor, null))));
            float hpMaxRate = Float.valueOf(String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(heals.hpMaxRate, sourceActor, null))));
            if(hpRate > hpMaxRate) hpRate = hpMaxRate;
            getActor().setCurrentHP((int) (getActor().getMaxHP() * hpRate / 100));

            ActionResult actionResultReturn = new ActionResult();
            actionResultReturn.actor = getActor().getActorID();
            actionResultReturn.id = getActionID().getIntValue();
            actionResultReturn.addProp(getActor().getCurrentHP());
            results.add(actionResultReturn);

            //buff chỉ số khi được hồi sinh trong endless night
            if(getActor().getMaster().getFightingManager().function == EFightingFunction.ENDLESS_NIGHT && getActor().getType() == ECharacterType.Creep){
                getActor().action(new EffectApplyAction(
                        getActor(),
                        EEffect.Stat_Buff,
                        999,
                        Arrays.asList(
                                Attr.ATTACK.shortName(),
                                20,
                                20
                        ),
                        false));

                getActor().action(new EffectApplyAction(
                        getActor(),
                        EEffect.Stat_Buff,
                        999,
                        Arrays.asList(
                                Attr.DEFENSE.shortName(),
                                20,
                                20
                        ),
                        false));
            }
        }

        return results;
    }
}
