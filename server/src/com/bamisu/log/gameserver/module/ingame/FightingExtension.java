package com.bamisu.log.gameserver.module.ingame;

import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingFactory;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.extensions.SFSExtension;
import org.apache.log4j.Logger;

/**
 * Create by Popeye on 11:39 AM, 1/14/2020
 */
public class FightingExtension extends BaseExtension {
//    FightingManager fightingManager;

    public FightingManager getFightingManager() {
        return null;
    }

    @Override
    public void init() {
        try{
//            fightingManager = FightingFactory.newFightingManager(this);
            initModule();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initConfig() {

    }

    @Override
    public void initLogic() {

    }

    @Override
    public void initDB() {

    }

    @Override
    public void initModule() {
//        new FightingHandler(this);
    }

    @Override
    public void initLogger() {

    }

    @Override
    public void onServerReady() {

    }
}
