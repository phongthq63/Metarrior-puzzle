package com.bamisu.log.gameserver.module.mage;

import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.gamelib.item.entities.SageEquipDataVO;
import com.bamisu.log.gameserver.datamodel.mage.UserMageModel;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.mage.cmd.rec.*;
import com.bamisu.log.gameserver.module.mage.cmd.send.*;
import com.bamisu.log.gameserver.module.skill.exception.SkillNotFoundException;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.ArrayList;
import java.util.List;

public class MageHandler extends ExtensionBaseClientRequestHandler {

    public MageHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_MAGE;
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_LOAD_SCENE_MAGE:
                doLoadSceneMage(user, data);
                break;
            case CMD.CMD_EQUIP_STONE_MAGE:
                doEquipStoneMage(user, data);
                break;
            case CMD.CMD_GET_BAG_MAGE_EQUIPMENT:
                doGetBagEquipmentMage(user, data);
                break;
            case CMD.CMD_EQUIP_MAGE_ITEM:
                doEquipMageItem(user, data);
                break;
            case CMD.CMD_UNEQUIP_MAGE_ITEM:
                doUnequipMageItem(user, data);
                break;
            case CMD.CMD_GET_USER_MAGE_SKIN:
                doGetUserMageSkin(user, data);
                break;
            case CMD.CMD_EQUIP_MAGE_SKIN:
                doEquipMageSkin(user, data);
                break;
            case CMD.CMD_GET_SKILL_TREE:
                doGetSkillTree(user, data);
                break;
            case CMD.CMD_STUDY_SKILL:
                doStudySkill(user, data);
                break;
            case CMD.CMD_STUDY_SKILL_MAX:
                doStudySkillMax(user, data);
                break;
            case CMD.CMD_RESET_ALL_SKILL:
                doResetAll(user, data);
                break;
            case CMD.CMD_RESET_LAST_COLUM_SKILL:
                doResetLastColum(user, data);
                break;
        }
    }

    @WithSpan
    private void doStudySkillMax(User user, ISFSObject data) {
        RecStudySkill recStudySkill = new RecStudySkill(data);
        recStudySkill.unpackData();
        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);
        try {
            MageManager.getInstance().studySkill(getParentExtension().getParentZone(), userModel.userID, recStudySkill.id, true);
        } catch (SkillNotFoundException e) {
            e.printStackTrace();
        }
    }

    @WithSpan
    private void doResetLastColum(User user, ISFSObject data) {
        MageManager.getInstance().resetLastColumSkill(this, user);
    }

    @WithSpan
    private void doResetAll(User user, ISFSObject data) {
        MageManager.getInstance().resetAllSkill(this, user);
    }

    @WithSpan
    private void doStudySkill(User user, ISFSObject data) {
        RecStudySkill recStudySkill = new RecStudySkill(data);
        recStudySkill.unpackData();
        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);
        try {
            MageManager.getInstance().studySkill(getParentExtension().getParentZone(), userModel.userID, recStudySkill.id, false);
        } catch (SkillNotFoundException e) {
            e.printStackTrace();
        }
    }

    @WithSpan
    private void doGetSkillTree(User user, ISFSObject data) {
        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);
        SendSkillTree sendSkillTree = MageManager.getInstance().getSkillTree(getParentExtension().getParentZone(), userModel.userID);
        send(sendSkillTree, user);
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_MAGE, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_MAGE, this);
    }



    /*-----------------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------------*/
    /**
     * Load Scene Mage
     */
    @WithSpan
    private void doLoadSceneMage(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserMageModel userMageModel = MageManager.getInstance().getUserMageModel(getParentExtension().getParentZone(), uid);

        SendLoadSceneMage objPut = new SendLoadSceneMage();
        objPut.userMageModel = userMageModel;
        objPut.stats = MageManager.getInstance().getStatsMage(userMageModel, getParentExtension().getParentZone());
        objPut.zone = getParentExtension().getParentZone();
        send(objPut, user);
    }



    /**
     * Equip stone manager
     */
    @WithSpan
    private void doEquipStoneMage(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserMageModel userMageModel = MageManager.getInstance().getUserMageModel(getParentExtension().getParentZone(), uid);

        RecEquipStoneMage objGet = new RecEquipStoneMage(data);
        //Thay doi STONE tren manager
        if(!MageManager.getInstance().updateUserStoneMage(getParentExtension().getParentZone(), userMageModel, objGet.id)){
            SendEquipStoneMage objPut = new SendEquipStoneMage(ServerConstant.ErrorCode.ERR_NOT_EXSIST_STONE_MAGE);
            send(objPut, user);
            return;
        }

        SendEquipStoneMage objPut = new SendEquipStoneMage();
        objPut.stats = MageManager.getInstance().getStatsMage(userMageModel, getParentExtension().getParentZone());
        send(objPut, user);
    }



    /**
     * Get List Equipment Mage
     */
    @WithSpan
    private void doGetBagEquipmentMage(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecGetBagMageEquipment objGet = new RecGetBagMageEquipment(data);
        List<SageEquipDataVO> listEquip = new ArrayList<>();
        //Get do theo vi tri
        listEquip.addAll(BagManager.getInstance().getSageEquipDependOnPosition(uid, getParentExtension().getParentZone(), objGet.position));

        SendGetBagMageEquipment objPut = new SendGetBagMageEquipment();
        objPut.listEquip = listEquip;
        objPut.mageManager = MageManager.getInstance();
        send(objPut, user);
    }



    /**
     * Equip Item manager
     */
    @WithSpan
    private void doEquipMageItem(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecEquipMageItem objGet = new RecEquipMageItem(data);
        UserMageModel userMageModel = MageManager.getInstance().getUserMageModel(getParentExtension().getParentZone(), uid);

        //Lay item trong bag
        SageEquipDataVO equip = BagManager.getInstance().getOneSageEquipInBag(uid, getParentExtension().getParentZone(), objGet.hashItem);
        //Kiem tra xem item co ton tai khong
        if(equip == null){
            SendEquipMageItem objPut = new SendEquipMageItem(ServerConstant.ErrorCode.ERR_NOT_EXSIST_ITEM_MAGE);
            send(objPut, user);
            return;
        }

        if(MageManager.getInstance().haveEquipItemMage(objGet.hashItem, userMageModel)){
            SendEquipMageItem objPut = new SendEquipMageItem(ServerConstant.ErrorCode.ERR_CHARACTER_EQUIP_ITEM_THEMSELF);
            send(objPut, user);
            return;
        }

        //Lay do tren nguoi neu co
        SageEquipDataVO unequip = MageManager.getInstance().getEquipmentMageModel(userMageModel, equip.position);
        if(unequip != null){
            if(!BagManager.getInstance().addNewSageEquip(uid, getParentExtension().getParentZone(), unequip)){
                SendEquipMageItem objPut = new SendEquipMageItem(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }
        }
        //Lap item (Update -> bo qua thao item)
        if(!MageManager.getInstance().updateEquipmentMageModel(getParentExtension().getParentZone(), userMageModel, equip, equip.position)){
            SendEquipMageItem objPut = new SendEquipMageItem(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendEquipMageItem objPut = new SendEquipMageItem();
        objPut.stats = MageManager.getInstance().getStatsMage(userMageModel, getParentExtension().getParentZone());
        send(objPut, user);
    }



    /**
     * Unequip Item manager
     */
    @WithSpan
    private void doUnequipMageItem(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecUnequipMageItem objGet = new RecUnequipMageItem(data);
        //Model
        UserMageModel userMageModel = MageManager.getInstance().getUserMageModel(getParentExtension().getParentZone(), uid);
        //Lay do tu model manager ra
        SageEquipDataVO equipData = MageManager.getInstance().getEquipmentMageModel(userMageModel, objGet.position);
        if(equipData == null){
            SendUnequipMageItem objPut = new SendUnequipMageItem(ServerConstant.ErrorCode.ERR_NOT_EXSIST_ITEM_MAGE);
            send(objPut, user);
            return;
        }
        //Add do vao tui
        if(BagManager.getInstance().addNewSageEquip(uid, getParentExtension().getParentZone(), equipData)){
            //Xoa do trong model
            if(MageManager.getInstance().unequipEquipmentMageModel(getParentExtension().getParentZone(), userMageModel, objGet.position)){
                SendUnequipMageItem objPut = new SendUnequipMageItem();
                objPut.stats = MageManager.getInstance().getStatsMage(userMageModel, getParentExtension().getParentZone());
                send(objPut, user);
                return;
            }
        }

        //Ban loi neu khong thanh cong
        SendUnequipMageItem objPut = new SendUnequipMageItem(ServerConstant.ErrorCode.ERR_SYS);
        send(objPut, user);
    }



    /**
     * Lay danh sach skin (da mo khoa)
     */
    @WithSpan
    private void doGetUserMageSkin(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserMageModel userMageModel = MageManager.getInstance().getUserMageModel(getParentExtension().getParentZone(), uid);

        SendGetUserMageSkin objPut = new SendGetUserMageSkin();
        objPut.listSkin = userMageModel.skinOwner;
        send(objPut, user);
    }



    /**
     * Equip Skin manager
     */
    @WithSpan
    private void doEquipMageSkin(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecEquipMageSkin objGet = new RecEquipMageSkin(data);
        if(!MageManager.getInstance().equipSkinMageModel(getParentExtension().getParentZone(), uid, objGet.idSkin)){
            SendEquipMageSkin objPut = new SendEquipMageSkin(ServerConstant.ErrorCode.ERR_NOT_EXSIST_SKIN_MAGE);
            send(objPut, user);
            return;
        }

        SendEquipMageSkin objPut = new SendEquipMageSkin();
        send(objPut, user);
    }
}
