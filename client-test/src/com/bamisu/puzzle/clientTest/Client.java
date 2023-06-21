package com.bamisu.puzzle.clientTest;

import com.bamisu.puzzle.clientTest.action.campaign.BuyStoreCampaignAction;
import com.bamisu.puzzle.clientTest.action.campaign.GetCurrentCampaignStateAction;
import com.bamisu.puzzle.clientTest.action.campaign.GetStoreCampaignAction;
import com.bamisu.puzzle.clientTest.action.campaign.UpdateAreaCampaignAction;
import com.bamisu.puzzle.clientTest.action.celestial.*;
import com.bamisu.puzzle.clientTest.action.event.GetListEventAction;
import com.bamisu.puzzle.clientTest.action.guild.*;
import com.bamisu.puzzle.clientTest.action.hero.*;
import com.bamisu.puzzle.clientTest.action.hero.SummonAction;
import com.bamisu.puzzle.clientTest.action.hunt.GetInfoHuntAction;
import com.bamisu.puzzle.clientTest.action.hunt.LoadSceneHuntAction;
import com.bamisu.puzzle.clientTest.action.hunt.RefreshHuntAction;
import com.bamisu.puzzle.clientTest.action.iap.GetInfoIAPTabAction;
import com.bamisu.puzzle.clientTest.action.invite.InputInviteCodeAction;
import com.bamisu.puzzle.clientTest.action.invite.LoadSceneInviteCodeAction;
import com.bamisu.puzzle.clientTest.action.invite.RewardInviteCodeAction;
import com.bamisu.puzzle.clientTest.action.mage.GetBagMageEquipmentAction;
import com.bamisu.puzzle.clientTest.action.mage.GetUserMageSkinAction;
import com.bamisu.puzzle.clientTest.action.mage.LoadSceneMageAction;
import com.bamisu.puzzle.clientTest.action.mage.UnequipMageItemAction;
import com.bamisu.puzzle.clientTest.action.mission.*;
import com.bamisu.puzzle.clientTest.action.quest.GetRewardChestQuestAction;
import com.bamisu.puzzle.clientTest.action.quest.GetRewardQuestAction;
import com.bamisu.puzzle.clientTest.action.quest.GetTableQuestAction;
import com.bamisu.puzzle.clientTest.action.tower.GetRankTowerAction;
import com.bamisu.puzzle.clientTest.action.tower.LoadSceneTowerAction;
import com.bamisu.puzzle.clientTest.base.BaseClient;
import com.bamisu.puzzle.clientTest.base.SmartFoxClient;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.requests.LoginRequest;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client extends BaseClient implements IEventListener {
    private String id = Utils.ranStr(10);
    private ScheduledExecutorService scheduler = LizThreadManager.getInstance().getFixExecutorServiceByName("client_test");

    private String token;
    private String serverAddr;
    private int port;
    private String zone;

    private SmartFoxClient sfsClient;

    public Client(String token, String serverAddr, int port, String zone) {
        this.token = token;
        this.serverAddr = serverAddr;
        this.port = port;
        this.zone = zone;
        initConnection();
        initAction();
    }

    private void initAction() {
        //Hero
//        addAction(new SummonAction(this));
//        addAction(new LoadSceneGetListHeroAction(this));
//        addAction(new GetUserHeroCollectionAction(this));
//        addAction(new UpSizeBagHeroModelAction(this));
//        addAction(new UnequipAllItemAllHeroAction(this));
//        addAction(new LoadSceneSummonHeroAction(this));
//        addAction(new LoadSceneTeamHeroAction(this));
//        addAction(new LoadSceneHeroBlessingAction(this));
//        addAction(new OpenSlotHeroBlessingAction(this));
//        addAction(new LoadSceneUpStarHeroAction(this));
//        addAction(new LoadSceneResetHeroAction(this));
//        addAction(new LoadSceneRetireHeroAction(this));
//        addAction(new SwitchAutoRetireHeroAction(this));
//        addAction(new GetHeroFriendBorrowAction(this));
//        //Campaign
//        addAction(new GetCurrentCampaignStateAction(this));
//        addAction(new UpdateAreaCampaignAction(this));
//        addAction(new GetStoreCampaignAction(this));
//        addAction(new BuyStoreCampaignAction(this));
//        //Celestial
//        addAction(new GetCelestialInfoAction(this));
//        addAction(new LoadSceneGetListCelestialAction(this));
//        addAction(new ChangeCelestialAction(this));
//        addAction(new GetBagCelestialEquipmentAction(this));
//        addAction(new UnequipCelestialItemAction(this));
//        //Event
//        addAction(new GetListEventAction(this));
//        //Guild
//        addAction(new LoadSceneGuildMainAction(this));
//        addAction(new GetListGuildInfoAction(this));
//        addAction(new GetResourceCreateGuildAction(this));
//        addAction(new GetGuildInfoAction(this));
//        addAction(new LeaveGuildAction(this));
//        addAction(new GetLogGuildAction(this));
//        addAction(new ContributeGuildAction(this));
//        addAction(new GetRequestJoinGuildAction(this));
//        addAction(new LoadSceneGetListCelestialGuildAction(this));
//        //Hunt
//        addAction(new LoadSceneHuntAction(this));
//        addAction(new GetInfoHuntAction(this));
//        addAction(new RefreshHuntAction(this));
//        addAction(new LeaveSceneHuntAction(this));
//        //IAP
        addAction(new GetInfoIAPTabAction(this));
//        //Invite
//        addAction(new InputInviteCodeAction(this));
//        addAction(new LoadSceneInviteCodeAction(this));
//        addAction(new RewardInviteCodeAction(this));
//        //Mage
//        addAction(new LoadSceneMageAction(this));
//        addAction(new GetBagMageEquipmentAction(this));
//        addAction(new UnequipMageItemAction(this));
//        addAction(new GetUserMageSkinAction(this));
//        //Mission
//        addAction(new GetMissionBoardAction(this));
//        addAction(new GetListMissionAction(this));
//        addAction(new RefreshMissionBoardAction(this));
//        addAction(new IncreateMissionBoardAction(this));
////        //Quest
//        addAction(new GetTableQuestAction(this));
//        addAction(new GetRewardQuestAction(this));
//        addAction(new GetRewardChestQuestAction(this));
////        //Tower
//        addAction(new LoadSceneTowerAction(this));
//        addAction(new GetRankTowerAction(this));
    }

    private void initConnection() {
        sfsClient = new SmartFoxClient(false);
        // Add event listeners
        sfsClient.addEventListener(SFSEvent.CONNECTION, this);
        sfsClient.addEventListener(SFSEvent.CONNECTION_LOST, this);
        sfsClient.addEventListener(SFSEvent.LOGIN, this);
        sfsClient.addEventListener(SFSEvent.LOGIN_ERROR, this);
        sfsClient.addEventListener(SFSEvent.ROOM_JOIN, this);
        sfsClient.addEventListener(SFSEvent.USER_ENTER_ROOM, this);
        sfsClient.addEventListener(SFSEvent.USER_EXIT_ROOM, this);
        sfsClient.addEventListener(SFSEvent.PUBLIC_MESSAGE, this);
        sfsClient.addEventListener(SFSEvent.ROOM_ADD, this);
        sfsClient.addEventListener(SFSEvent.ROOM_REMOVE, this);
        sfsClient.addEventListener(SFSEvent.ROOM_JOIN_ERROR, this);
        sfsClient.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);
        sfsClient.addEventListener(SFSEvent.CONNECTION_LOST, this);
        sfsClient.addEventListener(SFSEvent.SOCKET_ERROR, this);

//        try {
//            sfsClient.getSocketEngine().forceBlueBox(true);
//        } catch (SFSException e) {
//            e.printStackTrace();
//        }
        ////System.out.println("SmartFox created:" + sfsClient.isConnected() + " BlueBox enabled=" + sfsClient.useBlueBox());
    }

    public SmartFox getSfsClient() {
        return sfsClient;
    }

    public void start() {
        sfsClient.connect(serverAddr, port);
    }

    @Override
    public void dispatch(BaseEvent event) throws SFSException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION)) {
                    if (event.getArguments().get("success").equals(true)) {
                        doLogin();
                    } else {
                    }
                } else if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION_LOST)) {
                } else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN)) {
                    actionLoop();
                } else if (event.getType().equalsIgnoreCase(SFSEvent.ROOM_JOIN)) {
                }
                else if (event.getType().equals(SFSEvent.USER_ENTER_ROOM)) {
                }
                else if (event.getType().equals(SFSEvent.USER_EXIT_ROOM)) {
                }
                else if (event.getType().equals(SFSEvent.PUBLIC_MESSAGE)) {
                } else if (event.getType().equalsIgnoreCase(SFSEvent.ROOM_ADD) || event.getType().equalsIgnoreCase(SFSEvent.ROOM_REMOVE)) {
                }
                else if (event.getType().equals(SFSEvent.USER_ENTER_ROOM)) {
                }
                else if (event.getType().equalsIgnoreCase(SFSEvent.EXTENSION_RESPONSE)) {
                    /*ISFSObject resObj = (ISFSObject) event.getArguments().get("params");
                    int cmdID = resObj.getInt(Params.CMD_ID);
                    switch (cmdID){
                        case CMD.CMD_SUMMON_USER_HERO:
                            action();
                            break;
                    }*/
                    ISFSObject resObj = (ISFSObject) event.getArguments().get("params");
                    sfsClient.onResponse(resObj);
                }
            }


        }).run();
    }

    private void actionLoop() {
        doRandomAction();
        scheduler.schedule(this::actionLoop, Utils.randomInRange(2000, 3000), TimeUnit.MILLISECONDS);
    }

    public void doLogin(){
        SFSObject loginData = new SFSObject();
        loginData.putInt(Params.CMD_ID, 1002);
        loginData.putUtfString(Params.TOKEN, "");
        loginData.putInt(Params.USER_LOGIN_TYPE, 1);
        sfsClient.send(new LoginRequest("", "", zone, loginData));
    }
}
