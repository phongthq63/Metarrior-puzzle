package com.bamisu.gamelib.iap;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Quach Thanh Phong
 * On 7/8/2021 - 5:25 PM
 */
public class MenaIAPVerify implements IIAPVerify {
    private static final int INIT_CAPACITY = 2048;

    private final static String URL = "https://cbv.medrickgames.com/api/v1";
    private final static String MARKET = "CBFZE1";


    private final static ObjectMapper mapper = new ObjectMapper();

    private static MenaIAPVerify ourInstance = new MenaIAPVerify();
    public static MenaIAPVerify getInstance() {
        return ourInstance;
    }
    private MenaIAPVerify() { }




    @Override
    public IAPVerifyResult verify(String packageName, String productId, String purchaseToken) {
        IAPVerifyResult res = new IAPVerifyResult();

        //Clear stringbuilder + build new
        StringBuilder stringBuilder = new StringBuilder();
        String URL_VERIFY = stringBuilder.append(URL).
                append("/market/").append(MARKET).
                append("/verify/").append(packageName).
                append("/sku/").append(productId).
                append("/token/").append(purchaseToken).
                toString();
//        System.out.println(URL_VERIFY);

        try {
            java.net.URL url = new URL(URL_VERIFY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = null;
            StringBuilder builder = new StringBuilder(INIT_CAPACITY);
            String line;
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            while ((line = rd.readLine()) != null) {
                builder.append(line);
            }
            rd.close();
            String jsonResponse = builder.toString();
            System.out.println(jsonResponse);
            JsonNode jsonObj = mapper.readTree(jsonResponse);

            res.errorCode = (jsonObj.get("msg").asText().equals("OK")) ? ServerConstant.ErrorCode.NONE : ServerConstant.ErrorCode.ERR_INVALID_PAYMENT;
            res.message = jsonObj.get("data").asText();
            res.orderId = productId;
            res.purchaseTimeMillis = 0;
            switch (res.errorCode){
                case ServerConstant.ErrorCode.NONE:
                    res.purchaseType = jsonObj.get("data").get("consumptionState").asInt();
                    res.purchaseState = jsonObj.get("data").get("purchaseState").asInt();
                    break;
                default:
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.errorCode = ServerConstant.ErrorCode.ERR_SYS;
            res.message = "lỗi j đó!";
        }

        return res;
    }





    public static void main(String[] args){
        MenaIAPVerify a = new MenaIAPVerify();
        System.out.println(Utils.toJson(a.verify("com.bamisu.legends", "sky_daily_6", "Xj9S6_p1mRbUbuxd")));
    }
}
