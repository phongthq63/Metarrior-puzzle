package com.bamisu.gamelib.iap;

import com.bamisu.gamelib.entities.ServerConstant;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.ProductPurchase;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Create by Popeye on 2:00 PM, 5/5/2020
 */
public class AndroidIAPVerify implements IIAPVerify {

    private static AndroidIAPVerify ourInstance;
    //
    private HttpTransport HTTP_TRANSPORT = null;
    JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private GoogleCredential credential;
    private AndroidPublisher publisher;
    private AndroidPublisher.Purchases.Products products;

    public static AndroidIAPVerify getInstance() {
        if (ourInstance == null) {
            ourInstance = new AndroidIAPVerify();
        }
        return ourInstance;
    }

    private AndroidIAPVerify() {
        try {
            this.credential = GoogleCredential.fromStream(new FileInputStream(System.getProperty("user.dir") + "/config/log-account-service-key.json")).createScoped(AndroidPublisherScopes.all());
//            this.credential = GoogleCredential.fromStream(new FileInputStream("D:\\SmartFoxServer_2X_PUZZLE\\SFS2X\\config\\log-account-service-key.json")).createScoped(AndroidPublisherScopes.all());
//            this.credential = GoogleCredential.fromStream(
//                    new FileInputStream("D:\\SmartFoxServer_2X_Puzzle\\SFS2X\\config\\Google Play Android Developer-b6e4750aff44.json")).createScoped(AndroidPublisherScopes.all()
//            );
            this.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            this.publisher = new AndroidPublisher.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
            this.products = publisher.purchases().products();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized IAPVerifyResult verify(String packageName, String productId, String purchaseToken) {
        IAPVerifyResult res = new IAPVerifyResult();
        try {
            AndroidPublisher.Purchases.Products.Get product = products.get(packageName, productId, purchaseToken);
            ProductPurchase purchase = product.execute();
            if (purchase.getPurchaseState() != 0) {
                res.errorCode = ServerConstant.ErrorCode.ERR_SYS;
                res.message = "lỗi j đó!";
            }
            res.orderId = purchase.getOrderId();
            res.purchaseTimeMillis = purchase.getPurchaseTimeMillis();
            if(purchase.getPurchaseType() == null){
                res.purchaseType = 1;   // mua bang tien that
            }else {
                res.purchaseType = purchase.getPurchaseType(); // mua bang account test
            }
            res.purchaseState = purchase.getPurchaseState();

        } catch (IOException e) {
            res.errorCode = ServerConstant.ErrorCode.ERR_SYS;
            res.message = "lỗi j đó!";
            e.printStackTrace();
        }
        return res;
    }
}