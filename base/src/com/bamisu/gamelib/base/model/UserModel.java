package com.bamisu.gamelib.base.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.entities.Gender;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;

/**
 * Created by Popeye on 4/12/2017.
 */
public class UserModel extends DataModel {
    public long userID = 0;

    public String accountID = "";
    public int type = 0;    //0: user binh thuong, 1: bot
    public String displayName = "";
    public int createTime = 0;
    public String statusText = "Hello my friend";
    public short gender = Gender.MALE.getshortValue();
    public short status = 0;
    public String lang = "";
    public String avatar = "T1018";
    public int avatarFrame = 0;
    public List<Integer> avatarFrameList = Arrays.asList(0, 1, 2, 3, 4, 5, 6);
    public String access_token = "";
    public List<Integer> linked = new ArrayList<>();    //SocialNetwork
    public int serverId = 0;
    public String stage = "";

    public long lastLogin = 0;
    public int lastLogout = 0;
    public int loginCount = 0;
    public String ip = "";
    public List<Integer> stageV3 = new ArrayList<Integer>();

    public UserModel() {
    }

    public UserModel(final UserBase userBase) {
        this();
        this.accountID = userBase.accountID;
        this.userID = userBase.userID;
        this.createTime = userBase.createTime;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.userID), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lay thong tin tu database gan vao instance UserModel
     *
     * @param uId
     * @return null if false else instance UserModel
     */
    public static UserModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserModel copyFromDBtoObject(String uId, Zone zone) {
        UserModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    /**
     * tạo mới 1 UserModel từ 1 Account ID
     * @param accountID
     * @param zone
     * @return
     */
    public static UserModel createUserModel(String accountID, Zone zone, int serverId) {
        UserBase userBase = UserBase.copyFromDBtoObject(accountID, zone);
        if (userBase != null) {
            return null;
        }

        //TODO: Tạo UserBase
        userBase = UserBase.create(accountID, zone, serverId);
        userBase.saveToDB(zone);

        //TODO: Tạo UserModel
        UserModel userModel = new UserModel(userBase);
        userModel.serverId = Integer.parseInt(zone.getExtension().getConfigProperties().getProperty("server_id"));
        userModel.saveToDB(zone);

        return userModel;
    }

    /**
     * get map UserModel
     *
     * @param ids: list ids
     * @return Map<userId               ,               a                               UserModel>
     */
    public static Map<String, UserModel> copyMapUserModelfromDB(List<String> ids, Zone zone) {
        Map<String, UserModel> result = new HashMap<>();
        try {
            Map<String, Object> mapObj = multiGet(ids, UserModel.class, zone);
            Set<Map.Entry<String, Object>> entrySet = mapObj.entrySet();
            String str;
            UserModel userModel;
            for (Map.Entry<String, Object> entry : entrySet) {
                str = (String) entry.getValue();
                if (str != null) {
                    userModel = Utils.fromJson(str, UserModel.class);
                    if (userModel != null) {
                        result.put(String.valueOf(userModel.userID), userModel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * get list UserModel
     *
     * @param ids
     * @return
     */
    public static ArrayList<UserModel> copyListUserModelfromDB(List<String> ids, Zone zone) {
        ArrayList<UserModel> result = new ArrayList<UserModel>();
        try {
            Map<String, Object> mapObj = multiGet(ids, UserModel.class, zone);
            Set<Map.Entry<String, Object>> entrySet = mapObj.entrySet();
            String str;
            UserModel userModel;
            for (Map.Entry<String, Object> entry : entrySet) {
                str = (String) entry.getValue();
                if (str != null) {
                    userModel = Utils.fromJson(str, UserModel.class);
                    if (userModel != null) {
                        result.add(userModel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
