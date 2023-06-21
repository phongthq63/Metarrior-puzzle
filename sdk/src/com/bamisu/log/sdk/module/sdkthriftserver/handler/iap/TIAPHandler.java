package com.bamisu.log.sdk.module.sdkthriftserver.handler.iap;

import com.bamisu.log.sdk.module.iap.IAPManager;
import com.bamisu.log.sdkthrift.exception.ThriftSVException;
import com.bamisu.log.sdkthrift.service.iap.IAPService;
import org.apache.thrift.TException;

public class TIAPHandler implements IAPService.Iface {

    @Override
    public boolean haveInstanceBuyIAP(String accountID, String purchaseToken) throws ThriftSVException, TException {
        return IAPManager.getInstance().haveInstanceBuyIAP(accountID, purchaseToken);
    }

    @Override
    public boolean saveInstanceBuyIAP(String accountID, String purchaseToken) throws ThriftSVException, TException {
        try{
            IAPManager.getInstance().saveInstanceBuyIAP(accountID, purchaseToken);
            return true;

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
