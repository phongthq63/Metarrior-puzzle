package com.bamisu.log.gameserver.datamodel.hero;

import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.entities.ColorHero;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.defines.EGiftVip;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by Popeye on 11:33 AM, 10/30/2019
 */
public class UserAllHeroModel extends DataModel{
    public long uid;
    public List<HeroModel> listAllHeroModel = new ArrayList<>();
    public boolean firstReset = true;
    public byte countIncreateBag;
    public short sizeBag;
    private static short maxSizeBag = 500;

    public boolean haveRefreshLevelHight = true;
    public short level;
    public Set<String> setLevelHighest = new HashSet<>();       //Chua 5 hash cao level nhat  --------  Co che khong phai duyet nhieu lan

    public boolean autoRetire = false;
    public List<String> queueReset = new ArrayList<>();

    public short maxLevelUp = 1;


    private final Object lockHero = new Object();




    public UserAllHeroModel() {
    }

    public UserAllHeroModel(long uid, Zone zone) {
        this.uid = uid;
        init(zone);
    }

    private void init(Zone zone){
        //Khoi tao Hero
        initHero(zone);
        //Khoi tao team level cao nhat
        initTeamLevelHighest();
    }

    private void initHero(Zone zone){
        if(((ZoneExtension) zone.getExtension()).isTestServer()){
            HeroModel heroModel = null;
            for(HeroVO heroCf : CharactersConfigManager.getInstance().getHeroConfig()){
                heroModel = HeroModel.createHeroModel(uid, heroCf, EHeroType.NORMAL);
                heroModel.star = (short) ColorHero.RED_PLUS.getStar();
                heroModel.level = 240;
                listAllHeroModel.add(heroModel);
            }
        }else {
            HeroModel heroModel = null;
            heroModel = HeroModel.createHeroModel(uid, CharactersConfigManager.getInstance().getHeroConfig("T1040"), EHeroType.NORMAL);
            heroModel.star = (short) ColorHero.PURPLE.getStar();
            listAllHeroModel.add(heroModel);
            //DaoNQ for peromance test
//            heroModel = HeroModel.createHeroModel(uid, CharactersConfigManager.getInstance().getHeroConfig("T1039"), EHeroType.NORMAL);
//            heroModel.star = (short) ColorHero.PURPLE.getStar();
//            listAllHeroModel.add(heroModel);
//            heroModel = HeroModel.createHeroModel(uid, CharactersConfigManager.getInstance().getHeroConfig("T1038"), EHeroType.NORMAL);
//            heroModel.star = (short) ColorHero.PURPLE.getStar();
//            listAllHeroModel.add(heroModel);
            //end
            upLevelHero(heroModel.hash);

//            heroModel = HeroModel.createHeroModel(uid, CharactersConfigManager.getInstance().getHeroConfig("T1006"));
//            heroModel.star = (short) ColorHero.BLUE.getStar();
//            listAllHeroModel.add(heroModel);
//            upLevelHero(heroModel.hash);
//
//            heroModel = HeroModel.createHeroModel(uid, CharactersConfigManager.getInstance().getHeroConfig("T1018"));
//            heroModel.star = (short) ColorHero.BLUE.getStar();
//            listAllHeroModel.add(heroModel);
//            upLevelHero(heroModel.hash);
//
//            heroModel = HeroModel.createHeroModel(uid, CharactersConfigManager.getInstance().getHeroConfig("T1021"));
//            heroModel.star = (short) ColorHero.PURPLE.getStar();
//            listAllHeroModel.add(heroModel);
        }

        //khoi tao thu vien hero
        HeroManager.getInstance().createUserHeroCollectionModel(uid, listAllHeroModel.parallelStream().map(obj -> obj.id).collect(Collectors.toList()), zone);
    }

    private void initTeamLevelHighest(){
        if(!haveRefreshLevelHight){
            return;
        }
        List<HeroModel> listHighest = listAllHeroModel.parallelStream()
                .sorted((obj1, obj2) -> obj2.readLevel() - obj1.readLevel())
                .limit(5)
                .collect(Collectors.toList());
        if(listHighest.isEmpty())return;

        level = listHighest.get(listHighest.size() - 1).readLevel();
        setLevelHighest = listHighest.parallelStream().map(obj -> obj.hash).collect(Collectors.toSet());
        haveRefreshLevelHight = false;
    }

    public static UserAllHeroModel createUserAllHeroModel(long uid, Zone zone){
        UserAllHeroModel userAllHeroModel = new UserAllHeroModel(uid, zone);
        userAllHeroModel.countIncreateBag = 0;
        userAllHeroModel.sizeBag = CharactersConfigManager.getInstance().getBagHeroConfig().start;
        userAllHeroModel.saveToDB(zone);

        return userAllHeroModel;
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

    public static UserAllHeroModel copyFromDBtoObject(long uId, Zone zone) {
        UserAllHeroModel userAllHeroModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if(userAllHeroModel == null){
            userAllHeroModel = createUserAllHeroModel(uId, zone);
        }
        return userAllHeroModel;
    }

    private static UserAllHeroModel copyFromDBtoObject(String uId, Zone zone) {
        UserAllHeroModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserAllHeroModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserAllHeroModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*---------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * up level hero
     * @param hashHero
     * @return
     */
    public HeroModel upLevelHero(String hashHero){
        synchronized (lockHero){
            for(HeroModel heroModel : listAllHeroModel){
                if(heroModel.hash.equals(hashHero)){
                    //Luu tai nguyen da tieu vao database + level +1
                    heroModel.upLevel();

                    //Init lai 5 hero co level cao nhat
                    //Neu hero dc nang level khong trong 5 hero cao level cao nhat vao level moi cao hon level dc create
                    if(readLevelMin5Hero() < heroModel.readLevel()){
                        haveRefreshLevelHight = true;
                        initTeamLevelHighest();
                    }

                    if(heroModel.readLevel() > maxLevelUp){
                        maxLevelUp = heroModel.readLevel();
                    }

                    return heroModel;
                }
            }
        }
        return null;
    }


    public Set<String> readSetLevelHighest(Zone zone) {
        if(setLevelHighest.size() < 5){
            haveRefreshLevelHight = true;
        }
        if(haveRefreshLevelHight){

            synchronized (lockHero){
                initTeamLevelHighest();
            }

            saveToDB(zone);
        }
        return setLevelHighest;
    }

    public void saveQueueResetHero(String hashHero){
        queueReset.add(0, hashHero);
        queueReset = queueReset.stream().limit(2).collect(Collectors.toList());
    }


    /*---------------------------------------------------------------------------------------------------------------*/
    public boolean isFirstReset(){
        return firstReset;
    }

    public short readLevelMin5Hero(){
        return level;
    }

    public void firstReset(Zone zone){
        firstReset = false;
        saveToDB(zone);
    }

    public boolean switchAutoRetire(Zone zone){
        autoRetire = !autoRetire;
        return saveToDB(zone);
    }

    public boolean upSizeBagHero(int sum, Zone zone){
        if(readSizeBagHero(zone) + sum > maxSizeBag) return false;
        this.countIncreateBag++;
        this.sizeBag += sum;
        return saveToDB(zone);
    }

    public int readSizeBagHero(Zone zone){
        return sizeBag + VipManager.getInstance().getBonus(uid, zone, EGiftVip.MAX_HERO_SLOT);
    }

    public List<HeroModel> readListHero(){
        synchronized (lockHero){
            if(haveRefreshLevelHight){
                initTeamLevelHighest();
            }
            return listAllHeroModel;
        }
    }

    public int readMaxLevelUpHero(Zone zone) {
        if(maxLevelUp <= 1){
            boolean haveSave = false;

            synchronized (lockHero){
                for(HeroModel heroModel : listAllHeroModel){
                    if(heroModel.level > maxLevelUp){
                        maxLevelUp = heroModel.level;
                        haveSave = true;
                    }
                }

                if(haveSave) saveToDB(zone);
            }
        }
        return maxLevelUp;
    }
}
