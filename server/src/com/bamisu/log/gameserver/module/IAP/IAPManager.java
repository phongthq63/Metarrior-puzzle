package com.bamisu.log.gameserver.module.IAP;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.iap.IAPController;
import com.bamisu.gamelib.iap.IAPVerifyResult;
import com.bamisu.gamelib.iap.MenaIAPVerify;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.module.IAP.defind.EFlatform;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyHandler;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateIAP;
import com.smartfoxserver.v2.entities.Zone;
import org.apache.thrift.TException;

public class IAPManager {
    private static IAPManager ourInstance = new IAPManager();

    public static IAPManager getInstance() {
        return ourInstance;
    }

    private IAPManager() { }



    /*-----------------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------------*/
    /**
     * Kiem tra da mua IAP
     * @param uid
     * @param purchaseToken
     * @param zone
     * @return
     */
    private boolean haveBoughtIAP(long uid, String purchaseToken, Zone zone){
        UserModel userModel = ((IAPBuyHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_IAP_STORE)).getUserModel(uid);

        try {
            return SDKGateIAP.haveInstanceBuyIAP(userModel.accountID, purchaseToken);
        } catch (TException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Luu IAP da mua vao data
     * @param uid
     * @param purchaseToken
     * @param zone
     * @return
     */
    public void boughtIAP(long uid, String purchaseToken, Zone zone){
        UserModel userModel = ((IAPBuyHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_IAP_STORE)).getUserModel(uid);

        try {
            SDKGateIAP.haveInstanceBuyIAP(userModel.accountID, purchaseToken);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    /**
     * Kiem tra da thanh toan chua
     * @return
     */
    public boolean checkPaymentIAP(long uid, EFlatform flatform, String packageName, String productId, String purchaseToken, Zone zone){
        if(flatform == null) return false;
        //Kiem tra thanh toan thanh cong
        switch (flatform){
            case ANDROID:
                //Kiem tra da tung thanh toan chua
                if(haveBoughtIAP(uid, purchaseToken, zone)) return false;
                return IAPController.getInstance().verifyAndroid(packageName, productId, purchaseToken).isSuccess();
            case IOS:
                //Doi voi ios p kiem tra check orderId qua purchaseToken trc
                IAPVerifyResult iapVerifyResult = IAPController.getInstance().verifyIOS(packageName, productId, purchaseToken);
                if(!iapVerifyResult.isSuccess()) return false;
                //Kiem tra da tung thanh toan chua
                return !haveBoughtIAP(uid, iapVerifyResult.orderId, zone);
            case MENA:
                //Kiem tra da tung thanh toan chua
                if(haveBoughtIAP(uid, purchaseToken, zone)) return false;
                return IAPController.getInstance().verifyMena(packageName, productId, purchaseToken).isSuccess();
        }
        return false;
    }
}
