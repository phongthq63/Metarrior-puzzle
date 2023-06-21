package com.bamisu.log.gameserver.module.event;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.datamodel.event.entities.EventDataInfo;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.event.cmd.rec.*;
import com.bamisu.log.gameserver.module.event.cmd.send.*;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.event.defind.EEventInGame;
import com.bamisu.log.gameserver.module.event.event.christmas.ChristmasEventManager;
import com.bamisu.log.gameserver.module.event.event.christmas.config.entities.ExchangeChristmasVO;
import com.bamisu.log.gameserver.module.event.event.grand_opening_checkin.GrandOpeningCheckInManager;
import com.bamisu.log.gameserver.module.event.event.login14days.Login14DaysManager;
import com.bamisu.log.gameserver.module.nft.cmd.send.SendNotifyMintHero;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventInGameHandler extends ExtensionBaseClientRequestHandler {
    GrandOpeningCheckInManager grandOpeningCheckInManager;
    public EventInGameHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_EVENT;
        new EventInGameGameEventHandler(extension.getParentZone());

        grandOpeningCheckInManager = GrandOpeningCheckInManager.getInstance();
        grandOpeningCheckInManager.setEventInGameHandler(this);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_GET_LIST_EVENT:
                doGetListEvent(user, data);
                break;
            case CMD.CMD_GET_INFO_EVENT:
                doGetInfoEvent(user, data);
                break;
            case CMD.CMD_ACTION_IN_EVENT:
                doActionInEvent(user, data);
                break;
            case CMD.CMD_GET_EVENT_LOGIN_CONFIG:
                doGetEventLoginConfig(user);
                break;
            case CMD.CMD_COLLECT_GIFT_LOGIN:
                doCollectGiftLogin(user, data);
                break;
        }

        //Grand opening check in
        switch (cmdId){
            case CMD.CMD_SHOW_GRAND_OPENING_CHECK_IN:
                handleShowGrandOpening(user, data);
                break;
            case CMD.CMD_COLLECT_GIFT_GRAND_OPENING:
                handleCollectGiftGrandOpening(user, data);
                break;
            case CMD.CMD_CLAIM_QUICKLY:
                handleClaimQuickly(user, data);
                break;
        }
    }

    @WithSpan
    private void handleClaimQuickly(User user, ISFSObject data) {
        RecClaimQuickly rec = new RecClaimQuickly(data);
        rec.unpackData();
        grandOpeningCheckInManager.claimQuickly(user, extension);
    }

    @WithSpan
    private void handleCollectGiftGrandOpening(User user, ISFSObject data) {
        RecCollectGiftGrandOpening rec = new RecCollectGiftGrandOpening(data);
        rec.unpackData();
        grandOpeningCheckInManager.collectGiftGrandOpening(extension, user, rec.position);
    }

    @WithSpan
    private void handleShowGrandOpening(User user, ISFSObject data) {
        RecGrandOpening rec = new RecGrandOpening(data);
        rec.unpackData();
        grandOpeningCheckInManager.showGrandOpening(user, extension);
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_EVENT, this);
    }

    @Override
    protected void initHandlerServerEvent() {

    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetListEvent(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        SendGetListEvent objPut = new SendGetListEvent();
        objPut.listEvent = EventInGameManager.getInstance().getListCurrentEvent(uid, getParentExtension().getParentZone());
        send(objPut, user);
    }

    @WithSpan
    private void doGetInfoEvent(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecGetInfoEvent objGet = new RecGetInfoEvent(data);
        EEventInGame event = EEventInGame.fromID(objGet.id);
        Map<String,Integer> listEventGeneral = EventInGameManager.getInstance().getEventGeneral(getParentExtension().getParentZone());
        Map<String,Integer> listEventSpecial = EventInGameManager.getInstance().getEventSpecial(getParentExtension().getParentZone());
        if(!listEventGeneral.containsKey(objGet.id) && !listEventSpecial.containsKey(objGet.id)){
            SendGetInfoEvent objPut = new SendGetInfoEvent(ServerConstant.ErrorCode.ERR_EVENT_DONT_EXSIST);
            send(objPut, user);
            return;
        }

        SendGetInfoEvent objPut = new SendGetInfoEvent();
        objPut.idEvent = event.getId();
        objPut.userEventDataModel = EventInGameManager.getInstance().getUserEventDataModel(uid, getParentExtension().getParentZone());
        objPut.listEventGeneral = listEventGeneral;
        objPut.listEventSpecial = listEventSpecial;
        objPut.zone = getParentExtension().getParentZone();

        switch (event){
            case CHRISSMATE:
                objPut.christmasShopCf = ChristmasEventManager.getInstance().getShopChristmasConfig();
                break;
        }

        send(objPut, user);
    }


    /**
     * Xu ly Event in game
     * @param user
     * @param data
     */
    @WithSpan
    private void doActionInEvent(User user, ISFSObject data){
        RecActionInEvent objGet = new RecActionInEvent(data);

        switch (objGet.event){
            case CHRISSMATE:
                doActionInEventChristmas(user, objGet);
                break;
        }
    }

    @WithSpan
    private void doActionInEventChristmas(User user, RecActionInEvent data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        //Kiem tra event end chua
        if(ChristmasEventManager.getInstance().isTimeEndEvent(getParentExtension().getParentZone())){
            SendActionInEvent objPut = new SendActionInEvent(ServerConstant.ErrorCode.ERR_EVENT_HAVE_ENDED);
            send(objPut, user);
            return;
        }

        switch (data.action){
            case BUY:
                if(data.count <= 0){
                    SendActionInEvent objPut = new SendActionInEvent(ServerConstant.ErrorCode.ERR_INVALID_VALUE);
                    send(objPut, user);
                    return;
                }

                ExchangeChristmasVO exchangeCf = ChristmasEventManager.getInstance().getShopChristmasConfig(data.id);
                //Kiem tra cf ton tai khong
                if(exchangeCf == null){
                    SendActionInEvent objPut = new SendActionInEvent(ServerConstant.ErrorCode.ERR_NOT_EXSIST_PACKAGE);
                    send(objPut, user);
                    return;
                }

                //Kiem tra con du so luot khong
                EventDataInfo eventData = ChristmasEventManager.getInstance().getDataEventChristmas(uid, getParentExtension().getParentZone());
                if(eventData.readCountBuyChristmas(data.id) + data.count > exchangeCf.buy){
                    SendActionInEvent objPut = new SendActionInEvent(ServerConstant.ErrorCode.ERR_HAVE_LIMIT_BUY_IN_EVENT);
                    send(objPut, user);
                    return;
                }

                //Kiem tra du tai nguyen doi khong
                if(!BagManager.getInstance().addItemToDB(
                        exchangeCf.readCost().stream().map(obj -> new ResourcePackage(obj.id, obj.amount * data.count)).collect(Collectors.toList()),
                        uid,
                        getParentExtension().getParentZone(),
                        UserUtils.TransactionType.EXCHANGE_IN_EVENT)){
                    SendActionInEvent objPut = new SendActionInEvent(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
                    send(objPut, user);
                    return;
                }

                //Nhan tai nguyen
                Map<String, Object> dataEvent = new HashMap<>();
                dataEvent.put(data.id, data.count);
                List<ResourcePackage> reward = exchangeCf.readReward().stream().
                        map(obj -> new ResourcePackage(obj.id, obj.amount * data.count)).
                        collect(Collectors.toList());
                if(!BagManager.getInstance().addItemToDB(
                        reward,
                        uid,
                        getParentExtension().getParentZone(),
                        UserUtils.TransactionType.EXCHANGE_IN_EVENT)){
                    SendActionInEvent objPut = new SendActionInEvent(ServerConstant.ErrorCode.ERR_SYS);
                    send(objPut, user);
                    return;
                }
                if(!ChristmasEventManager.getInstance().updateDataEventChristmas(uid, getParentExtension().getParentZone(), data.action, dataEvent)){
                    SendActionInEvent objPut = new SendActionInEvent(ServerConstant.ErrorCode.ERR_SYS);
                    send(objPut, user);
                    return;
                }

                SendActionInEvent objPut = new SendActionInEvent();
                objPut.id = data.id;
                objPut.count = data.count;
                objPut.reward = reward;

                send(objPut, user);
                return;
        }

        SendActionInEvent objPut = new SendActionInEvent(ServerConstant.ErrorCode.ERR_EVENT_DONT_EXSIST);
        send(objPut, user);
    }

    private void doGetEventLoginConfig(User user) {
        SendGetConfigLogin packet = new SendGetConfigLogin();
        packet.config = Login14DaysManager.getInstance().getConfig();
        packet.state = Login14DaysManager.getInstance().getUserLoginEventModel(user).getGiftState();
        send(packet, user);
    }

    private void doCollectGiftLogin(User user, ISFSObject data) {
        RecCollectGiftLogin packet = new RecCollectGiftLogin(data);
        SendCollectGiftLogin sendCmd = Login14DaysManager.getInstance().collectGiftLogin(user, packet.day, packet.kingdom);
        if (!sendCmd.heroId.equalsIgnoreCase("")) {
            SendNotifyMintHero packet2 = new SendNotifyMintHero(Long.parseLong(user.getName()));
            send(packet2, user);
        }

        send(sendCmd, user);
    }
}
