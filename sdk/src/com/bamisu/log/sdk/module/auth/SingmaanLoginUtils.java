package com.bamisu.log.sdk.module.auth;

import com.bamisu.gamelib.utils.HttpRequestUtils;
import com.bamisu.gamelib.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;

/**
 * Created by Quach Thanh Phong
 * On 9/28/2021 - 10:08 AM
 */
public class SingmaanLoginUtils {
    private static final String SINGMAAN_AUTH_URL = "http://sdk.0you.cn/api/user/login_verify";
    private static final String SEPARATOR = "_";
    private MessageDigest md;
    private final static ObjectMapper mapper = new ObjectMapper();

    {
        try {
            md = MessageDigest.getInstance("MD5", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    private String AppID;
    private String ServerKey;


    private static SingmaanLoginUtils ourInstace = new SingmaanLoginUtils();
    public static SingmaanLoginUtils getInstance(){
        return ourInstace;
    }
    private SingmaanLoginUtils(){
        ISFSObject config = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/config/Singmaan Deverloper.json"));

        AppID = config.getUtfString("AppID");
        ServerKey = config.getUtfString("ServerKey");
        System.out.println("init singmaan confid ");
        System.out.println("AppID: " + AppID);
        System.out.println("ServerKey: " + ServerKey);
    }


    private String genSign(String userId, String token) throws NoSuchAlgorithmException {
        StringBuilder builder = new StringBuilder();
        builder.append("appid=").append(this.AppID)
                .append("&userID=").append(userId)
                .append("&token=").append(token)
                .append("&key=").append(this.ServerKey);
        String data = builder.toString();
//        System.out.println(data);
        String md5 = String.format("%032x", new BigInteger(1, md.digest(data.getBytes())));
//        System.out.println(md5);
        return md5;
    }

    /**
     * {"data":{"userID":"2993","appid":"1221"},"msg":"\u6210\u529f","status":1} => Success
     * {"data":null,"msg":"\u7b7e\u540d\u9519\u8bef","status":2} => Signature Error
     * {"data":null,"msg":"token\u8fc7\u671f","status":17} =>
     * @param authorizationCode
     * @return
     * @throws Exception
     */
    public String singmaanAuth(String authorizationCode) throws Exception{
        String[] data = authorizationCode.split(SEPARATOR);
        String userID = data[1];
        String token = data[0];

        HashMap<String,String> params = new HashMap<>();
        params.put("appid", this.AppID);
        params.put("userID", userID);
        params.put("token", token);
        params.put("sign", genSign(userID, token));
        String jsonResponse = HttpRequestUtils.post(SINGMAAN_AUTH_URL, params);
        if(jsonResponse.isEmpty()) return null;
//        System.out.println(jsonResponse);

        JsonNode jsonObj = mapper.readTree(jsonResponse);
        if(jsonObj.get("status").asInt() != 1) return null;
        return userID;
    }


    public static void main(String[] args) throws Exception {
        String uid = "2993";
        String token = "c1b806d26b38b1ee8af456f71138f6bc";

        System.out.println(SingmaanLoginUtils.getInstance().singmaanAuth(token + SEPARATOR + uid));
    }
}
