package com.bamisu.log.gameserver.module.ingame.entities.skill;

import com.bamisu.log.gameserver.module.ingame.entities.Diamond;

/**
 * Create by Popeye on 10:18 AM, 2/21/2020
 */
public class Combo {
    public Diamond diamond;
    public int count;

    public Combo() {
    }

    public Combo(int diamondID, int count) {
        this.count = count;
        this.diamond = Diamond.fromID(diamondID);
    }

    public Combo(Diamond diamond, int count) {
        this.diamond = diamond;
        this.count = count;
    }

    public ComboType getComboType(){
        if(count < 5) return ComboType.SKILL2;
        return ComboType.SKILL3;
    }

    public int getRate(){
        if(getComboType() == ComboType.SKILL2){
            if(count < 3){
                return count * 25;
            }

            if(count == 3){
                return 100;
            }

            if(count > 3){
                return 100 + 25 * (count - 3);
            }
        }

        if(getComboType() == ComboType.SKILL3){
            if(count < 5){
                return count * 25;
            }

            if(count == 5){
                return 100;
            }

            if(count > 5){
                return 100 + 25 * (count - 5);
            }
        }

        return 0;
    }

    public int getRate(ComboType type){
        if(type == getComboType()){
            return getRate();
        }

        if(type == ComboType.SKILL3){
            return Math.floorDiv(getRate(), 2);
        }

        if(type == ComboType.SKILL2){
            return getRate() * 2;
        }

        return 0;
    }
}
