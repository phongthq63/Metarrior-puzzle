package com.bamisu.log.gameserver.module.ingame.entities.character;

import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.gamelib.entities.Attr;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActorStatistical;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action.EffectApplyAction;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.actor.IngameActor;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.CampaignFightManager;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.log.gameserver.module.skill.SkillFactory;
import com.bamisu.log.gameserver.module.skill.SkillType;
import com.bamisu.log.gameserver.module.skill.SkillUtils;
import com.bamisu.log.gameserver.module.skill.template.active.ActiveSkillTemplateProps;
import com.bamisu.log.gameserver.module.skill.template.passive.PassiveSkillTemplateProps;
import com.bamisu.gamelib.skill.passive.Statbuff;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 4:45 PM, 1/15/2020
 */
public abstract class Character extends BaseCharacter implements IngameActor {
    private String actorID;
    private List<Skill> skills = new ArrayList<>();
    private List<Skill> otherSkills = new ArrayList<>();
    private List<Skill> otherPassive = new ArrayList<>();
    private TeamSlot teamSlot;
    private boolean haveTankForAlly = false;
    private boolean isBossMode = false;
    private int returnTime = 1;
    private int tankedTime = 0; //Số lần đã tank hộ đồng đội trong 1 turn
    private int onDiePassiveCount = 0; //đếm số lần đã kích hoạt nội tại on die
    private ActorStatistical actorStatistical;

    public Character(BasePlayer master) {
        setMaster(master);
        actorID = master.getFightingManager().genActorID();
        actorStatistical = new ActorStatistical(actorID);
    }

    public Character(BasePlayer master, ICharacter character) {
        setMaster(master);
        setCharacterVO(character);
        actorID = master.getFightingManager().genActorID();
        actorStatistical = new ActorStatistical(actorID);
    }

    public void initBefoFighting() {
        initSkill();

        if (getType() != ECharacterType.Sage && getType() != ECharacterType.Celestial) {
            setElement(Element.fromID(getCharacterVO().readElement()));
            setKingdom(Kingdom.fromID(getCharacterVO().readKingdom()));

            initHP();
        }

        setMaxEP(100);
        setCurrentEP(0);
        setShieldAll(0);

//        test
//        if(getMaster().getPlayerType() == EPlayerType.NPC){
//            setCurrentEP(90);
//        }

        //tutorial
        FightingManager fightingManager = getMaster().getFightingManager();
        if(fightingManager.function == EFightingFunction.CAMPAIGN){
            if(fightingManager.getCampaignArea() == 0
                    && fightingManager.getCampaignStation() == 2
                    && (((CampaignFightManager) fightingManager).tutorialState.contains(-1) && !((CampaignFightManager) fightingManager).tutorialState.contains(6))
                    ){
                if(getType() == ECharacterType.Sage){
                    setCurrentEP(98);
                }
            }
        }
    }

    public void initHP() {
        if (getType() != ECharacterType.Sage && getType() != ECharacterType.Celestial) {

            //chế độ boss không bao giờ chết
            setMaxHP(Math.round(getHP()));
            setCurrentHP(getMaxHP());
        }
    }

    public abstract void initSkill();

    public List<Skill> addSkill(Skill skill) {
        skills.add(skill);
        skill.setIndex(skills.size() - 1);
        return skills;
    }

    public Skill getOtherSkill(String skillId) {
        Skill tmpSkill = null;
        for (Skill skill : otherSkills) {
            if (skill.getSkillBaseInfo().id.equalsIgnoreCase(skillId)) {
                tmpSkill = skill;
                break;
            }
        }

        if (tmpSkill == null) {
            tmpSkill = SkillFactory.create(new SkillInfo(skillId, 1), ECharacterType.Other);
            otherSkills.add(tmpSkill);
        }
        return tmpSkill;
    }

    public List<Skill> addOtherPassiveSkill(Skill skill) {
        otherPassive.add(skill);
        skill.setIndex(otherPassive.size() - 1);
        return otherPassive;
    }

    public Skill getSkill(int index) {
        return skills.get(index);
    }

    @Override
    public String getActorID() {
        return actorID;
    }

    @Override
    public List<ActionResult> action(Action action) {
        action.setActor(this);
        return action.run();
    }

    public void setTeamSlot(TeamSlot teamSlot) {
        this.teamSlot = teamSlot;
    }

    public TeamSlot getTeamSlot() {
        return this.teamSlot;
    }

    public int getSkillCount() {
        return skills.size();
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public List<Skill> getOtherPassiveSkills() {
        return otherPassive;
    }

    public Skill getSkillByID(String skillID) {
        for (Skill skill : skills) {
            if (skill.getSkillInfo().id.equalsIgnoreCase(skillID)) {
                return skill;
            }
        }

        return null;
    }

    public int getUtilsSkillIndex() {
        if (getType() == ECharacterType.Hero) {
            return 3;
        }
        if (getType() == ECharacterType.Creep) {
            return 1;
        }
        if (getType() == ECharacterType.MiniBoss) {
            return 3;
        }
        if (getType() == ECharacterType.Boss) {
            return 3;
        }

        return -1;
    }

    public int getRandomNormalSkillIndex() {
        if (getType() == ECharacterType.Hero) {
            if (Utils.rate(50)) return 1;
            return 2;
        }
        if (getType() == ECharacterType.Creep) {
            return 0;
        }
        if (getType() == ECharacterType.MiniBoss) {
            if (Utils.rate(50)) return 1;
            return 2;
        }
        if (getType() == ECharacterType.Boss) {
            if (Utils.rate(50)) return 1;
            return 2;
        }

        return -1;
    }

    public int getMinSkill() {
        if (getType() == ECharacterType.Hero) {
            return 1;
        }
        if (getType() == ECharacterType.Creep) {
            return 0;
        }
        if (getType() == ECharacterType.MiniBoss) {
            return 1;
        }
        if (getType() == ECharacterType.Boss) {
            return 1;
        }

        return -1;
    }

    public int getMajSkill() {
        if (getType() == ECharacterType.Hero) {
            return 2;
        }
        if (getType() == ECharacterType.Creep) {
            return 0;
        }
        if (getType() == ECharacterType.MiniBoss) {
            return 2;
        }
        if (getType() == ECharacterType.Boss) {
            return 2;
        }

        return -1;
    }

    public boolean isUtilmate(Skill skill) {
        if (getType() == ECharacterType.Creep) {
            return skills.indexOf(skill) == 1;
        }

        if (getType() == ECharacterType.Sage || getType() == ECharacterType.Celestial) {
            return skills.indexOf(skill) == 0;
        }

        return skills.indexOf(skill) == 3;
    }

    public int getSkillIndex(Skill skill){
        return skills.indexOf(skill);
    }

    public boolean canTankForAlly(ActiveSkillTemplateProps sourceSkillProps) {
        if(sourceSkillProps.preventTank) return false;

        if (!canSkillPassive()) return false;

        if (isHaveTankForAlly()) {
            return false;
        }

        if (getType() == ECharacterType.Hero || getType() == ECharacterType.MiniBoss || getType() == ECharacterType.Boss) {
            Skill skill = getSkill(0);
            if (skill.getSkillBaseInfo().type == SkillType.PASSIVE.getIntValue()) {
                PassiveSkillTemplateProps props = skill.getTamplatePropsAsPassive();
                if (props.tank != null) {
                    if(tankedTime < props.tank.maxUsePerTurn){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public float getTankRate() {
        Skill skill = getSkill(0);
        if (skill.getSkillBaseInfo().type == SkillType.PASSIVE.getIntValue()) {
            PassiveSkillTemplateProps props = skill.getTamplatePropsAsPassive();
            if (props.tank != null) {
                return props.tank.absorbRate;
            }
        }

        return 0;
    }

    public void setHaveTankForAlly(boolean haveTankForAlly) {
        this.haveTankForAlly = haveTankForAlly;
    }

    public boolean isHaveTankForAlly() {
        return haveTankForAlly;
    }

    public void applyMapBonus(Element elementMap) {
        List<Statbuff> statbuffs = TeamUtils.getMapBonus(getElement(), elementMap);

        for (Statbuff statbuff : statbuffs) {
            action(new EffectApplyAction(
                    this,
                    isStatDebuffEffect(statbuff),
                    999,
                    Arrays.asList(
                            statbuff.attr,
                            calculaRateStatBuff(statbuff),
                            1000
                    ),
                    false));
        }

    }

    public EEffect isStatDebuffEffect(Statbuff statbuff) {
        if (statbuff.isDebuff) return EEffect.Stat_Debuff;
        return EEffect.Stat_Buff;
    }

    public float calculaRateStatBuff(Statbuff statbuff) {
        float finalRate = Float.valueOf(String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(statbuff.rate, this, null))));
        if (statbuff.max != null) {
            float maxRate = Float.valueOf(String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(statbuff.max, this, null))));
            if (finalRate > maxRate) {
                finalRate = maxRate;
            }
        }
        return finalRate;
    }

    public void applyPvPOfflineDefenderBonus() {
        action(new EffectApplyAction(this, EEffect.Stat_Buff, 999,
                Arrays.asList(
                        Attr.HP.shortName(),
                        20,
                        1000
                ),
                false));
        action(new EffectApplyAction(this, EEffect.Stat_Buff, 999,
                Arrays.asList(
                        Attr.ATTACK.shortName(),
                        2,
                        1000
                ),
                false));
        action(new EffectApplyAction(this, EEffect.Stat_Buff, 999,
                Arrays.asList(
                        Attr.DEFENSE.shortName(),
                        2,
                        1000
                ),
                false));
    }

    public void applyPositionBonus() {
        List<Statbuff> statbuffs = TeamUtils.getPosBonus(getTeamSlot().getPos());

        for (Statbuff statbuff : statbuffs) {
            action(new EffectApplyAction(
                    this,
                    isStatDebuffEffect(statbuff),
                    999,
                    Arrays.asList(
                            statbuff.attr,
                            calculaRateStatBuff(statbuff),
                            1000
                    ),
                    false));
        }

    }

    public boolean isBossMode() {
        return isBossMode;
    }

    public void setIsBossMode(boolean isBossMode) {
        this.isBossMode = isBossMode;
    }

    public boolean changeReturnTime() {
        if (returnTime <= 0) return false;
        returnTime--;
        return true;
    }

    public ActorStatistical getActorStatistical() {
        return actorStatistical;
    }

    public void incTankedTime(){
        this.tankedTime ++;
    }

    public void berfoturn(){
        this.tankedTime = 0;
    }

    /**
     * đếm số lần đã kích hoạt nội tại on die
     */
    public void pushOnDiePassiveCount() {
        this.onDiePassiveCount ++;
    }
    public int getOnDiePassiveCount() {
        return this.onDiePassiveCount;
    }

    /**
     * kiểm tra xem nhân vật này có thể bị giảm máu ko?
     * @return
     */
    public boolean canReductionHealth() {
        if(haveEffect(EEffect.Immortal)) return false;
        return true;
    }

    /**
     * kiểm tra xem nhân vật này có thể bị giảm máu ko?
     * @return
     */
    public boolean canApplyNegativeEffect() {
        if(haveEffect(EEffect.Immunity)) return false;
        return true;
    }
}
