package com.bamisu.log.gameserver.module.bag;

import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.define.ResourceType;
import com.bamisu.gamelib.item.define.SlotType;
import com.bamisu.gamelib.item.entities.*;
import com.bamisu.gamelib.sql.game.dbo.MoneyChangeDBO;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPChallenge;
import com.bamisu.log.gameserver.datamodel.IAP.home.UserIAPHomeModel;
import com.bamisu.log.gameserver.datamodel.IAP.store.UserIAPStoreModel;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.bag.entities.*;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.quest.UserQuestModel;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.bag.config.EnergyConfig;
import com.bamisu.log.gameserver.module.bag.config.entities.EnergyChangeVO;
import com.bamisu.log.gameserver.module.bag.entities.ItemGet;
import com.bamisu.log.gameserver.module.characters.clas.EClass;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.log.gameserver.module.nft.NFTManager;
import com.bamisu.log.gameserver.module.nft.defind.ETokenBC;
import com.bamisu.log.gameserver.module.quest.QuestManager;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BagManager {

    private EnergyConfig energyConfig;
    private EnergyConfig energyHuntConfig;

    private static BagManager ourInstance = null;

    public static BagManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new BagManager();
        }
        return ourInstance;
    }

    private void loadConfig() {
        energyConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Bag.FILE_PATH_CONFIG_ENERGY), EnergyConfig.class);
        energyHuntConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Bag.FILE_PATH_CONFIG_HUNTENERGY), EnergyConfig.class);
    }

    private BagManager() {
        //LoadConfig
        loadConfig();
    }

    /**
     *
     */
    public UserBagModel getUserBagModel(long uid, Zone zone) {
        return ((ZoneExtension) zone.getExtension()).getZoneCacheData().getUserBagModelCache(uid);
    }

    /**
     * thay đổi số lượng special item
     */
    public ChangeSpecialItemResult changeSpecialItem(UserBagModel userBagModel, List<SpecialItemPackageVO> specialItemPackageVOList, TransactionDetail detail, Zone zone) {
        return userBagModel.changeSpecialItem(specialItemPackageVOList, detail, zone);
    }

    /**
     * Thay đổi tiền của player
     */
    private ChangeMoneyResult changeMoney(UserBagModel userBagModel, List<MoneyPackageVO> moneyPackageVOList, TransactionDetail detail, Zone zone) {
        //Kiem tra level truoc
        boolean checkLevel = false;
        int level = 0;
        if (moneyPackageVOList.stream().anyMatch(obj -> obj.id.equals(MoneyType.SAGE_EXP.getId()))) {
            checkLevel = true;
            level = getLevelUser(userBagModel.uId, zone);
        }

        //Luu log sql
        long diamonBefore = userBagModel.readMoney(MoneyType.DIAMOND, zone);
        List<MoneyPackageVO> diamon = moneyPackageVOList.parallelStream().
                filter(obj -> obj.id.equals(MoneyType.DIAMOND.getId()) && obj.amount > 0).
                limit(1).
                collect(Collectors.toList());

        //Change
        ChangeMoneyResult result = userBagModel.changeMoney(moneyPackageVOList, detail, zone);

        //thanh cong -> thong bao
        if (result.isSuccess()) {
            SFSArray arrayCurrent = new SFSArray();
            for (MoneyPackageVO vo : moneyPackageVOList) {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putLong(Params.UID, userBagModel.uId);
                sfsObject.putUtfString(Params.MONEY_TYPE, vo.id);
                sfsObject.putLong(Params.MONEY, userBagModel.readMoney(MoneyType.fromID(vo.id), zone));
                arrayCurrent.addSFSObject(sfsObject);
            }
            //send notify change money
            UserUtils.changeMoney(userBagModel.uId, arrayCurrent, detail, zone);

            if (!diamon.isEmpty()) {
                pushSaveMoneyChange(MoneyChangeDBO.create(userBagModel.uId, detail.id, diamonBefore, userBagModel.readMoney(MoneyType.DIAMOND, zone)), zone);
            }

            //Event
            if (checkLevel) {
                int levelNew = getLevelUser(userBagModel.uId, zone);
                if (levelNew > level) {
                    Map<String, Object> data = new HashMap<>();
                    data.put(Params.BEFORE, (short) level);
                    data.put(Params.LEVEL, (short) levelNew);
                    GameEventAPI.ariseGameEvent(EGameEvent.LEVEL_USER_UPDATE, userBagModel.uId, data, zone);
                }
            }
        }

        return result;
    }

    /**
     * Delete item in bag
     *
     * @return
     */
    public boolean deleteItemInBag(long uid, EquipDataVO equipDataVO, Zone zone) {
        return deleteItemInBag(uid, Collections.singletonList(equipDataVO), zone);
    }

    public boolean deleteItemInBag(long uid, List<EquipDataVO> listEquipData, Zone zone) {
        UserBagModel userBagModel = getUserBagModel(uid, zone);
        return deleteItemInBag(userBagModel, listEquipData, zone);
    }

    public boolean deleteItemInBag(UserBagModel userBagModel, List<EquipDataVO> listEquipData, Zone zone) {
        if (listEquipData == null) return false;

        synchronized (userBagModel){
            boolean result = true;
            List<EquipDataVO> listEquipDataBag = userBagModel.readListEquipHero(zone);
            List<EquipDataVO> listEquipDulicate = userBagModel.readListEquipHero(zone).stream().
                    map(EquipDataVO::create).
                    collect(Collectors.toList());

            List<EquipDataVO> listEquipRemove = new ArrayList<>();
            List<EquipDataVO> listEquipNotFound = new ArrayList<>();
            EquipDataVO equipDataBag;
            EquipDataVO equipDataDulicate;
            out_loop:
            for (EquipDataVO equipDelete : listEquipData) {
                for (int i = 0; i < listEquipDataBag.size(); i++) {
                    equipDataBag = listEquipDataBag.get(i);

                    if (equipDelete.hash.equals(equipDataBag.hash)) {
                        //can phai dulicate neu item trong bag
                        //TH xoa item trong bag tham chieu se lam thay doi hash trong bag
                        listEquipDataBag.set(i, EquipDataVO.create(equipDataBag));
                        equipDataDulicate = listEquipDataBag.get(i);
                        equipDataDulicate.count -= equipDelete.count;

                        //TH = 0 hoac am trong bag -> Xoa
                        if (equipDataDulicate.count == 0) {
                            listEquipRemove.add(equipDataDulicate);
                        } else if (equipDataDulicate.count < 0) {
                            result = false;
                            break out_loop;
                        }

                        //Gan moi hash do bi xoa (Item khi lay ra khoi tui hash se thay doi)
                        equipDelete.hash = Utils.genItemHash();
                        continue out_loop;
                    }
                }

                listEquipNotFound.add(equipDelete);
                result = false;
                break;
            }
            if (!listEquipNotFound.isEmpty()) result = false;

            if (result) {
                //Remove list
                listEquipDataBag.removeAll(listEquipRemove);
                //Save
                return userBagModel.saveToDB(zone);
            } else {
                userBagModel.listEquip = listEquipDulicate;
                return false;
            }
        }
    }

    public boolean deleteStoneInBag(long uid, List<StoneDataVO> listStoneData, Zone zone) {
        UserBagModel userBagModel = getUserBagModel(uid, zone);
        return deleteStoneInBag(userBagModel, listStoneData, zone);
    }

    public boolean deleteStoneInBag(UserBagModel userBagModel, StoneDataVO stoneData, Zone zone) {
        return deleteStoneInBag(userBagModel, Collections.singletonList(stoneData), zone);
    }

    public boolean deleteStoneInBag(UserBagModel userBagModel, List<StoneDataVO> listStoneData, Zone zone) {
        if (listStoneData == null) return false;

        boolean result = true;
        List<StoneDataVO> listStoneDulicate = userBagModel.listStone.stream().
                map(StoneDataVO::create).
                collect(Collectors.toList());
        List<StoneDataVO> listStoneRemove = new ArrayList<>();
        List<StoneDataVO> listStoneNotFound = new ArrayList<>();
        StoneDataVO stoneDataBag;
        StoneDataVO stoneDataDulicate;
        out_loop:
        for (StoneDataVO stoneDelete : listStoneData) {
            for (int i = 0; i < userBagModel.listStone.size(); i++) {
                stoneDataBag = userBagModel.listStone.get(i);

                if (stoneDelete.hash.equals(stoneDataBag.hash)) {
                    //can phai dulicate neu item trong bag
                    //TH xoa item trong bag tham chieu se lam thay doi hash trong bag
                    userBagModel.listStone.set(i, StoneDataVO.create(stoneDataBag));
                    stoneDataDulicate = userBagModel.listStone.get(i);
                    stoneDataDulicate.count -= stoneDelete.count;
                    //TH = 0 hoac am trong bag -> Xoa
                    if (stoneDataDulicate.count == 0) {
                        listStoneRemove.add(stoneDataDulicate);
                    } else if (stoneDataDulicate.count < 0) {
                        result = false;
                        break out_loop;
                    }

                    //Gan moi hash do bi xoa (Item khi lay ra khoi tui hash se thay doi)
                    stoneDelete.hash = Utils.genStoneHash();
                    continue out_loop;
                }
            }

            listStoneNotFound.add(stoneDelete);
            result = false;
            break;
        }
        if (!listStoneNotFound.isEmpty()) result = false;

        if (result) {
            //Remove list
            userBagModel.listStone.removeAll(listStoneRemove);
            //Save
            return userBagModel.saveToDB(zone);
        } else {
            userBagModel.listStone = listStoneDulicate;
            return false;
        }
    }


    public void sortStone(List<StoneDataVO> listStone) {
        Collections.sort(listStone, this::compareStone);
    }

    public int compareStone(StoneDataVO stoneVO1, StoneDataVO stoneVO2) {
        return stoneVO2.level - stoneVO1.level;
    }


    public StoneDataVO getStoneInBag(long uid, String hashStone, Zone zone) {
        UserBagModel userBagModel = getUserBagModel(uid, zone);
        return getStoneInBag(userBagModel, hashStone);
    }

    public StoneDataVO getStoneInBag(UserBagModel userBagModel, String hashStone) {
        for (StoneDataVO stoneData : userBagModel.listStone) {
            if (stoneData.hash.equals(hashStone)) return stoneData;
        }
        return null;
    }

    public StoneDataVO getStoneInEquip(EquipDataVO equipData, int position) {
        return (!equipData.listSlotStone.get(position).haveLock()) ? null : new StoneDataVO(equipData.listSlotStone.get(position).stoneVO);
    }

    public StoneDataVO getStoneInEquip(EquipDataVO equipData, String hashStone) {
        for (StoneSlotVO slot : equipData.listSlotStone) {
            if (!slot.haveLock() || slot.stoneVO == null) continue;
            if (slot.stoneVO.hash.equals(hashStone)) return new StoneDataVO(slot.stoneVO);
        }
        return null;
    }

    /**
     * If stone exist = false
     * Else stone doesn't exist true
     */
    public boolean checkStoneInEquip(EquipDataVO equipDataVO) {
        for (int i = 0; i < equipDataVO.listSlotStone.size(); i++) {
            if (equipDataVO.listSlotStone.get(i).haveLock()) return false;
        }
        return true;
    }

    public List<ResourcePackage> upgradeWeapon(UserBagModel userBagModel, EquipVO equipVO, List<ItemGet> listWeapon, List<ResourcePackage> listHammer, Zone zone) {
        List<ResourcePackage> listHammerNew = new ArrayList<>();

        //Chuyen equip + hammer -> exp item
        int exp = chargeItemToExpHeroItem(userBagModel, listWeapon, listHammer, zone);
        if (exp == 0) return null;

        //Tang exp item
        equipVO.exp = equipVO.exp + exp;
        equipVO.expFis = equipVO.expFis + exp;

        //If level formula, get new Attribute
        EquipLevelVO newLevel = ItemManager.getInstance().getEquipLevelHigher(equipVO.id, equipVO.exp, equipVO.level);
        if (newLevel == null) return null;

        //?? -> khong hieu de day
        EquipLevelConfigVO levelEquipCf;
        EquipLevelVO level;
        int surplus = 0;
        int expNeed = 0;
        //Level MAX
        if (newLevel.level == equipVO.maxLevel) {

            for (int i = equipVO.level; i < newLevel.level; i++) {
                levelEquipCf = ItemManager.getInstance().getEquipLevelConfigVO(equipVO.id);
                level = ItemManager.getInstance().getAttrFollowLevelEquip(levelEquipCf, i);
                expNeed = expNeed + level.expNeed;
            }

            surplus = equipVO.exp - expNeed;
            listHammerNew = calculateHammer(userBagModel, surplus, zone);

            //if equipment on hero
            equipVO.listAttr = newLevel.listAttr;
            equipVO.level = newLevel.level;
            equipVO.expNeed = newLevel.expNeed;
            equipVO.exp = newLevel.expNeed;

        } else {
            //Level NOT MAX
            for (int i = equipVO.level; i < newLevel.level; i++) {
                levelEquipCf = ItemManager.getInstance().getEquipLevelConfigVO(equipVO.id);
                level = ItemManager.getInstance().getAttrFollowLevelEquip(levelEquipCf, i);
                expNeed = expNeed + level.expNeed;
            }

            surplus = equipVO.exp - expNeed;

            //If equipment on hero
            equipVO.listAttr = newLevel.listAttr;
            equipVO.level = newLevel.level;
            equipVO.expNeed = newLevel.expNeed;
            equipVO.exp = surplus;
        }

        if (HeroManager.getInstance().updateEquipmentHeroModel(userBagModel.uId, new EquipDataVO(equipVO), zone)) {
            //Event
            GameEventAPI.ariseGameEvent(EGameEvent.ENHANDCE_ITEM, userBagModel.uId, new HashMap<>(), zone);

            return listHammerNew;
        }

        return null;
    }

    /**
     * Charge item (equip + hammer) to exp + delete
     *
     * @param userBagModel
     * @param listEquip
     * @param listHammer
     * @param zone
     * @return
     */
    private int chargeItemToExpHeroItem(UserBagModel userBagModel, List<ItemGet> listEquip, List<ResourcePackage> listHammer, Zone zone) {
        List<EquipDataVO> listEquipRemove = new ArrayList<>();
        List<ResourcePackage> listHammerRemove = new ArrayList<>();

        int exp = 0;
        for (ItemGet equipFission : listEquip) {
            for (EquipDataVO equipDataBag : userBagModel.readListEquipHero(zone)) {
                //Xoa theo hash
                if (equipFission.hash.equals(equipDataBag.hash)) {
                    //Ktra client gui len dung data khong
                    if (equipFission.count <= equipDataBag.count) {
                        //Tang exp
                        exp += equipDataBag.expFis * equipFission.count;
                        //Add list -> romove
                        listEquipRemove.add(EquipDataVO.create(equipDataBag, equipFission.count));
                        break;

                    }

                    return 0;
                }
            }
        }

        //Check hammer exp
        ResourcePackage hammerCf;
        for (ResourcePackage hammerFission : listHammer) {
            //Check in bag
            for (ResourcePackage hammerBag : userBagModel.readHammer()) {
                if (hammerFission.id.equals(hammerBag.id)) {
                    //Ktra config
                    hammerCf = ItemManager.getInstance().getHammerConfig(hammerFission.id);
                    if (hammerCf == null) return 0;
                    //Kiem tra co the tieu dc khong
                    if (hammerBag.amount < hammerFission.amount) return 0;
                    //Tang exp
                    exp += hammerFission.amount * hammerCf.amount;
                    //Add list -> romove
                    listHammerRemove.add(new ResourcePackage(hammerFission.id, -hammerFission.amount));
                    break;
                }
            }
        }

        //Xoa trong bag (item + hammer)
        if (!deleteItemInBag(userBagModel, listEquipRemove, zone)) return 0;
        if (!addItemToDB(listHammerRemove, userBagModel.uId, zone, UserUtils.TransactionType.UP_LEVEL_EQUIP_HERO))
            return 0;
        return exp;
    }

    private boolean addHammerToBag(UserBagModel userBagModel, List<ResourcePackage> listHammer, Zone zone) {
        out_loop:
        for (ResourcePackage packAdd : listHammer) {
            for (ResourcePackage packBag : userBagModel.listHammer) {
                if (packAdd.id.equals(packBag.id)) {
                    packBag.amount += packAdd.amount;
                    continue out_loop;
                }
            }
            userBagModel.listHammer.add(packAdd);
        }
        return userBagModel.saveToDB(zone);


    }

    public boolean removeStoneFromEquip(long uid, EquipDataVO equipDataVO, int position, Zone zone) {
        equipDataVO.listSlotStone.get(position).unlock();
        equipDataVO.listSlotStone.get(position).stoneVO = null;
        return HeroManager.getInstance().updateEquipmentHeroModel(uid, equipDataVO, zone);
    }

    public boolean updateStoneToEquip(long uid, EquipDataVO equipData, StoneDataVO stoneData, int position, Zone zone) {
        if (equipData == null) return false;

        if (stoneData == null) {
            equipData.listSlotStone.get(position).stoneVO = null;
            equipData.listSlotStone.get(position).unlock();
        } else {
            StoneVO stoneCf = changeToStoneVO(stoneData);
            equipData.listSlotStone.get(position).stoneVO = stoneCf;
            equipData.listSlotStone.get(position).lock();
        }

        //TH trong bag
        if (equipData.hashHero == null || equipData.hashHero.isEmpty()) {

            return updateItemHeroInBag(uid, equipData, zone);
        } else {

            //TH tren hero
            return HeroManager.getInstance().updateEquipmentHeroModel(uid, EquipDataVO.create(equipData), zone);
        }
    }

    public boolean isSizeFusionWeapon(List<EquipDataVO> list) {
        int count = 0;

        for (int i = 0; i < list.size(); i++) {
            count += list.get(i).count;
        }

        return count % ItemManager.getInstance().getSizeInFusionWeapon() == 0;
    }

    public boolean isSizeListFusionWeapon(List<List<EquipDataVO>> list) {
        for (List<EquipDataVO> fusionList : list) {
            if(!isSizeFusionWeapon(fusionList)) return false;
        }

        return true;
    }

    public boolean checkSizeFusionStone(List<StoneDataVO> list) {
        int sizeFusionCf = ItemManager.getInstance().getSizeInFusionStone();

        for (StoneDataVO index : list) {
            if (index.count % sizeFusionCf != 0) return false;
        }
        return true;
    }

    /**
     * To fusion weapon and get a new weapon higher star + add hammer to bag
     */
    public EquipDataVO getFusionEquipHero(UserBagModel userBagModel, List<EquipDataVO> listEquipData, Zone zone) {
        int sizeFusionCf = ItemManager.getInstance().getSizeInFusionWeapon();
        EquipConfigVO equipCf;
        int exp = 0;
        int expFis;
        int count = 0;
        for (EquipDataVO equipData : listEquipData) {
            //Get exp fission
            equipCf = ItemManager.getInstance().getEquipConfig(equipData.id);
            expFis = (equipData.expFis * equipData.count) - (equipCf.expFis * equipData.count);
            exp += expFis;
            count += equipData.count;
        }

        //Get random Equip depend on star
        int newStar = ItemManager.getInstance().getFusionByStar(listEquipData.get(0).star).up;
        EquipConfigVO equipNewCf = getRandomEquipDependOnStar(newStar);
        EquipDataVO equipNewData = ItemManager.getInstance().convertEquipConfigToData(equipNewCf);
        EquipVO equipVO = changeToEquipVO(equipNewData);

        if (exp >= equipVO.expNeed) {
            EquipLevelVO newLevel = ItemManager.getInstance().getEquipLevelHigher(equipVO.id, exp, equipVO.level);
            int newExp = getSurplus(equipVO, newLevel);

            //Add surplus exp fission when max level
            EquipLevelConfigVO equipLevelConfigVO = ItemManager.getInstance().getEquipLevelConfigVO(equipVO.id);
            if (equipLevelConfigVO.maxLevel == newLevel.level) {
                //Create hammer exp
                calculateHammer(userBagModel, newExp, zone);
            }

            equipNewData.exp = newExp;
            equipNewData.level = newLevel.level;
            equipNewData.expFis = equipVO.expFis + exp;
            equipNewData.count = count / sizeFusionCf;
        } else {
            equipNewData.exp = exp;
            equipNewData.count = count / sizeFusionCf;
        }


        return equipNewData;
    }

    public List<EquipDataVO> getListFusionEquipHero(UserBagModel userBagModel, List<List<EquipDataVO>> listEquipData, Zone zone) {
        List<EquipDataVO> listGet = new ArrayList<>();

        for (List<EquipDataVO> fusionList : listEquipData) {
            listGet.add(getFusionEquipHero(userBagModel, fusionList, zone));
        }

        return listGet;
    }


    private List<ResourcePackage> calculateHammer(UserBagModel bag, int expOdd, Zone zone) {
        List<ResourcePackage> listHammerOdd = new ArrayList<>();
        List<ResourcePackage> listHammerCf = ItemManager.getInstance().getHammerConfig();
        ResourcePackage hammerCf;
        int count;
        while (expOdd > 0) {
            for (int i = listHammerCf.size() - 1; i >= 0; i--) {
                hammerCf = listHammerCf.get(i);

                if (expOdd > hammerCf.amount) {
                    count = expOdd / hammerCf.amount;
                    expOdd -= hammerCf.amount * count;
                    listHammerOdd.add(new ResourcePackage(hammerCf.id, count));
                    break;
                }
            }
        }
        if (!listHammerOdd.isEmpty()) addHammerToBag(bag, listHammerOdd, zone);
        return listHammerOdd;
    }

    private int getSurplus(EquipVO equipVO, EquipLevelVO newLevel) {
        int expNeed = 0;
        for (int i = equipVO.level; i < newLevel.level; i++) {
            EquipLevelConfigVO levelConfig = ItemManager.getInstance().getEquipLevelConfigVO(equipVO.id);
            EquipLevelVO level = ItemManager.getInstance().getAttrFollowLevelEquip(levelConfig, i);
            expNeed = expNeed + level.expNeed;
        }
        int surplus = equipVO.exp - expNeed;
        return surplus;
    }

    public EquipConfigVO getRandomEquipDependOnStar(int star) {
        EquipConfigVO equip = new EquipConfigVO();
        List<EquipConfigVO> listEquip = new ArrayList<>();
        List<EquipConfigVO> listEquipConfig = ItemManager.getInstance().getEquipConfig();
        //Get list weapon Array have same star follow requirement
        for (int i = 0; i < listEquipConfig.size(); i++) {
            if (listEquipConfig.get(i).star == star) {
                listEquip.add(listEquipConfig.get(i));
            }
        }

        int random = Utils.randomInRange(0, listEquip.size() - 1);
        equip = listEquip.get(random);
        return equip;
    }

    public boolean isSameStarWhenFusionWeapon(List<EquipDataVO> listEquipData) {

        if (listEquipData.size() == 1) return true;

        List<Integer> listStar = new ArrayList<>();
        for (EquipDataVO equipData : listEquipData) {
            listStar.add(equipData.star);
        }

        FusionVO fusionCf = ItemManager.getInstance().getFusionByStar(listStar.get(0));
        if (fusionCf == null) return false;

        for (int i = 1; i < listStar.size(); i++) {
            if (fusionCf.listStar.size() == 1) {
                if (listStar.get(i) != fusionCf.listStar.get(0).star) {
                    return false;
                }
            } else if (fusionCf.listStar.size() == 2) {
                if (listStar.get(i) != fusionCf.listStar.get(0).star && listStar.get(i) != fusionCf.listStar.get(1).star) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isSameStarWhenListFusionWeapon(List<List<EquipDataVO>> listEquipData) {
        for (List<EquipDataVO> fusionList : listEquipData) {
            if (!isSameStarWhenFusionWeapon(fusionList)) return false;
        }
        return true;
    }

    public StoneDataVO fusionStone(StoneDataVO stoneData) {
        StoneLevelVO stoneCf = ItemManager.getInstance().getStoneLevelHigher(stoneData);
        if (stoneCf == null) return null;

        int sizeFusionCf = ItemManager.getInstance().getSizeInFusionStone();
        StoneDataVO newStone = StoneDataVO.create(stoneData.id, stoneCf.level, stoneData.count / sizeFusionCf);

        return newStone;
    }

    public List<StoneDataVO> fusionStone(List<StoneDataVO> listStoneData) {
        List<StoneDataVO> listNewStone = new ArrayList<>();
        for (StoneDataVO stoneData : listStoneData) {
            listNewStone.add(fusionStone(stoneData));
        }

        return listNewStone.stream().
                filter(obj -> Objects.nonNull(obj) && obj.count > 0).
                collect(Collectors.toList());
    }

    public boolean isMaxStarFusionWeapon(int star) {
        return ItemManager.getInstance().getCostFusionHeroEquipConfig(star) != null;
    }

    public Map<String, SpecialItemPackageVO> getListSpecialItem(long uid, Zone zone) {
        UserBagModel bag = getUserBagModel(uid, zone);
        return bag.readSpecialItem();
    }

    public List<FragmentVO> getListFragmentHero(long uid, Zone zone) {
        UserBagModel bag = getUserBagModel(uid, zone);
        return bag.listFragment;
    }

    /**
     * Get all equipment in bag
     *
     * @param uid:  user star
     * @param zone: user's zone
     * @return List<EquipData>
     */
    public List<EquipDataVO> getListHeroEquipInBag(long uid, Zone zone) {
        UserBagModel userBagModel = getUserBagModel(uid, zone);
        return getListHeroEquipInBag(userBagModel, zone);
    }

    public List<EquipDataVO> getListHeroEquipInBag(UserBagModel userBagModel, Zone zone) {
        return userBagModel.readListEquipHero(zone);
    }

    public List<EquipDataVO> getListHeroEquipInBag(UserBagModel userBagModel, List<String> listHashEquip, Zone zone) {
        List<EquipDataVO> listEquip = getListHeroEquipInBag(userBagModel, zone);
        List<EquipDataVO> listGet = new ArrayList<>();

        for (EquipDataVO equip : listEquip) {
            if (listHashEquip.contains(equip.hash)) {
                listGet.add(equip);
            }
        }
        return listGet;
    }

    public EquipDataVO getHeroEquipInBag(long uid, String hashEquip, Zone zone) {
        UserBagModel userBagModel = getUserBagModel(uid, zone);
        return getHeroEquipInBag(userBagModel, hashEquip, zone);
    }

    public EquipDataVO getHeroEquipInBag(UserBagModel userBagModel, String hashEquip, Zone zone) {
        for (EquipDataVO equipData : userBagModel.readListEquipHero(zone)) {
            if (hashEquip.equals(equipData.hash)) return equipData;
        }
        return null;
    }

    public EquipDataVO getHeroEquipInBag(long uid, ItemGet itemGet, Zone zone) {
        UserBagModel userBagModel = getUserBagModel(uid, zone);
        return getHeroEquipInBag(userBagModel, itemGet, zone);
    }

    public EquipDataVO getHeroEquipInBag(UserBagModel userBagModel, ItemGet itemGet, Zone zone) {
        for (EquipDataVO equipData : userBagModel.readListEquipHero(zone)) {
            if (itemGet.hash.equals(equipData.hash) && equipData.count >= itemGet.count) {
                return EquipDataVO.create(equipData, itemGet.count);
            }
        }
        return null;
    }

    public List<EquipDataVO> getHeroEquipInBag(UserBagModel userBagModel, List<ItemGet> listItemGet, Zone zone) {
        List<EquipDataVO> listGet = new ArrayList<>();

        for (EquipDataVO equipData : userBagModel.readListEquipHero(zone)) {
            for (ItemGet itemGet : listItemGet) {
                if (itemGet.hash.equals(equipData.hash) && equipData.count >= itemGet.count) {
                    listGet.add(EquipDataVO.create(equipData, itemGet.count));
                }
            }
        }

        return listGet;
    }

    public EquipDataVO getHeroEquipHaveStone(List<EquipDataVO> listEquipData, String hashStone) {
        StoneDataVO stoneData;
        for (EquipDataVO equipData : listEquipData) {
            stoneData = getStoneInEquip(equipData, hashStone);
            if (stoneData != null) return equipData;
        }
        return null;
    }

    /**
     * Get all stone in bag
     */
    public List<StoneDataVO> getListStoneInBag(long uid, Zone zone) {
        UserBagModel userBagModel = getUserBagModel(uid, zone);
        return getListStoneInBag(userBagModel);
    }

    public List<StoneDataVO> getListStoneInBag(UserBagModel userBagModel) {
        return userBagModel.listStone;
    }

    public List<StoneDataVO> getListStoneInBag(UserBagModel userBagModel, List<String> listHashStone) {
        List<StoneDataVO> listGet = new ArrayList<>();

        for (StoneDataVO stoneBag : getListStoneInBag(userBagModel)) {
            if (listHashStone.contains(stoneBag.hash)) {
                listGet.add(stoneBag);
            }
        }

        return listGet;
    }

    public StoneDataVO getStoneInBag(UserBagModel userBagModel, ItemGet itemGet) {
        for (StoneDataVO stoneBag : getListStoneInBag(userBagModel)) {
            if (itemGet.hash.equals(stoneBag.hash) && stoneBag.count >= itemGet.count) {
                return StoneDataVO.create(stoneBag, itemGet.count);
            }
        }
        return null;
    }

    public List<StoneDataVO> getStoneInBag(UserBagModel userBagModel, List<ItemGet> listItemGet) {
        List<StoneDataVO> listGet = new ArrayList<>();

        for (StoneDataVO stoneBag : getListStoneInBag(userBagModel)) {
            for (ItemGet itemGet : listItemGet) {
                if (itemGet.hash.equals(stoneBag.hash) && stoneBag.count >= itemGet.count) {
                    listGet.add(StoneDataVO.create(stoneBag, itemGet.count));
                }
            }
        }

        return listGet;
    }

    /**
     * Change to EquipVO full attribute
     */
    public EquipVO changeToEquipVO(EquipDataVO equipDataVO) {
        if (equipDataVO != null) {
            EquipConfigVO equipConfigVO = ItemManager.getInstance().getEquipByID(equipDataVO.id);
            EquipLevelConfigVO equipLevelConfigVO = ItemManager.getInstance().getEquipLevelConfigVO(equipDataVO.id);
            if (equipLevelConfigVO == null) {
                System.out.println();
            }
            if (equipConfigVO == null) return null;
            EquipLevelVO equipLevelVO = ItemManager.getInstance().getAttrFollowLevelEquip(equipLevelConfigVO, equipDataVO.level);
            EquipVO equipVO = new EquipVO(equipDataVO, equipConfigVO, equipLevelConfigVO, equipLevelVO);
            return equipVO;
        }
        return null;
    }

    /**
     * Sort equipment in bag
     */
    public void sortEquip(List<EquipDataVO> listEquip) {
        Collections.sort(listEquip, this::compareEquip);
    }

    /**
     * Sort equipment in bag
     */
    private int compareEquip(EquipDataVO equipDataVO1, EquipDataVO equipDataVO2) {
        Stats e1 = HeroManager.getInstance().getStatsItem(equipDataVO1);
        int p1 = HeroManager.getInstance().getPower(e1);
        Stats e2 = HeroManager.getInstance().getStatsItem(equipDataVO2);
        int p2 = HeroManager.getInstance().getPower(e2);
        return p2 - p1;
    }

    /**
     * Add list Equipments to bag
     * Ko co TH do trong tui tu nhet vao tui (khong can tao new chong tham chieu)
     */
    public boolean addNewWeapon(long uid, Zone zone, EquipDataVO equipDataVO) {
        return addNewWeapon(uid, zone, Collections.singletonList(equipDataVO));
    }

    public boolean addNewWeapon(long uid, Zone zone, List<EquipDataVO> listEquipData) {
        UserBagModel userBagModel = getUserBagModel(uid, zone);
        return addNewWeapon(userBagModel, zone, listEquipData);
    }

    public boolean addNewWeapon(UserBagModel userBagModel, Zone zone, EquipDataVO equipDataVO) {
        return addNewWeapon(userBagModel, zone, Collections.singletonList(equipDataVO));
    }

    public boolean addNewWeapon(UserBagModel userBagModel, Zone zone, List<EquipDataVO> listEquipData) {
        List<EquipDataVO> listEquipDataBag = userBagModel.readListEquipHero(zone);

        outerloop:
        for (EquipDataVO equipAdd : listEquipData) {
            equipAdd.hashHero = null;

            for (EquipDataVO equipBag : listEquipDataBag) {
                if (equipBag.id.equals(equipAdd.id) &&
                        equipBag.level == equipAdd.level &&
                        equipBag.exp == equipAdd.exp &&
                        checkStoneInEquip(equipAdd) &&
                        checkStoneInEquip(equipBag)) {
                    //Gop item
                    equipBag.count += equipAdd.count;
                    //Gan hash item add = item in bag
                    equipAdd.hash = equipBag.hash;
                    continue outerloop;
                }
            }

            listEquipDataBag.add(EquipDataVO.create(equipAdd));
        }

        //Sort
        sortEquip(listEquipDataBag);
        //Save
        return userBagModel.saveToDB(zone);
    }

    /**
     * @param uid
     * @param equipData
     * @param zone
     * @return
     */
    public boolean updateItemHeroInBag(long uid, EquipDataVO equipData, Zone zone) {
        UserBagModel userBagModel = getUserBagModel(uid, zone);
        return updateItemHeroInBag(userBagModel, equipData, zone);
    }

    public boolean updateItemHeroInBag(UserBagModel userBagModel, EquipDataVO equipData, Zone zone) {
        List<EquipDataVO> listEquipDataBag = userBagModel.readListEquipHero(zone);
        EquipDataVO equipBag;
        for (int i = 0; i < listEquipDataBag.size(); i++) {
            equipBag = listEquipDataBag.get(i);

            if (equipBag.hash.equals(equipData.hash)) {
                //TH neu co ngoc
                if (equipData.haveStone()) {
                    if (equipBag.count > 1) {
                        listEquipDataBag.set(i, EquipDataVO.create(equipData, equipBag.count--));
                        equipData.hash = Utils.genItemHash();
                        return addNewWeapon(userBagModel, zone, equipData);
                    } else {
                        listEquipDataBag.set(i, EquipDataVO.create(equipData));
                        sortEquip(listEquipDataBag);
                        return userBagModel.saveToDB(zone);
                    }
                } else {
                    //TH xoa ngoc trong item
                    //Xoa item co ngoc trong bag + add item moi vao bag
                    listEquipDataBag.remove(i);
                    return addNewWeapon(userBagModel, zone, equipData);
                }
            }
        }

        return false;
    }

    /**
     * Get Item from bag depend on POSITION
     */
    public List<EquipDataVO> getItemHeroDependOnPosition(long uid, Zone zone, int position, String classType, String weaponType) {
        List<EquipDataVO> listEquipDataBag = getListHeroEquipInBag(uid, zone);
        List<EquipDataVO> listEquipGet = new ArrayList<>();
        EquipVO equipCf;

        //Its weapon
        if (position == SlotType.WEAPON.getValue()) {
            for (EquipDataVO equipData : listEquipDataBag) {
                if (equipData.position == position) {
                    equipCf = changeToEquipVO(equipData);
                    if (equipCf == null) continue;

                    if (weaponType.equals(equipCf.type)) listEquipGet.add(equipData);
                }
            }
            return listEquipGet;

        } else if (position == SlotType.ACCESSORY.getValue()) {
            for (EquipDataVO equipData : listEquipDataBag) {
                if (equipData.position == position) {
                    equipCf = changeToEquipVO(equipData);
                    if (equipCf == null) continue;

                    listEquipGet.add(equipData);
                }
            }
            return listEquipGet;

        } else {//another
            for (EquipDataVO equipData : listEquipDataBag) {
                if (equipData.position == position) {
                    equipCf = changeToEquipVO(equipData);
                    if (equipCf == null) continue;

                    if (classType.equals(equipCf.profession)) listEquipGet.add(equipData);
                }
            }
            return listEquipGet;
        }
    }

    /**
     * Change to StoneVO has full attribute
     */
    public StoneVO changeToStoneVO(StoneDataVO stoneDataVO) {
        StoneConfigVO stoneConfigVO = ItemManager.getInstance().getStoneById(stoneDataVO.id);
        StoneLevelConfigVO stoneLevelConfigVO = ItemManager.getInstance().getStoneLevel(stoneDataVO.id);
        StoneVO stoneVO = new StoneVO(stoneDataVO, stoneConfigVO, stoneLevelConfigVO);
        return stoneVO;
    }

    /**
     * Add stone
     *
     * @param uid
     * @param zone
     * @param listStone
     * @return
     */
    public boolean addNewStone(long uid, Zone zone, List<StoneDataVO> listStone) {
        UserBagModel userBagModel = getUserBagModel(uid, zone);
        return addNewStone(userBagModel, listStone, zone);
    }

    public boolean addNewStone(long uid, StoneDataVO stoneData, Zone zone) {
        UserBagModel userBagModel = getUserBagModel(uid, zone);
        return addNewStone(userBagModel, stoneData, zone);
    }

    public boolean addNewStone(UserBagModel userBagModel, StoneDataVO stoneData, Zone zone) {
        return addNewStone(userBagModel, Collections.singletonList(stoneData), zone);
    }

    public boolean addNewStone(UserBagModel userBagModel, List<StoneDataVO> listStone, Zone zone) {
        //Check if stone had in bag
        OUTERLOOP:
        for (StoneDataVO stoneAdd : listStone) {
            //TH ton tai stone same ID va same LEVEL
            for (StoneDataVO stoneDataBag : userBagModel.listStone) {
                if (stoneDataBag.id.equals(stoneAdd.id) &&
                        stoneDataBag.level == stoneAdd.level) {
                    //Gop stone
                    stoneDataBag.count += stoneAdd.count;
                    //Thay doi hash stone add = stone trong data
                    stoneAdd.hash = stoneDataBag.hash;
                    continue OUTERLOOP;
                }
            }
            //TH khong ton tai
            userBagModel.listStone.add(StoneDataVO.create(stoneAdd));
        }

        sortStone(userBagModel.listStone);
        return userBagModel.saveToDB(zone);
    }

    public boolean checkFragmentHero(long uid, Zone zone, String id, int count) {
        FragmentConfigVO fragmentConfigVO = ItemManager.getInstance().getFragmentConfig(id);
        UserBagModel bag = getUserBagModel(uid, zone);
        for (int i = 0; i < bag.listFragment.size(); i++) {
            if (bag.listFragment.get(i).id.equals(id)) {

                //Check amount of fragment
                int fragment = count * fragmentConfigVO.need;
                if (fragment < bag.listFragment.get(i).amount) {
                    bag.listFragment.get(i).amount -= fragment;
                    return bag.saveToDB(zone);
                } else if (fragment == bag.listFragment.get(i).amount) {
                    bag.listFragment.remove(i);
                    return bag.saveToDB(zone);
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public SageEquipDataVO getOneSageEquipInBag(long uid, Zone zone, String hash) {
        UserBagModel bag = getUserBagModel(uid, zone);
        if (bag.listSageEquip == null) {
            return null;
        }

        for (int i = 0; i < bag.listSageEquip.size(); i++) {

            if (hash.equals(bag.listSageEquip.get(i).hash)) {
                SageEquipDataVO sageEquipDataVO = bag.listSageEquip.get(i);
                sageEquipDataVO.count = 1;
                if (bag.listSageEquip.get(i).count == 1) {
                    bag.listSageEquip.remove(i);
                } else {
                    bag.listSageEquip.get(i).count--;
                    bag.listSageEquip.get(i).hash = Utils.genItemHash();
                }
                bag.saveToDB(zone);
                return sageEquipDataVO;
            }
        }
        return null;
    }

    public List<SageEquipDataVO> getSageEquipDependOnPosition(long uid, Zone zone, int position) {
        UserBagModel bag = getUserBagModel(uid, zone);
        List<SageEquipDataVO> list = new ArrayList<>();
        for (int i = 0; i < bag.listSageEquip.size(); i++) {
            if (bag.listSageEquip.get(i).position == position) {
                list.add(bag.listSageEquip.get(i));
            }
        }
        return list;
    }

    public boolean addNewSageEquip(long uid, Zone zone, SageEquipDataVO sageEquipDataVO) {
        UserBagModel bag = getUserBagModel(uid, zone);
        if (sageEquipDataVO.hash == null) {
            sageEquipDataVO.hash = Utils.genItemHash();
        }
        if (bag.listSageEquip == null) {
            sageEquipDataVO.count = 1;
            bag.listSageEquip.add(sageEquipDataVO);
            return bag.saveToDB(zone);
        }

        for (int i = 0; i < bag.listSageEquip.size(); i++) {
            if (bag.listSageEquip.get(i).id.equals(sageEquipDataVO.id)) {
                bag.listSageEquip.get(i).count++;
                return bag.saveToDB(zone);
            }
        }

        bag.listSageEquip.add(sageEquipDataVO);
        return bag.saveToDB(zone);
    }

    public boolean addItemToDB(Collection<? extends ResourcePackage> resources, long uid, Zone zone, TransactionDetail transactionDetail) {
        if (resources == null) return false;

        List<EquipDataVO> weapon = Collections.synchronizedList(new ArrayList<>());
        List<SpecialItemPackageVO> specialItem = Collections.synchronizedList(new ArrayList<>());
        List<MoneyPackageVO> money = Collections.synchronizedList(new ArrayList<>());
        List<ResourcePackage> fragment = Collections.synchronizedList(new ArrayList<>());
        List<ResourcePackage> res = Collections.synchronizedList(new ArrayList<>());
        List<TokenResourcePackage> tokenBC = Collections.synchronizedList(new ArrayList<>());
        List<ResourcePackage> other = Collections.synchronizedList(new ArrayList<>());
        List<ResourcePackage> hammer = Collections.synchronizedList(new ArrayList<>());

        resources.stream().forEach(obj -> {
            String id = "";
            Object value = null;
            int amount = 0;
            if (obj instanceof TokenResourcePackage) {
                TokenResourcePackage tokenResourcePackage = (TokenResourcePackage) obj;
                id = tokenResourcePackage.getId();
                value = tokenResourcePackage.getValue();
            } else {
                id = obj.id;
                amount = obj.amount;
            }

            switch (Objects.requireNonNull(ResourceType.fromID(id))) {
                case WEAPON:
                    weapon.add(ItemManager.getInstance().convertEquipConfigToData(ItemManager.getInstance().getEquipByID(id)));
                    break;
                case SPECIAL_ITEM:
                    if (id.equals("SPI1128") || id.equals("SPI1129") || id.equals("SPI1130") || id.equals("SPI1131") || id.equals("SPI1132") || id.equals("SPI1133") || id.equals("SPI1134") || id.equals("SPI1135") || id.equals("SPI1136") || id.equals("SPI1137")) {

                    } else {
                        specialItem.add(new SpecialItemPackageVO(id, amount));
                    }
                    break;
                case MONEY:
                    money.add(new MoneyPackageVO(id, amount));
                    break;
                case FRAGMENT_HERO:
                    fragment.add(new MoneyPackageVO(id, amount));
                    break;
                case RESOURCE:
                    if (id.equals("RES1008") || id.equals("RES1009") || id.equals("RES1010")) {
                        hammer.add(new ResourcePackage(id, amount));
                    } else {
                        res.add(new ResourcePackage(id, amount));
                    }
                    break;
                case TOKEN:
                    if (value == null) {
                        if (id.equalsIgnoreCase(ETokenBC.BUSD.getId())) {
                            value = (double) amount;
                        } else {
                            value = (long) amount;
                        }
                    }

                    tokenBC.add(new TokenResourcePackage(id, value));
                    
                    break;
                default:
                    other.add(obj);
            }
        });

        UserBagModel bag = getUserBagModel(uid, zone);
        if (!weapon.isEmpty() && !addNewWeapon(uid, zone, weapon)) return false;
        if (!specialItem.isEmpty() && !changeSpecialItem(bag, specialItem, transactionDetail, zone).isSuccess())
            return false;
        if (!money.isEmpty() && !changeMoney(bag, money, transactionDetail, zone).isSuccess()) return false;
        if (!fragment.isEmpty() && !addNewFragment(uid, zone, fragment)) return false;
        if (!res.isEmpty() && !changeResource(uid, res, transactionDetail, zone).isSuccess()) return false;
        if (!tokenBC.isEmpty() && !NFTManager.getInstance().updateToken(uid, tokenBC, transactionDetail, zone).isSuccess()) return false;

        boolean addHammerToBag = addHammerToBag(bag, hammer, zone);
        if (hammer.isEmpty() || addHammerToBag) {
            //log
            Logger.getLogger("translog").info("sv:" + ((ZoneExtension) zone.getExtension()).getServerID() + "|" + "uid:" + uid + "|" + "re:" + transactionDetail.desc + "|" + "rs:" + Utils.toJson(resources));
            return true;
        }
        return false;
    }

    /**
     * Thay đổi resource của player
     */
    private ChangeResourceResult changeResource(long uid, List<ResourcePackage> resources, TransactionDetail detail, Zone zone) {
        List<ResourcePackage> iapRes = Collections.synchronizedList(new ArrayList<>());
        List<ResourcePackage> questRes = Collections.synchronizedList(new ArrayList<>());
        List<ResourcePackage> energyRes = Collections.synchronizedList(new ArrayList<>());
        List<ResourcePackage> energyHuntRes = Collections.synchronizedList(new ArrayList<>());
        List<ResourcePackage> guildRes = Collections.synchronizedList(new ArrayList<>());

        resources.parallelStream().forEach(obj -> {
            switch (obj.id) {
                case "RES1017":
                case "RES1018":
                    iapRes.add(new MoneyPackageVO(obj.id, obj.amount));
                    break;
                case "RES1019":
                case "RES1020":
                    questRes.add(new MoneyPackageVO(obj.id, obj.amount));
                    break;
                case "RES1024":
                    energyRes.add(new MoneyPackageVO(obj.id, obj.amount));
                    break;
                case "RES1025":
                    energyHuntRes.add(new MoneyPackageVO(obj.id, obj.amount));
                    break;
                case "RES1026":
                    guildRes.add(new MoneyPackageVO(obj.id, obj.amount));
                    break;
            }
        });

        ChangeResourceResult result = new ChangeResourceResult();
        List<ResourcePackage> resSend = Collections.synchronizedList(new ArrayList<>());

        AtomicReference<InfoIAPChallenge> infoIAPChallenge = new AtomicReference<>();
        if (!iapRes.isEmpty()) {
            UserIAPStoreModel userIAPStoreModel = IAPBuyManager.getInstance().getUserIAPStoreModel(uid, zone);
            UserIAPHomeModel userIAPHomeModel = IAPBuyManager.getInstance().getUserIAPHomeModel(uid, zone);
            if (!iapRes.stream().
                    collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> {
                        oldValue.amount += newValue.amount;
                        return oldValue;
                    })).values().stream().
                    allMatch(obj -> {
                        switch (obj.id) {
                            case "RES1017":
                                if (!IAPBuyManager.getInstance().increaseIAPChallenge(userIAPStoreModel, "prestige1", null, obj.amount, zone))
                                    return false;

                                //Tang them IAP POINT thanh cong
                                infoIAPChallenge.set(IAPBuyManager.getInstance().getInfoIAPChallengeUserModel(userIAPStoreModel, userIAPHomeModel, "prestige1", zone));
                                resSend.add(new ResourcePackage(obj.id, infoIAPChallenge.get().point));
                                return true;
                            case "RES1018":
                                if (!IAPBuyManager.getInstance().increaseIAPChallenge(userIAPStoreModel, "prestige2", null, obj.amount, zone))
                                    return false;

                                //Tang them IAP POINT thanh cong
                                infoIAPChallenge.set(IAPBuyManager.getInstance().getInfoIAPChallengeUserModel(userIAPStoreModel, userIAPHomeModel, "prestige2", zone));
                                resSend.add(new ResourcePackage(obj.id, infoIAPChallenge.get().point));
                                return true;
                            default:
                                return false;
                        }
                    })) result.setSuccess(false);
        }
        if (!questRes.isEmpty()) {
            UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
            if (QuestManager.getInstance().changeQuestPoint(userQuestModel, questRes, zone)) result.setSuccess(false);

            //Tang them QUEST POINT thanh cong
        }
        if (!energyRes.stream().
                collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> {
                    oldValue.amount += newValue.amount;
                    return oldValue;
                })).values().isEmpty()
                ) {
            UserBagModel userBagModel = getUserBagModel(uid, zone);
            if (!changeEnergy(userBagModel, energyRes.get(0).amount, zone)) result.setSuccess(false);

            //Tang them ENERGY POINT thanh cong
            resSend.add(new ResourcePackage(energyRes.get(0).id, userBagModel.readEnergy(zone).point));
        }
        if (!energyHuntRes.stream().
                collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> {
                    oldValue.amount += newValue.amount;
                    return oldValue;
                })).values().isEmpty()
        ) {
            UserBagModel userBagModel = getUserBagModel(uid, zone);
            if (!changeEnergyHunt(userBagModel, energyHuntRes.get(0).amount, zone)) result.setSuccess(false);

            //Tang them ENERGY HUNT POINT thanh cong
            resSend.add(new ResourcePackage(energyHuntRes.get(0).id, userBagModel.readEnergyHunt(zone).point));
        }
        if (!guildRes.isEmpty()) {
            GuildModel guildModel = GuildManager.getInstance().getGuildModelByUserID(uid, zone);
            if (guildModel == null) {

            } else {
                if (!GuildManager.getInstance().changeResourceGuildModel(guildModel, guildRes, zone))
                    result.setSuccess(false);
            }
            //Tang them GUILD RES thanh cong
        }


        //thanh cong -> thong bao
        if (result.isSuccess() && !resSend.isEmpty()) {
            SFSArray arrayCurrent = new SFSArray();
            for (ResourcePackage vo : resSend) {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putLong(Params.UID, uid);
                sfsObject.putUtfString(Params.RESOURCE_TYPE, vo.id);
                sfsObject.putLong(Params.RESOURCE, vo.amount);
                arrayCurrent.addSFSObject(sfsObject);
            }
            //send notify change resource
            UserUtils.changeResource(uid, arrayCurrent, detail, zone);
        }

        return result;
    }

    private boolean addNewFragment(long uid, Zone zone, List<ResourcePackage> resource) {
        UserBagModel bag = getUserBagModel(uid, zone);
        OUTERLOOP:
        for (int count = 0; count < resource.size(); count++) {
            FragmentVO fragmentVO = new FragmentVO(resource.get(count).id, resource.get(count).amount);
            for (int x = 0; x < bag.listFragment.size(); x++) {
                if (bag.listFragment.get(x).id.equals(fragmentVO.id)) {
                    bag.listFragment.get(x).amount += fragmentVO.amount;
                    if (bag.listFragment.get(x).amount <= 0) {
                        bag.listFragment.remove(x);
                    }
                    continue OUTERLOOP;
                }
            }
            bag.listFragment.add(fragmentVO);
        }
        return bag.saveToDB(zone);
    }

    /**
     * Get Level
     *
     * @param uid
     * @param zone
     * @return
     */
    public int getLevelUser(long uid, Zone zone) {
        long exp = getExpUser(uid, zone);
        int level = convertExpToLevel(exp);
        return level;
    }

    private int convertExpToLevel(long exp) {
        int level = 0;
        List<LevelSageVO> listLevel = ItemManager.getInstance().getMethodLevel();
        for (LevelSageVO vo : listLevel) {
            if (exp >= vo.exp) {
                level = vo.level;
            } else {
                break;
            }
        }
        return level;
    }

    public long getExpUser(long uid, Zone zone) {
        UserBagModel bag = getUserBagModel(uid, zone);
        return bag.mapMoney.get(MoneyType.SAGE_EXP.getId()).amount;
    }

    public long getExpSurplusUser(long uid, Zone zone) {
        int level = getLevelUser(uid, zone);
        long expRoot = getExpInConfig(level);
        long expSurplus = getExpUser(uid, zone) - expRoot;
        return expSurplus;
    }

    public long getExpInConfig(int level) {
        return ItemManager.getInstance().getMethodLevel().get(level - 1).exp;
    }

    public List<ResourcePackage> getCostFusionEquipHero(int star) {
        PayEquipVO payEquipCf = ItemManager.getInstance().getCostFusionHeroEquipConfig(star);
        if (payEquipCf == null) return new ArrayList<>();
        return Collections.singletonList(new ResourcePackage(payEquipCf.money.id, -payEquipCf.money.amount));
    }

    public boolean checkEquip(EquipDataVO equipDataVO, EClass classType, EWeaponType heroEquipType) {
        EquipVO equipVO = changeToEquipVO(equipDataVO);
        if (equipVO == null) return false;
        if (equipVO.position == SlotType.ACCESSORY.getValue()) return true;
        return equipVO.profession.equals(classType.getId()) || equipVO.type.equals(heroEquipType.getID());
    }

    public List<ResourcePackage> getCostFusionStone(int level) {
        PayGemVO payGemCf = ItemManager.getInstance().getCostFusionStoneConfig(level);
        List<ResourcePackage> list = Stream.of(payGemCf.money).
                map(obj -> new ResourcePackage(obj.id, -obj.amount)).
                collect(Collectors.toList());

        return list;
    }



    /*-------------------------------------------- CACHE SAVE MONEY --------------------------------------------------*/

    /**
     * Get cache create money
     *
     * @param zone
     * @return
     */
    public List<MoneyChangeDBO> getMoneyCacheSaveModel(Zone zone) {
        return ((ZoneExtension) zone.getExtension()).getZoneCacheData().getMoneyCacheSaveModelCache();
    }

    public boolean pushSaveMoneyChange(MoneyChangeDBO moneyChangeDBO, Zone zone) {
        return getMoneyCacheSaveModel(zone).add(moneyChangeDBO);
    }

    public void clearCacheMoneyChange(Zone zone) {
        getMoneyCacheSaveModel(zone).clear();
    }



    /*------------------------------------------- ENERGY -------------------------------------------------------------*/
    public EnergyConfig getEnergyConfig() {
        return energyConfig;
    }

    // hunt
    public EnergyConfig getEnergyHuntConfig() {
        return energyHuntConfig;
    }

    public EnergyChangeVO getEnergyConfig(String id) {
        for (EnergyChangeVO cf : getEnergyConfig().up) {
            if (cf.id.equals(id)) {
                return cf;
            }
        }
        return null;
    }

    // hunt
    public EnergyChangeVO getEnergyHuntConfig(String id) {
        for (EnergyChangeVO cf : getEnergyHuntConfig().up) {
            if (cf.id.equals(id)) {
                return cf;
            }
        }
        return null;
    }

    public EnergyChargeInfo getEnergyInfo(UserBagModel userBagModel, Zone zone) {
        return userBagModel.readEnergy(zone);
    }

    //hunt
    public EnergyChargeInfo getEnergyHuntInfo(UserBagModel userBagModel, Zone zone) {
        return userBagModel.readEnergyHunt(zone);
    }

    public boolean changeEnergy(UserBagModel userBagModel, int point, Zone zone) {
        return userBagModel.changeEnergy(point, zone);
    }

    //hunt
    public boolean changeEnergyHunt(UserBagModel userBagModel, int point, Zone zone) {
        return userBagModel.changeEnergyHunt(point, zone);
    }

    public boolean changeEnergy(UserBagModel userBagModel, String id, Zone zone) {
        return userBagModel.changeEnergy(id, zone);
    }

    //hunt
    public boolean changeEnergyHunt(UserBagModel userBagModel, String id, Zone zone) {
        return userBagModel.changeEnergyHunt(id, zone);
    }

    public int getCountChargeEnergy(UserBagModel userBagModel, String id, Zone zone) {
        return userBagModel.readCountChargeUse(id, zone);
    }

    //hunt
    public int getCountChargeEnergyHunt(UserBagModel userBagModel, String id, Zone zone) {
        return userBagModel.readCountChargeUseHunt(id, zone);
    }
}
