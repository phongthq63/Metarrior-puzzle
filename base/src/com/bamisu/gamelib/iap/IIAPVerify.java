package com.bamisu.gamelib.iap;

/**
 * Create by Popeye on 2:00 PM, 5/5/2020
 */
public interface IIAPVerify {
    IAPVerifyResult verify(String packageName, String productId, String purchaseToken);
}
