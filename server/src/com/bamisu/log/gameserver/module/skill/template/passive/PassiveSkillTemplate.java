package com.bamisu.log.gameserver.module.skill.template.passive;

import com.bamisu.log.gameserver.module.ingame.entities.MatchState;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action.SkillingAction;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.log.gameserver.module.skill.template.SkillTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:39 PM, 5/20/2020
 */
public class PassiveSkillTemplate extends SkillTemplate {
    @Override
    public List<ActionResult> doSkill(MatchState state, Skill skill, Character actor, boolean rootSkill, boolean isPassive, boolean isCrit) {
        PassiveSkillTemplateProps props = skill.getTamplatePropsAsPassive();
        List<ActionResult> actionResults = new ArrayList<>();

        if (state == MatchState.BEFOTURNING) {
            if (actor.canSkillPassive() && props.skillingBefoTurn != null) {
                Skill activeSkill = actor.getOtherSkill(props.skillingBefoTurn);
                actionResults.addAll(actor.action(new SkillingAction(null, activeSkill, 0,true, true, false)));
            }
        }

        if(state == MatchState.ON_END_SKILL){

            //passive
            if(actor.canSkillPassive()) {
                if (props.skillingOnAllyDie != null) {
                    Skill activeSkill = actor.getOtherSkill(props.skillingOnAllyDie);

                    //find target
                    List<Character> targets = new ArrayList<>();
                    for (TeamSlot teamSlot : actor.getMaster().getTeam()) {
                        //điều kiện được hồi sinh
                        if(actor.getCharacterVO().readID().equalsIgnoreCase("MBS1003") || actor.getCharacterVO().readID().equalsIgnoreCase("MBS1004")){    //Evil Ektar
                            if (teamSlot.haveCharacter() && !teamSlot.getCharacter().isLive()) {
                                targets.add(teamSlot.getCharacter());
                            }
                        }else {
                            if (teamSlot.haveCharacter() && !teamSlot.getCharacter().isLive() && teamSlot.getCharacter().changeReturnTime()) {
                                targets.add(teamSlot.getCharacter());
                            }
                        }

                    }
                    if (targets.isEmpty()) return new ArrayList<>();

                    activeSkill.setCustomTargert(targets);
                    actionResults.addAll(actor.action(new SkillingAction(null, activeSkill, 0, true, true, false)));
                }
            }
        }
        return actionResults;
    }
}
