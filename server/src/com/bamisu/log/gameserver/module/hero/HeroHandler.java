package com.bamisu.log.gameserver.module.hero;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.item.define.HeroResource;
import com.bamisu.gamelib.item.define.ResourceType;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.RabbitMQHandler;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.bag.entities.EWeaponType;
import com.bamisu.log.gameserver.datamodel.bag.entities.EnergyChargeInfo;
import com.bamisu.log.gameserver.datamodel.hero.*;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroSlotBlessing;
import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.log.gameserver.datamodel.nft.UserBurnHeroModel;
import com.bamisu.log.gameserver.datamodel.nft.entities.HeroUpstarBurn;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.bag.cmd.send.SendUpdateEnergy;
import com.bamisu.log.gameserver.module.bag.config.EnergyConfig;
import com.bamisu.log.gameserver.module.bag.entities.ItemGet;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.clas.EClass;
import com.bamisu.log.gameserver.module.characters.entities.Hero;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.characters.star.entities.GraftHeroVO;
import com.bamisu.log.gameserver.module.characters.summon.entities.HeroSummonVO;
import com.bamisu.log.gameserver.module.characters.summon.entities.RewardVO;
import com.bamisu.log.gameserver.module.friends.FriendHeroManager;
import com.bamisu.log.gameserver.module.hero.cmd.rec.*;
import com.bamisu.log.gameserver.module.hero.cmd.send.*;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.bamisu.log.gameserver.module.hero.define.ESummonID;
import com.bamisu.log.gameserver.module.hero.define.ESummonType;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.hero.entities.HeroUpModel;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.log.gameserver.module.hero.exception.InvalidUpdateTeamException;
import com.bamisu.log.gameserver.module.hunt.HuntManager;
import com.bamisu.log.gameserver.module.nft.NFTManager;
import com.bamisu.log.gameserver.module.nft.cmd.send.SendNotifyMintHero;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HeroHandler extends ExtensionBaseClientRequestHandler {
    private Logger logger = Logger.getLogger(HeroHandler.class);

    public HeroHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_HERO;
    }

    public UserModel getUserModel(User user){
        return extension.getUserManager().getUserModel(user);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId) {
            case CMD.CMD_LOAD_SCENE_GET_LIST_HERO:
                doLoadSceneGetListHero(user, data);
                break;
            case CMD.CMD_GET_USER_HERO_COLLECTION:
                doGetUserHeroCollection(user, data);
                break;
            case CMD.CMD_UP_SIZE_HERO_MODEL:
                doUpSizeHeroModel(user, data);
                break;
            case CMD.CMD_UNEQUIP_ALL_ITEM_ALL_HERO:
                doUnequipAllItemAllHero(user, data);
                break;
            case CMD.CMD_GET_USER_HERO_INFO:
                doGetUserHeroInfo(user, data);
                break;
            case CMD.CMD_UP_LEVEL_USER_HERO:
                doUpLevelUserHero(user, data);
                break;
            case CMD.CMD_SHOW_ITEM_HERO_CAN_EQUIP:
                doShowItemHeroCanEquip(user, data);
                break;
            case CMD.CMD_EQUIP_ITEM_HERO:
                doEquipItemHero(user, data);
                break;
            case CMD.CMD_UNEQUIP_ITEM_HERO:
                doUnequipItemHero(user, data);
                break;
            case CMD.CMD_EQUIP_ITEM_HERO_QUICK:
                doEquipItemHeroQuick(user, data);
                break;
            case CMD.CMD_UNEQUIP_ITEM_HERO_QUICK:
                doUnequipItemHeroQuick(user, data);
                break;
            case CMD.CMD_GET_BONUS_STORY:
                doGetBonusStory(user, data);
                break;
            case CMD.CMD_LOAD_SCENE_SUMMON_HERO:
                doLoadSceneSummonHero(user, data);
                break;
            case CMD.CMD_SUMMON_USER_HERO:
                doSummonUserHero(user, data);
                break;
            case CMD.CMD_SUMMON_BONUS_USER_HERO:
                doSummonUserHeroBonus(user, data);
                break;
            case CMD.CMD_UPDATE_DAY_SUMMON_HERO:
                doUpdateDayUserSummonModel(user, data);
                break;
            case CMD.CMD_LOAD_SCENE_TEAM_HERO:
                doLoadSceneTeamHero(user, data);
                break;
            case CMD.CMD_UPDATE_TEAM_HERO:
                doUpdateTeamHero(user, data);
                break;
            case CMD.CMD_LOAD_SCENE_HERO_BLESSING:
                doLoadSceneHeroBlessing(user, data);
                break;
            case CMD.CMD_UPDATE_HERO_BLESSING:
                doUpdateHeroBlessing(user, data);
                break;
            case CMD.CMD_REMOVE_HERO_BLESSING:
                doRemoveHeroBlessing(user, data);
                break;
            case CMD.CMD_REDUCE_COUNTDOWN_BLESSING:
                doReduceCountdownBlessing(user, data);
                break;
            case CMD.CMD_OPEN_SLOT_BLESSING:
                doOpenSlotBlessing(user, data);
                break;
            case CMD.CMD_LOAD_SCENE_UP_STAR_HERO:
                doLoadSceneUpStarHero(user, data);
                break;
            case CMD.CMD_UP_STAR_USER_HERO:
                doUpStarUserHero(user, data);
                break;
//            case CMD.CMD_UP_STAR_LIST_USER_HERO:
//                doUpStarListUserHero(user, data);
//                break;
            case CMD.CMD_LOAD_SCENE_RESET_HERO:
                doLoadSceneResetHero(user, data);
                break;
            case CMD.CMD_RESET_HERO:
                doResetHero(user, data);
                break;
            case CMD.CMD_LOAD_SCENE_RETIRE_HERO:
                doLoadSceneRetireHero(user, data);
                break;
            case CMD.CMD_RETIRE_HERO:
                doRetireHero(user, data);
                break;
            case CMD.CMD_SWITCH_AUTO_RETIRE_HERO:
                doSwitchAutoRetireHero(user, data);
                break;
            case CMD.CMD_GET_HERO_FRIEND_BORROW:
                doGetHeroFriendBorrow(user, data);
                break;
            case CMD.CMD_GET_IDLE_HERO_DATA:
                doGetIdleHeroData(user, data);
                break;
        }
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_HERO, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_HERO, this);
    }





    /*--------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------------*/

    /**
     * Khi Load Scene : hien thi Kingdom + Element + gioi han HeroVO
     */
    @WithSpan
    private void doLoadSceneGetListHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        SendLoadSceneGetListHero packet = this.doLoadSceneGetListHero(uid, false);
        send(packet, user);
    }

    @WithSpan
    private SendLoadSceneGetListHero doLoadSceneGetListHero(long uid, boolean isHttp) {
        UserAllHeroModel userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, getParentExtension().getParentZone());
        UserBlessingHeroModel userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, getParentExtension().getParentZone());

        SendLoadSceneGetListHero objPut = new SendLoadSceneGetListHero();
        objPut.countIncreateBag = userAllHeroModel.countIncreateBag;
        objPut.maxBagHero = userAllHeroModel.readSizeBagHero(getParentExtension().getParentZone());
        objPut.userAllHeroModel = userAllHeroModel;
        objPut.userBlessingHeroModel = userBlessingHeroModel;
        objPut.zone = getParentExtension().getParentZone();
        objPut.isHttp = isHttp;
        objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone()).parallelStream().
                map(HeroModel::createByHeroModel).
                collect(Collectors.toList());
        return objPut;
    }

    @WithSpan
    public SFSObject doLoadSceneGetListHero(ISFSObject data) {
        long uid = data.getLong(Params.UID);
        SendLoadSceneGetListHero packet = this.doLoadSceneGetListHero(uid, true);
        packet.packData();
        return (SFSObject) packet.getData();
    }


    /**
     * Get HeroVO Info + da loc
     */
    @WithSpan
    private void doGetUserHeroCollection(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        SendGetUserHeroCollection objPut = new SendGetUserHeroCollection();
        objPut.listId = HeroManager.getInstance().getListHeroCollection(uid, getParentExtension().getParentZone());
        send(objPut, user);
    }


    /**
     * Tang kich co chua hero
     */
    @WithSpan
    private void doUpSizeHeroModel(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserAllHeroModel userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, getParentExtension().getParentZone());
        //Tieu tai nguyen
        if (!BagManager.getInstance().addItemToDB(
                CharactersConfigManager.getInstance().getCostUpSizeBagHeroConfig(userAllHeroModel.countIncreateBag + 1).parallelStream().
                        map(obj -> new ResourcePackage(obj.id, -obj.amount)).
                        collect(Collectors.toList()),
                uid,
                getParentExtension().getParentZone(), UserUtils.TransactionType.UPSIZE_HERO_BAG)) {

            SendUpSizeHeroModel objPut = new SendUpSizeHeroModel(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(objPut, user);
            return;
        }

        //Tang size
        if (!HeroManager.getInstance().upSizeBagListHero(userAllHeroModel, CharactersConfigManager.getInstance().getBagHeroConfig().increatePer, getParentExtension().getParentZone())) {
            SendUpSizeHeroModel objPut = new SendUpSizeHeroModel(ServerConstant.ErrorCode.ERR_LIMIT_UP_SIZE_BAG_HERO);
            send(objPut, user);
            return;
        }

        SendUpSizeHeroModel objPut = new SendUpSizeHeroModel();
        objPut.maxSizeBag = userAllHeroModel.readSizeBagHero(getParentExtension().getParentZone());
        send(objPut, user);
    }


    /**
     * Thao toan bo do tren toan bo tuong
     */
    @WithSpan
    private void doUnequipAllItemAllHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        List<HeroModel> listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone());

        //Lay toan bo trang bi tren toan bo tuong
        List<EquipDataVO> listEquip = new ArrayList<>();
        listHeroModel.forEach(obj -> listEquip.addAll(HeroManager.getInstance().getAllEquipmentHero(obj)));
        List<String> listHashHero = listHeroModel.stream().map(obj -> obj.hash).collect(Collectors.toList());
        //Xoa toan bo item tren nguoi hero
        if (!HeroManager.getInstance().deleteAllEquipmentHeroModel(uid, listHashHero, extension.getParentZone())) {
            SendUnequipAllItemAllHero objPut = new SendUnequipAllItemAllHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Add do vao tui
        if (!BagManager.getInstance().addNewWeapon(uid, getParentExtension().getParentZone(), listEquip)) {
            SendUnequipAllItemAllHero objPut = new SendUnequipAllItemAllHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendUnequipAllItemAllHero objPut = new SendUnequipAllItemAllHero();
        send(objPut, user);
    }


    /**
     * Get HeroVO Info - Scene chi tiet tuong
     */
    @WithSpan
    private void doGetUserHeroInfo(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecGetUserHeroInfo objGet = new RecGetUserHeroInfo(data);

        UserAllHeroModel userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, getParentExtension().getParentZone());
        UserBlessingHeroModel userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, getParentExtension().getParentZone());
        HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, objGet.hashHero, getParentExtension().getParentZone());
        if (heroModel == null) {
            SendGetUserHeroInfo objPut = new SendGetUserHeroInfo(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            send(objPut, user);
            return;
        }

        SendGetUserHeroInfo objPut = new SendGetUserHeroInfo();
        objPut.userAllHeroModel = userAllHeroModel;
        objPut.userBlessingHeroModel = userBlessingHeroModel;
        objPut.heroModel = heroModel;
        objPut.zone = getParentExtension().getParentZone();
        //Kim cuong nhan dc trong story hero
        objPut.bonusStory = HeroManager.getInstance().getBonusStoryHero(uid, heroModel.id, getParentExtension().getParentZone());
        objPut.skillModel = HeroSkillModel.getFromDB(heroModel, getParentExtension().getParentZone());

        send(objPut, user);
    }


    /**
     * Update level HeroVO - Co ca dot pha
     */
    @WithSpan
    private void doUpLevelUserHero(User user, ISFSObject data) {

        synchronized (user){

            long uid = extension.getUserManager().getUserModel(user).userID;
            RecUpLevelUserHero objGet = new RecUpLevelUserHero(data);

            HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, objGet.hashHero, getParentExtension().getParentZone());
            if (heroModel == null) {
                SendUpLevelUserHero objPut = new SendUpLevelUserHero(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
                send(objPut, user);
                return;
            }

            if(objGet.level <= heroModel.readLevel()){
                SendUpLevelUserHero objPut = new SendUpLevelUserHero(ServerConstant.ErrorCode.ERR_INVALID_UP_LEVEL_HERO);
                send(objPut, user);
                return;
            }

            //Check dang ban phuoc khong
            if (HeroManager.BlessingManager.getInstance().haveBlessing(uid, heroModel.hash, getParentExtension().getParentZone())) {
                SendUpLevelUserHero objPut = new SendUpLevelUserHero(ServerConstant.ErrorCode.ERR_HERO_BEING_BLESSING);
                send(objPut, user);
                return;
            }
            //Check max level
            if (CharactersConfigManager.getInstance().isMaxLevelHeroConfig(heroModel.star, heroModel.readLevel())) {
                SendUpLevelUserHero objPut = new SendUpLevelUserHero(ServerConstant.ErrorCode.ERR_LIMIT_LEVEL_CHARACTER);
                send(objPut, user);
                return;
            }

            //Tai nguyen can truoc khi tang cap
            List<MoneyPackageVO> listSpendResource = new ArrayList<>();
            for(int i = heroModel.readLevel() + 1; i <= objGet.level; i++){
                listSpendResource.addAll(HeroManager.getInstance().getCostUpdateLevelHero(i));
            }

            //Tieu tai nguyen, Model khac
            if (!BagManager.getInstance().addItemToDB(
                    listSpendResource.stream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (keyOld, keyNew) -> new MoneyPackageVO(keyOld.id, keyOld.amount + keyNew.amount))).values().stream().map(obj -> new ResourcePackage(obj.id, -obj.amount)).collect(Collectors.toList()),
                    uid,
                    getParentExtension().getParentZone(),
                    UserUtils.TransactionType.LEVEL_UP_HERO)) {
                SendUpLevelUserHero objPut = new SendUpLevelUserHero(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
                send(objPut, user);
                return;
            }

            //Up cap cho hero
            if (!HeroManager.getInstance().upLevelHeroModel(uid, objGet.hashHero, objGet.level - heroModel.readLevel(), getParentExtension().getParentZone())) {
                SendUpLevelUserHero objPut = new SendUpLevelUserHero(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }

            SendUpLevelUserHero objPut = new SendUpLevelUserHero();
            objPut.hashHero = objGet.hashHero;
            objPut.level = heroModel.readLevel();
            send(objPut, user);
        }
    }


    /**
     * Hien thi do co the lap
     */
    @WithSpan
    private void doShowItemHeroCanEquip(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecShowItemHeroCanEquip objGet = new RecShowItemHeroCanEquip(data);
        if (objGet.position < 0 || objGet.position >= 8) {
            SendShowItemHeroCanEquip objPut = new SendShowItemHeroCanEquip(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, objGet.hashHero, getParentExtension().getParentZone());
        HeroVO heroCf = CharactersConfigManager.getInstance().getHeroConfig(heroModel.id);

        //List item
        List<EquipDataVO> listEquip = new ArrayList<>();
        //Item trong hero
        listEquip.addAll(HeroManager.getInstance().getAllEquipmentHero(uid, objGet.position, getParentExtension().getParentZone()).stream().
                filter(obj -> BagManager.getInstance().checkEquip(obj, EClass.fromID(heroCf.clas), EWeaponType.fromID(heroCf.weaponType))).
                filter(Objects::nonNull).
                collect(Collectors.toList()));
        //Item trong bag
        listEquip.addAll(BagManager.getInstance().getItemHeroDependOnPosition(uid, getParentExtension().getParentZone(), objGet.position, heroCf.clas, heroCf.weaponType).stream().
                filter(Objects::nonNull).
                collect(Collectors.toList()));
        //Loai bo do cua hero dg mac <- HeroVO ko equip dc do da mac tren nguoi
        EquipDataVO equip = HeroManager.getInstance().getEquipmentItemSlotVO(uid, objGet.hashHero, objGet.position, getParentExtension().getParentZone());
        if (equip != null) {
            listEquip = listEquip.stream().
                    filter(Objects::nonNull).
                    filter(obj -> !obj.hash.equals(equip.hash)).
                    collect(Collectors.toList());
        }

        SendShowItemHeroCanEquip objPut = new SendShowItemHeroCanEquip();
        objPut.listEquip = listEquip;
        objPut.uid = uid;
        objPut.zone = getParentExtension().getParentZone();

        send(objPut, user);
    }


    /**
     * Lap do vao hero
     */
    @WithSpan
    private void doEquipItemHero(User user, ISFSObject data) {
        synchronized (user){

            long uid = extension.getUserManager().getUserModel(user).userID;
            RecEquipItemHero objGet = new RecEquipItemHero(data);
            HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, objGet.hashHero, getParentExtension().getParentZone());
            if (heroModel == null) {
                SendEquipItemHero objPut = new SendEquipItemHero(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
                send(objPut, user);
                return;
            }

            //Lay trang bi lap vao HeroVO
            //Lay item tren hero khac
            EquipDataVO equipVOEquip = HeroManager.getInstance().getEquipmentHeroModel(uid, objGet.hashItem, getParentExtension().getParentZone());
            if (equipVOEquip == null) {
                //Item trong tui
                equipVOEquip = BagManager.getInstance().getHeroEquipInBag(uid, ItemGet.create(objGet.hashItem, 1), getParentExtension().getParentZone());
            }

            //Ko co trong bag vs hero)
            if (equipVOEquip == null) {
                SendEquipItemHero objPut = new SendEquipItemHero(ServerConstant.ErrorCode.ERR_NOT_EXSIST_ITEM);
                send(objPut, user);
                return;
            }

            //TH lap do cua hero cho chinh hero do
            if (heroModel.hash.equals(equipVOEquip.hashHero)) {
                SendEquipItemHero objPut = new SendEquipItemHero(ServerConstant.ErrorCode.ERR_CHARACTER_EQUIP_ITEM_THEMSELF);
                send(objPut, user);
                return;
            }

            //Lay trang bi dang mac
            EquipDataVO equipVOUnequip = HeroManager.getInstance().getEquipmentItemSlotVO(heroModel, equipVOEquip.position);
            //Kiem tra item hero khac dang mac ko (Switch neu mac), Xoa trang bi trong bag trong TH con lai
            if (equipVOEquip.hashHero != null) {
                //Trang bi muon mac dang tren nguoi hero khac
                //Update item tren nguoi hero khac
                if (equipVOUnequip != null && !HeroManager.getInstance().updateEquipmentHeroModel(uid, equipVOEquip.hashHero, equipVOUnequip, getParentExtension().getParentZone())) {
                    SendEquipItemHero objPut = new SendEquipItemHero(ServerConstant.ErrorCode.ERR_CHAR);
                    send(objPut, user);
                    return;
                }

            } else {
                //Do trong tui thi xoa do trong tui
                if (!BagManager.getInstance().deleteItemInBag(uid, equipVOEquip, getParentExtension().getParentZone())) {
                    SendEquipItemHero objPut = new SendEquipItemHero(ServerConstant.ErrorCode.ERR_CHAR);
                    send(objPut, user);
                    return;
                }

                //Neu hien tai tuong dang mac do thi cho do do vao tui
                if (equipVOUnequip != null && equipVOUnequip.id != null) {
                    if (!BagManager.getInstance().addNewWeapon(uid, getParentExtension().getParentZone(), equipVOUnequip)) {
                        SendEquipItemHero objPut = new SendEquipItemHero(ServerConstant.ErrorCode.ERR_CHAR);
                        send(objPut, user);
                        return;
                    }
                }
            }

            //Trang bi do cho hero --- truyen vao EquipData = update
            if (!HeroManager.getInstance().updateEquipmentHeroModel(uid, objGet.hashHero, equipVOEquip, getParentExtension().getParentZone())) {
                SendEquipItemHero objPut = new SendEquipItemHero(ServerConstant.ErrorCode.ERR_CHAR);
                send(objPut, user);
                return;
            }

            SendEquipItemHero objPut = new SendEquipItemHero();
            objPut.equipData = equipVOEquip;
            send(objPut, user);

        }
    }


    /**
     * Lap do nhanh
     */
    @WithSpan
    private void doEquipItemHeroQuick(User user, ISFSObject data) {
        synchronized (user){

            long uid = extension.getUserManager().getUserModel(user).userID;
            RecEquipItemHeroQuick objGet = new RecEquipItemHeroQuick(data);

            HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, objGet.hash, getParentExtension().getParentZone());
            if (heroModel == null) {
                SendEquipItemHeroQuick objPut = new SendEquipItemHeroQuick(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
                send(objPut, user);
                return;
            }

            //Kiem tra xem co equip quick dc ko (server tu chon item manh nhat)
            UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
            //(Client tu chon item manh nhat)
            List<ItemGet> listItemGet = objGet.listEquip.stream().
                    map(obj -> ItemGet.create(obj, 1)).
                    collect(Collectors.toList());
            List<EquipDataVO> listEquipData = BagManager.getInstance().getHeroEquipInBag(userBagModel, listItemGet, getParentExtension().getParentZone());
            //Kiem tra item co trung vi tri lap khong
            if(listEquipData.stream().map(obj -> obj.position).count() != listEquipData.size()){
                SendEquipItemHeroQuick objPut = new SendEquipItemHeroQuick(ServerConstant.ErrorCode.ERR_INVALID_LIST_EQUIP);
                send(objPut, user);
                return;
            }

            //Xoa item trong tui
            if(!BagManager.getInstance().deleteItemInBag(userBagModel, listEquipData, getParentExtension().getParentZone())){
                SendEquipItemHeroQuick objPut = new SendEquipItemHeroQuick(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }

            //Lay list item tren nguoi hero
            List<EquipDataVO> listEquipDataHero = listEquipData.stream().
                    map(obj -> HeroManager.getInstance().getEquipmentItemSlotVO(heroModel, obj.position)).
                    filter(obj -> obj != null && obj.id != null).
                    collect(Collectors.toList());
            if(!listEquipDataHero.isEmpty() && !BagManager.getInstance().addNewWeapon(userBagModel, getParentExtension().getParentZone(), listEquipDataHero)){
                SendEquipItemHeroQuick objPut = new SendEquipItemHeroQuick(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }

            //Mac item vao hero
            if (!HeroManager.getInstance().updateEquipmentHeroModel(uid, objGet.hash, listEquipData, getParentExtension().getParentZone())) {
                SendEquipItemHeroQuick objPut = new SendEquipItemHeroQuick(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }

            SendEquipItemHeroQuick objPut = new SendEquipItemHeroQuick();
            objPut.hashHero = objGet.hash;
            objPut.listEquip = listEquipData;
            send(objPut, user);

        }
    }


    /**
     * Thao do hero
     */
    @WithSpan
    private void doUnequipItemHero(User user, ISFSObject data) {
        synchronized (user){

            long uid = extension.getUserManager().getUserModel(user).userID;
            RecUnequipItemHero objGet = new RecUnequipItemHero(data);

            //Get item ra truoc khi delete
            EquipDataVO equipData = HeroManager.getInstance().getEquipmentHeroModel(uid, objGet.hashItem, getParentExtension().getParentZone());
            EquipDataVO equipBeforeInBag = EquipDataVO.create(equipData);
            if (equipData == null) {
                SendUnequipItemHero objPut = new SendUnequipItemHero(ServerConstant.ErrorCode.ERR_NOT_EQUIP_ITEM);
                send(objPut, user);
                return;
            }

            //Delete item tren hero
            if (!HeroManager.getInstance().deleteEquipmentHeroModel(uid, equipData.hashHero, equipData.position, getParentExtension().getParentZone())) {
                SendUnequipItemHero objPut = new SendUnequipItemHero(ServerConstant.ErrorCode.ERR_CHAR);
                send(objPut, user);
                return;
            }

            //Nhet item vao bag
            if (!BagManager.getInstance().addNewWeapon(uid, getParentExtension().getParentZone(), equipData)) {
                SendUnequipItemHero objPut = new SendUnequipItemHero(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }

            SendUnequipItemHero objPut = new SendUnequipItemHero();
            objPut.hashHero = equipBeforeInBag.hashHero;
            objPut.equipData = equipData;
            send(objPut, user);

        }
    }


    /**
     * Thao do nhanh
     */
    @WithSpan
    private void doUnequipItemHeroQuick(User user, ISFSObject data) {
        synchronized (user){

            long uid = extension.getUserManager().getUserModel(user).userID;
            RecUnequipItemHeroQuick objGet = new RecUnequipItemHeroQuick(data);

            //Lay hero ra truoc khi delete tat ca item
            HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, objGet.hash, getParentExtension().getParentZone());

            //Lay item tren hero
            List<EquipDataVO> listEquipCurrent = HeroManager.getInstance().getAllEquipmentHero(heroModel);
            if (listEquipCurrent.size() <= 0) {
                SendUnequipItemHeroQuick objPut = new SendUnequipItemHeroQuick(ServerConstant.ErrorCode.ERR_NOT_HAVE_TO_UNEQUIP_QUICK);
                send(objPut, user);
                return;
            }

            //Xoa tat ca item
            if (!HeroManager.getInstance().deleteAllEquipmentHeroModel(uid, objGet.hash, getParentExtension().getParentZone())) {
                SendUnequipItemHeroQuick objPut = new SendUnequipItemHeroQuick(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }

            //Nhet do trong hero vao bag
            if (!BagManager.getInstance().addNewWeapon(uid, getParentExtension().getParentZone(), listEquipCurrent)) {
                SendUnequipItemHeroQuick objPut = new SendUnequipItemHeroQuick(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }

            SendUnequipItemHeroQuick objPut = new SendUnequipItemHeroQuick();
            objPut.hashHero = objGet.hash;
            send(objPut, user);

        }
    }


    /**
     * Lay bonus story
     */
    @WithSpan
    private void doGetBonusStory(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecGetBonusStory objGet = new RecGetBonusStory(data);

        //Lay kc dc bonus
        int diamondBonus = HeroManager.getInstance().getBonusStoryHero(uid, objGet.idHero, getParentExtension().getParentZone());
        //Kiem tra da nhan chua
        if (diamondBonus == 0) {
            SendGetBonusStory objPut = new SendGetBonusStory(ServerConstant.ErrorCode.ERR_ALREADY_GET_BONUS_STORY);
            send(objPut, user);
        }

        //Su dung bonus (Gan bonus hero = 0)
        if (!HeroManager.getInstance().useBonusStoryHero(uid, objGet.idHero, getParentExtension().getParentZone())) {
            SendGetBonusStory objPut = new SendGetBonusStory(ServerConstant.ErrorCode.ERR_CHAR);
            send(objPut, user);
        }

        //Nhan kc cho vao tui
        List<MoneyPackageVO> moneyPackageVOList = new ArrayList<>();
        moneyPackageVOList.add(new MoneyPackageVO(MoneyType.DIAMOND, diamondBonus));
        if (!BagManager.getInstance().addItemToDB(moneyPackageVOList, uid, getParentExtension().getParentZone(), UserUtils.TransactionType.GET_BONUS_STORY)) {
            SendGetBonusStory objPut = new SendGetBonusStory(ServerConstant.ErrorCode.ERR_CHAR);
            send(objPut, user);
        }

        SendGetBonusStory objPut = new SendGetBonusStory();
        send(objPut, user);
    }

    /**
     * load scene up star hero
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneUpStarHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserAllHeroModel userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, getParentExtension().getParentZone());
        UserBlessingHeroModel userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, getParentExtension().getParentZone());
        //Get List HeroModel
        List<HeroModel> listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone()).parallelStream().
                filter(obj -> obj.star != 0).collect(Collectors.toList());

        SendLoadSceneUpStarHero objPut = new SendLoadSceneUpStarHero();
        objPut.userAllHeroModel = userAllHeroModel;
        objPut.userBlessingHeroModel = userBlessingHeroModel;
        objPut.zone = getParentExtension().getParentZone();
        objPut.listHeroModel = listHeroModel;
        send(objPut, user);
    }

    /**
     * Nang STAR HERO
     */
    @WithSpan
    private SendUpStarUserHero doUpStarUserHero(long uid, ISFSObject data) {
        RecUpStarUserHero objGet = new RecUpStarUserHero(data);
        HeroModel heroUp = HeroManager.getInstance().getHeroModel(uid, objGet.hashHeroUp, getParentExtension().getParentZone());
        List<HeroModel> listHeroFission = HeroManager.getInstance().getHeroModel(uid, objGet.hashHeroFission, getParentExtension().getParentZone());
        //Kiem tra hero ton tai khong
        if (heroUp == null || listHeroFission.isEmpty() || listHeroFission.size() != objGet.hashHeroFission.size()) {
            return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
        }

        int numNonNft = 0;
        for (HeroModel foodModel : listHeroFission) {
            if (foodModel.isBreeding) {
                return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_HERO_BREEDING);
            }

            if (foodModel.type == EHeroType.NORMAL.getId()) {
                numNonNft++;
            }
        }

        if (heroUp.isBreeding) {
            return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_HERO_BREEDING);
        }

        //Check heroup NFT
        if (heroUp.type == EHeroType.NORMAL.getId()) {
            return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_INVALID_HERO_UP_STAR);
        }
        //Hero max sao --- khong the nang saoss
        if (CharactersConfigManager.getInstance().getMaxStarHeroConfig(heroUp.id) < heroUp.star + 1) {
            return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_HERO_MAX_STAR);
        }

        //Ktra co du dieu kien formula star hero ko
        if (!HeroManager.getInstance().checkConditionUpStarHero(heroUp, listHeroFission.parallelStream().map(HeroModel::createByHeroModel).collect(Collectors.toList()))) {
            return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_INVALID_HERO_FISSION);
        }

        //Tru tien nang hero
        GraftHeroVO graftHeroCf = CharactersConfigManager.getInstance().getGraftHeroConfig(heroUp.star + 1);
        if (!BagManager.getInstance().addItemToDB(graftHeroCf.readResourceCostUpdateStarHero(), uid, getParentExtension().getParentZone(), UserUtils.TransactionType.UPGRADE_HERO)) {
            return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_MONEY);
        }

        //Xoa cac hero dung de formula star hero
        List<String> listHeroDelete = new ArrayList<>();
        if (numNonNft < listHeroFission.size()) {
            listHeroDelete.add(objGet.hashHeroUp);
        }

        listHeroDelete.addAll(objGet.hashHeroFission);
        if (!HeroManager.getInstance().deleteHeroModel(uid, listHeroDelete, getParentExtension().getParentZone())) {
            return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_SYS);
        }

        //Save in db
        long timer = 0;
        if (numNonNft < listHeroFission.size()) {
            HeroUpstarBurn heroUpstarBurn = HeroUpstarBurn.create(heroUp, listHeroFission);
            heroUpstarBurn.listRes = graftHeroCf.readResourceCostUpdateStarHero();
            long now = Utils.getTimestampInSecond();
            timer = now + graftHeroCf.time * 60L;
            heroUpstarBurn.timer = timer;
            if (!NFTManager.getInstance().upstarHeroModel(uid, heroUpstarBurn, getParentExtension().getParentZone())) {
                return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_SYS);
            }
        } else {
            if (!HeroManager.getInstance().upStarHeroModel(uid, objGet.hashHeroUp, getParentExtension().getParentZone())) {
                return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_SYS);
            }

            //Tra lai nguyen lieu da nang cap hero
            List<ResourcePackage> listResource = new ArrayList<>();
            listHeroFission.forEach(obj -> listResource.addAll(obj.readResourceResetHeroModel()));
            List<EquipDataVO> listEquipData = HeroManager.getInstance().getAllEquipmentHero(listHeroFission);

            //Add vao bag
            if (!BagManager.getInstance().addNewWeapon(uid, getParentExtension().getParentZone(), listEquipData)) {
                return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_SYS);
            }
            if(!BagManager.getInstance().addItemToDB(listResource, uid, getParentExtension().getParentZone(), UserUtils.TransactionType.UPGRADE_HERO)){
                return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_SYS);
            }
        }


        EnergyConfig energyConfig = BagManager.getInstance().getEnergyConfig();
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
        EnergyChargeInfo energyChargeInfo = BagManager.getInstance().getEnergyInfo(userBagModel, getParentExtension().getParentZone());
        List<String> heroEnergy = energyChargeInfo.heros;
        heroEnergy.removeAll(objGet.hashHeroFission);
        int increase = 0;
        for (HeroModel heroModel : HeroManager.getInstance().getHeroModel(uid, heroEnergy, getParentExtension().getParentZone())) {
            increase += energyConfig.increase.getOrDefault((int) heroModel.star, 0);
        }
        userBagModel.energy.heros = energyChargeInfo.heros;
        userBagModel.energy.increase = increase;
        if (!userBagModel.saveToDB(getParentExtension().getParentZone())) {
            return new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_SYS);
        }

        SendUpStarUserHero packet = new SendUpStarUserHero();
        packet.timer = timer;
        SendNotifyMintHero sendNotify = new SendNotifyMintHero(uid);
        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (user != null) {
            send(sendNotify, user);
        }

        // send to rabbitmq server
        ISFSObject res = new SFSObject();
        ISFSArray heroes = new SFSArray();
        res.putText(Params.FROM, String.valueOf(uid));
        for (String hash : objGet.hashHeroFission) {
            ISFSObject hero = new SFSObject();
            hero.putText("hashHero", hash);
            heroes.addSFSObject(hero);
        }

        res.putSFSArray(Params.CONTENT, heroes);
        RabbitMQHandler.getInstance().sendAscendHero(res);
        return packet;
    }

    private void doUpStarUserHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        SendUpStarUserHero packet = this.doUpStarUserHero(uid, data);
        send(packet, user);
    }

    public SFSObject doUpStarUserHero(ISFSObject data) {
        long uid = data.getLong(Params.UID);
        SendUpStarUserHero packet = this.doUpStarUserHero(uid, data);
        packet.packData();
        return (SFSObject) packet.getData();
    }
    /**
     * end Nang STAR HERO
     */


    @WithSpan
    private void doUpStarListUserHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecUpStarListUserHero objGet = new RecUpStarListUserHero(data);
        List<HeroUpModel> listUpModel = objGet.listUpHash.parallelStream().
                map(index -> HeroUpModel.create(
                        HeroManager.getInstance().getHeroModel(uid, index.hashHeroUp, getParentExtension().getParentZone()),
                        HeroManager.getInstance().getHeroModel(uid, index.hashHeroFission, getParentExtension().getParentZone()))).
                collect(Collectors.toList());

        //Kiem tra hero ton tai khong
        if (listUpModel.stream().anyMatch(index -> index.modelHeroUp == null || index.modelHeroFission.isEmpty())) {
            SendUpStartListUserHero objPut = new SendUpStartListUserHero(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            send(objPut, user);
            return;
        }

        //Hero max sao --- khong the nang saoss
        if (listUpModel.stream().anyMatch(index -> CharactersConfigManager.getInstance().getMaxStarHeroConfig(index.modelHeroUp.id) < index.modelHeroUp.star + 1)) {
            SendUpStartListUserHero objPut = new SendUpStartListUserHero(ServerConstant.ErrorCode.ERR_HERO_MAX_STAR);
            send(objPut, user);
            return;
        }

        //Ktra co du dieu kien formula star hero ko
        if (listUpModel.stream().anyMatch(index ->
                !HeroManager.getInstance().checkConditionUpStarHero(
                        index.modelHeroUp,
                        index.modelHeroFission.stream().map(HeroModel::createByHeroModel).collect(Collectors.toList())))) {
            SendUpStarUserHero objPut = new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_INVALID_HERO_FISSION);
            send(objPut, user);
            return;
        }

        //Nang star hero
        if (!HeroManager.getInstance().upStarHeroModel(uid, objGet.listUpHash.parallelStream().map(obj -> obj.hashHeroUp).collect(Collectors.toList()), getParentExtension().getParentZone())) {
            SendUpStarUserHero objPut = new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Xoa cac hero dung de formula star hero
        List<String> listHashFission = new ArrayList<>();
        objGet.listUpHash.forEach(index -> listHashFission.addAll(index.hashHeroFission));
        if (!HeroManager.getInstance().deleteHeroModel(uid, listHashFission, getParentExtension().getParentZone())) {
            SendUpStarUserHero objPut = new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Tra lai nguyen lieu da nang cap hero
        List<ResourcePackage> listResource = new ArrayList<>();
        List<EquipDataVO> listEquipData = new ArrayList<>();
        listUpModel.forEach(index -> {
            index.modelHeroFission.forEach(fission -> listResource.addAll(fission.readResourceResetHeroModel()));
            listEquipData.addAll(HeroManager.getInstance().getAllEquipmentHero(index.modelHeroFission));
        });

        //Add vao bag
        if (!BagManager.getInstance().addNewWeapon(uid, getParentExtension().getParentZone(), listEquipData)) {
            SendUpStarUserHero objPut = new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }
        if(!BagManager.getInstance().addItemToDB(
                listResource.stream().
                        collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> new ResourcePackage(oldValue.id, oldValue.amount + newValue.amount))).values().parallelStream().
                        collect(Collectors.toList()),
                uid,
                getParentExtension().getParentZone(), UserUtils.TransactionType.UPGRADE_HERO)){
            SendUpStarUserHero objPut = new SendUpStarUserHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendUpStartListUserHero objPut = new SendUpStartListUserHero();
        objPut.listResource = listResource;
        objPut.listEquipData = listEquipData;
        send(objPut, user);
    }


    /**
     * Load Scene Summon
     */
    @WithSpan
    private void doLoadSceneSummonHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserSummonHeroModel summonModel = HeroManager.SummonManager.getInstance().getUserSummonHeroModel(uid, getParentExtension().getParentZone());
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());

        SendLoadSceneSummonHero objPut = new SendLoadSceneSummonHero();
        objPut.userSummonHeroModel = summonModel;
        objPut.timeToNextDay = (int) Utils.getDeltaSecondsToEndDay();
        objPut.userBagModel = userBagModel;
        objPut.zone = getParentExtension().getParentZone();
        send(objPut, user);
    }


    /**
     * SUMMON HeroVO
     */
    @WithSpan
    private void doSummonUserHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        SendSummonUserHero sendSummonUserHero = this.handleSumHero(uid, data);
        short errorCode = sendSummonUserHero.getData().getShort(Params.ERROR_CODE);
        if (errorCode == 0) {
            this.doGetBonusSummonUserHero(user, data);
        }

        send(sendSummonUserHero, user);
    }

    /**
     * Dung de test (phai comment khi test xong)
     * @param user
     * @param objGet
     * @return
     */
    @WithSpan
    private List<HeroSummonVO> doSummonUserHeroTest(User user, RecSummonUserHero objGet){
        long uid = extension.getUserManager().getUserModel(user).userID;

        if (objGet.count != 10 || !objGet.idSummon.equals(ESummonID.BANNER_NORMAL.getId())) {
            return null;
        }

        UserSummonHeroModel userSummonHeroModel = HeroManager.SummonManager.getInstance().getUserSummonHeroModel(uid, getParentExtension().getParentZone());
        //Summon hero
        List<HeroSummonVO> listSummon = new ArrayList<>();
        if(userSummonHeroModel.countSummon + 1 == 1){

            listSummon.add(HeroSummonVO.create("T1015", 3));
            listSummon.add(HeroSummonVO.create("T1034", 3));
            listSummon.add(HeroSummonVO.create("T1057", 3));

        }else if(userSummonHeroModel.countSummon + 1 == 2){

            listSummon.add(HeroSummonVO.create("T1052", 3));
            listSummon.add(HeroSummonVO.create("T1026", 3));
            listSummon.add(HeroSummonVO.create("T1033", 3));

        }else if(userSummonHeroModel.countSummon + 1 == 3){

            listSummon.add(HeroSummonVO.create("T1053", 3));
            listSummon.add(HeroSummonVO.create("T1011", 3));
            listSummon.add(HeroSummonVO.create("T1022", 3));

        }

        listSummon.addAll(HeroManager.SummonManager.getInstance().summonUserHero(
                uid, ESummonID.GREEN_N_BLUE.getId(), ESummonType.RANDOM, null, ResourceType.SPECIAL_ITEM, 7, getParentExtension().getParentZone()));

        Collections.shuffle(listSummon);

        //Save --- tong lan summon
        userSummonHeroModel.countSummon += 1;
        userSummonHeroModel.saveToDB(getParentExtension().getParentZone());

        return listSummon;
    }

    /**
     * Lay bonus cua summon hero
     */
    @WithSpan
    private void doGetBonusSummonUserHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        //Update Model
        List<RewardVO> listReward = HeroManager.SummonManager.getInstance().getBonusSummonUserHero(uid, getParentExtension().getParentZone());
        if (listReward == null || listReward.size() <= 0) {
            return;
        }
        //Sau khi update UserSummonHeroModel thi lay ra
        UserSummonHeroModel userSummonHeroModel = HeroManager.SummonManager.getInstance().getUserSummonHeroModel(uid, getParentExtension().getParentZone());

        //Cho item vao tui
        if (!BagManager.getInstance().addItemToDB(listReward.parallelStream().map(obj -> obj.toResourcePackage()).collect(Collectors.toList()), uid, getParentExtension().getParentZone(), UserUtils.TransactionType.BONUS_SUMMON_HERO)) {
            SendGetBonusSummonHero objPut = new SendGetBonusSummonHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendGetBonusSummonHero objPut = new SendGetBonusSummonHero();
        objPut.idCurrentChesst = userSummonHeroModel.idBonus;
        objPut.point = userSummonHeroModel.bonusPoint;
        objPut.bonus = SFSArray.newFromJsonData(Utils.toJson(listReward));
        send(objPut, user);
    }


    /**
     * Summon Bonus
     */
    @WithSpan
    private void doSummonUserHeroBonus(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        SendSummonUserHero sendSummonUserHero;

        //Kiem tra co max bag hero ko
        if (HeroManager.getInstance().isMaxSizeBagListHero(uid, 1, getParentExtension().getParentZone())) {
            sendSummonUserHero = new SendSummonUserHero(ServerConstant.ErrorCode.ERR_MAX_SIZE_BAG_HERO);
            send(sendSummonUserHero, user);
            return;
        }

        //Tao ra cac hero
        RecSummonUserHeroBonus objGet = new RecSummonUserHeroBonus(data);
        List<HeroSummonVO> listSummon = HeroManager.SummonManager.getInstance().
                summonUserHero(uid, MoneyType.RANDOM_KINGDOM_LEGENDARY_HERO_CARD.getId(), null, objGet.idKingdom, ResourceType.MONEY, 1, getParentExtension().getParentZone());
        if (listSummon == null) {
            sendSummonUserHero = new SendSummonUserHero(ServerConstant.ErrorCode.ERR_INVALID_SUMMON_TYPE);
            send(sendSummonUserHero, user);
            return;
        }

        //Tieu tai nguyen
        List<TokenResourcePackage> resourceUse = HeroManager.SummonManager.getInstance().
                useResourceSummonUserHero(uid, MoneyType.RANDOM_KINGDOM_LEGENDARY_HERO_CARD.getId(), 1, getParentExtension().getParentZone());
        if (resourceUse == null) {
            sendSummonUserHero = new SendSummonUserHero(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(sendSummonUserHero, user);
            return;
        }

        SendRetireHero sendTireHero = new SendRetireHero();
//        List<HeroModel> listSummonModel = HeroManager.SummonManager.getInstance().summonUserHero(uid, MoneyType.RANDOM_KINGDOM_LEGENDARY_HERO_CARD.getId(), listSummon, getParentExtension().getParentZone(), false, sendTireHero);
        String idSummon = MoneyType.RANDOM_KINGDOM_LEGENDARY_HERO_CARD.getId();
        List<HeroModel> listSummonModel = new ArrayList<>();
        for (HeroSummonVO summon : listSummon) {
            listSummonModel.add(HeroModel.createHeroModel(uid, summon.idHero, summon.star, EHeroType.NFT));
        }

        //Them vao bag
//        if (!HeroManager.getInstance().addUserAllHeroModel(uid, listSummonModel, getParentExtension().getParentZone(), false, sendTireHero)) {
//            sendSummonUserHero = new SendSummonUserHero(ServerConstant.ErrorCode.ERR_SYS);
//            send(sendSummonUserHero, user);
//            return;
//        }

        Map<String, Object> extraData = new HashMap<>();
        extraData.put(Params.ACTION, "Summon");
        extraData.put(Params.COUNT, 1);
        extraData.put(Params.STAR, listSummon.parallelStream().map(obj -> obj.star).collect(Collectors.toList()));

        //Add hero vao hang cho
        if (!NFTManager.getInstance().mintHeroModel(uid, listSummonModel, resourceUse, Utils.toJson(extraData), getParentExtension().getParentZone())) {
            sendSummonUserHero = new SendSummonUserHero(ServerConstant.ErrorCode.ERR_SYS);
            send(sendSummonUserHero, user);
            return;
        }

        //Tang diem tich luy summon Model
        HeroManager.SummonManager.getInstance().updateBonusUserSummonHeroModel(uid, listSummon.size() * CharactersConfigManager.getInstance().getBonusPointSummonConfig(idSummon), getParentExtension().getParentZone());

        sendSummonUserHero = new SendSummonUserHero();
        sendSummonUserHero.bonusPoint = HeroManager.SummonManager.getInstance().getUserSummonHeroModel(uid, getParentExtension().getParentZone()).bonusPoint;
        sendSummonUserHero.listEquipmentRetire = sendTireHero.listEquipment;
        sendSummonUserHero.listResourceRetire = sendTireHero.listResource;
        sendSummonUserHero.listSummonedModel = listSummonModel;
        sendSummonUserHero.zone = getParentExtension().getParentZone();
        send(sendSummonUserHero, user);

        //Get bonus Summon
        doGetBonusSummonUserHero(user, data);
    }


    /**
     * Chuyen DAY User Summon Model
     */
    @WithSpan
    private void doUpdateDayUserSummonModel(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecUpdateDayUserSummonModel objGet = new RecUpdateDayUserSummonModel(data);
        //Kiem tra co du tai nguyen de chuyen khong
        if (!HeroManager.SummonManager.getInstance().
                useResourceUpdateDaySummonUserHero(uid, objGet.id, HeroResource.fromID(objGet.id), getParentExtension().getParentZone())) {
            SendUpdateDayUserSummonModel objPut = new SendUpdateDayUserSummonModel(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(objPut, user);
            return;
        }

        //Chuyen thuoc tinh cua ngay trong summon
        if (!HeroManager.SummonManager.getInstance().
                updateDayUserSummonModel(uid, objGet.id, HeroResource.fromID(objGet.id), getParentExtension().getParentZone())) {
            SendUpdateDayUserSummonModel objPut = new SendUpdateDayUserSummonModel(ServerConstant.ErrorCode.ERR_INVALID_SUMMON_TYPE);
            send(objPut, user);
            return;
        }

        SendUpdateDayUserSummonModel objPut = new SendUpdateDayUserSummonModel();
        send(objPut, user);
    }


    /**
     * Lay doi hinh
     */
    @WithSpan
    private void doLoadSceneTeamHero(User user, ISFSObject data) {
        Zone zone = getParentExtension().getParentZone();
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecLoadSceneTeamHero objGet = new RecLoadSceneTeamHero(data);
        ETeamType teamType = ETeamType.fromID(objGet.teamType);
        if (teamType == null) {
            SendLoadSceneTeamHero objPut = new SendLoadSceneTeamHero(ServerConstant.ErrorCode.ERR_INVALID_TEAM_TYPE);
            send(objPut, user);
            return;
        }

        SendLoadSceneTeamHero objPut = new SendLoadSceneTeamHero();
        objPut.teamType = teamType;
        switch (teamType){
            case CAMPAIGN:
            case TOWER:
            case MISSION_OUTPOST:
            case DARK_GATE:
                objPut.userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
                objPut.userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, zone);
                objPut.zone = zone;
                objPut.listHeroModel = objPut.userAllHeroModel.listAllHeroModel;
                objPut.sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
                objPut.team = HeroManager.getInstance().getListMainHashHero(zone, uid, teamType, false);
                break;
            case PVP_OFFLINE:
                {
                    objPut.userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
                    objPut.userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, zone);
                    objPut.zone = zone;
                    objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, zone).stream().
                            map(HeroModel::createByHeroModel).
                            collect(Collectors.toList());
                    objPut.sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
                    objPut.team = HeroManager.getInstance().getListMainHashHero(zone, uid, teamType, false);

//                    UserAllHeroModel enemyUserAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(objGet.uid, zone);
                    //trả thêm thông tin team đối thủ
//                    objPut.listHeroEnemyModel = HeroManager.getInstance().getUserAllListHeroModel(enemyUserAllHeroModel);
//                    objPut.enemyUserBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(objGet.uid, zone);
                    objPut.enemyTeam = Hero.getPlayerTeam(objGet.uid, ETeamType.PVP_OFFLINE_DEFENSE, zone, true);
                }
                break;
            case ARENA:
                {
                    objPut.userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
                    objPut.userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, zone);
                    objPut.zone = zone;
                    objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, zone).stream().
                            map(HeroModel::createByHeroModel).
                            collect(Collectors.toList());
                    objPut.sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
                    objPut.team = HeroManager.getInstance().getListMainHashHero(zone, uid, teamType, false);

                    UserAllHeroModel enemyUserAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(objGet.uid, zone);
                    //trả thêm thông tin team đối thủ
//                    objPut.listHeroEnemyModel = HeroManager.getInstance().getUserAllListHeroModel(enemyUserAllHeroModel);
//                    objPut.enemyUserBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(objGet.uid, zone);
                    objPut.enemyTeam = Hero.getPlayerTeam(objGet.uid, ETeamType.ARENA_DEFENSE, zone, true);
                }
                break;
            case ARENA_DEFENSE:
                objPut.userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
                objPut.userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, zone);
                objPut.zone = zone;
                objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, zone).stream().
                        map(HeroModel::createByHeroModel).
                        collect(Collectors.toList());
                objPut.team = HeroManager.getInstance().getListMainHashHero(zone, uid, ETeamType.ARENA_DEFENSE, false);
                break;

            case MONSTER_HUNT:
                objPut.userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
                objPut.userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, zone);
                objPut.zone = zone;
                objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, zone).stream().
                        map(HeroModel::createByHeroModel).
                        collect(Collectors.toList());
                objPut.sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
                objPut.team = HeroManager.getInstance().getListMainHashHero(zone, uid, teamType, false);
                //Monster
                objPut.monsterTeam = HuntManager.getInstance().getUserHuntModel(uid, zone).huntInfo.listEnemy;
                break;
        }

        send(objPut, user);
    }

    /**
     * Update tuong trong doi hinh
     */
    @WithSpan
    private void doUpdateTeamHero(User user, ISFSObject data) {
        RecUpdateTeamHero objGet = new RecUpdateTeamHero(data);

        try {
            HeroManager.getInstance().doUpdateTeamHero(this, user, String.valueOf(objGet.type), objGet.update);
        } catch (InvalidUpdateTeamException e) {
            e.printStackTrace();
        }
    }

    /**
     * load scene den ban phuoc
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneHeroBlessing(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        List<HeroModel> listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone());
        UserAllHeroModel userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, getParentExtension().getParentZone());
        UserBlessingHeroModel userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, getParentExtension().getParentZone());
        List<HeroModel> levelHighest = HeroManager.getInstance().getTeamLeveHighestUserHeroModel(userAllHeroModel, getParentExtension().getParentZone());
        List<HeroSlotBlessing> listBlessing = HeroManager.BlessingManager.getInstance().getListHeroSlotBlessing(userBlessingHeroModel, getParentExtension().getParentZone());

        SendLoadSceneHeroBlessing objPut = new SendLoadSceneHeroBlessing();
        objPut.listHeroModel = listHeroModel;
        objPut.listLevelHighest = levelHighest;
        objPut.listBlessing = listBlessing;
        objPut.userBlessingHeroModel = userBlessingHeroModel;
        objPut.userAllHeroModel = userAllHeroModel;
        send(objPut, user);
    }

    /**
     * update he ro ban phuoc
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doUpdateHeroBlessing(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecUpdateHeroBlessing objGet = new RecUpdateHeroBlessing(data);
        HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, objGet.hash, getParentExtension().getParentZone());
        if (heroModel == null) {
            SendUpdateHeroBlessing objPut = new SendUpdateHeroBlessing(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            send(objPut, user);
            return;
        }

        Set<String> listHeroLevelHighest = HeroManager.getInstance().getTeamHeroLevelHighest(uid, getParentExtension().getParentZone());
        if(listHeroLevelHighest.contains(objGet.hash)){
            SendUpdateHeroBlessing objPut = new SendUpdateHeroBlessing(ServerConstant.ErrorCode.ERR_INVALID_HERO_BLESSING);
            send(objPut, user);
            return;
        }

        if (!HeroManager.BlessingManager.getInstance().addHeroBlessing(uid, heroModel.hash, heroModel.id, heroModel.star, heroModel.readLevel(), objGet.position, getParentExtension().getParentZone())) {
            SendUpdateHeroBlessing objPut = new SendUpdateHeroBlessing(ServerConstant.ErrorCode.ERR_INVALID_HERO_BLESSING);
            send(objPut, user);
            return;
        }

        //Level cua hero ban phuoc
        int level = HeroManager.BlessingManager.getInstance().getLevelBlessingHero(uid, heroModel.id, getParentExtension().getParentZone());

        SendUpdateHeroBlessing objPut = new SendUpdateHeroBlessing();
        objPut.hashHero = heroModel.hash;
        objPut.star = heroModel.star;
        objPut.level = level;
        send(objPut, user);

        //Event
        GameEventAPI.ariseGameEvent(EGameEvent.BLESSING_HERO, uid, new HashMap<>(), getParentExtension().getParentZone());
    }

    /**
     * Bo 1 hero khoi den ban phuoc
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doRemoveHeroBlessing(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecRemoveHeroBlessing objGet = new RecRemoveHeroBlessing(data);
        if (!HeroManager.BlessingManager.getInstance().removeHeroBlessing(uid, objGet.hashHero, getParentExtension().getParentZone())) {
            SendRemoveHeroBlessing objPut = new SendRemoveHeroBlessing(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            send(objPut, user);
            return;
        }

        HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, objGet.hashHero, getParentExtension().getParentZone());
        SendRemoveHeroBlessing objPut = new SendRemoveHeroBlessing();
        objPut.hashHero = heroModel.hash;
        objPut.level = heroModel.readLevel();
        send(objPut, user);
    }


    /**
     * giam thoi gian khoa o trong ban phuoc
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doReduceCountdownBlessing(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecReduceCountdownBlessing objGet = new RecReduceCountdownBlessing(data);
        UserBlessingHeroModel userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, getParentExtension().getParentZone());
        //Kiem tra slot ban phuoc
        if (HeroManager.BlessingManager.getInstance().getHeroSlotBlessing(userBlessingHeroModel, objGet.position, getParentExtension().getParentZone()) == null) {
            SendReduceCountdownBlessing objPut = new SendReduceCountdownBlessing(ServerConstant.ErrorCode.ERR_INVALID_SLOT_BLESSING);
            send(objPut, user);
            return;
        }

        //Lay tai nguyen de giam thoi gian ban phuoc
        List<ResourcePackage> cost = HeroManager.BlessingManager.getInstance().getCostReduceCountdownBlessing(userBlessingHeroModel, objGet.position, getParentExtension().getParentZone());
        if (cost.isEmpty()) {
            SendReduceCountdownBlessing objPut = new SendReduceCountdownBlessing(ServerConstant.ErrorCode.ERR_DONT_HAVE_COUNTDOWN_BLESSING);
            send(objPut, user);
            return;
        }

        //Tieu tai nguyen
        if (!BagManager.getInstance().addItemToDB(cost, uid, getParentExtension().getParentZone(), UserUtils.TransactionType.COUNT_DOWN_BLESSING)) {
            SendReduceCountdownBlessing objPut = new SendReduceCountdownBlessing(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(objPut, user);
            return;
        }

        //Xoa thoi gian countdown ban phuoc
        if (!HeroManager.BlessingManager.getInstance().removeSlotHeroBlessing(userBlessingHeroModel, objGet.position, getParentExtension().getParentZone())) {
            SendReduceCountdownBlessing objPut = new SendReduceCountdownBlessing(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendReduceCountdownBlessing objPut = new SendReduceCountdownBlessing();
        objPut.position = (short) objGet.position;
        send(objPut, user);
    }


    /**
     * Mo rong gioi han ban phuoc
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doOpenSlotBlessing(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecOpenSlotBlessing objGet = new RecOpenSlotBlessing(data);
        MoneyType moneyType = MoneyType.fromID(objGet.idMoney);
        if (moneyType == null) {
            SendOpenSlotBlessing objPut = new SendOpenSlotBlessing(ServerConstant.ErrorCode.ERR_INVALID_MONEY_TYPE);
            send(objPut, user);
            return;
        }

        UserBlessingHeroModel userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, getParentExtension().getParentZone());
        //Kiem tra xem co mo rong dc khong
        List<ResourcePackage> costUnlock = CharactersConfigManager.getInstance().getUnlockBlessingConfig().
                readCostUnlockBlessingConfig(
                        moneyType,
                        HeroManager.BlessingManager.getInstance().getCountUnlockSlotBlessing(userBlessingHeroModel, moneyType));
        if (costUnlock.isEmpty()) {
            SendOpenSlotBlessing objPut = new SendOpenSlotBlessing(ServerConstant.ErrorCode.ERR_MAX_BUY_SLOT_BLESSING);
            send(objPut, user);
            return;
        }

        if (!BagManager.getInstance().addItemToDB(costUnlock, uid, getParentExtension().getParentZone(), UserUtils.TransactionType.OPEN_SLOT_BLESSING)) {
            SendOpenSlotBlessing objPut = new SendOpenSlotBlessing(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(objPut, user);
            return;
        }

        if (!HeroManager.BlessingManager.getInstance().unlockSlotHeroBlessing(userBlessingHeroModel, moneyType, getParentExtension().getParentZone())) {
            SendOpenSlotBlessing objPut = new SendOpenSlotBlessing(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendOpenSlotBlessing objPut = new SendOpenSlotBlessing();
        objPut.userBlessingHeroModel = userBlessingHeroModel;
        send(objPut, user);
    }


    /**
     * Load scene reset hero -> list hero co the reset
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneResetHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        //Get List HeroModel
        List<HeroModel> listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone()).parallelStream().
                filter(obj -> obj.readLevel() > 1).
                collect(Collectors.toList());

        SendLoadSceneResetHero objPut = new SendLoadSceneResetHero();
        objPut.listHeroModel = listHeroModel;
        send(objPut, user);
    }

    /**
     * Reset hero
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doResetHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecResetHero objGet = new RecResetHero(data);
        UserAllHeroModel userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, getParentExtension().getParentZone());
        HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, objGet.hashHero, getParentExtension().getParentZone());
        if (heroModel == null) {
            SendResetHero objPut = new SendResetHero(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            send(objPut, user);
            return;
        }

        if (heroModel.readLevel() <= 1) {
            SendResetHero objPut = new SendResetHero(ServerConstant.ErrorCode.ERR_INVALID_HERO_RESET);
            send(objPut, user);
            return;
        }

        if(HeroManager.getInstance().isFirstResetHero(userAllHeroModel)){
            //Mien phi --- change status
            HeroManager.getInstance().firstResetHero(userAllHeroModel, getParentExtension().getParentZone());
        }else {
            //Tieu tien de reset hero
            if (!BagManager.getInstance().addItemToDB(
                    CharactersConfigManager.getInstance().getCostResetConfig().parallelStream().
                            map(obj -> new MoneyPackageVO(obj.id, -obj.amount)).
                            collect(Collectors.toList()),
                    uid,
                    getParentExtension().getParentZone(),
                    UserUtils.TransactionType.RESET_HERO)) {
                SendResetHero objPut = new SendResetHero(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
                send(objPut, user);
                return;
            }
        }


        //Add item vao bag
        //Add equipment vao bag
        List<EquipDataVO> listHeroEquip = heroModel.readEquipmentHeroModel();
        if (!BagManager.getInstance().addNewWeapon(uid, getParentExtension().getParentZone(), listHeroEquip)) {
            SendResetHero objPut = new SendResetHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }
        if(!BagManager.getInstance().addItemToDB(heroModel.readResourceResetHeroModel(), uid, getParentExtension().getParentZone(), UserUtils.TransactionType.RESET_HERO)){
            SendResetHero objPut = new SendResetHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Reset hero
        if (!HeroManager.getInstance().resetHeroModel(uid, heroModel.hash, getParentExtension().getParentZone())) {
            SendResetHero objPut = new SendResetHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendResetHero objPut = new SendResetHero();
        objPut.listEquipRemove = listHeroEquip;
        send(objPut, user);
    }

    /**
     * Load scene retire hero -> list hero co the retire
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneRetireHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        //Get List HeroModel
        List<HeroModel> listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone()).parallelStream().
                filter(obj -> CharactersConfigManager.getInstance().canRetireHero(obj.star)).
                collect(Collectors.toList());

        SendLoadSceneRetireHero objPut = new SendLoadSceneRetireHero();
        objPut.count = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone()).size();
        objPut.auto = HeroManager.getInstance().getStatusAutoRetireHero(uid, getParentExtension().getParentZone());
        objPut.listHeroModel = listHeroModel;
        send(objPut, user);
    }


    /**
     * Retire hero
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doRetireHero(User user, ISFSObject data) {
        RecRetireHero objGet = new RecRetireHero(data);

        //Phan giai hero
        doRetireHero(Long.parseLong(user.getName()), objGet.listHashHero, true);
    }

    @WithSpan
    public SendRetireHero doRetireHero(long uid, List<String> listHashHero, boolean sendRetireToPlayer) {
        SendRetireHero sendTireHero = null;
        User user = ExtensionUtility.getInstance().getUserById(uid);

        List<HeroModel> listHeroModel = HeroManager.getInstance().getHeroModel(uid, listHashHero, getParentExtension().getParentZone());
        //Lay ra nhung hero co the phan giai
        List<HeroModel> listRetire = new ArrayList<>();
        for (HeroModel heroModel : listHeroModel) {
            //Hero co the phan giai
            if (CharactersConfigManager.getInstance().canRetireHero(heroModel.star)) {
                listRetire.add(heroModel);
            }
        }

        //Add item vao bag
        //Add equipment vao bag
        List<EquipDataVO> listEquipment = new ArrayList<>();
        List<ResourcePackage> listResource = new ArrayList<>();
        for (HeroModel heroModel : listRetire) {
            listEquipment.addAll(heroModel.readEquipmentHeroModel());

            listResource.addAll(heroModel.readResourceResetHeroModel());
            listResource.addAll(CharactersConfigManager.getInstance().getResourceRetireHeroConfig(heroModel.star));
        }
        listResource = new ArrayList<>(listResource.stream().
                collect(Collectors.toMap(obj -> obj.id, Function.identity(), (keyOld, keyNew) -> new ResourcePackage(keyOld.id, keyOld.amount + keyNew.amount))).values());

        //Add Equip
        if (!BagManager.getInstance().addNewWeapon(uid, getParentExtension().getParentZone(), listEquipment)) {
            if (sendRetireToPlayer) {
                sendTireHero = new SendRetireHero(ServerConstant.ErrorCode.ERR_SYS);
                send(sendTireHero, user);
                return sendTireHero;
            }
        }
        //Add Resource
        if(!BagManager.getInstance().addItemToDB(listResource, uid, getParentExtension().getParentZone(), UserUtils.TransactionType.RETIRE_HERO)){
            if (sendRetireToPlayer) {
                sendTireHero = new SendRetireHero(ServerConstant.ErrorCode.ERR_SYS);
                send(sendTireHero, user);
                return sendTireHero;
            }
        }

        //Delete hero
        if (!HeroManager.getInstance().deleteHeroModel(uid, listRetire.parallelStream().map(obj -> obj.hash).collect(Collectors.toList()), getParentExtension().getParentZone())) {
            if (sendRetireToPlayer) {
                sendTireHero = new SendRetireHero(ServerConstant.ErrorCode.ERR_SYS);
                send(sendTireHero, user);
                return sendTireHero;
            }

        }

        sendTireHero = new SendRetireHero();
        sendTireHero.listResource = listResource;
        sendTireHero.listEquipment = listEquipment;

        if (sendRetireToPlayer) {
            send(sendTireHero, user);
        }
        return sendTireHero;
    }

    /**
     * doi trang thay tu dong phan giai tuong
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doSwitchAutoRetireHero(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        if (!HeroManager.getInstance().switchStatusAutoRetireHero(uid, getParentExtension().getParentZone())) {
            SendSwitchAutoRetireHero objPut = new SendSwitchAutoRetireHero(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendSwitchAutoRetireHero objPut = new SendSwitchAutoRetireHero();
        send(objPut, user);
    }

    @WithSpan
    private void doGetHeroFriendBorrow(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecGetHeroFriendBorrow objGet = new RecGetHeroFriendBorrow(data);

        //Kiem tra co the muon dc khong
        if (!FriendHeroManager.getInstance().canAssignHeroFriend(uid, ETeamType.fromID(objGet.id), getParentExtension().getParentZone())) {
            SendGetHeroFriendBorrow objPut = new SendGetHeroFriendBorrow(ServerConstant.ErrorCode.ERR_LIMIT_BORROW_HERO_FRIEND);
            send(objPut, user);
            return;
        }

        //Neu muon dc tra list danh sach tuo
        SendGetHeroFriendBorrow objPut = new SendGetHeroFriendBorrow();
        objPut.listHeroModel = FriendHeroManager.getInstance().getListFriendHero(uid, getParentExtension().getParentZone()).stream().
                map(obj -> obj.heroModel).
                collect(Collectors.toList());
        objPut.zone = getParentExtension().getParentZone();
        send(objPut, user);
    }

    @WithSpan
    private void doGetIdleHeroData(User user, ISFSObject data) {
        Zone zone = getParentExtension().getParentZone();
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecGetIdleHeroData objGet = new RecGetIdleHeroData(data);
        ETeamType teamType = ETeamType.fromID(objGet.teamType);
        if (teamType == null) {
            SendGetIdleHeroData objPut = new SendGetIdleHeroData(ServerConstant.ErrorCode.ERR_INVALID_TEAM_TYPE);
            send(objPut, user);
            return;
        }

        SendGetIdleHeroData objPut = new SendGetIdleHeroData();
        objPut.teamType = teamType;
        switch (teamType){
            case CAMPAIGN:
            case TOWER:
            case MISSION_OUTPOST:
            case DARK_GATE:
                objPut.userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
                objPut.userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, zone);
                objPut.zone = zone;
                objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, zone).stream().
                        map(HeroModel::createByHeroModel).
                        collect(Collectors.toList());
                objPut.sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
                objPut.team = HeroManager.getInstance().getListMainHashHero(zone, uid, teamType, false);
                break;
            case PVP_OFFLINE:
            {
                objPut.userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
                objPut.userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, zone);
                objPut.zone = zone;
                objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, zone).stream().
                        map(HeroModel::createByHeroModel).
                        collect(Collectors.toList());
                objPut.sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
                objPut.team = HeroManager.getInstance().getListMainHashHero(zone, uid, teamType, false);

                UserAllHeroModel enemyUserAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(objGet.uid, zone);
                //trả thêm thông tin team đối thủ
//                    objPut.listHeroEnemyModel = HeroManager.getInstance().getUserAllListHeroModel(enemyUserAllHeroModel);
//                    objPut.enemyUserBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(objGet.uid, zone);
                objPut.enemyTeam = Hero.getPlayerTeam(objGet.uid, ETeamType.PVP_OFFLINE, zone, true);
            }
            break;
            case ARENA:
            {
                objPut.userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
                objPut.userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, zone);
                objPut.zone = zone;
                objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, zone).stream().
                        map(HeroModel::createByHeroModel).
                        collect(Collectors.toList());
                objPut.sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
                objPut.team = HeroManager.getInstance().getListMainHashHero(zone, uid, teamType, false);

                UserAllHeroModel enemyUserAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(objGet.uid, zone);
                //trả thêm thông tin team đối thủ
//                    objPut.listHeroEnemyModel = HeroManager.getInstance().getUserAllListHeroModel(enemyUserAllHeroModel);
//                    objPut.enemyUserBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(objGet.uid, zone);
                objPut.enemyTeam = Hero.getPlayerTeam(objGet.uid, ETeamType.ARENA_DEFENSE, zone, true);
            }
            break;
            case ARENA_DEFENSE:
                objPut.userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
                objPut.userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, zone);
                objPut.zone = zone;
                objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, zone).stream().
                        map(HeroModel::createByHeroModel).
                        collect(Collectors.toList());
                objPut.team = HeroManager.getInstance().getListMainHashHero(zone, uid, ETeamType.ARENA_DEFENSE, false);
                break;

            case MONSTER_HUNT:
                objPut.userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
                objPut.userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, zone);
                objPut.zone = zone;
                objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, zone).stream().
                        map(HeroModel::createByHeroModel).
                        collect(Collectors.toList());
                objPut.sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
                objPut.team = HeroManager.getInstance().getListMainHashHero(zone, uid, teamType, false);
                //Monster
                objPut.monsterTeam = HuntManager.getInstance().getUserHuntModel(uid, zone).huntInfo.listEnemy;
                break;
        }

        send(objPut, user);
    }



    /**
     * SUMMON HeroVO
     */
    @WithSpan
    private SendSummonUserHero handleSumHero(long uid, ISFSObject data) {
        RecSummonUserHero objGet = new RecSummonUserHero(data);
        if (objGet.count <= 0) {
            return new SendSummonUserHero(ServerConstant.ErrorCode.ERR_CHAR);
        }

        //Kiem tra id co ton tai ko
        if (CharactersConfigManager.getInstance().getSummonConfig(objGet.idSummon) == null) {
            return new SendSummonUserHero(ServerConstant.ErrorCode.ERR_NOT_EXSIST_SUMMON_BANNER);
        }

        //Kiem tra co max bag hero ko
        if (HeroManager.getInstance().isMaxSizeBagListHero(uid, objGet.count, getParentExtension().getParentZone())) {
            return new SendSummonUserHero(ServerConstant.ErrorCode.ERR_MAX_SIZE_BAG_HERO);
        }

        List<HeroSummonVO> listSummoned = HeroManager.SummonManager.getInstance().summonUserHero(uid, objGet.idSummon, null, null, ResourceType.MONEY, objGet.count, getParentExtension().getParentZone());
        if (listSummoned == null || listSummoned.size() <= 0) {
            return new SendSummonUserHero(ServerConstant.ErrorCode.ERR_SYS);
        }

        //Tieu tai nguyen
        List<TokenResourcePackage> resourceUse = HeroManager.SummonManager.getInstance().
                useResourceSummonUserHero(uid, objGet.idSummon, objGet.count, getParentExtension().getParentZone());
        if (resourceUse.size() == 0) {
            return new SendSummonUserHero(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
        }

        //Them vao bag
        SendRetireHero sendTireHero = new SendRetireHero();
//        List<HeroModel> listSummonedModel = HeroManager.SummonManager.getInstance().summonUserHero(uid, objGet.idSummon, listSummoned, getParentExtension().getParentZone(), false, sendTireHero);
        List<HeroModel> listSummonedModel = new ArrayList<>();
        for (HeroSummonVO summon : listSummoned) {
            listSummonedModel.add(HeroModel.createHeroModel(uid, summon.idHero, summon.star, EHeroType.NFT));
        }

        //Add hero vao hang cho
        if (!NFTManager.getInstance().mintHeroModel(uid, listSummonedModel, resourceUse, objGet.idSummon, getParentExtension().getParentZone())) {
            return new SendSummonUserHero(ServerConstant.ErrorCode.ERR_SYS);
        }

        SendSummonUserHero sendSummonUserHero = new SendSummonUserHero();
        sendSummonUserHero.bonusPoint = HeroManager.SummonManager.getInstance().getUserSummonHeroModel(uid, getParentExtension().getParentZone()).bonusPoint;
        sendSummonUserHero.listSummonedModel = listSummonedModel;
        sendSummonUserHero.listEquipmentRetire = sendTireHero.listEquipment;
        sendSummonUserHero.listResourceRetire = sendTireHero.listResource;
        sendSummonUserHero.zone = getParentExtension().getParentZone();

        //Event
        Map<String, Object> dataEvent = new HashMap<>();
        dataEvent.put(Params.COUNT, objGet.count);
        dataEvent.put(Params.STAR, listSummoned.parallelStream().map(obj -> obj.star).collect(Collectors.toList()));
        GameEventAPI.ariseGameEvent(EGameEvent.SUMMON_TAVERN, uid, dataEvent, getParentExtension().getParentZone());
        return sendSummonUserHero;
        //Get bonus Summon (bổ sung sau)
//        doGetBonusSummonUserHero(user, data);
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     *
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject LockHero(ISFSObject rec) {
        long uid = rec.getLong(Params.UID);
        String hash = rec.getUtfString(Params.HASH);

        ISFSObject objPut = new SFSObject();
        HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, hash, getParentExtension().getParentZone());
        if(heroModel == null) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            objPut.putUtfString(Params.MESS, "Hero not exsist.");
            return objPut;
        }

        //check hero use to energy
//        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
//        if (userBagModel.energy.heros.stream().anyMatch(heroHash -> Objects.equals(heroHash, hash))) {
//            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
//            objPut.putUtfString(Params.MESS, "Hero is charging energy.");
//            return objPut;
//        }
        EnergyConfig energyConfig = BagManager.getInstance().getEnergyConfig();
        UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentExtension().getParentZone());
        EnergyChargeInfo energyChargeInfo = BagManager.getInstance().getEnergyInfo(userBagModel, getParentExtension().getParentZone());
        List<String> heroEnergy = energyChargeInfo.heros;
        heroEnergy.removeAll(Collections.singletonList(hash));
        int increase = 0;
        for (HeroModel heroModelBag : HeroManager.getInstance().getHeroModel(uid, heroEnergy, getParentExtension().getParentZone())) {
            increase += energyConfig.increase.getOrDefault((int) heroModelBag.star, 0);
        }
        userBagModel.energy.heros = energyChargeInfo.heros;
        userBagModel.energy.increase = increase;
        if (!userBagModel.saveToDB(getParentExtension().getParentZone())) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            objPut.putUtfString(Params.MESS, "Error system.");
            return objPut;
        }

        if (heroModel.isBreeding) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_HERO_BREEDING);
            objPut.putUtfString(Params.MESS, "Hero breeding.");
            return objPut;
        }

        if (!HeroManager.getInstance().addBlockHeroMode(uid, heroModel, getParentExtension().getParentZone())) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            objPut.putUtfString(Params.MESS, "Error system.");
            return objPut;
        }

        if (!HeroManager.getInstance().deleteHeroModel(uid, Collections.singletonList(heroModel.hash), getParentExtension().getParentZone())) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            objPut.putUtfString(Params.MESS, "Error system.");
            return objPut;
        }

        objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        objPut.putUtfString(Params.MESS, "");
        SendNotifyMintHero packet = new SendNotifyMintHero(uid);
        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (user != null) {
            send(packet, user);
        }

        return objPut;
    }

    /**
     *
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject UnlockHero(ISFSObject rec) {
        long uid = rec.getLong(Params.UID);
        String hash = rec.getUtfString(Params.HASH);

        ISFSObject objPut = new SFSObject();
        HeroModel heroModel = HeroManager.getInstance().removeBlockHeroMode(uid, hash, getParentExtension().getParentZone());
        if(heroModel == null) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            objPut.putUtfString(Params.MESS, "Hero not exsist.");
            return objPut;
        }

        if (!HeroManager.getInstance().addUserAllHeroModel(uid, Collections.singletonList(heroModel), getParentExtension().getParentZone(), false, null)) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            objPut.putUtfString(Params.MESS, "Error system.");
            return objPut;
        }

        objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        objPut.putUtfString(Params.MESS, "");

        SendNotifyMintHero packet = new SendNotifyMintHero(uid);
        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (user != null) {
            send(packet, user);
        }
        return objPut;
    }

    public ISFSObject removeHeroUpStar(ISFSObject rec) {
        long uid = rec.getLong(Params.UID);
        String hash = rec.getText(Params.HASH);
        Zone zone = getParentExtension().getParentZone();
        UserBurnHeroModel userBurnHeroModel = UserBurnHeroModel.copyFromDBtoObject(uid, zone);
        ISFSObject obj = new SFSObject();
        obj.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        if (userBurnHeroModel.mapUpstar.get(hash) == null) {
            return obj;
        }

        HeroModel heroModel = userBurnHeroModel.mapUpstar.get(hash).heroModel;
        if (!HeroManager.getInstance().addUserAllHeroModel(uid, Collections.singletonList(heroModel), zone, false, null)) {
            obj.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            return obj;
        }

        obj.putText(Params.HASH, hash);
        return obj;
    }

    @WithSpan
    public ISFSObject handleHttpSumHero(ISFSObject rec) {
        long uid = rec.getLong(Params.UID);
        SendSummonUserHero send = this.handleSumHero(uid, rec);

        ISFSArray list = new SFSArray();
        ISFSObject heroObj;
        CharactersConfigManager charactersConfigManager = CharactersConfigManager.getInstance();
        for(HeroModel hero : send.listSummonedModel){
            heroObj = new SFSObject();
            heroObj.putUtfString(Params.HASH, hero.hash);
            heroObj.putUtfString(Params.ModuleChracter.ID, hero.id);
            heroObj.putShort(Params.ModuleHero.STAR, hero.star);

            switch (EHeroType.fromId(hero.type)) {
                case NORMAL:
                    heroObj.putSFSObject(Params.STATS, SFSObject.newFromJsonData(Utils.toJson(charactersConfigManager.getHeroConfig(hero.id))));
                    heroObj.putSFSObject(Params.GROW, SFSObject.newFromJsonData(Utils.toJson(charactersConfigManager.getHeroStatsGrowConfig(hero.id))));
                    break;
                case NFT:
                    HeroBaseStatsModel heroBaseStatsModel = HeroBaseStatsModel.copyFromDBtoObject(hero.hash, hero.id, send.zone);
                    heroObj.putSFSObject(Params.STATS, SFSObject.newFromJsonData(Utils.toJson(heroBaseStatsModel.baseStats)));
                    heroObj.putSFSObject(Params.GROW, SFSObject.newFromJsonData(Utils.toJson(heroBaseStatsModel.growStats)));
                    break;
            }

            list.addSFSObject(heroObj);
        }
        ISFSObject obj = new SFSObject();
        obj.putSFSArray("hr", list);
        return obj;
    }

    @WithSpan
    public SFSObject getHeroAscendStats(ISFSObject data) {
        Zone zone = getParentExtension().getParentZone();
        long uid = data.getLong(Params.UID);
        String hash = data.getText(Params.HASH);
        HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, hash, zone);
        SFSObject res = new SFSObject();
        if (heroModel == null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_INVALID_HERO_UP_STAR);
            return res;
        }

        Stats stats = HeroManager.getInstance().getStatsNormalHeroModel(heroModel, heroModel.star + 1);
        ISFSObject result = SFSObject.newFromJsonData(Utils.toJson(stats));
        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        res.putSFSObject(Params.DATA, result);
        return res;
    }

    @WithSpan
    public ISFSObject checkHeroForSale(ISFSObject data) {
        Zone zone = getParentExtension().getParentZone();
        long uid = data.getLong(Params.UID);
        String hash = data.getText(Params.HASH);
        ISFSObject res = new SFSObject();
        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);

        HeroModel blockedHeroModel = HeroManager.getInstance().getBlockedHeroModel(uid, hash, zone);
        if (blockedHeroModel != null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_HERO_ON_SALE);
            return res;
        }

        UserBurnHeroModel userBurnHeroModel = UserBurnHeroModel.copyFromDBtoObject(uid, zone);
        for (Map.Entry<String, HeroUpstarBurn> entry : userBurnHeroModel.mapUpstar.entrySet()) {
            if(hash.equalsIgnoreCase(entry.getKey())) {
                res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_COUNTDOWN);
                return res;
            }
        }

        HeroModel heroModel = HeroManager.getInstance().getHeroModel(uid, hash, zone);
        if (heroModel == null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHARACTER);
            return res;
        }

        if (heroModel.isBreeding) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_HERO_BREEDING);
            return res;
        }

        return res;
    }

    /**
     *
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject DeleteHero(ISFSObject rec) {
        long uid = rec.getLong(Params.UID);
        String hash = rec.getUtfString(Params.HASH);

        ISFSObject objPut = new SFSObject();
        if (!HeroManager.getInstance().deleteHeroModel(uid, Collections.singletonList(hash), getParentExtension().getParentZone())) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            objPut.putUtfString(Params.MESS, "Error system.");
            return objPut;
        }

        objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        objPut.putUtfString(Params.MESS, "");
        return objPut;
    }

    /**
     *
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject DeleteHeroBlock(ISFSObject rec) {
        long uid = rec.getLong(Params.UID);
        String hash = rec.getUtfString(Params.HASH);

        ISFSObject objPut = new SFSObject();
        UserBlockHeroModel userBlockHeroModel = UserBlockHeroModel.copyFromDBtoObject(uid, getParentExtension().getParentZone());
        userBlockHeroModel.listHeroModel = userBlockHeroModel.listHeroModel.stream()
                .filter(heroModel -> Objects.equals(heroModel.hash, hash))
                .collect(Collectors.toList());
        if (!userBlockHeroModel.saveToDB(getParentExtension().getParentZone())) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            objPut.putUtfString(Params.MESS, "Error system.");
            return objPut;
        }

        objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        objPut.putUtfString(Params.MESS, "");
        return objPut;
    }

    @WithSpan
    public ISFSObject moveHero(ISFSObject rec) {
        long from = rec.getLong(Params.UID);
        long to = rec.getLong(Params.TO);
        String hash = rec.getUtfString(Params.HASH);
        Zone zone = getParentExtension().getParentZone();
        HeroModel heroRemove = null;
        UserAllHeroModel allHeroModel = UserAllHeroModel.copyFromDBtoObject(from, zone);
        for (HeroModel heroModel : allHeroModel.listAllHeroModel) {
            if (heroModel.hash.equalsIgnoreCase(hash)) {
                heroRemove = heroModel;
                break;
            }
        }

        ISFSObject objPut = new SFSObject();
        objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        if (heroRemove == null) {
            objPut.putText(Params.MESS, "Hero not found");
            return objPut;
        }

        if (!HeroManager.getInstance().deleteHeroModel(from, Collections.singletonList(hash), zone)) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            objPut.putUtfString(Params.MESS, "Delete hero error.");
            return objPut;
        }

        if (!HeroManager.getInstance().addUserAllHeroModel(to, Collections.singletonList(heroRemove), zone, false, null)) {
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
            objPut.putUtfString(Params.MESS, "Add hero model error.");
            return objPut;
        }

        return objPut;
    }
}
