package com.bamisu.log.gameserver.module.adventure;

import com.bamisu.log.gameserver.datamodel.bag.entities.AdventureModel;
import com.bamisu.log.gameserver.module.adventure.cmd.receive.*;
import com.bamisu.log.gameserver.module.adventure.cmd.send.SendClickOnFastReward;
import com.bamisu.log.gameserver.module.adventure.cmd.send.SendGoToAdventure;
import com.bamisu.log.gameserver.module.adventure.entities.FastRewardVO;
import com.bamisu.log.gameserver.module.mail.MailManager;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.defines.EGiftVip;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AdventureHandler extends ExtensionBaseClientRequestHandler {
    AdventureManager adventureManager;
    public AdventureHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_ADVENTURE;
        adventureManager = AdventureManager.getInstance();
        adventureManager.setAdventureHandlerHandler(this);
    }

    public UserModel getUserModel(long uid){
        return getUserModel(uid);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_LOOT_ITEM:
                handleLootItem(user, data);
                break;
            case CMD.CMD_GO_TO_ADVENTURE:
                try {
                    handleGoToAdventure(user, data);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case CMD.CMD_GET_FAST_REWARD:
                handleGetFastReward(user, data);
                break;
            case CMD.CMD_CLICK_ON_FAST_REWARD:
                handleClickOnFastReward(user, data);
                break;
            case CMD.CMD_CLICK_ON_CHEST_LOOT_ITEM:
                handleClickOnChestLootItem(user, data);
                break;
        }

    }

    @WithSpan
    private void handleClickOnChestLootItem(User user, ISFSObject data) {
        UserModel um = extension.getUserManager().getUserModel(user);
        RecClickOnChestLootItem rec = new RecClickOnChestLootItem(data);
//        UserModel um = extension.getUserManager().getUserModel(user);
        AdventureManager.getInstance().seeLootReward(user, um);
//        ExtensionUtility.getInstance().getUserById(uid);

    }

    @WithSpan
    private void handleClickOnFastReward(User user, ISFSObject data) {
        RecClickOnFastReward rec = new RecClickOnFastReward(data);
        rec.unpackData();
        SendClickOnFastReward send = new SendClickOnFastReward();
        long uid = extension.getUserManager().getUserModel(user).userID;
        AdventureModel adventureModel = adventureManager.getAdventureModel(uid, user.getZone());
        adventureManager.getInstance().checkNewDay(adventureModel, user.getZone());
        try {
            int timeReset = (int) AdventureManager.getInstance().getTimeResetFastReward(adventureModel);
            send.timeReset = timeReset;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        send.reward = adventureModel.reward;
        send(send, user);
    }

    @WithSpan
    private void handleGetFastReward(User user, ISFSObject data) {
        RecGetFastReward rec = new RecGetFastReward(data);
        UserModel um = extension.getUserManager().getUserModel(user);
        AdventureManager.getInstance().getGiftInAdventure(um, user.getZone(), user);
    }

    @WithSpan
    private void handleGoToAdventure(User user, ISFSObject data) throws ParseException {
        RecGoToAdventure rec = new RecGoToAdventure(data);
        long uid = extension.getUserManager().getUserModel(user).userID;
        SendGoToAdventure send = new SendGoToAdventure();
        send.fastReward = AdventureManager.getInstance().checkFastReward(uid, user.getZone());
        send(send, user);
    }

    @WithSpan
    private void handleLootItem(User user, ISFSObject data) {
        RecLootItem rec = new RecLootItem(data);
        UserModel um = extension.getUserManager().getUserModel(user);
        AdventureManager.getInstance().newLootReward(um, null, true, user, false);

    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_ADVENTURE, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_ADVENTURE, this);
    }
}
