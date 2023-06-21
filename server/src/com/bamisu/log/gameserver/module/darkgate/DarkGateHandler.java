package com.bamisu.log.gameserver.module.darkgate;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.darkgate.cmd.rec.RecFightDarkRealm;
import com.bamisu.log.gameserver.module.darkgate.cmd.rec.RecFightEndlessNight;
import com.bamisu.log.gameserver.module.darkgate.cmd.send.SendEndlessNightLogs;
import com.bamisu.log.gameserver.module.darkgate.model.EndlessNightLogsModel;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

/**
 * Create by Popeye on 10:04 AM, 11/6/2020
 */
public class DarkGateHandler extends ExtensionBaseClientRequestHandler {
    public DarkGateManager darkGateManager;

    public DarkGateHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_DARK_GATE;

        darkGateManager = new DarkGateManager(this);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId) {
            case CMD.GET_DARK_GATE_SCENE_INFO:
                getDarkGateSceneInfo(user, data);
                break;
            case CMD.GET_DARK_REALM_SCENE_INFO:
                getDarkRealmSceneInfo(user, data);
                break;
            case CMD.GET_DARK_REALM_RANK:
                getDarkRealmRank(user, data);
                break;
            case CMD.CHALLENGE_DARK_REALM:
                fightDarkRealm(user, data);
                break;
            case CMD.GET_DARK_REALM_MY_RANK:
                getDarkRealmMyRank(user, data);
                break;

            case CMD.GET_ENDLESS_NIGHT_SCENE_INFO:
                getEndlessNightSceneInfo(user, data);
                break;
            case CMD.GET_ENDLESS_NIGHT_RANK:
                getEndlessNightRank(user, data);
                break;
            case CMD.CHALLENGE_ENDLESS_NIGHT:
                fightEndlessNight(user, data);
                break;
            case CMD.GET_ENDLESS_NIGHT_MY_RANK:
                getEndlessNightMyRank(user, data);
                break;

            case CMD.GET_DARK_REALM_LOGS:
                getDarkRealmLogs(user, data);
                break;
            case CMD.GET_ENDLESS_NIGHT_LOGS:
                getEndlessLogs(user, data);
                break;
        }
    }

    @WithSpan
    private void getEndlessLogs(User user, ISFSObject data) {
        darkGateManager.getEndlessLogs(user);
    }

    @WithSpan
    private void getDarkRealmLogs(User user, ISFSObject data) {
        darkGateManager.getDarkRealmLogs(user);
    }

    @WithSpan
    private void getEndlessNightMyRank(User user, ISFSObject data) {
        darkGateManager.getEndlessNightMyRank(user);
    }

    @WithSpan
    private void fightEndlessNight(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecFightEndlessNight recFightEndlessNight = new RecFightEndlessNight(data);
        darkGateManager.fightEndlessNight(user, uid, recFightEndlessNight.update, recFightEndlessNight.sageSkill);
    }

    @WithSpan
    private void getEndlessNightRank(User user, ISFSObject data) {
        darkGateManager.getEndlessNightRank(user);
    }

    @WithSpan
    private void getEndlessNightSceneInfo(User user, ISFSObject data) {
        darkGateManager.getEndlessNightSceneInfo(user);
    }

    /**
     * lấy thông tin rank của mình
     * @param user
     * @param data
     */
    @WithSpan
    private void getDarkRealmMyRank(User user, ISFSObject data) {
        darkGateManager.getDarkRealmMyRank(user);
    }

    @WithSpan
    private void fightDarkRealm(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecFightDarkRealm recFightDarkRealm = new RecFightDarkRealm(data);
        darkGateManager.fightDarkRealm(user, uid, recFightDarkRealm.update, recFightDarkRealm.sageSkill);
    }

    @WithSpan
    private void getDarkRealmRank(User user, ISFSObject data) {
        darkGateManager.getDarkRealmRank(user);
    }

    @WithSpan
    private void getDarkRealmSceneInfo(User user, ISFSObject data) {
        darkGateManager.getDarkRealmSceneInfo(user);
    }

    @WithSpan
    private void getDarkGateSceneInfo(User user, ISFSObject data) {
        darkGateManager.getDarkGateSceneInfo(user);
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_DARK_GATE, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_DARK_GATE, this);
    }
}
