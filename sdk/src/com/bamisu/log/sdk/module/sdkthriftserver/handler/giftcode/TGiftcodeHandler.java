package com.bamisu.log.sdk.module.sdkthriftserver.handler.giftcode;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.TokenResourcePackage;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.giftcode.GiftcodeManager;
import com.bamisu.log.sdk.module.giftcode.entities.GiftCodeExtra;
import com.bamisu.log.sdk.module.giftcode.model.GiftcodeModel;
import com.bamisu.log.sdkthrift.entities.TActiveGiftcodeResult;
import com.bamisu.log.sdkthrift.exception.ThriftSVException;
import com.bamisu.log.sdkthrift.service.giftcode.GiftcodeService;
import com.bamisu.gamelib.utils.Utils;
import org.apache.thrift.TException;

import java.util.List;

/**
 * Create by Popeye on 4:50 PM, 5/13/2020
 */
public class TGiftcodeHandler implements GiftcodeService.Iface {
    @Override
    public TActiveGiftcodeResult activeGiftcode(String code, int serverID, String userID, String accountID) throws ThriftSVException, TException {
        TActiveGiftcodeResult tActiveGiftcodeResult = new TActiveGiftcodeResult();

        GiftcodeModel giftcodeModel = GiftcodeModel.copyFromDB(code, SDKDatacontroler.getInstance());
        GiftcodeManager.getInstance().activeCode(
                giftcodeModel,
                serverID,
                userID,
                accountID
        );

        tActiveGiftcodeResult.serverID = serverID;
        tActiveGiftcodeResult.userID = userID;
        tActiveGiftcodeResult.accountID = accountID;
        List<ResourcePackage> gifts = giftcodeModel.gifts;
        if (giftcodeModel.extras.size() > 0) {
            for (GiftCodeExtra extra : giftcodeModel.extras) {
                gifts.add(new TokenResourcePackage(extra.moneyType, extra.getRandom()));
            }
        }

        tActiveGiftcodeResult.gifts = Utils.toJson(gifts);
        return tActiveGiftcodeResult;
    }
}
