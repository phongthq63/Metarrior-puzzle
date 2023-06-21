package com.bamisu.log.gameserver.datamodel.hero;

import com.bamisu.log.gameserver.datamodel.ServerVariableModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.entities.ColorHero;
import com.bamisu.log.gameserver.entities.EStatus;
import com.bamisu.log.gameserver.manager.ServerManager;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.element.entities.ElementVO;
import com.bamisu.log.gameserver.module.characters.kingdom.entities.KingdomVO;
import com.bamisu.log.gameserver.module.characters.summon.entities.BonusSummonVO;
import com.bamisu.log.gameserver.module.characters.summon.entities.KingdomSummonVO;
import com.bamisu.log.gameserver.module.characters.summon.entities.SummonVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.entities.CountSummonInfo;
import com.bamisu.log.gameserver.module.hero.exception.IndexOutOfBoundByChangeConfigException;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserSummonHeroModel extends DataModel {

    public long uid;
    public int bonusPoint;
    public String idBonus;
    public int elementIndex;
    public int kingdomIndex;
    public Map<String, Integer> timeSave = new HashMap<>();
    public int timeStamp;

    //Kich ban trieu hoi gioi han
    public long countSummon = 0;

    private Object lockBonus = new Object();




    private void init(Zone zone){
        ServerVariableModel server = ServerManager.getInstance().getServerVariableModel(zone);
        elementIndex = server.readElementDay(zone);
        kingdomIndex = server.readKingdomDay(zone);
        timeStamp = server.timeStamp;

        //Bonus hien tai
        List<BonusSummonVO> bonus = CharactersConfigManager.getInstance().getBonusSummonConfig();
        if(bonus.size() > 0){
            idBonus = bonus.get(0).idChest;
        }

        initBanner();
    }
    private void initBanner(){
        for(SummonVO summonVO : CharactersConfigManager.getInstance().getSummonConfig().listSummonBanner){
            if(summonVO.timeFree > 0) {
                timeSave.put(summonVO.id, Utils.getTimestampInSecond());
            }
        }
    }
    private void initBanner(String idBanner){
        for(SummonVO summonVO : CharactersConfigManager.getInstance().getSummonConfig().listSummonBanner){
            if(summonVO.timeFree > 0 && summonVO.id.equals(idBanner)) {
                timeSave.put(summonVO.id, Utils.getTimestampInSecond());
            }
        }
    }

    public static UserSummonHeroModel createUserSummonHeroModel(long uid, Zone zone){
        UserSummonHeroModel userSummonHeroModel = new UserSummonHeroModel();
        userSummonHeroModel.uid = uid;
        userSummonHeroModel.init(zone);
        userSummonHeroModel.saveToDB(zone);

        return userSummonHeroModel;
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

    public static UserSummonHeroModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserSummonHeroModel copyFromDBtoObject(String uId, Zone zone) {
        UserSummonHeroModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserSummonHeroModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserSummonHeroModel.class);
                if (pInfo != null) {
//                    pInfo.wrNiteLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*-----------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------*/
    /**
     * Tang diem bonus
     */
    public boolean updateBonusPoint(int count, Zone zone){
        if(count <= 0){
            return false;
        }
        synchronized (lockBonus){
            bonusPoint += count;
            return saveToDB(zone);
        }
    }

    /**
     * Have Bonus
     * kiem tra xem co dc bonus + update diem tich luy + update bonus nhan dc neu co bonus
     * @param zone
     * @return List id chest summon bonus
     */
    public List<BonusSummonVO> getBonus(Zone zone){
        List<BonusSummonVO> listBonusCf = CharactersConfigManager.getInstance().getBonusSummonConfig();
        if(listBonusCf.size() <= 0){
            return null;
        }
        BonusSummonVO bonus = CharactersConfigManager.getInstance().getBonusSummonConfig(idBonus);
        if(bonusPoint < bonus.cost){
            return new ArrayList<>();
        }
        //List phan thuong
        List<BonusSummonVO> listBonus = new ArrayList<>();

        //Synchro -> ngan ngua bonus thay doi khi dang cap nhat bonus
        // for i(bat dau tu index list bonus config) duyet den het mang list bonus config
        // bonusPoint tru qua cac index va add vao listBonus
        // neu bonusPoint < bonusPoint cua index config -> return
        // khi chay het mang -> gan i = phan tu dau tien -> cho chay lai -> den khi bonusPoint < bonusPoint trong config
        synchronized (lockBonus){
            boolean flat = true;
            while (flat){
                for(int i = listBonusCf.indexOf(bonus); i < listBonusCf.size(); i++){
                    if(bonusPoint < listBonusCf.get(i).cost){
                        idBonus = listBonusCf.get(i).idChest;
                        flat = false;
                        break;
                    }
                    bonusPoint -= listBonusCf.get(i).cost;
                    listBonus.add(listBonusCf.get(i));
                }
                if(!flat){
                    saveToDB(zone);
                    break;
                }
                bonus = listBonusCf.get(0);
            }
        }
        return listBonus;
    }


    /**
     * Get time create
     */
    public int getBannerFreeTime(String idBanner){
        Integer time = timeSave.get(idBanner);
        if(time == null){
            initBanner(idBanner);
        }
        return timeSave.get(idBanner);
    }

    /**
     * Summon free --- Save if true / Ko create if false
     */
    public boolean summonBannerFree(String idBanner, Zone zone){
        int now = Utils.getTimestampInSecond();
        int timeBannerFree = CharactersConfigManager.getInstance().getDistanceTimeSummonFreeConfig(idBanner);
        if(timeBannerFree <= 0){
            return false;
        }
        int time = timeBannerFree - (now - getBannerFreeTime(idBanner));
        if(time <= 0){
            timeSave.put(idBanner, now);
            return saveToDB(zone);
        }
        return false;
    }

    /**
     * Get Element User
     * @param zone
     * @return
     */
    public int readElementDay(Zone zone){
        if(elementIndex >= CharactersConfigManager.getInstance().getElementConfig().size()){
            elementIndex = 0;
            new IndexOutOfBoundByChangeConfigException().printStackTrace();
            saveToDB(zone);
        }

        if(isNewDay()){
            updateNewDay(zone);
        }
        return elementIndex;
    }

    /**
     * Change Element User
     * @param idElement
     * @return
     */
    public boolean updateElementDay(String idElement, Zone zone){
        List<ElementVO> listCf = CharactersConfigManager.getInstance().getElementConfig();
        if(listCf.get(elementIndex).id.equals(idElement)){
            return false;
        }
        for(int i = 0; i < listCf.size(); i++){
            if(listCf.get(i).id.equals(idElement)){
                elementIndex = i;
                return saveToDB(zone);
            }
        }
        return false;
    }

    /**
     * Get Kingdom User
     * @param zone
     * @return
     */
    public int readKingdomDay(Zone zone){
        if(kingdomIndex >= CharactersConfigManager.getInstance().getKingdomSummonConfig().size()){
            kingdomIndex = 0;
            new IndexOutOfBoundByChangeConfigException().printStackTrace();
            saveToDB(zone);
        }

        if(isNewDay()){
            updateNewDay(zone);
        }
        return kingdomIndex;
    }

    /**
     * Change Element User
     * @param idKingdom
     * @param zone
     * @return
     */
    public boolean updateKingdomDay(String idKingdom, Zone zone){
        //Loc cac king dc dc active
        List<KingdomSummonVO> listCf = CharactersConfigManager.getInstance().getKingdomSummonConfig();
        if(listCf.get(kingdomIndex).id.equals(idKingdom)){
            return false;
        }
        for(int i = 0; i < listCf.size(); i++){
            if(listCf.get(i).id.equals(idKingdom)){
                kingdomIndex = i;
                return saveToDB(zone);
            }
        }
        return false;
    }


    private void updateNewDay(Zone zone){
        ServerVariableModel serverVariableModel = ServerManager.getInstance().getServerVariableModel(zone);

        elementIndex = serverVariableModel.readElementDay(zone);
        kingdomIndex = serverVariableModel.readKingdomDay(zone);
        timeStamp = Utils.getTimestampInSecond();
        saveToDB(zone);
    }

    /**
     * Check qua ngay moi
     * @return
     */
    private boolean isNewDay(){
        return Utils.isNewDay(timeStamp);
    }

    public boolean updateCountSummon(int count, Zone zone){
        countSummon += count;
        return saveToDB(zone);
    }
}
