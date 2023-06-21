package com.bamisu.log.gameserver.module.ingame.entities.effect.instance;

import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.Effect;

import java.util.List;

/**
 * Create by Popeye on 4:06 PM, 7/2/2020
 */
public class SE_Poisoned extends Effect {
    public float rate;  //% mau mat moi turn

    public SE_Poisoned(Character sourceActor, EEffect type, int turn, List<String> props, boolean isDisplay) {
        super(sourceActor, type, turn, props, isDisplay);
        this.rate = Float.parseFloat(props.get(0));
    }
}
