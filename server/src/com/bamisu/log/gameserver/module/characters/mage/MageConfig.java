package com.bamisu.log.gameserver.module.characters.mage;

import com.bamisu.log.gameserver.module.characters.entities.ICharacterVO;

public class MageConfig implements ICharacterVO {
    public int atk;
    public int crit;
    public int critBonus;
    public int defensePenetration;

    public static MageConfig createMage(int atk, int crit, int critDmg, int pierce) {
        MageConfig mage = new MageConfig();
        mage.atk = atk;
        mage.crit = crit;
        mage.critBonus = critDmg;
        mage.defensePenetration = pierce;

        return mage;
    }

    @Override
    public String getID() {
        return "";
    }

    @Override
    public int readStar() {
        return 0;
    }

    @Override
    public String readkingdom() {
        return null;
    }

    @Override
    public String readelement() {
        return null;
    }
}
