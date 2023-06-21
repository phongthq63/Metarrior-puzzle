package com.bamisu.log.gameserver.datamodel.hero;

import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroSlotVO;
import com.bamisu.log.gameserver.module.friends.FriendHeroManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.hero.exception.InvalidPositionInTeamException;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Create by Popeye on 11:33 AM, 10/30/2019
 */
public class UserMainHeroModel extends DataModel {
    public long uid;
    public List<HeroSlotVO> listMainHeroCampaign;
    public List<HeroSlotVO> listMainHeroTower;
    public List<HeroSlotVO> listMainHeroMission;
    public List<HeroSlotVO> listMainHeroHunt;
    public List<HeroSlotVO> listMainHeroPvPOffline;
    public List<HeroSlotVO> listMainHeroDarkGate;
    public List<HeroSlotVO> listMainHeroArena;
    public List<HeroSlotVO> listMainHeroArenaDefense;

    private final Object lockCampaign = new Object();
    private final Object lockTower = new Object();
    private final Object lockMission = new Object();
    private final Object lockHunt = new Object();
    private final Object lockPvPOff = new Object();
    private final Object lockDarkGate = new Object();
    private final Object lockArena = new Object();





    private void initHero(){

    }
    

    public static UserMainHeroModel createUserMainHeroModel(long uid, Zone zone){
        UserMainHeroModel userMainHeroModel = new UserMainHeroModel();
        userMainHeroModel.uid = uid;
        userMainHeroModel.listMainHeroCampaign = CharactersConfigManager.getInstance().getHeroSlotConfig();
        userMainHeroModel.listMainHeroTower = CharactersConfigManager.getInstance().getHeroSlotConfig();
        userMainHeroModel.listMainHeroMission = CharactersConfigManager.getInstance().getHeroSlotConfig();
        userMainHeroModel.listMainHeroHunt = CharactersConfigManager.getInstance().getHeroSlotConfig();
        userMainHeroModel.listMainHeroPvPOffline = CharactersConfigManager.getInstance().getHeroSlotConfig();
        userMainHeroModel.listMainHeroDarkGate = CharactersConfigManager.getInstance().getHeroSlotConfig();
        userMainHeroModel.listMainHeroArena = CharactersConfigManager.getInstance().getHeroSlotConfig();
        userMainHeroModel.listMainHeroArenaDefense = CharactersConfigManager.getInstance().getHeroSlotConfig();

        userMainHeroModel.initHero();
        userMainHeroModel.saveToDB(zone);

        return userMainHeroModel;
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

    public static UserMainHeroModel copyFromDBtoObject(long uId, Zone zone) {
        UserMainHeroModel userMainHeroModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if(userMainHeroModel == null){
            userMainHeroModel = createUserMainHeroModel(uId, zone);
        }
        return userMainHeroModel;
    }

    private static UserMainHeroModel copyFromDBtoObject(String uId, Zone zone) {
        UserMainHeroModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserMainHeroModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserMainHeroModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Lay hero theo vi tri
     */
    public HeroSlotVO getSlotDependPosition(List<HeroSlotVO> team, int position){
        return team.get(position);
    }

    /**
     * Lay vi tri hero theo hash
     */
    public int getPositionHeroModel(List<HeroSlotVO> team, String hash){
        for(int i = 0; i < team.size(); i++){
            if(team.get(i).haveLock() && team.get(i).hashHero.equals(hash)){
                return i;
            }
        }

        try {
            throw new InvalidPositionInTeamException();
        } catch (InvalidPositionInTeamException e) {
            e.printStackTrace();
        }
        return -1;
    }


    private List<HeroSlotVO> readTeam(ETeamType teamType, Zone zone){
        List<HeroSlotVO> team = CharactersConfigManager.getInstance().getHeroSlotConfig();
        boolean haveSave = false;
        switch (teamType){
            case CAMPAIGN:
                synchronized (lockCampaign){
                    if(listMainHeroCampaign == null){
                        listMainHeroCampaign = team;
                        haveSave = true;
                    }else {
                        team = listMainHeroCampaign;
                    }
                }
                break;
            case TOWER:
                if(listMainHeroTower == null){
                    listMainHeroTower = team;
                    haveSave = true;
                }else {
                    team = listMainHeroTower;
                }
                break;
            case MISSION_OUTPOST:
                synchronized (lockMission){
                    if(listMainHeroMission == null){
                        listMainHeroMission = team;
                        haveSave = true;
                    }else {
                        team = listMainHeroMission;
                    }
                }
                break;
            case MONSTER_HUNT:
                synchronized (lockHunt){
                    if(listMainHeroHunt == null){
                        listMainHeroHunt = team;
                        haveSave = true;
                    }else {
                        team = listMainHeroHunt;
                    }
                }
                break;
            case PVP_OFFLINE:
                synchronized (lockPvPOff){
                    if(listMainHeroPvPOffline == null){
                        listMainHeroPvPOffline = team;
                        haveSave = true;
                    }else {
                        team = listMainHeroPvPOffline;
                    }
                }
                break;
            case DARK_GATE:
                synchronized (lockDarkGate){
                    if(listMainHeroDarkGate == null){
                        listMainHeroDarkGate = team;
                        haveSave = true;
                    }else {
                        team = listMainHeroDarkGate;
                    }
                }
                break;
            case ARENA:
                synchronized (lockArena){
                    if(listMainHeroArena == null){
                        listMainHeroArena = team;
                        haveSave = true;
                    }else {
                        team = listMainHeroArena;
                    }
                }
                break;
            case ARENA_DEFENSE:
                synchronized (lockArena){
                    if(listMainHeroArenaDefense == null){
                        listMainHeroArenaDefense = team;
                        haveSave = true;
                    }else {
                        team = listMainHeroArenaDefense;
                    }
                }
                break;
        }
        //Save lai neu thay doi
        if(haveSave)saveToDB(zone);

        return team;
    }
    public List<String> readHero(ETeamType teamType, Zone zone, boolean autoFill){
        //List Hash Hero Team
        List<String> listHash = getHero(readTeam(teamType, zone), zone);

        switch (teamType){
            case CAMPAIGN:
            case TOWER:
            case MISSION_OUTPOST:
            case MONSTER_HUNT:
            case ARENA:
                return listHash;
            case PVP_OFFLINE:
                if(!autoFill) return listHash;
                if(listHash.parallelStream().allMatch(obj -> obj == null ||obj.equals(""))){

                    return HeroManager.getInstance().getTeamStrongestUserHeroModel(uid, zone).parallelStream().
                            map(obj -> obj.hash).
                            collect(Collectors.toList());
                }else {
                    return listHash;
                }
            case PVP_OFFLINE_DEFENSE:
                return HeroManager.getInstance().getTeamStrongestUserHeroModel(uid, zone).parallelStream().
                        map(obj -> obj.hash).
                        collect(Collectors.toList());
            case ARENA_DEFENSE:
                synchronized (lockArena){
                    if(!autoFill) return listHash;
                    if(listHash.parallelStream().allMatch(obj -> obj == null || obj.equals(""))){
                        //Team phong thu = team cong
                        listHash = getHero(readTeam(ETeamType.ARENA, zone), zone);
                        if(listHash.parallelStream().allMatch(obj -> obj == null ||obj.equals(""))){
                            //Van rong
                            return HeroManager.getInstance().getTeamStrongestUserHeroModel(uid, zone).parallelStream().
                                    map(obj -> obj.hash).
                                    collect(Collectors.toList());
                        }
                    }else {
                        return listHash;
                    }
                }
            case DARK_GATE:
                return listHash;
        }
        return new ArrayList<>();
    }
    private List<String> getHero(List<HeroSlotVO> team, Zone zone){
        List<String> list = new ArrayList<>();
        for(HeroSlotVO slotVO : team){
            if(!slotVO.haveLock() || slotVO.hashHero.isEmpty()){
                list.add("");
            }else {
                list.add(slotVO.hashHero);
            }
        }

        AtomicBoolean haveSave = new AtomicBoolean(false);
        List<String> listHashHave = new ArrayList<>();
        listHashHave.addAll(HeroManager.getInstance().getHeroModel(uid, list, zone).parallelStream().map(obj -> obj.hash).collect(Collectors.toList()));
        listHashHave.addAll(FriendHeroManager.getInstance().getHeroInfo(list, uid, zone).parallelStream().map(obj -> obj.heroModel.hash).collect(Collectors.toList()));
        //Tim kiem hash khong ton tai
        list.parallelStream().forEach(obj -> {

            if(!listHashHave.contains(obj)){
                obj = "";
                haveSave.set(true);
            }

        });
        //Neu thay doi create vao data
        if(haveSave.get()) saveToDB(zone);

        return list;
    }

    public boolean updateHero(ETeamType teamType, List<String> listHashHero, Zone zone){
        switch (teamType){
            case CAMPAIGN:
                synchronized (lockCampaign){
                    updateHero(readTeam(teamType, zone), listHashHero);
                }
                break;
            case TOWER:
                updateHero(readTeam(teamType, zone), listHashHero);
                break;
            case MISSION_OUTPOST:
                synchronized (lockMission){
                    updateHero(readTeam(teamType, zone), listHashHero);
                }
                break;
            case MONSTER_HUNT:
                synchronized (lockHunt){
                    updateHero(readTeam(teamType, zone), listHashHero);
                }
                break;
            case PVP_OFFLINE:
                synchronized (lockPvPOff){
                    updateHero(readTeam(teamType, zone), listHashHero);
                }
                break;
            case DARK_GATE:
                synchronized (lockDarkGate){
                    updateHero(readTeam(teamType, zone), listHashHero);
                }
                break;
            case ARENA:
            case ARENA_DEFENSE:
                synchronized (lockArena){
                    updateHero(readTeam(teamType, zone), listHashHero);
                }
                break;
        }
        return saveToDB(zone);
    }
    private void updateHero(List<HeroSlotVO> team, List<String> listHashHero){
        if(team.size() != listHashHero.size()){
            return;
        }
        for(int i = 0; i < team.size(); i++){
            team.get(i).hashHero = listHashHero.get(i);
            if(listHashHero.get(i).isEmpty()){
                team.get(i).unlock();
            }else {
                team.get(i).lock();
            }
        }
    }

    public void deleteHero(ETeamType teamType, List<String> listHashHero, Zone zone){
        switch (teamType){
            case CAMPAIGN:
                synchronized (lockCampaign){
                    deleteHero(readTeam(teamType, zone), listHashHero);
                }
                break;
            case TOWER:
                deleteHero(readTeam(teamType, zone), listHashHero);
                break;
            case MISSION_OUTPOST:
                synchronized (lockMission){
                    deleteHero(readTeam(teamType, zone), listHashHero);
                }
                break;
            case MONSTER_HUNT:
                synchronized (lockHunt){
                    deleteHero(readTeam(teamType, zone), listHashHero);
                }
                break;
            case PVP_OFFLINE:
                synchronized (lockPvPOff){
                    deleteHero(readTeam(teamType, zone), listHashHero);
                }
                break;
            case DARK_GATE:
                synchronized (lockDarkGate){
                    deleteHero(readTeam(teamType, zone), listHashHero);
                }
                break;
            case ARENA:
            case ARENA_DEFENSE:
                synchronized (lockArena){
                    deleteHero(readTeam(teamType, zone), listHashHero);
                }
                break;
        }
    }
    private void deleteHero(List<HeroSlotVO> team, List<String> listHashHero){
        for(HeroSlotVO slot : team){
            if(listHashHero.contains(slot.hashHero)){
                slot.hashHero = "";
                slot.unlock();
            }
        }
    }
}
