package com.bamisu.log.gameserver.datamodel.campaign;

import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.campaign.entities.StoreSlotCampaignInfo;
import com.bamisu.log.gameserver.datamodel.campaign.entities.UserMainCampaignDetail;
import com.bamisu.log.gameserver.datamodel.campaign.entities.UserStoreCampaignInfo;
import com.bamisu.log.gameserver.module.campaign.config.StoreCampaignSlotConfig;
import com.bamisu.log.gameserver.module.campaign.config.entities.StoreCampaignSlotVO;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.List;

/**
 * Create by Popeye on 3:41 PM, 2/5/2020
 */
public class UserCampaignDetailModel extends DataModel {
    public long uid;
    public UserMainCampaignDetail userMainCampaignDetail = new UserMainCampaignDetail();
    public UserStoreCampaignInfo userStoreCampaignInfo = //Khi khoi tao luon la chap 0 -> viet lay loi ti
            UserStoreCampaignInfo.create(Integer.parseInt(userMainCampaignDetail.readNextStation().split(",", 2)[0]));
    public int lose;

    public UserCampaignDetailModel() {
    }

    public UserCampaignDetailModel(long uid) {
        this.uid = uid;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserCampaignDetailModel copyFromDBtoObject(long uid, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uid), zone);
    }

    public static UserCampaignDetailModel copyFromDBtoObject(String uId, Zone zone) {
        UserCampaignDetailModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserCampaignDetailModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserCampaignDetailModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if (pInfo == null) {
            //System.out.println("Create UserCampaignDetail");
            pInfo = new UserCampaignDetailModel(Long.parseLong(uId));
            pInfo.saveToDB(zone);
        }

//        if(((ZoneExtension)zone.getExtension()).isTestServer()){
//            if(Integer.parseInt(pInfo.userMainCampaignDetail.readNextStation().split(",")[0]) < 10){
//                pInfo.userMainCampaignDetail.nextStation = "10,0";
//                pInfo.saveToDB(zone);
//            }
//        }
        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public int readLose(){
        return lose;
    }

    public boolean increaseLose(Zone zone){
        lose++;
        return saveToDB(zone);
    }
}
