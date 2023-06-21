package com.bamisu.log.gameserver.module.event.event.black_friday;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.IAP.event.entities.InfoIAPSale;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPChallengeVO;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPPackageVO;
import com.bamisu.log.gameserver.module.event.EventInGameManager;
import com.bamisu.log.gameserver.module.event.defind.EEventInGame;
import com.bamisu.log.gameserver.module.mail.MailUtils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class BlackFridayEventManager {
    private static BlackFridayEventManager ourInstance = new BlackFridayEventManager();

    public static BlackFridayEventManager getInstance() {
        return ourInstance;
    }

    private BlackFridayEventManager() { }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public void sendGiftEvent(long uid, String id, Zone zone){
        if(isTimeEndEvent(zone)) return;

        int count = 0;

        InfoIAPSale dataIAPSale = IAPBuyManager.getInstance().getInfoIAPSale(uid, id, zone);
        if(dataIAPSale != null){
            count = Math.round(dataIAPSale.cost);
        }else {
            //Package
            IAPPackageVO packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(id, zone);
            if(packageCf != null){
                count = Math.round(packageCf.cost);
            }
            //Challenge
            IAPChallengeVO challengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
            if(challengeCf != null){
                count = (int) Math.round(challengeCf.cost);
            }
        }

        if(count > 0){
            List<ResourcePackage> listGift = new ArrayList<>();
            listGift.add(new ResourcePackage("MON1000", 1000 * count));
            MailUtils.getInstance().sendMailUser(uid, listGift, "1022", "1023", new ArrayList<>(), zone);
        }
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    public int getTimeEndEvent(Zone zone){
        return EventInGameManager.getInstance().getEventSpecial(zone).getOrDefault(EEventInGame.BLACK_FRIDAY.getId(), 0);
    }

    public boolean isTimeEndEvent(Zone zone){
        int timeEnd = getTimeEndEvent(zone);
        if(timeEnd < 0) return false;
        return  timeEnd < Utils.getTimestampInSecond();
    }
}
