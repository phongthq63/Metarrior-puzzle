package com.bamisu.gamelib.iap;

/**
 * Create by Popeye on 1:59 PM, 5/5/2020
 */
public class IAPVerifyResult {
    public int errorCode = 0;
    public String message = "No error!";
    public String orderId = "";
    public long purchaseTimeMillis = 0;
    public int purchaseType = 0;
    public int purchaseState = 0;

    @Override
    public String toString() {
        return "IAPVerifyResult{" +
                "errorCode=" + errorCode +
                ", message='" + message + '\'' +
                ", orderId='" + orderId + '\'' +
                ", purchaseTimeMillis=" + purchaseTimeMillis +
                ", purchaseType=" + purchaseType +
                ", purchaseState=" + purchaseState +
                '}';
    }

    public boolean isSuccess(){
        return errorCode == 0;
    }
}
