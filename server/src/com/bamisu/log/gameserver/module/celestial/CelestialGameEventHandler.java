package com.bamisu.log.gameserver.module.celestial;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.datamodel.celestial.UserCelestialModel;
import com.bamisu.log.gameserver.module.GameEvent.BaseGameEvent;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EConditionType;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.celestial.entities.CelestialVO;
import com.bamisu.log.gameserver.module.characters.entities.Celestial;
import com.smartfoxserver.v2.entities.Zone;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CelestialGameEventHandler extends BaseGameEvent {

    public CelestialGameEventHandler(Zone zone) {
        super(zone);
    }

    @Override
    public void handleGameEvent(EGameEvent event, long uid, Map<String, Object> data) {
        switch (event){
            case LEVEL_USER_UPDATE:
                handerLevelUserUpdate(uid, data);
                break;
        }
    }

    @Override
    public void initEvent() {
        this.registerEvent(EGameEvent.LEVEL_USER_UPDATE);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    private void handerLevelUserUpdate(long uid, Map<String, Object> data) {
        int level = (short) data.getOrDefault(Params.LEVEL, -1);
        if(level == -1)return;

        List<CelestialVO> listCelestial = CharactersConfigManager.getInstance().getCelestialConfig().stream().
                filter(index -> {
                    if(index.unlock.size() == 1){
                        ResourcePackage condition = index.unlock.get(0);
                        return condition.id.equals(EConditionType.LEVEL_USER.getId()) &&
                                level >= condition.amount;
                    }else {
                        return false;
                    }
                }).
                collect(Collectors.toList());
        UserCelestialModel userCelestialModel = CelestialManager.getInstance().getUserCelestialModel(zone, uid);
        for (CelestialVO celestialVO : listCelestial) {
            if (CelestialManager.getInstance().checkCanUnlockCelestial(userCelestialModel, celestialVO.id, zone)) {
                CelestialManager.getInstance().unlockCelestial(userCelestialModel, celestialVO.id, zone);
            }
        }
    }
}
