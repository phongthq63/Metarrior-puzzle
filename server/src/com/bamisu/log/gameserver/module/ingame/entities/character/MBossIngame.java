package com.bamisu.log.gameserver.module.ingame.entities.character;

import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.skill.SkillFactory;

import java.util.List;

/**
 * Create by Popeye on 9:27 PM, 4/27/2020
 */
public class MBossIngame extends Character {
    public MBossIngame(BasePlayer master) {
        super(master);
        this.setType(ECharacterType.MiniBoss);
    }

    public MBossIngame(BasePlayer master, ICharacter character){
        super(master, character);
        this.setType(ECharacterType.MiniBoss);
    }

    @Override
    public void initSkill() {
        List<SkillInfo> skillInfos = (List<SkillInfo>) getCharacterVO().getSkill();
        for(SkillInfo skillInfo : skillInfos){
            addSkill(SkillFactory.create(skillInfo, ECharacterType.MiniBoss));
        }
    }
}
