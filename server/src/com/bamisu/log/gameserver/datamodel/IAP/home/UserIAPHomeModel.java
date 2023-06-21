package com.bamisu.log.gameserver.datamodel.IAP.home;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPChallenge;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPPackage;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoRewardIAPChallenge;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPChallengeVO;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPPackageVO;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPClaimType;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPPackageType;
import com.bamisu.log.gameserver.module.IAPBuy.entities.ConditionVO;
import com.bamisu.log.gameserver.module.IAPBuy.entities.IAPInstanceVO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;
import java.util.stream.Collectors;

public class UserIAPHomeModel extends DataModel {

    public long uid;
    public int countPayment = 0;
    public List<InfoIAPPackage> listPackage = new ArrayList<>();        //IAP package
    public List<InfoIAPChallenge> listChallenge = new ArrayList<>();        //IAP challenge
    public Map<String, IAPInstanceVO> mapTrigger = new HashMap<>();     //Save nhung goi da xuat hien do dat dc thanh tuu


    private final Object lockIAP = new Object();
    private final Object lockPackage = new Object();
    private final Object lockChallenge = new Object();




    private void init(){
        initPackage();
        initChallenge();
    }
    private void initPackage(){
        //Add toan bo IAP package ma co san cua user (khong can dieu kien de xuat hien)
        List<IAPPackageVO> packCf = IAPBuyManager.getInstance().getIAPPackageHomeConfig();
        List<String> listId = packCf.parallelStream().filter(cf -> !cf.haveConditionExsist()).map(cf -> cf.id).collect(Collectors.toList());
        listPackage.addAll(listId.parallelStream().map(id -> InfoIAPPackage.create(id)).collect(Collectors.toList()));
    }
    private void initChallenge(){

    }

    public static UserIAPHomeModel createUserIAPHomeModel(long uid, Zone zone){
        UserIAPHomeModel userIAPHomeModel = new UserIAPHomeModel();
        userIAPHomeModel.uid = uid;
        userIAPHomeModel.countPayment = 0;
        userIAPHomeModel.init();
        userIAPHomeModel.saveToDB(zone);

        return userIAPHomeModel;
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

    public static UserIAPHomeModel copyFromDBtoObject(long uId, Zone zone) {
        UserIAPHomeModel userIAPHomeModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if(userIAPHomeModel == null){
            userIAPHomeModel = UserIAPHomeModel.createUserIAPHomeModel(uId, zone);
        }
        return userIAPHomeModel;
    }

    private static UserIAPHomeModel copyFromDBtoObject(String uId, Zone zone) {
        UserIAPHomeModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserIAPHomeModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserIAPHomeModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*---------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Lay list package
     * @param zone
     * @return
     */
    public List<InfoIAPPackage> readIAPPackage(Zone zone){
        synchronized (lockPackage){
            refreshIAPPackage(zone);
            return listPackage;
        }
    }

    /**
     * Lay IAP challenge data
     */
    public List<InfoIAPChallenge> readIAPChallenge(Zone zone){
        synchronized (lockChallenge){
            refreshIAPPChallenge(zone);
            return listChallenge;
        }
    }
    public InfoIAPChallenge readIAPChallenge(String id, Zone zone){
        synchronized (lockChallenge){
            for(InfoIAPChallenge challenge : readIAPChallenge(zone)){
                if(challenge.id.equals(id)){
                    return challenge;
                }
            }
        }
        return null;
    }

    /**
     * Add package
     */
    public boolean addIAPPackage(String id){
        synchronized (lockPackage){
            for(InfoIAPPackage data : listPackage){
                if(data.id.equals(id)){
                    return false;
                }
            }
            listPackage.add(InfoIAPPackage.create(id));
        }
        return true;
    }
    public boolean addIAPChallenge(IAPChallengeVO challengeCf){
        synchronized (lockChallenge){
            for(InfoIAPChallenge data : listChallenge){
                if(data.id.equals(challengeCf.id)){
                    return false;
                }
            }
            listChallenge.add(InfoIAPChallenge.create(challengeCf));
        }
        return true;
    }



    /**
     * Check mua dc package khong
     */
    public boolean canClaimIAPPackage(String id, Zone zone){
        synchronized (lockPackage){
            for(InfoIAPPackage pack : readIAPPackage(zone)){
                if(pack.id.equals(id)){
                    return pack.canBuyMore(zone);
                }
            }
        }
        return false;
    }
    public boolean canClaimIAPChallenge(String id, int point, EIAPClaimType type, Zone zone){
        synchronized (lockChallenge){
            for(InfoIAPChallenge challenge : readIAPChallenge(zone)){
                if(challenge.id.equals(id)) return challenge.canClaimIAPChallenge(point, type);
            }
        }
        return false;
    }



    /**
     * Mua package
     */
    public boolean claimIAPPackage(String id, Zone zone){
        synchronized (lockPackage){
            //Kiem tra cac goi co san
            for(InfoIAPPackage pack : readIAPPackage(zone)){
                if(pack.id.equals(id)){
                    pack.buy();
                    return saveToDB(zone);
                }
            }
        }
        return false;
    }
    public boolean claimIAPChallenge(String id, int point, EIAPClaimType type, Zone zone){
        IAPChallengeVO iapCf;

        synchronized (lockChallenge){
            for(InfoIAPChallenge challenge : readIAPChallenge(zone)){
                if(challenge.id.equals(id)){
                    switch (type){
                        case ACTIVE_PREDIUM:
                            iapCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
                            switch (iapCf.increaseAchievementBy()){
                                case POINT:
                                case CHAP:
                                case CHECKIN_NEW_DAY:
                                    break;
                                case NEW_DAY:
                                    challenge.timeStampDispear = Utils.getTimestampInSecond();
                                    break;
                            }
                        case FREE_PREDIUM:
                        case PREDIUM:
                        case FREE:
                        case ALL:
                            if(challenge.claimIAPChallenge(point, type)){
                                return saveToDB(zone);
                            }
                            return false;
                    }
                    break;
                }
            }
        }
        return false;
    }


    /**
     * Lam moi model
     * @param zone
     */
    private void refreshIAPPackage(Zone zone){
        IAPPackageVO packageCf;
        boolean haveSave = false;

        synchronized (lockPackage){
            Iterator<InfoIAPPackage> iterator = listPackage.iterator();
            while (iterator.hasNext()){
                InfoIAPPackage pack = iterator.next();
                packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(pack.id, zone);
                if(packageCf == null) continue;

                //Tu dong kick hoat
                if(pack.activeSpecial()) haveSave = true;
                //Kiem tra time
                switch (EIAPPackageType.fromID(packageCf.readType())){
                    case EXTANT:    //Khong co gioi han mua - khong the lam moi - khong bien mat
                    case BUY:   //Co gioi han mua - khong the lam moi - khong bien mat
                        break;
                    case TIME:  //Khong co gioi han mua - khong lam moi - co bien mat
                    case BUY_TIME:   //Co gioi han mua - khong lam moi - co bien mat
                        if(pack.checkTimeDisapear(zone)){
                            iterator.remove();
                            haveSave = true;
                        }
                        break;
                    case REFRESH:   //Khong co gioi han mua - co lam moi - khong bien mat
                    case BUY_REFRESH:  //Co gioi han mua - co the lam moi - ko bien mat
                        if(pack.checkTimeRefresh(zone)){
                            pack.refresh(zone);
                            haveSave = true;
                        }
                        break;
                    case TIME_REFRESH:  //Khong gioi han mua - co lam moi - co bien mat
                    case BUY_TIME_REFRESH: //Co gioi han mua - co the lam moi - co bien mat
                        if(pack.checkTimeDisapear(zone)){
                            iterator.remove();
                            haveSave = true;
                            break;
                        }
                        if(pack.checkTimeRefresh(zone)){
                            pack.refresh(zone);
                            haveSave = true;
                        }
                        break;
                }
            }

            if(haveSave) saveToDB(zone);
        }
    }
    private void refreshIAPPChallenge(Zone zone){
        boolean haveSave = false;

        synchronized (lockChallenge){
            Iterator<InfoIAPChallenge> iterator = listChallenge.iterator();
            InfoIAPChallenge challengeData;
            while (iterator.hasNext()){
                challengeData = iterator.next();
                //Active TH db
                if(challengeData.activeSpecial()) haveSave = true;
                //Tu dong tang diem
                if(challengeData.autoIncreasePoint()) haveSave = true;
                //Kiem tra thoi gian bien mat
                if(challengeData.checkTimeDisapear(zone)){
                    iterator.remove();
                    haveSave = true;
                    break;
                }
                //Kiem tra thoi gian lam moi
                if(challengeData.checkTimeRefresh(zone)){
                    challengeData.refresh(zone);
                    haveSave = true;
                }
            }

            if(haveSave) saveToDB(zone);
        }
    }

    public List<String> readListPackageTrigged(){
        synchronized (lockIAP){
            return new ArrayList<>(mapTrigger.keySet());
        }
    }


    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Khi nao dk tang se check
     * Kiem tra goi da tung kich hoat chua (kich hoat != mua)
     * Kich hoat co the chua mua hoac da mua
     * @param condition la diem, level, chap,... NEW (Phong TH co nh dk)
     * @return
     */
    public boolean isTrigger(String id, ConditionVO condition, Zone zone){
        if(condition == null){
            return false;
        }
        // xay ra khi ko ton tai goi hay goi level cao hon dc create
        //Neu ko ton tai thi tao moi
        synchronized (lockIAP){
            if(!mapTrigger.containsKey(id)){
                List<ConditionVO> listCondition = new ArrayList<>();
                //Goi IAP
                IAPPackageVO packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(id, zone);
                if(packageCf != null){
                    for(String idCondition : packageCf.exsistCondition){
                        listCondition.add(new ConditionVO(idCondition, 0));
                    }

                }else {
                    //Thap IAP
                    IAPChallengeVO challengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(id);
                    if(challengeCf == null) return false;
                    for(String idCondition : challengeCf.exsistCondition){
                        listCondition.add(new ConditionVO(idCondition, 0));
                    }
                }

                mapTrigger.put(id, IAPInstanceVO.create(id, listCondition));
            }
            //Kiem tra co goi dc create co dk cao hon ko
            IAPInstanceVO save = mapTrigger.get(id);
            for(ConditionVO conditionSave : save.condition){
                if(!conditionSave.id.equals(condition.id)) continue;

                if(conditionSave.count >= condition.count) return false;
                conditionSave.count = condition.count;
                return saveToDB(zone);
            }
            //TH dk moi
            save.condition.add(condition);
            return saveToDB(zone);
        }
    }

    /*---------------------------------------------------------------------------------------------------------------*/
    public void increasePayment(Zone zone){
        synchronized (lockPackage){
            synchronized (lockChallenge){
                countPayment++;
                saveToDB(zone);
            }
        }
    }
}
