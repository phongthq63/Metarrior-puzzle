package com.bamisu.log.gameserver.module.event.event.login14days.models;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.event.event.login14days.Login14DaysManager;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class UserLoginEventModel extends DataModel {
    private long userId;
    private int session;
    private List<Integer> giftState;
    private String currentDay;

    public UserLoginEventModel() {

    }

    public UserLoginEventModel(long userId, int session) {
        this.userId = userId;
        this.session = session;
        this.currentDay = "";
        int numGift = Login14DaysManager.getInstance().getGiftsConfig().size();
        this.giftState = new ArrayList<>();
        for (int i = 0; i < numGift; i++) {
            this.giftState.add(0);
        }
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public List<Integer> getGiftState() {
        return giftState;
    }

    public void setGiftState(List<Integer> giftState) {
        this.giftState = giftState;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(String currentDay) {
        this.currentDay = currentDay;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(genKey(this.userId, this.session), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final UserLoginEventModel copyFromDBtoObject(String userId, int session, Zone zone) {
        UserLoginEventModel model = null;
        String key = genKey(userId, session);
        try {
            String str = (String) getModel(key, UserLoginEventModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, UserLoginEventModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (model == null) {
            model = new UserLoginEventModel(Long.parseLong(userId), session);
            model.saveToDB(zone);
        }

        return model;
    }

    public static final UserLoginEventModel copyFromDBtoObject(long userId, int session, Zone zone) {
        return copyFromDBtoObject(String.valueOf(userId), session, zone);
    }

    private static String genKey(String userId, int session) {
        return userId + "_" + session;
    }

    private static String genKey(long userId, int session) {
        return genKey(String.valueOf(userId), session);
    }
}
