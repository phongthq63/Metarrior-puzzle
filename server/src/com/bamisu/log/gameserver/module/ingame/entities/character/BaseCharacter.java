package com.bamisu.log.gameserver.module.ingame.entities.character;

import com.bamisu.gamelib.entities.Attr;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.log.gameserver.module.ingame.cmd.send.SendHeroSkill;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.Effect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EffectCategory;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.SE_StatBuff;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.SE_StatDebuff;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.log.gameserver.module.skill.template.passive.PassiveSkillTemplateProps;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 10:35 AM, 2/19/2020
 */
public class BaseCharacter {
    private BasePlayer master;
    private ICharacter characterVO;
    private ECharacterType type;

    private Element element;
    private Kingdom kingdom;

    private List<Effect> effectList = new ArrayList<>();
    private int maxHP;
    private int currentHP;
    private int maxEP;
    private int currentEP;
    private int shieldAll;
    private int shieldAllLastTurn = -1;

    public BasePlayer getMaster() {
        return master;
    }

    public void setMaster(BasePlayer master) {
        this.master = master;
    }

    public ICharacter getCharacterVO() {
        return characterVO;
    }

    public void setCharacterVO(ICharacter characterVO) {
        this.characterVO = characterVO;
    }

    public ECharacterType getType() {
        return type;
    }

    public void setType(ECharacterType type) {
        this.type = type;
    }

    public String getID() {
        return getCharacterVO().readID();
    }

    public int getLevel() {
        return getCharacterVO().readLevel();
    }

    public int getStar() {
        return getCharacterVO().readStar();
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;
    }

    public boolean isLive() {
        return getCurrentHP() > 0;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public void setCurrentHP(int currentHP) {
        if (currentHP > getMaxHP()) {
            this.currentHP = getMaxHP();
        }
        this.currentHP = currentHP;
    }

    public double getCurrentHPPercent() {
        return 1.0 * currentHP * 100 / maxHP;
    }

    public int getMaxEP() {
        return maxEP;
    }

    public void setMaxEP(int maxEP) {
        this.maxEP = maxEP;
    }

    public int getCurrentEP() {
        return currentEP;
    }

    public void setCurrentEP(int currentEP) {
        if (currentEP < 0) this.currentEP = 0;
        if (currentEP > 100) this.currentEP = 100;
        else this.currentEP = currentEP;
    }

    public int getShieldAll() {
        return shieldAll;
    }

    public void setShieldAll(int currentShieldAll) {
        if (currentShieldAll < 0) this.shieldAll = 0;
        if (currentShieldAll > maxHP) this.shieldAll = maxHP;
        else this.shieldAll = currentShieldAll;
    }

    public int getATTR(String shortName) {
        return 0;
    }

    private float getStatDeBuffRate(Attr attr) {
        float value = 0;
        for (Effect effect : effectList) {
            if (effect.getType() == EEffect.Stat_Debuff) {
                if (((SE_StatDebuff) effect).attr == attr) {
                    value -= ((SE_StatDebuff) effect).rate;
                }
            }
        }
        return value;
    }

    private float getStatBuffRate(Attr attr) {
        float value = 0;
        for (Effect effect : effectList) {
            if (effect.getType() == EEffect.Stat_Buff) {
                if (((SE_StatBuff) effect).attr == attr) {
                    value += ((SE_StatBuff) effect).rate;
                }
            }
        }
        return value;
    }


    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getHP() {
        float incRate = getStatBuffRate(Attr.HP);
        float reRate = getStatDeBuffRate(Attr.HP);
        float finalRate = incRate + reRate;
        return (int) (characterVO.readHP() + (characterVO.readHP() * finalRate / 100));
    }

    public float getSTR() {
        float incRate = getStatBuffRate(Attr.STRENGTH);
        float reRate = getStatDeBuffRate(Attr.STRENGTH);
        float incRateATK = getStatBuffRate(Attr.ATTACK);
        float reRateATK = getStatDeBuffRate(Attr.ATTACK);

        //tăng STR khi mất máu
        float incRateATKWhenHPLoss = 0;
        if (type != ECharacterType.Sage && type != ECharacterType.Celestial && type != ECharacterType.Creep) {
            Skill passiveSkill = ((Character) this).getSkill(0);
            PassiveSkillTemplateProps props = passiveSkill.getTamplatePropsAsPassive();
            if (props.increaseStrengthWhenHPLoss != null) {
                if (props.increaseStrengthWhenHPLoss.attr.equalsIgnoreCase(Attr.STRENGTH.shortName())) {
                    if (getCurrentHPPercent() < props.increaseStrengthWhenHPLoss.floor) {
                        incRateATKWhenHPLoss += ((props.increaseStrengthWhenHPLoss.floor - getCurrentHPPercent()) / props.increaseStrengthWhenHPLoss.per) * props.increaseStrengthWhenHPLoss.value;
                    }
                }
            }
        }

        float finalRate = incRate + incRateATK - reRate - reRateATK + incRateATKWhenHPLoss;
        return characterVO.readSTR() + characterVO.readSTR() * finalRate / 100;
    }

    public float getINT() {
        float incRate = getStatBuffRate(Attr.INTELLIGENCE);
        float reRate = getStatDeBuffRate(Attr.INTELLIGENCE);
        float incRateATK = getStatBuffRate(Attr.ATTACK);
        float reRateATK = getStatDeBuffRate(Attr.ATTACK);
        float finalRate = incRate + incRateATK - reRate - reRateATK;
        return characterVO.readINT() + characterVO.readINT() * finalRate / 100;
    }

    public float getATK() {
        float incRateATK = getStatBuffRate(Attr.ATTACK);
        float reRateATK = getStatDeBuffRate(Attr.ATTACK);
        float finalRate = incRateATK - reRateATK;
        return characterVO.readATK() + characterVO.readATK() * finalRate / 100;
    }

    public float getDEF() {
        float incRateDEF = getStatBuffRate(Attr.DEFENSE);
        float reRateDEF = getStatDeBuffRate(Attr.DEFENSE);
        float finalRate = incRateDEF - reRateDEF;
        return characterVO.readDEF() + characterVO.readDEF() * finalRate / 100;
    }

    public float getCRIT() {
        float incRate = getStatBuffRate(Attr.CRITICAL_CHANCE);
        float reRate = getStatDeBuffRate(Attr.CRITICAL_CHANCE);
        float finalRate = incRate - reRate;
        return characterVO.readCRIT() + characterVO.readCRIT() * finalRate / 100;
    }

    public float getCRITBONUS() {
        float incRate = getStatBuffRate(Attr.CRITICAL_BONUS_DAMAGE);
        float reRate = getStatDeBuffRate(Attr.CRITICAL_BONUS_DAMAGE);
        float finalRate = incRate - reRate;
        return characterVO.readCRITBONUS() + characterVO.readCRITBONUS() * finalRate / 100;
    }

    public float getTEN() {
        float incRate = getStatBuffRate(Attr.TENACITY);
        float reRate = getStatDeBuffRate(Attr.TENACITY);
        float finalRate = incRate - reRate;
        return characterVO.readTEN() + characterVO.readTEN() * finalRate / 100;
    }

    public float getELU() {
        float incRate = getStatBuffRate(Attr.ELUSIVENESS);
        float reRate = getStatDeBuffRate(Attr.ELUSIVENESS);
        float finalRate = incRate - reRate;
        return characterVO.readELU() + characterVO.readELU() * finalRate / 100;
    }

    public float getAGI() {
        float incRate = getStatBuffRate(Attr.AGILITY);
        float reRate = getStatDeBuffRate(Attr.AGILITY);
        float finalRate = incRate - reRate;
        return characterVO.readAGI() + characterVO.readAGI() * finalRate / 100;
    }

    public float getARM() {
        float incRate = getStatBuffRate(Attr.ARMOR);
        float reRate = getStatDeBuffRate(Attr.ARMOR);
        float incRateDef = getStatBuffRate(Attr.DEFENSE);
        float reRateDef = getStatDeBuffRate(Attr.DEFENSE);
        float finalRate = incRate - reRate + incRateDef - reRateDef;
        return characterVO.readARM() + characterVO.readARM() * finalRate / 100;
    }

    public float getDEX() {
        float incRate = getStatBuffRate(Attr.DEXTERITY);
        float reRate = getStatDeBuffRate(Attr.DEXTERITY);

        float finalRate = incRate - reRate;
        return characterVO.readDEX() + characterVO.readDEX() * finalRate / 100;
    }

    public float getMR() {
        float incRate = getStatBuffRate(Attr.MAGIC_RESISTANCE);
        float reRate = getStatDeBuffRate(Attr.MAGIC_RESISTANCE);
        float incRateDef = getStatBuffRate(Attr.DEFENSE);
        float reRateDef = getStatDeBuffRate(Attr.DEFENSE);
        float finalRate = incRate - reRate + incRateDef - reRateDef;
        return characterVO.readMR() + characterVO.readMR() * finalRate / 100;
    }

    public float getAPEN() {
        float incRate = getStatBuffRate(Attr.ARMOR_PENETRATION);
        float reRate = getStatDeBuffRate(Attr.ARMOR_PENETRATION);
        float incRateDPEN = getStatBuffRate(Attr.DEFENSE_PENETRATION);
        float reRateDPEN = getStatDeBuffRate(Attr.DEFENSE_PENETRATION);
        float finalRate = incRate - reRate + incRateDPEN - reRateDPEN;
        return characterVO.readAPEN() + characterVO.readAPEN() * finalRate / 100;
    }

    public float getMPEN() {
        float incRate = getStatBuffRate(Attr.MAGIC_PENETRATION);
        float reRate = getStatDeBuffRate(Attr.MAGIC_PENETRATION);
        float incRateDPEN = getStatBuffRate(Attr.DEFENSE_PENETRATION);
        float reRateDPEN = getStatDeBuffRate(Attr.DEFENSE_PENETRATION);
        float finalRate = incRate - reRate + incRateDPEN - reRateDPEN;
        return characterVO.readMPEN() + characterVO.readMPEN() * finalRate / 100;
    }

    public float getDPEN() {
        float incRateDPEN = getStatBuffRate(Attr.DEFENSE_PENETRATION);
        float reRateDPEN = getStatDeBuffRate(Attr.DEFENSE_PENETRATION);
        float finalRate = incRateDPEN - reRateDPEN;
        return characterVO.readDPEN() + characterVO.readDPEN() * finalRate / 100;
    }

    public List<Effect> getEffectList() {
        return effectList;
    }

    public boolean haveEffect(EEffect effectType) {
        for (Effect effect : effectList) {
            if (effect.getType() == effectType) {
                return true;
            }
        }

        return false;
    }

    /**
     * dính 1 hiệu ứng
     *
     * @param effect
     * @return
     */
    public boolean applyEffect(Effect effect) {
        synchronized (effectList) {
            List<Effect> removeList = new ArrayList<>();

            //nếu là soft hoặc hard
            switch (effect.getCategory()) {
                case HARD:
                    for (Effect tmpEffect : effectList) {
                        if (tmpEffect.getCategory() == EffectCategory.HARD) {
                            if (!removeList.contains(tmpEffect)) removeList.add(tmpEffect);
                        }
                    }
                    if (!removeList.isEmpty()) {
                        effectList.removeAll(removeList);
                    }
                    effectList.add(effect);
                    return true;
                case SOFT:
                    int count = 0;
                    for (Effect tmpEffect : effectList) {
                        if (tmpEffect.same(effect)) {
                            count++;
                        }
                    }
                    if (count < effect.getType().getMaxStack()) {
                        effectList.add(effect);
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        }
    }

    public boolean canUltimate(boolean isActive) {
        return true;
    }

    public boolean canUltimate() {
        return canUltimate(false, new SendHeroSkill(), "");
    }

    public boolean canUltimate(boolean isActive, SendHeroSkill sendHeroSkill, String actorID) {
        if(isActive) return true;
        for (Effect effect : effectList) {
            if (effect.getType() == EEffect.Stunned ||
                    effect.getType() == EEffect.Rooted ||
                    effect.getType() == EEffect.Paralyzed ||
                    effect.getType() == EEffect.Sleep ||
                    effect.getType() == EEffect.Petrified ||
                    effect.getType() == EEffect.Frozen ||
                    effect.getType() == EEffect.Silence
                    ) {
                if (isActive) {
                    sendHeroSkill.error = true;
                    sendHeroSkill.errorInfo = actorID + ": đang bị hiệu ứng " + effect.getType().getID();
                }
                return false;
            }
        }

        if (getType() == ECharacterType.Sage || getType() == ECharacterType.Celestial) {
            if (getCurrentEP() < getMaxEP()) {
                return false;
            }
            return true;
        } else {
            if (!isLive()) {
                return false;
            }
            if (getCurrentEP() < getMaxEP()) {
                if (isActive) {
                    sendHeroSkill.error = true;
                    sendHeroSkill.errorInfo = actorID + ": không đủ mana " + getCurrentEP() + "/" + getMaxEP();
                }
                return false;
            }
            return true;
        }
    }

    public boolean canSkillActivate() {
        for (Effect effect : effectList) {
            if (effect.getType() == EEffect.Stunned ||
                    effect.getType() == EEffect.Rooted ||
                    effect.getType() == EEffect.Paralyzed ||
                    effect.getType() == EEffect.Sleep ||
                    effect.getType() == EEffect.Petrified ||
                    effect.getType() == EEffect.Frozen
                    ) return false;
        }

        return true;
    }


    public boolean canSkillPassive() {
        //boss trong chế độ Endless Night luôn passive đc
        if ((getMaster().getFightingManager().function == EFightingFunction.ENDLESS_NIGHT) && getType() == ECharacterType.MiniBoss) {
            return true;
        }

        for (Effect effect : effectList) {
            if (effect.getType() == EEffect.Stunned ||
                    effect.getType() == EEffect.Rooted ||
                    effect.getType() == EEffect.Paralyzed ||
                    effect.getType() == EEffect.Sleep ||
                    effect.getType() == EEffect.Petrified ||
                    effect.getType() == EEffect.Frozen ||
                    effect.getType() == EEffect.Confused) return false;
        }

        return true;
    }


    public void removeEffect(List<Effect> removeList) {
        effectList.removeAll(removeList);
    }

    /**
     * xóa tất cả các hiệu ứng hiển thị bên client
     */
    public void clearAllDisplayEffect() {
        List<Effect> removeList = new ArrayList<>();
        for (Effect effect : effectList) {
            if (effect.isDisplay) {
                removeList.add(effect);
            }
        }
        effectList.removeAll(removeList);
    }

    /**
     * xóa các hiệu ứng có hại thị bên client
     */
    public void clearAllEffectNegative() {
        List<Effect> removeList = new ArrayList<>();
        for (Effect effect : effectList) {
            if (effect.isDisplay && effect.isNegative()) {
                removeList.add(effect);
            }
        }
        effectList.removeAll(removeList);
    }

    /**
     * % damage bị giảm (ko tinh ultilmate)
     */
    public float getDeDamageOutRateWithoutUltilmate() {
        float rate = 0;
        for (Effect effect : getEffectList()) {
            if (effect.getType() == EEffect.Cripple) {
                rate += 50;
            }
        }
        return rate;
    }

    public int getShieldAllLastTurn() {
        return shieldAllLastTurn;
    }

    public void setShieldAllLastTurn(int shieldAllLastTurn) {
        this.shieldAllLastTurn = shieldAllLastTurn;
    }
}
