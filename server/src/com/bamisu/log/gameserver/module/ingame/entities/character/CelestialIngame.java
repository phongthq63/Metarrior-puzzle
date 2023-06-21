package com.bamisu.log.gameserver.module.ingame.entities.character;

import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.skill.SkillFactory;

import java.util.List;

/**
 * Create by Popeye on 11:17 AM, 5/18/2020
 */
public class CelestialIngame extends Character{
    public CelestialIngame(BasePlayer master) {
        super(master);
        this.setType(ECharacterType.Celestial);
    }

    public CelestialIngame(BasePlayer master, ICharacter character) {
        super(master, character);
        this.setType(ECharacterType.Celestial);
    }

    @Override
    public void initSkill() {
        //ultil
        List<SkillInfo> skillInfos = (List<SkillInfo>) getCharacterVO().getSkill();
        for(SkillInfo skillInfo : skillInfos){
            addSkill(SkillFactory.create(skillInfo, ECharacterType.Celestial));
        }
    }
}
