package com.bamisu.log.sdk.module.account.model;

import com.bamisu.log.sdk.module.account.exception.SocialAccountAlreadyExist;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import org.apache.log4j.Logger;

/**
 * Create by Popeye on 11:32 AM, 4/23/2020
 */

/**
 * Map tài khoản mạng xã hội với account
 */
public class SocialAccountModel extends DataModel {
    public String key;
    public int socialNetwork;
    public String socialNetworkKey;
    public String accountID;

    public SocialAccountModel() {
    }

    public SocialAccountModel(int socialNetwork, String socialNetworkKey, String accountID) {
        this.socialNetwork = socialNetwork;
        this.socialNetworkKey = socialNetworkKey;
        this.accountID = accountID;

        this.key = genKey(socialNetwork, socialNetworkKey);
    }

    private static String genKey(int socialNetwork, String socialNetworkKey) {
        return socialNetwork + ":" + socialNetworkKey;
    }

    public boolean save(SDKDatacontroler sdkDatacontroler) {
        try {
            this.saveModel(key, sdkDatacontroler.getController());
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static SocialAccountModel copyFromDB(int socialNetwork, String socialNetworkKey, SDKDatacontroler sdkDatacontroler) {
        return copyFromDB(genKey(socialNetwork, socialNetworkKey), sdkDatacontroler);
    }

    private static SocialAccountModel copyFromDB(String key, SDKDatacontroler sdkDatacontroler) {
        SocialAccountModel model = null;
        try {
            String str = (String) getModel(key, SocialAccountModel.class, sdkDatacontroler.getController());
            if (str != null) {
                model = Utils.fromJson(str, SocialAccountModel.class);
                if (model != null) {
//                    model.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        return model;
    }

    /**
     * tạo 1 social model mới liên kết với 1 account
     * @param accountModel
     * @param socialNetwork
     * @param socialNetworkKey
     * @param sdkDatacontroler
     * @return
     * @throws SocialAccountAlreadyExist
     */
    public static SocialAccountModel create(AccountModel accountModel, int socialNetwork, String socialNetworkKey, SDKDatacontroler sdkDatacontroler) throws SocialAccountAlreadyExist {
        SocialAccountModel socialAccountModel = copyFromDB(socialNetwork, socialNetworkKey, sdkDatacontroler);
        if (socialAccountModel != null) {
            throw new SocialAccountAlreadyExist();
        }

        socialAccountModel = new SocialAccountModel(socialNetwork, socialNetworkKey, accountModel.id);
        if(socialAccountModel.save(sdkDatacontroler)){
            return socialAccountModel;
        }

        return null;
    }
}
