package com.bamisu.log.gameserver.datamodel.IAP.store;

import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPChallenge;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPPackage;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoRewardIAPChallenge;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EConditionType;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPClaimType;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPPackageType;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPChallengeVO;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPPackageVO;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;
import java.util.stream.Collectors;

public class UserIAPStoreModel extends DataModel {

    public long uid;
    public int countPayment;
    public List<InfoIAPPackage> listPackage = new ArrayList<>();        //IAP package
    public List<InfoIAPChallenge> listChallenge = new ArrayList<>();        //IAP challenge


    private Object lockPackage = new Object();
    private Object lockChallenge = new Object();



    private void initPackage(Zone zone){
        //Add toan bo IAP package ma co san cua user (khong can dieu kien de xuat hien)
        List<IAPPackageVO> packCf = IAPBuyManager.getInstance().getIAPPackageStoreConfig(zone);
        List<String> listId = packCf.parallelStream().filter(cf -> !cf.haveConditionExsist()).map(cf -> cf.id).collect(Collectors.toList());
        listPackage.addAll(listId.parallelStream().map(InfoIAPPackage::create).collect(Collectors.toList()));
    }
    private void initChallenge(){
        //Add challenge
        List<IAPChallengeVO> challengeCf = IAPBuyManager.getInstance().getIAPChallengeConfig();
        listChallenge.addAll(challengeCf.parallelStream().map(InfoIAPChallenge::create).collect(Collectors.toList()));
    }


    public static UserIAPStoreModel createUserIAPStoreModel(long uid, Zone zone){
        UserIAPStoreModel userIAPStoreModel = new UserIAPStoreModel();
        userIAPStoreModel.uid = uid;
        userIAPStoreModel.initPackage(zone);
        userIAPStoreModel.initChallenge();
        userIAPStoreModel.saveToDB(zone);

        return userIAPStoreModel;
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

    public static UserIAPStoreModel copyFromDBtoObject(long uId, Zone zone) {
        UserIAPStoreModel userIAPStoreModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if(userIAPStoreModel == null){
            userIAPStoreModel = UserIAPStoreModel.createUserIAPStoreModel(uId, zone);
        }
        return userIAPStoreModel;
    }

    private static UserIAPStoreModel copyFromDBtoObject(String uId, Zone zone) {
        UserIAPStoreModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserIAPStoreModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserIAPStoreModel.class);
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
     * Update point inchallenge
     */
    public boolean increaseIAPChallenge(String id, EConditionType condition, int point, Zone zone){
        synchronized (lockChallenge){
            InfoIAPChallenge infoIAPChallenge = readIAPChallenge(id, zone);
            if(infoIAPChallenge == null) return false;

            if(condition == null){
                infoIAPChallenge.point += point;
            }else {
                switch (condition){
                    case CHAP_DUNGEON:
                        infoIAPChallenge.point = point;
                        break;
                    default:
                        infoIAPChallenge.point += point;
                        break;
                }
            }
            return saveToDB(zone);
        }
    }



    /**
     * Check mua dc package khong
     */
    public boolean canClaimIAPPackage(String id, Zone zone){
        synchronized (lockPackage){
            refreshIAPPackage(zone);
            for(InfoIAPPackage pack : readIAPPackage(zone)){
                if(pack.id.equals(id)){
                    return pack.canBuyMore(zone);
                }
            }
        }
        //TH khong ton tai tren data (Moi add them) -> luon dung
        return true;
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
     * Check de refresh trc ---> toi uu
     * @param id
     * @param zone
     * @return
     */
    public boolean claimIAPPackage(String id, Zone zone){
        synchronized (lockPackage){
            for(InfoIAPPackage pack : listPackage){
                if(pack.id.equals(id)){
                    pack.buy();
                    return saveToDB(zone);
                }
            }
            if(addIAPPackage(id)){
                return claimIAPPackage(id, zone);
            }
        }
        return false;
    }

    /**
     * Check de refresh trc ---> toi uu
     * @param id
     * @param point
     * @param type
     * @param zone
     * @return
     */
    public boolean claimIAPChallenge(String id, int point, EIAPClaimType type, Zone zone){
        synchronized (lockChallenge){
            for(InfoIAPChallenge challenge : listChallenge){
                if(challenge.id.equals(id) && challenge.claimIAPChallenge(point, type)){
                    return saveToDB(zone);
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

            //Neu co thay doi
            if(haveSave) saveToDB(zone);
        }
    }
    private void refreshIAPPChallenge(Zone zone){
        InfoIAPChallenge challengeData;
        boolean haveSave = false;

        synchronized (lockChallenge){
            Iterator<InfoIAPChallenge> iterator = listChallenge.iterator();
            while (iterator.hasNext()){
                challengeData = iterator.next();

                if(challengeData.activeSpecial()) haveSave = true;

                if(challengeData.autoIncreasePoint()) haveSave = true;

                if(challengeData.checkTimeDisapear(zone)){
                    iterator.remove();
                    haveSave = true;
                    break;
                }

                if(challengeData.checkTimeRefresh(zone)){
                    challengeData.refresh(zone);
                    haveSave = true;
                }
            }

            if(haveSave) saveToDB(zone);
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