package com.bamisu.gamelib.entities;

/**
 * Create by Popeye on 5:33 PM, 4/8/2020
 */
public enum Gender {
    MALE(0),
    FEMALE(1);

    short shortValue;

    Gender(int shortValue){
        this.shortValue = (short) shortValue;
    }

    public short getshortValue() {
        return shortValue;
    }

    public static Gender fromshortValue(short shortValue){
        for(Gender gender : values()){
            if(gender.getshortValue() == shortValue){
                return gender;
            }
        }

        return null;
    }
}
