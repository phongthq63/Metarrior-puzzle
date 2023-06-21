package com.bamisu.log.gameserver.module.WoL;

import com.bamisu.log.gameserver.datamodel.WoL.WoLUserModel;
import com.bamisu.log.gameserver.entities.ColorHero;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.GameEvent.BaseGameEvent;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.WoL.defines.WoLAreaDefines;
import com.bamisu.log.gameserver.module.WoL.defines.WoLStageDefine;
import com.bamisu.log.gameserver.module.WoL.entities.WoLHeroColor;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.List;
import java.util.Map;

public class WoLGameEventHandler extends BaseGameEvent {

    public WoLGameEventHandler(Zone zone) {
        super(zone);
    }

    @WithSpan
    @Override
    public void handleGameEvent(EGameEvent event, long uid, Map<String, Object> data) {
        switch (event){
            case CHAP_CAMPAIGN_UPDATE:
                updateCampaignProgression(uid, data);
                break;
            case FINISH_CAMPAIGN_FIGHTING:
                updateCampaignFighting(uid, data);
                break;
            case FINISH_TOWER_FIGHTING:
                updateTowerFighting(uid, data);
                break;
            case FINISH_HUNT_FIGHTING:
                updateHuntFighting(uid, data);
                break;
            case FINISH_MISSION_FIGHTING:
                updateMissionFighting(uid, data);
                break;
            case SUMMON_TAVERN:
                updateSummonTavern(uid, data);
                break;
            case UPDATE_MONEY:
                updateAllianceCoin(uid, data);
                break;
        }
    }

    @WithSpan
    private void updateAllianceCoin(long uid, Map<String, Object> data) {
        WoLHeroColor allianceCoin = new WoLHeroColor(MoneyType.ALLIANCE_COIN.getId(), 0);
        List<ResourcePackage> list = (List<ResourcePackage>) data.get(Params.LIST);
        WoLUserModel woLUserModel = WoLUserModel.copyFromDBtoObject(uid, zone);
        for (WoLHeroColor woLHeroColor: woLUserModel.listChallenge){
            if (woLHeroColor.id.equals(allianceCoin.id)) {
                woLHeroColor.count += allianceCoin.count;
                WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.HERO, WoLStageDefine.HERO_SUMMONS, woLHeroColor.count, uid, zone);
            }
        }
        woLUserModel.saveToDB(zone);
    }

    @WithSpan
    private void updateSummonTavern(long uid, Map<String, Object> data) {
        WoLHeroColor purple = new WoLHeroColor(String.valueOf(ColorHero.PURPLE.getStar()), 0);
        WoLHeroColor red = new WoLHeroColor(String.valueOf(ColorHero.RED.getStar()), 0);
        if (data.get(Params.STAR) == null) {
            return;
        }

        List<Integer> list = (List<Integer>) data.get(Params.STAR);
        for (int i = 0; i< list.size(); i++){
            int num = list.get(i);
            if (num == ColorHero.PURPLE.getStar()){
                purple.count++;
            }

            if (num == ColorHero.RED.getStar()){
                red.count++;
            }
        }

        WoLUserModel woLUserModel = WoLUserModel.copyFromDBtoObject(uid, zone);
        for (WoLHeroColor woLHeroColor: woLUserModel.listChallenge){
            if (woLHeroColor.id.equals(purple.id)){
                woLHeroColor.count+=purple.count;
                WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.HERO, WoLStageDefine.HERO_SUMMONS, woLHeroColor.count, uid, zone);
            }else if (woLHeroColor.id.equals(red.id)){
                woLHeroColor.count+=red.count;
                WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.HERO, WoLStageDefine.HERO_ASCENSION, woLHeroColor.count, uid, zone);
            }
        }
        woLUserModel.saveToDB(zone);
    }

    @WithSpan
    private void updateMissionFighting(long uid, Map<String, Object> data) {
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.MISSION, WoLStageDefine.MISSIONS_DAMAGE, (int) data.get(Params.DAMAGE), uid, zone);
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.MISSION, WoLStageDefine.MISSIONS_HEAL, (int) data.get(Params.HEAL), uid, zone);
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.MISSION, WoLStageDefine.MISSION_TANK, (int) data.get(Params.TANK), uid, zone);
    }

    @WithSpan
    private void updateHuntFighting(long uid, Map<String, Object> data) {
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.TREASURE, WoLStageDefine.TREASURE_HUNT_DAMAGE, (int) data.get(Params.DAMAGE), uid, zone);
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.TREASURE, WoLStageDefine.TREASURE_HUNT_HEAL, (int) data.get(Params.HEAL), uid, zone);
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.TREASURE, WoLStageDefine.TREASURE_HUNT_TANK, (int) data.get(Params.TANK), uid, zone);
    }

    @WithSpan
    private void updateTowerFighting(long uid, Map<String, Object> data) {
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.TOWER, WoLStageDefine.TOWER_DAMAGE, (int) data.get(Params.DAMAGE), uid, zone);
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.TOWER, WoLStageDefine.TOWER_HEAL, (int) data.get(Params.HEAL), uid, zone);
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.TOWER, WoLStageDefine.TOWER_TANK, (int) data.get(Params.TANK), uid, zone);
    }

    @WithSpan
    private void updateCampaignFighting(long uid, Map<String, Object> data) {
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.CAMPAIGN, WoLStageDefine.CAMPAIGN_DAMAGE, (int) data.get(Params.DAMAGE), uid, zone);
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.CAMPAIGN, WoLStageDefine.CAMPAIGN_HEAL, (int) data.get(Params.HEAL), uid, zone);
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.CAMPAIGN, WoLStageDefine.CAMPAIGN_TANK, (int) data.get(Params.TANK), uid, zone);
    }

    @WithSpan
    private void updateCampaignProgression(long uid, Map<String, Object> data) {
        WoLManager.getInstance().checkWoLConditionPlayer(WoLAreaDefines.CAMPAIGN, WoLStageDefine.CAMPAIGN_PROGRESSION, (int) data.get(Params.AREA) + 1, uid, zone);
    }

    @Override
    public void initEvent() {
        this.registerEvent(EGameEvent.CHAP_CAMPAIGN_UPDATE);
        this.registerEvent(EGameEvent.FINISH_CAMPAIGN_FIGHTING);
        this.registerEvent(EGameEvent.FINISH_TOWER_FIGHTING);
        this.registerEvent(EGameEvent.FINISH_HUNT_FIGHTING);
        this.registerEvent(EGameEvent.FINISH_MISSION_FIGHTING);
        this.registerEvent(EGameEvent.SUMMON_TAVERN);
        this.registerEvent(EGameEvent.UPDATE_MONEY);
    }
}
