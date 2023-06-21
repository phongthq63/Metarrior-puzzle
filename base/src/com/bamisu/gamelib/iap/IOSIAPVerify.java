package com.bamisu.gamelib.iap;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.HttpRequestUtils;
import com.bamisu.gamelib.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IOSIAPVerify implements IIAPVerify {
    private final static String SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";
    private final static String PRODUCTION_URL = "https://buy.itunes.apple.com/verifyReceipt";
    private String KEY_SHARED_SECRET;
    private boolean isTestPurchase = false;

    private final static ObjectMapper mapper = new ObjectMapper();


    private static IOSIAPVerify ourInstance = new IOSIAPVerify();
    public static IOSIAPVerify getInstance() {
        return ourInstance;
    }
    private IOSIAPVerify() {
//        ISFSObject config = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/config/Apple Store IOS Developer.json"));
////        JsonNode cf = null;
////        try {
////            cf = new ObjectMapper().readTree(new FileInputStream(System.getProperty("user.dir") + "/config/Apple Store IOS Developer.json"));
////        } catch (IOException e) {
////            e.printStackTrace();
////            return;
////        }
//
//        KEY_SHARED_SECRET = config.getUtfString("key_shared_secret");
//        isTestPurchase = config.getBool("is_test_purchase");
//        System.out.println("init ios confid ");
//        System.out.println("KEY_SHARED_SECRET: " + KEY_SHARED_SECRET);
//        System.out.println("isTestPurchase: " + isTestPurchase);
    }

    @Override
    public synchronized IAPVerifyResult verify(String packageName, String productId, String purchaseToken) {
        IAPVerifyResult res = new IAPVerifyResult();

        Map<String,Object> dataRequest = new HashMap();
        dataRequest.put("password", KEY_SHARED_SECRET);
        dataRequest.put("receipt-data", purchaseToken);
        try {
            String jsonResponse = HttpRequestUtils.sendHttpRequest(isTestPurchase ? SANDBOX_URL : PRODUCTION_URL, "POST", Utils.toJson(dataRequest));
            JsonNode jsonObj = mapper.readTree(jsonResponse);

            //Kiem tra khop goi thanh toan
            if(!productId.equals(jsonObj.get("receipt").get("in_app").get(0).get("product_id").asText())){
                res.errorCode = ServerConstant.ErrorCode.ERR_INVALID_PAYMENT;
                res.message = "Gói thanh toán không khớp";
            }else {
                res.errorCode = jsonObj.get("status").asInt();
                res.message = readStatus(res.errorCode);
                res.orderId = jsonObj.get("receipt").get("in_app").get(0).get("transaction_id").asText();
                res.purchaseTimeMillis = jsonObj.get("receipt").get("in_app").get(0).get("purchase_date_ms").asInt();
                res.purchaseType = isTestPurchase ? 1 : 0;
                res.purchaseState = jsonObj.get("status").asInt();
            }

        } catch (IOException e) {
            e.printStackTrace();
            res.errorCode = ServerConstant.ErrorCode.ERR_SYS;
            res.message = "lỗi j đó!";
            e.printStackTrace();
        }

        return res;
    }

    private String readStatus(int status) {
        String message = status + ": ";
        switch (status) {
            case 0: return "";
            case 21000: message += "The App Store could not read the JSON object you provided."; break;
            case 21002: message += "The data in the receipt-data property was malformed."; break;
            case 21003: message += "The receipt could not be authenticated."; break;
            case 21004: message += "The shared secret you provided does not match the shared secret on file for your account."; break;
            case 21005: message += "The receipt server is not currently available."; break;
            case 21006: message += "This receipt is valid but the subscription has expired. When this status code is returned to your server, the receipt data is also decoded and returned as part of the response."; break;
            case 21007: message += "This receipt is a sandbox receipt, but it was sent to the production service for verification."; break;
            case 21008: message += "This receipt is a production receipt, but it was sent to the sandbox service for verification."; break;
            default:
                /** unknown error code (nevertheless a problem) */
                message = "Unknown error: status code = " + status;
        }

        return message;
    }
}
