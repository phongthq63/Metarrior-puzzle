package com.bamisu.log.gameserver.module.skill.template.active;

/**
 * Create by Popeye on 3:39 PM, 3/24/2020
 */

import com.bamisu.gamelib.skill.passive.Statbuff;
import com.bamisu.log.gameserver.module.ingame.entities.MatchState;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action.*;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.SkillingActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.CampaignFightManager;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.EPlayerType;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.log.gameserver.module.ingame.entities.skill.Damage;
import com.bamisu.log.gameserver.module.ingame.entities.skill.DamagePackge;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.log.gameserver.module.skill.SkillType;
import com.bamisu.log.gameserver.module.skill.SkillUtils;
import com.bamisu.log.gameserver.module.skill.DamageType;
import com.bamisu.log.gameserver.module.skill.template.SkillTemplate;
import com.bamisu.log.gameserver.module.skill.template.entities.EHealsType;
import com.bamisu.log.gameserver.module.skill.template.entities.Heals;
import com.bamisu.log.gameserver.module.skill.template.entities.SkillMakeSEDesc;
import com.bamisu.log.gameserver.module.skill.template.passive.PassiveSkillTemplateProps;
import com.bamisu.gamelib.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gây [x0]% sát thương [x1] cho [x2] mục tiêu, có cơ hội gây hiệu ứng [x3]
 * props:
 * damage [
 * <p>
 * ]
 * 1: x1: DamageType (PHYSICAL(0),MAGIC(1),STANDARD(2))
 * 2: x2: random (là số > 0), back (-1), front (-2)
 * 3: x3: Mô tả việc gây hiệu ứng:
 * - {"se": "nothing"}
 * - {"se": "stun", "rate": "(15 + 0.05 * ARM)", "maxRate": 30}
 * <p>
 * <p>
 * "damage":[{"damageRate": 175, "damageType": "physical"}],
 * "target": "1",
 * "suddenDie": {"percentHP": "(20 + 0.0005 * MR)", "maxpercentHP": 35}    //mô tả việc đánh chết ngay 1 target nếu thỏa mãn điều kiện
 * "se": [{"se": "stun", "rate": "(25 + 0.0005 * ARM)", "maxRate": 40, "turn": 2, "props": []}]
 * "additional": [{"type": "critical", "value": "(10 + 0.02 * ARM)", "maxValue": 20}]
 */
public class ActiveSkillTemplate extends SkillTemplate {
    @Override
    public List<ActionResult> doSkill(MatchState state, Skill skill, Character actor, boolean rootSkill, boolean isPassive, boolean isCrit) {
        ActiveSkillTemplateProps props = skill.getTamplatePropsAsActive();
        List<ActionResult> actionResults = new ArrayList<>();

        //đã chết hết rồi thì k skill nữa
        if (actor.getMaster().getFightingManager().haveDiePlayer()) {
            return actionResults;
        }

        //action skill
        SkillingActionResult skillingActionResult = new SkillingActionResult();
        skillingActionResult.actor = actor.getActorID();
        skillingActionResult.id = ActionID.SKILLING.getIntValue();
        skillingActionResult.skillIndex = getSkillID(actor, skill);

        if (actor.isUtilmate(skill)) {
            skillingActionResult.pushAction(actor.action(new EnergyChangeAction(-100)));
        }

        //miss
        boolean isMiss = false;
        if (isPassive) {
            isMiss = false;
        } else {
            if (isCanMiss()) {
                isMiss = SkillUtils.calculateMiss(actor);
            }
        }

        List<TeamSlot> listTargetTeamSlot = null;
        List<String> listTargetID = null;

        if (isMiss) {
            skillingActionResult.isMiss = true;  //is miss
        } else {
            //tăng chỉ số sau mỗi lần tấn công
            Skill passiveSkill = actor.getSkill(0);
            PassiveSkillTemplateProps passiveSkillTemplateProps = passiveSkill.getTamplatePropsAsPassive();
            if (passiveSkillTemplateProps.incAttrPerAttack != null) {
                for (Statbuff statbuff : passiveSkillTemplateProps.incAttrPerAttack) {
                    actor.action(new EffectApplyAction(
                            actor,
                            EEffect.Stat_Buff,
                            999,
                            Arrays.asList(
                                    statbuff.attr,
                                    actor.calculaRateStatBuff(statbuff),
                                    1000
                            ),
                            false));
                }
            }

            Damage totalDame = new Damage(0);
            Damage totalDameMana = new Damage(0);
            Map<ActionResult, TeamSlot> mapWillCounter = new HashMap<>();
            //skill có tác dụng gây damage
            if (props.damage != null) {
                //xác định mục tiêu
                listTargetTeamSlot = SkillUtils.findTarget(actor, props.target);

                //tutorial luôn đánh choáng ganda
                FightingManager fightingManager = actor.getMaster().getFightingManager();
                if (fightingManager.function == EFightingFunction.CAMPAIGN) {
                    if (fightingManager.getCampaignArea() == 0 &&
                            fightingManager.getCampaignStation() == 2 &&
                            (((CampaignFightManager) fightingManager).tutorialState.contains(-1) && !((CampaignFightManager) fightingManager).tutorialState.contains(6))) {
                        if (actor.getType() == ECharacterType.Creep) {
                            if (fightingManager.turnCount == 1) {
                                if (!listTargetTeamSlot.contains(fightingManager.getPlayer(0).team.get(3))) {  //target k có ganda thì add ganda vao
                                    listTargetTeamSlot.add(fightingManager.getPlayer(0).team.get(0));
                                }

                                //tăng tỉ lệ stun lên 100 %
                                if (props.se == null) {
                                    props.se = new ArrayList<>();
                                }
                                if (props.se.size() > 0) {
                                    props.se.clear();
                                }

                                SkillMakeSEDesc se = new SkillMakeSEDesc();
                                props.se.add(se);
                                se.SEName = "Stun";
                                se.rate = "(100 * 1)";
                                se.maxRate = 100;
                                se.turn = 2;
                                se.props = new ArrayList<>();
                            } else {
                                props.se = new ArrayList<>();
                            }
                        }
                    }
                }

                //muc tieu duoc uu tien chon
                if (!listTargetTeamSlot.isEmpty()) {
//                    if (actor.isUtilmate(skill) || actor.getType() == ECharacterType.Sage || actor.getID().equalsIgnoreCase("T1040")) {    //đánh util
//                    if (actor.getType() == ECharacterType.Sage || actor.getID().equalsIgnoreCase("T1040")) {    //đánh util
                    if (actor.getMaster().target != null) {   //đã chọn target
                        BasePlayer targetPlayer = actor.getMaster().getEnemyPlayer();
                        TeamSlot targetSlot = targetPlayer.getTeamSlotByActorID(actor.getMaster().target);
                        if (targetSlot.haveCharacter() && targetSlot.getCharacter().isLive()) {   //target còn sống
                            if (!listTargetTeamSlot.contains(targetSlot)) { //target k dc đánh vào
                                listTargetTeamSlot.remove(listTargetTeamSlot.size() - 1);
                                listTargetTeamSlot.add(targetSlot);
                            }
                        }
                    }
//                    }
                }

                // không xác định được target
                if (listTargetTeamSlot.isEmpty()) return new ArrayList<>();

                //nếu đang bị hỗn loạn
                if (actor.haveEffect(EEffect.Confused)) {
                    if (Utils.rate(50)) { //đánh bên mình
                        List<TeamSlot> allyList = new ArrayList<>();
                        for (TeamSlot targetSlot : listTargetTeamSlot) {
                            for (TeamSlot allyTeamSlot : actor.getMaster().team) {
                                if (allyTeamSlot.haveCharacter() && allyTeamSlot.getCharacter().isLive() && !allyTeamSlot.getCharacter().equals(actor) && !allyList.contains(allyTeamSlot)) {
                                    allyList.add(allyTeamSlot);
                                    break;
                                }
                            }
                        }
                        listTargetTeamSlot.clear();
                        listTargetTeamSlot.addAll(allyList);
                    }
                }

                //Check SE from Passive
                List<SkillMakeSEDesc> makeSEPerAttack = null;
                if (actor.getType() == ECharacterType.Hero || actor.getType() == ECharacterType.MiniBoss || actor.getType() == ECharacterType.Boss) {
                    Skill skillPassive = actor.getSkill(0);
                    if (skillPassive.getSkillBaseInfo().type == SkillType.PASSIVE.getIntValue()) {
                        PassiveSkillTemplateProps template100Props = skillPassive.getTamplatePropsAsPassive();
                        if (template100Props.makeSEPerAttack != null) {
                            makeSEPerAttack = template100Props.makeSEPerAttack;
                        }
                    }
                }

                //đánh vào các target
                listTargetID = new ArrayList<>();
                Map<Character, TankAction> tankActions = new ConcurrentHashMap<>();

                //có chí mạng không?
                boolean crit;
                if (isCrit) {
                    crit = true;
                } else {
                    //cộng thêm tỉ lệ chí mạng trong skill
                    double additionalCrit = props.caculationCit(actor);
                    crit = Utils.rate(actor.getCRIT() + additionalCrit);
                }

                //đánh vào từng enemy
                for (TeamSlot targetSlot : listTargetTeamSlot) {
                    //đã chết thì thôi k đánh
                    if (!targetSlot.getCharacter().isLive()) {
                        continue;
                    }

                    //tính D1
                    DamagePackge damageD1 = new DamagePackge();
                    DamagePackge damageMana = new DamagePackge();
                    for (DamageDesc damageDesc : props.damage) {
                        if (damageDesc.hpType()) {
                            damageD1.push(SkillUtils.calculateD1(DamageType.fromStrValue(damageDesc.damageType), actor, targetSlot.getCharacter(), damageDesc.damageRate, crit));
                            damageD1.applyRate((100 + diamondDamageRateBonus * 1.0) / 100);
                        }

                        if (damageDesc.manaType()) {
                            damageMana.push(SkillUtils.calculateD1(DamageType.fromStrValue(damageDesc.damageType), actor, targetSlot.getCharacter(), damageDesc.damageRate, false));
                        }
                    }
                    //bị giảm damage out
                    if (!actor.isUtilmate(skill)) {
                        damageD1.deDamage(actor.getDeDamageOutRateWithoutUltilmate());
                    }
                    ////

                    listTargetID.add(targetSlot.getCharacter().getActorID());
                    //khắc chế
                    DamagePackge D1 = SkillUtils.opposition(damageD1, actor, targetSlot.getCharacter());

                    //check né
                    boolean isDodge = SkillUtils.calculateDodge(actor);
                    if (isDodge) { //né được
                        skillingActionResult.pushAction(targetSlot.getCharacter().action(new DodgeAction(Arrays.asList())));
                    } else {
                        //check tank
                        if (!targetSlot.getCharacter().isHaveTankForAlly() && D1.haveDamage()) {
                            List<TeamSlot> canTankList = new ArrayList<>();
                            for (TeamSlot targetTeamSlot : targetSlot.getPlayer().team) {
                                if (targetTeamSlot.haveCharacter() && targetTeamSlot.getCharacter().isLive()) {
                                    if (targetTeamSlot.getCharacter().equals(targetSlot.getCharacter()))
                                        continue;    //không tank cho chính mình
                                    if (targetTeamSlot.getCharacter().canTankForAlly(props)) {
                                        canTankList.add(targetTeamSlot);
                                    }
                                }
                            }

                            Character tanker = null;
                            if (!canTankList.isEmpty()) {
                                //ưu tiên chọn hero ở gần
                                for (TeamSlot tankerSlot : canTankList) {
                                    if (tankerSlot.isNear(targetSlot)) {
                                        tanker = tankerSlot.getCharacter();
                                        break;
                                    }
                                }

                                //ko có tướng tank ở gần
                                if (tanker == null) {
                                    Collections.shuffle(canTankList);
                                    tanker = canTankList.get(0).getCharacter();
                                }
                            }

                            //cache Tank action
                            if (tanker != null) {
                                tanker.setHaveTankForAlly(true);
                                targetSlot.getCharacter().setHaveTankForAlly(true);
                                DamagePackge DTank = D1.share(tanker.getTankRate());
                                tankActions.put(tanker, new TankAction(actor, targetSlot.getCharacter(), DTank, totalDame, totalDameMana));
                            }
                        }

                        boolean canCounter = rootSkill;
                        if (props.damage.isEmpty()) {
                            canCounter = false;
                        }
                        //BeatenAction
                        List<ActionResult> actionResultList = targetSlot.getCharacter().action(new BeatenAction(actor, D1, damageMana, props.se, makeSEPerAttack, props.suddenDie, totalDame, totalDameMana, canCounter, false, crit));
                        skillingActionResult.pushAction(actionResultList);

                        //map will counter (map này chứa tất cả các beaten action và team slot để check có thể phản đòn hay không)
                        if (canCounter) {
                            mapWillCounter.put(actionResultList.get(0), targetSlot);
                        }
                    }
                }

                //tank hộ
                for (Character tanker : tankActions.keySet()) {
                    if (tanker.isLive()) {
                        skillingActionResult.pushAction(tanker.action(tankActions.get(tanker)));
                        tanker.incTankedTime();
                    }
                }

                skillingActionResult.fightTarget = listTargetID;
            }

            //skill có tác dụng heals
            FightingManager fightingManager = actor.getMaster().getFightingManager();
            if (props.heals != null) {
                List<Character> customTarget = new ArrayList<>();
                for (Heals heals : props.heals) {
                    //tutorial
                    if (fightingManager.function == EFightingFunction.CAMPAIGN) {
                        if (fightingManager.getCampaignArea() == 0 && fightingManager.getCampaignStation() == 2
                                && (((CampaignFightManager) fightingManager).tutorialState.contains(-1) && !((CampaignFightManager) fightingManager).tutorialState.contains(6))
                                ) {
                            if (skill.getSkillBaseInfo().id.equalsIgnoreCase("SS0006")) {
                                for (SkillMakeSEDesc skillMakeSEDesc : heals.se) {
                                    skillMakeSEDesc.rate = "(100 * 1)";
                                    skillMakeSEDesc.maxRate = 100;
                                }
                            }
                        }
                    }

                    List<Character> target = SkillUtils.findBuffTarget(heals.target, actor);
                    if (heals.type != null) { //khong hoi phuc ma chi SE
                        EHealsType healsType = heals.findType();
                        switch (healsType) {
                            case HP:
                                for (Character targetCharacter : target) {
                                    int healsValue = heals.findHealValue(actor, targetCharacter, totalDame.getValue(), totalDameMana.getValue());
                                    skillingActionResult.pushAction(targetCharacter.action(new HealthChangeAction(actor, healsValue)));

                                    //heals và kèm hiệu ứng
                                    if (heals.se != null) {
                                        for (SkillMakeSEDesc skillMakeSEDesc : heals.se) {
                                            if (skillMakeSEDesc.canMake(actor)) {
                                                skillingActionResult.pushAction(targetCharacter.action(new EffectApplyAction(actor, EEffect.fromName(skillMakeSEDesc.readSEName()), skillMakeSEDesc.turn, skillMakeSEDesc.props, true)));
                                            }
                                        }
                                    }
                                }
                                break;
                            case EP:
                                for (Character targetCharacter : target) {
                                    int healsValue = heals.findHealValue(actor, targetCharacter, totalDame.getValue(), totalDameMana.getValue());
                                    skillingActionResult.pushAction(targetCharacter.action(new EnergyChangeAction(healsValue)));

                                    //heals và kèm hiệu ứng
                                    if (heals.se != null) {
                                        for (SkillMakeSEDesc skillMakeSEDesc : heals.se) {
                                            if (skillMakeSEDesc.canMake(actor)) {
                                                skillingActionResult.pushAction(targetCharacter.action(new EffectApplyAction(actor, EEffect.fromName(skillMakeSEDesc.readSEName()), skillMakeSEDesc.turn, skillMakeSEDesc.props, true)));
                                            }
                                        }
                                    }
                                }
                                break;
                            case REVIVAL:
                                customTarget = getCustomTargert();
                                List<Character> removeListRevival = new ArrayList<>();
                                for (Character revivalTarget : customTarget) {
                                    if (!revivalTarget.isLive() && heals.useTime > 0) {
                                        skillingActionResult.pushAction(revivalTarget.action(new ReturnAction(actor, heals)));
                                        heals.useTime--;
                                    } else {
                                        removeListRevival.add(revivalTarget);
                                    }
                                }

                                if (!removeListRevival.isEmpty()) {
                                    customTarget.removeAll(removeListRevival);
                                }

                                break;
                            case SHIELD_ALL:
                                for (Character targetCharacter : target) {
                                    int healsValue = heals.findHealValue(actor, targetCharacter, totalDame.getValue(), totalDameMana.getValue());
                                    skillingActionResult.pushAction(targetCharacter.action(new ShieldChangeAction(healsValue, actor.getActorID())));

                                    //heals và kèm hiệu ứng
                                    if (heals.se != null) {
                                        for (SkillMakeSEDesc skillMakeSEDesc : heals.se) {
                                            if (skillMakeSEDesc.canMake(actor)) {
                                                skillingActionResult.pushAction(targetCharacter.action(new EffectApplyAction(actor, EEffect.fromName(skillMakeSEDesc.readSEName()), skillMakeSEDesc.turn, skillMakeSEDesc.props, true)));
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    } else {
                        //make SE from Active
                        if (heals.se != null) {
                            List<Character> buffMissTarget = new ArrayList<>();
                            for (Character targetCharacter : target) {
                                for (SkillMakeSEDesc skillMakeSEDesc : heals.se) {
                                    if (skillMakeSEDesc.canMake(actor)) {
                                        skillingActionResult.pushAction(targetCharacter.action(new EffectApplyAction(actor, EEffect.fromName(skillMakeSEDesc.readSEName()), skillMakeSEDesc.turn, skillMakeSEDesc.props, true)));
                                    } else {
                                        buffMissTarget.add(targetCharacter);
                                    }
                                }
                            }
                            if (!buffMissTarget.isEmpty()) {
                                target.removeAll(buffMissTarget);
                            }
                        }
                    }
                    for (Character character : target) {
                        if (!customTarget.contains(target)) {
                            customTarget.add(character);
                        }
                    }
                }

                for (Character character : customTarget) {
                    if (!skillingActionResult.buffTarget.contains(character.getActorID())) {
                        skillingActionResult.buffTarget.add(character.getActorID());
                    }

                }
            }

            //counter - phản đòn
            for (ActionResult actionResult : mapWillCounter.keySet()) {
                Character character = mapWillCounter.get(actionResult).getCharacter();
                if (character.isLive()) {
                    //phản đòn

                    if (character.getType() == ECharacterType.Hero || character.getType() == ECharacterType.Boss || character.getType() == ECharacterType.MiniBoss) {
                        if (character.canSkillPassive()) {
                            Skill tmpPassiveSkill = character.getSkill(0);
                            PassiveSkillTemplateProps tmpPassiveSkillTemplateProps = tmpPassiveSkill.getTamplatePropsAsPassive();
                            if (tmpPassiveSkillTemplateProps.counterEffect != null) {
                                Skill psSkill = character.getSkill(Integer.parseInt(tmpPassiveSkillTemplateProps.counterEffect.kill) - 1);
                                actionResult.pushAction(character.action(new SkillingAction(null, psSkill, 0, false, true, false)));
                            }
                        }
                    }
                }
            }

            //skill thay thế diamond
            if (props.replateDiamond != null) {
                skillingActionResult.replateDiamond = props.replateDiamond;
            }

            skillingActionResult.isMiss = false; //is miss
        }

        actionResults.add(skillingActionResult.packData());
        if (actor.isUtilmate(skill)) {
//            skillingActionResult.pushAction(actor.action(new EnergyChangeAction(-100)));
        } else {
            if (!isPassive) {
                if (actor.getType() != ECharacterType.Sage && actor.getType() != ECharacterType.Celestial) {
                    if (actor.getMaster().havePuzzleBoard()) {
                        if (actor.getType() == ECharacterType.Creep) {
                            skillingActionResult.pushAction(actor.action(new EnergyChangeAction(30)));
                        } else {
                            if (skill.getIndex() == 1) {
                                skillingActionResult.pushAction(actor.action(new EnergyChangeAction(10)));
                            }

                            if (skill.getIndex() == 2) {
                                skillingActionResult.pushAction(actor.action(new EnergyChangeAction(30)));
                            }
                        }
                    } else {
                        if (actor.getType() == ECharacterType.Creep) {
                            skillingActionResult.pushAction(actor.action(new EnergyChangeAction(30)));
                        } else {
                            if (skill.getIndex() == 1) {
                                skillingActionResult.pushAction(actor.action(new EnergyChangeAction(10)));
                            }

                            if (skill.getIndex() == 2) {
                                skillingActionResult.pushAction(actor.action(new EnergyChangeAction(30)));
                            }
                        }
                    }
                }
            }
        }

        //tăng mana sage
        if (!isPassive) {
            if (actor.getType() != ECharacterType.Sage && actor.getType() != ECharacterType.Celestial) {
                if (actor.getMaster().sage != null) {
                    int sageMana = 0;

                    if (skill.getIndex() == 1 || skill.getIndex() == 2 || skill.getIndex() == 3) {
                        sageMana += 6;
                    }

                    if (sageMana > 0) {
                        skillingActionResult.pushAction(actor.getMaster().sage.action(new EnergyChangeAction(sageMana)));
                    }
                }

                if (actor.getMaster().celestial != null) {
                    int celestialMana = 0;

                    if (skill.getIndex() == 1 || skill.getIndex() == 2 || skill.getIndex() == 3) {
                        celestialMana += 4;
                    }

                    if (celestialMana > 0) {
                        skillingActionResult.pushAction(actor.getMaster().celestial.action(new EnergyChangeAction(celestialMana)));
                    }
                }
            }
        }

        // ON END SKILL
        for (BasePlayer player : actor.getMaster().getFightingManager().players) {
            for (TeamSlot teamSlot : player.team) {
                if (teamSlot.haveCharacter() && teamSlot.getCharacter().isLive()) {
                    Character character = teamSlot.getCharacter();
                    if (character.getType() == ECharacterType.Hero || character.getType() == ECharacterType.Boss || character.getType() == ECharacterType.MiniBoss) {
                        Skill passiveSkill = character.getSkill(0);
                        passiveSkill.setCanMiss(false);
                        actionResults.addAll(character.action(new SkillingAction(MatchState.ON_END_SKILL, passiveSkill, 0, false, true, false)));
                    }
                }
            }
        }

        //clear tank flag
        if (rootSkill) {
            for (BasePlayer player : actor.getMaster().getFightingManager().players) {
                player.clearTankFlag();
            }
        }

        return actionResults;
    }

    private Object getSkillID(Character actor, Skill skill) {
        if (actor.getType() == ECharacterType.Sage || actor.getType() == ECharacterType.Celestial) {
            return skill.getSkillInfo().id;
        }

        return skill.getIndex();
    }
}
