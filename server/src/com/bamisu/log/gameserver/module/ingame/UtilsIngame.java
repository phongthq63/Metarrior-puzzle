package com.bamisu.log.gameserver.module.ingame;

import com.bamisu.log.gameserver.module.ingame.entities.character.CelestialIngame;
import com.bamisu.log.gameserver.module.ingame.entities.character.SageIngame;
import com.bamisu.log.gameserver.module.ingame.entities.effect.Effect;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.*;

/**
 * Create by Popeye on 9:38 AM, 2/12/2020
 */
public class UtilsIngame {
    public static void putSlotData(SFSObject sfsSlot, TeamSlot teamSlot) {
        if(teamSlot.getCharacter() == null) {
            sfsSlot.putBool(Params.IS_EMPTY, true);
            return;
        }

        sfsSlot.putBool(Params.IS_EMPTY, false);
        sfsSlot.putInt(Params.CHAR_TYPE, teamSlot.getCharacter().getType().getType());
        try {
            sfsSlot.putUtfString(Params.ELEMENT, teamSlot.getCharacter().getElement().getId());
            sfsSlot.putUtfString(Params.KINGDOM, teamSlot.getCharacter().getKingdom().getId());
        }catch (Exception ex){
            ex.printStackTrace();
        }

        sfsSlot.putUtfString(Params.ID, teamSlot.getCharacter().getID());
        sfsSlot.putInt(Params.LEVEL, teamSlot.getCharacter().getLevel());
        sfsSlot.putInt(Params.STAR, teamSlot.getCharacter().getStar());
        sfsSlot.putUtfString(Params.ACTOR_ID, teamSlot.getCharacter().getActorID());
        sfsSlot.putBool(Params.BOSS_MODE, teamSlot.getCharacter().isBossMode());

        sfsSlot.putInt(Params.MAX_HP, teamSlot.getCharacter().getMaxHP());
        sfsSlot.putInt(Params.CURRENT_HP, teamSlot.getCharacter().getCurrentHP());
        sfsSlot.putInt(Params.MAX_EP, teamSlot.getCharacter().getMaxEP());
        sfsSlot.putInt(Params.CURRENT_EP, teamSlot.getCharacter().getCurrentEP());
        sfsSlot.putInt(Params.SHIELD, teamSlot.getCharacter().getShieldAll());
        sfsSlot.putUtfStringArray(Params.EFFECT, packEffect(teamSlot));

        switch (teamSlot.getCharacter().getType()){
            case Hero:
                break;
            case Creep:
                break;
        }
    }

    private static Collection<String> packEffect(TeamSlot teamSlot) {
        List<String> effectList = new ArrayList<>();
        for(Effect effect : teamSlot.getCharacter().getEffectList()){
            if(effect.isDisplay){
                effectList.add(effect.getID());
            }
        }
        return effectList;
    }

    public static void putSage(SFSObject sfsPlayer, SageIngame sage) {
        ISFSObject sageData = new SFSObject();
        ISFSArray activeSkillArray = new SFSArray();
        ISFSArray ultilmateSkillArray = new SFSArray();
        for(Skill skill : sage.getSkills()){
            //ultilmate
            if(sage.getSkills().indexOf(skill) == 0){
                ultilmateSkillArray.addSFSObject(SFSObject.newFromJsonData(Utils.toJson(skill.getSkillInfo())));
            }else {
                ISFSObject activeSkill = SFSObject.newFromJsonData(Utils.toJson(skill.getSkillInfo()));
                activeSkill.putInt(Params.MAX_MANA, skill.getSkillBaseInfo().mana);
                activeSkill.putInt(Params.CURRENT_MANA, sage.getCurrentEPActiveSkill(skill.getSkillBaseInfo().id));
                activeSkillArray.addSFSObject(activeSkill);
            }
        }
        sageData.putSFSArray(Params.ACTIVE_SKILLS, activeSkillArray);
        sageData.putSFSArray(Params.ULTILMATE_SKILLS, ultilmateSkillArray);

        sageData.putInt(Params.MAX_EP, sage.getMaxEP());
        sageData.putInt(Params.CURRENT_EP, sage.getCurrentEP());
        sageData.putUtfString(Params.ACTOR_ID, sage.getActorID());

        sfsPlayer.putSFSObject(Params.SAGE, sageData);
    }

    public static void putCelestial(SFSObject sfsPlayer, CelestialIngame celestialIngame) {
        ISFSObject celestialData = new SFSObject();
        ISFSArray ultilmateSkillArray = new SFSArray();
        for(Skill skill : celestialIngame.getSkills()){
            //ultilmate
            if(celestialIngame.getSkills().indexOf(skill) == 0){
                ultilmateSkillArray.addSFSObject(SFSObject.newFromJsonData(Utils.toJson(skill.getSkillInfo())));
            }
        }
        celestialData.putSFSArray(Params.ULTILMATE_SKILLS, ultilmateSkillArray);

        celestialData.putUtfString(Params.ID, celestialIngame.getID());
        celestialData.putInt(Params.MAX_EP, celestialIngame.getMaxEP());
        celestialData.putInt(Params.CURRENT_EP, celestialIngame.getCurrentEP());
        celestialData.putUtfString(Params.ACTOR_ID, celestialIngame.getActorID());

        sfsPlayer.putSFSObject(Params.CELESTIAL, celestialData);
    }
}
