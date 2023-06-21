package com.bamisu.log.gameserver.datamodel.bag;

import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.bag.entities.ChangeMoneyResult;
import com.bamisu.log.gameserver.datamodel.bag.entities.ChangeSpecialItemResult;
import com.bamisu.gamelib.item.entities.SageEquipDataVO;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.datamodel.bag.entities.EnergyChargeInfo;
import com.bamisu.log.gameserver.module.bag.config.entities.EnergyChangeVO;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateVip;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.define.SpecialItem;
import com.bamisu.gamelib.item.entities.*;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.TransactionDetail;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import org.apache.thrift.TException;

import java.util.*;

/**
 * Create by Popeye on 11:34 AM, 10/30/2019
 */
public class UserBagModel extends DataModel {

    public long uId;
    public List<EquipDataVO> listEquip = new ArrayList<>();
    public List<FragmentVO> listFragment = new ArrayList<>();
    public List<SageEquipDataVO> listSageEquip = new ArrayList<>();
    public List<CelestialEquipDataVO> listCelestial = new ArrayList<>();
    public List<StoneDataVO> listStone = new ArrayList<>();
    public Map<String, MoneyPackageVO> mapMoney = new HashMap<>();
    public Map<String, SpecialItemPackageVO> mapSpecialItem = new HashMap<>();
    public List<ResourcePackage> listHammer = new ArrayList<>();
    public EnergyChargeInfo energy = EnergyChargeInfo.create();
    public EnergyChargeInfo energyHunt = EnergyChargeInfo.create();

    public int timeStamp;

    private static int sizeEquip = ItemManager.getInstance().getSizeEquip();
    private static int sizeStone = ItemManager.getInstance().getSizeStone();

    private Object lockMoney = new Object();
    private Object lockEquip = new Object();
    private Object lockSpecialItem = new Object();
    private Object lockEnergy = new Object();
    private Object lockEnergyHunt = new Object();



    public static UserBagModel create(long uId, Zone zone) {
        UserBagModel userBagModel = new UserBagModel();
        userBagModel.uId = uId;
        userBagModel.initMoney(zone);
        userBagModel.initItemHero(zone);
        userBagModel.initStoneItemHero(zone);
        userBagModel.initItem(zone);
        userBagModel.saveToDB(zone);

        return userBagModel;
    }


    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uId), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserBagModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserBagModel copyFromDBtoObject(String uId, Zone zone) {
        UserBagModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserBagModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserBagModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if (pInfo == null) {
            pInfo = UserBagModel.create(Long.parseLong(uId), zone);
        }
        return pInfo;
    }

    public static boolean checkAccount(long uid, Zone zone) {
        UserBagModel bag = null;
        try {
            String str = (String) getModel(String.valueOf(uid), UserBagModel.class, zone);
            if (str != null) {
                bag = Utils.fromJson(str, UserBagModel.class);
                if (bag != null) {
                    return true;
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void initMoney(Zone zone) {
        for (MoneyType money : MoneyType.values()) {
            if (((ZoneExtension) zone.getExtension()).isTestServer()) {
                this.mapMoney.put(money.getId(), new MoneyPackageVO(money.getId(), 1000000000));
                this.mapMoney.put(MoneyType.SAGE_EXP.getId(), new MoneyPackageVO(MoneyType.SAGE_EXP.getId(), 100000000));
                this.mapMoney.put(MoneyType.STAR_CAMPAIGN.getId(), new MoneyPackageVO(MoneyType.STAR_CAMPAIGN.getId(), 0));
                this.mapMoney.put(MoneyType.GOLD.getId(), new MoneyPackageVO(MoneyType.GOLD.getId(), 1000000000));
                this.mapMoney.put(MoneyType.MERITS.getId(), new MoneyPackageVO(MoneyType.MERITS.getId(), 1000000000));
                this.mapMoney.put(MoneyType.ESSENCE.getId(), new MoneyPackageVO(MoneyType.ESSENCE.getId(), 1000000000));
                this.mapMoney.put(MoneyType.HONOR.getId(), new MoneyPackageVO(MoneyType.HONOR.getId(), 0));
                this.mapMoney.put(MoneyType.HERO_BANNER.getId(), new MoneyPackageVO(MoneyType.HERO_BANNER.getId(), 1000000000));
            } else {
                this.mapMoney.put(money.getId(), new MoneyPackageVO(money.getId(), 0));
                this.mapMoney.put(MoneyType.DIAMOND.getId(), new MoneyPackageVO(MoneyType.DIAMOND.getId(), 0));
                this.mapMoney.put(MoneyType.HERO_BANNER.getId(), new MoneyPackageVO(MoneyType.HERO_BANNER.getId(), 10));
                this.mapMoney.put(MoneyType.GOLD.getId(), new MoneyPackageVO(MoneyType.GOLD.getId(), 0));
                this.mapMoney.put(MoneyType.MERITS.getId(), new MoneyPackageVO(MoneyType.MERITS.getId(), 0));
                this.mapMoney.put(MoneyType.ESSENCE.getId(), new MoneyPackageVO(MoneyType.ESSENCE.getId(), 0));
            }
        }
    }

    public void initItemHero(Zone zone){
        List<EquipDataVO> listEquipData = new ArrayList<>();

        if (((ZoneExtension) zone.getExtension()).isTestServer()) {
            List<EquipConfigVO> listEquipCf = ItemManager.getInstance().getEquipConfig();
            EquipDataVO dataData;
            for (EquipConfigVO questCf : listEquipCf) {
                dataData = EquipDataVO.create(ItemManager.getInstance().convertEquipConfigToData(questCf));
                dataData.count = 2;
                listEquipData.add(dataData);
            }

        }else {
            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1019", 0, 1)));
            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1020", 0, 1)));
            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1021", 0, 1)));
            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1022", 0, 1)));
            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1023", 0, 1)));

            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1025", 0, 1)));
            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1026", 0, 1)));
            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1027", 0, 1)));
            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1028", 0, 1)));
            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1029", 0, 1)));
            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1030", 0, 1)));

            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1032", 0, 5)));

            listEquipData.add(new EquipDataVO(ItemManager.getInstance().getEquipVO("EQU1051", 0, 1)));
        }

        BagManager.getInstance().sortEquip(listEquipData);
        this.listEquip = listEquipData;
    }
    public void initStoneItemHero(Zone zone){
        List<StoneDataVO> listStoneData = new ArrayList<>();

        if (((ZoneExtension) zone.getExtension()).isTestServer()) {
            List<StoneConfigVO> listStoneCf = ItemManager.getInstance().getStoneConfig();
            StoneDataVO stoneData;
            for (StoneConfigVO stoneCf : listStoneCf) {
                for (int i = 1; i <= 5; i++) {
                    stoneData = ItemManager.getInstance().convertStoneConfigToData(stoneCf, i);
                    stoneData.level = i;
                    stoneData.hash = Utils.genStoneHash();
                    stoneData.count = 10;
                    listStoneData.add(stoneData);
                }
            }

        }else {
            listStoneData.add(ItemManager.getInstance().convertStoneConfigToData(ItemManager.getInstance().getStoneById("GEM101"), 1));
        }

        BagManager.getInstance().sortStone(listStoneData);
        this.listStone = listStoneData;
    }

    public void initItem(Zone zone) {
        //SERVER TEST
        if (((ZoneExtension) zone.getExtension()).isTestServer()) {
            //------------ Add Fragment -----------------
            List<FragmentConfigVO> list = ItemManager.getInstance().getFragmentConfig();
            List<FragmentVO> listFragment = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FragmentVO vo = new FragmentVO(list.get(i));
                listFragment.add(vo);
            }
            this.listFragment = listFragment;


            for (FragmentVO vo : listFragment) {
                vo.amount = 600;
            }
            this.listFragment = listFragment;
        } else {
            //----SPECIAL ITEM----
            UserModel um = ((ZoneExtension) zone.getExtension()).getUserManager().getUserModel(uId);
            try {
                if (SDKGateVip.canTakeFeeVip(um.accountID)) {
//                    this.mapSpecialItem.put("SPI1124", new SpecialItemPackageVO("SPI1124", 1));
//                    this.mapSpecialItem.put("SPI1126", new SpecialItemPackageVO("SPI1126", 1));
                }
            } catch (TException e) {
                e.printStackTrace();
            }
        }
    }

    public long readMoney(MoneyType type, Zone zone) {
        if (mapMoney.containsKey(type.getId())) {
            return mapMoney.get(type.getId()).amount;
        }

        return 0;
    }

    public ChangeMoneyResult changeMoney(List<MoneyPackageVO> moneyPackageVOList, TransactionDetail detail, Zone zone) {
        ChangeMoneyResult result = new ChangeMoneyResult();
        synchronized (lockMoney) {
            for (MoneyPackageVO moneyPackage : moneyPackageVOList) {
                //ko ton tai
                if (!mapMoney.containsKey(moneyPackage.id)) {
                    mapMoney.put(moneyPackage.id, new MoneyPackageVO(moneyPackage.id, 0));
//                    return new ChangeMoneyResult(false, new ArrayList<>());
                }

                //ko du tien
                if (mapMoney.get(moneyPackage.id).amount + moneyPackage.amount < 0) {
                    result.setSuccess(false);
                    result.getNotEnoughList().add(MoneyType.fromID(moneyPackage.id));
                }
            }

            //check thành công
            if (result.isSuccess()) {
                //thay doi gia tri
                for (MoneyPackageVO moneyPackage : moneyPackageVOList) {
                    mapMoney.get(moneyPackage.id).amount += moneyPackage.amount;
                    if (moneyPackage.id.equals(MoneyType.HONOR.getId())) {
                        VipManager.getInstance().checkLevelUpHonor(this.uId, zone, mapMoney.get(moneyPackage.id).amount);
                    }
                }

                //create thanh cong
                if (saveToDB(zone)) {
                    return result;
                }

                //create ko thanh cong
                return new ChangeMoneyResult(false, new ArrayList<>());
            }

            //that bai
            return result;
        }
    }

    public ChangeSpecialItemResult changeSpecialItem(List<SpecialItemPackageVO> specialItemPackageVOList, TransactionDetail detail, Zone zone) {
        ChangeSpecialItemResult result = new ChangeSpecialItemResult();
        synchronized (lockSpecialItem) {
            for (SpecialItemPackageVO vo : specialItemPackageVOList) {
                //ko du tien
                if (readSpecialItem(Objects.requireNonNull(SpecialItem.fromID(vo.id)), zone) + vo.amount < 0) {
                    result.setSuccess(false);
                    result.getNotEnoughList().add(SpecialItem.fromID(vo.id));
                }
            }

            //check thành công
            if (result.isSuccess()) {
                //thay doi gia tri
                for (SpecialItemPackageVO vo : specialItemPackageVOList) {
                    mapSpecialItem.get(vo.id).amount += vo.amount;
                }

                //create thanh cong
                if (saveToDB(zone)) {
                    return result;
                }

                //create ko thanh cong
                return new ChangeSpecialItemResult(false, new ArrayList<>());
            }

            //that bai
            return result;
        }
    }
    public Map<String, SpecialItemPackageVO> readSpecialItem(){
        return (mapSpecialItem != null) ? mapSpecialItem : new HashMap<>();
    }

    public long readSpecialItem(SpecialItem specialItem, Zone zone) {
        if (!mapSpecialItem.containsKey(specialItem.getId())) {
            mapSpecialItem.put(specialItem.getId(), new SpecialItemPackageVO(specialItem.getId(), 0));
            saveToDB(zone);
        }
        return mapSpecialItem.get(specialItem.getId()).amount;
    }

    public List<ResourcePackage> readHammer(){
        return (listHammer != null) ? listHammer : new ArrayList<>();
    }

    public List<EquipDataVO> readListEquipHero(Zone zone){
        synchronized (lockEquip){
            boolean haveSave = false;
            Set<String> setHashEquip = new HashSet<>();

            Iterator<EquipDataVO> iterator = listEquip.iterator();
            EquipDataVO equipData;
            while (iterator.hasNext()){
                equipData = iterator.next();

                if(setHashEquip.contains(equipData.hash)){
                    iterator.remove();
                    haveSave = true;
                }else {
                    setHashEquip.add(equipData.hash);
                }
            }

            if(haveSave) saveToDB(zone);

            return listEquip;
        }
    }

    private EnergyChargeInfo readEnergyChargeInfo(Zone zone) {
        synchronized (lockEnergy) {
            if (energy == null) {
                energy = EnergyChargeInfo.create();
                saveToDB(zone);
            }
            return energy;
        }
    }

    //hunt
    private EnergyChargeInfo readEnergyHuntChargeInfo(Zone zone) {
        synchronized (lockEnergyHunt) {
            if (energyHunt == null) {
                energyHunt = EnergyChargeInfo.create();
                saveToDB(zone);
            }
            return energyHunt;
        }
    }

    public EnergyChargeInfo readEnergy(Zone zone) {
        synchronized (lockEnergy) {
            EnergyChargeInfo data = readEnergyChargeInfo(zone);
            data.readEnergy();
            if (isNewWeek()) {
                energy.refreshNewWeek();
                timeStamp = Utils.getTimestampInSecond();
            }
            saveToDB(zone);
            return data;
        }
    }

    //hunt
    public EnergyChargeInfo readEnergyHunt(Zone zone) {
        synchronized (lockEnergyHunt) {
            EnergyChargeInfo data = readEnergyHuntChargeInfo(zone);
            data.readEnergyHunt();
            if (isNewDay()) {
                energyHunt.refreshNewWeekHunt();
                timeStamp = Utils.getTimestampInSecond();
            }
            saveToDB(zone);
            return data;
        }
    }

    public boolean changeEnergy(int point, Zone zone) {
        synchronized (lockEnergy) {
            EnergyChargeInfo data = readEnergyChargeInfo(zone);
            data.readEnergy();
            if (data.point + point < 0) return false;
            data.changeEnergy(point);
            saveToDB(zone);
            return true;
        }
    }

    //hunt
    public boolean changeEnergyHunt(int point, Zone zone) {
        synchronized (lockEnergyHunt) {
            EnergyChargeInfo data = readEnergyHuntChargeInfo(zone);
            data.readEnergyHunt();
            if (data.point + point < 0) return false;
            data.changeEnergy(point);
            saveToDB(zone);
            return true;
        }
    }

    public boolean changeEnergy(String id, Zone zone) {
        EnergyChangeVO cf = BagManager.getInstance().getEnergyConfig(id);
        if (cf == null) return false;

        synchronized (lockEnergy) {
            EnergyChargeInfo data = readEnergyChargeInfo(zone);
            data.readEnergy();
            if (data.point + cf.increase.amount < 0) return false;
            data.changeEnergy(cf.increase.amount);
            data.useCharge(cf.id);
            saveToDB(zone);
            return true;
        }
    }

    //hunt
    public boolean changeEnergyHunt(String id, Zone zone) {
        EnergyChangeVO cf = BagManager.getInstance().getEnergyHuntConfig(id);
        if (cf == null) return false;

        synchronized (lockEnergyHunt) {
            EnergyChargeInfo data = readEnergyHuntChargeInfo(zone);
            data.readEnergyHunt();
            if (data.point + cf.increase.amount < 0) return false;
            data.changeEnergy(cf.increase.amount);
            data.useCharge(cf.id);
            saveToDB(zone);
            return true;
        }
    }

    public int readCountChargeUse(String id, Zone zone) {
        if (isNewDay()) {
            energy.refreshNewDay();
            timeStamp = Utils.getTimestampInSecond();
            saveToDB(zone);
        }
        return energy.readCountUseCharge(id);
    }

    //hunt
    public int readCountChargeUseHunt(String id, Zone zone) {
        if (isNewDay()) {
            energyHunt.refreshNewDay();
            timeStamp = Utils.getTimestampInSecond();
            saveToDB(zone);
        }
        return energyHunt.readCountUseCharge(id);
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    public boolean isNewDay() {
        return Utils.isNewDay(timeStamp);
    }
    public boolean isNewWeek() {
        return Utils.isNewWeek(timeStamp);
    }
}
