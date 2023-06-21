package com.bamisu.gamelib.skill;

import com.bamisu.gamelib.skill.config.*;
import com.bamisu.gamelib.skill.config.entities.*;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Create by Popeye on 5:30 PM, 12/23/2019
 */
public class SkillConfigManager {
    public static SkillConfigManager instance = new SkillConfigManager();

    public static SkillConfigManager getInstance(){
        return instance;
    }

    HeroSkillConfig heroSkillConfig;
    HeroSkillDescConfig heroSkillDescConfig;

    SageSkillConfig sageSkillTreeConfig;
    SageSkillDescConfig sageSkillDescConfig;

    CelestialSkillConfig celestialSkillConfig;
    CelestialSkillDescConfig celestialSkillDescConfig;

    CreepSkillConfig creepSkillConfig;
    CreepSkillDescConfig creepSkillDescConfig;

    OtherSkillConfig otherSkillConfig;
    OtherSkillDescConfig otherSkillDescConfig;

    WinCoditionConfig winCoditionConfig;
    TeamEffectConfig teamEffectConfig;
    BossModeConfig bossModeConfig;

    public SkillConfigManager() {
        heroSkillConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKILL_BASE_INFO), HeroSkillConfig.class);
        heroSkillConfig.build();
        heroSkillDescConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKILL_DESC), HeroSkillDescConfig.class);

        sageSkillTreeConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKILL_TREE_SAGE), SageSkillConfig.class);
        sageSkillDescConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKILL_INFO_SAGE), SageSkillDescConfig.class);

        celestialSkillConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKILL_CELESTIAL), CelestialSkillConfig.class);
        celestialSkillDescConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKILL_DESC_CELESTIAL), CelestialSkillDescConfig.class);

        creepSkillConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKILL_CREEP), CreepSkillConfig.class);
        creepSkillDescConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKILL_DESC_CREEP), CreepSkillDescConfig.class);

        otherSkillConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKILL_OTHER), OtherSkillConfig.class);
        otherSkillDescConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKILL_DESC_OTHER), OtherSkillDescConfig.class);

        winCoditionConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_WIN_CODITION), WinCoditionConfig.class);
        teamEffectConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_TEAM_EFFECT), TeamEffectConfig.class);
        bossModeConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_BOSS_MODE_TEAM), BossModeConfig.class);
    }

    public BossModeConfig getBossModeConfig() {
        return bossModeConfig;
    }

    public BaseSkillInfo getBaseSkillInfo(String skillID){
        if(heroSkillConfig.readMap().containsKey(skillID)){
            return heroSkillConfig.readMap().get(skillID);
        }

        //System.out.println("SKILL NULL 1 "  + skillID);
        return null;
    }

    public SkillDesc getSkillDesc(String skillID){
        for(SkillDesc skillDesc : heroSkillDescConfig.list){
            if(skillDesc.skillID.equalsIgnoreCase(skillID)){
                return skillDesc;
            }
        }

        //System.out.println("SKILL NULL 2 "  + skillID);
        return null;
    }

    public BaseSkillInfo getBaseSkillInfoCelestial(String skillID){
        for(BaseSkillInfo baseSkillInfo : celestialSkillConfig.list){
            if(baseSkillInfo.id.equalsIgnoreCase(skillID)){
                return baseSkillInfo;
            }
        }

        //System.out.println("SKILL NULL 3 "  + skillID);
        return null;
    }

    public SkillDesc getSkillDescCelestial(String skillID){
        for(SkillDesc skillDesc : celestialSkillDescConfig.list){
            if(skillDesc.skillID.equalsIgnoreCase(skillID)){
                return skillDesc;
            }
        }

        //System.out.println("SKILL NULL 4 "  + skillID);
        return null;
    }

    public BaseSkillInfo getBaseSkillInfoCreep(String skillID){
        for(BaseSkillInfo baseSkillInfo : creepSkillConfig.list){
            if(baseSkillInfo.id.equalsIgnoreCase(skillID)){
                return baseSkillInfo;
            }
        }

        //System.out.println("SKILL NULL 5 "  + skillID);
        return null;
    }

    public SkillDesc getSkillDescCreep(String skillID){
        for(SkillDesc skillDesc : creepSkillDescConfig.list){
            if(skillDesc.skillID.equalsIgnoreCase(skillID)){
                return skillDesc;
            }
        }

        //System.out.println("SKILL NULL 6 "  + skillID);
        return null;
    }

    public BaseSkillInfo getBaseSkillInfoOther(String skillID){
        for(BaseSkillInfo baseSkillInfo : otherSkillConfig.list){
            if(baseSkillInfo.id.equalsIgnoreCase(skillID)){
                return baseSkillInfo;
            }
        }

        //System.out.println("SKILL NULL 7 "  + skillID);
        return null;
    }

    public SkillDesc getSkillDescOther(String skillID){
        for(SkillDesc skillDesc : otherSkillDescConfig.list){
            if(skillDesc.skillID.equalsIgnoreCase(skillID)){
                return skillDesc;
            }
        }

        //System.out.println("SKILL NULL 8 "  + skillID);
        return null;
    }

    public SkillDesc getSkillDescSage(String skillID){
        for(SkillDesc skillDesc : sageSkillDescConfig.list){
            if(skillDesc.skillID.equalsIgnoreCase(skillID)){
                return skillDesc;
            }
        }

        //System.out.println("SKILL NULL 9 "  + skillID);
        return null;
    }

    public SageSkillVO getSageSkill(String skillID){
        for(SageSkillVO vo : sageSkillTreeConfig.skills){
            if(vo.id.equals(skillID)) return vo;
        }

        //System.out.println("SKILL NULL 10 "  + skillID);
        return null;
    }

    public List<WinCondition> getWinCodition(){
        return winCoditionConfig.coditions;
    }
    public WinCondition getWinCodition(String id) {
        for (WinCondition winCodition : winCoditionConfig.coditions){
            if(winCodition.id.equalsIgnoreCase(id)) return winCodition;
        }
        return null;
    }

    public TeamEffect getTeamEffect(int id) {
        for(TeamEffect teamEffect : teamEffectConfig.kingdom){
            if(teamEffect.id == id){
                return teamEffect;
            }
        }

        return null;
    }

    public String getRandomSkillByGroup(String group) {
        return heroSkillConfig.getMapGroup().get(group).get(Utils.randomInRange(0, heroSkillConfig.getMapGroup().get(group).size() - 1)).id;
    }

    public List<SkillInfo> generateSkillHero(List<String> skillsConfig) {
        List<String> skills = new ArrayList<>();
        for (String skillID : skillsConfig) {
            if(skillID.startsWith("G")){
                String skillRandom = this.getRandomSkillByGroup(skillID);
                while (skills.contains(skillRandom)) {
                    skillRandom = this.getRandomSkillByGroup(skillID);
                }
                skills.add(skillRandom);
            }else {
                skills.add(skillID);
            }
        }

        List<SkillInfo> skillInfos = new ArrayList<>();
        for (String skillID : skills) {
            BaseSkillInfo baseSkillInfo = SkillConfigManager.getInstance().getBaseSkillInfo(skillID);
            SkillDesc skillDesc = SkillConfigManager.getInstance().getSkillDesc(skillID);
            if (baseSkillInfo == null) {
                return null;
            }

            skillInfos.add(new SkillInfo(baseSkillInfo.id, 1));
        }
        return skillInfos;
    }

    public List<SkillInfo> generateSkillHeroNFT(List<String> skillsConfig) {
        List<String> skills = new ArrayList<>();
        for (String skillID : skillsConfig) {
            if(skillID.startsWith("G")){
                String skillRandom = this.getRandomSkillByGroup(skillID);
                while (skills.contains(skillRandom)) {
                    skillRandom = this.getRandomSkillByGroup(skillID);
                }
                skills.add(skillRandom);
            }else {
                skills.add(skillID);
            }
        }

        List<SkillInfo> skillInfos = new ArrayList<>();
        for (String skillID : skills) {
            BaseSkillInfo baseSkillInfo = SkillConfigManager.getInstance().getBaseSkillInfo(skillID);
            SkillDesc skillDesc = SkillConfigManager.getInstance().getSkillDesc(skillID);
            if (baseSkillInfo == null) {
                return null;
            }

            skillInfos.add(new SkillInfo(baseSkillInfo.id, Utils.randomInRange(1, skillDesc.templateProps.size())));
        }
        return skillInfos;
    }
}
