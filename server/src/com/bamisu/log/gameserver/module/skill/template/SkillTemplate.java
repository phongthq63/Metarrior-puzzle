package com.bamisu.log.gameserver.module.skill.template;

import com.bamisu.log.gameserver.module.ingame.entities.MatchState;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.log.gameserver.module.skill.template.active.ActiveSkillTemplate;
import com.bamisu.log.gameserver.module.skill.template.passive.PassiveSkillTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:21 PM, 3/24/2020
 */
public abstract class SkillTemplate {
    public int diamondDamageRateBonus = 0;
    public static SkillTemplate getTemplate(int id){
        switch (id){
            case 0:
                return new ActiveSkillTemplate();
            case 100:
                return new PassiveSkillTemplate();
        }

        return null;
    }
    List<Character> customTargert = new ArrayList<>();
    boolean canMiss = true;

    public boolean isCanMiss() {
        return canMiss;
    }

    public SkillTemplate setDiamondDamageRateBonus(int rate){
        this.diamondDamageRateBonus = rate;
        return this;
    }

    public SkillTemplate setCanMiss(boolean canMiss) {
        this.canMiss = canMiss;
        return this;
    }

    public List<Character> getCustomTargert() {
        return customTargert;
    }

    public SkillTemplate setCustomTargert(List<Character> customTargert) {
        this.customTargert = customTargert;
        return this;
    }

    public abstract List<ActionResult> doSkill(MatchState state, Skill skill, Character actor, boolean rootSkill, boolean isPassive, boolean isCrit);
}
