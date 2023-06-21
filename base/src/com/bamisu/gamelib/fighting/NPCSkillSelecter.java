package com.bamisu.gamelib.fighting;

import com.bamisu.gamelib.entities.LIZRandom;
import com.bamisu.gamelib.entities.RandomObj;

/**
 * Create by Popeye on 6:11 PM, 2/4/2021
 */
public class NPCSkillSelecter {
    private static NPCSkillSelecter ourInstance = new NPCSkillSelecter();

    LIZRandom npcSkillSelecter;

    public static NPCSkillSelecter getInstance() {
        return ourInstance;
    }

    private NPCSkillSelecter() {
        npcSkillSelecter = new LIZRandom();
        npcSkillSelecter.push(new RandomObj("min", 8));
        npcSkillSelecter.push(new RandomObj("maj", 2));
        npcSkillSelecter.push(new RandomObj("max", 0));
    }

    public LIZRandom getNpcSkillSelecter() {
        return npcSkillSelecter;
    }

    public String randomSkill() {
        return String.valueOf(npcSkillSelecter.next().value);
    }
}
