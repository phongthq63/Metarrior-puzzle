package com.bamisu.log.gameserver.module.social;

import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.log.gameserver.module.campaign.entities.HeroPackage;
import com.bamisu.log.gameserver.module.characters.entities.Celestial;
import com.bamisu.log.gameserver.module.characters.entities.Hero;
import com.bamisu.log.gameserver.module.characters.entities.Sage;
import com.bamisu.log.gameserver.module.friends.FriendManager;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.hero.exception.InvalidUpdateTeamException;
import com.bamisu.log.gameserver.module.ingame.FightingCreater;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingType;
import com.bamisu.log.gameserver.module.mage.MageManager;
import com.bamisu.log.gameserver.module.social.rec.RecSocialBattle;
import com.bamisu.log.gameserver.module.social.send.SendOthersProfile;
import com.bamisu.log.gameserver.module.user.cmd.receive.RecGetOthersProfile;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
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

/**
 * Create by Popeye on 10:33 AM, 5/8/2020
 */
public class SocialHandler extends ExtensionBaseClientRequestHandler {
    public SocialHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_SOCIAL;
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_GET_OTHERS_PROFILE:
                handleGetOthersProfile(user, data);
                break;
            case CMD.CMD_SOCIAL_BATTLE:   //giao đấu với người khác
                handleSocialBattle(user, data);
                break;
        }
    }

    /**
     * giao đấu với người khác
     */
    @WithSpan
    private void handleSocialBattle(User user, ISFSObject data) {
        RecSocialBattle recSocialBattle = new RecSocialBattle(data);
        long uid = extension.getUserManager().getUserModel(user).userID;

        //update team tower
        Zone zone = getParentExtension().getParentZone();
        try {
            HeroManager.getInstance().doUpdateTeamHero(zone, uid, ETeamType.PVP_OFFLINE.getId(), recSocialBattle.update);
        } catch (InvalidUpdateTeamException e) {
            e.printStackTrace();
            return;
        }

        //update sage skill
        SageSkillModel sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
        sageSkillModel.updateCurrentSkill(zone, recSocialBattle.sageSkill);

        //create tower fighting room
        CreateRoomSettings cfg = new CreateRoomSettings();
        cfg.setName(Utils.ranStr(10));
        cfg.setGroupId(ConfigHandle.instance().get(Params.SOCIAL_ROOM_GROUP_ID));
        cfg.setMaxUsers(1);
        cfg.setDynamic(true);
        cfg.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);
        cfg.setGame(true);

        Map<Object, Object> props = new HashMap<>();
        props.put(Params.UID, uid);
        props.put(Params.WIN_CONDITIONS, Arrays.asList("WCO001"));

        //player team
        List<Hero> team = Hero.getPlayerTeam(uid, ETeamType.PVP_OFFLINE, zone, false);
        HeroPackage heroPackage = new HeroPackage(team);
        props.put(Params.PLAYER_TEAM, Utils.toJson(heroPackage));

        //sage
        Sage sage = Sage.createMage(zone, uid);
        props.put(Params.SAGE, Utils.toJson(sage));

        //celestial
        Celestial celestial = Celestial.createCelestial(zone, uid);
        props.put(Params.CELESTIAL, Utils.toJson(celestial));

        //enemy team
        List<Hero> enemyTeam = Hero.getPlayerTeam(recSocialBattle.enemyUserID, ETeamType.PVP_OFFLINE_DEFENSE, zone, true);
        for(int i = enemyTeam.size(); i < 5; i++){
            enemyTeam.add(null);
        }
        props.put(Params.NPC_TEAM, Utils.toJson(enemyTeam));

        //enemy sage
        Sage enemySage = Sage.createMage(zone, recSocialBattle.enemyUserID);
        props.put(Params.ENEMY_SAGE, Utils.toJson(enemySage));

        //enemy celestial
        Celestial enemyCelestial = Celestial.createCelestial(zone, recSocialBattle.enemyUserID);
        props.put(Params.ENEMY_CELESTIAL, Utils.toJson(enemyCelestial));

        cfg.setRoomProperties(props);

        List<RoomVariable> roomVariableList = new ArrayList<>();
        roomVariableList.add(new SFSRoomVariable(Params.TYPE, FightingType.PvM.getType()));
        roomVariableList.add(new SFSRoomVariable(Params.FUNCTION, EFightingFunction.PvP_FRIEND.getIntValue()));
        cfg.setRoomVariables(roomVariableList);
        try {
            FightingCreater.creatorFightingRoom(zone, user, cfg);
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }
    }

    @WithSpan
    private void handleGetOthersProfile(User user, ISFSObject data) {
        RecGetOthersProfile rec = new RecGetOthersProfile(data);

        SendOthersProfile sendOthersProfile = new SendOthersProfile();
        sendOthersProfile.zone = getParentExtension().getParentZone();
        sendOthersProfile.userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(rec.uid);

        GuildModel guildModel = GuildManager.getInstance().getGuildModelByUserID(sendOthersProfile.userModel.userID, getParentExtension().getParentZone());
        if(guildModel != null){
            sendOthersProfile.alliance = guildModel.gName;
        }
        sendOthersProfile.guild = guildModel;

        UserModel um = extension.getUserManager().getUserModel(user);
        sendOthersProfile.isFriend = FriendManager.getInstance().checkIsFriend(um.userID, user.getZone(), rec.uid);
        sendOthersProfile.primaryHeroes = HeroManager.getInstance().getTeamStrongestUserHeroModel(sendOthersProfile.userModel.userID, getParentExtension().getParentZone());
        send(sendOthersProfile, user);
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_SOCIAL, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_SOCIAL, this);
    }
}
