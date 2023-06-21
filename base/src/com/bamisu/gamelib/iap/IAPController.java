package com.bamisu.gamelib.iap;

/**
 * Create by Popeye on 1:58 PM, 5/5/2020
 */
public class IAPController implements IIAPController {
    private static IAPController ourInstance = new IAPController();

    public static IAPController getInstance() {
        return ourInstance;
    }

    private IAPController() {
    }

    /**
     *
     * @param packageName
     * @param productId
     * @param purchaseToken
     * @return
     */
    @Override
    public IAPVerifyResult verifyAndroid(String packageName, String productId, String purchaseToken) {
        return AndroidIAPVerify.getInstance().verify(packageName, productId, purchaseToken);
    }

    @Override
    public IAPVerifyResult verifyIOS(String packageName, String productId, String purchaseToken) {
        return IOSIAPVerify.getInstance().verify(packageName, productId, purchaseToken);
    }

    @Override
    public IAPVerifyResult verifyMena(String packageName, String productId, String purchaseToken) {
        return MenaIAPVerify.getInstance().verify(packageName, productId, purchaseToken);
    }
}
