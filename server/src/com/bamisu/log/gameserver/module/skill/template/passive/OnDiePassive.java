package com.bamisu.log.gameserver.module.skill.template.passive;

import com.bamisu.log.gameserver.module.skill.template.entities.SkillMakeSEDesc;

import java.util.List;

/**
 * Create by Popeye on 2:53 PM, 2/24/2021
 */
public class OnDiePassive {
    public int activeTime; // số lần tối đa kích hoạt trong 1 trận đấu
    public List<SkillMakeSEDesc> se = null;

    public boolean canActive(int activeCount){
        return activeCount < activeTime;
    }
}
