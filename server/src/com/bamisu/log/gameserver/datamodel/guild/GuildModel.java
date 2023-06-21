package com.bamisu.log.gameserver.datamodel.guild;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.item.define.ResourceType;
import com.bamisu.log.gameserver.datamodel.guild.entities.GiftGuildDescription;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.datamodel.guild.entities.GiftGuildInfo;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.guild.config.entities.GiftGuildVO;
import com.bamisu.log.gameserver.module.pushnotify.PushNotifyHandler;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.guild.entities.LogGuildInfo;
import com.bamisu.log.gameserver.module.guild.GuildHandler;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.guild.config.entities.GuildVO;
import com.bamisu.log.gameserver.module.guild.define.*;
import com.bamisu.log.gameserver.module.guild.config.entities.RequestGuildVO;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.datacontroller.ZoneDatacontroler;
import com.bamisu.gamelib.base.datacontroller.couchbase.CouchbaseDataController;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.ValidateUtils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;

public class GuildModel extends DataModel {

    static final String TABLE = "GUILD_MODEL";

    public long gId;    //Id tren database
    public String id;     //Id hien thi cho nguoi choi --- tuy chinh theo kich ban
    public String gPattern = "pat0";
    public String gSymbol = "sym0";
    public String gName;    //Ten guild
    public short level = 1;     //Level guild hien tai
    public long exp = 0;        //Kinh nghiem hien tai

    //Setting guild guild
    public String notice;
    public long powerRequest = 1;
    public String idLanguage;
    public String verification;

    //Request join guild
    public Set<Long> requestJoin = new HashSet<>();

    //Guild co dinh: 1 chu guild + 1 pho guild + 3 doi truong
    public long guildMaster;
    public Set<Long> guildVice = new HashSet<>();
    private final static short maxVice = 1;
    public Set<Long> guildLeader = new HashSet<>();
    private final static short maxLead = 3;

    //List member
    public Set<Long> member = new HashSet<>();      //Id nguoi choi trong guild

    //Time log
    public int timeStamp;
    public List<List<LogGuildInfo>> log = new ArrayList<>();        //Luu timestamp ->
    private final static int time7day = 604800;      //7 ngay (giay)

    //Push noti gift cache
    public List<Long> havePushCache = new ArrayList<>();

    //Gift
    public short giftLevel = 1;     //Level gift guild hien tai
    public long giftExp = 0;        //Kinh nghiem gift guild hien tai
    public List<GiftGuildInfo> listGift = new ArrayList<>();
    private final static int time1day = 86400;      //7 ngay (giay)


    private final Object lockMember = new Object();
    private final Object lockLevel = new Object();
    private final Object lockRequest = new Object();
    private final Object lockLog = new Object();
    private final Object lockMoney = new Object();
    private final Object lockSetting = new Object();
    private final Object lockGift = new Object();


    public GuildModel() {
    }

    public GuildModel(Zone zone) {
        init(zone);
    }

    private void init(Zone zone) {

    }

    public static GuildModel createGuildModel(String pattern, String symbol, String name, String description, String verify, String power, String language, UserModel guildMaster, Zone zone) {
        GuildModel guildModel = new GuildModel(zone);
        guildModel.gPattern = pattern;
        guildModel.gSymbol = symbol;
        guildModel.notice = description;
        guildModel.verification = verify;
        guildModel.powerRequest = GuildManager.getInstance().getPowerRequestGuildConfig(power).point;
        guildModel.idLanguage = language;

        //Tao id Guild
        ZoneDatacontroler zoneDatacontroler = ((BaseExtension) zone.getExtension()).getDataController();
        if (zoneDatacontroler.getController() instanceof CouchbaseDataController) {
            long tmp = ((CouchbaseDataController) zoneDatacontroler.getController()).getClient().incr(GuildModel.TABLE, 1, 1L);
            if (tmp < 10000) {
                ((CouchbaseDataController) zoneDatacontroler.getController()).getClient().incr(GuildModel.TABLE, 10000 - tmp - 1, 1L);
            }
            while (true) {
                guildModel.gId = ((CouchbaseDataController) zoneDatacontroler.getController()).getClient().incr(GuildModel.TABLE, 1, 1L);
                if (GuildModel.copyFromDBtoObject(guildModel.gId, zone) == null) {
                    break;
                }
            }
        }
        //Name guild + tao model guild name
        guildModel.gName = name;
        GuildNameModel.create(guildModel.gName, guildModel.gId, zone);
        //ID display + tao model guild ID
        //TH id tang dan nen khong can de y (id chac chan khong trung)
        //TH id ngau nhien -> Se tao + check id tai day
        guildModel.id = String.valueOf(100000 + guildModel.gId - 10000);
        GuildIDModel.create(guildModel.id, guildModel.gId, zone);

        //Thanh vien dau tien la chu guild - Guild Master
        guildModel.guildMaster = guildMaster.userID;
        guildModel.member.add(guildMaster.userID);
        //Luu time stamp
        guildModel.timeStamp = Utils.getTimestampInSecond();
        //Thuong khi tao guild
        guildModel.addGiftCreateGuild(zone);

        //Viet log
        guildModel.writeLog(EGuildLog.CREATE_GUILD, guildMaster, zone);
        guildModel.saveToDB(zone);

        return guildModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.gId), zone);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public final boolean deleteFromDB(Zone zone) {
        try {
            deleteModel(String.valueOf(this.gId), zone);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GuildModel copyFromDBtoObject(long gId, Zone zone) {
        if (gId == 0) return null;
        return copyFromDBtoObject(String.valueOf(gId), zone);
    }

    private static GuildModel copyFromDBtoObject(String gId, Zone zone) {
        GuildModel pInfo = null;
        try {
            String str = (String) getModel(gId, GuildModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, GuildModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public static List<GuildModel> copyFromDBtoObject(List<Long> listGId, Zone zone) {
        List<GuildModel> listModel = new ArrayList<>();
        try {
            List<String> keys = Lists.transform(listGId, Functions.toStringFunction());
            List<String> jsons = new ArrayList(multiGet(keys, GuildModel.class, zone).values());
            GuildModel model;
            for (String json : jsons) {
                if (json != null) {
                    model = Utils.fromJson(json, GuildModel.class);
                    listModel.add(model);
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return listModel;
    }



    /*-----------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------*/

    /**
     * Kiem tra user co quyen kick or add khong
     */
    public boolean havePermission(long uid, EGuildAction permission) {
        switch (permission) {
            case EXECUTE_REQUEST_JOIN_GUILD:
                if (readVerification().equals(EGuildVerificationType.AUTO_JOIN.getId())) {
                    return true;
                }
                if (uid == guildMaster) {
                    return true;
                }
                if (guildVice.contains(uid)) {
                    return true;
                }
                if (guildLeader.contains(uid)) {
                    return true;
                }
                break;
            case KICK_MEMBER:
                if (uid == guildMaster) {
                    return true;
                }
                if (guildVice.contains(uid)) {
                    return true;
                }
                break;
            case LEAVE_GUILD:
                if (uid != guildMaster || member.size() <= 1) {
                    return true;
                }
                break;
            case SEE_LOG:
                return havaMember(uid);
            case SETTING_GUILD:
            case CHANGE_OFFICE:
                if (guildMaster == uid) {
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * Chinh sua cai dat guild
     * @param setting
     * @param pemission uid chinh sua
     * @param value gia tri chinh sua
     * @param select 0 = add, > 0 giá trị cần chinh sửa
     * @param zone
     * @return
     */
    public boolean settingGuild(EGuildSetting setting, long pemission, Object value, long select, Zone zone) {
        long uid = 0;
        List<String> params = new ArrayList<>();

        synchronized (lockSetting) {
            switch (setting) {
                case NOTICE:
                    notice = value.toString();
                    return saveToDB(zone);
                case POWER_REQUEST:
                    RequestGuildVO powerRequestCf = GuildManager.getInstance().getPowerRequestGuildConfig(value.toString());
                    if (powerRequestCf == null) return false;
                    powerRequest = powerRequestCf.point;
                    return saveToDB(zone);
                case LANGUAGE:
                    if (!ValidateUtils.isLanguageID(value.toString())) return false;
                    idLanguage = value.toString();
                    return saveToDB(zone);
                case VERIFICATION:
                    EGuildVerificationType type = EGuildVerificationType.fromID(value.toString());
                    if (type == null) return false;
                    verification = type.getId();
                    return saveToDB(zone);
                case GUILD_MASTER:
                    try {
                        uid = Long.valueOf(value.toString());
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        return false;
                    }
                    //Kiem tra user co trong guild khong
                    if (!havaMember(uid)) return false;
                    //Xoa chuc vu hien tai cua user
                    removeOffice(uid);
                    //Add chuc vu moi
                    guildMaster = uid;

                    //Viet log
                    params.add(((GuildHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(pemission).displayName);
                    params.add(((GuildHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(uid).displayName);
                    writeLog(EGuildLog.UP_OFFICE_MASTER.getId(), params, zone);

                    return saveToDB(zone);
                case GUILD_VICE:
                    try {
                        uid = Long.valueOf(value.toString());
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        return false;
                    }
                    //Xoa chuc vu hien tai cua target
                    if (uid <= 0) {
                        removeOffice(select);

                        //Viet log
                        params.add(((GuildHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(pemission).displayName);
                        params.add(((GuildHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(select).displayName);
                        writeLog(EGuildLog.DOWN_OFFICE_MEMBER.getId(), params, zone);

                        return saveToDB(zone);
                    }
                    //Kiem tra user co trong guild khong
                    if (!havaMember(uid)) return false;
                    if ((select <= 0 && guildVice.size() < maxVice) || guildVice.remove(select)) {
                        //Xoa chuc vu hien tai cua user
                        removeOffice(uid);
                        //Add chuc vu moi
                        guildVice.add(uid);

                        //Viet log
                        params.add(((GuildHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(pemission).displayName);
                        params.add(((GuildHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(uid).displayName);
                        writeLog(EGuildLog.UP_OFFICE_VICE.getId(), params, zone);

                        return saveToDB(zone);
                    }
                    break;
                case GUILD_LEADER:
                    try {
                        uid = Long.valueOf(value.toString());
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        return false;
                    }
                    //Xoa chuc vu hien tai cua target
                    if (uid <= 0) {
                        removeOffice(select);

                        //Viet log
                        params.add(((GuildHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(pemission).displayName);
                        params.add(((GuildHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(select).displayName);
                        writeLog(EGuildLog.DOWN_OFFICE_MEMBER.getId(), params, zone);

                        return saveToDB(zone);
                    }
                    //Kiem tra user co trong guild khong
                    if (!havaMember(uid)) return false;
                    if ((select <= 0 && guildLeader.size() < maxLead) || guildLeader.remove(select)) {
                        //Xoa chuc vu hien tai cua user
                        removeOffice(uid);
                        //Add chuc vu moi
                        guildLeader.add(uid);

                        //Viet log
                        params.add(((GuildHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(pemission).displayName);
                        params.add(((GuildHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(uid).displayName);
                        writeLog(EGuildLog.UP_OFFICE_LEAD.getId(), params, zone);

                        return saveToDB(zone);
                    }
                    break;
                case AVATAR:
                    String[] split = value.toString().split(ServerConstant.SEPARATER);
                    if (GuildManager.getInstance().getPatternGuildConfig(split[0]) == null ||
                            GuildManager.getInstance().getSymbolGuildConfig(split[1]) == null) return false;
                    gPattern = split[0];
                    gSymbol = split[1];
                    return saveToDB(zone);
            }
        }
        return false;
    }

    public boolean removeOffice(long uid) {
        return guildVice.remove(uid) || guildLeader.remove(uid);
    }

    /**
     * Kiem tra nguoi choi trong gui
     */
    public boolean havaMember(long uid) {
        return member.contains(uid);
    }


    /**
     * @return
     */
    public short readLevel() {
        synchronized (lockLevel) {
            return (level < 1) ? 1 : level;
        }
    }

    public long readExp() {
        synchronized (lockLevel) {
            return exp;
        }
    }

    private void upLevel(Zone zone) {
        List<GuildVO> listCf = GuildManager.getInstance().getGuildConfig();

        synchronized (lockLevel) {
            for (int i = 0; i < listCf.size(); i++) {
                if (listCf.get(i).level == readLevel()) {
                    if (readExp() >= listCf.get(i).upCost && listCf.get(i).upCost > 0) {
                        level++;
                        exp -= listCf.get(i).upCost;

                        //Viet log
                        List<String> params = new ArrayList<>();
                        params.add(String.valueOf(readLevel()));
                        writeLog(EGuildLog.CHANGE_LEVEL_GUILD.getId(), params, zone);
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /**
     * Lay request join guild
     *
     * @return
     */
    public List<Long> readRequestJoinGuild() {
        synchronized (lockRequest) {
            return new ArrayList<>(requestJoin);
        }
    }

    public void updateRequestJoinGuild(Set<Long> setUid) {
        synchronized (lockRequest) {
            requestJoin = setUid;
        }
    }

    /**
     * Lua danh sach don xin gia nhap guild
     */
    public boolean addRequestJoinGuild(long uid, Zone zone) {
        if (readVerification().equals(EGuildVerificationType.AUTO_JOIN.getId())) {
            return false;
        }
        synchronized (lockRequest) {
            requestJoin.add(uid);
            return saveToDB(zone);
        }
    }

    /**
     * Xoa request khoi list danh sach
     *
     * @param uid
     * @param zone
     * @return
     */
    public boolean removeRequestJoinGuild(long uid, Zone zone) {
        synchronized (lockRequest) {
            requestJoin.remove(uid);
            return saveToDB(zone);
        }
    }

    public boolean removeRequestJoinGuild(List<Long> listUid, Zone zone) {
        synchronized (lockRequest) {
            requestJoin.removeAll(listUid);
            return saveToDB(zone);
        }
    }

    public boolean removeAllRequestJoinGuild(Zone zone) {
        synchronized (lockRequest) {
            if (requestJoin.size() > 0) {
                requestJoin.clear();
                return saveToDB(zone);
            }
        }
        return true;
    }

    /**
     * Them thanh vien
     *
     * @param uid
     * @param zone
     * @return
     */
    public boolean addMember(long uid, Zone zone) {
        synchronized (lockMember) {
            if (!isGuildFull(1)) {
                member.add(uid);

                //Viet log
                writeLog(EGuildLog.MEMBER_JOIN_GUILD,
                        ((GuildHandler) ((BaseExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(uid), zone);

                return saveToDB(zone);
            }
            return false;
        }
    }

    public boolean addMember(List<Long> listUid, Zone zone) {
        synchronized (lockMember) {
            if (!isGuildFull(listUid.size())) {
                member.addAll(listUid);

                //Viet log
                listUid.parallelStream().forEach(uid -> writeLog(EGuildLog.MEMBER_JOIN_GUILD,
                        ((GuildHandler) ((BaseExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(uid), zone));

                return saveToDB(zone);
            }
        }
        return false;
    }

    /**
     * Xoa User khoi guild
     *
     * @param uid
     * @param zone
     * @return
     */
    public boolean removeMember(long uid, Zone zone, boolean isKick){
        if(guildMaster == uid && member.size() > 1){
            if(guildVice.isEmpty()){
                //Neu khong co pho guild
                return false;
            } else {
                //Neu setting khong thanh cong
                if (!settingGuild(EGuildSetting.GUILD_MASTER, uid, guildVice.toArray()[0], uid, zone)) return false;
            }
        }
        if (guildMaster == uid && member.size() == 1) {
            //Xoa trong data + trong guild manager
            return GuildManager.getInstance().removeGuildModel(gId, zone);
        }
        synchronized (lockMember) {
            if (havaMember(uid)) {
                //Xoa thanh vien trong guild
                member.remove(uid);
                //Xoa chuc danh memver so huu
                removeOffice(uid);

                //Viet log
                if(isKick){
                    writeLog(
                            EGuildLog.KICK_FROM_GUILD,
                            ((GuildHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(uid), zone);
                }else {
                    writeLog(
                            EGuildLog.LEAVE_GUILD,
                            ((GuildHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_GUILD)).getUserModel(uid), zone);
                }
                //Save Db
                return saveToDB(zone);
            }
            return true;
        }
    }



    /*---------------------------------------------- GIFT ----------------------------------------------------------*/

    /**
     * @return
     */
    public short readLevelGift() {
        synchronized (lockGift) {
            return (giftLevel < 1) ? 1 : giftLevel;
        }
    }

    public long readExpGift() {
        synchronized (lockGift) {
            return giftExp;
        }
    }

    private void upLevelGift(Zone zone) {
        List<GiftGuildVO> listCf = GuildManager.getInstance().getGiftGuildConfig();

        synchronized (lockGift) {
            for (int i = 0; i < listCf.size(); i++) {
                if (listCf.get(i).level == readLevelGift()) {
                    if (readExpGift() >= listCf.get(i).upCost && listCf.get(i).upCost > 0) {
                        addGiftUpLevelGiftGuild(readLevelGift(), zone);
                        giftLevel++;
                        giftExp -= listCf.get(i).upCost;
                    }else {
                        return;
                    }
                }
            }
        }
    }

    public List<GiftGuildInfo> readListGiftGuild(Zone zone) {
        synchronized (lockGift) {
            boolean haveSave = false;
            if (listGift == null) {
                listGift = new ArrayList<>();
                haveSave = true;
            }

            Iterator<GiftGuildInfo> iterator = listGift.iterator();
            GiftGuildInfo data;
            int now = Utils.getTimestampInSecond();
            while (iterator.hasNext()) {
                data = iterator.next();

                switch (EGuildGiftType.fromID(data.type)) {
                    case CREATE:
                    case DAILY:
                    case BUY:
                        if (data.timeExpert != -1 && now >= data.timeExpert) {
                            iterator.remove();
                            haveSave = true;
                        }
                        break;
                    case UP_LEVEL_GIFT:
                        break;
                }
            }
            if (isNewDay()) {
                refreshNewDay(zone);
                haveSave = true;
            }
            if (haveSave) saveToDB(zone);


            return listGift;
        }
    }

    private void addGiftCreateGuild(Zone zone) {
        int now = Utils.getTimestampInSecond();

        synchronized (lockGift) {
            List<ResourcePackage> rewards = GuildManager.getInstance().getRewardGiftCreateGuild();
            for (ResourcePackage res : rewards) {
                for (int i = 0; i < res.amount; i++) {
                    addGiftGuild(GiftGuildInfo.create(res.id, EGuildGiftType.CREATE, now + time1day), zone);
                }
            }
        }
    }
    private void addGiftDailyGuild(Zone zone){
        synchronized (lockGift){
            List<ResourcePackage> rewards = GuildManager.getInstance().getRewardGiftDailyGuild();
            for(ResourcePackage res : rewards){
                for(int i = 0; i < res.amount; i++){
                    addGiftGuild(GiftGuildInfo.create(res.id, EGuildGiftType.DAILY, -1), zone);
                }
            }
        }
    }
    private void addGiftUpLevelGiftGuild(int level, Zone zone) {
        synchronized (lockGift) {
            List<ResourcePackage> rewards = GuildManager.getInstance().getGiftGuildConfig(level).gift;

            for(ResourcePackage res : rewards){
                for(int i = 0; i < res.amount; i++){
                    addGiftGuild(GiftGuildInfo.create(res.id, EGuildGiftType.UP_LEVEL_GIFT, GiftGuildDescription.create(readLevelGift()), -1), zone);
                }
            }

            //Viet log
            List<String> params = new ArrayList<>();
            params.add(String.valueOf(readLevelGift()));
            writeLog(EGuildLog.GET_GIFT_GUILD_DEPEND_LEVEL.getId(), params, zone);
        }
    }
    public void addGiftBuyGuild(List<GiftGuildInfo> guildInfo, Zone zone) {
        synchronized (lockGift) {
            addGiftGuild(guildInfo, zone);
        }
    }

    /**
     * Add gift guild
     * @param giftGuild
     * @param zone
     */
    private void addGiftGuild(GiftGuildInfo giftGuild, Zone zone) {
        synchronized (lockGift) {
            listGift.add(giftGuild);
            pushNotifyGift(zone);

            //Event
            Map<String,Object> eventData = new HashMap<>();
            eventData.put(Params.UIDS, new ArrayList<>(member));
            GameEventAPI.ariseGameEvent(EGameEvent.NEW_GIFT_GUILD, 0, eventData, zone);
        }
    }
    private void addGiftGuild(List<GiftGuildInfo> listGiftGuild, Zone zone) {
        synchronized (lockGift) {
            listGift.addAll(listGiftGuild);
            pushNotifyGift(zone);

            //Event
            Map<String,Object> eventData = new HashMap<>();
            eventData.put(Params.UIDS, new ArrayList<>(member));
            GameEventAPI.ariseGameEvent(EGameEvent.NEW_GIFT_GUILD, 0, eventData, zone);
        }
    }

    private void pushNotifyGift(Zone zone) {
        //Push notify
        List<Long> listToPush = getListToPushnotiGift();
        if (!listToPush.isEmpty()) {
            ((PushNotifyHandler) ((BaseExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_PUSH_NOTIFY)).pushNotifyManager.onAllianceHaveNewGift(listToPush);
            for(Long uid : listToPush){
                if(!havePushCache.contains(uid)){
                    havePushCache.add(uid);
                }
            }
        }
    }

    /**
     * lấy danh sách có thể nhận push notify gift (loại bỏ các trường hợp đã nhận rồi nhưng chưa đăng nhập)
     *
     * @return
     */
    private List<Long> getListToPushnotiGift() {
        List<Long> listToPush = new ArrayList<>();
        for (Long uid : member) {
            if (!havePushCache.contains(uid) && ExtensionUtility.getInstance().getUserById(uid) == null) {
                listToPush.add(uid);
            }
        }

        return listToPush;
    }



    /*--------------------------------------------- RESOURCE GUILD ---------------------------------------------------*/

    /**
     * up resource
     */
    public boolean updateResourceGuild(List<ResourcePackage> resources, Zone zone) {
        boolean haveSave = false;

        synchronized (lockMoney) {
            for (ResourcePackage res : resources) {
                switch (ResourceType.fromID(res.id)) {
                    case RESOURCE:
                        switch (EResourceGuildType.fromID(res.id)) {
                            case GUILD_EXP:
                                exp += res.amount;
                                //Up level newu co the
                                upLevel(zone);
                                haveSave = true;
                                break;
                            case GIFT_EXP:
                                giftExp += res.amount;
                                //Up level newu co the
                                upLevelGift(zone);
                                haveSave = true;
                                break;
                        }
                        break;
                }
            }

            if (haveSave) {
                return saveToDB(zone);
            } else {
                return false;
            }
        }
    }



    /*-------------------------------------------------- LOG ---------------------------------------------------------*/

    /**
     * Doc log
     */
    public List<List<LogGuildInfo>> readLogGuild(Zone zone) {
        removeLog(zone);
        return log;
    }


    /**
     * Viet log
     *
     * @param logType
     * @param userModel
     */
    private void writeLog(EGuildLog logType, UserModel userModel, Zone zone) {
        List<String> params = new ArrayList<>();
        switch (logType) {
            case CREATE_GUILD:
                params.add(userModel.displayName);
                params.add(this.gName);
                break;
            case MEMBER_JOIN_GUILD:
            case LEAVE_GUILD:
            case KICK_FROM_GUILD:
                params.add(userModel.displayName);
                break;
        }
        if (params.size() > 0) {
            writeLog(logType.getId(), params, zone);
        }
    }

    /**
     * Viet log
     *
     * @param id
     * @param param
     */
    private void writeLog(String id, List<String> param, Zone zone) {
        synchronized (lockLog) {
            //Log
            LogGuildInfo logMsg = LogGuildInfo.createLogGuildInfo(id, param);

            //Khi khoi tao log rong
            if (log.size() == 0) {
                log.add(new ArrayList<>());
                log.get(0).add(logMsg);
            } else {
                //Lay log cua ngay cuoi cung ghi lai
                //Neu la ngay moi -> Tao them mang roi gan log moi vao
                //Neu khong phai -> Gan log moi vao mang cuoi
                if (Utils.isNewDay(log.get(log.size() - 1).get(0).time)) {
                    log.add(new ArrayList<>());
                    log.get(log.size() - 1).add(logMsg);
                } else {
                    log.get(log.size() - 1).add(logMsg);
                }
            }

            //Send Message Log Runtime to user
            GuildManager.getInstance().sendMessageLog(gId, logMsg, zone);
        }
    }

    private void removeLog(Zone zone) {
        //Kiem tra 7 ngay tu ngay dau --> ngay thu 7
        //Neu qua 7 ngay -> Xoa
        int current = Utils.getTimestampInSecond();
        boolean haveSave = false;

        for (int i = 0; i < log.size(); i++) {
            if (current - log.get(i).get(0).time > time7day) {
                log.remove(0);
                haveSave = true;
            } else {
                break;
            }
        }

        if (haveSave) saveToDB(zone);
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    private void refreshNewDay(Zone zone) {
        addGiftDailyGuild(zone);    //Add gift daily
        timeStamp = Utils.getTimestampInSecond();
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    public String readNotice() {
        return (notice == null) ? "" : notice;
    }

    public String readAvatar() {
        if (gPattern == null || gPattern.isEmpty() || gSymbol == null || gSymbol.isEmpty())
            return "pat0".concat(ServerConstant.SEPARATER).concat("sym0");
        return gPattern + ServerConstant.SEPARATER + gSymbol;
    }

    public long readRequestPower() {
        return (powerRequest < 0) ? 0 : powerRequest;
    }

    public String readLanguage() {
        return (idLanguage == null) ? ServerConstant.LanguageID.ENGLISH : idLanguage;
    }

    public String readVerification() {
        return (verification == null || EGuildVerificationType.fromID(verification) == null) ? EGuildVerificationType.AUTO_JOIN.getId() : verification;
    }

    public boolean isGuildFull(int count) {
        return member.size() + count > GuildManager.getInstance().getGuildConfig(readLevel()).member;
    }

    private boolean isNewDay() {
        return Utils.isNewDay(timeStamp);
    }
}
