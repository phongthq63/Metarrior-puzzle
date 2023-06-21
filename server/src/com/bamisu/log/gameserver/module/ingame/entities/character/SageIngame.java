package com.bamisu.log.gameserver.module.ingame.entities.character;

import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.CampaignFightManager;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.gamelib.skill.SkillConfigManager;
import com.bamisu.log.gameserver.module.ingame.entities.skill.SageActiveSkillIngameMana;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.log.gameserver.module.skill.SkillFactory;
import com.bamisu.log.gameserver.module.skill.SkillType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by Popeye on 9:07 AM, 5/14/2020
 */
public class SageIngame extends Character {
    private Map<String, Integer> useSkillCountMap = new ConcurrentHashMap<>();
    private Map<String, SageActiveSkillIngameMana> activeSkillManaMap = new ConcurrentHashMap<>();

    public SageIngame(BasePlayer master) {
        super(master);
        this.setType(ECharacterType.Sage);
    }

    public SageIngame(BasePlayer master, ICharacter character) {
        super(master, character);
        this.setType(ECharacterType.Sage);
    }

    public Map<String, SageActiveSkillIngameMana> getActiveSkillManaMap() {
        return activeSkillManaMap;
    }

    public boolean canActiveSkill(boolean isACtive, String skillID) {
        return isACtive;
    }

    public boolean canActiveSkill(String skillID) {
//        if (useSkillCountMap.containsKey(skillID)) {
//            return useSkillCountMap.get(skillID) < 1;
//        }
        if (activeSkillManaMap.containsKey(skillID)) {
            return activeSkillManaMap.get(skillID).canActiveSkill();
        }

        return true;
    }

    public int getCurrentEPActiveSkill(String skillID) {
        return activeSkillManaMap.get(skillID).mana;
    }

    public void useActiveSkill(String skillID) {
//        if (useSkillCountMap.containsKey(skillID)) {
//            useSkillCountMap.put(skillID, useSkillCountMap.get(skillID) + 1);
//        }
        if (activeSkillManaMap.containsKey(skillID)) {
            activeSkillManaMap.get(skillID).useSkill();
        }
    }

    public void changeManaACtiveSkill(int mana) {
        for (String key : activeSkillManaMap.keySet()) {
            activeSkillManaMap.get(key).changeMana(mana);
        }
    }

    @Override
    public void initSkill() {
        SageSkillModel sageSkillModel = (SageSkillModel) getCharacterVO().getSkill();

        //ultimate
        addSkill(SkillFactory.create(sageSkillModel.readCurrentUltil(), ECharacterType.Sage));

        //5 skill
        FightingManager fightingManager = getMaster().getFightingManager();
        for (SkillInfo skillInfo : sageSkillModel.currentSkill) {
            Skill skill = SkillFactory.create(skillInfo, ECharacterType.Sage);
            addSkill(skill);
            useSkillCountMap.put(skillInfo.id, 0);

            //tutorial
            if (fightingManager.function == EFightingFunction.CAMPAIGN
                    && fightingManager.getCampaignArea() == 0
                    && fightingManager.getCampaignStation() == 2
                    && (((CampaignFightManager) fightingManager).tutorialState.contains(-1) && !((CampaignFightManager) fightingManager).tutorialState.contains(6))
                    ) {

                    activeSkillManaMap.put(skillInfo.id, new SageActiveSkillIngameMana(skill.getSkillBaseInfo().mana - 10, skill.getSkillBaseInfo().mana));
            }else {
                activeSkillManaMap.put(skillInfo.id, new SageActiveSkillIngameMana(skill.getSkillBaseInfo().mana, skill.getSkillBaseInfo().mana));
            }
        }

        for (SkillInfo skillInfo : sageSkillModel.skills) {
            if (SkillConfigManager.getInstance().getSageSkill(skillInfo.id).type.equalsIgnoreCase(SkillType.PASSIVE.getName())) {
                addOtherPassiveSkill(SkillFactory.create(skillInfo, ECharacterType.Sage));
            }
        }
    }
}
