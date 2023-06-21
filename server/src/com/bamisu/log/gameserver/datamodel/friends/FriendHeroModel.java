package com.bamisu.log.gameserver.datamodel.friends;

import com.bamisu.log.gameserver.module.friends.FriendHeroManager;
import com.bamisu.log.gameserver.module.hero.entities.HeroInfo;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FriendHeroModel extends DataModel {
    private static Object lock = new Object();
    public long uid;
    public List<HeroInfo> listHero = new ArrayList<>();
    public int time;

    public FriendHeroModel(long uId) {
        this.uid = uId;
        init();
    }

    public List<HeroInfo> readHero(){
        synchronized (lock){
            return this.listHero.parallelStream().
                    filter(Objects::nonNull).
                    collect(Collectors.toList());
        }
    }

    private void init() {
        this.time = Utils.getTimestampInSecond() + FriendHeroManager.getInstance().getTimeRefreshCacheHeroFriendModelConfig()*60;
    }

    public FriendHeroModel() {

    }


    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static FriendHeroModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    public static FriendHeroModel copyFromDBtoObject(String uId, Zone zone) {
        FriendHeroModel pInfo = null;
        try {
            String str = (String) getModel(uId, FriendHeroModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, FriendHeroModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
        }
        if (pInfo == null) {
            pInfo = new FriendHeroModel(Long.parseLong(uId));
            pInfo.saveToDB(zone);
        }
        return pInfo;
    }

    public static FriendHeroModel create(long uId, Zone zone) {
        FriendHeroModel d = new FriendHeroModel(uId);
        if (d.saveToDB(zone)) {
            return d;
        }
        return null;
    }

}
