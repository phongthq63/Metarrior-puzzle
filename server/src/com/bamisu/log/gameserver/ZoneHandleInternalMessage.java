package com.bamisu.log.gameserver;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.datamodel.IAP.event.entities.InfoIAPSale;
import com.bamisu.log.gameserver.manager.ServerManager;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.arena.ArenaManager;
import com.bamisu.log.gameserver.module.bag.BagHandler;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.campaign.CampaignManager;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.chat.ChatManager;
import com.bamisu.log.gameserver.module.chat.ChatManagerHandler;
import com.bamisu.log.gameserver.module.darkgate.DarkGateHandler;
import com.bamisu.log.gameserver.module.darkgate.DarkGateManager;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.hero.HeroHandler;
import com.bamisu.log.gameserver.module.hunt.HuntManager;
import com.bamisu.log.gameserver.module.mail.MailManager;
import com.bamisu.log.gameserver.module.mission.MissionManager;
import com.bamisu.log.gameserver.module.nft.NFTHandler;
import com.bamisu.log.gameserver.module.notification.NotificationManager;
import com.bamisu.log.gameserver.module.notification.defind.EActionNotiModel;
import com.bamisu.log.gameserver.module.notification.defind.ENotification;
import com.bamisu.log.gameserver.module.pushnotify.PushNotifyHandler;
import com.bamisu.log.gameserver.module.tower.TowerManager;
import com.bamisu.gamelib.ExtensionHandleInternalMessage;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.user.UserHandler;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Create by Popeye on 5:41 PM, 10/21/2019
 */
public class ZoneHandleInternalMessage extends ExtensionHandleInternalMessage {

    public ZoneHandleInternalMessage(BaseExtension extension) {
        super(extension);
    }

    @WithSpan
    @Override
    public Object handleInternalMessage(String cmdName, Object params) {
        ZoneExtension zoneExtension = ((ZoneExtension)extension.getParentZone().getExtension());
        ISFSObject rec = (ISFSObject) params;
        ISFSObject res = new SFSObject();
        switch (cmdName) {
            case CMD.InternalMessage.GET_SERVER_INFO:
                res.putUtfString(Params.NAME, extension.getConfigProperties().getProperty("server_name"));
                res.putUtfString(Params.ID, extension.getConfigProperties().getProperty("server_id"));
                res.putUtfString(Params.ADDRESS, extension.getConfigProperties().getProperty("server_addr"));
                res.putInt(Params.PORT, 9933);
                res.putInt(Params.STATUS, 1);
                break;
            case CMD.InternalMessage.GET_LOGIN_INFO:
                res = ((UserHandler) extension.getServerHandler(Params.Module.MODULE_USER)).GetLoginInfo(rec);
                break;
            case CMD.InternalMessage.CHANGE_USER_MONEY:
                ((BagHandler) extension.getServerHandler(Params.Module.MODULE_BAG)).notifyChaneMoney(rec);
                break;
            case CMD.InternalMessage.CHANGE_USER_RESOURCE:
                ((BagHandler) extension.getServerHandler(Params.Module.MODULE_BAG)).notifyChaneResource(rec);
                break;
            case CMD.InternalMessage.CHANGE_USER_TOKEN:
                ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).notifyChangeToken(rec);
                break;
            case CMD.InternalMessage.REMOVE_USER_ROOM_CHAT:
                res = ((ChatManagerHandler) extension.getServerHandler(Params.Module.MODULE_CHAT)).RemoveUserToChat(rec);
                break;
            case CMD.InternalMessage.ADD_USER_ROOM_CHAT:
                res = ((ChatManagerHandler) extension.getServerHandler(Params.Module.MODULE_CHAT)).AddUserToChat(rec);
                break;
            case CMD.InternalMessage.CHECK_USER_ROOM_CHAT:
                res = ((ChatManagerHandler) extension.getServerHandler(Params.Module.MODULE_CHAT)).IsUserInChat(rec);
                break;
            case CMD.InternalMessage.LIST_USER_ROOM_CHAT:
                res = ((ChatManagerHandler) extension.getServerHandler(Params.Module.MODULE_CHAT)).ListUserInChat(rec);
                break;
            case CMD.InternalMessage.ARISE_GAME_EVENT:
            {
                EGameEvent event = EGameEvent.fromID(rec.getUtfString(Params.EVENT));
                if(event == null) break;

                long uid = rec.getLong(Params.UID);
                Map<String, Object> data = Utils.fromJson(rec.getUtfString(Params.DATA), HashMap.class);
                GameEventAPI.ariseGameEvent(EGameEvent.CHAT, uid, data, extension.getParentZone());
                break;
            }
            case CMD.InternalMessage.FIGHT_CAMPAIGN_RESULT:
                Map<String, Object> statisticals1 = new ConcurrentHashMap<>(); //damage info
                statisticals1.put(Params.DAMAGE, rec.getSFSObject(Params.STATISTICAL).getInt(Params.DAMAGE));
                statisticals1.put(Params.TANK, rec.getSFSObject(Params.STATISTICAL).getInt(Params.TANK));
                statisticals1.put(Params.HEAL, rec.getSFSObject(Params.STATISTICAL).getInt(Params.HEAL));
                CampaignManager.getInstance().completeCampaign(extension.getParentZone(), statisticals1, rec.getBool(Params.WIN), rec.getLong(Params.UID), rec.getInt(Params.STATION), rec.getInt(Params.STAR));
                break;
            case CMD.InternalMessage.FIGHT_MISSION_RESULT:
                Map<String, Object> statisticals2 = new ConcurrentHashMap<>(); //damage info
                statisticals2.put(Params.DAMAGE, 0);
                statisticals2.put(Params.TANK, 0);
                statisticals2.put(Params.HEAL, 0);
                MissionManager.getInstance().complateMission(extension.getParentZone(), statisticals2, rec.getBool(Params.WIN), rec.getLong(Params.UID), rec.getUtfString(Params.MISSION_HASH), rec.getLong(Params.POINT));
                break;
            case CMD.InternalMessage.FIGHT_TOWER_RESULT:
                Map<String, Object> statisticals3 = new ConcurrentHashMap<>(); //damage info
                statisticals3.put(Params.DAMAGE, rec.getSFSObject(Params.STATISTICAL).getInt(Params.DAMAGE));
                statisticals3.put(Params.TANK, rec.getSFSObject(Params.STATISTICAL).getInt(Params.TANK));
                statisticals3.put(Params.HEAL, rec.getSFSObject(Params.STATISTICAL).getInt(Params.HEAL));
                TowerManager.getInstance().completeTower(extension.getParentZone(), statisticals3, rec.getBool(Params.WIN), rec.getLong(Params.UID));
                break;
            case CMD.InternalMessage.FIGHT_HUNT_RESULT:
                Map<String, Object> statisticals4 = new ConcurrentHashMap<>(); //damage info
                statisticals4.put(Params.DAMAGE, rec.getSFSObject(Params.STATISTICAL).getInt(Params.DAMAGE));
                statisticals4.put(Params.TANK, rec.getSFSObject(Params.STATISTICAL).getInt(Params.TANK));
                statisticals4.put(Params.HEAL, rec.getSFSObject(Params.STATISTICAL).getInt(Params.HEAL));
                HuntManager.getInstance().complateHunt(extension.getParentZone(), statisticals4, rec.getFloatArray(Params.Remaining_HP), rec.getBool(Params.WIN), rec.getLong(Params.UID));
                break;
            case CMD.InternalMessage.FIGHT_ARENA_OFFLINE_RESULT:
                ArenaManager.getInstance().completeArena(zoneExtension.getParentZone(), rec.getBool(Params.WIN), rec.getLong(Params.UID), rec.getLong(Params.ENEMY), rec.getUtfString(Params.BATTLE_ID));
                break;
            case CMD.InternalMessage.FIGHT_DARK_REALM_RESULT:
                DarkGateHandler darkGateHandler = (DarkGateHandler) zoneExtension.getServerHandler(Params.Module.MODULE_DARK_GATE);
                darkGateHandler.darkGateManager.complateFightDarkRealm(rec.getLong(Params.UID), rec.getLong(Params.POINT));
                break;
            case CMD.InternalMessage.FIGHT_ENDLESS_NIGHT_RESULT:
                ((DarkGateHandler) zoneExtension.getServerHandler(Params.Module.MODULE_DARK_GATE)).darkGateManager.complateFightEndlessNight(rec.getLong(Params.UID), rec.getLong(Params.POINT), rec.getLong(Params.CANDY));
                break;
            case CMD.InternalMessage.SEND_ALL_PLAYER_NOTIFY_MODEL:
                List<String> listId = new ArrayList<>(rec.getUtfStringArray(Params.ID));
                EActionNotiModel action = EActionNotiModel.fromID(rec.getUtfString(Params.ACTION));
                if(action == null) break;

                NotificationManager.getInstance().sendNotifyModel(new ArrayList<>(zoneExtension.getParentZone().getUserList()), action, listId, zoneExtension.getParentZone());
                break;
            case CMD.InternalMessage.UPDATE_CONFIG_MODULE_SERVER:
                String moduleName = rec.getUtfString(Params.MODULE);
                boolean active = rec.getBool(Params.IS_ACTIVE);
                int time = rec.getInt(Params.TIME);

                ServerManager.getInstance().updateActiveEventModule(moduleName, active, time, zoneExtension.getParentZone());
                break;
            case CMD.TEST:
                res = test(rec);
                break;
            case CMD.InternalMessage.GET_CACHE_USER_MODEL:
            {
                long uid = rec.getLong(Params.UID);
                res.putUtfString(Params.DATA, Utils.toJson(extension.getUserManager().getUserModel(uid)));
                break;
            }
            case CMD.InternalMessage.GET_CACHE_USER_GUILD_MODEL:
            {
                long uid = rec.getLong(Params.UID);
                res.putUtfString(Params.DATA, Utils.toJson(((ZoneExtension)extension).getZoneCacheData().getUserGuildModelCache(uid)));
                break;
            }
            case CMD.InternalMessage.GET_ALL_MESSAGE_CHAT:
            {
                long uid = rec.getLong(Params.UID);
                res.putUtfString(Params.DATA, Utils.toJson(ChatManager.getInstance().loadAllNewMessage(uid, extension.getParentZone())));
                break;
            }
            case CMD.InternalMessage.GET_LEVEL_USER:
            {
                long uid = rec.getLong(Params.UID);
                res.putInt(Params.DATA, BagManager.getInstance().getLevelUser(uid, extension.getParentZone()));
                break;
            }
            case CMD.InternalMessage.GET_LOG_GUILD:
            {
                long uid = rec.getLong(Params.UID);
                res.putUtfString(Params.DATA, Utils.toJson(GuildManager.getInstance().getLogGuildModelByUserID(uid, extension.getParentZone())));
                break;
            }
            case CMD.InternalMessage.SEND_MAIL_TO_PLAYER:
                String title = rec.getUtfString(Params.TITLE);
                String content = rec.getUtfString(Params.CONTENT);
                List<ResourcePackage> gift = Utils.fromJsonList(rec.getUtfString(Params.GIFT_LIST), ResourcePackage.class);
                List<Long> uids = (rec.containsKey(Params.UIDS)) ? new ArrayList<>(rec.getLongArray(Params.UIDS)) : new ArrayList<>();

                String listFalse = "";
                if(uids.isEmpty()){
                    //Tao mail + send notify
                    MailManager.getInstance().createMailAdmin(zoneExtension.getParentZone(), title, content, gift);
                    NotificationManager.getInstance().sendNotify(
                            new ArrayList<>(ExtensionUtility.getInstance().getZoneManager().getZoneByName(extension.getParentZone().getName()).getUserList()),
                            Collections.singletonList(ENotification.HAVE_MAIL_NOT_SEE_OR_COLLECT.getNotifyID(null)),
                            extension.getParentZone());
                }else {
                    //Tao mail + send notify
                    for(long uid : uids){
                        if(extension.getUserManager().getUserModel(uid) == null){
                            listFalse += uid + ",";
                        }else {
                            MailManager.getInstance().createMail(zoneExtension.getParentZone(), uid, title, content, gift);
                        }
                    }
                    NotificationManager.getInstance().sendNotify(
                            uids.stream().map(uid -> ExtensionUtility.getInstance().getUserById(uid)).collect(Collectors.toList()),
                            Collections.singletonList(ENotification.HAVE_MAIL_NOT_SEE_OR_COLLECT.getNotifyID(null)),
                            extension.getParentZone());
                }

                res.putUtfString(Params.DATA, listFalse);
                break;
            case CMD.InternalMessage.ADD_SALE_IAP:
            {
                String sale = rec.getUtfString(Params.SALE);
                InfoIAPSale infoIAPSale = Utils.fromJson(sale, InfoIAPSale.class);

                IAPBuyManager.getInstance().getIAPEventModel(zoneExtension.getParentZone()).addInfoIAPsale(infoIAPSale, zoneExtension.getParentZone());
                break;
            }
            case CMD.InternalMessage.REMOVE_SALE_IAP:
            {
                List<String> listIdIAP = new ArrayList<>(rec.getUtfStringArray(Params.SALE));

                IAPBuyManager.getInstance().getIAPEventModel(zoneExtension.getParentZone()).removeInfoIAPsale(listIdIAP, zoneExtension.getParentZone());
                break;
            }
            case CMD.InternalMessage.SEND_NOTIFY:
            {
                List<Long> listUid = new ArrayList<>(rec.getLongArray(Params.UID));
                String id = rec.getUtfString(Params.ID);

                NotificationManager.getInstance().sendNotify(listUid.stream().map(uid -> ExtensionUtility.getInstance().getUserById(uid)).collect(Collectors.toList()), Collections.singletonList(id), extension.getParentZone());
                break;
            }
            case "dg_end":
                DarkGateManager.endWeekTime = 60 * 60 * 24 *5;
                ((DarkGateHandler) zoneExtension.getServerHandler(Params.Module.MODULE_DARK_GATE)).darkGateManager.onEndWeek();
                break;
            case "dg_start":
                DarkGateManager.endWeekTime = 60 * 60;
                ((DarkGateHandler) zoneExtension.getServerHandler(Params.Module.MODULE_DARK_GATE)).darkGateManager.onStartWeek();
                break;
            case CMD.InternalMessage.ARENA_END_DAY:
                ArenaManager.getInstance().taskStartDaily(zoneExtension.getParentZone());
                break;
            case CMD.InternalMessage.CLOSE_SEASON_ARENA:
                ArenaManager.getInstance().taskEndSeason(zoneExtension.getParentZone());
                break;
            case CMD.InternalMessage.OPEN_SEASON_ARENA:
                ArenaManager.getInstance().taskStartSeason(zoneExtension.getParentZone());
                break;
            case CMD.InternalMessage.ON_PRIVATE_CHAT:
                ((PushNotifyHandler) extension.getServerHandler(Params.Module.MODULE_PUSH_NOTIFY)).pushNotifyManager.onPrivateChat(rec.getLong(Params.FROM), rec.getLong(Params.TO));
                break;
            case CMD.InternalMessage.ON_ALLIANCE_CHAT:
                ((PushNotifyHandler) extension.getServerHandler(Params.Module.MODULE_PUSH_NOTIFY)).pushNotifyManager.onAllianceChat(rec.getLongArray(Params.TO));
                break;
            case CMD.InternalMessage.ON_GLOBAL_CHAT:
                ((PushNotifyHandler) extension.getServerHandler(Params.Module.MODULE_PUSH_NOTIFY)).pushNotifyManager.onGlobalChat(rec.getLong(Params.FROM));
                break;
            case CMD.InternalMessage.ON_CHANEL_CHAT:
                ((PushNotifyHandler) extension.getServerHandler(Params.Module.MODULE_PUSH_NOTIFY)).pushNotifyManager.onChanelChat(rec.getLong(Params.FROM), rec.getUtfString(Params.CHANEL_ID));
                break;
            case "tools_get_all_character":
                res = CharactersConfigManager.getInstance().getAllCharacter();
                break;
            case "set_campaign":
                CampaignManager.getInstance().setCampaign(rec);
                break;
            case CMD.InternalMessage.GET_NFT_HERO_LIST:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).GetListNFTHeroInfo(rec);
                break;
            case CMD.InternalMessage.MINT_NFT_HERO:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).MintNFTHero(rec);
                break;
            case CMD.InternalMessage.VERIFY_MINT_NFT_HERO:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).VerifyNFTHero(rec);
                break;
            case CMD.InternalMessage.TRANFER_NFT_HERO:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).TranferNFTHero(rec);
                break;
            case CMD.InternalMessage.TRANSFER_HERO_FROM_WALLET:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).transferHeroFromWallet(rec);
                break;
            case CMD.InternalMessage.LOCK_HERO:
                res = ((HeroHandler) extension.getServerHandler(Params.Module.MODULE_HERO)).LockHero(rec);
                break;
            case CMD.InternalMessage.UNLOCK_HERO:
                res = ((HeroHandler) extension.getServerHandler(Params.Module.MODULE_HERO)).UnlockHero(rec);
                break;
            case CMD.InternalMessage.GET_SOG_OF_USER:
                res = ((UserHandler) extension.getServerHandler(Params.Module.MODULE_USER)).getUserInfo(rec);
                break;
            case CMD.InternalMessage.UPDATE_USERNAME_PASSWORD:
                res = ((UserHandler) extension.getServerHandler(Params.Module.MODULE_USER)).updateUsername(rec);
                break;
            case CMD.InternalMessage.CHANGE_PASSWORD:
                res = ((UserHandler) extension.getServerHandler(Params.Module.MODULE_USER)).changePassword(rec);
                break;
            case CMD.InternalMessage.DEPOSIT_TOKEN:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).depositToken(rec);
                break;
            case CMD.InternalMessage.WITHDRAW_TOKEN:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).doVerifyClaimToken(rec);
                break;
            case CMD.InternalMessage.REQUEST_WITHDRAW_TOKEN:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).doClaimTokenMine(rec);
                break;
            case CMD.InternalMessage.REJECT_WITHDRAW_TOKEN:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).doReturnClaimToken(rec);
                break;
            case CMD.InternalMessage.HTTP_SUM_HERO:
                res = ((HeroHandler) extension.getServerHandler(Params.Module.MODULE_HERO)).handleHttpSumHero(rec);
                break;

            case CMD.HttpCMD.ASCEND_HERO:
                res = ((HeroHandler) extension.getServerHandler(Params.Module.MODULE_HERO)).doUpStarUserHero(rec);
                break;

            case CMD.HttpCMD.CANCEL_ASCEND_HERO:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).doReturnNFTHeroUpStar(rec);
                break;

            case CMD.HttpCMD.CONFIRM_ASCEND_HERO:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).doVerifyUpStarNFTHero(rec);
                break;
            case CMD.HttpCMD.GET_HERO_BREEDING:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).listHeroCountdown(rec);
                break;
            case CMD.HttpCMD.LIST_HERO_ASCEND:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).doGetListHeroAscend(rec);
                break;
            case CMD.HttpCMD.GET_HERO_ASCEND_STATS:
                res = ((HeroHandler) extension.getServerHandler(Params.Module.MODULE_HERO)).getHeroAscendStats(rec);
                break;
            case CMD.InternalMessage.HTTP_LINK_WALLET:
                res = ((UserHandler) extension.getServerHandler(Params.Module.MODULE_USER)).linkWallet(rec);
                break;
            case CMD.HttpCMD.CHECK_HERO_FOR_SALE:
                res = ((HeroHandler) extension.getServerHandler(Params.Module.MODULE_HERO)).checkHeroForSale(rec);
                break;
            case CMD.HttpCMD.LIST_HERO_OPEN_BOX:
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).getListHeroOpenBox(rec);
                break;
            case CMD.HttpCMD.RESET_RMQ:
                res = ((UserHandler) extension.getServerHandler(Params.Module.MODULE_USER)).resetRmq();
                break;
            case "breed":
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).handleBreed(rec);
                break;
            case "countdown":
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).getListHeroCountdown(rec);
                break;
            case "list_hero":
                res = ((NFTHandler) extension.getServerHandler(Params.Module.MODULE_NFT)).getListHeroBreed(rec);
                break;
            case "delete_hero":
                res = ((HeroHandler) extension.getServerHandler(Params.Module.MODULE_HERO)).DeleteHero(rec);
                break;
            case "delete_hero_block":
                res = ((HeroHandler) extension.getServerHandler(Params.Module.MODULE_HERO)).DeleteHeroBlock(rec);
                break;
            case "move_hero":
                res = ((HeroHandler) extension.getServerHandler(Params.Module.MODULE_HERO)).moveHero(rec);
                break;
            case "move_hero_upstar":
                res = ((HeroHandler) extension.getServerHandler(Params.Module.MODULE_HERO)).removeHeroUpStar(rec);
                break;
        }
        return res;
    }

    public ISFSObject test(ISFSObject rec){
//        String username = rec.getUtfString(Params.USER_NAME);
////        long uid = extension.getUserManager().getUserModelByKey(username).userID;
//        List<ResourcePackage> list = new ArrayList<>();
//        list.add(new ResourcePackage("MON1000", 100));
//        list.add(new ResourcePackage("MON1000", 100));
//        list.add(new ResourcePackage("MON1000", 100));
//        ((MailHandler)extension.getServerHandler(Params.Module.MODULE_MAIL)).getMailManager().createMailAdmin(extension.getParentZone(), "1000", "1010", list);
//        ISFSObject objPut = new SFSObject();
        return null;
    }
}
