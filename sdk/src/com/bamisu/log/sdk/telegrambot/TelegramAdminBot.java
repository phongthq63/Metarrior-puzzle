package com.bamisu.log.sdk.telegrambot;

import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.gamethriftclient.IAP.entities.InfoIAPSale;
import com.bamisu.log.sdk.module.gamethriftclient.event.entities.EventConfigManager;
import com.bamisu.log.sdk.module.gamethriftclient.event.entities.InfoEventNoti;
import com.bamisu.log.sdk.module.gamethriftclient.mail.entities.MailInfo;
import com.bamisu.log.sdk.module.giftcode.GiftcodeManager;
import com.bamisu.log.sdk.module.giftcode.model.GiftcodeModel;
import com.bamisu.log.sdk.module.multiserver.MultiServerManager;
import com.bamisu.log.sdk.telegrambot.exception.TimeEventInvalidException;
import com.smartfoxserver.v2.entities.data.SFSArray;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Create by Popeye on 10:22 AM, 10/2/2020
 */
public class TelegramAdminBot extends TelegramLongPollingBot {
    private static TelegramAdminBot ourInstance = new TelegramAdminBot();

    public static TelegramAdminBot getInstance() {
        if (ourInstance == null) {
            ourInstance = new TelegramAdminBot();
        }
        return ourInstance;
    }

    public String uname;
    public String token;

    public Logger logger = Logger.getLogger(this.getClass());

    private TelegramAdminBot() {
        super();
        try {
            this.uname = ConfigHandle.instance().get("telegram_bot_name");
            this.token = ConfigHandle.instance().get("telegram_bot_api");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getChatId() + "-" + update.getMessage().getFrom().getUserName() + " : " + update.getMessage().getText());
        String content = update.getMessage().getText();
        String repText = "Sai cú pháp. Nhấn /help để xem cú pháp";
        SendMessage sendMessage = null;

        try {
            BotTokenModel botTokenModel = BotTokenModel.copyFromDBtoObject(SDKDatacontroler.getInstance().getController());
            String[] args = content.split(";;");
            if (args[0].equalsIgnoreCase("/help")) {
                repText = "Danh sách lệnh: \n\n";
                repText += "* christmas_on: bật sự kiện xmas \n\n";
                repText += "* maintenace: bảo trì server \n\n";
                repText += "* ccu: check ccu \n\n";
                repText += "* create_giftcode [code] [expired(timestamp)] [max(số lần sử dụng tối đa)] [gifts(list ResourcePackage)]: tạo giftcode \n\n";
                repText += "* send_mail [server id(if input <= 0 mean all server)] [list user id(seprelater by ,)(if input , mean all user)] [title id] [content id] [gift(list ResourcePackage json)]: phát thư \n\n";
                repText += "* send_gift_arena_daily [server id(if don't input mean all server)]: phát quà đấu trường hằng ngày \n\n";
                repText += "* close_season_arena [server id(if don't input mean all server address)]: đóng mùa giải đấu trường hiện tại đồng thời phát phần thưởng cuối mùa giải \n\n";
                repText += "* open_season_arena [server id(if don't input mean all server address)]: mở mùa giải đấu trường hiện tại đồng thời cập nhật lại danh sách xếp hạng và điểm người chơi \n " +
                        "(Lưu ý: API này KHÔNG phát thưởng cho người chơi ) \n\n";
                repText += "* buy_iap [server id] [user id] [special package id]: mua gói in-app purchase cho người chơi \n " +
                        "(Lưu ý: API CHỈ mua gói hiện đang có, sẽ không mua được gói không tồn tại hoặc đã hết hạn ) \n\n";

                repText += "Cú pháp lệnh: \"[token];;[lệnh];;[tham số 1];;[tham số 2] ...\" \n \n";
            }else

            if(args[0].equalsIgnoreCase("token")){
                botTokenModel.token ++;
                botTokenModel.saveToDB(SDKDatacontroler.getInstance().getController());
                repText = "Token: " + botTokenModel.token;
            }

            //check token
            else if (botTokenModel.token != Integer.parseInt(args[0])) {
                repText = "Sai token";
            } else {
                botTokenModel.token ++;
                botTokenModel.saveToDB(SDKDatacontroler.getInstance().getController());

                if(args[1].equalsIgnoreCase("christmas_on")){
                    int time = EventConfigManager.getInstance().getTimeEndEvent("christmas");
                    int now = Utils.getTimestampInSecond();
                    if(time <= now){
                        throw new TimeEventInvalidException();
                    }

                    MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("vip1_1m", "vip1_1m_sale", (float) 3.99, time - now, new ArrayList<>()));
                    MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("vip2_1m", "vip2_1m_sale", (float) 7.49, time - now, new ArrayList<>()));

                    MultiServerManager.getInstance().addEventGeneral(0, InfoEventNoti.create("christmas", time));

                    MultiServerManager.getInstance().updateEventModuleServer(0, "iap", true, time);
                    MultiServerManager.getInstance().updateEventModuleServer(0, "quest", true, time);
                    repText = "Bật sự kiện christmas thành công";
                }
                
                if(args[1].equalsIgnoreCase("maintenace")){
                    if(args[2].equalsIgnoreCase("true")){
                        ServerConstant.PRE_MAINTENANCE = true;
                        MultiServerManager.getInstance().maintenanceServer(ServerConstant.PRE_MAINTENANCE);
                        repText = "Bật bảo trì server thành công thành công";
                    }else {
                        ServerConstant.PRE_MAINTENANCE = false;
                        MultiServerManager.getInstance().maintenanceServer(ServerConstant.PRE_MAINTENANCE);
                        repText = "tắt bảo trì thành công";
                    }

                }

                if(args[1].equalsIgnoreCase("ccu")){
                    repText = Utils.toJson(MultiServerManager.getInstance().getCCUAllServer());
                }

                if(args[1].equalsIgnoreCase("create_giftcode")){
                    String code = args[2];
                    int expired = Integer.parseInt(args[3]);
                    int max = Integer.parseInt(args[4]);
                    List<ResourcePackage> gifts = new ArrayList<>();
                    SFSArray sfsArray = SFSArray.newFromJsonData(args[5]);
                    for (int i = 0; i < sfsArray.size(); i++){
                        gifts.add(Utils.fromJson(sfsArray.getSFSObject(i).toJson(), ResourcePackage.class));
                    }

                    GiftcodeModel giftcodeModel = GiftcodeManager.getInstance().genCode(code, expired, max, gifts);
                    repText = Utils.toJson(giftcodeModel);
                }

                if(args[1].equalsIgnoreCase("send_mail")){
                    int serverID = (args.length - 1 < 2) ? 0 : Integer.parseInt(args[2]);
                    String[] uids = (args.length - 1 < 3) ? new String[0] : args[3].split(",");
                    String titleId = (args.length - 1 < 4) ? "ADMIN" : args[4];
                    String contentId = (args.length - 1 < 5) ? "" : args[5];
                    String giftJson = (args.length - 1 < 6) ? "[]" : args[6];

                    List<Long> uidsLong = new ArrayList<>();
                    for(String uid : uids){
                        uidsLong.add(Long.parseLong(uid));
                    }

                    repText = "Phát thư: \n" + MultiServerManager.getInstance().sendMailToPlayer(serverID, uidsLong, MailInfo.create(titleId, contentId, giftJson));
                }

                if(args[1].equalsIgnoreCase("send_gift_arena_daily")){
                    int serverID = (args.length - 1 < 2) ? 0 : Integer.parseInt(args[2]);

                    repText = "Gửi quà đấu trường hằng ngày " + MultiServerManager.getInstance().sendGiftArenaDaily(serverID);
                }

                if(args[1].equalsIgnoreCase("close_season_arena")){
                    int serverID = (args.length - 1 < 2) ? 0 : Integer.parseInt(args[2]);

                    repText = "Đóng mùa giải đấu trường " + MultiServerManager.getInstance().closeSeasonArena(serverID);
                }

                if(args[1].equalsIgnoreCase("open_season_arena")){
                    int serverID = (args.length - 1 < 2) ? 0 : Integer.parseInt(args[2]);

                    repText = "Mở mùa giải đấu trường " + MultiServerManager.getInstance().openSeasonArena(serverID);
                }

                if(args[1].equalsIgnoreCase("open_black_friday")){
                    int time = EventConfigManager.getInstance().getTimeEndEvent("black_friday");
                    int now = Utils.getTimestampInSecond();

                    MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("prestige1", "prestige1_sale", (float) 12.49, time - now, new ArrayList<>()));
                    MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("prestige2", "prestige2_sale", (float) 14.99, time - now, new ArrayList<>()));
                    MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("prestige3", "prestige3_sale", (float) 9.99, time - now, new ArrayList<>()));

                    MultiServerManager.getInstance().addEventSpecial(0, InfoEventNoti.create("sale", time));
                    MultiServerManager.getInstance().addEventSpecial(0, InfoEventNoti.create("black_friday", time));

                    repText = "open_black_friday ok";
                }

                if(args[1].equalsIgnoreCase("buy_iap")){
                    int serverID = (args.length - 1 < 2) ? 0 : Integer.parseInt(args[2]);
                    int uid = (args.length - 1 < 3) ? 0 : Integer.parseInt(args[3]);
                    String idSpecialPackage = (args.length - 1 < 3) ? "" : args[4];

                    repText = "add special iap " + MultiServerManager.getInstance().buyPackageIAP(serverID, uid, idSpecialPackage);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
//            repText = Utils.exceptionToString(ex);
        }

        try {
            sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());
            sendMessage.setText(repText);
            sendApiMethod(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        // TODO
        return uname;
    }

    @Override
    public String getBotToken() {
        // TODO
        return token;
    }

    public void sendToChatID(String chatID, String content) {
        SendMessage sendMessage = new SendMessage().setChatId(chatID);
        sendMessage.setText(content);
        try {
            sendApiMethod(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
