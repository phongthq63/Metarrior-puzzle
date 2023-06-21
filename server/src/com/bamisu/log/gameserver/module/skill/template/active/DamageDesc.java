package com.bamisu.log.gameserver.module.skill.template.active;

import com.bamisu.log.gameserver.module.skill.DamageType;

/**
 * Create by Popeye on 11:55 AM, 5/28/2020
 */
public class DamageDesc {
    public String damageRate;
    public String damageType;

    public boolean hpType() {
        return damageType.equalsIgnoreCase(DamageType.STANDARD.getStrValue()) ||
                damageType.equalsIgnoreCase(DamageType.MAGIC.getStrValue()) ||
                damageType.equalsIgnoreCase(DamageType.PHYSICAL.getStrValue());
    }

    public boolean manaType() {
        return damageType.equalsIgnoreCase(DamageType.MANA.getStrValue());
    }
}
