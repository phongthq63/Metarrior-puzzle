package com.bamisu.log.gameserver.datamodel.hero.entities;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;

public class HeroSlotBlessing {
    public String hashHero;
    public String idHero;
    public short level;
    public short position;
    public int timeStamp;

    public static HeroSlotBlessing create(String hashHero, String idHero, int level, int position) {
        HeroSlotBlessing heroSlotBlessing = new HeroSlotBlessing();
        heroSlotBlessing.hashHero = hashHero;
        heroSlotBlessing.idHero = idHero;
        heroSlotBlessing.level = (short) level;
        heroSlotBlessing.position = (short) position;

        return heroSlotBlessing;
    }

    public boolean haveCheck(){
        return (timeStamp != 0);
    }

    public boolean canRemove(){
        if(hashHero == null && Utils.getTimestampInSecond() - timeStamp > CharactersConfigManager.getInstance().getTimeReblessingConfig()){
            return true;
        }
        return false;
    }
}
