package com.bamisu.log.gameserver.module.ingame.entities.character;

import com.bamisu.log.gameserver.datamodel.hero.HeroSkillModel;
import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.skill.SkillFactory;

/**
 * Create by Popeye on 4:47 PM, 1/15/2020
 */
public class HeroIngame extends Character {
    public HeroIngame(BasePlayer master) {
        super(master);
        this.setType(ECharacterType.Hero);
    }

    public HeroIngame(BasePlayer master, ICharacter character) {
        super(master, character);
        this.setType(ECharacterType.Hero);
    }

    @Override
    public void initSkill() {
        HeroSkillModel heroSkillModel = (HeroSkillModel) getCharacterVO().getSkill();
        for (SkillInfo skillInfo : heroSkillModel.skills) {
            addSkill(SkillFactory.create(skillInfo, ECharacterType.Hero));
        }
    }
}
