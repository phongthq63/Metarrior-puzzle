package com.bamisu.log.sdk.module.giftcode.model;

import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.giftcode.entities.GiftCodeExtra;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 2:08 PM, 4/22/2020
 */
public class GiftcodeModel extends DataModel {
    public String code;
    public int expired = Utils.getTimestampInSecond() + 24 * 60 * 60;
    public List<ResourcePackage> gifts = new ArrayList<>();
    public int max = 1;
    public int used = 0;
    public List<GiftCodeExtra> extras = new ArrayList<>();

    public boolean save(SDKDatacontroler sdkDatacontroler) {
        try {
            this.saveModel(code, sdkDatacontroler.getController());
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GiftcodeModel copyFromDB(String code, SDKDatacontroler sdkDatacontroler) {
        GiftcodeModel model = null;
        try {
            String str = (String) getModel(code, GiftcodeModel.class, sdkDatacontroler.getController());
            if (str != null) {
                model = Utils.fromJson(str, GiftcodeModel.class);
                if (model != null) {
//                    model.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        return model;
    }

    public static GiftcodeModel create(int expired, int max, List<ResourcePackage> gifts) {
        return create(Utils.genGiftCode(), expired, max, gifts);
    }

    public static GiftcodeModel create(String code, int expired, int max, List<ResourcePackage> gifts) {
        GiftcodeModel giftcodeModel = new GiftcodeModel();
        giftcodeModel.code = code;
        giftcodeModel.expired = Utils.getTimestampInSecond() + expired * 24 * 60 * 60;;
        giftcodeModel.gifts = gifts;
        giftcodeModel.max = max;

        if (giftcodeModel.save(SDKDatacontroler.getInstance())) {
            return giftcodeModel;
        }

        return null;

    }

    public boolean expired() {
        return Utils.getTimestampInSecond() > expired;
    }

    public boolean isGone() {
        return used >= max;
    }

    public boolean use(SDKDatacontroler sdkDatacontroler) {
        used++;
        if (!save(sdkDatacontroler)) {
            used--;
            return false;
        }

        return true;
    }
}
