package com.bamisu.log.gameserver.module.invite;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.invite.UserInviteModel;
import com.bamisu.log.gameserver.module.GameEvent.BaseGameEvent;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EConditionType;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.IAPBuy.entities.ConditionVO;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.bamisu.log.gameserver.module.invite.config.entities.InviteConditionVO;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateAccount;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InviteGameEventHandler extends BaseGameEvent {

    public InviteGameEventHandler(Zone zone) {
        super(zone);
    }

    @WithSpan
    @Override
    public void handleGameEvent(EGameEvent event, long uid, Map<String, Object> data) {
        switch (event){
            case LINK_ACCOUNT:
                handlerLinkAccount(uid, data);
                break;
            case LEVEL_USER_UPDATE:
                handlerLevelUserUpdate(uid, data);
                break;
            case GET_HERO:
                handlerGetHero(uid, data);
                break;
        }
    }

    @Override
    public void initEvent() {
        this.registerEvent(EGameEvent.LINK_ACCOUNT);
        this.registerEvent(EGameEvent.LEVEL_USER_UPDATE);
        this.registerEvent(EGameEvent.GET_HERO);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    @WithSpan
    private void handlerLevelUserUpdate(long uid, Map<String, Object> data) {
        short before = (short) data.getOrDefault(Params.BEFORE, -1);
        short level = (short) data.getOrDefault(Params.LEVEL, -1);
        if(level < 0 || before < 0)return;

        short condition = Short.valueOf(EConditionType.LEVEL_USER_50.getDescription());
        condition = 15;
        List<HeroModel> heroModels = HeroManager.getInstance().getAllHeroModel(uid, zone);
        long countNFT = heroModels.stream()
                .filter(heroModel -> Objects.equals(heroModel.type, EHeroType.NFT.getId()))
                .count();
        if(before < condition && level >= condition && countNFT >= 1){
            InviteManager.getInstance().triggerUpdateRewardInviteModel(zone, uid, EConditionType.LEVEL_USER_50);

            //Save to user is trigger Ultimate
            UserInviteModel userInviteModel = UserInviteModel.copyFromDBtoObject(uid, zone);
            userInviteModel.triggerUltimate = true;
            userInviteModel.saveToDB(zone);
        }
    }

    @WithSpan
    private void handlerLinkAccount(long uid, Map<String, Object> data) {
        InviteManager.getInstance().triggerUpdateRewardInviteModel(zone, uid, EConditionType.LINK_ACCOUNT);
    }

    @WithSpan
    private void handlerGetHero(long uid, Map<String, Object> data) {
        UserInviteModel userInviteModel = UserInviteModel.copyFromDBtoObject(uid, zone);
        List<HeroModel> heroModels = HeroManager.getInstance().getAllHeroModel(uid, zone);
        long countNFT = heroModels.stream()
                .filter(heroModel -> Objects.equals(heroModel.type, EHeroType.NFT.getId()))
                .count();
        int level = BagManager.getInstance().getLevelUser(uid, zone);
        boolean isTrigger = userInviteModel.triggerUltimate;
        short condition = Short.valueOf(EConditionType.LEVEL_USER_50.getDescription());
        condition = 15;

        if(level >= condition && countNFT >= 1 && !isTrigger){
            InviteManager.getInstance().triggerUpdateRewardInviteModel(zone, uid, EConditionType.LEVEL_USER_50);

            //Save to user is trigger Ultimate
            userInviteModel.triggerUltimate = true;
            userInviteModel.saveToDB(zone);
        }

    }
}
