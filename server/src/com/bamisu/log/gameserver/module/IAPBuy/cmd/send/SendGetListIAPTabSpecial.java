package com.bamisu.log.gameserver.module.IAPBuy.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPChallenge;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPPackage;
import com.bamisu.log.gameserver.datamodel.IAP.home.UserIAPHomeModel;
import com.bamisu.log.gameserver.datamodel.IAP.store.UserIAPStoreModel;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPTabVO;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPType;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.Map;

public class SendGetListIAPTabSpecial extends BaseMsg {

    public UserIAPStoreModel userIAPStoreModel;
    public UserIAPHomeModel userIAPHomeModel;
    public Zone zone;

    public List<String> listTabID;
    public List<String> listPackageIDExile;
    public Map<String,Integer> listEventNoti;



    public SendGetListIAPTabSpecial() {
        super(CMD.CMD_GET_LIST_IAP_TAB_SPECIAL);
    }

    @Override
    public void packData() {
        super.packData();

        ISFSArray arrayPack;
        ISFSObject objPack;
        InfoIAPChallenge challengeData = null;
        List<InfoIAPPackage> packagesData = null;
        IAPTabVO tabCf;
        EIAPType typeTab;

        arrayPack = new SFSArray();
        out_loop:
        for(String tabID : listTabID){
            objPack = new SFSObject();

            tabCf = IAPBuyManager.getInstance().getIAPTabConfig(tabID);
            if(tabCf == null) continue;
            typeTab = EIAPType.fromID(tabCf.type);
            switch (typeTab){
                case PACKAGE_ITEM:
                    packagesData = IAPBuyManager.getInstance().getInfoIAPPackageUserModelDependByTab(userIAPHomeModel.uid, tabID, zone);

                    if(packagesData.size() == 1){
                        objPack.putInt(Params.TIME, packagesData.get(0).readTimeDispear(zone));
                    }else {
                        objPack.putInt(Params.TIME, -1);
                    }
                    break;
                case CHALLENGE:
                    challengeData = IAPBuyManager.getInstance().getInfoIAPChallengeUserModel(userIAPStoreModel, userIAPHomeModel, tabCf.packages.get(0), zone);

                    objPack.putInt(Params.TIME, challengeData.readTimeDispear());
                    break;
                default:
                    continue out_loop;
            }

            objPack.putUtfString(Params.ID, tabCf.id);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);

        //Goi Special Event
        int now = Utils.getTimestampInSecond();
        arrayPack = new SFSArray();
        for(String idNoti : listEventNoti.keySet()){
            objPack = new SFSObject();

            objPack.putUtfString(Params.ID, idNoti);
            objPack.putInt(Params.TIME, (listEventNoti.get(idNoti) != -1) ? listEventNoti.get(idNoti) - now : -1);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.NOTI, arrayPack);

        //List Package Exile
        data.putUtfStringArray(Params.EXILE, listPackageIDExile);
    }
}
