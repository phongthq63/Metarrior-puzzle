package com.bamisu.gamelib.iap;

/**
 * Create by Popeye on 1:58 PM, 5/5/2020
 */
public interface IIAPController {
    IAPVerifyResult verifyAndroid(String packageName, String productId, String purchaseToken);

    IAPVerifyResult verifyIOS(String packageName, String productId, String purchaseToken);

    IAPVerifyResult verifyMena(String packageName, String productId, String purchaseToken);
}
