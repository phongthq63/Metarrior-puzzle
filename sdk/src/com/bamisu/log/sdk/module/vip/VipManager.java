package com.bamisu.log.sdk.module.vip;

import com.bamisu.log.sdk.module.account.model.AccountModel;
import com.bamisu.log.sdk.module.vip.model.VipModel;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.gamelib.entities.VipData;

import java.util.Collection;
import java.util.List;

/**
 * Create by Popeye on 10:19 AM, 7/14/2020
 */
public class VipManager {
    private static VipManager ourInstance = new VipManager();

    public static VipManager getInstance() {
        return ourInstance;
    }

    SDKDatacontroler sdkDatacontroler = SDKDatacontroler.getInstance();

    private VipManager() {
    }

    /**
     *
     * @param accountID
     * @return
     */
    public VipModel getVipModel(String accountID){
        return VipModel.copyFromDB(accountID, sdkDatacontroler);
    }

    /**
     *
     * @param accountID
     * @return
     */
    public Collection<VipData> getVipData(String accountID){
        return getVipModel(accountID).vips;
    }

    /**
     * Cộng thêm ngày vip
     * @param accountID
     * @param addVipDataList
     * @return
     */
    public Collection<VipData> addVip(String accountID, Collection<VipData> addVipDataList){
        VipModel vipModel = getVipModel(accountID);
        if(!addVipDataList.isEmpty()){
            vipModel.addVip(addVipDataList, sdkDatacontroler);
        }
        return vipModel.vips;
    }

    public boolean canTakeFeeVip(String accountID) {
        AccountModel accountModel = AccountModel.copyFromDB(accountID, SDKDatacontroler.getInstance());
        if(accountModel == null) return false;
//        System.out.println("====================");
//        System.out.println(accountModel.mapPlayer.values().size());
//        System.out.println("====================");
        return accountModel.mapPlayer.values().size() == 1;
    }
}
