package com.bamisu.log.gameserver.datamodel.bag.entities;

import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.campaign.entities.UserMainCampaignDetail;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.adventure.AdventureManager;
import com.bamisu.log.gameserver.module.adventure.EAdventureReward;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.defines.EGiftVip;
import com.smartfoxserver.v2.entities.Zone;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AdventureModel extends DataModel {
    public long uid;
    public String timeFastReward = "";
    public FastRewardDataVO reward = null;
    public CampaignFinishVO timeLoot = new CampaignFinishVO();
//    public Map<String, Integer> mapReward = new ConcurrentHashMap<>(); //Money - Surplus
    public Map<String, Integer> mapSurplus = new ConcurrentHashMap<>(); //Money - Surplus
    public AdventureModel(){}



    public AdventureModel(long uid){
        this.uid = uid;
        initTime();
//        initReward();
    }

    private void initTime() {
        UserMainCampaignDetail userMainCampaignDetail = new UserMainCampaignDetail();
        CampaignFinishVO campaignFinishVO = new CampaignFinishVO(userMainCampaignDetail, Utils.timeNowString());
        this.timeLoot = campaignFinishVO;
        this.timeFastReward = AdventureManager.getInstance().getTime();
        this.mapSurplus.put(MoneyType.ESSENCE.getId(), 0);

    }

    private void initReward(long uid, Zone zone){
//        for (FastRewardVO fastRewardVO: AdventureManager.getInstance().getFastRewardConfig()){
            this.reward = new FastRewardDataVO(AdventureManager.getInstance().getFastRewardConfig().get(EAdventureReward.FREE.getId()));
            this.reward.count += VipManager.getInstance().getBonus(uid, zone, EGiftVip.FREE_TURN_FAST_REWARD);
//            break;
//        }
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static AdventureModel copyFromDBtoObject(long uid, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uid), zone);
    }

    public static AdventureModel copyFromDBtoObject(String uid, Zone zone) {
        AdventureModel pInfo = null;
        try {
            String str = (String) getModel(uid, AdventureModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, AdventureModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {

        }
        if (pInfo == null) {
            pInfo = new AdventureModel(Long.parseLong(uid));
            pInfo.saveToDB(zone);

            AdventureManager.getInstance().updateNotify(Long.parseLong(uid), zone);
        }

        if (pInfo.reward == null){
            pInfo.initReward(Long.parseLong(uid), zone);
            pInfo.saveToDB(zone);
        }
        return pInfo;
    }

    public static AdventureModel create(long uid, Zone zone) {
        AdventureModel d = new AdventureModel(uid);
        if (d.saveToDB(zone)) {
            return d;
        }
        return null;
    }
}
