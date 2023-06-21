package com.bamisu.gamelib.manager;

import com.bamisu.gamelib.base.model.UserBase;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.model.ListUserLastLoginModel;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.bamisu.gamelib.base.model.UserBase;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.ECacheType;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.model.DisplayNameModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Popeye on 11/15/2017.
 */
public class UserManager {
    private LoadingCache<String, UserModel> USER_CACHE;
    private Zone zone;
    private ListUserLastLoginModel listUserLastLoginModel;
    private int countLogin = 0;

    public UserManager(){
    }

    public UserManager(Zone _zone) {
        this.zone = _zone;

        CacheLoader<String, UserModel> loader;
        loader = new CacheLoader<String, UserModel>() {
            @Override
            public UserModel load(String uid) throws Exception {
                return UserModel.copyFromDBtoObject(Long.valueOf(uid), zone);
            }
        };
        this.USER_CACHE = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build(loader);
        this.listUserLastLoginModel = ListUserLastLoginModel.copyFromDBtoObject(zone);
    }

    public UserModel getUserModel(String uid) {
        try {
//            return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getUserModelCache(Long.parseLong(uid));
            return this.USER_CACHE.getUnchecked(uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserModel getUserModel(long uid) {
        return this.getUserModel(String.valueOf(uid));
    }

    public void cache(String name, UserModel um) {
        this.USER_CACHE.put(name, um);
    }

    public void invalidate(UserModel userModel) {
        try {
            this.USER_CACHE.invalidate(userModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserModel getUserModelByKey(String key) {
        try {
            UserBase userBase = UserBase.copyFromDBtoObject(key, zone);
            if (userBase != null) {
                return this.getUserModel(userBase.userID);
            }

            DisplayNameModel displayNameModel = DisplayNameModel.copyFromDBtoObject(key.toLowerCase(), zone);
            if (displayNameModel != null) {
                return this.getUserModel(displayNameModel.userID);
            }

            return this.getUserModel(Long.parseLong(key));
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public void onUserLogin(long uid){
        synchronized (listUserLastLoginModel){
            if(!listUserLastLoginModel.ids.contains(uid)){
                if(listUserLastLoginModel.ids.size() > 100){
                    listUserLastLoginModel.ids.remove(0);
                }
                listUserLastLoginModel.ids.add(uid);
            }
        }
        if(countLogin % 10 == 0){
            listUserLastLoginModel.saveToDB(zone);
        }
        countLogin ++;
    }


    /**
     * lấy ngẫu nhiên 1 list user login gần nhất
     * @param count số lượng cần lấy
     */
    public List<Long> getLastLoginUser(int count){
        if(count >= listUserLastLoginModel.ids.size()){
            return new ArrayList<>(listUserLastLoginModel.ids);
        }

        List<Long> tmpList = new ArrayList<>(listUserLastLoginModel.ids);
        Collections.shuffle(tmpList);
        return tmpList.subList(0, count - 1);
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public UserModel getUserModel(User user) {
        return getUserModel(user.getName());
    }

}
