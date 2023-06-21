package com.bamisu.log.gameserver.module.ingame.entities.character;

import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.skill.SkillFactory;

import java.util.List;

/**
 * Create by Popeye on 4:48 PM, 1/15/2020
 */
public class CreepIngame extends Character {
    public CreepIngame(BasePlayer master) {
        super(master);
        this.setType(ECharacterType.Creep);
    }

    public CreepIngame(BasePlayer master, ICharacter character){
        super(master, character);
        this.setType(ECharacterType.Creep);
    }

    @Override
    public void initSkill() {
        List<SkillInfo> skillInfos = (List<SkillInfo>) getCharacterVO().getSkill();
        for(SkillInfo skillInfo : skillInfos){
            addSkill(SkillFactory.create(skillInfo, ECharacterType.Creep));
        }
    }
}
