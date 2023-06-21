package com.bamisu.log.gameserver.module.bag;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.define.ColorWeapon;
import com.bamisu.gamelib.item.define.ResourceType;
import com.bamisu.gamelib.item.entities.*;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.bag.MissionDetailModel;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.bag.entities.EnergyChargeInfo;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.bag.cmd.rec.*;
import com.bamisu.log.gameserver.module.bag.cmd.send.*;
import com.bamisu.log.gameserver.module.bag.config.EnergyConfig;
import com.bamisu.log.gameserver.module.bag.config.entities.EnergyChangeVO;
import com.bamisu.log.gameserver.module.bag.defind.EEnergyChargeType;
import com.bamisu.log.gameserver.module.bag.entities.ItemGet;
import com.bamisu.log.gameserver.module.characters.summon.entities.HeroSummonVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BagHandler extends ExtensionBaseClientRequestHandler {

    private SpecialItemManager specialItemManager;


    public BagHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_BAG;
        specialItemManager = new SpecialItemManager(this);
    }

    public UserModel getUserModel(long uid){
        return extension.getUserManager().getUserModel(uid);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_SHOW_INFO_WEAPON:
                handleShowInfoWeapon(user, data);
                break;
            case CMD.CMD_GET_WEAPON_TO_UPGRADE:
                handleGetWeaponToUpgrade(user, data);
                break;
            case CMD.CMD_GET_LIST_STONE:
                handleGetListStone(user, data);
                break;
            case CMD.CMD_UP_LEVEL_WEAPON:
                handleUpLevelWeapon(user, data);
                break;
            case CMD.CMD_ADD_STONE_TO_EQUIP:
                handleAddStoneToEquip(user, data);
                break;
            case CMD.CMD_REMOVE_STONE_FROM_EQUIP:
                handleRemoveStoneFromEquip(user, data);
                break;
            case CMD.CMD_FUSION_WEAPON:
                handleFusionWeapon(user, data);
                break;
            case CMD.CMD_LIST_FUSION_EQUIP:
                handleListFusionEquip(user, data);
                break;
            case CMD.CMD_FUSION_STONE:
                handleFusionStone(user, data);
                break;
            case CMD.CMD_LIST_FUSION_STONE:
                handleListFusionStone(user, data);
                break;
            case CMD.CMD_GET_LIST_SPECIAL_ITEM:
                handleGetListSpecialItem(user, data);
                break;
            case CMD.CMD_GET_LIST_FRAGMENT_HERO:
                handleGetListFragmentHero(user, data);
                break;
            case CMD.CMD_USING_ITEM:
                handleUsingItem(user, data);
                break;
            case CMD.CMD_USING_FRAGMENT_HERO:
                handleUsingFragmentHero(user, data);
                break;
            case CMD.CMD_GET_ALL_MONEY:
                handleGetAllMoney(user, data);
                break;
            case CMD.CMD_GET_LIST_EQUIP_HERO:
                handleGetListEquipInBag(user, data);
                break;
            case CMD.CMD_GET_ENERGY_BAR:
                handlerGetEnergy(user, data);
                break;
            case CMD.CMD_CHANGE_ENERGY_BAR:
                handlerChangeEnergy(user, data);
                break;
            case CMD.CMD_UPDATE_ENERGY_BAR:
                handlerUpdateEnergy(user, data);
                break;
            case CMD.CMD_GET_HUNT_ENERGY_BAR:
                handlerGetHuntEnergy(user, data);
                break;
            case CMD.CMD_CHANGE_HUNT_ENERGY_BAR:
                handlerChangeHuntEnergy(user, data);
                break;
            case CMD.CMD_UPDATE_HUNT_ENERGY_BAR:
                handlerUpdateEnergyHunt(user, data);
                break;
        }
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleShowInfoWeapon(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecShowInfoWeapon objGet = new RecShowInfoWeapon(data);

        //Kiem tra trong tui
        EquipDataVO equipDataVO = BagManager.getInstance().getHeroEquipInBag(uid, objGet.hash, getParentExtension().getParentZone());
        //Kiem tra trong hero
        if(equipDataVO == null) equipDataVO = HeroManager.getInstance().getEquipmentHeroModel(uid, objGet.hash, getParentExtension().getParentZone());

        if (equipDataVO == null){
            SendShowInfoWeapon send = new SendShowInfoWeapon(ServerConstant.ErrorCode.ERR_NOT_EXSIST_ITEM);
            send(send, user);
            return;
        }

        SendShowInfoWeapon send = new SendShowInfoWeapon();
        send.equipDataVO = equipDataVO;
        send(send, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleGetWeaponToUpgrade(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecGetWeaponToUpgrade objGet = new RecGetWeaponToUpgrade(data);
        //Check trong hero
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
        EquipDataVO equipData = HeroManager.getInstance().getEquipmentHeroModel(uid, objGet.hashItem, getParentExtension().getParentZone());
//        //Check trong tui
//        if(equipData == null) equipData = BagManager.getInstance().getHeroEquipInBag(userBagModel, objGet.hashItem);

        EquipVO equipVO = BagManager.getInstance().changeToEquipVO(equipData);
        if (equipVO == null || equipVO.star == ColorWeapon.GREY.getStar()) {
            SendGetWeaponToUpgrade send = new SendGetWeaponToUpgrade(ServerConstant.ErrorCode.ERR_WRONG_WEAPON);
            send(send, user);
            return;
        } else if (equipVO.level == equipVO.maxLevel){
            SendGetWeaponToUpgrade send = new SendGetWeaponToUpgrade(ServerConstant.ErrorCode.ERR_MAX_LEVEL);
            send(send, user);
            return;
        }

        SendGetWeaponToUpgrade objPut = new SendGetWeaponToUpgrade();
        objPut.listHammer = userBagModel.readHammer();
        objPut.listEquip = BagManager.getInstance().getListHeroEquipInBag(uid, getParentExtension().getParentZone());;
        objPut.equip = equipVO;
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleGetListStone(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        SendGetListStone objPut = new SendGetListStone();
        objPut.uid = uid;
        objPut.zone = getParentExtension().getParentZone();
        objPut.listStone = BagManager.getInstance().getListStoneInBag(uid, user.getZone());
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleUpLevelWeapon(User user, ISFSObject data) {
        UserModel userModel = extension.getUserManager().getUserModel(user);

        RecUpLevelWeapon objGet = new RecUpLevelWeapon(data);
        //Lay item tren hero
        EquipDataVO equipData = HeroManager.getInstance().getEquipmentHeroModel(userModel.userID, objGet.hashW, getParentExtension().getParentZone());
        EquipVO equipCf = BagManager.getInstance().changeToEquipVO(equipData);
        //Check ton tai tren nguoi hero + config
        if (equipCf == null){
            SendUpLevelWeapon send = new SendUpLevelWeapon(ServerConstant.ErrorCode.ERR_NOT_HAVE_EQUIP);
            send(send, user);
            return;
        }

        //Check max level
        if (equipCf.level == equipCf.maxLevel){
            SendUpLevelWeapon send = new SendUpLevelWeapon(ServerConstant.ErrorCode.ERR_MAX_LEVEL);
            send(send, user);
            return;
        }

        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(userModel.userID, getParentExtension().getParentZone());
        List<EquipDataVO> listEquipData = BagManager.getInstance().getListHeroEquipInBag(userBagModel, objGet.listWeapon.stream().map(obj -> obj.hash).collect(Collectors.toList()), getParentExtension().getParentZone());
        List<StoneDataVO> listStoneData = new ArrayList<>();
        listEquipData.forEach(equip -> listStoneData.addAll(equip.readListStoneEquip().stream().
                map(StoneDataVO::new).
                collect(Collectors.toList())));

        //Xoa item + up exp item
        List<ResourcePackage> listHammer = BagManager.getInstance().upgradeWeapon(userBagModel, equipCf, objGet.listWeapon, objGet.listHammer, user.getZone());
        if (listHammer == null){
            SendUpLevelWeapon send = new SendUpLevelWeapon(ServerConstant.ErrorCode.ERR_SYS);
            send(send, user);
            return;
        }

        //Add stone to bag (neu co)
        if(!BagManager.getInstance().addNewStone(userBagModel, listStoneData, getParentExtension().getParentZone())){
            SendUpLevelWeapon send = new SendUpLevelWeapon(ServerConstant.ErrorCode.ERR_SYS);
            send(send, user);
            return;
        }

        SendUpLevelWeapon objPut = new SendUpLevelWeapon();
        objPut.listHammer = listHammer;
        objPut.listStoneData = listStoneData;
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleAddStoneToEquip(User user, ISFSObject data) {
        synchronized (user){

            long uid = extension.getUserManager().getUserModel(user).userID;

            RecAddStoneToEquip objGet = new RecAddStoneToEquip(data);
            UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
            //Item stone can lap ngoc vao
            //TH tim trong hero
            EquipDataVO equipDataEquip = HeroManager.getInstance().getEquipmentHeroModel(uid, objGet.hashWeapon, user.getZone());
            EquipDataVO equipDataUnequip = null;
            if(equipDataEquip == null){
                //TH trong bag
                equipDataEquip = BagManager.getInstance().getHeroEquipInBag(userBagModel, objGet.hashWeapon, getParentExtension().getParentZone());
                if(equipDataEquip != null){
                    SendAddStoneToEquip send = new SendAddStoneToEquip(ServerConstant.ErrorCode.ERR_NOT_EQUIP_ITEM);
                    send(send, user);
                    return;
                }

                //TH khong tim thay tren ca hero vs bag
                if (equipDataEquip == null){
                    SendAddStoneToEquip send = new SendAddStoneToEquip(ServerConstant.ErrorCode.ERR_NOT_EXSIST_ITEM);
                    send(send, user);
                    return;
                }
            }

            //Stone trong item can lap vao == stone thao ra
            StoneDataVO stoneDataUnequip = BagManager.getInstance().getStoneInEquip(equipDataEquip, objGet.position);
            //Tim stone dc chon
            //TH stone dc chon trong bag khong trong equip
            StoneDataVO stoneDataEquip = BagManager.getInstance().getStoneInBag(userBagModel, objGet.hashStone);
            if(stoneDataEquip != null){
                //Xoa stone choose in bag
                stoneDataEquip = StoneDataVO.create1(stoneDataEquip);
                if(!BagManager.getInstance().deleteStoneInBag(userBagModel, stoneDataEquip, getParentExtension().getParentZone())){
                    SendAddStoneToEquip send = new SendAddStoneToEquip(ServerConstant.ErrorCode.ERR_SYS);
                    send(send, user);
                    return;
                }

                //Add stone dg co tren item stone lap vao vao bag (neu co)
                if(stoneDataUnequip != null && !BagManager.getInstance().addNewStone(userBagModel, stoneDataUnequip, getParentExtension().getParentZone())){
                    SendAddStoneToEquip send = new SendAddStoneToEquip(ServerConstant.ErrorCode.ERR_SYS);
                    send(send, user);
                    return;
                }

            }else {

                //TH stone choose trong bag trong equip
                List<EquipDataVO> listEquipDataBag = BagManager.getInstance().getListHeroEquipInBag(userBagModel, getParentExtension().getParentZone());
                equipDataUnequip = BagManager.getInstance().getHeroEquipHaveStone(listEquipDataBag, objGet.hashStone);
                if(equipDataUnequip == null){

                    //TH stone trong equip hero dang mac
                    listEquipDataBag = HeroManager.getInstance().getAllEquipmentHero(uid, getParentExtension().getParentZone());
                    equipDataUnequip = BagManager.getInstance().getHeroEquipHaveStone(listEquipDataBag, objGet.hashStone);
                }

                //Khong tim thay equip chua stone
                if(equipDataUnequip == null){
                    SendAddStoneToEquip send = new SendAddStoneToEquip(ServerConstant.ErrorCode.ERR_NOT_EXSIST_ITEM);
                    send(send, user);
                    return;
                }

                //Lay stone ra
                stoneDataEquip = BagManager.getInstance().getStoneInEquip(equipDataUnequip, objGet.position);
                if(stoneDataEquip == null){
                    SendAddStoneToEquip send = new SendAddStoneToEquip(ServerConstant.ErrorCode.ERR_NOT_EXSIST_STONE);
                    send(send, user);
                    return;
                }

                //Lap stone bo ra cua item dc chon vao item dang dung (= null la xoa stone)
                if(!BagManager.getInstance().updateStoneToEquip(uid, equipDataUnequip, stoneDataUnequip, objGet.position, getParentExtension().getParentZone())){
                    SendAddStoneToEquip objPut = new SendAddStoneToEquip(ServerConstant.ErrorCode.ERR_SYS);
                    send(objPut, user);
                    return;
                }
            }

            //Lap stone dc chon vao equip
            if(!BagManager.getInstance().updateStoneToEquip(uid, equipDataEquip, stoneDataEquip, objGet.position, getParentExtension().getParentZone())){
                SendAddStoneToEquip objPut = new SendAddStoneToEquip(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }

            SendAddStoneToEquip objPut = new SendAddStoneToEquip();
            objPut.hashItem = equipDataEquip.hash;
            objPut.hashStone = stoneDataEquip.hash;
            send(objPut, user);

        }
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleRemoveStoneFromEquip(User user, ISFSObject data) {
        synchronized (user){

            long uid = extension.getUserManager().getUserModel(user).userID;

            RecRemoveStoneFromEquip objGet = new RecRemoveStoneFromEquip(data);
            EquipDataVO equipData = HeroManager.getInstance().getEquipmentHeroModel(uid, objGet.hash, getParentExtension().getParentZone());
            //Kiem tra equip co tren nguoi hero
            if(equipData == null){
                SendRemoveStoneFromEquip objPut = new SendRemoveStoneFromEquip(ServerConstant.ErrorCode.ERR_NOT_HAVE_EQUIP);
                send(objPut, user);
                return;
            }

            //Get stone data
            StoneDataVO stoneData = BagManager.getInstance().getStoneInEquip(equipData, objGet.position);
            if(stoneData == null){
                SendRemoveStoneFromEquip objPut = new SendRemoveStoneFromEquip(ServerConstant.ErrorCode.ERR_NOT_EXSIST_STONE);
                send(objPut, user);
                return;
            }

            //Add vao tui
            if(!BagManager.getInstance().addNewStone(uid, stoneData, getParentExtension().getParentZone())){
                SendRemoveStoneFromEquip objPut = new SendRemoveStoneFromEquip(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }

            //Xoa stone from equip hero tren nguoi hero
            if (!BagManager.getInstance().removeStoneFromEquip(uid, equipData, objGet.position, getParentExtension().getParentZone())){
                SendRemoveStoneFromEquip objPut = new SendRemoveStoneFromEquip(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }

            SendRemoveStoneFromEquip objPut = new SendRemoveStoneFromEquip();
            objPut.hashItem = objGet.hash;
            send(objPut, user);

        }
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleFusionWeapon(User user, ISFSObject data) {
        UserModel userModel = extension.getUserManager().getUserModel(user);

        RecFusionWeapon objGet = new RecFusionWeapon(data);
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(userModel.userID, user.getZone());
        List<ItemGet> listItemGet = objGet.list.stream().
                map(obj -> ItemGet.create(obj.hash, obj.count)).
                collect(Collectors.toList());
        List<EquipDataVO> listEquipFusionDataBag = BagManager.getInstance().getHeroEquipInBag(userBagModel, listItemGet, getParentExtension().getParentZone());
        List<StoneDataVO> listStoneDataInEquip = new ArrayList<>();
        listEquipFusionDataBag.forEach(equip -> listStoneDataInEquip.addAll(equip.readListStoneEquip().stream().
                map(StoneDataVO::new).
                collect(Collectors.toList())));

        //Check item exsist
        if(listEquipFusionDataBag.isEmpty()){
            SendFusionWeapon objPut = new SendFusionWeapon(ServerConstant.ErrorCode.ERR_NOT_EXSIST_ITEM);
            send(objPut, user);
            return;
        }

        //Check size weapon received from client
        if (!BagManager.getInstance().isSizeFusionWeapon(listEquipFusionDataBag)){
            SendFusionWeapon objPut = new SendFusionWeapon(ServerConstant.ErrorCode.ERR_WRONG_SIZE_TO_FUSION_WEAPON);
            send(objPut, user);
            return;
        }

        //Check the stars are different from client
        if (!BagManager.getInstance().isSameStarWhenFusionWeapon(listEquipFusionDataBag)){
            SendFusionWeapon objPut = new SendFusionWeapon(ServerConstant.ErrorCode.ERR_DIFFERNT_STAR_WHEN_FUSION);
            send(objPut, user);
            return;
        }

        //Check max Star
        if (!BagManager.getInstance().isMaxStarFusionWeapon(listEquipFusionDataBag.get(0).star)){
            SendFusionWeapon objPut = new SendFusionWeapon(ServerConstant.ErrorCode.ERR_MAX_STAR_FUSION);
            send(objPut, user);
            return;
        }

        EquipDataVO equipGet = BagManager.getInstance().getFusionEquipHero(userBagModel, listEquipFusionDataBag, getParentExtension().getParentZone());
        if(equipGet == null){
            SendFusionWeapon objPut = new SendFusionWeapon(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Tieu resource
        List<ResourcePackage> resCost = BagManager.getInstance().getCostFusionEquipHero(listEquipFusionDataBag.get(0).star);
        if(!BagManager.getInstance().addItemToDB(resCost, userModel.userID, getParentExtension().getParentZone(), UserUtils.TransactionType.FUSION_EQUIP_HERO)){
            SendFusionWeapon objPut = new SendFusionWeapon(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(objPut, user);
            return;
        }

        //Xoa equip trong bag
        if(!BagManager.getInstance().deleteItemInBag(userBagModel, listEquipFusionDataBag, getParentExtension().getParentZone())){
            SendFusionWeapon objPut = new SendFusionWeapon(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Add stone trong bag
        if(!BagManager.getInstance().addNewStone(userBagModel, listStoneDataInEquip, getParentExtension().getParentZone())){
            SendFusionWeapon objPut = new SendFusionWeapon(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Add equip trong bag
        if(!BagManager.getInstance().addNewWeapon(userBagModel, getParentExtension().getParentZone(), equipGet)){
            SendFusionWeapon objPut = new SendFusionWeapon(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendFusionWeapon objPut = new SendFusionWeapon();
        objPut.newEquip = equipGet;
        objPut.listStoneDataNew = listStoneDataInEquip;
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleListFusionEquip(User user, ISFSObject data) {
        UserModel userModel = extension.getUserManager().getUserModel(user);

        RecListFusionEquip objGet = new RecListFusionEquip(data);
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(userModel.userID, user.getZone());

        List<List<EquipDataVO>> listEquipDataFusion = new ArrayList<>();
        List<EquipDataVO> listEquipDataGet;
        for(List<ItemGet> fusionList : objGet.list){
            listEquipDataGet = new ArrayList<>();

            for(ItemGet fusion : fusionList){
                listEquipDataGet.add(BagManager.getInstance().getHeroEquipInBag(userBagModel, fusion, getParentExtension().getParentZone()));
            }

            listEquipDataFusion.add(listEquipDataGet);
        }

        //Check ton tai
        for(List<EquipDataVO> groupEquipFusion : listEquipDataFusion){
            for(EquipDataVO equipData : groupEquipFusion){
                if(equipData == null){
                    SendListFusionEquip objPut = new SendListFusionEquip(ServerConstant.ErrorCode.ERR_NOT_EQUIP_ITEM);
                    send(objPut, user);
                    return;
                }
            }
        }

        //Check count fusion
        if(!BagManager.getInstance().isSizeListFusionWeapon(listEquipDataFusion)){
            SendListFusionEquip objPut = new SendListFusionEquip(ServerConstant.ErrorCode.ERR_WRONG_SIZE_TO_FUSION_WEAPON);
            send(objPut, user);
            return;
        }

        //Check invalid
        if(!BagManager.getInstance().isSameStarWhenListFusionWeapon(listEquipDataFusion)){
            SendListFusionEquip objPut = new SendListFusionEquip(ServerConstant.ErrorCode.ERR_DIFFERNT_STAR_WHEN_FUSION);
            send(objPut, user);
            return;
        }

        //Tao ra item moi
        List<EquipDataVO> listEquipNew = BagManager.getInstance().getListFusionEquipHero(userBagModel, listEquipDataFusion, getParentExtension().getParentZone());
        if(listEquipNew.isEmpty()){
            SendListFusionEquip objPut = new SendListFusionEquip(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Tieu resource
        List<ResourcePackage> resCost = new ArrayList<>();
        int count;
        for(List<EquipDataVO> fusionList : listEquipDataFusion){
            count = 0;

            for(EquipDataVO fusion : fusionList){
                count += fusion.count;
            }

            for(int i = 0; i < count / 5; i++){
                resCost.addAll(BagManager.getInstance().getCostFusionEquipHero(fusionList.get(0).star));
            }
        }
        resCost = new ArrayList<>(resCost.stream().
                collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue,newValue) -> new ResourcePackage(oldValue.id, oldValue.amount + newValue.amount))).values());
        if(!BagManager.getInstance().addItemToDB(resCost, userModel.userID, getParentExtension().getParentZone(), UserUtils.TransactionType.FUSION_EQUIP_HERO)){
            SendListFusionEquip objPut = new SendListFusionEquip(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(objPut, user);
            return;
        }

        //Add stone trong bag
        List<EquipDataVO> listEquipFission = new ArrayList<>();
        listEquipDataFusion.forEach(listEquipFission::addAll);
        List<StoneDataVO> listStoneInEquipFission = new ArrayList<>();
        listEquipFission.forEach(equip -> listStoneInEquipFission.addAll(equip.readListStoneEquip().stream().
                map(StoneDataVO::new).
                collect(Collectors.toList())));
        if(!BagManager.getInstance().addNewStone(userBagModel, listStoneInEquipFission, getParentExtension().getParentZone())){
            SendListFusionEquip objPut = new SendListFusionEquip(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Xoa equip trong bag
        if(!BagManager.getInstance().deleteItemInBag(userBagModel, listEquipFission, getParentExtension().getParentZone())){
            SendListFusionEquip objPut = new SendListFusionEquip(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Add equip trong bag
        if(!BagManager.getInstance().addNewWeapon(userBagModel, getParentExtension().getParentZone(), listEquipNew)){
            SendListFusionEquip objPut = new SendListFusionEquip(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendListFusionEquip objPut = new SendListFusionEquip();
        objPut.listNewEquip = listEquipNew;
        objPut.listStoneDataNew = listStoneInEquipFission;
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleFusionStone(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecFusionStone objGet = new RecFusionStone(data);
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
        int sizeFusionCf = ItemManager.getInstance().getSizeInFusionStone();
        StoneDataVO stoneData = BagManager.getInstance().getStoneInBag(userBagModel, ItemGet.create(objGet.fusion.hash, objGet.fusion.count * sizeFusionCf));
        if(stoneData == null){
            SendFusionStone send = new SendFusionStone(ServerConstant.ErrorCode.ERR_NOT_EXSIST_STONE);
            send(send, user);
            return;
        }

        //Check size stone received from  client
        if (!BagManager.getInstance().checkSizeFusionStone(Collections.singletonList(stoneData))){
            SendFusionStone send = new SendFusionStone(ServerConstant.ErrorCode.ERR_WRONG_SIZE_TO_FUSION_WEAPON);
            send(send, user);
            return;
        }

        StoneDataVO newStone = BagManager.getInstance().fusionStone(stoneData);
        if (newStone == null || newStone.count == 0){
            SendFusionStone send = new SendFusionStone(ServerConstant.ErrorCode.ERR_SYS);
            send(send, user);
            return;
        }

        //Tieu tai nguyen
        List<ResourcePackage> listResCost = BagManager.getInstance().getCostFusionStone(stoneData.level);
        if (!BagManager.getInstance().addItemToDB(listResCost, uid, getParentExtension().getParentZone(), UserUtils.TransactionType.FUSION_STONE)){
            SendFusionStone send = new SendFusionStone(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(send, user);
            return;
        }

        //Delete stone
        if(!BagManager.getInstance().deleteStoneInBag(userBagModel, stoneData, getParentExtension().getParentZone())){
            SendFusionStone send = new SendFusionStone(ServerConstant.ErrorCode.ERR_SYS);
            send(send, user);
            return;
        }

        //Add stone
        if(!BagManager.getInstance().addNewStone(userBagModel, newStone, getParentExtension().getParentZone())){
            SendFusionStone send = new SendFusionStone(ServerConstant.ErrorCode.ERR_SYS);
            send(send, user);
            return;
        }


        SendFusionStone send = new SendFusionStone();
        send.newStone = newStone;
        send(send, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleListFusionStone(User user, ISFSObject data) {
        UserModel userModel = extension.getUserManager().getUserModel(user);

        RecListFusionStone objGet = new RecListFusionStone(data);
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(userModel.userID, user.getZone());
        List<StoneDataVO> listStoneData = BagManager.getInstance().getStoneInBag(userBagModel, objGet.list);

        //Lay stone trong bag
        if(listStoneData.isEmpty()){
            SendListFusionStone send = new SendListFusionStone(ServerConstant.ErrorCode.ERR_NOT_EXSIST_STONE);
            send(send, user);
            return;
        }

        //Check size stone received from  client
        if (!BagManager.getInstance().checkSizeFusionStone(listStoneData)){
            SendListFusionStone send = new SendListFusionStone(ServerConstant.ErrorCode.ERR_WRONG_SIZE_TO_FUSION_WEAPON);
            send(send, user);
            return;
        }

        //Stone new
        List<StoneDataVO> listStoneNew = BagManager.getInstance().fusionStone(listStoneData);

        //Resource cost fusion stone
        int sizeFusionCf = ItemManager.getInstance().getSizeInFusionStone();
        List<ResourcePackage> listResCostFusion = new ArrayList<>();
        for(StoneDataVO stoneData : listStoneData){
            for(int i = 0; i < stoneData.count / sizeFusionCf; i++){
                listResCostFusion.addAll(BagManager.getInstance().getCostFusionStone(stoneData.level));
            }
        }
        listResCostFusion = new ArrayList<>(listResCostFusion.stream().
                collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> new ResourcePackage(oldValue.id, oldValue.amount + newValue.amount))).values());

        //Tieu tai nguyen
        if (!BagManager.getInstance().addItemToDB(listResCostFusion, userModel.userID, getParentExtension().getParentZone(), UserUtils.TransactionType.LIST_FUSION_GEM)) {
            SendListFusionStone objPut = new SendListFusionStone(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(objPut, user);
            return;
        }

        //Xoa stone
        if(!BagManager.getInstance().deleteStoneInBag(userBagModel, listStoneData, getParentExtension().getParentZone())){
            SendListFusionStone objPut = new SendListFusionStone(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Them stone
        if(!BagManager.getInstance().addNewStone(userBagModel, listStoneNew, getParentExtension().getParentZone())){
            SendListFusionStone objPut = new SendListFusionStone(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendListFusionStone objPut = new SendListFusionStone();
        objPut.listStoneNew = listStoneNew;
        send(objPut, user);
    }

    @WithSpan
    private void handleGetListEquipInBag(User user, ISFSObject data) {
        UserModel um = extension.getUserManager().getUserModel(user);
        UserBagModel bag = BagManager.getInstance().getUserBagModel(um.userID, user.getZone());

        SendGetListEquip send = new SendGetListEquip();
        send.list = bag.readListEquipHero(getParentExtension().getParentZone());
        send(send, user);
    }

    @WithSpan
    private void handleGetAllMoney(User user, ISFSObject data) {
        UserModel userModel = extension.getUserManager().getUserModel(user);
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(userModel.userID, getParentExtension().getParentZone());
        if(userBagModel.mapMoney.get(MoneyType.VOUCHER_LOTO_SOG.getId()) == null){ // khoi tao MON1024 neu chua co
            userBagModel.mapMoney.put("MON1024", new MoneyPackageVO("MON1024", 0));
            userBagModel.saveToDB(user.getZone());
        }

        SendGetAllMoney sendGetAllMoney = new SendGetAllMoney();
        sendGetAllMoney.mapMoney = userBagModel.mapMoney;
        send(sendGetAllMoney, user);
    }

    @WithSpan
    private void handleUsingFragmentHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecUsingFragmentHero objGet = new RecUsingFragmentHero(data);
        if (HeroManager.getInstance().isMaxSizeBagListHero(uid, objGet.count, user.getZone())){
            SendUsingFragmentHero objPut = new SendUsingFragmentHero(ServerConstant.ErrorCode.ERR_MAX_SIZE_BAG_HERO);
            send(objPut, user);
            return;
        }

        //Get Hero
        List<HeroSummonVO> listHero = HeroManager.SummonManager.getInstance().summonUserHero(uid, objGet.id, null, null, ResourceType.FRAGMENT_HERO, objGet.count, getParentExtension().getParentZone());

        //Error System
        if (listHero == null){
            SendUsingFragmentHero objPut = new SendUsingFragmentHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }


        if (!BagManager.getInstance().checkFragmentHero(uid, user.getZone(), objGet.id, objGet.count)){
            SendUsingFragmentHero objPut = new SendUsingFragmentHero(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_FRAGMENT);
            send(objPut, user);
            return;
        }

        List<HeroModel> listSummonModel = HeroManager.SummonManager.getInstance().summonUserHero(uid, null, listHero, user.getZone(), false, null);
        if (listSummonModel == null){
            SendUsingFragmentHero objPut = new SendUsingFragmentHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendUsingFragmentHero objPut = new SendUsingFragmentHero();
        objPut.listHero = listSummonModel;
        send(objPut, user);

    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handlerGetEnergy(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());

        synchronized (userBagModel){
            SendGetEnergy objPut = new SendGetEnergy();
            objPut.userBagModel = userBagModel;
            objPut.energyInfo = BagManager.getInstance().getEnergyInfo(userBagModel, getParentExtension().getParentZone());
            objPut.zone = getParentExtension().getParentZone();
            send(objPut, user);
        }
        MissionDetailModel missionDetailModel = MissionDetailModel.copyFromDBtoObject(user.getZone());

        ((ZoneExtension) user.getZone().getExtension()).getZoneCacheData().updateMissionDetail(uid, Utils.getTimestampInSecond(),60*60);
    }

    @WithSpan
    private void handlerGetHuntEnergy(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());

        synchronized (userBagModel){
            SendGetEnergyHunt objPut = new SendGetEnergyHunt();
            objPut.userBagModel = userBagModel;
            objPut.energyInfo = BagManager.getInstance().getEnergyHuntInfo(userBagModel, getParentExtension().getParentZone());
            objPut.zone = getParentExtension().getParentZone();
            send(objPut, user);
        }
    }


    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handlerChangeEnergy(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecChangeEnergy objGet = new RecChangeEnergy(data);
        EEnergyChargeType chargeType = EEnergyChargeType.fromID(objGet.id);
        EnergyChangeVO cf = BagManager.getInstance().getEnergyConfig(objGet.id);
        if(chargeType == null || cf == null){
            SendChangeEnergy objPut = new SendChangeEnergy(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Kiem tra da sd chua
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
        if(BagManager.getInstance().getCountChargeEnergy(userBagModel, objGet.id, getParentExtension().getParentZone()) >= cf.max){
            SendChangeEnergy objPut = new SendChangeEnergy(ServerConstant.ErrorCode.ERR_OUT_CHARGR_ENERGY);
            send(objPut, user);
            return;
        }

        //Kiem tra du dk chua
        if(!chargeType.haveSatifyCondition(uid, getParentExtension().getParentZone())){
            SendChangeEnergy objPut = new SendChangeEnergy(ServerConstant.ErrorCode.ERR_NOT_ENOUGHT_CONDITION_CHARGR_ENERGY);
            send(objPut, user);
            return;
        }

        if(!BagManager.getInstance().changeEnergy(userBagModel, objGet.id, getParentExtension().getParentZone())){
            SendChangeEnergy objPut = new SendChangeEnergy(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendChangeEnergy objPut = new SendChangeEnergy();
        objPut.id = objGet.id;
        send(objPut, user);
    }

    @WithSpan
    private void handlerChangeHuntEnergy(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecChangeEnergy objGet = new RecChangeEnergy(data);
        EEnergyChargeType chargeType = EEnergyChargeType.fromID(objGet.id);
        EnergyChangeVO cf = BagManager.getInstance().getEnergyHuntConfig(objGet.id);
        if(chargeType == null || cf == null){
            SendChangeEnergyHunt objPut = new SendChangeEnergyHunt(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Kiem tra da sd chua
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
        if(BagManager.getInstance().getCountChargeEnergyHunt(userBagModel, objGet.id, getParentExtension().getParentZone()) >= cf.max){
            SendChangeEnergyHunt objPut = new SendChangeEnergyHunt(ServerConstant.ErrorCode.ERR_OUT_CHARGR_ENERGY);
            send(objPut, user);
            return;
        }

        //Kiem tra du dk chua
        if(!chargeType.haveSatifyCondition(uid, getParentExtension().getParentZone())){
            SendChangeEnergyHunt objPut = new SendChangeEnergyHunt(ServerConstant.ErrorCode.ERR_NOT_ENOUGHT_CONDITION_CHARGR_ENERGY);
            send(objPut, user);
            return;
        }

        if(!BagManager.getInstance().changeEnergyHunt(userBagModel, objGet.id, getParentExtension().getParentZone())){
            SendChangeEnergyHunt objPut = new SendChangeEnergyHunt(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendChangeEnergyHunt objPut = new SendChangeEnergyHunt();
        objPut.id = objGet.id;
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handlerUpdateEnergy(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecUpdateEnergy objGet = new RecUpdateEnergy(data);
        List<String> hashHeroNotEmpty = objGet.hashHeros.stream()
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());
        if (hashHeroNotEmpty.size() > 3) {
            SendUpdateEnergy objPut = new SendUpdateEnergy(ServerConstant.ErrorCode.ERR_UPDATE_ENERGY_HERO_COUNT);
            send(objPut, user);
            return;
        }
        List<HeroModel> heroModels = HeroManager.getInstance().getHeroModel(uid, hashHeroNotEmpty, getParentExtension().getParentZone());
        if (hashHeroNotEmpty.size() != heroModels.size()) {
            SendUpdateEnergy objPut = new SendUpdateEnergy(ServerConstant.ErrorCode.ERR_UPDATE_ENERGY_HERO_NOT_EXIST);
            send(objPut, user);
            return;
        }

        if (heroModels.stream().anyMatch(heroModel -> Objects.equals(heroModel.type, EHeroType.NORMAL.getId()))) {
            SendUpdateEnergy objPut = new SendUpdateEnergy(ServerConstant.ErrorCode.ERR_UPDATE_ENERGY_HERO_INVALID);
            send(objPut, user);
            return;
        }

        EnergyConfig energyConfig = BagManager.getInstance().getEnergyConfig();
        int increase = 0;
        for (HeroModel heroModel : heroModels) {
            increase += energyConfig.increase.getOrDefault((int) heroModel.star, 0);
        }

        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
        EnergyChargeInfo energyChargeInfo = BagManager.getInstance().getEnergyInfo(userBagModel, getParentExtension().getParentZone());
        userBagModel.energy.heros = objGet.hashHeros;
        userBagModel.energy.increase = increase;
        if (!userBagModel.saveToDB(getParentExtension().getParentZone())) {
            SendUpdateEnergy objPut = new SendUpdateEnergy(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        synchronized (userBagModel){
            SendUpdateEnergy objPut = new SendUpdateEnergy();
            objPut.userBagModel = userBagModel;
            objPut.energyInfo = energyChargeInfo;
            objPut.zone = getParentExtension().getParentZone();
            send(objPut, user);
        }
    }

    @WithSpan
    private void handlerUpdateEnergyHunt(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecUpdateEnergy objGet = new RecUpdateEnergy(data);
        List<String> hashHeroNotEmpty = objGet.hashHeros.stream()
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());
        if (hashHeroNotEmpty.size() > 3) {
            SendUpdateEnergyHunt objPut = new SendUpdateEnergyHunt(ServerConstant.ErrorCode.ERR_UPDATE_ENERGY_HERO_COUNT);
            send(objPut, user);
            return;
        }
        List<HeroModel> heroModels = HeroManager.getInstance().getHeroModel(uid, hashHeroNotEmpty, getParentExtension().getParentZone());
        if (hashHeroNotEmpty.size() != heroModels.size()) {
            SendUpdateEnergyHunt objPut = new SendUpdateEnergyHunt(ServerConstant.ErrorCode.ERR_UPDATE_ENERGY_HERO_NOT_EXIST);
            send(objPut, user);
            return;
        }

        if (heroModels.stream().anyMatch(heroModel -> Objects.equals(heroModel.type, EHeroType.NORMAL.getId()))) {
            SendUpdateEnergyHunt objPut = new SendUpdateEnergyHunt(ServerConstant.ErrorCode.ERR_UPDATE_ENERGY_HERO_INVALID);
            send(objPut, user);
            return;
        }

        EnergyConfig energyConfig = BagManager.getInstance().getEnergyHuntConfig();
        int increase = 0;
        for (HeroModel heroModel : heroModels) {
            increase += energyConfig.increase.getOrDefault((int) heroModel.star, 0);
        }

        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
        EnergyChargeInfo energyChargeInfo = BagManager.getInstance().getEnergyHuntInfo(userBagModel, getParentExtension().getParentZone());
        userBagModel.energyHunt.heros = objGet.hashHeros;
        userBagModel.energyHunt.increase = increase;
        if (!userBagModel.saveToDB(getParentExtension().getParentZone())) {
            SendUpdateEnergyHunt objPut = new SendUpdateEnergyHunt(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        synchronized (userBagModel){
            SendUpdateEnergyHunt objPut = new SendUpdateEnergyHunt();
            objPut.userBagModel = userBagModel;
            objPut.energyInfo = energyChargeInfo;
            objPut.zone = getParentExtension().getParentZone();
            send(objPut, user);
        }
    }

    @WithSpan
    private void handleUsingItem(User user, ISFSObject data) {
        UserModel userModel = extension.getUserManager().getUserModel(user);

        RecUsingItem objGet = new RecUsingItem(data);
        specialItemManager.useSpecialItem(objGet, userModel, user.getZone(), user);
    }

    @WithSpan
    private void handleGetListFragmentHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        List<FragmentVO> listFragment = BagManager.getInstance().getListFragmentHero(uid, user.getZone());

        SendGetListFragmentHero send = new SendGetListFragmentHero();
        send.listFragment = listFragment;
        send(send, user);
    }

    @WithSpan
    private void handleGetListSpecialItem(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        Map<String, SpecialItemPackageVO> mapItem = BagManager.getInstance().getListSpecialItem(uid, user.getZone());

        SendGetListSpecialItem send = new SendGetListSpecialItem();
        send.mapItem = mapItem;
        send(send, user);
    }

    @WithSpan
    public void notifyChaneMoney(ISFSObject rec){
        ISFSArray arrayCurrent = rec.getSFSArray(Params.DATA);
        User user = ExtensionUtility.getInstance().getUserById(arrayCurrent.getSFSObject(0).getLong(Params.UID));
        if (user == null) {
            return;
        }
        SendNotifyChangeMoney sendCmd = new SendNotifyChangeMoney();
        sendCmd.arrayCurrent = arrayCurrent;
        send(sendCmd, user);
    }

    @WithSpan
    public void notifyChaneResource(ISFSObject rec){
        ISFSArray arrayCurrent = rec.getSFSArray(Params.DATA);
        User user = ExtensionUtility.getInstance().getUserById(arrayCurrent.getSFSObject(0).getLong(Params.UID));
        if (user == null) {
            return;
        }
        SendNotifyChangeResource sendCmd = new SendNotifyChangeResource();
        sendCmd.arrayCurrent = arrayCurrent;
        send(sendCmd, user);
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_BAG, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_BAG, this);
    }
}