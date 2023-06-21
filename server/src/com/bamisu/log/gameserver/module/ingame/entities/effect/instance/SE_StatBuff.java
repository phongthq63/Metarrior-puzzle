package com.bamisu.log.gameserver.module.ingame.entities.effect.instance;

import com.bamisu.gamelib.entities.Attr;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.Effect;

import java.util.List;

/**
 * Create by Popeye on 10:20 AM, 5/27/2020
 */
public class SE_StatBuff extends Effect {
    public Attr attr;
    public float rate;  //%

    public SE_StatBuff(Character sourceActor, EEffect type, int turn, List<String> props, boolean isDisplay) {
        super(sourceActor, type, turn, props, isDisplay);
        this.attr = Attr.fromStrValue(props.get(0));
        this.rate = Float.parseFloat(props.get(1));
        float maxRate = Float.parseFloat(props.get(2));
        if(this.rate > maxRate) rate = maxRate;
    }

    @Override
    public String getID() {
        return type.getID() + "_" + attr.shortName();
    }
}
