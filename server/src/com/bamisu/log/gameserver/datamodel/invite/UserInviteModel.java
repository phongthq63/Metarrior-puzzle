package com.bamisu.log.gameserver.datamodel.invite;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.UserMainHeroModel;
import com.smartfoxserver.v2.entities.Zone;

/**
 * Created by Quach Thanh Phong
 * On 12/14/2022 - 12:55 AM
 */
public class UserInviteModel extends DataModel {

    public long uid;
    public boolean triggerUltimate = false;


    public static UserInviteModel createUserInviteModel(long uid, Zone zone){
        UserInviteModel userInviteModel = new UserInviteModel();
        userInviteModel.uid = uid;
        userInviteModel.saveToDB(zone);

        return userInviteModel;
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

    public static UserInviteModel copyFromDBtoObject(long uId, Zone zone) {
        UserInviteModel userInviteModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if(userInviteModel == null){
            userInviteModel = createUserInviteModel(uId, zone);
        }
        return userInviteModel;
    }

    private static UserInviteModel copyFromDBtoObject(String uId, Zone zone) {
        UserInviteModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserMainHeroModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserInviteModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

}
