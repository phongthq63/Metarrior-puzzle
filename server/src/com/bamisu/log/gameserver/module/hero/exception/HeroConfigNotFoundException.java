package com.bamisu.log.gameserver.module.hero.exception;

import com.bamisu.gamelib.base.excepions.BaseServerException;

/**
 * Create by Popeye on 10:24 AM, 12/25/2019
 */
public class HeroConfigNotFoundException extends BaseServerException {
    public HeroConfigNotFoundException() {
        super((short) -1, "hero config not found");
    }
}
