package com.bamisu.log.gameserver.module.skill;

import com.bamisu.gamelib.skill.SkillConfigManager;
import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.module.ingame.entities.MatchState;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.gamelib.skill.config.entities.BaseSkillInfo;
import com.bamisu.gamelib.skill.config.entities.SageSkillVO;
import com.bamisu.gamelib.skill.config.entities.SkillDesc;
import com.bamisu.log.gameserver.module.skill.template.SkillTemplate;
import com.bamisu.log.gameserver.module.skill.template.active.ActiveSkillTemplateProps;
import com.bamisu.log.gameserver.module.skill.template.passive.PassiveSkillTemplateProps;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 10:26 AM, 2/27/2020
 */
public class Skill {
    SkillInfo skillInfo;
    SkillDesc skillDesc;
    BaseSkillInfo skillBaseInfo;
    Object tamplateProps;
    ActiveSkillTemplateProps tamplatePropsAsActive;
    PassiveSkillTemplateProps tamplatePropsAsPassive;
    int index;

    public Skill(SkillInfo skillInfo, ECharacterType characterType) {
        this.skillInfo = skillInfo;
        switch (ECharacterType.fromType(characterType.getType())) {
            case Hero:
                this.skillDesc = SkillConfigManager.getInstance().getSkillDesc(skillInfo.id);
                this.skillBaseInfo = SkillConfigManager.getInstance().getBaseSkillInfo(skillInfo.id);
                break;
            case Celestial:
                this.skillDesc = SkillConfigManager.getInstance().getSkillDescCelestial(skillInfo.id);
                this.skillBaseInfo = SkillConfigManager.getInstance().getBaseSkillInfoCelestial(skillInfo.id);
                break;
            case Creep:
                this.skillDesc = SkillConfigManager.getInstance().getSkillDescCreep(skillInfo.id);
                this.skillBaseInfo = SkillConfigManager.getInstance().getBaseSkillInfoCreep(skillInfo.id);
                break;
            case MiniBoss:
                this.skillDesc = SkillConfigManager.getInstance().getSkillDescCreep(skillInfo.id);
                this.skillBaseInfo = SkillConfigManager.getInstance().getBaseSkillInfoCreep(skillInfo.id);
                break;
            case Boss:
                this.skillDesc = SkillConfigManager.getInstance().getSkillDescCreep(skillInfo.id);
                this.skillBaseInfo = SkillConfigManager.getInstance().getBaseSkillInfoCreep(skillInfo.id);
                break;
            case Other:
                this.skillDesc = SkillConfigManager.getInstance().getSkillDescOther(skillInfo.id);
                this.skillBaseInfo = SkillConfigManager.getInstance().getBaseSkillInfoOther(skillInfo.id);
                break;
            case Sage:
                this.skillDesc = SkillConfigManager.getInstance().getSkillDescSage(skillInfo.id);

                SageSkillVO sageSkillVO = SkillConfigManager.getInstance().getSageSkill(skillInfo.id);
                if (sageSkillVO.type.equalsIgnoreCase("Ultimate")) {
                    this.skillBaseInfo = new BaseSkillInfo(sageSkillVO.id, SkillType.ACTIVE.getIntValue(), sageSkillVO.name);
                } else {
                    this.skillBaseInfo = new BaseSkillInfo(sageSkillVO.id, SkillType.fromName(sageSkillVO.type).getIntValue(), sageSkillVO.name);
                    this.skillBaseInfo.mana = sageSkillVO.mana;
                }

                break;
            default:
                this.skillDesc = SkillConfigManager.getInstance().getSkillDesc(skillInfo.id);
                this.skillBaseInfo = SkillConfigManager.getInstance().getBaseSkillInfo(skillInfo.id);
        }

        build();
    }

    /**
     * buid skill tương ứng với level skill
     */
    public void build() {
        try {
            setTamplateProps(getSkillDesc().templateProps.get(getSkillInfo().level - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SkillInfo getSkillInfo() {
        return skillInfo;
    }

    public void setSkillInfo(SkillInfo skillInfo) {
        this.skillInfo = skillInfo;
    }

    public SkillDesc getSkillDesc() {
        return skillDesc;
    }

    public void setSkillDesc(SkillDesc skillDesc) {
        this.skillDesc = skillDesc;
    }

    public BaseSkillInfo getSkillBaseInfo() {
        return skillBaseInfo;
    }

    public void setSkillBaseInfo(BaseSkillInfo skillBaseInfo) {
        this.skillBaseInfo = skillBaseInfo;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<ActionResult> doSkill(MatchState state, Character actor, int diamondDamageRateBonus, boolean rootSkill, boolean isPassive, boolean isCrit) {
        return SkillTemplate.getTemplate(getSkillDesc().template).setCustomTargert(customTargert).setCanMiss(canMiss).setDiamondDamageRateBonus(diamondDamageRateBonus).doSkill(state, this, actor, rootSkill, isPassive, isCrit);
    }

    boolean canMiss = true;

    public boolean isCanMiss() {
        return canMiss;
    }

    public void setCanMiss(boolean canMiss) {
        this.canMiss = canMiss;
    }

    List<Character> customTargert = new ArrayList<>();

    public List<Character> getCustomTargert() {
        return customTargert;
    }

    public void setCustomTargert(List<Character> customTargert) {
        this.customTargert = customTargert;
    }


    public void setTamplateProps(Object tamplateProps) {
        this.tamplateProps = tamplateProps;
    }

    private Object getTamplateProps() {
        return tamplateProps;
    }

    public ActiveSkillTemplateProps getTamplatePropsAsActive() {
        if (this.tamplatePropsAsActive == null) {
            this.tamplatePropsAsActive = Utils.fromJson(Utils.toJson(tamplateProps), ActiveSkillTemplateProps.class);
        }
        return this.tamplatePropsAsActive;
    }

    public PassiveSkillTemplateProps getTamplatePropsAsPassive() {
        if (this.tamplatePropsAsPassive == null) {
            this.tamplatePropsAsPassive = Utils.fromJson(Utils.toJson(tamplateProps), PassiveSkillTemplateProps.class);
        }
        return this.tamplatePropsAsPassive;
    }
}
