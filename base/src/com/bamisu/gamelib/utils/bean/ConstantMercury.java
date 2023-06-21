package com.bamisu.gamelib.utils.bean;


import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.base.config.ConfigHandle;

public class ConstantMercury {
    // DISPATCHER

    public static final int SCRIBE_PORT = ConfigHandle.instance().get("scribe_port") == null ? 1463 : ConfigHandle.instance().getLong("scribe_port").intValue();


    // GENERAL
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final int MAX_MESSAGE_LENGTH = 65535;
   
   
    //SUFFIX AND PREFIX FOR KEY IN DB
    public static final String PREFIX_SNSGAME_GENERAL = ConfigHandle.instance().get("games")+"_"; //
    public static final String SUFFIX_ZM_SESSION = "_ZmSession"; // key luu session zingme expire time la 15 phut.
    public static final String SUFFIX_ZM_INFO = "_ZmInfo"; // key luu user info lay tu zingme
    public static final String SUFFIX_ZM_USER_ID = "_ZmId"; // key luu userId lay tu zingme
    public static final String SUFFIX_ZM_FRIENDS = "_ZmFriendList"; // key luu danh sach userIds.
    public static final String SUFFIX_XU = "_xu"; // key luu tien xu;
    public static final String SUFFIX_ONLINE = "_online"; // key kiem tra user nay da online hay chua. neu = "" la chua online, = serverIp la da online roi
    //public static final String
    
    // CONFIG FOR BILLING
    public static final String URL_REQUEST_BILLING = ConfigHandle.instance().get("url_billing");
    public static final String PRODUCTID = ConfigHandle.instance().get("productId");
    public static final String SECRET_KEY = ConfigHandle.instance().get("secretKey");
    
    public static final String KEY_BALANCE_SERVICE = "balance";
    public static final String KEY_PROMO_SERVICE= "promo";
    public static final String KEY_HOLD_CASH_SERVICE = "holdCash";
    public static final String KEY_RELEASE_CASH_SERVICE = "releaseCash";
    public static final String KEY_PAYOUT_CASH_SERVICE = "payOutCash";
    public static final String KEY_GET_HOLD_ID_SERVICE = "getHoldId";
    public static final String KEY_GET_HOLD_STATE_SERVICE = "holdState";
    public static final String KEY_PURCHASE_SERVICE = "purchase";
  
    public static final String SUFFIX_HOLD_ID = "_holdId";
    public static final String HOLD_ID_DEFAULT = "0000000000000000";
  
    public static final boolean ENABLE_PAYMENT = ConfigHandle.instance().get("enable_payment") == null?false:(ConfigHandle.instance().get("enable_payment").equals("1"));

    public static final String PREFIX_MYPLAY_PROFILE = "myplay_" ;
}
