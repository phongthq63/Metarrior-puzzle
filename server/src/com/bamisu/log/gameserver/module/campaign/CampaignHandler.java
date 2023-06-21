package com.bamisu.log.gameserver.module.campaign;

import com.bamisu.gamelib.sql.game.dbo.RankLeagueDBO;
import com.bamisu.log.gameserver.datamodel.campaign.UserCampaignDetailModel;
import com.bamisu.log.gameserver.datamodel.league.RankCampaignModel;
import com.bamisu.log.gameserver.module.campaign.cmd.rec.RecBuyStoreCampaign;
import com.bamisu.log.gameserver.module.campaign.cmd.rec.RecFightMainCampaign;
import com.bamisu.log.gameserver.module.campaign.cmd.send.SendCurrentCampainState;
import com.bamisu.log.gameserver.module.campaign.cmd.send.SendGetCampaignRank;
import com.bamisu.log.gameserver.module.campaign.cmd.send.SendStoreCampaign;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.league.LeagueManager;
import com.bamisu.log.gameserver.sql.rank.dao.RankDAO;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

/**
 * Create by Popeye on 10:28 AM, 2/6/2020
 */
public class CampaignHandler extends ExtensionBaseClientRequestHandler {

    public CampaignHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_CAMPAIGN;
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.FIGHT_MAIN_CAMPAIGN:
                handleFightMainCampaign(user, data);
                break;
            case CMD.GET_CURRENT_CAMPAIGN_STATE:
                handleGetCurrentCampaignState(user, data);
                break;
            case CMD.CMD_UPDATE_AREA_CAMPAIGN:
                handlerUpdateAreaCampaign(user, data);
                break;
            case CMD.CMD_GET_STORE_CAMPAIGN:
                handlerGetStoreCampaign(user, data);
                break;
            case CMD.CMD_BUY_STORE_CAMPAIGN:
                handlerBuyStoreCampaign(user, data);
                break;
            case CMD.CMD_GET_CAMPAIGN_RANK:
                handlerGetCampaignRank(user, data);
                break;
        }
    }

    @WithSpan
    private void handleGetCurrentCampaignState(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserCampaignDetailModel userCampaignDetailModel = CampaignManager.getInstance().getUserCampaignDetailModel(user.getZone(), uid);
        String nextStation = userCampaignDetailModel.userMainCampaignDetail.readNextStation();

        SendCurrentCampainState send = new SendCurrentCampainState();
        send.area = Integer.parseInt(nextStation.split(",")[0]);
        send.state = Integer.parseInt(nextStation.split(",")[1]);
        send.saveStation = userCampaignDetailModel.userMainCampaignDetail.mapSaveStation;
        send(send, user);
    }

    @WithSpan
    private void handleFightMainCampaign(User user, ISFSObject data) {
//        Logger.getLogger("catch").info("==fight==");
        RecFightMainCampaign recFightMainCampaign = new RecFightMainCampaign(data);
        recFightMainCampaign.unpackData();
        CampaignManager.getInstance().fightMainCampaign(user.getZone(), user, recFightMainCampaign, recFightMainCampaign.update, recFightMainCampaign.sageSkill);
//        Logger.getLogger("catch").info("=======");
    }

    @WithSpan
    private void handlerUpdateAreaCampaign(User user, ISFSObject data){
        CampaignManager.getInstance().updateAreaCampaign(user.getZone(), user);
    }

    @WithSpan
    private void handlerGetStoreCampaign(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        UserCampaignDetailModel userCampaignDetailModel = CampaignManager.getInstance().getUserCampaignDetailModel(user.getZone(), uid);

        SendStoreCampaign send = new SendStoreCampaign();
        send.totalStar = userCampaignDetailModel.userMainCampaignDetail.readTotalStar();
        send.slots = CampaignManager.getInstance().readStoreCampaign(user.getZone(), userCampaignDetailModel);
        send(send, user);
    }

    @WithSpan
    private void handlerBuyStoreCampaign(User user, ISFSObject data){
        RecBuyStoreCampaign rec = new RecBuyStoreCampaign(data);
        CampaignManager.getInstance().buyStoreCampaign(user.getZone(), user, rec.position);
    }

    @WithSpan
    private void handlerGetCampaignRank(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RankLeagueDBO rankLeagueDBO = RankDAO.getRankLeague(user.getZone(), uid);
        int leagueId = rankLeagueDBO != null ? rankLeagueDBO.leagueId : -1;
//        List<RankCampaignDBO> rankCampaignDBOS = RankDAO.getListRankCampaign(user.getZone(), uid);
        RankCampaignModel rankCampaignModel = LeagueManager.getInstance().getRankCampaignModel(leagueId, user.getZone());

        SendGetCampaignRank sendGetCampaignRank = new SendGetCampaignRank();
        sendGetCampaignRank.userManager = extension.getUserManager();
        sendGetCampaignRank.rankCampaignModel = rankCampaignModel;
        sendGetCampaignRank.rankLeagueDBO = rankLeagueDBO;
        send(sendGetCampaignRank, user);
    }



    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_CAMPAIGN, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_CAMPAIGN, this);
    }
}
