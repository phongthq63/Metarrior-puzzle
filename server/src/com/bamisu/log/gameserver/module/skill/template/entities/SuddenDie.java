package com.bamisu.log.gameserver.module.skill.template.entities;

import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.skill.SkillUtils;
import com.bamisu.gamelib.utils.Utils;

/**
 * Create by Popeye on 8:19 PM, 6/2/2020
 */
public class SuddenDie {
    public String percentHP;
    public int maxpercentHP;

    public boolean can(Character actor, Character target){
        double percent = Double.parseDouble(String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(percentHP, actor, null))));
        if(percent > maxpercentHP){
            percent = maxpercentHP;
        }
        double currentHPPercent = target.getCurrentHPPercent();
        return currentHPPercent <= percent;
    }
}
