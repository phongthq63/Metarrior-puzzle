package com.bamisu.log.sdk.module.account.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class UsernameModel extends DataModel {
    public String accountID;
    public String username;
    public String password;
    public String salt;
    public String email = "";

    public UsernameModel() {

    }

    public UsernameModel(String accountID) {
        this.accountID = accountID;
        this.username = "";
        this.password = "";
    }

    public UsernameModel(String accountID, String username, String password) {
        String randomString = Utils.ranStr(10);
        this.accountID = accountID;
        this.username = username;
        try {
            this.password = Utils.md5(password + randomString);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            this.password = "";
        }
        this.salt = randomString;
    }

    public final boolean save(SDKDatacontroler sdkDatacontroler) {
        try {
            saveModel(this.username, sdkDatacontroler.getController());
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static UsernameModel load(String username, SDKDatacontroler sdkDatacontroler) {
        UsernameModel model = null;
        try {
            String str = (String) DataModel.getModel(username, UsernameModel.class, sdkDatacontroler.getController());
            if (str != null) {
                model = Utils.fromJson(str, UsernameModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        return model;
    }

    public static UsernameModel create(String accountID, String username, String password) {
        UsernameModel model = new UsernameModel(accountID, username, password);
        if (model.save(SDKDatacontroler.getInstance())) {
            return model;
        }

        return null;
    }

    public boolean verifyPassword(String password) {
        try {
            String pwd = Utils.md5(password + this.salt);
            if (this.password.equalsIgnoreCase(pwd)) {
                return true;
            }
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean changePassword(String password) {
        try {
            this.password = Utils.md5(password + this.salt);
            this.save(SDKDatacontroler.getInstance());
            return true;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return false;
    }
}
