package com.bamisu.log.gameserver.module.tower;

import com.bamisu.log.gameserver.module.tower.cmd.rec.RecFightTower;
import com.bamisu.log.gameserver.module.tower.cmd.send.SendGetRankTower;
import com.bamisu.log.gameserver.module.tower.cmd.send.SendLoadSceneTower;
import com.bamisu.log.gameserver.module.tower.config.entities.TowerVO;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.manager.UserManager;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

public class TowerHandler extends ExtensionBaseClientRequestHandler {

    public TowerHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_TOWER;
    }

    public UserManager getUserManager(){
        return extension.getUserManager();
    }

    public UserModel getUserModel(long uid){
        return getUserManager().getUserModel(uid);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_LOAD_SCENE_TOWER:
                doLoadSceneTower(user, data);
                break;
            case CMD.CMD_GET_RANK_TOWER:
                doGetRankTower(user, data);
                break;
            case CMD.CMD_FIGHT_TOWER:
                doFightTower(user, data);
                break;
        }
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_TOWER, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_TOWER, this);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Load scene thap
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneTower(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        SendLoadSceneTower objPut = new SendLoadSceneTower();
        objPut.floor = TowerManager.getInstance().getFloorUserTowerModel(uid, getParentExtension().getParentZone());
        send(objPut, user);
    }

    /**
     * Lay danh sach top leo thap
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetRankTower(User user, ISFSObject data){
        UserModel userModel = extension.getUserManager().getUserModel(user);

        SendGetRankTower objPut = new SendGetRankTower();
        objPut.userModel = userModel;
        objPut.userManager = extension.getUserManager();
        objPut.userTowerModel = TowerManager.getInstance().getUserTowerModel(userModel.userID, getParentExtension().getParentZone());
        objPut.listRanker = TowerManager.getInstance().getListTopRankTower(getParentExtension().getParentZone());
        send(objPut, user);
    }

    /**
     * Bat dau leo thap
     * @param user
     * @param data
     */
    @WithSpan
    private void doFightTower(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        TowerVO towerCf = TowerManager.getInstance().getTowerConfig(
                TowerManager.getInstance().getFloorUserTowerModel(uid, getParentExtension().getParentZone())
        );

        RecFightTower recFightTower = new RecFightTower(data);
        TowerManager.getInstance().fight(user, uid, this, recFightTower.update, towerCf, recFightTower.sageSkill);
    }
}
