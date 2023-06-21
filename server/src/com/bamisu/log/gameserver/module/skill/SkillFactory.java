package com.bamisu.log.gameserver.module.skill;

import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;

/**
 * Create by Popeye on 10:33 AM, 2/27/2020
 */
public class SkillFactory {

    /**
     *
     * @param skillInfo
     * @param characterType kiểu skill
     * @return
     */
    public static Skill create(SkillInfo skillInfo, ECharacterType characterType){
        return new Skill(skillInfo, characterType);

//        switch (SkillConfigManager.getInstance().getBaseSkillInfo(skillInfo.id).id){
//            case "T1018S1":
//                return new T1018S1(skillInfo);
//            case "T1018S2":
//                return new T1018S2(skillInfo);
//            case "T1018S3":
//                return new T1018S3(skillInfo);
//            case "T1018S4":
//                return new T1018S4(skillInfo);
//            default:
//                return new T1018S1(skillInfo);
//        }
    }
}
