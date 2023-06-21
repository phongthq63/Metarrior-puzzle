package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.skill.Damage;
import com.bamisu.log.gameserver.module.ingame.entities.skill.DamagePackge;
import com.bamisu.log.gameserver.module.skill.template.entities.SkillMakeSEDesc;
import com.bamisu.log.gameserver.module.skill.template.entities.SuddenDie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 5:45 PM, 6/23/2020
 */
public class TankAction extends Action {
    Character target;
    DamagePackge D1;
    Character sourceActor;
    Damage totalDame;
    Damage totalDameMana;
    List<SkillMakeSEDesc> se = null;
    SuddenDie suddenDie = null;

    public TankAction(ActionID actionID, List<Object> props) {
        super(actionID, props);
    }

    public TankAction(Character sourceActor, Character target, DamagePackge D1, Damage totalDame, Damage totalDameMana) {
        super(ActionID.MISS, new ArrayList<>());
        this.target = target;
        this.D1 = D1;
        this.sourceActor = sourceActor;
        this.totalDame = totalDame;
        this.totalDameMana = totalDameMana;
    }

    @Override
    public List<ActionResult> run() {
        List<ActionResult> results = new ArrayList<>();

        ActionResult tankActionResult = new ActionResult();
        tankActionResult.actor = getActor().getActorID();
        tankActionResult.id = ActionID.TANK.getIntValue();
        tankActionResult.addProp(target.getActorID());
        results.add(tankActionResult);

        tankActionResult.pushAction(getActor().action(new BeatenAction(sourceActor, D1, null, se, null, suddenDie, this.totalDame, this.totalDameMana,false, true, false)));
        return results;
    }
}
