package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.log.gameserver.module.ingame.entities.MatchState;
import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.actor.IAction;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.gamelib.utils.business.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 10:24 AM, 2/28/2020
 */
public class SkillingAction extends Action implements IAction {
    Skill skill;
    boolean rootSkill;
    MatchState state;
    int diamondDamageRateBonus; //damage rate
    boolean isPassive;
    boolean isCrit = false;

    public SkillingAction(MatchState state, Skill skill, int rate, boolean rootSkill, boolean isPassive, boolean isCrit) {
        super(ActionID.SKILLING, new ArrayList<>());
        this.skill = skill;
        this.rootSkill = rootSkill;
        this.state = state;
        this.diamondDamageRateBonus = rate;
        this.isPassive = isPassive;
        this.isCrit = isCrit;
    }

    @Override
    public List<ActionResult> run() {
//        Debug.trace("RUN SKILL: " + skill.getIndex());
        return this.skill.doSkill(this.state, getActor(), this.diamondDamageRateBonus, this.rootSkill, this.isPassive, this.isCrit);
    }
}
