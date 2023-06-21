package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.Effect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.SE_Immortal;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.SE_Immunity;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.log.gameserver.module.skill.template.entities.SkillMakeSEDesc;
import com.bamisu.log.gameserver.module.skill.template.passive.PassiveSkillTemplateProps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 3:50 PM, 3/5/2020
 */
public class HealthChangeAction extends Action {
    int value;
    boolean crit;
    Character sourceCharacter;
    int D1 = -1;

    public HealthChangeAction(ActionID actionID, List<Object> props) {
        super(actionID, props);
    }

    public HealthChangeAction(List<Object> props) {
        super(ActionID.DIE, props);
    }

    public HealthChangeAction(Character sourceActor, int value) {
        super(ActionID.DIE, new ArrayList<>());
        this.value = value;
        this.sourceCharacter = sourceActor;
    }

    public HealthChangeAction(Character sourceActor, int D1, int value, boolean crit) {
        super(ActionID.DIE, new ArrayList<>());
        this.value = value;
        this.D1 = D1;
        this.crit = crit;
        this.sourceCharacter = sourceActor;
    }

    @Override
    public List<ActionResult> run() {
        List<ActionResult> results = new ArrayList<>();
        ActionResult actionHealthChange = new ActionResult();
        actionHealthChange.actor = getActor().getActorID();
        actionHealthChange.id = ActionID.HEALTH_CHANGE.getIntValue();
        boolean isInc = true;

        //giảm hồi máu
        if (value > 0) {
            if (getActor().haveEffect(EEffect.Soulburn)) {
                value = value * 50 / 100;
                if (value < 1) value = 1;
            }
        }

        //hiếu ứng miễn nhiễm sát thương
        if (value < 0) {
            isInc = false;
            if (!getActor().canReductionHealth()) {
                value = 0;
            } else {
                //hồi máu cho người buff khiên
                for (Effect effect : getActor().getEffectList()) {
                    if (effect.getType() == EEffect.Immunity || effect.getType() == EEffect.Immortal) {
                        Character sourceEffectActor = effect.getSourceActor();
                        if (sourceEffectActor.isLive()) {
                            float healthForSourceActor = 0;
                            if (effect.getType() == EEffect.Immunity) {
                                healthForSourceActor = ((SE_Immunity) effect).healthForSourceActor;

                            }
                            if (effect.getType() == EEffect.Immortal) {
                                healthForSourceActor = ((SE_Immortal) effect).healthForSourceActor;
                            }

                            if (healthForSourceActor > 0) {
                                results.addAll(sourceEffectActor.action(new HealthChangeAction(getActor(), (int) (Math.abs(value) * healthForSourceActor / 100))));
                            }
                        }
                    }
                }
            }
        }

        //thay đổi máu
        if (value + getActor().getCurrentHP() > getActor().getMaxHP()) { //cộng thừa máu
            value = getActor().getMaxHP() - getActor().getCurrentHP();
        }

        int shieldChange = 0;
        int hpChange = 0;

        //còn giáp
        if (value < 0 && getActor().getShieldAll() > 0) {
            if (getActor().getShieldAll() + value >= 0) { //sát thương vừa hết giáp
                shieldChange = value;
            }

            if (getActor().getShieldAll() + value < 0) {  //sát thương lớn hơn giáp
                shieldChange = -getActor().getShieldAll();
                hpChange = getActor().getShieldAll() + value;
            }

            results.addAll(getActor().action(new ShieldChangeAction(shieldChange, sourceCharacter.getActorID())));
        } else {
            hpChange = value;
        }

        //là boss đánh ko chết
        if ((getActor().getMaster().getFightingManager().function == EFightingFunction.DARK_REALM || getActor().getMaster().getFightingManager().function == EFightingFunction.ENDLESS_NIGHT)
                && getActor().getType() == ECharacterType.MiniBoss) {
            //TODO: do nothing
        } else {
            if (hpChange + getActor().getCurrentHP() < 0) { //trừ âm máu
                hpChange = 0 - getActor().getCurrentHP();
            }
            getActor().setCurrentHP(getActor().getCurrentHP() + hpChange);
        }

        actionHealthChange.addProp(hpChange);
        actionHealthChange.addProp(crit);
        if (hpChange > 0) {
            actionHealthChange.addProp(true);
        }
        if (hpChange < 0) {
            actionHealthChange.addProp(false);
        }
        if (hpChange == 0) {
            actionHealthChange.addProp(isInc);
        }
        results.add(actionHealthChange);

        //check die
        if (getActor().getCurrentHP() <= 0) {
            //nội tại làm gì đó khi máu <= 0
            Skill passiveSkill = getActor().getSkill(0);
            PassiveSkillTemplateProps props = passiveSkill.getTamplatePropsAsPassive();
            if (props.immortal != null) {
                if (props.immortal.canActive(getActor().getOnDiePassiveCount())) {   //vẫn còn số lần kích hoạt
                    results.addAll(getActor().action(new HealthChangeAction(getActor(), 1)));
                    for (SkillMakeSEDesc tmpSE : props.immortal.se) {
                        results.addAll(getActor().action(new EffectApplyAction(getActor(), EEffect.fromName(tmpSE.SEName), 2, tmpSE.props, true)));
                    }
                    getActor().pushOnDiePassiveCount(); //tăng số lần kích hoạt lên
                }
            }
            //

            if (getActor().getCurrentHP() <= 0) {
                results.addAll(getActor().action(new DieAction(Arrays.asList())));
                getActor().getActorStatistical().pushDieCount(1);
            }
        } else {
            //hoi mau moi lan bi danh
            if (hpChange < 0) {
                if (getActor().getType() == ECharacterType.Hero || getActor().getType() == ECharacterType.Boss || getActor().getType() == ECharacterType.MiniBoss) {
                    if (getActor().isLive()) {
                        Skill passiveSkill = getActor().getSkill(0);
                        PassiveSkillTemplateProps props = passiveSkill.getTamplatePropsAsPassive();
                        if (props.healsOnDamageOn != null) {
                            int heals = props.healsOnDamageOn.findHealValue(getActor(), getActor(), 0, 0);
                            if (heals > Math.abs(hpChange)) {
                                heals = Math.abs(hpChange);
                            }
                            results.addAll(getActor().action(new HealthChangeAction(getActor(), heals)));
                        }
                    }
                }
            }
        }

        //thống kê
        if (value < 0) {
            if (D1 != -1) {
                getActor().getActorStatistical().pushDamageTaken(Math.abs(D1));
            } else {
                getActor().getActorStatistical().pushDamageTaken(Math.abs(value));
            }
            sourceCharacter.getActorStatistical().pushDamage(Math.abs(value));
            getActor().getActorStatistical().pushLostBlood(Math.abs(value));
            if (getActor().getCharacterVO().readID().equalsIgnoreCase("MBS1000") || getActor().getCharacterVO().readID().equalsIgnoreCase("MBS1003")) {
//                System.out.println("====");
//                System.out.println(value);
//                System.out.println("total: " + getActor().getActorStatistical().lostBlood);
//                System.out.println("====");
            }
        } else {
            if (sourceCharacter != null) {
                sourceCharacter.getActorStatistical().pushHealing(Math.abs(value));
            }
        }

        return results;
    }
}
