package com.bamisu.gamelib.sql.sdk.dbo;

import com.bamisu.gamelib.utils.Utils;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "iap")
public class IAPDBO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "uid")
    public long uid;

    @Column(name = "product_id")
    public String productId;

    @Column(name = "purchase_token")
    public String purchaseToken;

    @Column(name = "transaction_id")
    public String transactionId;

    @Column(name = "time")
    public int timeStamp;

    public int platform;

    public int sid;

    public String account;

    public static IAPDBO create(long uid, String productId, String purchaseToken, String transactionId, int platform, int sid, String account) {
        IAPDBO iapPackageDBO = new IAPDBO();
        iapPackageDBO.uid = uid;
        iapPackageDBO.productId = productId;
        iapPackageDBO.purchaseToken = purchaseToken;
        iapPackageDBO.transactionId = transactionId;
        iapPackageDBO.timeStamp = Utils.getTimestampInSecond();
        iapPackageDBO.platform = platform;
        iapPackageDBO.sid = sid;
        iapPackageDBO.account = account;

        return iapPackageDBO;
    }
}
