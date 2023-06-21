package com.bamisu.log.gameserver.module.ingame.entities.effect;

import com.couchbase.client.core.message.stat.Stat;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.*;

import java.util.List;

/**
 * Create by Popeye on 3:56 PM, 2/18/2020
 */
public class Effect {
    public EEffect type;
    public List<String> props;
    public int turn = 1;
    public boolean isDisplay;
    public Character sourceActor;

    public Effect() {
    }

    public Effect(Character sourceActor, EEffect type, int turn, List<String> props, boolean isDisplay) {
        this.type = type;
        this.turn = turn;
        this.props = props;
        this.isDisplay = isDisplay;
        this.sourceActor = sourceActor;
    }

    public static Effect create(Character sourceActor, EEffect type, int turn, List<String> props, boolean isDisplay){
        switch (type){
            case Stat_Buff:
                return new SE_StatBuff(sourceActor, type, turn, props, isDisplay);
            case Stat_Debuff:
                return new SE_StatDebuff(sourceActor, type, turn, props, isDisplay);
            case Poisoned:
                return new SE_Poisoned(sourceActor, type, turn, props, isDisplay);
            case Bleed:
                return new SE_Bleed(sourceActor, type, turn, props, isDisplay);
            case Invigorated:
                return new SE_Invigorated(sourceActor, type, turn, props, isDisplay);
            case Immortal:
                return new SE_Immortal(sourceActor, type, turn, props, isDisplay);
            case Immunity:
                return new SE_Immunity(sourceActor, type, turn, props, isDisplay);
        }

        return new Effect(sourceActor, type, turn, props, isDisplay);
    }

    public boolean same(Effect effect) {
        return getID().equalsIgnoreCase(effect.getID());
    }

    public EffectCategory getCategory(){
        return type.getCategory();
    }

    public EEffect getType() {
        return type;
    }

    public void setType(EEffect type) {
        this.type = type;
    }

    public String getID(){
        return type.getID();
    }

    /**
     * check xem có phải hiệu ứng có hại không
     * @return
     */
    public boolean isNegative() {
        return getType().isNegative();
    }

    public Character getSourceActor() {
        return sourceActor;
    }
}
