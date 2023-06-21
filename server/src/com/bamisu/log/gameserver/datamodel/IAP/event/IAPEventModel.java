package com.bamisu.log.gameserver.datamodel.IAP.event;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPChallenge;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPPackage;
import com.bamisu.log.gameserver.datamodel.IAP.event.entities.InfoIAPSale;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPSaleVO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;
import java.util.stream.Collectors;

public class IAPEventModel extends DataModel {
    private static final long id = 0;

    public List<InfoIAPSale> mapSale = new ArrayList<>();
    private final Object lockSale = new Object();



    private void init(){
        List<IAPSaleVO> start = IAPBuyManager.getInstance().getIAPSaleConfig();
        for(IAPSaleVO cf : start){
            mapSale.add(InfoIAPSale.create(cf.id, cf.idSale, cf.cost, cf.timeStamp, cf.target));
        }
    }

    public static IAPEventModel createIAPEventModel(Zone zone){
        IAPEventModel iapEventModel = new IAPEventModel();
        iapEventModel.init();
        iapEventModel.saveToDB(zone);

        return iapEventModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.id), zone);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static IAPEventModel copyFromDBtoObject(Zone zone) {
        IAPEventModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), IAPEventModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, IAPEventModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if(pInfo == null){
            pInfo = IAPEventModel.createIAPEventModel(zone);
        }
        return pInfo;
    }



    /*---------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------*/
    public List<InfoIAPSale> readInfoIAPsale(Zone zone){
        refresh(zone);
        return mapSale;
    }

    public boolean addInfoIAPsale(InfoIAPSale sale, Zone zone){
        synchronized (lockSale){
            for(InfoIAPSale data : readInfoIAPsale(zone)){
                if(data.idSale.equals(sale.idSale)) return false;
            }
            mapSale.add(sale);
        }
        return saveToDB(zone);
    }
    public boolean removeInfoIAPsale(List<String> listIdIAP, Zone zone){
        boolean haveSave = false;
        InfoIAPSale data;

        synchronized (lockSale){
            Iterator<InfoIAPSale> iterator = readInfoIAPsale(zone).iterator();
            while (iterator.hasNext()){
                data = iterator.next();

                if(listIdIAP.contains(data.id)){
                    iterator.remove();
                    haveSave = true;
                }
            }
        }

        if(haveSave) saveToDB(zone);

        return saveToDB(zone);
    }



    /*---------------------------------------------------------------------------------------------------------------*/
    private void refresh(Zone zone){
        boolean haveSave = false;
        InfoIAPSale data;

        synchronized (lockSale){
            Iterator<InfoIAPSale> iterator = mapSale.iterator();
            while (iterator.hasNext()){
                data = iterator.next();

                if(data.isTimeDisapear()){
                    iterator.remove();
                    haveSave = true;
                }
            }
        }

        if(haveSave) saveToDB(zone);
    }
}
