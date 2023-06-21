package com.bamisu.log.gameserver.module.event.event.login14days;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.event.cmd.send.SendCollectGiftLogin;
import com.bamisu.log.gameserver.module.event.event.login14days.configs.Login14DaysConfig;
import com.bamisu.log.gameserver.module.event.event.login14days.models.UserLoginEventModel;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Login14DaysManager {

    private Logger logger = Logger.getLogger(Login14DaysManager.class);
    private final Login14DaysConfig config;
    private static final Login14DaysManager instance = new Login14DaysManager();
    public static Login14DaysManager getInstance() {
        return instance;
    }

    private Login14DaysManager() {
        this.config = Utils.fromJson(Utils.loadConfig(ServerConstant.Event.FILE_PATH_CONFIG_14_DAYS_LOGIN), Login14DaysConfig.class);
    }

    public Login14DaysConfig getConfig() {
        return this.config;
    }

    public List<List<ResourcePackage>> getGiftsConfig() {
        return this.config.getGifts();
    }

    public List<ResourcePackage> getGiftConfigByDay(int day) {
        List<List<ResourcePackage>> gifts = this.getGiftsConfig();
        if (day < 0 || day >= gifts.size()) {
            return new ArrayList<>();
        }

        return gifts.get(day);
    }

    public boolean isActiveEvent() {
        long currentTime = System.currentTimeMillis() / 1000;
        this.logger.info("isActiveEvent current time: " + currentTime);
        this.logger.info("isActiveEvent this.config.getStartTime() time: " + this.config.getStartTime());
        this.logger.info("isActiveEvent this.config.getEndTime() time: " + this.config.getEndTime());
        return currentTime >= this.config.getStartTime() && currentTime <= this.config.getEndTime();
    }



    public synchronized void handleUserLogin(long userId, Zone zone) {
        if (!this.isActiveEvent()) {
            return;
        }

        String currentDate = Utils.dateNowString();
        UserLoginEventModel model = UserLoginEventModel.copyFromDBtoObject(userId, this.config.getSession(), zone);
        boolean isSave = false;
        if (currentDate.equalsIgnoreCase(model.getCurrentDay())) {
            return;
        }

        List<Integer> giftState = model.getGiftState();
        for (int i = 0; i < giftState.size(); i++) {
            if (giftState.get(i) == 0) {
                giftState.set(i, 1);
                isSave = true;
                model.setCurrentDay(currentDate);
                break;
            }
        }

        if (isSave) {
            model.saveToDB(zone);
        }
    }

    public UserLoginEventModel getUserLoginEventModel(User user) {
        Zone zone = user.getZone();
        return UserLoginEventModel.copyFromDBtoObject(user.getName(), this.config.getSession(), zone);
    }

    public UserLoginEventModel getUserLoginEventModel(long userId, Zone zone) {
        return UserLoginEventModel.copyFromDBtoObject(userId, this.config.getSession(), zone);
    }

     public UserLoginEventModel getUserLoginEventModel(String userId, Zone zone) {
        return UserLoginEventModel.copyFromDBtoObject(userId, this.config.getSession(), zone);
    }



    public synchronized SendCollectGiftLogin collectGiftLogin(User user, int day, String kingdom) {
        UserLoginEventModel model = this.getUserLoginEventModel(user);
        long userId = Long.parseLong(user.getName());
        List<Integer> giftState = model.getGiftState();
        int index = day - 1;
        if (index < 0 || index >= giftState.size()) {
            return new SendCollectGiftLogin(ServerConstant.ErrorCode.ERR_INCOMPLETE);
        }

        int value = giftState.get(index);
        if (value != 1) {
            return new SendCollectGiftLogin(ServerConstant.ErrorCode.ERR_ALREADY_RECEIVED);
        }

        giftState.set(index, 2);
        List<ResourcePackage> resourcePackages = this.getGiftConfigByDay(index);
        SendCollectGiftLogin packet = new SendCollectGiftLogin();
        packet.resourcePackages = resourcePackages;
        if (resourcePackages.get(0).id.equalsIgnoreCase("MON1010")) {
            if (kingdom.equalsIgnoreCase("") || kingdom.equalsIgnoreCase("K6")) {
                int randomId = Utils.randomInRange(1, 5);
                kingdom = "K" + randomId;
            }

            HeroVO heroVO = CharactersConfigManager.getInstance().getRandomHeroConfig(4, kingdom, null, null);
            HeroModel heroModel = HeroModel.createHeroModel(userId, heroVO.id, heroVO.star, EHeroType.NORMAL);
            UserAllHeroModel userAllHeroModel = UserAllHeroModel.copyFromDBtoObject(userId, user.getZone());
            userAllHeroModel.listAllHeroModel.add(heroModel);
            userAllHeroModel.saveToDB(user.getZone());
            packet.heroId = heroModel.id;
        } else {
            BagManager.getInstance().addItemToDB(resourcePackages, userId, user.getZone(), UserUtils.TransactionType.COLLECT_GIFT_LOGIN);
        }

        model.saveToDB(user.getZone());
        return packet;
    }
}
