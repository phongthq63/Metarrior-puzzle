package com.bamisu.log.gameserver.module.notification.defind;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.quest.UserQuestModel;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPAchievementVO;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPChallengeVO;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPTabVO;
import com.bamisu.log.gameserver.module.WoL.WoLManager;
import com.bamisu.log.gameserver.module.adventure.AdventureManager;
import com.bamisu.log.gameserver.module.arena.ArenaManager;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.darkgate.DarkGateHandler;
import com.bamisu.log.gameserver.module.friends.FriendManager;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.invite.InviteManager;
import com.bamisu.log.gameserver.module.mage.MageManager;
import com.bamisu.log.gameserver.module.mail.MailManager;
import com.bamisu.log.gameserver.module.mission.MissionManager;
import com.bamisu.log.gameserver.module.quest.QuestManager;
import com.bamisu.log.gameserver.module.tree.EventTreeManager;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public enum ENotification {
    NOT_CLAIM_GIFT_INVITE("events#event2", null),
    NOT_CLAIM_GIFT_HONOR("honor", null),
    NOT_CLAIM_GIFT_LOGIN_EVENT("events#event0", null),
    CAN_CLAIM_IAP_PACKAGE("", Arrays.asList("sky_daily_free", "AUN001", "AUN002", "sky_weekly_free", "AUN003", "sky_monthly_free", "AUN004", "first_purchase")),
    CAN_CLAIM_IAP_CHALLENGE("", Arrays.asList("prestige1", "prestige2", "prestige3", "anese_7_day", "ektar_7_day", "samson_7_day", "gandaar_7_day")),
    HAVE_EXSIST_IAP("", Arrays.asList("anese_7_day", "ektar_7_day", "samson_7_day", "gandaar_7_day")),
    HAVE_MAIL_NOT_SEE_OR_COLLECT("mail", null),
    GUILD_HUNT_OPEN("dark_gate", null),
    ARENA_OPEN("arena", null),
    JOIN_OUR_COMMUNITY("btn_community", Arrays.asList("facebook", "reddit", "discord")),
    HAVE_SKILL_POINT_MAGE("sage_academy#btn_skill_sage", null),
    HAVE_FAST_REWARD("btn_fast_reward", null),
    CAN_CLAIM_GIFT_QUEST("quests", Arrays.asList("daily", "weekly", "all_time")),
    CAN_UP_STAR_HERO("hero_ascension", null),
    HAVE_MESSAGE_CHAT("chat", Arrays.asList("0", "1", "2", "3")),
    CAN_SEND_OR_RECEIVE_FRIEND_POINT("friend_list_sar", null),
    HAVE_FRIEND_REQUEST("friend_request", null),
    NOT_CLAIM_GIFT_IN_HIGHEST_RECORD("milestones_monument", null),
    HAVE_MISSION_CAN_FIGHT("missions", null),
    NOTIFY_HUNT("treasure_hunt", null),
    CAN_OPEN_SLOT_BLESSING_OR_CAN_BLESSING_HERO("mirage_garden", null),
    NOTIFY_TOWER("tower_of_time", null),
    CHECK_IN_GUILD("al_chat#al_checkin", null),
    CAN_CLAIM_GIFT_GUILD("alliance#al_gift", null);


    public static final String SEPARATER_1 = "#";
    public static final String SEPARATER_2 = "_";

    final String id;
    final List<String> params;

    ENotification(String id, List<String> params) {
        this.id = id;
        this.params = params;
    }

    private String getId() {
        return id;
    }

    public List<String> getParams() {
        return params;
    }

    public static ENotification fromName(String name){
        for(ENotification index : ENotification.values()){
            if(index.name().equals(name)){
                return index;
            }
        }
        return null;
    }

    public String getNotifyID(List<String> params) {
        switch (this){
            case NOT_CLAIM_GIFT_INVITE:
            case NOT_CLAIM_GIFT_HONOR:
            case NOT_CLAIM_GIFT_LOGIN_EVENT:
            case HAVE_MAIL_NOT_SEE_OR_COLLECT:
            case HAVE_SKILL_POINT_MAGE:
            case GUILD_HUNT_OPEN:
            case ARENA_OPEN:
            case HAVE_FAST_REWARD:
            case CAN_UP_STAR_HERO:
            case CAN_SEND_OR_RECEIVE_FRIEND_POINT:
            case HAVE_FRIEND_REQUEST:
            case HAVE_MISSION_CAN_FIGHT:
            case NOTIFY_HUNT:
            case CAN_OPEN_SLOT_BLESSING_OR_CAN_BLESSING_HERO:
            case NOTIFY_TOWER:
            case CHECK_IN_GUILD:
            case CAN_CLAIM_GIFT_GUILD:
                return this.getId();

            case CAN_CLAIM_GIFT_QUEST:
            case JOIN_OUR_COMMUNITY:
                if(params == null || params.isEmpty()) return null;
                if(!this.getParams().contains(params.get(0))) return null;
                return this.getId().concat(SEPARATER_1).concat(params.get(0));

            case CAN_CLAIM_IAP_PACKAGE:
            {
                if(params == null || params.isEmpty()) return null;
                if(!this.getParams().contains(params.get(0))) return null;
                IAPTabVO tabCf = IAPBuyManager.getInstance().getIAPTabConfigDependPackage(params.get(0));
                if(tabCf == null) return null;

                return tabCf.id;
            }
            case CAN_CLAIM_IAP_CHALLENGE:
            case HAVE_EXSIST_IAP:
            {
                if(params == null || params.isEmpty()) return null;
                if(!this.getParams().contains(params.get(0))) return null;
                IAPTabVO tabCf = IAPBuyManager.getInstance().getIAPTabConfigDependPackage(params.get(0));
                if(tabCf == null) return null;

                return params.get(0);
            }
            case HAVE_MESSAGE_CHAT:
                if(params == null || params.isEmpty()) return null;
                if(!this.getParams().contains(params.get(0))) return null;
                if(params.size() <= 1){
                    return this.getId().concat(SEPARATER_1).concat("item").concat(SEPARATER_2).concat(params.get(0));
                }else {
                    return this.getId().concat(SEPARATER_1).concat("item").concat(SEPARATER_2).concat(params.get(0)).concat(SEPARATER_2).concat(params.get(1));
                }
            case NOT_CLAIM_GIFT_IN_HIGHEST_RECORD:
                if(params == null || params.size() < 2) return null;
                return this.getId().concat(SEPARATER_1).concat("area").concat(SEPARATER_2).concat(params.get(0)).concat(SEPARATER_1).concat("play").concat(SEPARATER_2).concat(params.get(0)).concat(SEPARATER_2).concat(params.get(1));
        }
        return null;
    }

    public Set<String> getListNotifyID(long uid, Zone zone){
        Set<String> listNoti = new HashSet<>();
        String notiID;
        List<String> params;

        try {
            switch (this){
                case NOT_CLAIM_GIFT_INVITE:
                    if(InviteManager.getInstance().haveGiftCanReceive(uid, zone)){
                        notiID = this.getId();
                        if(notiID != null) listNoti.add(notiID);
                    }
                    break;

                case NOT_CLAIM_GIFT_HONOR:
                    if(!EventTreeManager.getInstance().checkGiftHonor(zone, uid).isEmpty()) listNoti.add(getNotifyID(null));
                    break;

                case NOT_CLAIM_GIFT_LOGIN_EVENT:
                    if(EventTreeManager.getInstance().checkEventLogin(zone, uid)) listNoti.add(getNotifyID(null));
                    break;

                case JOIN_OUR_COMMUNITY:
                    List<String> listQuestID = Arrays.asList("all15", "all16", "all17");
                    List<String> listNotiID = getParams();
                    UserQuestModel userQuestModel = QuestManager.getInstance().getUserQuestModel(uid, zone);
                    for(int i = 0; i < listNotiID.size(); i++){
                        if(!QuestManager.getInstance().haveCompleteQuest(userQuestModel, listQuestID.get(i), zone)){
                            listNoti.add(getNotifyID(Collections.singletonList(listNotiID.get(i))));
                        }
                    }
                    break;

                case CAN_CLAIM_IAP_PACKAGE:
                    List<String> listIAPPachage = IAPBuyManager.getInstance().getListIAPPackageCanClaim(uid, zone);
                    for(String packID : listIAPPachage){
                        notiID = getNotifyID(Collections.singletonList(packID));
                        listNoti.add(notiID);
                    }
                    break;

                case CAN_CLAIM_IAP_CHALLENGE:
                    List<String> listIAPChallenge = IAPBuyManager.getInstance().getListIAPListGetCanClaim(uid, zone);
                    for(String challengeID : listIAPChallenge){
                        notiID = getNotifyID(Collections.singletonList(challengeID));
                        listNoti.add(notiID);
                    }
                    break;

                case HAVE_EXSIST_IAP:
                    List<String> listIAP = IAPBuyManager.getInstance().getListIAPHaveExsist(uid, zone);
                    for(String packID : listIAP){
                        notiID = getNotifyID(Collections.singletonList(packID));
                        listNoti.add(notiID);
                    }
                    break;

                case HAVE_MAIL_NOT_SEE_OR_COLLECT:
                    if(MailManager.getInstance().checkNewMail(zone, uid)) listNoti.add(getNotifyID(null));
                    break;

                case GUILD_HUNT_OPEN:
                    if(((DarkGateHandler)((BaseExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_DARK_GATE)).darkGateManager.isDarkGateOpen()) listNoti.add(getNotifyID(null));
                    break;

                case ARENA_OPEN:
                    if(ArenaManager.getInstance().isOpenSeason(zone)) listNoti.add(getNotifyID(null));
                    break;

                case HAVE_SKILL_POINT_MAGE:
                    if(MageManager.getInstance().haveSkillPointMage(uid, zone)) listNoti.add(getNotifyID(null));
                    break;

                case HAVE_FAST_REWARD:
                    if(AdventureManager.getInstance().checkNoticeFastReward(uid, zone)) listNoti.add(getNotifyID(null));
                    break;

                case CAN_CLAIM_GIFT_QUEST:
                    List<String> listTabQuest = getParams();
                    List<String> listTabQuestID = QuestManager.getInstance().tabCanRewardGiftQuest(uid, zone);
//                    if(!listTabQuestID.isEmpty()){
//                        listNoti.add(getNotifyID(null));
//                    }
                    for(String tab : listTabQuestID){
                        switch (tab){
                            case "0":
                                listNoti.add(getNotifyID(Collections.singletonList(listTabQuest.get(0))));
                                break;
                            case "1":
                                listNoti.add(getNotifyID(Collections.singletonList(listTabQuest.get(1))));
                                break;
                            case "2":
                                listNoti.add(getNotifyID(Collections.singletonList(listTabQuest.get(2))));
                                break;
                        }
                    }
                    break;

                case CAN_UP_STAR_HERO:
                    if(HeroManager.getInstance().haveHeroCanUpStar(uid, zone)) listNoti.add(getNotifyID(null));
                    break;

                case CAN_SEND_OR_RECEIVE_FRIEND_POINT:
                    if(FriendManager.getInstance().checkRedNodeNewPoint(uid, zone)) listNoti.add(getNotifyID(null));
                    break;

                case HAVE_FRIEND_REQUEST:
                    if(FriendManager.getInstance().checkRedNodeHaveRequest(uid, zone)) listNoti.add(getNotifyID(null));
                    break;

                case NOT_CLAIM_GIFT_IN_HIGHEST_RECORD:
                    Map<Integer, List<Integer>> mapGet = WoLManager.getInstance().checkRedNode(uid, zone);

                    for(int area : mapGet.keySet()){
                        for(int state : mapGet.getOrDefault(area, new ArrayList<>())){
                            params = new ArrayList<>();
                            params.add(String.valueOf(area));
                            params.add(String.valueOf(state));
                            listNoti.add(getNotifyID(params));
                        }
                    }
                    break;

                case HAVE_MISSION_CAN_FIGHT:
                    if(!MissionManager.getInstance().getListUserMissionInfoCanFight(uid, zone).isEmpty()) listNoti.add(getNotifyID(null));
                    break;

                case NOTIFY_HUNT:
                case NOTIFY_TOWER:
                    listNoti.add(getNotifyID(null));
                    break;

                case CAN_OPEN_SLOT_BLESSING_OR_CAN_BLESSING_HERO:
                    if(HeroManager.BlessingManager.getInstance().haveSendNotifyBlessingHero(uid, zone)) listNoti.add(getNotifyID(null));
                    break;

                case CHECK_IN_GUILD:
                    if(!GuildManager.getInstance().haveCheckInGuild(uid, zone)) listNoti.add(getNotifyID(null));
                    break;

                case CAN_CLAIM_GIFT_GUILD:
                    if(!GuildManager.getInstance().getListGiftGuildUserCanClaim(uid, zone).isEmpty()) listNoti.add(getNotifyID(null));
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return listNoti.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
