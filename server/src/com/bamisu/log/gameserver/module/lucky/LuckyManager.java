package com.bamisu.log.gameserver.module.lucky;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.TokenResourcePackage;
import com.bamisu.gamelib.entities.TransactionDetail;
import com.bamisu.gamelib.sql.lucky.HistoryWinnerDBO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.lucky.*;
import com.bamisu.log.gameserver.datamodel.lucky.entities.LuckyBuyTicketInfo;
import com.bamisu.log.gameserver.datamodel.lucky.entities.LuckyWinner;
import com.bamisu.log.gameserver.datamodel.nft.UserTokenModel;
import com.bamisu.log.gameserver.module.lucky.cmd.receive.RecBuyLuckyNo;
import com.bamisu.log.gameserver.module.lucky.cmd.receive.RecReward;
import com.bamisu.log.gameserver.module.nft.entities.ChangeTokenResult;
import com.bamisu.log.gameserver.sql.lucky.dao.LuckyDAO;
import com.smartfoxserver.v2.entities.Zone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class LuckyManager {
    private static LuckyManager ourInstance = null;

    public static LuckyManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new LuckyManager();
        }
        return ourInstance;
    }

    // tính toán kết quả xổ số hàng ngày
    public void doCreateWinner(Zone zone) {
        LuckyWinnerModel luckyWinnerModel = LuckyWinnerModel.copyFromDBtoObject(Utils.dateNowToLong(), zone);
        if (luckyWinnerModel == null) {
            luckyWinnerModel = LuckyWinnerModel.createLuckyWinnerModel(zone);
        }
        LuckyModel jackpot = LuckyModel.copyFromDBtoObject(1L, zone);
        LuckyRewardModel luckyRewardModel = LuckyRewardModel.copyFromDBtoObject(2L, zone);
        LuckyPublisherModel luckyPublisherModel = LuckyPublisherModel.copyFromDBtoObject(3L, zone);
        LuckyNumberModel lnm = LuckyNumberModel.copyFromDBtoObject(Utils.dateNowToLong(), zone);
        BuyTicketOfDayModel btodm = BuyTicketOfDayModel.copyFromDBtoObject(Utils.dateNowToLong(), zone);
        if (btodm == null) {
            btodm = BuyTicketOfDayModel.createUserHistory(zone);
        }

        // danh sách ticket SOG trúng 3 số
        List<LuckyBuyTicketInfo> listWinSOG3 = btodm.history.stream().filter(x ->
                        x.no1 == lnm.num1 && x.no2 == lnm.num2 && x.no3 == lnm.num3 && x.type.equals("SOG"))
                .collect(Collectors.toList());

        // danh sách ticket SOG trúng 2 số
        List<LuckyBuyTicketInfo> listWinSOG2 = btodm.history.stream().filter(x ->
                        x.no1 != lnm.num1 && x.no2 == lnm.num2 && x.no3 == lnm.num3 && x.type.equals("SOG"))
                .collect(Collectors.toList());

        // danh sách ticket SOG trúng 1 số
        List<LuckyBuyTicketInfo> listWinSOG1 = btodm.history.stream().filter(x ->
                        x.no2 != lnm.num2 && x.no3 == lnm.num3 && x.type.equals("SOG"))
                .collect(Collectors.toList());

//        // danh sách ticket MEWA trúng 3 số
//        List<LuckyBuyTicketInfo> listWinMEWA3 = btodm.history.stream().filter(x ->
//                        x.no1 == lnm.num1 && x.no2 == lnm.num2 && x.no3 == lnm.num3 && x.type.equals("MEWA"))
//                .collect(Collectors.toList());
//
//        // danh sách ticket MEWA trúng 2 số
//        List<LuckyBuyTicketInfo> listWinMEWA2 = btodm.history.stream().filter(x ->
//                        x.no1 != lnm.num1 && x.no2 == lnm.num2 && x.no3 == lnm.num3 && x.type.equals("MEWA"))
//                .collect(Collectors.toList());
//
//        // danh sách ticket MEWA trúng 1 số
//        List<LuckyBuyTicketInfo> listWinMEWA1 = btodm.history.stream().filter(x ->
//                        x.no2 != lnm.num2 && x.no3 == lnm.num3 && x.type.equals("MEWA"))
//                .collect(Collectors.toList());


        // lưu các ticket trúng thưởng SOG vào couchbase
        if (!listWinSOG3.isEmpty()) { // trúng 3 số
            List<LuckyWinner> listSOGWinner3 = listWinSOG3.stream()
                    .map(x -> {
                        LuckyWinner lw = new LuckyWinner();
                        lw.userId = x.uid;
                        lw.no1 = x.no1;
                        lw.no2 = x.no2;
                        lw.no3 = x.no3;
                        lw.type = "SOG";
                        lw.amount = amountSogOfTicket;
                        lw.isWin = "3";
                        lw.priceAmt = Math.round((jackpot.sog * is85Percentage) / listWinSOG3.size());
                        lw.totalPrice = (int) jackpot.sog;
                        lw.received = 0;
                        lw.createDate = Utils.dateNowString();
                        lw.name = UserModel.copyFromDBtoObject(x.uid, zone).displayName;
                        return lw;
                    })
                    .collect(Collectors.toList());

            luckyWinnerModel.listSOGWin3.addAll(listSOGWinner3);
            luckyWinnerModel.saveToDB(zone);

            //// tính toán cập nhật lại tiền vào các hũ
            // tính số tiền SOG phải trả cho user
            int sumSOG3 = luckyWinnerModel.listSOGWin3.stream().mapToInt(x -> x.priceAmt).sum();

            // cập nhật lại số tiền SOG vào hũ trả thưởng
            luckyRewardModel.sog = luckyRewardModel.sog + sumSOG3;
            luckyRewardModel.saveToDB(zone);

            // hũ hệ thống đc cộng thêm 5% SOG
            luckyPublisherModel.sog = luckyPublisherModel.sog + Math.round(jackpot.sog * is5PercentageIntoPublisher);
            luckyPublisherModel.saveToDB(zone);

            // cập nhật lại số tiền trong jackpot
            jackpot.sog = jackpot.sog - sumSOG3 - Math.round(jackpot.sog * is5PercentageIntoPublisher);
            jackpot.saveToDB(zone);

        } else { // trúng 2 số, trúng 1 số
            List<LuckyWinner> listSOGWinner2 = listWinSOG2.stream()
                    .map(x -> {
                        LuckyWinner lw = new LuckyWinner();
                        lw.userId = x.uid;
                        lw.no1 = x.no1;
                        lw.no2 = x.no2;
                        lw.no3 = x.no3;
                        lw.type = "SOG";
                        lw.amount = amountSogOfTicket;
                        lw.isWin = "2";
                        lw.priceAmt = Math.round((jackpot.sog * is10Percentage) / listWinSOG2.size());
                        lw.totalPrice = (int) jackpot.sog;
                        lw.received = 0;
                        lw.createDate = Utils.dateNowString();
                        lw.name = UserModel.copyFromDBtoObject(x.uid, zone).displayName;
                        return lw;
                    })
                    .collect(Collectors.toList());

            List<LuckyWinner> listSOGWinner1 = listWinSOG1.stream()
                    .map(x -> {
                        LuckyWinner lw = new LuckyWinner();
                        lw.userId = x.uid;
                        lw.no1 = x.no1;
                        lw.no2 = x.no2;
                        lw.no3 = x.no3;
                        lw.type = "SOG";
                        lw.amount = amountSogOfTicket;
                        lw.isWin = "1";
                        lw.priceAmt = Math.round((jackpot.sog * is5Percentage) / listWinSOG1.size());
                        lw.totalPrice = (int) jackpot.sog;
                        lw.received = 0;
                        lw.createDate = Utils.dateNowString();
                        lw.name = UserModel.copyFromDBtoObject(x.uid, zone).displayName;
                        return lw;
                    })
                    .collect(Collectors.toList());

            luckyWinnerModel.listSOGWin2.addAll(listSOGWinner2);
            luckyWinnerModel.listSOGWin1.addAll(listSOGWinner1);
            luckyWinnerModel.saveToDB(zone);

            //// tính toán cập nhật lại tiền vào các hũ
            // tính số tiền SOG phải trả cho user
            int sumSOG2 = luckyWinnerModel.listSOGWin2.stream().mapToInt(x -> x.priceAmt).sum();
            int sumSOG1 = luckyWinnerModel.listSOGWin1.stream().mapToInt(x -> x.priceAmt).sum();

            // cập nhật lại số tiền SOG vào hũ trả thưởng
            luckyRewardModel.sog = luckyRewardModel.sog + sumSOG1 + sumSOG2;
            luckyRewardModel.saveToDB(zone);

            // cập nhật lại số tiền SOG trong jackpot
            jackpot.sog = jackpot.sog - (sumSOG1 + sumSOG2);
            jackpot.saveToDB(zone);
        }

        // Todo: lưu các ticket trúng thưởng MEWA vào couchbase chưa làm
    }

    public void buyLucky(long uid, RecBuyLuckyNo objGet, Zone zone) {
        LuckyBuyTicketInfo lbti = new LuckyBuyTicketInfo();
        lbti.uid = uid;
        lbti.no1 = objGet.no1;
        lbti.no2 = objGet.no2;
        lbti.no3 = objGet.no3;
        if (objGet.type.equals("MON1024")) {
            lbti.type = "SOG";
        } else {
            lbti.type = objGet.type;
        }
        lbti.amount = objGet.type.equals("MEWA") ? amountMewaOfTicket : amountSogOfTicket;
        lbti.time = Utils.getTimestampInSecond();

        BuyTicketOfDayModel buyTicketOfDayModel = BuyTicketOfDayModel.copyFromDBtoObject(Utils.dateNowToLong(), zone);
        buyTicketOfDayModel.history.add(lbti);
        buyTicketOfDayModel.saveToDB(zone);
    }

    public int getRewardPrice(long uid, String type, String create_day, Zone zone) {
        int totalMoney = 0;
        LuckyWinnerModel lwm = LuckyWinnerModel.copyFromDBtoObject(create_day, zone);

        if (type.equals("SOG")) { // nhận thưởng SOG
            List<LuckyWinner> listSOG1 = new ArrayList<>();
            List<LuckyWinner> listSOG2 = new ArrayList<>();
            List<LuckyWinner> listSOG3 = new ArrayList<>();
            listSOG1.addAll(lwm.listSOGWin1);
            listSOG2.addAll(lwm.listSOGWin2);
            listSOG3.addAll(lwm.listSOGWin3);

            // tính tổng tiền sog mà user trúng  1 số theo ngày truyền vào
            List<LuckyWinner> totalSOGreward1 = listSOG1.stream().filter(x -> x.userId == uid && x.received == 0).collect(Collectors.toList());
            int moneySOG1 = 0;
            if (!totalSOGreward1.isEmpty()) {
                moneySOG1 = totalSOGreward1.stream().mapToInt(x -> x.priceAmt).sum();
                for (int i = 0; i < listSOG1.size(); i++) {// thay đổi received từ 0 -> 1
                    int index = IntStream.range(0, listSOG1.size())
                            .filter(a -> listSOG1.get(a).userId == uid && listSOG1.get(a).received == 0)
                            .findFirst()
                            .orElse(-1);

                    if (index != -1) {
                        List<LuckyWinner> list = new ArrayList<>();
                        LuckyWinner obj = new LuckyWinner();
                        obj.userId = listSOG1.get(index).userId;
                        obj.no1 = listSOG1.get(index).no1;
                        obj.no2 = listSOG1.get(index).no2;
                        obj.no3 = listSOG1.get(index).no3;
                        obj.type = listSOG1.get(index).type;
                        obj.amount = listSOG1.get(index).amount;
                        obj.isWin = listSOG1.get(index).isWin;
                        obj.priceAmt = listSOG1.get(index).priceAmt;
                        obj.totalPrice = listSOG1.get(index).totalPrice;
                        obj.received = 1;
                        obj.createDate = listSOG1.get(index).createDate;
                        obj.name = listSOG1.get(index).name;

                        list.add(obj);

                        listSOG1.remove(index);
                        listSOG1.addAll(list);

                        lwm.listSOGWin1 = listSOG1;
                        lwm.saveToDB(zone);
                    }
                }
            }


            // tính tổng tiền sog mà user trúng  2 số theo ngày truyền vào
            List<LuckyWinner> totalSOGreward2 = listSOG2.stream().filter(x -> x.userId == uid && x.received == 0).collect(Collectors.toList());
            int moneySOG2 = 0;
            if (!totalSOGreward2.isEmpty()) {
                moneySOG2 = totalSOGreward2.stream().mapToInt(x -> x.priceAmt).sum();
                for (int i = 0; i < listSOG2.size(); i++) { // thay đổi received từ 0 -> 1
                    int index = IntStream.range(0, listSOG2.size())
                            .filter(a -> listSOG2.get(a).userId == uid && listSOG2.get(a).received == 0)
                            .findFirst()
                            .orElse(-1);

                    if (index != -1) {
                        List<LuckyWinner> list = new ArrayList<>();
                        LuckyWinner obj = new LuckyWinner();
                        obj.userId = listSOG2.get(index).userId;
                        obj.no1 = listSOG2.get(index).no1;
                        obj.no2 = listSOG2.get(index).no2;
                        obj.no3 = listSOG2.get(index).no3;
                        obj.type = listSOG2.get(index).type;
                        obj.amount = listSOG2.get(index).amount;
                        obj.isWin = listSOG2.get(index).isWin;
                        obj.priceAmt = listSOG2.get(index).priceAmt;
                        obj.totalPrice = listSOG2.get(index).totalPrice;
                        obj.received = 1;
                        obj.createDate = listSOG2.get(index).createDate;
                        obj.name = listSOG2.get(index).name;

                        list.add(obj);

                        listSOG2.remove(index);
                        listSOG2.addAll(list);

                        lwm.listSOGWin2 = listSOG2;
                        lwm.saveToDB(zone);
                    }
                }
            }


            // tính tổng tiền sog mà user trúng  3 số theo ngày truyền vào
            List<LuckyWinner> totalSOGreward3 = listSOG3.stream().filter(x -> x.userId == uid && x.received == 0).collect(Collectors.toList());
            int moneySOG3 = 0;
            if (!totalSOGreward3.isEmpty()) {
                moneySOG3 = totalSOGreward3.stream().mapToInt(x -> x.priceAmt).sum();
                for (int i = 0; i < listSOG3.size(); i++) { // thay đổi received từ 0 -> 1
                    int index = IntStream.range(0, listSOG3.size())
                            .filter(a -> listSOG3.get(a).userId == uid && listSOG3.get(a).received == 0)
                            .findFirst()
                            .orElse(-1);

                    if (index != -1) {
                        List<LuckyWinner> list = new ArrayList<>();
                        LuckyWinner obj = new LuckyWinner();
                        obj.userId = listSOG3.get(index).userId;
                        obj.no1 = listSOG3.get(index).no1;
                        obj.no2 = listSOG3.get(index).no2;
                        obj.no3 = listSOG3.get(index).no3;
                        obj.type = listSOG3.get(index).type;
                        obj.amount = listSOG3.get(index).amount;
                        obj.isWin = listSOG3.get(index).isWin;
                        obj.priceAmt = listSOG3.get(index).priceAmt;
                        obj.totalPrice = listSOG3.get(index).totalPrice;
                        obj.received = 1;
                        obj.createDate = listSOG3.get(index).createDate;
                        obj.name = listSOG3.get(index).name;

                        list.add(obj);

                        listSOG3.remove(index);
                        listSOG3.addAll(list);

                        lwm.listSOGWin3 = listSOG3;
                        lwm.saveToDB(zone);
                    }
                }
            }

            totalMoney = moneySOG1 + moneySOG2 + moneySOG3;
        } else {// nhận thưởng Mewa

        }

        return totalMoney;
    }


    // check tgian mua ticket hợp lệ nằm ngoài khoảng 23:29:59 -> 00:59:59 theo giờ UTC
    public boolean checkTimeBuyTicket() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        java.time.LocalDateTime current = LocalDateTime.now(ZoneId.of("UTC"));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        java.util.Date before;
        java.util.Date next;
        java.util.Date now;
        try {
            before = sdf.parse("00:59:59");
            next = sdf.parse("23:29:59");
            now = sdf.parse(current.format(formatter));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (before.before(now) && now.before(next)) {
            return true;
        }
        return false;
    }

    //check for duplicate tickets of each user
    public boolean checkDuplicateTicket(long uid, RecBuyLuckyNo rec, Zone zone) {
        BuyTicketOfDayModel ticket = BuyTicketOfDayModel.copyFromDBtoObject(Utils.dateNowToLong(), zone);
        Boolean checkDuplicate = false;
        if (!ticket.history.isEmpty()) {
            List<LuckyBuyTicketInfo> list = ticket.history.stream().filter(x -> x.uid == uid).collect(Collectors.toList());
            for (LuckyBuyTicketInfo lbti : list) {
                if(rec.type.equals("MON1024")){
                    String type = "SOG";
                    if (rec.no1 == lbti.no1 && rec.no2 == lbti.no2 && rec.no3 == lbti.no3 && type.equals(lbti.type)) {
                        checkDuplicate = true;
                        break;
                    } else {
                        checkDuplicate = false;
                    }
                } else {
                    if (rec.no1 == lbti.no1 && rec.no2 == lbti.no2 && rec.no3 == lbti.no3 && rec.type.equals(lbti.type)) {
                        checkDuplicate = true;
                        break;
                    } else {
                        checkDuplicate = false;
                    }
                }

            }
        }
        if (rec.no1 == -1 && rec.no2 == -1 && rec.no3 == -1) {
            checkDuplicate = true;
        }
        return checkDuplicate;
    }

    public LuckyNumberModel createLuck(Zone zone) {
        List<LuckyNumberModel> oldLuckyResults = new ArrayList<>();

        LuckyNumberModel luckyNumberModel = this.createLuckResult(oldLuckyResults);
        luckyNumberModel.saveToDB(zone);
        return luckyNumberModel;
    }

    public LuckyNumberModel createLuckResult(List<LuckyNumberModel> oldLuckyResults) {
        int number1 = Utils.randRange(0, 99);
        int number2 = Utils.randRange(0, 99);
        int number3 = Utils.randRange(0, 99);

        if (number1 == number2 || number1 == number3 || number2 == number3) {
            return this.createLuckResult(oldLuckyResults);
        }

        Optional<LuckyNumberModel> result = oldLuckyResults.stream().filter(luck ->
                luck.num1 == number1 && luck.num2 == number2 && luck.num3 == number3
        ).findFirst();

        if (result.isPresent()) {
            return this.createLuckResult(oldLuckyResults);
        }

//        // fake kết quả loto 09,05,20
//        int number1 = 9;
//        int number2 = 5;
//        int number3 = 20;

        return LuckyNumberModel.createLuckyNumberModel(number1, number2, number3, Utils.getTimestampInSecond(), Utils.dateNowString());
    }


    public ChangeTokenResult updateToken(long uid, List<TokenResourcePackage> resourcePackageList, TransactionDetail detail, Zone zone) {
        UserTokenModel userTokenModel = getUserMineTokenModel(uid, zone);
        ChangeTokenResult result = userTokenModel.changeToken(resourcePackageList, detail, zone);
        return result;
    }

    // mua ticket -> số tiền mua ticket sẽ đc cộng vào jackpot
    public void updateJackpot(long uid, RecBuyLuckyNo objGet, Zone zone) {
        LuckyModel jportModel = LuckyModel.copyFromDBtoObject(uid, zone);

        if (objGet.type.equals("MEWA")) {
            jportModel.mewa = jportModel.mewa + amountMewaOfTicket;
        } else {
            jportModel.sog = jportModel.sog + amountSogOfTicket;
        }
        jportModel.saveToDB(zone);
    }

    // nhận thưởng -> số tiền sau khi nhận thưởng sẽ trừ vào hũ trả thưởng
    public boolean updateReward(long uid, int price, RecReward objGet, Zone zone) {
        LuckyRewardModel rewardModel = LuckyRewardModel.copyFromDBtoObject(uid, zone);

        if (objGet.type.equals("MEWA")) {
            rewardModel.mewa = rewardModel.mewa - price;
        } else {
            rewardModel.sog = rewardModel.sog - price;
        }
        return rewardModel.saveToDB(zone);
    }

    private UserTokenModel getUserMineTokenModel(long uid, Zone zone) {
        UserTokenModel userTokenModel = UserTokenModel.copyFromDBtoObject(uid, zone);
        if (userTokenModel == null) {
            userTokenModel = UserTokenModel.createUserMineTokenModel(uid, zone);
        }
        return userTokenModel;
    }

    //lấy ra lịch sử trúng thưởng từ DB
    public List<LuckyWinner> getHistoryToDB(Zone zone) {
        List<HistoryWinnerDBO> listluckyDAO = LuckyDAO.getListHistoryWinner(zone);
        List<LuckyWinner> list = listluckyDAO.stream()
                .map(x -> {
                    LuckyWinner lw = new LuckyWinner();
                    lw.userId = x.userId;
                    lw.no1 = 0;
                    lw.no2 = 0;
                    lw.no3 = 0;
                    lw.type = x.type;
                    lw.amount = 0;
                    lw.isWin = x.is_win;
                    lw.priceAmt = x.price_amt;
                    lw.totalPrice = x.total_price;
                    lw.received = 1;
                    lw.createDate = String.valueOf(x.create_date);
                    lw.name = UserModel.copyFromDBtoObject(x.userId, zone).displayName;
                    return lw;
                })
                .collect(Collectors.toList());
        return list;
    }

    // giá tiền của ticket theo mewa - sog
    public static final int amountMewaOfTicket = 50;
    public static final int amountSogOfTicket = 100;

    // tỷ lệ phần trăm chia thưởng
    public static final float is5Percentage = (float) 0.05;
    public static final float is10Percentage = (float) 0.1;
    public static final float is85Percentage = (float) 0.85;

    public static final float is5PercentageIntoPublisher = (float) 0.05;

}
