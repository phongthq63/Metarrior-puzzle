package com.bamisu.log.gameserver.module.ingame.entities.skill;

import com.bamisu.log.gameserver.module.ingame.entities.Diamond;

/**
 * Create by Popeye on 11:01 AM, 2/21/2020
 */
public class FinalColorCombo {
    public Diamond diamond;
    public ComboType type = ComboType.SKILL3;
    public int count;
    public int rate = 0;
    public boolean isCrit = false;

    public void addRate(Combo combo) {
        rate += combo.getRate(type);
    }
}
