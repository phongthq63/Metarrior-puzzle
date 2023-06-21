package com.bamisu.log.gameserver.module.ingame.entities.character;

import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;

/**
 * Create by Popeye on 4:49 PM, 1/15/2020
 */
public class BossIngame extends Character {
    public BossIngame(BasePlayer master) {
        super(master);
        this.setType(ECharacterType.Boss);
    }

    @Override
    public void initSkill() {

    }
}
