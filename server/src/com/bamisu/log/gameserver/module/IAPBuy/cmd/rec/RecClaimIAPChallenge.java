package com.bamisu.log.gameserver.module.IAPBuy.cmd.rec;

import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPClaimType;
import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecClaimIAPChallenge extends BaseCmd {

    public String flatform;
    public int point;
    public String typeClaim;
    public String packageName;
    public String productId;
    public String purchaseToken;
    public String transactionId;

    public RecClaimIAPChallenge(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        productId = data.getUtfString(Params.PRODUCT_ID);

        if(data.containsKey(Params.TYPE)){
            typeClaim = data.getUtfString(Params.TYPE);
            point = data.getInt(Params.POSITION);
        }else {
            typeClaim = EIAPClaimType.ACTIVE_PREDIUM.getId();
        }

        flatform = data.getUtfString(Params.PLATFORM);
        packageName = data.getUtfString(Params.PACK_NAME);
        purchaseToken = data.getUtfString(Params.PURCHASE_TOKEN);
        transactionId = data.getUtfString(Params.TRANSACTION_ID);
    }
}
