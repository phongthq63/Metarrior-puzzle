package com.bamisu.log.gameserver.module.IAPBuy.cmd.send;

import com.bamisu.log.gameserver.datamodel.IAP.event.entities.InfoIAPSale;
import com.bamisu.log.gameserver.datamodel.IAP.home.UserIAPHomeModel;
import com.bamisu.log.gameserver.datamodel.IAP.store.UserIAPStoreModel;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPChallenge;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPPackage;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPTabVO;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPType;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPChallengeVO;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPPackageVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SendGetInfoIAPTab extends BaseMsg {

    public IAPTabVO tabCf;
    public EIAPType type;
    public UserIAPStoreModel userIAPStoreModel;
    public UserIAPHomeModel userIAPHomeModel;
    public List<InfoIAPSale> listSale;

    public List<InfoIAPPackage> listData;
    public InfoIAPChallenge infoData;       //Dulicate

    public Zone zone;


    public SendGetInfoIAPTab() {
        super(CMD.CMD_GET_INFO_IAP_TAB);
    }

    public SendGetInfoIAPTab(short errorCode) {
        super(CMD.CMD_GET_INFO_IAP_TAB, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.ID, (tabCf != null) ? tabCf.id : "");
        Map<String, InfoIAPSale> mapSale = listSale.parallelStream().
                collect(Collectors.toMap(obj -> obj.id, Function.identity(), (objOld, objNew) -> objNew));

        InfoIAPSale dataSale;
        int timeNow = Utils.getTimestampInSecond();
        boolean haveSale = false;
        int timeSale = -1;
        switch (type){
            case PACKAGE_ITEM:

                ISFSArray packList = new SFSArray();
                ISFSObject packObj;
                IAPPackageVO packageCf;
                out_loop:
                for(String packCf : tabCf.packages){
                    //TH co trong data
                    for(InfoIAPPackage data : listData){
                        if(!packCf.equals(data.id)) continue;

                        packObj = new SFSObject();
                        packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(data.id, zone);

                        packObj.putUtfString(Params.ID, data.id);

                        if(IAPBuyManager.getInstance().canClaimIAPPackage(userIAPStoreModel, userIAPHomeModel, packageCf, zone)){
                            packObj.putInt(Params.COUNT, (packageCf.maxBuy <= 0) ? -1 : Math.max(packageCf.maxBuy - data.buy, 0));
                        }else {
                            packObj.putInt(Params.COUNT, 0);
                        }
                        //Sale
                        if(mapSale.containsKey(data.id)){
                            dataSale = mapSale.get(data.id);
                            haveSale = dataSale.haveSale(userIAPHomeModel.uid);

                            if(haveSale) timeSale = dataSale.readTimeDisapear();
                        }
                        packObj.putBool(Params.IS_SALE, haveSale);

                        packObj.putInt(Params.SALE, timeSale);
                        packObj.putInt(Params.REFRESH, data.readTimeRefresh(zone));
                        packObj.putInt(Params.EXSIST, data.readTimeDispear(zone));

                        packList.addSFSObject(packObj);
                        continue out_loop;
                    }

                    //TH chua co tren data (goi moi add)
                    packObj = new SFSObject();
                    packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(packCf, zone);

                    packObj.putUtfString(Params.ID, packCf);
                    if(IAPBuyManager.getInstance().canClaimIAPPackage(userIAPStoreModel, userIAPHomeModel, packageCf, zone)){
                        packObj.putInt(Params.COUNT, (packageCf.maxBuy <= 0) ? -1 : packageCf.maxBuy);
                    }else {
                        packObj.putInt(Params.COUNT, 0);
                    }
                    //Sale
                    if(mapSale.containsKey(packCf)){
                        dataSale = mapSale.get(packCf);
                        haveSale = dataSale.haveSale(userIAPHomeModel.uid);

                        if(haveSale) timeSale = dataSale.readTimeDisapear();
                    }
                    packObj.putBool(Params.IS_SALE, haveSale);

                    packObj.putInt(Params.SALE, timeSale);
                    packObj.putInt(Params.REFRESH, TimeUtils.getDeltaTimeToTime(ETimeType.fromID(packageCf.timeRefresh.get(0)), timeNow));
                    packObj.putInt(Params.EXSIST, TimeUtils.getDeltaTimeToTime(ETimeType.fromID(packageCf.timeExsist.get(0)), timeNow));

                    packList.addSFSObject(packObj);
                }
                data.putSFSArray(Params.LIST, packList);

                break;
            case CHALLENGE:

                if(infoData == null){
                    data.putNull(Params.PACK);
                }else {
                    infoData = InfoIAPChallenge.create(infoData);
                    IAPChallengeVO challengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(infoData.id);

                    infoData.timeStampRefresh = TimeUtils.getDeltaTimeToTime(ETimeType.fromID(challengeCf.timeRefresh), infoData.timeStampRefresh);
                    infoData.timeStampDispear = infoData.readTimeDispear();
                    data.putSFSObject(Params.PACK, SFSObject.newFromJsonData(Utils.toJson(infoData)));
                    //Sale
                    if(mapSale.containsKey(infoData.id)){
                        dataSale = mapSale.get(infoData.id);
                        haveSale = dataSale.haveSale(userIAPHomeModel.uid);

                        if(haveSale && dataSale.haveLimitTime()){
                            timeSale = dataSale.readTimeDisapear();
                        }
                    }
                    data.getSFSObject(Params.PACK).putBool(Params.IS_SALE, haveSale);
                    data.getSFSObject(Params.PACK).putInt(Params.SALE, timeSale);
                }

                break;
        }
    }
}
