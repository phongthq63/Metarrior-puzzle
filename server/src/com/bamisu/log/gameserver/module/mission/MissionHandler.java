package com.bamisu.log.gameserver.module.mission;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.sql.game.dbo.RankMissionDBO;
import com.bamisu.gamelib.sql.game.dbo.UserRankMissionDBO;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.mission.UserMissionPuzzleModel;
import com.bamisu.log.gameserver.module.campaign.cmd.rec.RecGetMissionRank;
import com.bamisu.log.gameserver.module.campaign.cmd.send.SendGetMissionRank;
import com.bamisu.log.gameserver.module.mission.cmd.send.*;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.event.EEvent;
import com.bamisu.gamelib.event.EventHandler;
import com.bamisu.log.gameserver.sql.rank.dao.RankDAO;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.List;
import java.util.Map;

public class MissionHandler extends ExtensionBaseClientRequestHandler {

    EventHandler eventHandler;

    public MissionHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_MISSION;

        eventHandler = new EventHandler((ZoneExtension) getParentExtension()) {
            @Override
            public void onEvent(Map<String, Object> data) {
                //System.out.println(Utils.toJson(data));
            }
        };

        eventHandler.register(EEvent.ON_USER_CHANGE_DNAME_SUCCESS);
    }

    public UserModel getUserModel(long uid){
        return extension.getUserManager().getUserModel(uid);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_GET_MISSION_CONFIG:
                doGetMissionConfig(user, data);
                break;
            case CMD.CMD_DO_MISSION:
                doDoMission(user, data);
                break;
            case CMD.CMD_GET_MISSION_RANK:
                doGetMissionRank(user, data);
                break;
        }
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_MISSION, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_MISSION, this);
    }



    /*-----------------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------------*/
    /**
     * Lay thong danh sach nhiem vu
     * @param user
     */
    @WithSpan
    private void doGetMissionConfig(User user, ISFSObject dara){
        long uid = extension.getUserManager().getUserModel(user).userID;
        UserRankMissionDBO userRankMissionDBO = RankDAO.getUserRankMission(getParentExtension().getParentZone(), uid);

        SendGetMissionConfig objPut = new SendGetMissionConfig();
        objPut.userRankMissionDBO = userRankMissionDBO;
        send(objPut, user);
    }

    @WithSpan
    private void doDoMission(User user, ISFSObject data){
        MissionManager.getInstance().doMission(this, user);
    }

    @WithSpan
    private void doGetMissionRank(User user, ISFSObject data) {
        RecGetMissionRank objGet = new RecGetMissionRank(data);

        List<RankMissionDBO> rankMissionDBOS = RankDAO.getListRankMission(getParentExtension().getParentZone(), objGet.page, objGet.size);

        SendGetMissionRank objPut = new SendGetMissionRank();
        objPut.userManager = extension.getUserManager();
        objPut.rankMissionDBOS = rankMissionDBOS;
        send(objPut, user);
    }
}
