package com.bamisu.log.gameserver.module.guild;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.guild.UserGuildModel;
import com.bamisu.log.gameserver.datamodel.guild.entities.GiftGuildInfo;
import com.bamisu.log.gameserver.entities.ExtensionClass;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.log.gameserver.module.guild.cmd.rec.*;
import com.bamisu.log.gameserver.module.guild.cmd.send.*;
import com.bamisu.log.gameserver.module.guild.define.EGuildAction;
import com.bamisu.log.gameserver.module.guild.define.EGuildSetting;
import com.bamisu.log.gameserver.module.guild.define.EGuildVerificationType;
import com.bamisu.log.gameserver.module.guild.entities.GuildSearchInfo;
import com.bamisu.log.gameserver.module.guild.config.entities.AvatarGuildVO;
import com.bamisu.log.gameserver.module.guild.config.entities.GuildVO;
import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.ValidateUtils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSJoinRoomException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GuildHandler extends ExtensionBaseClientRequestHandler {

    public GuildHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_GUILD;
    }

    public UserModel getUserModel(long uid){
        return extension.getUserManager().getUserModel(uid);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_LOAD_SCENE_GUILD_MAIN:
                doLoadSceneGuildMain(user, data);
                break;
            case CMD.CMD_GET_LIST_GUILD_INFO:
                doGetListGuildInfo(user, data);
                break;
            case CMD.CMD_CREATE_GUILD:
                doCreateGuild(user, data);
                break;
            case CMD.CMD_GET_GUILD_INFO:
                doGetGuildInfo(user, data);
                break;
            case CMD.CMD_LEAVE_GUILD:
                doLeaveGuild(user, data);
                break;
            case CMD.CMD_GET_REQUEST_JOIN_GUILD:
                doGetRequestJoinGuild(user, data);
                break;
            case CMD.CMD_REQUEST_JOIN_GUILD:
                doRequestJoinGuild(user, data);
                break;
            case CMD.CMD_EXECUTION_REQUEST_JOIN_GUILD:
                doExecutionRequestJoinGuild(user, data);
                break;
            case CMD.CMD_SETTING_GUILD:
                doSettingGuild(user, data);
                break;
            case CMD.CMD_CHANGE_OFFICE_GUILD:
                doChangeOfficeGuild(user, data);
                break;
            case CMD.CMD_CHECK_IN_GUILD:
                doCheckInGuild(user, data);
                break;
            case CMD.CMD_GET_LIST_GIFT_GUILD:
                doGetListGiftGuild(user, data);
                break;
            case CMD.CMD_CLAIM_GIFT_GUILD:
                doClaimGiftGuild(user, data);
                break;
            case CMD.CMD_REMOVE_GIFT_GUILD:
                doRemoveGiftGuild(user, data);
                break;
            case CMD.CMD_REMOVE_ALL_GIFT_GUILD:
                doRemoveAllGiftGuild(user, data);
                break;
        }
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {}

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_GUILD, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_GUILD, this);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Xay ra khi bam vao guild
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneGuildMain(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());

        SendLoadSceneGuildMain objPut = new SendLoadSceneGuildMain();
        objPut.userManager = extension.getUserManager();
        objPut.userGuild = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        objPut.haveGuild = GuildManager.getInstance().userInGuild(userGuildModel, getParentExtension().getParentZone());
        objPut.zone = getParentExtension().getParentZone();
        //TH user trong guild
        if(objPut.haveGuild){
            objPut.guildModel = GuildManager.getInstance().getGuildModel(userGuildModel.gid, getParentExtension().getParentZone());;
        }else {
            //TH user khong trong guild
            objPut.listModel = GuildManager.getInstance().getListGuildModel(getParentExtension().getParentZone());
        }
        send(objPut, user);
    }

    /**
     * Lay thong tin list guild ----- Load scene danh sach guild
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetListGuildInfo(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        //Kiem tra thoi gian
        if(!GuildManager.getInstance().canRefreshSearchGuild(uid, getParentExtension().getParentZone())){
            SendListGuildInfo objPut = new SendListGuildInfo(ServerConstant.ErrorCode.ERR_SEARCH_TOO_FAST);
            send(objPut, user);
            return;
        }

        RecGetListGuildInfo objGet = new RecGetListGuildInfo(data);
        List<GuildSearchInfo> listGuildModel;
        if(objGet.nameOrId == null || objGet.nameOrId.length() <= 0){
            //Lay toan bo list GUILD model
            listGuildModel = GuildManager.getInstance().getListGuildModel(getParentExtension().getParentZone());
        }else {
            //Lay theo thong tin tim kiem
            listGuildModel = GuildManager.getInstance().searchGuidModel(objGet.nameOrId, getParentExtension().getParentZone());
        }


        SendListGuildInfo objPut = new SendListGuildInfo();
        objPut.userManager = extension.getUserManager();
        objPut.listModel = listGuildModel;
        send(objPut, user);
    }

    /**
     * Tao Guild
     */
    @WithSpan
    public void doCreateGuild(User user, ISFSObject data){
        UserModel userModel = extension.getUserManager().getUserModel(user);

        RecCreateGuild objGet = new RecCreateGuild(data);
        //Kiem tra avatar co hop le
        //Avatar khi tao guild chi co cap 1
        AvatarGuildVO pattern = GuildManager.getInstance().getPatternGuildConfig(objGet.idPattern);
        AvatarGuildVO symbol = GuildManager.getInstance().getSymbolGuildConfig(objGet.idSymbol);
        if(pattern == null || symbol == null){
            SendCreateGuild objPut = new SendCreateGuild(ServerConstant.ErrorCode.ERR_NOT_EXSIST_AVATAR);
            send(objPut, user);
            return;
        }
        if(pattern.level != 1 || symbol.level != 1){
            SendCreateGuild objPut = new SendCreateGuild(ServerConstant.ErrorCode.ERR_INVALID_AVATAR);
            send(objPut, user);
            return;
        }

        //Kiem tra ten co hop le = validate
        if(!ValidateUtils.isDisplayName(objGet.name)){
            SendCreateGuild objPut = new SendCreateGuild(ServerConstant.ErrorCode.ERR_INVALID_NAME);
            send(objPut, user);
            return;
        }

        //Kiem tra setting verify config
        if(EGuildVerificationType.fromID(objGet.verify) == null){
            SendCreateGuild objPut = new SendCreateGuild(ServerConstant.ErrorCode.ERR_NOT_EXSIST_VERIFY_GUILD);
            send(objPut, user);
            return;
        }

        //Kiem tra setting power config
        if(GuildManager.getInstance().getPowerRequestGuildConfig(objGet.power) == null){
            SendCreateGuild objPut = new SendCreateGuild(ServerConstant.ErrorCode.ERR_NOT_EXSIST_REQUEST_POWER_GUILD);
            send(objPut, user);
            return;
        }

        //Kiem tra setting language
        if(!ValidateUtils.isLanguageID(objGet.language)){
            SendCreateGuild objPut = new SendCreateGuild(ServerConstant.ErrorCode.ERR_INVALID_LANGUAGE_ID);
            send(objPut, user);
            return;
        }

        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(userModel.userID, getParentExtension().getParentZone());
        //Tieu tai nguyen
        if(!BagManager.getInstance().
                addItemToDB(
                        GuildManager.getInstance().getResourceCreateGuld(userGuildModel.createGuild).parallelStream().map(obj -> new MoneyPackageVO(obj.id, -obj.amount)).collect(Collectors.toList()),
                        userModel.userID,
                        getParentExtension().getParentZone(),
                        UserUtils.TransactionType.CREATE_GUILD)){
            SendCreateGuild objPut = new SendCreateGuild(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            send(objPut, user);
            return;
        }

        //Tao guild model
        GuildModel guild = GuildManager.getInstance()
                .createGuildModel(objGet.idPattern, objGet.idSymbol, objGet.name, objGet.description, objGet.verify, objGet.power, objGet.language, userModel, userGuildModel, getParentExtension().getParentZone());


        //Event
        GameEventAPI.ariseGameEvent(EGameEvent.JOIN_GUILD, userGuildModel.uid, new HashMap<>(), getParentExtension().getParentZone());

        //Tao room guild
        List<GuildVO> guildCf = GuildManager.getInstance().getGuildConfig();
        CreateRoomSettings cfgRoomChannel = new CreateRoomSettings();
        cfgRoomChannel.setName(GuildManager.getInstance().getNameRoomGuild(guild.gId));
        cfgRoomChannel.setGroupId(Params.Module.MODULE_CHAT);
        cfgRoomChannel.setMaxUsers(guildCf.get(guildCf.size() - 1).member + 10);
        cfgRoomChannel.setDynamic(true);
        cfgRoomChannel.setGame(false);
        cfgRoomChannel.setAutoRemoveMode(SFSRoomRemoveMode.WHEN_EMPTY);

        List<RoomVariable> listVariableChannel = new ArrayList<>();
        listVariableChannel.add(new SFSRoomVariable(Params.TYPE, EChatType.GUILD.getId()));
        listVariableChannel.add(new SFSRoomVariable(Params.NAME, guild.gName));
        cfgRoomChannel.setRoomVariables(listVariableChannel);

        cfgRoomChannel.setExtension(new CreateRoomSettings.RoomExtensionSettings(getParentExtension().getParentZone().getName(), ExtensionClass.CHAT_EXT));
        try {
            Room roomGuild = ExtensionUtility.getInstance().createRoom(getParentExtension().getParentZone(), cfgRoomChannel, null, true, null);
            ExtensionUtility.getInstance().joinRoom(user, roomGuild);
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
            SendCreateGuild objPut = new SendCreateGuild(ServerConstant.ErrorCode.ERR_INVALID_NAME);
            send(objPut, user);
            return;
        } catch (SFSJoinRoomException e) {
            e.printStackTrace();
            SendCreateGuild objPut = new SendCreateGuild(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Tao guild thanh cong
        SendCreateGuild objPut = new SendCreateGuild();
        send(objPut, user);
    }

    /**
     * Get Guild Info
     */
    @WithSpan
    public void doGetGuildInfo(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecGetGuildInfo objGet = new RecGetGuildInfo(data);
        long gid = (objGet.gid != 0) ? objGet.gid : GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone()).gid;
        GuildModel guildModel = GuildManager.getInstance().getGuildModel(gid, getParentExtension().getParentZone());
        if(guildModel == null){
            SendGetGuildInfo objPut = new SendGetGuildInfo(ServerConstant.ErrorCode.ERR_NOT_EXSIST_GUILD);
            send(objPut, user);
            return;
        }

        SendGetGuildInfo objPut = new SendGetGuildInfo();
        objPut.guildModel = guildModel;
        objPut.userManager = extension.getUserManager();
        objPut.zone = getParentExtension().getParentZone();
        send(objPut, user);
    }

    /**
     * Thoat Guild
     */
    @WithSpan
    public void doLeaveGuild(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecLeaveGuild objGet = new RecLeaveGuild(data);
        long uidKick = (objGet.uid == 0) ? extension.getUserManager().getUserModel(user).userID : objGet.uid;
        User userKick = ExtensionUtility.getInstance().getUserById(uidKick);

        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        //Kiem tra user co trong guild khong
        if(!GuildManager.getInstance().userInGuild(userGuildModel, getParentExtension().getParentZone())){
            SendLeaveGuild objPut = new SendLeaveGuild(ServerConstant.ErrorCode.ERR_USER_NOT_IN_GUILD);
            send(objPut, user);
            return;
        }

        //Xem chuc vu trong guild kha thi khong
        //Quyen kick nguoi choi
        GuildModel guildModel = GuildManager.getInstance().getGuildModel(userGuildModel.gid, getParentExtension().getParentZone());
        if(objGet.uid != 0 && !GuildManager.getInstance().userHavePermission(uid, EGuildAction.KICK_MEMBER, guildModel)){
            SendLeaveGuild objPut = new SendLeaveGuild(ServerConstant.ErrorCode.ERR_NOT_HAVE_PERMISSION);
            send(objPut, user);
            return;
        }

        //Thoat guild
        if(!GuildManager.getInstance().leaveGuild(uidKick, getParentExtension().getParentZone(), objGet.uid != 0)){
            SendLeaveGuild objPut = new SendLeaveGuild(ServerConstant.ErrorCode.ERR_CURRENT_CAN_NOT_LEAVE_GUILD);
            send(objPut, user);
            return;
        }

        SendLeaveGuild objPut = new SendLeaveGuild();
        objPut.uid = uidKick;
        send(objPut, user);

        if(userKick != null){
            if(user != userKick){
                send(objPut, userKick);
            }
            ExtensionUtility.getInstance().leaveRoom(userKick, getParentExtension().getParentZone().getRoomByName(String.valueOf(guildModel.gId)));
        }
    }

    /**
     * Lay list request join guild
     */
    @WithSpan
    private void doGetRequestJoinGuild(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        //Kiem tra nguoi dung trong guild khong
        if(!GuildManager.getInstance().userInGuild(userGuildModel, getParentExtension().getParentZone())){
            SendGetRequestJoinGuild objPut = new SendGetRequestJoinGuild(ServerConstant.ErrorCode.ERR_USER_NOT_IN_GUILD);
            send(objPut, user);
            return;
        }

        GuildModel guildModel = GuildManager.getInstance().getGuildModel(userGuildModel.gid, getParentExtension().getParentZone());
        //Kiem tra quyen han nguoi dung
        if(!GuildManager.getInstance().userHavePermission(uid, EGuildAction.EXECUTE_REQUEST_JOIN_GUILD, guildModel)){
            SendGetRequestJoinGuild objPut = new SendGetRequestJoinGuild(ServerConstant.ErrorCode.ERR_NOT_HAVE_PERMISSION);
            send(objPut, user);
            return;
        }

        SendGetRequestJoinGuild objPut = new SendGetRequestJoinGuild();
        objPut.listUid = GuildManager.getInstance().getRequestJoinGuild(guildModel, getParentExtension().getParentZone());
        objPut.userManager = extension.getUserManager();
        objPut.zone = getParentExtension().getParentZone();
        send(objPut, user);
    }


    /**
     *  Guild Yeu cau vao guild
     * @param user
     * @param data
     */
    @WithSpan
    public void doRequestJoinGuild(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        //Kiem tra user da trong guild khong
        if(GuildManager.getInstance().userInGuild(userGuildModel, getParentExtension().getParentZone())){
            SendRequestJoinGuild objPut = new SendRequestJoinGuild(ServerConstant.ErrorCode.ERR_USER_IN_GUILD);
            send(objPut, user);
            return;
        }

        if(!userGuildModel.canJoinGuild()){
            SendRequestJoinGuild objPut = new SendRequestJoinGuild(ServerConstant.ErrorCode.ERR_CURRENT_CAN_NOT_JOIN_GUILD);
            send(objPut, user);
            return;
        }

        RecRequestJoinGuild objGet = new RecRequestJoinGuild(data);
        //Gui don xin thanh cong
        GuildModel guildModel = GuildManager.getInstance().getGuildModel(objGet.gid, getParentExtension().getParentZone());
        if(HeroManager.getInstance().getPower(uid, getParentExtension().getParentZone()) < guildModel.readRequestPower()){
            SendRequestJoinGuild objPut = new SendRequestJoinGuild(ServerConstant.ErrorCode.ERR_NOT_ENOUGHT_REQUEST_GUILD);
            send(objPut, user);
            return;
        }
        if(GuildManager.getInstance().requestJoinGuildModel(uid, guildModel, getParentExtension().getParentZone())){
            SendRequestJoinGuild objPut = new SendRequestJoinGuild();
            send(objPut, user);
            return;
        }

        //TH khong thanh cong tu dong vao guild
        doUserJoinGuild(userGuildModel, guildModel);
    }


    /**
     * Xu ly yeu cau vao guild
     * @param user
     * @param data
     */
    @WithSpan
    private void doExecutionRequestJoinGuild(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        //Nguoi xu ly tin nhan
        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        if(!GuildManager.getInstance().userInGuild(userGuildModel, getParentExtension().getParentZone())){
            SendExecutionRequestJoinGuild objPut = new SendExecutionRequestJoinGuild(ServerConstant.ErrorCode.ERR_USER_NOT_IN_GUILD);
            send(objPut, user);
            return;
        }

        GuildModel guildModel = GuildManager.getInstance().getGuildModel(userGuildModel.gid, getParentExtension().getParentZone());
        //Kiem tra quyen nguoi dung
        if(!GuildManager.getInstance().userHavePermission(uid, EGuildAction.EXECUTE_REQUEST_JOIN_GUILD, guildModel)){
            SendExecutionRequestJoinGuild objPut = new SendExecutionRequestJoinGuild(ServerConstant.ErrorCode.ERR_NOT_HAVE_PERMISSION);
            send(objPut, user);
            return;
        }

        RecExecutionRequestJoinGuild objGet = new RecExecutionRequestJoinGuild(data);
        //Xu ly user vao guild tu request
        //TH chap nhan cho vao guild
        if(objGet.accept && !objGet.all){
            UserGuildModel userJoinGuild = GuildManager.getInstance().getUserGuildModel(objGet.uid, getParentExtension().getParentZone());
            doUserJoinGuild(userJoinGuild, guildModel);
        }else
            //TH chap nhan tat ca request vao guild
        if(objGet.accept && objGet.all){
            List<UserGuildModel> listModel = GuildManager.getInstance().getListUserGuildModel(
                            GuildManager.getInstance().getRequestJoinGuild(guildModel, getParentExtension().getParentZone()), getParentExtension().getParentZone());
            doUserJoinGuild(listModel, guildModel);
        }

        //Xu ly request xin vao guild
        //Xoa request da duyet
        if(!GuildManager.getInstance().removeRequestJoinGuild(objGet.uid, objGet.all, guildModel, getParentExtension().getParentZone())){
            SendExecutionRequestJoinGuild objPut = new SendExecutionRequestJoinGuild(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendExecutionRequestJoinGuild objPut = new SendExecutionRequestJoinGuild();
        send(objPut, user);
    }
    /**
     * Xu ly user vao guild (Add user vao guild)
     * @param userGuildModel
     * @param guildModel
     */
    @WithSpan
    private void doUserJoinGuild(UserGuildModel userGuildModel, GuildModel guildModel){
        User user = ExtensionUtility.getInstance().getUserById(userGuildModel.uid);

        //Gui vao guild thanh cong cho nguoi vao guild
        if(GuildManager.getInstance().userJoinGuildModel(userGuildModel, guildModel, getParentExtension().getParentZone())){
            if(user == null){
                return;
            }

            Room room = getParentExtension().getParentZone().getRoomByName(GuildManager.getInstance().getNameRoomGuild(guildModel.gId));
            if(room != null){
                try {
                    ExtensionUtility.getInstance().joinRoom(user, room);
                    System.out.println();
                    System.out.println("-------------------------- Vào Guild Chat thanh công -------------------------");
                    System.out.println();
                    //TH thanh cong -> tra ve vao guild thanh cong
                    SendUserJoinGuild objPut = new SendUserJoinGuild();
                    send(objPut, user);
                    return;
                } catch (SFSJoinRoomException e) {
                    e.printStackTrace();
                    System.out.println();
                    System.out.println("-------------------------- Vào Guild Chat thất bại -------------------------");
                    System.out.println();
                    SendUserJoinGuild objPut = new SendUserJoinGuild(ServerConstant.ErrorCode.ERR_SYS);
                    send(objPut, user);
                    return;
                }
            }else {
                //Tao room guild
                List<GuildVO> guildCf = GuildManager.getInstance().getGuildConfig();
                CreateRoomSettings cfgRoomGuild = new CreateRoomSettings();
                cfgRoomGuild.setName("guild".concat(ServerConstant.SEPARATER.concat(String.valueOf(guildModel.gId))));
                cfgRoomGuild.setGroupId(Params.Module.MODULE_CHAT);
                cfgRoomGuild.setMaxUsers(guildCf.get(guildCf.size() - 1).member + 10);
                cfgRoomGuild.setDynamic(true);
                cfgRoomGuild.setAutoRemoveMode(SFSRoomRemoveMode.WHEN_EMPTY);

                List<RoomVariable> listVariableChannel = new ArrayList<>();
                listVariableChannel.add(new SFSRoomVariable(Params.TYPE, EChatType.GUILD.getId()));
                listVariableChannel.add(new SFSRoomVariable(Params.NAME, String.valueOf(guildModel.gName)));
                cfgRoomGuild.setRoomVariables(listVariableChannel);

                cfgRoomGuild.setExtension(new CreateRoomSettings.RoomExtensionSettings(getParentExtension().getParentZone().getName(), ExtensionClass.CHAT_EXT));
                try {
                    room = ExtensionUtility.getInstance().createRoom(getParentExtension().getParentZone(), cfgRoomGuild, null, true, null);
                    ExtensionUtility.getInstance().joinRoom(user, room);
                    System.out.println();
                    System.out.println("-------------------------- Tạo Guild Chat thành công -------------------------");
                    System.out.println();
                    //TH thanh cong -> tra ve vao guild thanh cong
                    SendUserJoinGuild objPut = new SendUserJoinGuild();
                    send(objPut, user);
                    return;
                } catch (SFSCreateRoomException e) {
                    e.printStackTrace();
                    System.out.println();
                    System.out.println("-------------------------- Tạo Guild Chat thất bại -------------------------");
                    System.out.println();
                    SendUserJoinGuild objPut = new SendUserJoinGuild(ServerConstant.ErrorCode.ERR_SYS);
                    send(objPut, user);
                    return;
                } catch (SFSJoinRoomException e) {
                    e.printStackTrace();
                    System.out.println();
                    System.out.println("-------------------------- Vào Guild Chat thất bại -------------------------");
                    System.out.println();
                    SendUserJoinGuild objPut = new SendUserJoinGuild(ServerConstant.ErrorCode.ERR_SYS);
                    send(objPut, user);
                    return;
                }
            }
        }

        //That bai vao guild khi max member
        SendUserJoinGuild objPut = new SendUserJoinGuild(ServerConstant.ErrorCode.ERR_GUILD_MAX_MEMBER);
        send(objPut, user);
    }
    @WithSpan
    private void doUserJoinGuild(List<UserGuildModel> listUserGuildModel, GuildModel guildModel){
        List<User> listUser = new ArrayList<>();

        //Add member vao guild + create Guild info vao mode User
        //Cac TH that bai se khong tra ve j
        //TH thanh cong -> tra ve vao guild thanh cong
        List<Long> listUid = GuildManager.getInstance().userJoinGuildModel(listUserGuildModel, guildModel, getParentExtension().getParentZone());

        for(long uid : listUid){
            User user = ExtensionUtility.getInstance().getUserById(uid);
            if(user == null){
                continue;
            }
            listUser.add(user);
        }

        SendUserJoinGuild objPut = new SendUserJoinGuild();
        send(objPut, listUser);

        Room room = getParentExtension().getParentZone().getRoomByName(String.valueOf(guildModel.gId));
        for(User user : listUser){
            try {
                ExtensionUtility.getInstance().joinRoom(user, room);
            } catch (SFSJoinRoomException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Setting lai guild
     * @param user
     * @param data
     */
    @WithSpan
    private void doSettingGuild(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        //Kiem tra user co trong guild khong
        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        if(!GuildManager.getInstance().userInGuild(userGuildModel, getParentExtension().getParentZone())){
            SendSettingGuild objPut = new SendSettingGuild(ServerConstant.ErrorCode.ERR_USER_NOT_IN_GUILD);
            send(objPut, user);
            return;
        }

        GuildModel guildModel = GuildManager.getInstance().getGuildModel(userGuildModel.gid, getParentExtension().getParentZone());
        //Kiem tra quyen setting guild
        if(!GuildManager.getInstance().userHavePermission(uid, EGuildAction.SETTING_GUILD, guildModel)){
            SendSettingGuild objPut = new SendSettingGuild(ServerConstant.ErrorCode.ERR_NOT_HAVE_PERMISSION);
            send(objPut, user);
            return;
        }

        //Setting guild
        RecSettingGuild objGet = new RecSettingGuild(data);
        if(!objGet.listSetting.stream().
                allMatch(setting -> GuildManager.getInstance().settingGuild(EGuildSetting.fromID(setting.id), uid, setting.param, 0, guildModel, getParentExtension().getParentZone()))){
            SendSettingGuild objPut = new SendSettingGuild(ServerConstant.ErrorCode.ERR_INVALID_VALUE);
            send(objPut, user);
            return;
        }

        SendSettingGuild objPut = new SendSettingGuild();
        send(objPut, user);
    }


    /**
     * Setting lai guild
     * @param user
     * @param data
     */
    @WithSpan
    private void doChangeOfficeGuild(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        //Kiem tra user co quyen chinh sua guild khong
        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        if(!GuildManager.getInstance().userInGuild(userGuildModel, getParentExtension().getParentZone())){
            SendChangeOfficeGuild objPut = new SendChangeOfficeGuild(ServerConstant.ErrorCode.ERR_USER_NOT_IN_GUILD);
            send(objPut, user);
            return;
        }

        GuildModel guildModel = GuildManager.getInstance().getGuildModel(userGuildModel.gid, getParentExtension().getParentZone());
        //Kiem tra quyen setting guild
        if(!GuildManager.getInstance().userHavePermission(uid, EGuildAction.CHANGE_OFFICE, guildModel)){
            SendChangeOfficeGuild objPut = new SendChangeOfficeGuild(ServerConstant.ErrorCode.ERR_NOT_HAVE_PERMISSION);
            send(objPut, user);
            return;
        }

        RecChangeOfficeGuild objGet = new RecChangeOfficeGuild(data);
        //Setting guild
        EGuildSetting guildSetting = EGuildSetting.fromID(objGet.id);
        if(guildSetting == null || !GuildManager.getInstance().settingGuild(guildSetting, uid, objGet.param, objGet.select, guildModel, getParentExtension().getParentZone())){
            SendChangeOfficeGuild objPut = new SendChangeOfficeGuild(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendChangeOfficeGuild objPut = new SendChangeOfficeGuild();
        objPut.guildModel = guildModel;
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doCheckInGuild(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        //Kiem tra nguoi dung trong guild khong
        if(!GuildManager.getInstance().userInGuild(userGuildModel, getParentExtension().getParentZone())){
            SendCheckInGuild objPut = new SendCheckInGuild(ServerConstant.ErrorCode.ERR_USER_NOT_IN_GUILD);
            send(objPut, user);
            return;
        }

        //Ktra check in chua
        if(GuildManager.getInstance().haveCheckInGuild(userGuildModel, getParentExtension().getParentZone())){
            SendCheckInGuild objPut = new SendCheckInGuild(ServerConstant.ErrorCode.ERR_ALREADY_CHECK_IN_GUILD);
            send(objPut, user);
            return;
        }

        //Check in
        if(!GuildManager.getInstance().checkInGuild(userGuildModel, getParentExtension().getParentZone())){
            SendCheckInGuild objPut = new SendCheckInGuild(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Add phan thuong
        List<ResourcePackage> rewards = GuildManager.getInstance().getRewardCheckInConfig();
        if(!BagManager.getInstance().addItemToDB(rewards, uid, getParentExtension().getParentZone(), UserUtils.TransactionType.CHECK_IN_GUILD)){
            SendCheckInGuild objPut = new SendCheckInGuild(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendCheckInGuild objPut = new SendCheckInGuild();
        objPut.reward = rewards;
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetListGiftGuild(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        //Kiem tra trong guild khong
        if(!userGuildModel.inGuild()){
            SendGetListGiftGuild objPut = new SendGetListGiftGuild(ServerConstant.ErrorCode.ERR_USER_NOT_IN_GUILD);
            send(objPut, user);
            return;
        }

        SendGetListGiftGuild objPut = new SendGetListGiftGuild();
        objPut.listGift = GuildManager.getInstance().getListGiftGuildUser(userGuildModel, getParentExtension().getParentZone());
        objPut.setClaimed = GuildManager.getInstance().getListGiftClaimed(userGuildModel, getParentExtension().getParentZone());
        objPut.userManager = extension.getUserManager();
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doClaimGiftGuild(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        if(!userGuildModel.inGuild()){
            SendClaimGiftGuild objPut = new SendClaimGiftGuild(ServerConstant.ErrorCode.ERR_USER_NOT_IN_GUILD);
            send(objPut, user);
            return;
        }

        RecClaimGiftGuild objGet = new RecClaimGiftGuild(data);
        GiftGuildInfo giftData = GuildManager.getInstance().getGiftGuildInfo(uid, objGet.hashGift, getParentExtension().getParentZone());
        //Kiem tra ton tai gift + da nhan
        if(giftData == null){
            SendClaimGiftGuild objPut = new SendClaimGiftGuild(ServerConstant.ErrorCode.ERR_NOT_EXSIST_GIFT_GUILD);
            send(objPut, user);
            return;
        }

        if(!GuildManager.getInstance().canClaimGiftGuildUser(giftData)){
            SendClaimGiftGuild objPut = new SendClaimGiftGuild(ServerConstant.ErrorCode.ERR_ALREADY_CLAIM_GIFT_GUILD);
            send(objPut, user);
            return;
        }

        //Nhan thuong trong guild
        List<ResourcePackage> resources = GuildManager.getInstance().getRewardGiftBoxGuild(giftData.id);
        if(!GuildManager.getInstance().claimGiftGuildUser(userGuildModel, GiftGuildInfo.create(giftData, resources), getParentExtension().getParentZone())){
            SendClaimGiftGuild objPut = new SendClaimGiftGuild(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Gen phan thuong + Add phan thuong vao tui
        if(!BagManager.getInstance().addItemToDB(resources, uid, getParentExtension().getParentZone(), UserUtils.TransactionType.CLAIM_GIFT_GUILD)){
            SendClaimGiftGuild objPut = new SendClaimGiftGuild(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendClaimGiftGuild objPut = new SendClaimGiftGuild();
        objPut.hashGift = giftData.hash;
        objPut.reward = resources;
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doRemoveGiftGuild(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        if(!userGuildModel.inGuild()){
            SendRemoveGiftGuild objPut = new SendRemoveGiftGuild(ServerConstant.ErrorCode.ERR_USER_NOT_IN_GUILD);
            send(objPut, user);
            return;
        }

        RecRemoveGiftGuild objGet = new RecRemoveGiftGuild(data);
        //Remove
        if(!GuildManager.getInstance().removeGiftGuildUser(userGuildModel, objGet.hashGift, getParentExtension().getParentZone())){
            SendRemoveGiftGuild objPut = new SendRemoveGiftGuild(ServerConstant.ErrorCode.ERR_NOT_EXSIST_GIFT_GUILD);
            send(objPut, user);
            return;
        }

        SendRemoveGiftGuild objPut = new SendRemoveGiftGuild();
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doRemoveAllGiftGuild(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, getParentExtension().getParentZone());
        if(!userGuildModel.inGuild()){
            SendRemoveAllGiftGuild objPut = new SendRemoveAllGiftGuild(ServerConstant.ErrorCode.ERR_USER_NOT_IN_GUILD);
            send(objPut, user);
            return;
        }

        //Remove
        if(!GuildManager.getInstance().removeAllGiftGuildUser(userGuildModel, getParentExtension().getParentZone())){
            SendRemoveAllGiftGuild objPut = new SendRemoveAllGiftGuild(ServerConstant.ErrorCode.ERR_NOT_EXSIST_GIFT_GUILD);
            send(objPut, user);
            return;
        }

        SendRemoveAllGiftGuild objPut = new SendRemoveAllGiftGuild();
        send(objPut, user);
    }
}