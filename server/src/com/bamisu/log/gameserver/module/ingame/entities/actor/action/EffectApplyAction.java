package com.bamisu.log.gameserver.module.ingame.entities.actor.action;

import com.bamisu.gamelib.entities.Attr;
import com.bamisu.log.gameserver.module.ingame.entities.actor.Action;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActionID;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.effect.Effect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EffectCategory;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.skill.SkillUtils;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 5:15 PM, 5/6/2020
 */

/**
 * 0: effetc id
 * 1: turns
 */
public class EffectApplyAction extends Action {
    public Character sourceActor;
    public List<Object> otherProps;
    public EEffect effectEnum;
    public int turn;
    public boolean isDisplay;
    public EffectApplyAction(ActionID actionID, List<Object> props) {
        super(actionID, props);
    }

    public EffectApplyAction(Character sourceActor, EEffect effectEnum, int turn, List<Object> otherProps, boolean isDisplay) {
        super(ActionID.APPLY_EFFECT, new ArrayList<>());
        this.sourceActor = sourceActor;
        this.isDisplay = isDisplay;
        this.effectEnum = effectEnum;
        this.turn = turn;
        this.otherProps = otherProps;
    }

    @Override
    public List<ActionResult> run() {
        if(getActor().getType() == ECharacterType.Sage || getActor().getType() == ECharacterType.Celestial) {
//            return new ArrayList<>();
            if(effectEnum == EEffect.Stat_Buff || effectEnum == EEffect.Stat_Debuff) {
                Attr attr = Attr.fromStrValue(this.otherProps.get(0).toString());
                getActor().applyEffect(Effect.create(
                        sourceActor,
                        effectEnum,
                        turn,
                        Arrays.asList(
                                attr.shortName(),
                                String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(this.otherProps.get(1).toString(), this.sourceActor, null))),    //rate
                                String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(this.otherProps.get(2).toString(), this.sourceActor, null))) //max rate
                        ),
                        isDisplay));
            }
        }

        List<ActionResult> results = new ArrayList<>();

        //ko bị hiệu ứng
        if ((getActor().getMaster().getFightingManager().function == EFightingFunction.DARK_REALM) && getActor().getType() == ECharacterType.MiniBoss) {
            if(effectEnum.getCategory() == EffectCategory.HARD){
                return results;
            }
        }

        // miễn nhiễm hiệu ứng xấu
        if(effectEnum.isNegative()){
            if(!getActor().canApplyNegativeEffect()){
                return results;
            }
        }

        if(isDisplay){
            //nội tại miễn nhiễm hiệu ứng
            if(getActor().getSkill(0).getTamplatePropsAsPassive().immunity != null){
                if(getActor().getSkill(0).getTamplatePropsAsPassive().immunity.equalsIgnoreCase("all")){
                    return results;
                }

                if(effectEnum.getCategory() == EffectCategory.fromName(getActor().getSkill(0).getTamplatePropsAsPassive().immunity)){
                    return results;
                }
            }
        }

        if(effectEnum == EEffect.CLEAN_ALL_SE){
            if(!getActor().canUltimate()){
                //System.out.println();
            }
            getActor().clearAllDisplayEffect();
            ActionResult actionResultApplyEffect = new ActionResult();
            actionResultApplyEffect.actor = getActor().getActorID();
            actionResultApplyEffect.id = getActionID().getIntValue();
            actionResultApplyEffect.addProp(effectEnum.getID());
            results.add(actionResultApplyEffect);

            return results;
        }

        if(effectEnum == EEffect.CLEAN_ALL_SE_NEGATIVE){
            if(!getActor().canUltimate()){
                //System.out.println();
            }
            getActor().clearAllEffectNegative();
            ActionResult actionResultApplyEffect = new ActionResult();
            actionResultApplyEffect.actor = getActor().getActorID();
            actionResultApplyEffect.id = getActionID().getIntValue();
            actionResultApplyEffect.addProp(effectEnum.getID());
            results.add(actionResultApplyEffect);

            return results;
        }

        if(effectEnum == EEffect.Stat_Buff || effectEnum == EEffect.Stat_Debuff) {
            Attr attr = Attr.fromStrValue(this.otherProps.get(0).toString());
            getActor().applyEffect(Effect.create(
                    sourceActor,
                    effectEnum,
                    turn,
                    Arrays.asList(
                            attr.shortName(),
                            String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(this.otherProps.get(1).toString(), this.sourceActor, null))),    //rate
                            String.valueOf(Utils.calculationFormula(SkillUtils.fillDataToFormula(this.otherProps.get(2).toString(), this.sourceActor, null))) //max rate
                    ),
                    isDisplay));
        }
        else if(effectEnum == EEffect.Bleed || effectEnum == EEffect.Poisoned){
            getActor().applyEffect(Effect.create(
                    sourceActor,
                    effectEnum,
                    turn,
                    Arrays.asList(
                            this.otherProps.get(0).toString()
                    ), isDisplay));
        }
        else if(effectEnum == EEffect.Invigorated){
            getActor().applyEffect(Effect.create(
                    sourceActor,
                    effectEnum,
                    turn,
                    Arrays.asList(
                            this.otherProps.get(0).toString()
                    ), isDisplay));
        }
        else if(effectEnum == EEffect.Immortal){
            getActor().applyEffect(Effect.create(
                    sourceActor,
                    effectEnum,
                    turn,
                    Arrays.asList(
                            this.otherProps.get(0).toString(),   //số máu hồi sau khi hết hiệu ứng
                            this.otherProps.get(1).toString()   //số máu mà người buff đc hồi khi người nhận buff mất máu
                    ), isDisplay));
        }
        else if(effectEnum == EEffect.Immunity){
            getActor().applyEffect(Effect.create(
                    sourceActor,
                    effectEnum,
                    turn,
                    Arrays.asList(
                            this.otherProps.get(0).toString(),   //số máu hồi sau khi hết hiệu ứng
                            this.otherProps.get(1).toString()   //số máu mà người buff đc hồi khi người nhận buff mất máu
                    ), isDisplay));
        }
        else {
            getActor().applyEffect(Effect.create(
                    sourceActor,
                    effectEnum,
                    turn,
                    Arrays.asList(), isDisplay));
        }

        ActionResult actionResultApplyEffect = new ActionResult();
        actionResultApplyEffect.actor = getActor().getActorID();
        actionResultApplyEffect.id = getActionID().getIntValue();
        actionResultApplyEffect.addProp(effectEnum.getID());

        if(effectEnum == EEffect.Stat_Buff || effectEnum == EEffect.Stat_Debuff){
            Attr attr = Attr.fromStrValue(this.otherProps.get(0).toString());
            actionResultApplyEffect.addProp(attr.shortName());
        }
        if(isDisplay){
            results.add(actionResultApplyEffect);
        }
        return results;
    }
}
