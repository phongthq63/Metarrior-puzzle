package com.bamisu.log.gameserver.module.sdkthriftclient;

import com.bamisu.log.sdkthrift.service.vip.VipService;
import com.bamisu.gamelib.entities.VipData;
import com.bamisu.gamelib.entities.VipDataToSend;
import com.bamisu.gamelib.utils.Utils;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Create by Popeye on 10:48 AM, 7/14/2020
 */
public class SDKGateVip {
    public static final String serviceName = "vip";

    @WithSpan
    public static Collection<VipData> getVip(String accountID) throws TException {
        Collection<VipData> vipData = new ArrayList<VipData>();
        String jsonDataVipDataList;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            VipService.Client client = (VipService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            jsonDataVipDataList = client.getVip(accountID);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }
        return Utils.fromJson(jsonDataVipDataList, vipData.getClass());
    }

    @WithSpan
    public static Collection<VipData> addVip(String accountID, Collection<VipData> vipData) throws TException {
        VipDataToSend vipDataToSend = new VipDataToSend(vipData);
        String jsonData;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            VipService.Client client = (VipService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            jsonData = client.addVip(accountID, Utils.toJson(vipDataToSend));
            vipDataToSend = Utils.fromJson(jsonData, VipDataToSend.class);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }
        return vipDataToSend.vipDataCollection;
    }

    @WithSpan
    public static boolean canTakeFeeVip(String accountID) throws TException {
        boolean canTakeFeeVip = false;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            VipService.Client client = (VipService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            canTakeFeeVip = client.canTakeFeeVip(accountID);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }
        return canTakeFeeVip;
    }
}
