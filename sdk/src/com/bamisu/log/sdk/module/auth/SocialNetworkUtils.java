package com.bamisu.log.sdk.module.auth;

import com.bamisu.gamelib.entities.Params;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;
import com.bamisu.gamelib.utils.HttpRequestUtils;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.web3j.crypto.WalletUtils;

/**
 * Create by Popeye on 4:33 PM, 7/17/2020
 */
public class SocialNetworkUtils {

    public static String getIDforAppFacebook(String socialNetworkToken) {
        try {
            FacebookClient client = new DefaultFacebookClient(socialNetworkToken, Version.LATEST);
            User fbUser = client.fetchObject("me", User.class, Parameter.with("fields", "name,id,picture,gender,token_for_business,ids_for_business,friends"));
//            JsonObject js = client.fetchObject("/me/picture", JsonObject.class,
//                    Parameter.with("width", 120), // the image size
//                    Parameter.with("height", 120), // the image size
//                    Parameter.with("redirect", "false"));
//            String avatarUrl = js.get("data").asObject().getString("url", "");
            return fbUser.getTokenForBusiness();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static String getIDforAppGoogle(String socialNetworkToken) {
        try {
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + socialNetworkToken;
            String jsonString = HttpRequestUtils.get(url);
            ISFSObject data = SFSObject.newFromJsonData(jsonString);
            return data.containsKey(Params.USER_EMAIL) ? data.getUtfString("email") : "";
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    public static String getIDforAppApple(String socialNetworkToken){
        String uaid = null;
//        try {
//            uaid = AppleLoginUtils.getInstance().appleAuth(socialNetworkToken);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
        return (uaid != null) ? uaid : "";
    }

    public static String getIDforAppSingmaan(String socialNetworkToken){
        String userId = null;
        try {
            userId = SingmaanLoginUtils.getInstance().singmaanAuth(socialNetworkToken);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return (userId != null) ? userId : "";
    }

    public static String getAddressWalletBlockchain(String address){
        if (WalletUtils.isValidAddress(address)) {
            return address;
        } else {
            return "";
        }
    }

    public static void main(String args[]){
        System.out.println(getIDforAppGoogle("21"));
    }
}
