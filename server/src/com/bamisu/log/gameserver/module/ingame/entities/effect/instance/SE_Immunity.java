package com.bamisu.log.gameserver.module.ingame.entities.effect.instance;

import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.Effect;

import java.util.List;

/**
 * Create by Popeye on 4:33 PM, 2/24/2021
 */
public class SE_Immunity extends Effect {
    public float health;  //% máu hồi khi hết hiệu ứng %HP
    public float healthForSourceActor;   //hồi máu cho source actor khi người được bảo vệ mất máu

    public SE_Immunity(Character sourceActor, EEffect type, int turn, List<String> props, boolean isDisplay) {
        super(sourceActor, type, turn, props, isDisplay);
        this.health = Float.parseFloat(props.get(0));
        this.healthForSourceActor = Float.parseFloat(props.get(1));
    }
}
