package com.bamisu.log.gameserver.module.bag;

import com.bamisu.gamelib.item.define.ColorGem;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.datamodel.bag.entities.AdventureModel;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.adventure.AdventureManager;
import com.bamisu.log.gameserver.module.adventure.entities.LootVO;
import com.bamisu.log.gameserver.module.bag.cmd.rec.RecUsingItem;
import com.bamisu.log.gameserver.module.bag.cmd.send.SendUsingItem;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.log.gameserver.module.characters.summon.entities.HeroSummonVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ESummonType;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.define.ResourceType;
import com.bamisu.gamelib.item.define.SpecialItem;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.SpecialItemPackageVO;
import com.bamisu.gamelib.item.entities.StoneDataVO;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateVip;
import com.bamisu.log.gameserver.module.vip.VipHandler;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.cmd.send.SendShowInfoVipIAP;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.EVip;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.entities.VipData;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import org.apache.thrift.TException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Create by Popeye on 3:24 PM, 5/7/2020
 */
public class SpecialItemManager {
    public static int NUMBER_FRAGMENTS = 60;

    private BagHandler bagHandler;


    public SpecialItemManager(BagHandler bagHandler) {
        this.bagHandler = bagHandler;
    }

    /**
     * logic use special item
     *
     * @param getData
     * @param userModel
     * @param zone
     */
    public void useSpecialItem(RecUsingItem getData, UserModel userModel, Zone zone, User user) {
        SpecialItem specialItem = SpecialItem.fromID(getData.id);
        if(specialItem != null){
            switch (specialItem) {
                case GOLD_2HOURS:
                    useGOLD_2HOURS(getData, userModel, zone, user);
                    break;
                case GOLD_6HOURS:
                    useGOLD_6HOURS(getData, userModel, zone, user);
                    break;
                case GOLD_12HOURS:
                    useGOLD_12HOURS(getData, userModel, zone, user);
                    break;
                case GOLD_24HOURS:
                    useGOLD_24HOURS(getData, userModel, zone, user);
                    break;
                case MERITS_2HOURS:
                    useMERITS_2HOURS(getData, userModel, zone, user);
                    break;
                case MERITS_6HOURS:
                    useMERITS_6HOURS(getData, userModel, zone, user);
                    break;
                case MERITS_12HOURS:
                    useMERITS_12HOURS(getData, userModel, zone, user);
                    break;
                case MERITS_24HOURS:
                    useMERITS_24HOURS(getData, userModel, zone, user);
                    break;
                case ESSENCE_2HOURS:
                    useESSENCE_2HOURS(getData, userModel, zone, user);
                    break;
                case ESSENCE_6HOURS:
                    useESSENCE_6HOURS(getData, userModel, zone, user);
                    break;
                case ESSENCE_12HOURS:
                    useESSENCE_12HOURS(getData, userModel, zone, user);
                    break;
                case ESSENCE_24HOURS:
                    useESSENCE_24HOURS(getData, userModel, zone, user);
                    break;
                case COMMON_DIAMOND_CHEST_20:
                    useCOMMON_DIAMOND_CHEST_20(getData, userModel, zone, user);
                    break;
                case ELITE_DIAMOND_CHEST_50:
                    useELITE_DIAMOND_CHEST_50(getData, userModel, zone, user);
                    break;
                case EPIC_DIAMOND_CHEST_100:
                    useEPIC_DIAMOND_CHEST_100(getData, userModel, zone, user);
                    break;
                case LEGENDARY_DIAMOND_CHEST_250:
                    useLEGENDARY_DIAMOND_CHEST_250(getData, userModel, zone, user);
                    break;
                case CHOOSE_HERO_CHEST:
                    useCHOOSE_HERO_CHEST(getData, userModel, zone, user);
                    break;
                case RANDOM_EPIC_HERO_SHARDS_BLUE:
                    useRANDOM_EPIC_HERO_SHARDS_BLUE(getData, userModel, zone, user);
                    break;
                case RANDOM_LEGENDARY_HERO_SHARDS_PURPLE:
                    useRANDOM_LEGENDARY_HERO_SHARDS_PURPLE(getData, userModel, zone, user);
                    break;
                case DWARF_KINGDOM_EPIC_HERO_CARD_BLUE:
                    useDWARF_KINGDOM_EPIC_HERO_CARD_BLUE(getData, userModel, zone, user);
                    break;
                case DRUID_KINGDOM_EPIC_HERO_CARD_BLUE:
                    useDRUID_KINGDOM_EPIC_HERO_CARD_BLUE(getData, userModel, zone, user);
                    break;
                case BANISHED_KINGDOM_EPIC_HERO_CARD_BLUE:
                    useBANISHED_KINGDOM_EPIC_HERO_CARD_BLUE(getData, userModel, zone, user);
                    break;
                case RANDOM_COMMON_EQUIPMENT_CHEST_GREY:
                    useRANDOM_COMMON_EQUIPMENT_CHEST_GREY(getData, userModel, zone, user);
                    break;
                case RANDOM_RARE_EQUIPMENT_CHEST_GREEN:
                    useRANDOM_RARE_EQUIPMENT_CHEST_GREEN(getData, userModel, zone, user);
                    break;
                case RANDOM_ELITE_EQUIPMENT_CHEST_TEAL:
                    useRANDOM_ELITE_EQUIPMENT_CHEST_TEAL(getData, userModel, zone, user);
                    break;
                case RANDOM_EPIC_EQUIPMENT_CHEST_BLUE:
                    useRANDOM_EPIC_EQUIPMENT_CHEST_BLUE(getData, userModel, zone, user);
                    break;
                case RANDOM_LEGENDARY_EQUIPMENT_CHEST_PURPLE:
                    useRANDOM_LEGENDARY_EQUIPMENT_CHEST_PURPLE(getData, userModel, zone, user);
                    break;
                case RANDOM_COMMON_GEMS_CHEST_GREY:
                    useRANDOM_COMMON_GEMS_CHEST_GREY(getData, userModel, zone, user);
                    break;
                case RANDOM_RARE_GEM_CHEST_GREEN:
                    useRANDOM_RARE_GEM_CHEST_GREEN(getData, userModel, zone, user);
                    break;
                case RANDOM_ELITE_GEM_CHEST_TEAL:
                    useRANDOM_ELITE_GEM_CHEST_TEAL(getData, userModel, zone, user);
                    break;
                case RANDOM_EPIC_GEM_CHEST_BLUE:
                    useRANDOM_EPIC_GEM_CHEST_BLUE(getData, userModel, zone, user);
                    break;
                case RANDOM_LEGENDARY_GEM_CHEST_PURPLE:
                    useRANDOM_LEGENDARY_GEM_CHEST_PURPLE(getData, userModel, zone, user);
                    break;
                case PROTECTOR_EMBLEM_3DAYS:
                    usePROTECTOR_EMBLEM_3DAYS(getData, userModel, zone, user);
                    break;
                case PROTECTOR_EMBLEM_30DAYS:
                    usePROTECTOR_EMBLEM_30DAYS(getData, userModel, zone, user);
                    break;
                case ARCHMAGE_EMBLEM_3DAYS:
                    useARCHMAGE_EMBLEM_3DAYS(getData, userModel, zone, user);
                    break;
                case ARCHMAGE_EMBLEM_30DAYS:
                    useARCHMAGE_EMBLEM_30DAYS(getData, userModel, zone, user);
                    break;
                default:
                    SendUsingItem send = new SendUsingItem(ServerConstant.ErrorCode.ERR_COMING_SOON);
                    bagHandler.send(send, user);
                    break;
            }
        }
    }

    private void useDIAMOND_CHEST(UserModel userModel, SpecialItemPackageVO specialItemPackageVO, Zone zone, User user, int amount){
        List<ResourcePackage> list = new ArrayList<>();
        ResourcePackage resourcePackage = new ResourcePackage(MoneyType.DIAMOND.getId(), amount * specialItemPackageVO.amount);
        list.add(resourcePackage);
        ResourcePackage special = new ResourcePackage(specialItemPackageVO.id, -specialItemPackageVO.amount);
        list.add(special);
        if (BagManager.getInstance().addItemToDB(list, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_DIAMOND_CHEST)) {
            list.remove(special);
            List<ResourcePackage> listSpending = new ArrayList<>();
            listSpending.add(special);
            SendUsingItem send = new SendUsingItem();
            send.listReward = list;
            send.listSpending = listSpending;
            bagHandler.send(send, user);
            return;
        }
        SendUsingItem send = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
        bagHandler.send(send, user);
    }

    private void useGOLD(UserModel userModel, Zone zone, SpecialItemPackageVO specialItemPackageVO, User user, int time){
        AdventureModel adventureModel = AdventureManager.getInstance().getAdventureModel(userModel.userID, zone);
        List<LootVO> listMoney = AdventureManager.getInstance().getRewardFromLoot(adventureModel.timeLoot);
        List<ResourcePackage> list = new ArrayList<>();
        for (LootVO resourcePackage : listMoney) {
            if (resourcePackage.id.equals(MoneyType.GOLD.getId())) {
                resourcePackage.amount = (resourcePackage.amount * (time * 60)) * specialItemPackageVO.amount;
                list.add(new ResourcePackage(resourcePackage.id, (int) resourcePackage.amount));
                break;
            }
        }
        ResourcePackage resourcePackage = new ResourcePackage(specialItemPackageVO.id, -specialItemPackageVO.amount);
        list.add(resourcePackage);
        if (BagManager.getInstance().addItemToDB(list, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_GOLD)) {
            list.remove(resourcePackage);
            List<ResourcePackage> listSpending = new ArrayList<>();
            listSpending.add(resourcePackage);
            SendUsingItem send = new SendUsingItem();
            send.listReward = list;
            send.listSpending = listSpending;
            bagHandler.send(send, user);
            return;
        }
        SendUsingItem send = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
        bagHandler.send(send, user);
    }

    private void useMERITS(UserModel userModel, Zone zone, SpecialItemPackageVO specialItemPackageVO, User user, int time){
        AdventureModel adventureModel = AdventureManager.getInstance().getAdventureModel(userModel.userID, zone);
        List<LootVO> listMoney = AdventureManager.getInstance().getRewardFromLoot(adventureModel.timeLoot);
        List<ResourcePackage> list = new ArrayList<>();
        for (LootVO resourcePackage : listMoney) {
            if (resourcePackage.id.equals(MoneyType.MERITS.getId())) {
                resourcePackage.amount = (resourcePackage.amount * (time * 60)) * specialItemPackageVO.amount;
                list.add(new ResourcePackage(resourcePackage.id, (int) resourcePackage.amount));
                break;
            }
        }
        ResourcePackage resourcePackage = new ResourcePackage(specialItemPackageVO.id, -specialItemPackageVO.amount);
        list.add(resourcePackage);
        if (BagManager.getInstance().addItemToDB(list, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_MERIT)) {
            list.remove(resourcePackage);
            List<ResourcePackage> listSpending = new ArrayList<>();
            listSpending.add(resourcePackage);
            SendUsingItem send = new SendUsingItem();
            send.listReward = list;
            send.listSpending = listSpending;
            bagHandler.send(send, user);
            return;
        }
        SendUsingItem send = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
        bagHandler.send(send, user);
    }

    private void useVipItem(SpecialItemPackageVO specialItemPackageVO, UserModel userModel, Zone zone, User user, Collection<VipData> addListVipData) {
        try {
            List<ResourcePackage> listSpendResource = Collections.singletonList(new ResourcePackage(specialItemPackageVO.id, -specialItemPackageVO.amount));
            Collection<VipData> vipDataResult = SDKGateVip.addVip(userModel.accountID, addListVipData);
            if(vipDataResult == null){
                SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
                bagHandler.send(objPut, user);
                return;
            }

            if (!BagManager.getInstance().addItemToDB(listSpendResource, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_VIP)) {
                SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
                bagHandler.send(objPut, user);
                return;
            }

            VipManager.getInstance().updateCache(userModel.accountID, vipDataResult);

            //------Update notify------
            AdventureManager.getInstance().updateNotify(userModel.userID, user.getZone());
            //-------------------------

            //Send mail gift
            VipManager.getInstance().updateGiftFromMail(userModel.userID, zone);
            //--------------

            SendUsingItem objPut = new SendUsingItem();
            objPut.listSpending = listSpendResource;
            bagHandler.send(objPut, user);

            SendShowInfoVipIAP sendShowInfoVipIAP = new SendShowInfoVipIAP();
            sendShowInfoVipIAP.list = VipManager.getInstance().getListVip(userModel.accountID);
            VipManager.getInstance().sendVip(sendShowInfoVipIAP, user);

        } catch (TException e) {
            e.printStackTrace();

            SendUsingItem send = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(send, user);
        }
    }

    private void userEquipmentChest(SpecialItemPackageVO specialItemPackageVO, UserModel userModel, Zone zone, User user, int type, int level){
        List<EquipDataVO> listEquipAdd = new ArrayList<>();
        EquipDataVO equipDataVO;
        OUTERLOOP:
        for (int i = 0; i < specialItemPackageVO.amount; i++) {
            //Create item
            equipDataVO = ItemManager.getInstance().getRandomEquipDependOnStar(type, level);
            listEquipAdd.add(equipDataVO);
        }

        //Tieu tai nguyen
        List<ResourcePackage> listSpendResource = Collections.singletonList(new ResourcePackage(specialItemPackageVO.id, -specialItemPackageVO.amount));
        if(!BagManager.getInstance().addItemToDB(listSpendResource, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_EQUIPMENT_CHEST)){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }
        //Add equip
        if (!BagManager.getInstance().addNewWeapon(userModel.userID, zone, listEquipAdd)) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        SendUsingItem objPut = new SendUsingItem();
        objPut.listEquipData = new ArrayList<>(listEquipAdd.stream().
                collect(Collectors.toMap(obj -> obj.hash, Function.identity(), (oldValue, newValue) -> EquipDataVO.create(oldValue, oldValue.count + newValue.count))).values());
        objPut.listSpending = listSpendResource;
        bagHandler.send(objPut, user);
    }

    private void useGemChest(SpecialItemPackageVO specialItemPackageVO, UserModel userModel, Zone zone, User user, int level){
        List<StoneDataVO> listStoneData = new ArrayList<>();
        StoneDataVO stoneData;
        for (int i = 0; i < specialItemPackageVO.amount; i++) {
            //Create stone
            stoneData = ItemManager.getInstance().getRandomStoneDependOnStar(ColorGem.randomGem().getValue(), level);
            listStoneData.add(stoneData);
        }

        //Tieu tai nguyen
        List<ResourcePackage> listSpendResource = Collections.singletonList(new ResourcePackage(specialItemPackageVO.id, -specialItemPackageVO.amount));
        if(!BagManager.getInstance().addItemToDB(listSpendResource, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_GEM_CHEST)){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }
        //Add stone
        if (!BagManager.getInstance().addNewStone(userModel.userID, zone, listStoneData)) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        SendUsingItem send = new SendUsingItem();
        send.listStoneData = new ArrayList<>(listStoneData.stream().
                collect(Collectors.toMap(
                        obj -> obj.hash,
                        Function.identity(),
                        (oldValue, newValue) -> StoneDataVO.create(oldValue, oldValue.count + newValue.count))).values());
        send.listSpending = listSpendResource;
        bagHandler.send(send, user);
    }

    private void useESSENCE(UserModel userModel, Zone zone, SpecialItemPackageVO specialItemPackageVO, User user, int time){
        AdventureModel adventureModel = AdventureManager.getInstance().getAdventureModel(userModel.userID, zone);
        List<LootVO> listMoney = AdventureManager.getInstance().getRewardFromLoot(adventureModel.timeLoot);
        List<ResourcePackage> list = new ArrayList<>();
        for (LootVO lootVO : listMoney) {
            if (lootVO.id.equals(MoneyType.ESSENCE.getId())) {
                lootVO.amount = ((lootVO.amount * time * 60)/lootVO.perMinute) * specialItemPackageVO.amount;
                list.add(new ResourcePackage(lootVO.id, lootVO.amount));
                break;
            }
        }
        ResourcePackage resourcePackage = new ResourcePackage(specialItemPackageVO.id, -specialItemPackageVO.amount);
        list.add(resourcePackage);
        if (BagManager.getInstance().addItemToDB(list, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_ESSENCE)) {
            list.remove(resourcePackage);
            List<ResourcePackage> listSpending = new ArrayList<>();
            listSpending.add(resourcePackage);
            SendUsingItem send = new SendUsingItem();
            send.listReward = list;
            send.listSpending = listSpending;
            bagHandler.send(send, user);
            return;
        }
        SendUsingItem send = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
        bagHandler.send(send, user);
    }

    private void useARCHMAGE_EMBLEM_30DAYS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 30 * 86400 * recData.amount;

        useVipItem(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, Collections.singletonList(new VipData(EVip.PROTECTOR, time)));
    }

    private void useARCHMAGE_EMBLEM_3DAYS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 3 * 86400 * recData.amount;

        useVipItem(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, Collections.singletonList(new VipData(EVip.PROTECTOR, time)));
    }

    private void usePROTECTOR_EMBLEM_30DAYS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 30 * 86400 * recData.amount;

        useVipItem(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, Collections.singletonList(new VipData(EVip.ARCHMAGE, time)));
    }

    private void usePROTECTOR_EMBLEM_3DAYS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 3 * 86400 * recData.amount;

        useVipItem(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, Collections.singletonList(new VipData(EVip.ARCHMAGE, time)));
    }

    private void useRANDOM_LEGENDARY_GEM_CHEST_PURPLE(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int level = 5;

        useGemChest(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, level);
    }

    private void useRANDOM_EPIC_GEM_CHEST_BLUE(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int level = 4;

        useGemChest(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, level);
    }

    private void useRANDOM_ELITE_GEM_CHEST_TEAL(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int level = 3;

        useGemChest(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, level);
    }

    private void useRANDOM_RARE_GEM_CHEST_GREEN(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int level = 2;

        useGemChest(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, level);
    }

    private void useRANDOM_COMMON_GEMS_CHEST_GREY(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int level = 1;

        useGemChest(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, level);
    }

    private void useRANDOM_LEGENDARY_EQUIPMENT_CHEST_PURPLE(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int type = 5;
        int level = 0;

        userEquipmentChest(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, type, level);
    }

    private void useRANDOM_EPIC_EQUIPMENT_CHEST_BLUE(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int type = 4;
        int level = 0;

        userEquipmentChest(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, type, level);
    }

    private void useRANDOM_ELITE_EQUIPMENT_CHEST_TEAL(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int type = 3;
        int level = 0;

        userEquipmentChest(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, type, level);
    }

    private void useRANDOM_RARE_EQUIPMENT_CHEST_GREEN(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int type = 2;
        int level = 0;

        userEquipmentChest(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, type, level);
    }

    private void useRANDOM_COMMON_EQUIPMENT_CHEST_GREY(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int type = 1;
        int level = 0;

        userEquipmentChest(new SpecialItemPackageVO(recData.id, recData.amount), userModel, zone, user, type, level);
    }

    private void useBANISHED_KINGDOM_EPIC_HERO_CARD_BLUE(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        if (HeroManager.getInstance().isMaxSizeBagListHero(userModel.userID, recData.amount, zone)) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_MAX_SIZE_BAG_HERO);
            bagHandler.send(objPut, user);
            return;
        }

        //Get Hero
        List<HeroSummonVO> listHero = HeroManager.SummonManager.getInstance().summonUserHero(userModel.userID, recData.id, ESummonType.CHOOSE_KINGDOM, Kingdom.BANISHED.getId(), ResourceType.SPECIAL_ITEM, recData.amount, zone);
        if(listHero == null){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        //Tieu tai nguyen
        List<ResourcePackage> listSpendResource = Collections.singletonList(new ResourcePackage(recData.id, -recData.amount));
        if (!BagManager.getInstance().addItemToDB(listSpendResource, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_SUMMON_HERO)) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        //Add hero vao bag
        List<HeroModel> listHeroModel = HeroManager.SummonManager.getInstance().summonUserHero(userModel.userID, null, listHero, user.getZone(), false, null);
        if(listHeroModel == null){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        SendUsingItem objPut = new SendUsingItem();
        objPut.listHeroModel = listHeroModel;
        objPut.listSpending = listSpendResource;
        bagHandler.send(objPut, user);
    }

    private void useDRUID_KINGDOM_EPIC_HERO_CARD_BLUE(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        if (HeroManager.getInstance().isMaxSizeBagListHero(userModel.userID, recData.amount, zone)) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_MAX_SIZE_BAG_HERO);
            bagHandler.send(objPut, user);
            return;
        }

        //Get Hero
        List<HeroSummonVO> listHero = HeroManager.SummonManager.getInstance().summonUserHero(userModel.userID, recData.id, ESummonType.CHOOSE_KINGDOM, Kingdom.DRUID.getId(), ResourceType.SPECIAL_ITEM, recData.amount, zone);
        if(listHero == null){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        //Tieu tai nguyen
        List<ResourcePackage> listSpendResource = Collections.singletonList(new ResourcePackage(recData.id, -recData.amount));
        if (!BagManager.getInstance().addItemToDB(listSpendResource, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_SUMMON_HERO)) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        //Add hero vao bag
        List<HeroModel> listHeroModel = HeroManager.SummonManager.getInstance().summonUserHero(userModel.userID, null, listHero, user.getZone(), false, null);
        if(listHeroModel == null){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        SendUsingItem objPut = new SendUsingItem();
        objPut.listHeroModel = listHeroModel;
        objPut.listSpending = listSpendResource;
        bagHandler.send(objPut, user);
    }

    private void useDWARF_KINGDOM_EPIC_HERO_CARD_BLUE(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        if (HeroManager.getInstance().isMaxSizeBagListHero(userModel.userID, recData.amount, zone)) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_MAX_SIZE_BAG_HERO);
            bagHandler.send(objPut, user);
            return;
        }

        //Get Hero
        List<HeroSummonVO> listHeroSummon = HeroManager.SummonManager.getInstance().summonUserHero(userModel.userID, recData.id, ESummonType.CHOOSE_KINGDOM, Kingdom.DWARF.getId(), ResourceType.SPECIAL_ITEM, recData.amount, zone);
        if(listHeroSummon == null){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        //Tieu tai nguyen
        List<ResourcePackage> listSpendResource = Collections.singletonList(new ResourcePackage(recData.id, -recData.amount));
        if (!BagManager.getInstance().addItemToDB(listSpendResource, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_SUMMON_HERO)) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        //Add hero vao tui
        List<HeroModel> listHeroModel = HeroManager.SummonManager.getInstance().summonUserHero(userModel.userID, null, listHeroSummon, user.getZone(), false, null);
        if(listHeroModel == null){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        SendUsingItem objPut = new SendUsingItem();
        objPut.listHeroModel = listHeroModel;
        objPut.listSpending = listSpendResource;
        bagHandler.send(objPut, user);
    }

    private void useRANDOM_LEGENDARY_HERO_SHARDS_PURPLE(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        if (recData.amount % NUMBER_FRAGMENTS != 0) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_WRONG_QUANTITY);
            bagHandler.send(objPut, user);
            return;
        }

        int number = recData.amount / NUMBER_FRAGMENTS;
        if (HeroManager.getInstance().isMaxSizeBagListHero(userModel.userID, number, user.getZone())) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_MAX_SIZE_BAG_HERO);
            bagHandler.send(objPut, user);
            return;
        }

        //Get Hero
        List<HeroSummonVO> listHeroSummon = HeroManager.SummonManager.getInstance().summonUserHero(userModel.userID, recData.id, ESummonType.RANDOM, null, ResourceType.SPECIAL_ITEM, number, zone);
        if(listHeroSummon == null){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        //Tieu tai nguyen
        List<ResourcePackage> listSpendResource = Collections.singletonList(new ResourcePackage(recData.id, -recData.amount));
        if (!BagManager.getInstance().addItemToDB(listSpendResource, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_SUMMON_HERO)) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        //Add hero vao tui
        List<HeroModel> listHeroModel = HeroManager.SummonManager.getInstance().summonUserHero(userModel.userID, null, listHeroSummon, user.getZone(), false, null);
        if(listHeroModel == null){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        SendUsingItem send = new SendUsingItem();
        send.listHeroModel = listHeroModel;
        send.listSpending = listSpendResource;
        bagHandler.send(send, user);
    }

    private void useRANDOM_EPIC_HERO_SHARDS_BLUE(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        if (recData.amount % NUMBER_FRAGMENTS != 0) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_WRONG_QUANTITY);
            bagHandler.send(objPut, user);
            return;
        }

        int number = recData.amount / NUMBER_FRAGMENTS;
        if (HeroManager.getInstance().isMaxSizeBagListHero(userModel.userID, number, user.getZone())) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_MAX_SIZE_BAG_HERO);
            bagHandler.send(objPut, user);
            return;
        }

        //Get Hero
        List<HeroSummonVO> listHeroSummon = HeroManager.SummonManager.getInstance().summonUserHero(userModel.userID, recData.id, ESummonType.RANDOM, null, ResourceType.SPECIAL_ITEM, number, zone);
        if(listHeroSummon == null){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        //Tieu tai nguyen
        List<ResourcePackage> listSpendResource = Collections.singletonList(new ResourcePackage(recData.id, -recData.amount));
        if (!BagManager.getInstance().addItemToDB(listSpendResource, userModel.userID, zone, UserUtils.TransactionType.USE_SPECIAL_ITEM_SUMMON_HERO)) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        //Add Hero vao tui
        List<HeroModel> listHeroModel = HeroManager.SummonManager.getInstance().summonUserHero(userModel.userID, null, listHeroSummon, user.getZone(), false, null);
        if(listHeroModel == null){
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        SendUsingItem objPut = new SendUsingItem();
        objPut.listHeroModel = listHeroModel;
        objPut.listSpending = listSpendResource;
        bagHandler.send(objPut, user);
    }

    private void useCHOOSE_HERO_CHEST(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        List<ResourcePackage> listSpendResource = Collections.singletonList(new SpecialItemPackageVO(recData.id, -1).toResource());
        List<ResourcePackage> listRewardResource = Collections.singletonList(ItemManager.getInstance().getHeroInChest(recData.position));

        //Tieu tai nguyen
        List<ResourcePackage> listResource = new ArrayList<>();
        listResource.addAll(listSpendResource);
        listResource.addAll(listRewardResource);
        if (!BagManager.getInstance().addItemToDB(
                listResource,
                userModel.userID,
                zone,
                UserUtils.TransactionType.USE_SPECIAL_ITEM_SUMMON_HERO)) {
            SendUsingItem objPut = new SendUsingItem(ServerConstant.ErrorCode.ERR_SYS);
            bagHandler.send(objPut, user);
            return;
        }

        SendUsingItem objPut = new SendUsingItem();
        objPut.listReward = listRewardResource;
        objPut.listSpending = listSpendResource;
        bagHandler.send(objPut, user);
    }

    private void useLEGENDARY_DIAMOND_CHEST_250(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int amount = 250;

        useDIAMOND_CHEST(userModel, new SpecialItemPackageVO(recData.id, recData.amount), zone, user, amount);
    }

    private void useEPIC_DIAMOND_CHEST_100(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int amount = 100;

        useDIAMOND_CHEST(userModel, new SpecialItemPackageVO(recData.id, recData.amount), zone, user, amount);
    }

    private void useELITE_DIAMOND_CHEST_50(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int amount = 50;

        useDIAMOND_CHEST(userModel, new SpecialItemPackageVO(recData.id, recData.amount), zone, user, amount);
    }

    private void useCOMMON_DIAMOND_CHEST_20(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int amount = 20;

        useDIAMOND_CHEST(userModel, new SpecialItemPackageVO(recData.id, recData.amount), zone, user, amount);
    }

    private void useESSENCE_24HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 24;

        useESSENCE(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }

    private void useESSENCE_12HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 12;

        useESSENCE(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }

    private void useESSENCE_6HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 6;

        useESSENCE(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }

    private void useESSENCE_2HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 2;

        useESSENCE(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }

    private void useMERITS_24HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 24;

        useMERITS(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }

    private void useMERITS_12HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 12;

        useMERITS(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }

    private void useMERITS_6HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 6;

        useMERITS(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }

    private void useMERITS_2HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 2;

        useMERITS(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }

    private void useGOLD_24HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 24;

        useGOLD(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }

    private void useGOLD_12HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 12;

        useGOLD(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }

    private void useGOLD_6HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 6;

        useGOLD(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }


    private void useGOLD_2HOURS(RecUsingItem recData, UserModel userModel, Zone zone, User user) {
        int time = 2;

        useGOLD(userModel, zone, new SpecialItemPackageVO(recData.id, recData.amount), user, time);
    }
}
