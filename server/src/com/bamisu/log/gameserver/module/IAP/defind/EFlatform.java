package com.bamisu.log.gameserver.module.IAP.defind;

import com.bamisu.gamelib.entities.ServerConstant;

public enum EFlatform {
    ANDROID(ServerConstant.IAPGate.GOOGLE_PLAY, "android"),
    IOS(ServerConstant.IAPGate.APPLE_STORE, "ios"),
    MENA(ServerConstant.IAPGate.MENA, "mena");

    String strValue;
    int intValue;

    public String getStrValue() {
        return strValue;
    }

    public int getIntValue() {
        return intValue;
    }

    EFlatform(int intValue, String strtValue) {
        this.intValue = intValue;
        this.strValue = strtValue;
    }

    public static EFlatform fromStrValue(String strValue){
        for(EFlatform flatform : EFlatform.values()){
            if(flatform.strValue.toLowerCase().equals(strValue.toLowerCase())){
                return flatform;
            }
        }
        return null;
    }

    public static EFlatform fromIntValue(int intValue){
        for(EFlatform flatform : EFlatform.values()){
            if(flatform.intValue == intValue){
                return flatform;
            }
        }
        return null;
    }
}
