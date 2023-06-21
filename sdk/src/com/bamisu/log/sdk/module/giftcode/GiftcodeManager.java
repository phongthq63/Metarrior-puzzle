package com.bamisu.log.sdk.module.giftcode;

import com.bamisu.gamelib.sql.sdk.dao.GiftcodeDAO;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.giftcode.entities.GiftCodeExtra;
import com.bamisu.log.sdk.module.giftcode.model.AccountUseGiftcodeModel;
import com.bamisu.log.sdk.module.giftcode.model.GiftcodeModel;
import com.bamisu.log.sdk.module.sql.SDKsqlManager;
import com.bamisu.log.sdkthrift.exception.SDKThriftError;
import com.bamisu.log.sdkthrift.exception.ThriftSVException;
import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:43 PM, 4/22/2020
 */
public class GiftcodeManager {
    private static GiftcodeManager ourInstance = new GiftcodeManager();

    public static GiftcodeManager getInstance() {
        return ourInstance;
    }

    private GiftcodeManager() {
    }

    public GiftcodeModel genCode(String code, int expired, int max, List<ResourcePackage> gifts) {
        if(code.isEmpty()){
            return GiftcodeModel.create(expired, max, gifts);
        }

        return GiftcodeModel.create(code, expired, max, gifts);
    }

    public List<GiftcodeModel> genCode(int numCode, int expired, List<ResourcePackage> gifts) {
        List<GiftcodeModel> lst = new ArrayList<>();
        for (int i = 0; i < numCode; i++) {
            GiftcodeModel giftcodeModel = GiftcodeModel.create(expired, 1, gifts);
            lst.add(giftcodeModel);
        }

        return lst;
    }

    public List<GiftcodeModel> genCode(int numCode, int expired, List<ResourcePackage> gifts, List<GiftCodeExtra> extras) {
        List<GiftcodeModel> lst = new ArrayList<>();
        for (int i = 0; i < numCode; i++) {
            GiftcodeModel giftcodeModel = GiftcodeModel.create(expired, 1, gifts);
            giftcodeModel.extras = extras;
            giftcodeModel.save(SDKDatacontroler.getInstance());
            lst.add(giftcodeModel);
        }

        return lst;
    }

    public List<GiftcodeModel> genCode(String code, int expired, int max, List<ResourcePackage> gifts, List<GiftCodeExtra> extras) {
        GiftcodeModel giftcodeModel = null;
        List<GiftcodeModel> lst = new ArrayList<>();
        if(code.isEmpty()){
            giftcodeModel = GiftcodeModel.create(expired, max, gifts);
        } else {
            giftcodeModel = GiftcodeModel.create(code, expired, max, gifts);
        }

        if (giftcodeModel != null) {
            giftcodeModel.extras = extras;
            giftcodeModel.save(SDKDatacontroler.getInstance());
            lst.add(giftcodeModel);
        }
        return lst;
    }

    public void activeCode(GiftcodeModel giftcodeModel, int serverID, String uid, String accountID) throws ThriftSVException {
        if(giftcodeModel == null) {
            throw SDKThriftError.GIFTCODE_NOT_FOUND;
        }

        if(giftcodeModel.expired()){
            throw SDKThriftError.GIFTCODE_EXPIRED;
        }

        if(giftcodeModel.isGone()){
            throw SDKThriftError.GIFTCODE_GONE;
        }

        AccountUseGiftcodeModel accountUseGiftcodeModel = AccountUseGiftcodeModel.copyFromDB(accountID, SDKDatacontroler.getInstance());
        if(accountUseGiftcodeModel == null){
            throw SDKThriftError.SYSTEM_ERROR;
        }

        //check đã dùng chưa
        if(accountUseGiftcodeModel.haveUsed(giftcodeModel.code, String.valueOf(serverID))){
            throw SDKThriftError.HAVE_USED;
        }

        if(!giftcodeModel.use(SDKDatacontroler.getInstance())){
            throw SDKThriftError.SYSTEM_ERROR;
        }

        if(!accountUseGiftcodeModel.activeCode(giftcodeModel.code, String.valueOf(serverID), uid, SDKDatacontroler.getInstance())){
            throw SDKThriftError.SYSTEM_ERROR;
        }

        GiftcodeDAO.update(giftcodeModel.code, Long.parseLong(uid), SDKsqlManager.getInstance().getSqlController());
    }
}
