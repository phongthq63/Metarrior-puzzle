package com.bamisu.gamelib.sql.game.dbo;

import com.bamisu.gamelib.utils.Utils;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "purchase_iap")
public class IAPPackageDBO implements Serializable {
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

    public static IAPPackageDBO create(long uid, String productId, String purchaseToken, String transactionId, int platform) {
        IAPPackageDBO iapPackageDBO = new IAPPackageDBO();
        iapPackageDBO.uid = uid;
        iapPackageDBO.productId = productId;
        iapPackageDBO.purchaseToken = purchaseToken;
        iapPackageDBO.transactionId = transactionId;
        iapPackageDBO.timeStamp = Utils.getTimestampInSecond();
        iapPackageDBO.platform = platform;

        return iapPackageDBO;
    }
}
