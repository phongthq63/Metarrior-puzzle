package com.bamisu.log.gameserver.module.celestial;

import com.bamisu.log.gameserver.datamodel.celestial.CelestialSkillV5Model;
import com.bamisu.log.gameserver.datamodel.celestial.UserCelestialModel;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EConditionType;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.celestial.cmd.rec.*;
import com.bamisu.log.gameserver.module.celestial.cmd.send.*;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.celestial.entities.CelestialVO;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.List;
import java.util.stream.Collectors;

public class CelestialHandler extends ExtensionBaseClientRequestHandler {

    public CelestialHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_CELESTIAL;
        new CelestialGameEventHandler(extension.getParentZone());
    }

    public BaseExtension getExtension(){
        return extension;
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_LOAD_SCENE_GET_LIST_CELESTIAL:
                doLoadSceneGetListCelestial(user, data);
                break;
            case CMD.CMD_GET_CELESTIAL_INFO:
                doGetCelestialInfo(user, data);
                break;
            case CMD.CMD_CHANGE_CELESTIAL:
                doChangeCelestial(user, data);
                break;
            case CMD.CMD_UNLOCK_CELESTIAL:
                doUnlockCelestial(user, data);
                break;
        }
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_CELESTIAL, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_CELESTIAL, this);

    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Lay thong tin linh thu
     */
    @WithSpan
    private void doGetCelestialInfo(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecGetCelestialInfo objGet = new RecGetCelestialInfo(data);
        UserCelestialModel userCelestialModel = CelestialManager.getInstance().getUserCelestialModel(getParentExtension().getParentZone(), uid);
        String idCelestial = (objGet.idCelestial == null || objGet.idCelestial.isEmpty()) ? userCelestialModel.readIdCelestial() : objGet.idCelestial;
        if(CharactersConfigManager.getInstance().getCelestialConfig(idCelestial) == null){
            SendGetCelestialInfo objPut = new SendGetCelestialInfo(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CELESTIAL);
            send(objPut, user);
            return;
        }


        SendGetCelestialInfo objPut = new SendGetCelestialInfo();
        objPut.userCelestialModel = userCelestialModel;
        objPut.idCelestial = idCelestial;
        objPut.zone = getParentExtension().getParentZone();
        objPut.skills = CelestialSkillV5Model.copyFromDBtoObject(getParentExtension().getParentZone(), uid, idCelestial).skills;
        send(objPut, user);
    }

    /**
     * Load scene list celestial
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneGetListCelestial(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserCelestialModel userCelestialModel = CelestialManager.getInstance().getUserCelestialModel(getParentExtension().getParentZone(), uid);
        String celestialUse = userCelestialModel.readIdCelestial();
        List<String> celestialUnlock = userCelestialModel.readListCelestialUnlocked(getParentExtension().getParentZone());

        //Unlock con co the auto unlock
        List<CelestialVO> listCelestialAutoUnlock = CharactersConfigManager.getInstance().getCelestialConfig().stream().
                filter(index -> {
                    if(index.unlock.size() == 1){
                        return index.unlock.get(0).id.equals(EConditionType.LEVEL_USER.getId());
                    }else {
                        return false;
                    }
                }).
                collect(Collectors.toList());
        for (CelestialVO celestialVO : listCelestialAutoUnlock) {
            if (CelestialManager.getInstance().checkCanUnlockCelestial(userCelestialModel, celestialVO.id, getParentExtension().getParentZone())) {
                CelestialManager.getInstance().unlockCelestial(userCelestialModel, celestialVO.id, getParentExtension().getParentZone());
            }
        }

        SendLoadSceneGetListCelestial objPut = new SendLoadSceneGetListCelestial();
        objPut.userCelestialModel = userCelestialModel;
        objPut.use = celestialUse;
        objPut.unlock = celestialUnlock;
        objPut.zone = getParentExtension().getParentZone();
        objPut.skills = CelestialSkillV5Model.copyFromDBtoObject(getParentExtension().getParentZone(), uid, celestialUse).skills;
        send(objPut, user);
    }

    /**
     * Thay doi linh thu
     * @param user
     * @param data
     */
    @WithSpan
    private void doChangeCelestial(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecChangeCelestial objGet = new RecChangeCelestial(data);
        CelestialVO cf = CharactersConfigManager.getInstance().getCelestialConfig(objGet.idCelestial);
        //Kiem tra linh thu co khong
        if(cf == null){
            SendChangeCelestial objPut = new SendChangeCelestial(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CELESTIAL);
            send(objPut, user);
            return;
        }

        UserCelestialModel userCelestialModel = CelestialManager.getInstance().getUserCelestialModel(getParentExtension().getParentZone(), uid);
        if(objGet.idCelestial.equals(userCelestialModel.readIdCelestial())){
            SendChangeCelestial objPut = new SendChangeCelestial(ServerConstant.ErrorCode.ERR_ALREADY_USE_CELESTIAL);
            send(objPut, user);
            return;
        }

        //Kiem tra unlock chua
        if(!CelestialManager.getInstance().haveUnlockCelestial(userCelestialModel, objGet.idCelestial, getParentExtension().getParentZone())){
            SendChangeCelestial objPut = new SendChangeCelestial(ServerConstant.ErrorCode.ERR_CAN_NOT_UNLOCK_CELESTIAL);
            send(objPut, user);
            return;
        }

        //Thay doi linh thu dang su dung
        if(!CelestialManager.getInstance().changeCelestialUserCelestialModel(getParentExtension().getParentZone(), userCelestialModel, objGet.idCelestial)){
            SendChangeCelestial objPut = new SendChangeCelestial(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendChangeCelestial objPut = new SendChangeCelestial();
        objPut.idCelestial = objGet.idCelestial;
        send(objPut, user);
    }

    /**
     * Mo khoa linh thu
     * @param user
     * @param data
     */
    @WithSpan
    private void doUnlockCelestial(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecUnlockCelestial objGet = new RecUnlockCelestial(data);
        if(objGet.idCelestial == null || objGet.idCelestial.isEmpty()){
            SendUnlockCelestial objPut = new SendUnlockCelestial(ServerConstant.ErrorCode.ERR_CHOOSE_INVALID_CELESTIAL);
            send(objPut, user);
            return;
        }

        //Kiem tra id ton tai khong
        CelestialVO cf = CharactersConfigManager.getInstance().getCelestialConfig(objGet.idCelestial);
        if(cf == null){
            SendUnlockCelestial objPut = new SendUnlockCelestial(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CELESTIAL);
            send(objPut, user);
            return;
        }

        //Kiem tra da unlock chua
        UserCelestialModel userCelestialModel = CelestialManager.getInstance().getUserCelestialModel(getParentExtension().getParentZone(), uid);
        if(!CelestialManager.getInstance().haveUnlockCelestial(userCelestialModel, objGet.idCelestial, getParentExtension().getParentZone())){
            SendUnlockCelestial objPut = new SendUnlockCelestial(ServerConstant.ErrorCode.ERR_UNLOCKED_CELESTIAL);
            send(objPut, user);
            return;
        }

        //Kiem tra dk
        if(!CelestialManager.getInstance().checkCanUnlockCelestial(userCelestialModel, objGet.idCelestial, getParentExtension().getParentZone())){
            SendUnlockCelestial objPut = new SendUnlockCelestial(ServerConstant.ErrorCode.ERR_CAN_NOT_UNLOCK_CELESTIAL);
            send(objPut, user);
            return;
        }

        //Unlock
        if(!CelestialManager.getInstance().unlockCelestial(userCelestialModel, objGet.idCelestial, getParentExtension().getParentZone())){
            SendUnlockCelestial objPut = new SendUnlockCelestial(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendUnlockCelestial objPut = new SendUnlockCelestial();
        objPut.idCelestial = objGet.idCelestial;
        send(objPut, user);
    }
}
