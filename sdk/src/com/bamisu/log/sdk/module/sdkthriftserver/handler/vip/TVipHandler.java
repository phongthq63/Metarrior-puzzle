package com.bamisu.log.sdk.module.sdkthriftserver.handler.vip;

import com.bamisu.log.sdk.module.vip.VipManager;
import com.bamisu.log.sdkthrift.exception.ThriftSVException;
import com.bamisu.log.sdkthrift.service.vip.VipService;
import com.bamisu.gamelib.entities.VipDataToSend;
import com.bamisu.gamelib.utils.Utils;
import org.apache.thrift.TException;

/**
 * Create by Popeye on 10:34 AM, 7/14/2020
 */
public class TVipHandler implements VipService.Iface {
    @Override
    public String getVip(String accountID) throws ThriftSVException, TException {
        return Utils.toJson(VipManager.getInstance().getVipData(accountID));
    }

    @Override
    public String addVip(String accountID, String jsonDataListVipData) throws ThriftSVException, TException {
        VipDataToSend vipDataToSend = Utils.fromJson(jsonDataListVipData, VipDataToSend.class);
        vipDataToSend.vipDataCollection = VipManager.getInstance().addVip(accountID, vipDataToSend.vipDataCollection);
        return Utils.toJson(vipDataToSend);
    }

    @Override
    public boolean canTakeFeeVip(String accountID) throws ThriftSVException, TException {
        return VipManager.getInstance().canTakeFeeVip(accountID);
    }
}
