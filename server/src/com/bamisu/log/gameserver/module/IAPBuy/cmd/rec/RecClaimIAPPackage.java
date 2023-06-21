package com.bamisu.log.gameserver.module.IAPBuy.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecClaimIAPPackage extends BaseCmd {

    public String flatform;
    public String packageName;
    public String productId;
    public String purchaseToken;
    public String transactionId;


    public RecClaimIAPPackage(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        flatform = data.getUtfString(Params.PLATFORM);
        packageName = data.getUtfString(Params.PACK_NAME);
        productId = data.getUtfString(Params.PRODUCT_ID);
        purchaseToken = data.getUtfString(Params.PURCHASE_TOKEN);
        transactionId = data.getUtfString(Params.TRANSACTION_ID);
    }
}
