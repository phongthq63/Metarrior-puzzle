package com.bamisu.log.gameserver.module.ingame.entities.skill;

/**
 * Create by Popeye on 2:39 PM, 3/10/2021
 */
public class SageActiveSkillIngameMana {
    public int mana;
    public int maxMana;

    public SageActiveSkillIngameMana(int mana, int maxMana) {
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public boolean canActiveSkill() {
        return mana >= maxMana;
    }

    public void useSkill() {
        mana = 0;
    }

    public void changeMana(int mana) {
        this.mana += mana;
        if (this.mana < 0) {
            this.mana = 0;
            return;
        }

        if (this.mana > this.maxMana) {
            this.mana = this.maxMana;
            return;
        }
    }
}
