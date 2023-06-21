package com.bamisu.log.gameserver.datamodel.IAP.entities;

import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPAchievementVO;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPChallengeVO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPClaimType;
import com.couchbase.client.deps.com.fasterxml.jackson.annotation.JsonProperty;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InfoIAPChallenge implements IIAPItem{
    public String id;
    public boolean predium;
    public int point;
    public List<InfoRewardIAPChallenge> reward = new ArrayList<>();
    @JsonProperty("tsr")
    public int timeStampRefresh;
    @JsonProperty("tsd")
    public int timeStampDispear;
    @JsonProperty("tss")
    public int timeStampSave;
    public boolean activeSpecial;

    private static final int time1day = 86400;



    public static InfoIAPChallenge create(IAPChallengeVO cf) {
        InfoIAPChallenge infoIAPChallenge = new InfoIAPChallenge();
        infoIAPChallenge.id = cf.id;
        infoIAPChallenge.predium = false;
        infoIAPChallenge.point = 0;
        infoIAPChallenge.timeStampRefresh = Utils.getTimestampInSecond();
        infoIAPChallenge.timeStampDispear = infoIAPChallenge.timeStampRefresh;
        infoIAPChallenge.timeStampSave = infoIAPChallenge.timeStampRefresh;

        return infoIAPChallenge;
    }
    public static InfoIAPChallenge create(InfoIAPChallenge data) {
        InfoIAPChallenge infoIAPChallenge = new InfoIAPChallenge();
        infoIAPChallenge.id = data.id;
        infoIAPChallenge.predium = data.predium;
        infoIAPChallenge.point = data.point;

        IAPChallengeVO challengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(data.id);
        if(challengeCf != null){
            out_loop:
            for(IAPAchievementVO achievement : challengeCf.achievement){
                for(InfoRewardIAPChallenge reward : data.reward){
                    if(achievement.point == reward.point){
                        infoIAPChallenge.reward.add(InfoRewardIAPChallenge.create(reward));
                        continue out_loop;
                    }
                }
                if(achievement.free.isEmpty()){
                    infoIAPChallenge.reward.add(InfoRewardIAPChallenge.create(achievement.point, true, false));
                }else if (achievement.predium.isEmpty()) {
                    infoIAPChallenge.reward.add(InfoRewardIAPChallenge.create(achievement.point, false, true));
                }else {
                    infoIAPChallenge.reward.add(InfoRewardIAPChallenge.create(achievement.point, false, false));
                }
            }
        }else {
            infoIAPChallenge.reward = data.reward.stream().map(InfoRewardIAPChallenge::create).collect(Collectors.toList());
        }

        infoIAPChallenge.timeStampRefresh = data.timeStampRefresh;
        infoIAPChallenge.timeStampDispear = data.timeStampDispear;
        infoIAPChallenge.timeStampSave = data.timeStampSave;
        infoIAPChallenge.activeSpecial = data.activeSpecial;

        return infoIAPChallenge;
    }



    @Override
    public void refresh(Zone zone) {
        IAPChallengeVO challengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
        if(challengeCf == null) return;

        this.point = 0;
        this.predium = false;
        this.activeSpecial = false;
        this.reward.clear();
        this.timeStampRefresh = Utils.getTimestampInSecond();
    }

    @Override
    public boolean autoIncreasePoint() {
        IAPChallengeVO challengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
        if(challengeCf == null) return false;

        switch (challengeCf.increaseAchievementBy()){
            case POINT:
                return false;
            case CHAP:
                return false;
            case NEW_DAY:
            {
                //Chua kich hoat package
                if(!predium) return false;
                //Da kich hoat package
                int now = Utils.getTimestampInSecond();     //Tg hien tai (giay)
                int day = (now - timeStampDispear) / time1day;     //So ngay troi qua (cu 24h = 1ngay)
                int newPoint = day;
                if(Utils.isNewDay(timeStampDispear + time1day * day)) newPoint += 1;   //Neu troi qua ngay moi --> +1
                if(newPoint > this.point){
                    this.point = newPoint;
                    return true;
                }
                return false;
            }
            case CHECKIN_NEW_DAY:
            {
                if(timeStampSave <= 0) timeStampSave = Utils.getTimestampInSecond();
                if(Utils.isNewDay(timeStampSave)){
                    this.point++;
                    timeStampSave = Utils.getTimestampInSecond();
                    return true;
                }else {
                    return false;
                }
            }
        }
        return false;
    }


    @Override
    public boolean checkTimeRefresh(Zone zone) {
        IAPChallengeVO challengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
        if(challengeCf == null || !challengeCf.canRefresh()) return false;
        return TimeUtils.isTimeTo(ETimeType.fromID(challengeCf.timeRefresh), timeStampRefresh);
    }

    public boolean activeSpecial(){
        IAPChallengeVO iapChallengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
        if(iapChallengeCf == null) return false;

        //TH goi dac biet
        if(iapChallengeCf.id.equals("welcome_7_day") &&
                point > iapChallengeCf.achievement.get(iapChallengeCf.achievement.size() - 1).point){
            //Sau khi da dat full point
            //Het reward co the nhan -> bien mat sau 1h
            if(!activeSpecial &&
                    InfoIAPChallenge.create(this).reward.stream().
                            noneMatch(reward -> Arrays.stream(EIAPClaimType.values()).
                                    filter(type -> !type.getId().equals(EIAPClaimType.ACTIVE_PREDIUM.getId())).
                                    anyMatch(reward::canClaimReward)
                            )
            ){
                activeSpecial = true;
                timeStampDispear = Utils.getTimestampInSecond();
                return true;
            }
        }

        //Da mua goi
        if(predium){
            //TH goi dac biet
            if((iapChallengeCf.id.equals("anese_7_day") || iapChallengeCf.id.equals("ektar_7_day") || iapChallengeCf.id.equals("samson_7_day") || iapChallengeCf.id.equals("gandaar_7_day")) &&
                    point > iapChallengeCf.achievement.get(iapChallengeCf.achievement.size() - 1).point){
                //Sau khi da dat full point
                //Het reward co the nhan -> bien mat sau 1h
                if(!activeSpecial &&
                        InfoIAPChallenge.create(this).reward.stream().
                                noneMatch(reward -> Arrays.stream(EIAPClaimType.values()).
                                        filter(type -> !type.getId().equals(EIAPClaimType.ACTIVE_PREDIUM.getId())).
                                        anyMatch(reward::canClaimReward)
                                )
                ){
                    activeSpecial = true;
                    timeStampDispear = Utils.getTimestampInSecond();
                    return true;
                }
            }

            return false;
        }else {
            //TH chua mua goi

            return false;
        }
    }
    @Override
    public boolean checkTimeDisapear(Zone zone) {
        IAPChallengeVO iapChallengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);

        //TH goi dac biet
        if(iapChallengeCf.id.equals("welcome_7_day") &&
                point >= iapChallengeCf.achievement.get(iapChallengeCf.achievement.size() - 1).point){
            //Co reward co the nhan -> khong bien mat
            if(InfoIAPChallenge.create(this).reward.stream().
                    anyMatch(reward -> Arrays.stream(EIAPClaimType.values()).
                            filter(type -> !type.getId().equals(EIAPClaimType.ACTIVE_PREDIUM.getId())).
                            anyMatch(reward::canClaimReward)
                    )) return checkTimeDisapear(2);

            //Het reward co the nhan -> bien mat sau 1h
            return checkTimeDisapear(3);
        }

        //Da kick hoat goi
        if(predium){
            //TH goi dac biet
            if((iapChallengeCf.id.equals("anese_7_day") || iapChallengeCf.id.equals("ektar_7_day") || iapChallengeCf.id.equals("samson_7_day") || iapChallengeCf.id.equals("gandaar_7_day")) &&
                    point >= iapChallengeCf.achievement.get(iapChallengeCf.achievement.size() - 1).point){
                //Co reward co the nhan -> khong bien mat
                if(InfoIAPChallenge.create(this).reward.stream().
                        anyMatch(reward -> Arrays.stream(EIAPClaimType.values()).
                                filter(type -> !type.getId().equals(EIAPClaimType.ACTIVE_PREDIUM.getId())).
                                anyMatch(reward::canClaimReward)
                        )) return checkTimeDisapear(2);

                //Het reward co the nhan -> bien mat sau 1h
                return checkTimeDisapear(3);
            }

            //Bien mat sau 7 ngay
            return checkTimeDisapear(1);
        }else {

            //Chua kich hoat goi -> Bien mat sau 24h
            return checkTimeDisapear(0);
        }
    }
    public boolean checkTimeDisapear(int index){
        IAPChallengeVO challengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
        if(challengeCf == null) return true;
        if(!challengeCf.haveLimitTime()) return false;
        return TimeUtils.isTimeTo(ETimeType.fromID(challengeCf.timeExsist.get(index)), timeStampDispear);
    }

    public int readTimeDispear(){
        IAPChallengeVO iapChallengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
        if(iapChallengeCf == null || iapChallengeCf.timeExsist.size() <= 0) return -1;
        int index = 0;

        //Da kich hoat goi
        if(predium){
            index = 1;

            //Goi dac biet
            if(iapChallengeCf.id.equals("anese_7_day") || iapChallengeCf.id.equals("ektar_7_day") || iapChallengeCf.id.equals("samson_7_day") || iapChallengeCf.id.equals("gandaar_7_day")){
                //TH point full
                if(point > iapChallengeCf.achievement.get(iapChallengeCf.achievement.size() - 1).point) index = 2;
                //TH da nhan het reward
                if(activeSpecial) index = 3;
            }
        }else {
            //Chua kich hoat goi
            index = 0;
        }

        //TH xu ly dac biet
        if(activeSpecial) index = 3;

        return TimeUtils.getDeltaTimeToTime(ETimeType.fromID(iapChallengeCf.timeExsist.get(index)), timeStampDispear);
    }

    @Override
    public boolean canBuyMore(Zone zone) {
        return false;
    }

    public boolean canClaimIAPChallenge(int point, EIAPClaimType type){
        switch (type){
            case ACTIVE_PREDIUM:
                return !predium;
            case FREE_PREDIUM:
            case PREDIUM:
                if(!predium) return false;
            case FREE:
            {
                //TH co tren data
                for(InfoRewardIAPChallenge ach : reward){
                    if(point <= this.point && ach.point == point){
                        return ach.canClaimReward(type);
                    }
                }
                //TH khong co tren data
                //Check config : neu khong co false
                IAPChallengeVO iapChallengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
                if(iapChallengeCf == null) return false;
                for(IAPAchievementVO rewardCf : iapChallengeCf.achievement){
                    if(rewardCf.point == point){
                        switch (type){
                            case FREE:
                                if(rewardCf.free.isEmpty()) return false;
                                break;
                            case PREDIUM:
                                if(rewardCf.predium.isEmpty()) return false;
                                break;
                            case FREE_PREDIUM:
                                if(rewardCf.free.isEmpty() && rewardCf.predium.isEmpty()) return false;
                                break;
                        }
                        return true;
                    }
                }
                return false;
            }
            case ALL:
            {
                List<Integer> listPointData = new ArrayList<>();

                //Check data da ton tai con cai co the nhan khong
                for(InfoRewardIAPChallenge ach : reward){
                    //Check data ton tai vs config
                    if(ach.point <= this.point){
                        //TH kich hoat goi r
                        if(predium){
                            if(ach.canClaimReward(EIAPClaimType.FREE_PREDIUM)) return true;
                        }else {
                            if(ach.canClaimReward(EIAPClaimType.FREE)) return true;
                        }
                    }
                    //Luu lai cac point da kiem tra r
                    listPointData.add(ach.point);
                }
                //Check data tren config
                IAPChallengeVO iapChallengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
                if(iapChallengeCf == null) return false;
                for(IAPAchievementVO rewardCf : iapChallengeCf.achievement){
                    //Bo qua cac index da kiem tra va lon hon point can check
                    if(listPointData.contains(rewardCf.point)) continue;
                    if(rewardCf.point > this.point) continue;
                    //Kiem tra theo kich hoat goi
                    if(predium){
                        if(!rewardCf.free.isEmpty() || !rewardCf.predium.isEmpty()) return true;
                    }else {
                        if(!rewardCf.free.isEmpty()) return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public boolean claimIAPChallenge(int point, EIAPClaimType type){
        switch (type){
            case ACTIVE_PREDIUM:
                predium = true;
                return true;
            case FREE_PREDIUM:
            case PREDIUM:
            case FREE:
            {
                //TH da ton tai tren data
                for(InfoRewardIAPChallenge ach : reward){
                    if(ach.point == point && ach.claimReward(type)) return true;
                }
                //TH khong ton tai tren data
                //Check config
                IAPChallengeVO iapChallengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
                if(iapChallengeCf == null) return false;
                for(IAPAchievementVO achievement : iapChallengeCf.achievement){
                    if(achievement.point == point){
                        switch (type){
                            case FREE_PREDIUM:
                                reward.add(InfoRewardIAPChallenge.create(point, true, true));
                                return true;
                            case PREDIUM:
                                if(achievement.free.isEmpty()){
                                    reward.add(InfoRewardIAPChallenge.create(point, true, true));
                                }else {
                                    reward.add(InfoRewardIAPChallenge.create(point, false, true));
                                }
                                return true;
                            case FREE:
                                if(achievement.predium.isEmpty()){
                                    reward.add(InfoRewardIAPChallenge.create(point, true, true));
                                }else {
                                    reward.add(InfoRewardIAPChallenge.create(point, true, false));
                                }
                                return true;
                        }
                    }
                }
                return false;
            }
            case ALL:
            {
                List<Integer> listPointData = new ArrayList<>();

                //Xu ly cac TH co tren database truoc
                for(InfoRewardIAPChallenge ach : reward){
                    if(ach.point <= this.point){
                        //Kiem tra dua theo predium (kich hoat goi hay chua)
                        if(predium){
                            if(!ach.claimReward(EIAPClaimType.FREE_PREDIUM)) return false;
                        }else {
                            if(!ach.claimReward(EIAPClaimType.FREE)) return false;
                        }
                    }
                    //Save cac point da xu ly
                    listPointData.add(ach.point);
                }
                //TH khong ton tai tren data
                //Check config
                IAPChallengeVO iapChallengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
                if(iapChallengeCf == null) return false;
                for(IAPAchievementVO achievement : iapChallengeCf.achievement){
                    if(listPointData.contains(achievement.point)) continue;
                    if(achievement.point > this.point) continue;

                    if(predium){
                        reward.add(InfoRewardIAPChallenge.create(achievement.point, true, true));
                    }else {
                        if(achievement.predium.isEmpty()){
                            reward.add(InfoRewardIAPChallenge.create(achievement.point, true, true));
                        }else {
                            reward.add(InfoRewardIAPChallenge.create(achievement.point, true, false));
                        }
                    }
                }

                return true;
            }
        }
        return false;
    }
}
