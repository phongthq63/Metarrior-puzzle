package com.bamisu.log.gameserver.module.store;

import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.store.cmd.receive.RecBuyInStore;
import com.bamisu.log.gameserver.module.store.cmd.receive.RecRefreshStore;
import com.bamisu.log.gameserver.module.store.cmd.receive.RecShowStoreInGame;
import com.bamisu.log.gameserver.module.store.cmd.send.SendBuyInStore;
import com.bamisu.log.gameserver.module.store.cmd.send.SendRefreshStore;
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

import java.util.HashMap;
import java.util.Map;

public class StoreHandler extends ExtensionBaseClientRequestHandler {
    StoreManager storeManager;
    public StoreHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_STORE;
        storeManager = StoreManager.getInstance();
        storeManager.setStoreHandler(this);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_SHOW_STORE_IN_GAME:
                handleShowStoreInGame(user, data);
                break;
            case CMD.CMD_BUY_IN_STORE:
                handleBuyInStore(user, data);
                break;
            case CMD.CMD_REFRESH_STORE:
                handleRefreshStore(user, data);
                break;
        }
    }

    @WithSpan
    private void handleRefreshStore(User user, ISFSObject data) {
        RecRefreshStore rec = new RecRefreshStore(data);
        rec.unpackData();
        UserModel um = extension.getUserManager().getUserModel(user);
        if (storeManager.usingMoneyToRefreshStore(um, user.getZone(), rec.idStore, user)){
            SendRefreshStore send = new SendRefreshStore();
            send.idStore = rec.idStore;
            send(send, user);
            return;
        }
        SendRefreshStore send = new SendRefreshStore(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_MONEY);
        send(send, user);
    }

    @WithSpan
    private void handleBuyInStore(User user, ISFSObject data) {
        RecBuyInStore rec = new RecBuyInStore(data);
        UserModel um = extension.getUserManager().getUserModel(user);
        if (storeManager.buy(rec.idStore, rec.slot, um, user.getZone())){
            SendBuyInStore send = new SendBuyInStore();
            send(send, user);

            //Event
            Map<String,Object> dataEvent = new HashMap<>();
            dataEvent.put(Params.TYPE, rec.idStore);
            GameEventAPI.ariseGameEvent(EGameEvent.BUY_INGAME_STORE, um.userID, dataEvent, getParentExtension().getParentZone());
            return;
        }
        SendBuyInStore send = new SendBuyInStore(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_MONEY);
        send(send, user);
    }

    @WithSpan
    private void handleShowStoreInGame(User user, ISFSObject data) {
        RecShowStoreInGame rec = new RecShowStoreInGame(data);
        rec.unpackData();
        long uid = extension.getUserManager().getUserModel(user).userID;
        storeManager.showItemInStore(rec.idStore, user, uid, user.getZone());
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_STORE, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_STORE, this);
    }
}
