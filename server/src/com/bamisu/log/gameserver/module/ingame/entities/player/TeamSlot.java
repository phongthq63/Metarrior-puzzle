package com.bamisu.log.gameserver.module.ingame.entities.player;

import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action.SkillingAction;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.skill.Skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 4:01 PM, 2/17/2020
 */
public class TeamSlot {
    private Character character;
    private BasePlayer player;

    public TeamSlot(Character character) {
        this.character = character;
        this.character.setTeamSlot(this);
        this.player = character.getMaster();
    }

    public TeamSlot(BasePlayer player) {
        this.player = player;
    }

    public BasePlayer getPlayer() {
        return player;
    }

    public void setPlayer(BasePlayer player) {
        this.player = player;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public boolean haveCharacter() {
        return character != null;
    }

    public void clear() {
        character = null;
    }

    public void initBefoMatch() {
        if (character != null) {
            character.initBefoFighting();
        }
    }

    public void initHP() {
        if (character != null) {
            character.initHP();
        }
    }

    /**
     * đánh skill với % damage
     *
     * @param skillIndex
     * @param rate
     * @return
     */
    public List<ActionResult> doSkill(int skillIndex, int rate, boolean isPassive, boolean isCrit) {
        //
        if (!haveCharacter()) {
            return null;
        }
        List<ActionResult> actions = new ArrayList<>();

        //xác định skill sẽ đánh
        Skill skill = null;
        try {
            skill = getCharacter().getSkill(skillIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //character doSkill action
        actions.addAll(getCharacter().action(new SkillingAction(null, skill, rate, true, isPassive, isCrit)));
        return actions;
    }

    public List<ActionResult> doSkill(int skillIndex, int rate, boolean isPassive) {
        return doSkill(skillIndex, rate, isPassive, false);
    }

    public int getPos() {
        return player.team.indexOf(this);
    }

    public boolean isNear(TeamSlot teamSlot) {
        switch (getPos()) {
            case 0:
                if (teamSlot.getPos() == 1) return true;
                if (teamSlot.getPos() == 3) return true;
                return false;
            case 1:
                if (teamSlot.getPos() == 0) return true;
                if (teamSlot.getPos() == 2) return true;
                if (teamSlot.getPos() == 3) return true;
                if (teamSlot.getPos() == 4) return true;
                return false;
            case 2:
                if (teamSlot.getPos() == 1) return true;
                if (teamSlot.getPos() == 4) return true;
                return false;
            case 3:
                if (teamSlot.getPos() == 0) return true;
                if (teamSlot.getPos() == 1) return true;
                if (teamSlot.getPos() == 4) return true;
                return false;
            case 4:
                if (teamSlot.getPos() == 1) return true;
                if (teamSlot.getPos() == 2) return true;
                if (teamSlot.getPos() == 3) return true;
                return false;
        }
        return false;
    }
}
