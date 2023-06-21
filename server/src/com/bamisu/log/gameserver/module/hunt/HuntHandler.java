package com.bamisu.log.gameserver.module.hunt;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.hunt.UserHuntModel;
import com.bamisu.log.gameserver.datamodel.hunt.entities.HuntInfo;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hunt.cmd.rec.RecDoHunt;
import com.bamisu.log.gameserver.module.hunt.cmd.send.SendGetInfoHunt;
import com.bamisu.log.gameserver.module.hunt.cmd.send.SendLoadSceneHunt;
import com.bamisu.log.gameserver.module.hunt.config.entities.RewardPowerVO;
import com.bamisu.log.gameserver.module.hunt.config.entities.SlotRewardVO;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.ArrayList;
import java.util.List;

public class HuntHandler extends ExtensionBaseClientRequestHandler {

    public HuntHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_HUNT;
    }

    public UserModel getUserModel(long uid){
        return extension.getUserManager().getUserModel(uid);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_LOAD_SCENE_HUNT:
                doLoadSceneHunt(user, data);
                break;
            case CMD.CMD_GET_INFO_HUNT:
                doGetInfoHunt(user, data);
                break;
            case CMD.CMD_HUNT:
                doHunt(user, data);
                break;
        }
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {
    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_HUNT, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_HUNT, this);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Day user vao room
     * @param user
     * @param data
     */
    @WithSpan
    private  void doLoadSceneHunt(User user, ISFSObject data){
        SendLoadSceneHunt objPut = new SendLoadSceneHunt();
        send(objPut, user);

        doGetInfoHunt(user, data);
    }

    /**
     * vao scene hunt
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetInfoHunt(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserHuntModel userHuntModel = HuntManager.getInstance().getUserHuntModel(uid, getParentExtension().getParentZone());
        HuntInfo huntInfo = HuntManager.getInstance().getUserHuntInfo(userHuntModel, getParentExtension().getParentZone());

        // khởi tạo MON1024 nếu chưa có
        UserBagModel userBagModel = UserBagModel.copyFromDBtoObject(uid, user.getZone());
        if(userBagModel.mapMoney.get(MoneyType.VOUCHER_LOTO_SOG.getId()) == null){
            userBagModel.mapMoney.put("MON1024", new MoneyPackageVO("MON1024", 0));
            userBagModel.saveToDB(user.getZone());
        }
        RewardPowerVO rewardCf = HuntManager.getInstance().getRewardHuntConfig(HeroManager.getInstance().getMaxLevelUpHeroModel(uid, user.getZone()));
        List<ResourcePackage> list = new ArrayList<>();
        for (SlotRewardVO slot : rewardCf.slot) {
            list.add(new ResourcePackage(slot.reward.id, slot.reward.amount));
        }

        SendGetInfoHunt objPut = new SendGetInfoHunt();
        objPut.huntInfo = huntInfo;
        objPut.listReward = list;
        objPut.fightCost = - (HuntManager.getInstance().getCostFightHunt().get(0).amount);
        objPut.zone = getParentExtension().getParentZone();
        send(objPut, user);
    }

    /**
     * di san
     * @param user
     * @param data
     */
    @WithSpan
    private void doHunt(User user, ISFSObject data){
        RecDoHunt recDoHunt = new RecDoHunt(data);
        long uid = extension.getUserManager().getUserModel(user).userID;
        HuntManager.getInstance().doHunt(user, uid, recDoHunt.update, this, recDoHunt.sageSkill);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
}
