package com.bamisu.log.sdk.module.vip.model;

import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.entities.EVip;
import com.bamisu.gamelib.entities.VipData;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by Popeye on 9:42 AM, 7/14/2020
 */
public class VipModel extends DataModel {
    public String accountID;
    public Collection<VipData> vips = new ArrayList<>();

    public VipModel() {
        init();
    }

    public VipModel(String accountID) {
        this.accountID = accountID;
        init();
    }

    private void init() {
        for(EVip eVip : EVip.values()){
            vips.add(new VipData(eVip, -1));
        }
    }

    public boolean save(SDKDatacontroler sdkDatacontroler) {
        try {
            this.saveModel(accountID, sdkDatacontroler.getController());
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static VipModel copyFromDB(String accountID, SDKDatacontroler sdkDatacontroler) {
        VipModel model = null;
        try {
            String str = (String) getModel(accountID, VipModel.class, sdkDatacontroler.getController());
            if (str != null) {
                model = Utils.fromJson(str, VipModel.class);
                if (model != null) {
//                    model.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if(model == null){
            model = new VipModel(accountID);
            model.save(sdkDatacontroler);
        }
        return model;
    }

    /**
     * cộng thêm ngày vip
     * @param addVipDataList
     */
    public void addVip(Collection<VipData> addVipDataList, SDKDatacontroler sdkDatacontroler) {
        for(VipData addVipData : addVipDataList){
            for(VipData vipData : vips){
                if(vipData.eVip == addVipData.eVip){
                    if(vipData.expired < Utils.getTimestampInSecond()){
                        vipData.expired = Utils.getTimestampInSecond() + addVipData.expired;
                    }else {
                        vipData.expired += addVipData.expired;
                    }
                }
            }
        }
        save(sdkDatacontroler);
    }
}
