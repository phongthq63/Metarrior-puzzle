package com.bamisu.log.gameserver.gamethriftserver.handler.IAP;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.datamodel.IAP.event.entities.InfoIAPSale;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gamethrift.entities.exception.ThriftSVException;
import com.log.bamisu.gamethrift.service.IAP.IAPClientService;
import com.smartfoxserver.v2.SmartFoxServer;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;

import java.util.List;

public class TIAPClientHandler implements IAPClientService.Iface {

    @WithSpan
    @Override
    public boolean addSaleIAP(String zoneName, String jsonData) throws ThriftSVException, TException {
        InfoIAPSale saleData = Utils.fromJson(jsonData, InfoIAPSale.class);

        if(zoneName == null || zoneName.isEmpty()){
            SmartFoxServer.getInstance().getZoneManager().getZoneList().parallelStream().
                    filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                    forEach(zone -> IAPBuyManager.getInstance().addSaleIAP(saleData, zone));
        }else {
            IAPBuyManager.getInstance().addSaleIAP(saleData, ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName));
        }

        return true;
    }

    @WithSpan
    @Override
    public boolean removeSaleIAP(String zoneName, List<String> listIdSale) throws ThriftSVException, TException {
        if(zoneName == null || zoneName.isEmpty()){
            SmartFoxServer.getInstance().getZoneManager().getZoneList().parallelStream().
                    filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                    forEach(zone -> IAPBuyManager.getInstance().removeSaleIAP(listIdSale, zone));
        }else {
            IAPBuyManager.getInstance().removeSaleIAP(listIdSale, ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName));
        }

        return true;
    }

    @WithSpan
    @Override
    public boolean buyIAP(String zoneName, long uid, String idPackage) throws ThriftSVException, TException {
        if(zoneName == null || zoneName.isEmpty() || uid <= 0 || idPackage == null || idPackage.isEmpty()) return false;

        return IAPBuyManager.getInstance().buyIAP(uid, idPackage, ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName));
    }
}
