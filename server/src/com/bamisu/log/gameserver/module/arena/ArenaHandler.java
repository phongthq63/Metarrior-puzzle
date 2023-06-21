package com.bamisu.log.gameserver.module.arena;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.arena.UserArenaModel;
import com.bamisu.log.gameserver.datamodel.arena.UserFightArenaModel;
import com.bamisu.log.gameserver.datamodel.arena.entities.RecordArenaInfo;
import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserBlessingHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.log.gameserver.module.arena.cmd.rec.RecBuyTicketArena;
import com.bamisu.log.gameserver.module.arena.cmd.rec.RecFightArena;
import com.bamisu.log.gameserver.module.arena.cmd.send.*;
import com.bamisu.log.gameserver.module.arena.config.entities.BuyArenaVO;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.campaign.entities.HeroPackage;
import com.bamisu.log.gameserver.module.characters.entities.Celestial;
import com.bamisu.log.gameserver.module.characters.entities.Hero;
import com.bamisu.log.gameserver.module.characters.entities.Sage;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.hero.entities.HeroPosition;
import com.bamisu.log.gameserver.module.hero.exception.InvalidUpdateTeamException;
import com.bamisu.log.gameserver.module.ingame.FightingCreater;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingType;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.*;
import java.util.stream.Collectors;

public class ArenaHandler extends ExtensionBaseClientRequestHandler {
    PvPOnlineQueue pvPOnlineQueue = null;
    public ArenaHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_ARENA;
        pvPOnlineQueue = new PvPOnlineQueue(ArenaManager.getInstance(), this);

        ArenaManager.getInstance().startThreadArena(extension.getParentZone());
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_LOAD_SCENE_ARENA:
                doLoadSceneArena(user, data);
                break;
            case CMD.CMD_GET_LIST_FIGHT_ARENA:
                doGetListFightArena(user, data);
                break;
            case CMD.CMD_REFRESH_LIST_FIGHT_ARENA:
                doRefreshListFightArena(user, data);
                break;
            case CMD.CMD_BUY_TICKET_ARENA:
                doBuyTicketArena(user, data);
                break;
            case CMD.CMD_FIGHT_ARENA:
                doFightArena(user, data);
                break;
            case CMD.CMD_LOAD_SCENE_TEAM_HERO_ARENA:
                doLoadSceneTeamHeroArena(user, data);
                break;
            case CMD.CMD_GET_LIST_RECORD_ARENA:
                doGetListRecordArena(user, data);
                break;
            case CMD.CMD_CHALLENGE_PvP_ONLINE:
                doChallengePvPOnline(user, data);
                break;
        }
    }

    private void doChallengePvPOnline(User user, ISFSObject data) {
        ArenaManager.getInstance().challengePvPOnLine(this, user, data);
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_ARENA, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_ARENA, this);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneArena(User user, ISFSObject data) {
        UserModel userModel = extension.getUserManager().getUserModel(user);
        UserArenaModel userArenaModel = ArenaManager.getInstance().getUserArenaModel(userModel.userID, getParentExtension().getParentZone());
        //Active nguoi choi
        ArenaManager.getInstance().activeUser(userArenaModel, getParentExtension().getParentZone());
//        Long userRank = ((ZoneExtension)getParentExtension().getParentZone().getExtension()).getRedisController().getJedis().zrevrank("arena_leaderboard_" + userArenaModel.readSeason(), String.valueOf(userModel.userID));
        Long userRank = 0L;

        SendLoadSceneArena objPut = new SendLoadSceneArena();
        objPut.userModel = userModel;
        objPut.userRank = userRank;
        objPut.rank = ArenaManager.getInstance().getListTopRankArena(0, 100, getParentExtension().getParentZone());
        objPut.userManager = extension.getUserManager();
        objPut.userArenaModel = userArenaModel;
        objPut.userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(userModel.userID, getParentExtension().getParentZone());
        objPut.team = HeroManager.getInstance().getListMainHashHero(getParentExtension().getParentZone(), userModel.userID, ETeamType.ARENA_DEFENSE, true);
        objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(userModel.userID, getParentExtension().getParentZone()).parallelStream().
                map(HeroModel::createByHeroModel).
                collect(Collectors.toList());
        objPut.userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(userModel.userID, getParentExtension().getParentZone());
        objPut.timeEndSeason = ArenaManager.getInstance().getTimeEndSeason(getParentExtension().getParentZone());
        objPut.zone = getParentExtension().getParentZone();
        send(objPut, user);
    }


    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetListFightArena(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        SendGetListFightArena objPut = new SendGetListFightArena();
        objPut.userManager = extension.getUserManager();
        objPut.enemy = ArenaManager.getInstance().getListEnemyArena(uid, getParentExtension().getParentZone());
        send(objPut, user);
    }


    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doRefreshListFightArena(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        UserFightArenaModel userFightArenaModel = ArenaManager.getInstance().getUserFightArenaModel(uid, getParentExtension().getParentZone());

        //Kiem tra du time
        if(!ArenaManager.getInstance().canRefreshListEnemyArena(userFightArenaModel, getParentExtension().getParentZone())){
            SendRefreshListFightArena objPut = new SendRefreshListFightArena(ServerConstant.ErrorCode.ERR_REFRESH_ARENA_TO_FAST);
            send(objPut, user);
            return;
        }

        //Refresh
        if(!ArenaManager.getInstance().refreshListEnemyArena(userFightArenaModel, getParentExtension().getParentZone())){
            SendRefreshListFightArena objPut = new SendRefreshListFightArena(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendRefreshListFightArena objPut = new SendRefreshListFightArena();
        objPut.userManager = extension.getUserManager();
        objPut.enemy = ArenaManager.getInstance().getListEnemyArena(uid, getParentExtension().getParentZone());
        send(objPut, user);
    }


    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doBuyTicketArena(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecBuyTicketArena objGet = new RecBuyTicketArena(data);

        if(objGet.count < 1){
            SendBuyTicketArena objPut = new SendBuyTicketArena(ServerConstant.ErrorCode.ERR_INVALID_VALUE);
            send(objPut, user);
            return;
        }

        BuyArenaVO buyArenaCf = ArenaManager.getInstance().getBuyArenaFightConfig();
        //Tieu tai nguyen
        if(!BagManager.getInstance().addItemToDB(
                buyArenaCf.readCost().parallelStream().map(obj -> new ResourcePackage(obj.id, -obj.amount * objGet.count)).collect(Collectors.toList()),
                uid,
                getParentExtension().getParentZone(),
                UserUtils.TransactionType.BUY_ARENA_TICKET)){
            SendBuyTicketArena objPut = new SendBuyTicketArena(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(objPut, user);
            return;
        }

        //Nhan tai nguyen
        if(!BagManager.getInstance().addItemToDB(
                buyArenaCf.readReward().parallelStream().map(obj -> new ResourcePackage(obj.id, obj.amount * objGet.count)).collect(Collectors.toList()),
                uid,
                getParentExtension().getParentZone(),
                UserUtils.TransactionType.BUY_ARENA_TICKET)){
            SendBuyTicketArena objPut = new SendBuyTicketArena(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendBuyTicketArena objPut = new SendBuyTicketArena();
        send(objPut, user);
    }


    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doFightArena(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        UserArenaModel userArenaModel = ArenaManager.getInstance().getUserArenaModel(uid, getParentExtension().getParentZone());

        //Kiem tra ket thuc mua giai
        if(ArenaManager.getInstance().isEndSeason(getParentExtension().getParentZone())){
            SendFightArena objPut = new SendFightArena(ServerConstant.ErrorCode.ERR_END_SEASON_ARENA);
            send(objPut, user);
            return;
        }

        //Kiem tra so lan con lai
        if(ArenaManager.getInstance().getFreeArenaFight(userArenaModel, getParentExtension().getParentZone()) <= 0 &&
                !BagManager.getInstance().addItemToDB(ArenaManager.getInstance().getCostFightArena().parallelStream().map(obj -> new ResourcePackage(obj.id, -obj.amount)).collect(Collectors.toList()), uid, getParentExtension().getParentZone(), UserUtils.TransactionType.FIGHT_ARENA)){
            SendFightArena objPut = new SendFightArena(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(objPut, user);
            return;
        }

        if(!ArenaManager.getInstance().increaseArenaFight(userArenaModel, getParentExtension().getParentZone())){
            SendFightArena objPut = new SendFightArena(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        RecFightArena objGet = new RecFightArena(data);
        List<Long> listEnemy = ArenaManager.getInstance().getListEnemyArena(uid, getParentExtension().getParentZone());
        //Kiem tra dung doi thu khong
        if(!listEnemy.contains(objGet.uid)){
            SendFightArena objPut = new SendFightArena(ServerConstant.ErrorCode.ERR_WRONG_ANEMY_MATCH_ARENA);
            send(objPut, user);
            return;
        }

        //tạo phòng
        createFightingRoom(user, uid, objGet.uid, objGet.update, objGet.sageSkill, ArenaManager.getInstance().getRewardWinArena());
    }

    @WithSpan
    private void createFightingRoom(User user, long uid, long enemyUserID, List<HeroPosition> update, Collection<String> sageSkill, List<ResourcePackage> rewardWinArena) {
        //update team tower
        Zone zone = getParentExtension().getParentZone();
        try {
            HeroManager.getInstance().doUpdateTeamHero(zone, uid, ETeamType.ARENA.getId(), update);
        } catch (InvalidUpdateTeamException e) {
            e.printStackTrace();
            return;
        }

        //update sage skill
        SageSkillModel sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
        sageSkillModel.updateCurrentSkill(zone, sageSkill);

        //create tower fighting room
        CreateRoomSettings cfg = new CreateRoomSettings();
        cfg.setName("arena" + Utils.ranStr(10));
        cfg.setGroupId(ConfigHandle.instance().get("arena_fight"));
        cfg.setMaxUsers(1);
        cfg.setDynamic(true);
        cfg.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);
        cfg.setGame(true);

        Map<Object, Object> props = new HashMap<>();
        props.put(Params.UID, uid);
        props.put(Params.WIN_CONDITIONS, Arrays.asList("WCO001"));
        props.put(Params.ENEMY_ID, enemyUserID);
        props.put(Params.REWARD, Utils.toJson(rewardWinArena));

        //player team
        List<Hero> team = Hero.getPlayerTeam(uid, ETeamType.ARENA, zone, false);
        HeroPackage heroPackage = new HeroPackage(team);
        props.put(Params.PLAYER_TEAM, Utils.toJson(heroPackage));

        //sage
        Sage sage = Sage.createMage(zone, uid);
        props.put(Params.SAGE, Utils.toJson(sage));

        //celestial
        Celestial celestial = Celestial.createCelestial(zone, uid);
        props.put(Params.CELESTIAL, Utils.toJson(celestial));

        //enemy team
        List<Hero> enemyTeam = Hero.getPlayerTeam(enemyUserID, ETeamType.ARENA_DEFENSE, zone, true);
        for(int i = enemyTeam.size(); i < 5; i++){
            enemyTeam.add(null);
        }
        props.put(Params.NPC_TEAM, Utils.toJson(enemyTeam));

        //enemy sage
        Sage enemySage = Sage.createMage(zone, enemyUserID);
        props.put(Params.ENEMY_SAGE, Utils.toJson(enemySage));

        //enemy celestial
        Celestial enemyCelestial = Celestial.createCelestial(zone, enemyUserID);
        props.put(Params.ENEMY_CELESTIAL, Utils.toJson(enemyCelestial));

        cfg.setRoomProperties(props);

        List<RoomVariable> roomVariableList = new ArrayList<>();
        roomVariableList.add(new SFSRoomVariable(Params.TYPE, FightingType.PvM.getType()));
        roomVariableList.add(new SFSRoomVariable(Params.FUNCTION, EFightingFunction.PvP_ARENA.getIntValue()));
        cfg.setRoomVariables(roomVariableList);
        try {
            FightingCreater.creatorFightingRoom(zone, user, cfg);
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneTeamHeroArena(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        UserAllHeroModel userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, getParentExtension().getParentZone());
        UserBlessingHeroModel userBlessingHeroModel = HeroManager.BlessingManager.getInstance().getUserBlessingHeroModel(uid, getParentExtension().getParentZone());

        SendLoadSceneTeamHeroArena objPut = new SendLoadSceneTeamHeroArena();
        objPut.team = HeroManager.getInstance().getListMainHashHero(getParentExtension().getParentZone(), uid, ETeamType.ARENA_DEFENSE, false);
        objPut.userAllHeroModel = userAllHeroModel;
        objPut.userBlessingHeroModel = userBlessingHeroModel;
        objPut.zone = getParentExtension().getParentZone();
        objPut.listHeroModel = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone()).parallelStream().
                map(HeroModel::createByHeroModel).
                collect(Collectors.toList());
        send(objPut, user);
    }


    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetListRecordArena(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        List<RecordArenaInfo> record = ArenaManager.getInstance().getListRecordArena(uid, getParentExtension().getParentZone());

        SendGetListRecordArena objPut = new SendGetListRecordArena();
        objPut.list = record;
        objPut.userManager = extension.getUserManager();
        send(objPut, user);
    }

    public PvPOnlineQueue getPvPOnlineQueue() {
        return pvPOnlineQueue;
    }
}
