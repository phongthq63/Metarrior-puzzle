package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.Effect;
import com.bamisu.log.gameserver.module.ingame.entities.player.EPlayerType;
import com.bamisu.log.gameserver.module.ingame.entities.skill.Damage;
import com.bamisu.log.gameserver.module.ingame.entities.skill.DamagePackge;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.log.gameserver.module.skill.SkillUtils;
import com.bamisu.log.gameserver.module.skill.template.entities.SkillMakeSEDesc;
import com.bamisu.log.gameserver.module.skill.template.entities.SuddenDie;
import com.bamisu.log.gameserver.module.skill.template.passive.PassiveSkillTemplateProps;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 10:30 AM, 3/2/2020
 */

/**
 * props:
 * 0:
 * 1:
 * 2: List<SkillMakeSEDesc> skillMakeSEDescList (json string)
 */
public class BeatenAction extends Action {
    public SuddenDie suddenDie;
    public Character sourceActor;
    public Damage totalDame;
    public Damage totalDameMana;
    public boolean canCounters;
    public DamagePackge D1;
    DamagePackge damageMana;
    public List<SkillMakeSEDesc> se;
    public List<SkillMakeSEDesc> sePassive;
    public boolean isTank;  //có phải tank hộ không
    public boolean crit;

    public BeatenAction(ActionID actionID, List<Object> props) {
        super(actionID, props);
    }

    public BeatenAction(Character sourceActor, DamagePackge D1, DamagePackge damageMana, List<SkillMakeSEDesc> se, List<SkillMakeSEDesc> sePassive, SuddenDie suddenDie, Damage totalDame, Damage totalDameMana, boolean canCounters, boolean isTank, boolean crit) {
        super(ActionID.BEATEN, new ArrayList<>());
        this.sourceActor = sourceActor;
        this.suddenDie = suddenDie;
        this.totalDame = totalDame;
        this.totalDameMana = totalDameMana;
        this.canCounters = canCounters;
        this.D1 = D1;
        this.damageMana = damageMana;
        this.se = se;
        this.sePassive = sePassive;
        this.isTank = isTank;
        this.crit = crit;
    }

    @Override
    public List<ActionResult> run() {
        List<ActionResult> results = new ArrayList<>();
        ActionResult actionResultBeaten = new ActionResult();
        actionResultBeaten.actor = getActor().getActorID();
        actionResultBeaten.id = ActionID.BEATEN.getIntValue();
        if (!isTank) {
            results.add(actionResultBeaten);
        }

        if (D1.haveDamage()) {
            //D2
            int D2 = SkillUtils.calculateD2(D1, sourceActor, getActor());

            //nếu đánh chết ngay
            if (canCounters) {
                if (suddenDie != null) {
                    if (suddenDie.can(this.sourceActor, getActor())) {
                        D2 = getActor().getCurrentHP();
                    }
                }
            }

            totalDame.addDamage(D2);

            //action trừ máu
            results.addAll(getActor().action(new HealthChangeAction(sourceActor, D1.total(), -D2, crit)));
        }

        if (getActor().isLive()) {
            //remove sleep effect
            List<Effect> sleepEffects = new ArrayList<>();
            for (Effect effect : getActor().getEffectList()) {
                if (effect.getType() == EEffect.Sleep) {
                    sleepEffects.add(effect);
                }
            }
            if (!sleepEffects.isEmpty()) {
                results.addAll(getActor().action(new EffectRemoveAction(sleepEffects)));
            }

            if (sePassive != null) {
                for (SkillMakeSEDesc linkedHashMap : sePassive) {
                    SkillMakeSEDesc skillMakeSEDesc = Utils.fromJson(Utils.toJson(linkedHashMap), SkillMakeSEDesc.class);
                    if (skillMakeSEDesc.canMake(sourceActor)) {
                        results.addAll(getActor().action(new EffectApplyAction(sourceActor, EEffect.fromName(skillMakeSEDesc.readSEName()), skillMakeSEDesc.turn, skillMakeSEDesc.props, true)));
                    }
                }
            }

            //SE
            if (se != null) {
                for (SkillMakeSEDesc linkedHashMap : se) {
                    SkillMakeSEDesc skillMakeSEDesc = Utils.fromJson(Utils.toJson(linkedHashMap), SkillMakeSEDesc.class);
                    if (skillMakeSEDesc.canMake(sourceActor)) {
                        results.addAll(getActor().action(new EffectApplyAction(sourceActor, EEffect.fromName(skillMakeSEDesc.readSEName()), skillMakeSEDesc.turn, skillMakeSEDesc.props, true)));
                    }
                }
            }

            //action tăng mana
            if (!isTank && D1.total() > 0) {
                results.addAll(getActor().action(new EnergyChangeAction(15)));
            }

            if (damageMana != null && damageMana.haveDamage()) {
                if (damageMana.total() != 0) {
                    results.addAll(getActor().action(new EnergyChangeAction(damageMana.total())));
                    totalDameMana.addDamage(Math.abs(damageMana.total()));
                }
            }
        }
        return results;
    }
}
