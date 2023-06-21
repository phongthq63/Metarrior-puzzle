package com.bamisu.log.gameserver.datamodel.WoL;

import com.bamisu.log.gameserver.entities.ColorHero;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.WoL.WoLManager;
import com.bamisu.log.gameserver.module.WoL.defines.WoLConquerStatus;
import com.bamisu.log.gameserver.module.WoL.defines.WoLRewardDefine;
import com.bamisu.log.gameserver.module.WoL.entities.*;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;

public class WoLUserModel extends DataModel {
    public long uid;
    public List<WoLHeroColor> listChallenge = new ArrayList<>();
    public Map<Integer, List<WoLUserConquer>> mapConquer = new HashMap<>(); //area - list stage and status

    public WoLUserModel(long uId) {
        this.uid = uId;
        init();
        initColorHero();
    }

    private void initColorHero() {
        WoLHeroColor purple = new WoLHeroColor(String.valueOf(ColorHero.PURPLE.getStar()), 0);
        WoLHeroColor red = new WoLHeroColor(String.valueOf(ColorHero.RED.getStar()), 0);
        WoLHeroColor allianceCoin = new WoLHeroColor(MoneyType.ALLIANCE_COIN.getId(), 0);
        this.listChallenge.add(purple);
        this.listChallenge.add(red);
        this.listChallenge.add(allianceCoin);
    }

    private void init() {
        for (int area = 0; area< WoLManager.getInstance().getWoLConfig().size(); area++){
            List<WoLUserConquer> listSum = new ArrayList<>();
            for (int stage = 0; stage< WoLManager.getInstance().getWoLConfig().get(area).list.size(); stage++){
                WoLUserConquer woLUserConquer = new WoLUserConquer();
                woLUserConquer.stage = stage;
                List<WoLChallengeVO> listChallenges = new ArrayList<>();
                for (int challenges = 0; challenges< WoLManager.getInstance().getWoLConfig().get(area).list.get(stage).listChallenges.size(); challenges++){
                    WoLChallengeVO woLChallengeVO = new WoLChallengeVO(WoLConquerStatus.INCOMPLETE.getStatus(), WoLRewardDefine.REWARD_2.getId());
                    listChallenges.add(woLChallengeVO);
                }
                woLUserConquer.listChallenges = listChallenges;
                listSum.add(woLUserConquer);
            }
            this.mapConquer.put(area, listSum);
        }
    }

    public WoLUserModel() {

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

    public static WoLUserModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    public static WoLUserModel copyFromDBtoObject(String uId, Zone zone) {
        WoLUserModel pInfo = null;
        try {
            String str = (String) getModel(uId, WoLUserModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, WoLUserModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
        }
        if (pInfo == null) {
            pInfo = new WoLUserModel(Long.parseLong(uId));
            pInfo.saveToDB(zone);
        }
        return pInfo;
    }

    public static WoLUserModel create(long uId, Zone zone) {
        WoLUserModel d = new WoLUserModel(uId);
        if (d.saveToDB(zone)) {
            return d;
        }
        return null;
    }
}
