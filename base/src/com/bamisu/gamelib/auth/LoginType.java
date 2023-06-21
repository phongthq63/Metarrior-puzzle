package com.bamisu.gamelib.auth;

/**
 * Create by Popeye on 8:10 PM, 4/23/2020
 */
public enum LoginType {
    GUESST(0),
    TOKEN(1),
    USERNAME(2);

    LoginType(int intValue) {
        this.intValue = intValue;
    }

    private int intValue;

    public int getIntValue() {
        return intValue;
    }

    public static LoginType fromInt(int intValue){
        for(LoginType loginType : LoginType.values()){
            if(loginType.getIntValue() == intValue){
                return loginType;
            }
        }

        return null;
    }
}
