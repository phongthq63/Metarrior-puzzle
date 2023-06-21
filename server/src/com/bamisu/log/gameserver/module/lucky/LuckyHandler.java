package com.bamisu.log.gameserver.module.lucky;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.lucky.*;
import com.bamisu.log.gameserver.datamodel.lucky.entities.LuckyBuyTicketInfo;
import com.bamisu.log.gameserver.datamodel.lucky.entities.LuckyWinner;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.lucky.cmd.receive.RecBuyLuckyNo;
import com.bamisu.log.gameserver.module.lucky.cmd.receive.RecReward;
import com.bamisu.log.gameserver.module.lucky.cmd.receive.RecUserHistoryBuy;
import com.bamisu.log.gameserver.module.lucky.cmd.send.*;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.bamisu.gamelib.utils.Utils.DATE_FORMAT2;

public class LuckyHandler extends ExtensionBaseClientRequestHandler {
    LuckyManager luckyManager;

    public LuckyHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_LUCKY;
        this.luckyManager = LuckyManager.getInstance();
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId) {
            case CMD.CMD_GET_LUCKY_PRICE:
                doGetJackpotForUser(user);
                break;
            case CMD.CMD_BUY_LUCKY:
                doBuyJackpot(user, data);
                break;
            case CMD.CMD_HIST_WINNER:
                doGetHistoryWinner(user, data);
                break;
            case CMD.CMD_REWARD_LUCKY:
                doGetReward(user, data);
                break;
            case CMD.CMD_HIST_LUCKY:
                doGetBuyHistoryUser(user, data);
                break;
            case CMD.CMD_AMOUNT_TO_BUY_TICKETS:
                doSendAmountTickets(user);
                break;
        }
    }

    // gửi số tiền hũ hiện tại cho user khi mới đăng nhập vào loto - 66001
    @WithSpan
    private void doGetJackpotForUser(User user) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        // khởi tạo hũ jackpot
        LuckyModel jportModel = LuckyModel.copyFromDBtoObject(uid, user.getZone());
        if (jportModel == null) {
            jportModel = LuckyModel.createJackpot(uid, user.getZone());
        }

        // khởi tạo hũ trả thưởng
        LuckyRewardModel luckyRewardModel = LuckyRewardModel.copyFromDBtoObject(uid, user.getZone());
        if (luckyRewardModel == null) {
            luckyRewardModel = LuckyRewardModel.createRewardModel(uid, user.getZone());
        }

        // khởi tạo hũ hệ thống
        LuckyPublisherModel luckyPublisherModel = LuckyPublisherModel.copyFromDBtoObject(uid, user.getZone());
        if (luckyPublisherModel == null) {
            luckyPublisherModel = LuckyPublisherModel.createPublisherModel(uid, user.getZone());
        }

        BuyTicketOfDayModel buyTicketOfDayModel = BuyTicketOfDayModel.copyFromDBtoObject(Utils.dateNowToLong(), user.getZone());
        if (buyTicketOfDayModel == null) {
            buyTicketOfDayModel = BuyTicketOfDayModel.createUserHistory(user.getZone());
        }

        LuckyWinnerModel luckyWinnerModel = LuckyWinnerModel.copyFromDBtoObject(Utils.dateNowToLong(), user.getZone());
        if (luckyWinnerModel == null) {
            luckyWinnerModel = LuckyWinnerModel.createLuckyWinnerModel(user.getZone());
        }

        SendJackpotPrice objPut = new SendJackpotPrice();
        objPut.jportModel = jportModel;
        send(objPut, user);
    }

    @WithSpan
    private void doSendAmountTickets(User user) {
        SendAmountBuyTickets sendAmountBuyTickets = new SendAmountBuyTickets();
        send(sendAmountBuyTickets, user);
    }

    // lịch sử user mua ticket - 66007
    @WithSpan
    private void doGetBuyHistoryUser(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecUserHistoryBuy objRec = new RecUserHistoryBuy(data);
        SendGetListHistoryLucky sendGetListHistoryLucky = new SendGetListHistoryLucky();

        //get jackpot - lich sử mua ticket theo ngay
        if (objRec.date != null && !objRec.date.equals("")) {
            LuckyNumberModel objLuckyNo = LuckyNumberModel.copyFromDBtoObject(objRec.date.replaceAll("-", ""), user.getZone());
            sendGetListHistoryLucky.listLuckyNumberModel.add(objLuckyNo);

            BuyTicketOfDayModel buyTicketOfDayModel = BuyTicketOfDayModel.copyFromDBtoObject(objRec.date.replaceAll("-", ""), user.getZone());
            if (buyTicketOfDayModel != null) { // nếu ngày truyền vào có lịch sử mua
                List<LuckyBuyTicketInfo> listBuyTicket = buyTicketOfDayModel.history.stream().filter(x -> x.uid == uid).collect(Collectors.toList());
                List<LuckyWinner> listConvert = listBuyTicket.stream()
                        .map(x -> {
                            LuckyWinner lw = new LuckyWinner();
                            lw.userId = x.uid;
                            lw.no1 = x.no1;
                            lw.no2 = x.no2;
                            lw.no3 = x.no3;
                            lw.type = x.type;
                            lw.amount = x.amount;

                            lw.isWin = "";
                            lw.priceAmt = 0;
                            lw.totalPrice = 0;
                            lw.received = 1;
                            lw.createDate = "";
                            lw.name = "";
                            return lw;
                        }).collect(Collectors.toList());

                List<LuckyWinner> listAllSog = new ArrayList<>();
                LuckyWinnerModel luckyWinnerModel = LuckyWinnerModel.copyFromDBtoObject(objRec.date.replaceAll("-", ""), user.getZone());
                List<LuckyWinner> listWinnerSog3 = luckyWinnerModel.listSOGWin3.stream().filter(x -> x.userId == uid).collect(Collectors.toList());
                if (!listWinnerSog3.isEmpty()) {
                    int index = IntStream.range(0, listConvert.size())
                            .filter(a -> listConvert.get(a).no1 == listWinnerSog3.get(0).no1
                                    && listConvert.get(a).no2 == listWinnerSog3.get(0).no2
                                    && listConvert.get(a).no3 == listWinnerSog3.get(0).no3)
                            .findFirst()
                            .orElse(-1);
                    if (index != -1) {
                        listConvert.remove(index);
                        listAllSog.addAll(listConvert);
                        listAllSog.addAll(listWinnerSog3);
                    }
                } else {
                    List<LuckyWinner> listWinner = new ArrayList<>();
                    List<LuckyWinner> listWinnerSog1 = luckyWinnerModel.listSOGWin1.stream().filter(x -> x.userId == uid).collect(Collectors.toList());
                    List<LuckyWinner> listWinnerSog2 = luckyWinnerModel.listSOGWin2.stream().filter(x -> x.userId == uid).collect(Collectors.toList());

                    listWinner.addAll(listWinnerSog1);
                    listWinner.addAll(listWinnerSog2);

                    for (int i = 0; i < listWinner.size(); i++) {
                        int finalI = i;
                        int index = IntStream.range(0, listConvert.size())
                                .filter(a -> listConvert.get(a).no1 == listWinner.get(finalI).no1
                                        && listConvert.get(a).no2 == listWinner.get(finalI).no2
                                        && listConvert.get(a).no3 == listWinner.get(finalI).no3)
                                .findFirst()
                                .orElse(-1);
                        if (index != -1) {
                            listConvert.remove(index);
                        }
                    }
                    listAllSog.addAll(listConvert);
                    listAllSog.addAll(listWinner);
                }

                sendGetListHistoryLucky.listHistory = listAllSog;
            } else { // nếu ngày truyền vào k có lịch sử mua thì trả ra list trống
                List<LuckyWinner> list = new ArrayList<>();
                sendGetListHistoryLucky.listHistory = list;
            }

        } else { // lấy giá trị ngày hiện tại
            LuckyNumberModel objLuckyNo = LuckyNumberModel.copyFromDBtoObject(Utils.dateNowToLong(), user.getZone());
            sendGetListHistoryLucky.listLuckyNumberModel.add(objLuckyNo);

            BuyTicketOfDayModel buyTicketOfDayModel = BuyTicketOfDayModel.copyFromDBtoObject(Utils.dateNowToLong(), user.getZone());
            List<LuckyBuyTicketInfo> list = buyTicketOfDayModel.history.stream().filter(x -> x.uid == uid).collect(Collectors.toList());
            List<LuckyWinner> listConvert = list.stream()
                    .map(x -> {
                        LuckyWinner lw = new LuckyWinner();
                        lw.userId = x.uid;
                        lw.no1 = x.no1;
                        lw.no2 = x.no2;
                        lw.no3 = x.no3;
                        lw.type = x.type;
                        lw.amount = x.amount;

                        lw.isWin = "";
                        lw.priceAmt = 0;
                        lw.totalPrice = 0;
                        lw.received = 1;
                        lw.createDate = "";
                        lw.name = "";
                        return lw;
                    }).collect(Collectors.toList());
            sendGetListHistoryLucky.listHistory = listConvert;
        }

        send(sendGetListHistoryLucky, user);
    }

    // lấy ra lịch sử winner - 66003 - couchbase
    @WithSpan
    private void doGetHistoryWinner(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        List<LuckyWinner> list = new ArrayList<>();
        List<String> listDay = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime day = now.minusDays(i);
            String time = day.format(DateTimeFormatter.ofPattern(DATE_FORMAT2));
            listDay.add(time);
        }

        for (int i = 0; i < listDay.size(); i++) {
            LuckyWinnerModel luckyWinnerModel = LuckyWinnerModel.copyFromDBtoObject(listDay.get(i), user.getZone());
            if (luckyWinnerModel != null) {
                list.addAll(luckyWinnerModel.listSOGWin1);
                list.addAll(luckyWinnerModel.listSOGWin2);
                list.addAll(luckyWinnerModel.listSOGWin3);
            }
        }

        // lấy ra lịch sử trúng thưởng trên DB
        List<LuckyWinner> listLuckyWinnerDB = LuckyManager.getInstance().getHistoryToDB(user.getZone());
        list.addAll(listLuckyWinnerDB);
        SendGetHistoryWinner sendGetHistoryWinner = new SendGetHistoryWinner();
        sendGetHistoryWinner.listLuckyWinners = list;
        send(sendGetHistoryWinner, user);
    }

    // nhận thưởng - couchbase
    @WithSpan
    private void doGetReward(User user, ISFSObject data) {
        short errorCode = 0;
        RecReward objGet = new RecReward(data);
        long uid = extension.getUserManager().getUserModel(user).userID;
        int price = luckyManager.getRewardPrice(uid, objGet.type, objGet.create_day.replaceAll("-", ""), user.getZone());
        //int price = luckyManager.getRewardPrice(uid, user.getZone(), objGet.type, objGet.create_day);

        if (price != 0) {
            List<TokenResourcePackage> reward = Collections.singletonList(new TokenResourcePackage(objGet.type, +price));
            if (luckyManager.updateReward(uid, price, objGet, user.getZone())) {
                if (luckyManager.updateToken(uid, reward, UserUtils.TransactionType.REWARD_LUCK_JACK, getParentExtension().getParentZone()).isSuccess()) {
                    SendGetRewardLucky sendObj = new SendGetRewardLucky();
                    sendObj.price = price;
                    sendObj.type = objGet.type;
                    send(sendObj, user);
                    return;
                }
            }
        } else {
            errorCode = ServerConstant.ErrorCode.ERR_CLAIM_REWARD;
            SendGetRewardLucky sendGetRewardLucky = new SendGetRewardLucky(errorCode);
            send(sendGetRewardLucky, user);
        }
    }

    // gửi thông tin jackport (mewa-sog) sau khi user mua ticket tới tất cả user trong zone hiện tại
    @WithSpan
    private void doGetJackpot(User user) {
        List<User> list = (List<User>) getParentExtension().getParentZone().getUserList();

        long uid = extension.getUserManager().getUserModel(user).userID;
        LuckyModel jportModel = LuckyModel.copyFromDBtoObject(uid, user.getZone());
        SendJackpotPrice objPut = new SendJackpotPrice();
        objPut.jportModel = jportModel;
        send(objPut, list);
    }

    // mua ticket - code mới thao tác trên couch base
    @WithSpan
    private void doBuyJackpot(User user, ISFSObject data) {
        UserModel um = extension.getUserManager().getUserModel(user);
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecBuyLuckyNo objGet = new RecBuyLuckyNo(data);
        short errorCode = 0;
        if (luckyManager.checkTimeBuyTicket()) {
            if (luckyManager.checkDuplicateTicket(uid, objGet, user.getZone()) == false) {
                if (objGet.type.equals(MoneyType.VOUCHER_LOTO_SOG.getId())) {// dùng thẻ VOUCHER_LOTO_SOG để mua
                    UserBagModel userBagModel = UserBagModel.copyFromDBtoObject(uid, user.getZone());
                    int count = userBagModel.mapMoney.get(MoneyType.VOUCHER_LOTO_SOG.getId()).amount;
                    if (count > 0) {
                        List<MoneyPackageVO> money = Collections.singletonList(new MoneyPackageVO(MoneyType.VOUCHER_LOTO_SOG.getId(), -1));
                        if (BagManager.getInstance().addItemToDB(money, uid, user.getZone(), UserUtils.TransactionType.BUY_LUCKY_JACK)) {
                            luckyManager.buyLucky(uid, objGet, user.getZone());
                            SendBuyLucky send = new SendBuyLucky();
                            send(send, user);
                            return;
                        }
                    } else {
                        errorCode = ServerConstant.ErrorCode.ERR_NOT_ENOUGH_MONEY;
                    }

                } else { // dung resource(sog) để mua
                    //tru tien truoc
                    List<TokenResourcePackage> buyLucky = Collections.singletonList(new TokenResourcePackage(objGet.type, objGet.type.equals("MEWA") ? -luckyManager.amountMewaOfTicket : -luckyManager.amountSogOfTicket));

                    if (luckyManager.updateToken(um.userID, buyLucky, UserUtils.TransactionType.BUY_LUCKY_JACK, getParentExtension().getParentZone()).isSuccess()) {
                        luckyManager.buyLucky(uid, objGet, user.getZone());
                        luckyManager.updateJackpot(um.userID, objGet, user.getZone());
                        SendBuyLucky send = new SendBuyLucky();
                        send(send, user);
                        doGetJackpot(user);
                        return;
                    } else {
                        errorCode = ServerConstant.ErrorCode.ERR_NOT_ENOUGH_MONEY;
                    }
                }

            } else {
                errorCode = ServerConstant.ErrorCode.ERR_TICKET_DUPLICATE;
            }
        } else {
            errorCode = ServerConstant.ErrorCode.ERR_OVER_TIME_TO_BUY_TICKET;
        }
        SendBuyLucky send = new SendBuyLucky(errorCode);
        send(send, user);

    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_LUCKY, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_LUCKY, this);
    }
}
