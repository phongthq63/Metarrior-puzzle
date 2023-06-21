package com.bamisu.log.gameserver.module.ingame.entities.wincodition;

import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;

/**
 * Create by Popeye on 3:24 PM, 5/19/2020
 */
public enum EFightingResult {
    WIN(0),
    LOSE(1),
    TIE(2);

    int intValue;

    EFightingResult(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public EFightingResult fromIntValue(int intValue){
        for(EFightingResult eFightingResult : values()){
            if(eFightingResult.getIntValue() == intValue){
                return eFightingResult;
            }
        }

        return null;
    }
}
