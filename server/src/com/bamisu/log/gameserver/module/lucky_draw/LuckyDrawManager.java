package com.bamisu.log.gameserver.module.lucky_draw;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.entities.TokenResourcePackage;
import com.bamisu.gamelib.sql.game.dbo.ConfigRankLeagueDBO;
import com.bamisu.gamelib.sql.luckydraw.HistoryLuckyDrawDBO;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.league.RankCampaignModel;
import com.bamisu.log.gameserver.datamodel.league.RankDarkrealmModel;
import com.bamisu.log.gameserver.datamodel.league.RankEndlessnightModel;
import com.bamisu.log.gameserver.datamodel.league.entities.UserRankInfo;
import com.bamisu.log.gameserver.datamodel.luckydraw.LuckyDrawRewardModel;
import com.bamisu.log.gameserver.datamodel.luckydraw.RankTopUserModel;
import com.bamisu.log.gameserver.datamodel.luckydraw.TotalBusdOfUserInSeasonModel;
import com.bamisu.log.gameserver.datamodel.luckydraw.entities.RankUser;
import com.bamisu.log.gameserver.datamodel.luckydraw.entities.TotalBusdUserInSeason;
import com.bamisu.log.gameserver.entities.EStatus;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.darkgate.entities.EDarkGateEvent;
import com.bamisu.log.gameserver.module.darkgate.model.DarkGateModel;
import com.bamisu.log.gameserver.module.lucky_draw.cmd.rev.RecLuckyDrawItemsConfig;
import com.bamisu.log.gameserver.module.lucky_draw.config.LuckyDrawConfig;
import com.bamisu.log.gameserver.module.lucky_draw.config.entities.LuckyDrawVO;
import com.bamisu.log.gameserver.sql.luckydraw.dao.LuckyDrawDAO;
import com.bamisu.log.gameserver.sql.rank.dao.RankDAO;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.unit.objects.Probabilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LuckyDrawManager {
    private static LuckyDrawManager ourInstance = new LuckyDrawManager();

    public static LuckyDrawManager getInstance() {
        return ourInstance;
    }

    private LuckyDrawConfig luckyDrawConfigSOG;

    private LuckyDrawConfig luckyDrawConfig;
    private LuckyDrawConfig luckyDrawConfigTopUser1;
    private LuckyDrawConfig luckyDrawConfigTopUser2;
    private LuckyDrawConfig luckyDrawConfigTopUser3;

    private LuckyDrawConfig luckyDrawConfigSuperSOG;

    private LuckyDrawConfig luckyDrawConfigSuper;
    private LuckyDrawConfig luckyDrawConfigSuperTopUser4;
    private LuckyDrawConfig luckyDrawConfigSuperTopUser5;
    private LuckyDrawConfig luckyDrawConfigSuperTopUser6;

    private LuckyDrawManager() {
        loadConfig();
    }

    private void loadConfig() {
        // normal
        luckyDrawConfigSOG = Utils.fromJson(Utils.loadConfig(ServerConstant.LuckyDraw.FILE_PATH_CONFIG_LUCK_DRAW_SOG), LuckyDrawConfig.class);

        luckyDrawConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.LuckyDraw.FILE_PATH_CONFIG_LUCK_DRAW), LuckyDrawConfig.class);
        luckyDrawConfigTopUser1 = Utils.fromJson(Utils.loadConfig(ServerConstant.LuckyDraw.FILE_PATH_CONFIG_LUCK_DRAW_TOP_USER_1), LuckyDrawConfig.class);
        luckyDrawConfigTopUser2 = Utils.fromJson(Utils.loadConfig(ServerConstant.LuckyDraw.FILE_PATH_CONFIG_LUCK_DRAW_TOP_USER_2), LuckyDrawConfig.class);
        luckyDrawConfigTopUser3 = Utils.fromJson(Utils.loadConfig(ServerConstant.LuckyDraw.FILE_PATH_CONFIG_LUCK_DRAW_TOP_USER_3), LuckyDrawConfig.class);

        // super
        luckyDrawConfigSuperSOG = Utils.fromJson(Utils.loadConfig(ServerConstant.LuckyDraw.FILE_PATH_CONFIG_LUCK_DRAW_SUPER_SOG), LuckyDrawConfig.class);

        luckyDrawConfigSuper = Utils.fromJson(Utils.loadConfig(ServerConstant.LuckyDraw.FILE_PATH_CONFIG_LUCK_DRAW_SUPER), LuckyDrawConfig.class);
        luckyDrawConfigSuperTopUser4 = Utils.fromJson(Utils.loadConfig(ServerConstant.LuckyDraw.FILE_PATH_CONFIG_LUCK_DRAW_SUPER_TOP_USER_4), LuckyDrawConfig.class);
        luckyDrawConfigSuperTopUser5 = Utils.fromJson(Utils.loadConfig(ServerConstant.LuckyDraw.FILE_PATH_CONFIG_LUCK_DRAW_SUPER_TOP_USER_5), LuckyDrawConfig.class);
        luckyDrawConfigSuperTopUser6 = Utils.fromJson(Utils.loadConfig(ServerConstant.LuckyDraw.FILE_PATH_CONFIG_LUCK_DRAW_SUPER_TOP_USER_6), LuckyDrawConfig.class);
    }

    // tra ve config vong quay thuong
    public List<LuckyDrawVO> getLuckyDrawConfig() {
        List<LuckyDrawVO> list = new ArrayList<>();
        list.addAll(luckyDrawConfig.lucky_items);
        return list.parallelStream().filter(obj -> EStatus.COMMING_SOON.getId() != obj.status).collect(Collectors.toList());
    }

    // tra ve config vong quay dac biet
    public List<LuckyDrawVO> getLuckyDrawConfigSuper() {
        List<LuckyDrawVO> list = new ArrayList<>();
        list.addAll(luckyDrawConfigSuper.lucky_items);
        return list.parallelStream().filter(obj -> EStatus.COMMING_SOON.getId() != obj.status).collect(Collectors.toList());
    }

    // tao ket qua item theo loai vong quay tuong ung voi loai user
    public String getLuckyNumResult(String topUser, int typeSpin, String typeTokenSpin) {
        MockNeat mockNeat = MockNeat.threadLocal();
        Probabilities<String> probabilites = mockNeat.probabilites(String.class);
        if (typeTokenSpin.equals("BUSD")) {
            if (topUser.equals("1") && typeSpin == 0) {
                luckyDrawConfigTopUser1.lucky_items.stream().forEach((k) -> {
                    probabilites.add(k.item_win_rate, k.item_id);
                });
            }
            if (topUser.equals("2") && typeSpin == 0) {
                luckyDrawConfigTopUser2.lucky_items.stream().forEach((k) -> {
                    probabilites.add(k.item_win_rate, k.item_id);
                });
            }
            if (topUser.equals("3") && typeSpin == 0) {
                luckyDrawConfigTopUser3.lucky_items.stream().forEach((k) -> {
                    probabilites.add(k.item_win_rate, k.item_id);
                });
            }
            if (topUser.equals("4") && typeSpin == 1) {
                luckyDrawConfigSuperTopUser4.lucky_items.stream().forEach((k) -> {
                    probabilites.add(k.item_win_rate, k.item_id);
                });
            }
            if (topUser.equals("5") && typeSpin == 1) {
                luckyDrawConfigSuperTopUser5.lucky_items.stream().forEach((k) -> {
                    probabilites.add(k.item_win_rate, k.item_id);
                });
            }
            if (topUser.equals("6") && typeSpin == 1) {
                luckyDrawConfigSuperTopUser6.lucky_items.stream().forEach((k) -> {
                    probabilites.add(k.item_win_rate, k.item_id);
                });
            }
        } else {
            if (typeSpin == 0) {
                luckyDrawConfigSOG.lucky_items.stream().forEach((k) -> {
                    probabilites.add(k.item_win_rate, k.item_id);
                });
            }
            if (typeSpin == 1) {
                luckyDrawConfigSuperSOG.lucky_items.stream().forEach((k) -> {
                    probabilites.add(k.item_win_rate, k.item_id);
                });
            }
        }
        return probabilites.val();
    }

    //  doc item tu file config theo chi so item, loai user va loai token (BUSD - SOG)
    public LuckyDrawVO getConfigVo(String id, String topUser, String typeTokenSpin, int typeSpin) {
        LuckyDrawVO vo = null;
        if (typeTokenSpin.equals("BUSD")) {
            if (topUser.equals("1")) {
                vo = luckyDrawConfigTopUser1.readLuckyConfig(id);
            }
            if (topUser.equals("2")) {
                vo = luckyDrawConfigTopUser2.readLuckyConfig(id);
            }
            if (topUser.equals("3")) {
                vo = luckyDrawConfigTopUser3.readLuckyConfig(id);
            }
            if (topUser.equals("4")) {
                vo = luckyDrawConfigSuperTopUser4.readLuckyConfig(id);
            }
            if (topUser.equals("5")) {
                vo = luckyDrawConfigSuperTopUser5.readLuckyConfig(id);
            }
            if (topUser.equals("6")) {
                vo = luckyDrawConfigSuperTopUser6.readLuckyConfig(id);
            }
        } else {
            if (typeSpin == 0) {
                vo = luckyDrawConfigSOG.readLuckyConfig(id);
            }
            if (typeSpin == 1) {
                vo = luckyDrawConfigSuperSOG.readLuckyConfig(id);
            }
        }
        return vo;
    }

    // luu tru vao BD voi user trung BUSD
    public boolean createLuckyDrawHistory(long userId, String type, double amount, String topUser, Zone zone) {
        LuckyDrawDAO luckyDrawDAO = new LuckyDrawDAO();
        HistoryLuckyDrawDBO obj = new HistoryLuckyDrawDBO();
        ConfigRankLeagueDBO configRankLeagueDBO = RankDAO.getConfigRankLeagueDBO(zone);
        int season = configRankLeagueDBO != null ? configRankLeagueDBO.season : 0;
        obj.userId = userId;
        obj.type = type;
        obj.amount = amount;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LocalDateTime current = LocalDateTime.now(ZoneId.of("UTC"));
        java.util.Date now;
        try {
            now = sdf.parse(current.format(formatter));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        obj.date = now; //new Date(new java.util.Date().getTime());
        obj.season = season;
        obj.top_user = topUser;
        return luckyDrawDAO.createLuckyDrawHistoryToDB(zone, obj);
    }

    public void genHistoryRank(Zone zone) {
        ConfigRankLeagueDBO configRankLeagueDBO = RankDAO.getConfigRankLeagueDBO(zone);
        int season = configRankLeagueDBO != null ? (configRankLeagueDBO.season - 1) : 0;

        RankTopUserModel rankTopUserModel = RankTopUserModel.createRankTopUserModel(zone);

        // xep rank Campaign
        List<RankUser> listRankCampaignTop1 = new ArrayList<>();
        List<RankUser> listRankCampaignTop2 = new ArrayList<>();
        for (long i = 1; i < 100; i++) {
            RankCampaignModel rankCampaignModel = RankCampaignModel.copyFromDBtoObject(season, i, zone);
            if (rankCampaignModel == null) {
                continue;
            } else {
                List<UserRankInfo> sort = rankCampaignModel.rank.stream().sorted((s1, s2) -> s2.score - s1.score)
                        .collect(Collectors.toList());
                List<RankUser> listRankCampaign = sort.stream()
                        .map(x -> {
                            RankUser rankUser = new RankUser();
                            rankUser.uid = x.uid;
                            rankUser.score = x.score;
                            rankUser.time = x.time;
                            return rankUser;
                        })
                        .collect(Collectors.toList());

                for (int j = 0; j < 5; j++) {
                    try {
                        listRankCampaignTop1.add(listRankCampaign.get(j));
                    } catch (Exception e) {
                        break;
                    }
                }
                for (int j = 5; j < 10; j++) {
                    try {
                        listRankCampaignTop2.add(listRankCampaign.get(j));
                    } catch (Exception e) {
                        break;
                    }

                }
            }
        }

        // xep rank Darkrealm
        List<RankUser> listRankDarkrealmTop1 = new ArrayList<>();
        List<RankUser> listRankDarkrealmTop2 = new ArrayList<>();
        for (long i = 1; i < 100; i++) {
            RankDarkrealmModel rankDarkrealmModel = RankDarkrealmModel.copyFromDBtoObject(season - 1, i, zone);
            if (rankDarkrealmModel == null) {
                continue;
            } else {
                List<UserRankInfo> sort = rankDarkrealmModel.rank.stream().sorted((s1, s2) -> s2.score - s1.score)
                        .collect(Collectors.toList());
                List<RankUser> list = sort.stream()
                        .map(x -> {
                            RankUser rankUser = new RankUser();
                            rankUser.uid = x.uid;
                            rankUser.score = x.score;
                            rankUser.time = x.time;
                            return rankUser;
                        })
                        .collect(Collectors.toList());

                for (int j = 0; j < 5; j++) {
                    try {
                        listRankDarkrealmTop1.add(list.get(j));
                    } catch (Exception e) {
                        break;
                    }
                }
                for (int j = 5; j < 10; j++) {
                    try {
                        listRankDarkrealmTop2.add(list.get(j));
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        }

        // xep rank Endlessnight
        List<RankUser> listRankEndlessnightTop1 = new ArrayList<>();
        List<RankUser> listRankEndlessnightTop2 = new ArrayList<>();
        for (long i = 1; i < 100; i++) {
            RankEndlessnightModel rankEndlessnightModel = RankEndlessnightModel.copyFromDBtoObject(season - 1, i, zone);
            if (rankEndlessnightModel == null) {
                continue;
            } else {
                List<UserRankInfo> sort = rankEndlessnightModel.rank.stream().sorted((s1, s2) -> s2.score - s1.score)
                        .collect(Collectors.toList());
                List<RankUser> list = sort.stream()
                        .map(x -> {
                            RankUser rankUser = new RankUser();
                            rankUser.uid = x.uid;
                            rankUser.score = x.score;
                            rankUser.time = x.time;
                            return rankUser;
                        })
                        .collect(Collectors.toList());

                for (int j = 0; j < 5; j++) {
                    try {
                        listRankEndlessnightTop1.add(list.get(j));
                    } catch (Exception e) {
                        break;
                    }
                }
                for (int j = 5; j < 10; j++) {
                    try {
                        listRankEndlessnightTop2.add(list.get(j));
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        }

        rankTopUserModel.topUserRankCampaign1.addAll(listRankCampaignTop1);
        rankTopUserModel.topUserRankCampaign2.addAll(listRankCampaignTop2);
        rankTopUserModel.topUserRankDarkrealm1.addAll(listRankDarkrealmTop1);
        rankTopUserModel.topUserRankDarkrealm2.addAll(listRankDarkrealmTop2);
        rankTopUserModel.topUserRankEndlessnight1.addAll(listRankEndlessnightTop1);
        rankTopUserModel.topUserRankEndlessnight2.addAll(listRankEndlessnightTop2);
        rankTopUserModel.saveToDB(zone);

    }

    // kiem tra xem user thuoc loai Top nao
    public String checkTypeTopUser(RecLuckyDrawItemsConfig obGet, long uid, Zone zone) {
        String topUser = "";
        RankTopUserModel rankTopUserModel = RankTopUserModel.copyFromDBtoObject(zone);
        if (rankTopUserModel == null) {
            rankTopUserModel = RankTopUserModel.createRankTopUserModel(zone);
        }

        if (obGet.typeSpin == 0) {
            topUser = "3";
            List<RankUser> list1 = rankTopUserModel.topUserRankCampaign1.stream().filter(x -> x.uid == uid).collect(Collectors.toList());
            if (!list1.isEmpty()) {
                topUser = "1";
            }
            List<RankUser> list2 = rankTopUserModel.topUserRankCampaign2.stream().filter(x -> x.uid == uid).collect(Collectors.toList());
            if (!list2.isEmpty()) {
                topUser = "2";
            }
        }

        if (obGet.typeSpin == 1) {
            topUser = "6";
            // TODO: kiem tra xem tuan nay dang mo darkrealm hay endlessnight
            DarkGateModel darkGateModel = DarkGateModel.copyFromDBtoObject(zone);

            if (darkGateModel.containsEventID(EDarkGateEvent.Dark_Realm.id)) {
                List<RankUser> listDark1 = rankTopUserModel.topUserRankDarkrealm1.stream().filter(x -> x.uid == uid).collect(Collectors.toList());
                if (!listDark1.isEmpty()) {
                    topUser = "4";
                }
                List<RankUser> listDark2 = rankTopUserModel.topUserRankDarkrealm2.stream().filter(x -> x.uid == uid).collect(Collectors.toList());
                if (!listDark2.isEmpty()) {
                    topUser = "5";
                }
            } else {
                List<RankUser> listEnd1 = rankTopUserModel.topUserRankEndlessnight1.stream().filter(x -> x.uid == uid).collect(Collectors.toList());
                if (!listEnd1.isEmpty()) {
                    topUser = "4";
                }
                List<RankUser> listEnd2 = rankTopUserModel.topUserRankEndlessnight2.stream().filter(x -> x.uid == uid).collect(Collectors.toList());
                if (!listEnd2.isEmpty()) {
                    topUser = "5";
                }
            }
        }
        return topUser;
    }

    // kiem tra so tien trong hu hien tai
    public String checkAmountBUSD(String topUser, RecLuckyDrawItemsConfig obGet, User user) {
        LuckyDrawRewardModel ldrm = LuckyDrawRewardModel.copyFromDBtoObject(0, user.getZone());
        if (topUser.equals("1")) {
            if (ldrm.busdTopuser1 <= 0 && obGet.typeSpin == 0) {
                return topUser = "7";
            }
        }
        if (topUser.equals("2")) {
            if (ldrm.busdTopuser2 <= 0 && obGet.typeSpin == 0) {
                return topUser = "7";
            }
        }
        if (topUser.equals("3")) {
            if (ldrm.busdTopuser3 <= 0 && obGet.typeSpin == 0) {
                return topUser = "7";
            }
        }
        if (topUser.equals("4")) {
            if (ldrm.busdTopuser4 <= 0 && obGet.typeSpin == 1) {
                return topUser = "8";
            }
        }
        if (topUser.equals("5")) {
            if (ldrm.busdTopuser5 <= 0 && obGet.typeSpin == 1) {
                return topUser = "8";
            }
        }
        if (topUser.equals("6")) {
            if (ldrm.busdTopuser6 <= 0 && obGet.typeSpin == 1) {
                return topUser = "8";
            }
        }
        return topUser;
    }

    public boolean checkNegative(String topUser, User user, double money) {
        LuckyDrawRewardModel luckyDrawRewardModel = LuckyDrawRewardModel.copyFromDBtoObject(0, user.getZone());
        if (topUser.equals("1")) {
            double a = luckyDrawRewardModel.busdTopuser1 - money;
            if (a < 0) {
                return true;
            }
        }
        if (topUser.equals("2")) {
            double a = luckyDrawRewardModel.busdTopuser2 - money;
            if (a < 0) {
                return true;
            }
        }
        if (topUser.equals("3")) {
            double a = luckyDrawRewardModel.busdTopuser3 - money;
            if (a < 0) {
                return true;
            }
        }
        if (topUser.equals("4")) {
            double a = luckyDrawRewardModel.busdTopuser4 - money;
            if (a < 0) {
                return true;
            }
        }
        if (topUser.equals("5")) {
            double a = luckyDrawRewardModel.busdTopuser5 - money;
            if (a < 0) {
                return true;
            }
        }
        if (topUser.equals("6")) {
            double a = luckyDrawRewardModel.busdTopuser6 - money;
            if (a < 0) {
                return true;
            }
        }
        return false;
    }

    // cap nhat so luong token
    public void updateAmountToken(LuckyDrawVO vo, Long uid, String topUser, User user) {
        List<TokenResourcePackage> nhanthuong = Collections.singletonList(new TokenResourcePackage(vo.reward.get(0).getId(), vo.reward.get(0).getValue()));
        if (BagManager.getInstance().addItemToDB(nhanthuong, uid, user.getZone(), UserUtils.TransactionType.REWARD_LUCKY_DRAW)) {
            if (vo.reward.get(0).getId().equals("BUSD")) { // lưu bản ghi trúng busd
                double money = Double.parseDouble(vo.reward.get(0).getValue().toString());
                if (createLuckyDrawHistory(uid, "BUSD", money, topUser, user.getZone())) {
                }

                // cap nhat so tien BUSD cua hu 1,2,3,4,5,6 tuong ung theo topUser = 1,2,3,4,5,6
                LuckyDrawRewardModel luckyDrawRewardModel = LuckyDrawRewardModel.copyFromDBtoObject(0, user.getZone());
                if (topUser.equals("1")) {
                    luckyDrawRewardModel.busdTopuser1 = luckyDrawRewardModel.busdTopuser1 - money;
                    luckyDrawRewardModel.saveToDB(user.getZone());
                }
                if (topUser.equals("2")) {
                    luckyDrawRewardModel.busdTopuser2 = luckyDrawRewardModel.busdTopuser2 - money;
                    luckyDrawRewardModel.saveToDB(user.getZone());
                }
                if (topUser.equals("3")) {
                    luckyDrawRewardModel.busdTopuser3 = luckyDrawRewardModel.busdTopuser3 - money;
                    luckyDrawRewardModel.saveToDB(user.getZone());
                }
                if (topUser.equals("4")) {
                    luckyDrawRewardModel.busdTopuser4 = luckyDrawRewardModel.busdTopuser4 - money;
                    luckyDrawRewardModel.saveToDB(user.getZone());
                }
                if (topUser.equals("5")) {
                    luckyDrawRewardModel.busdTopuser5 = luckyDrawRewardModel.busdTopuser5 - money;
                    luckyDrawRewardModel.saveToDB(user.getZone());
                }
                if (topUser.equals("6")) {
                    luckyDrawRewardModel.busdTopuser6 = luckyDrawRewardModel.busdTopuser6 - money;
                    luckyDrawRewardModel.saveToDB(user.getZone());
                }

                // cập nhật lại số tiền busd user vừa trúng vào couchbase
                TotalBusdOfUserInSeasonModel totalBusdOfUserInSeasonModel = TotalBusdOfUserInSeasonModel.copyFromDBtoObject(user.getZone());

                List<TotalBusdUserInSeason> listAll = new ArrayList<>();
                listAll.addAll(totalBusdOfUserInSeasonModel.listTotalBusdUserInSeason);
                List<TotalBusdUserInSeason> listInfoReward = new ArrayList<>();

                listInfoReward = listAll.stream().filter(x -> x.uid == uid).collect(Collectors.toList());
                int index = IntStream.range(0, listAll.size())
                        .filter(i -> listAll.get(i).uid == uid)
                        .findFirst()
                        .orElse(-1);

                List<TotalBusdUserInSeason> list = new ArrayList<>();

                if (listInfoReward == null || listInfoReward.isEmpty()) { // tức là user này quay trúng busd lần đầu trong tuần
                    TotalBusdUserInSeason totalBusdUserInSeasons = new TotalBusdUserInSeason();
                    totalBusdUserInSeasons.uid = uid;
                    totalBusdUserInSeasons.totalBusd = money;

                    list.add(totalBusdUserInSeasons);
                    listAll.addAll(list);
                    totalBusdOfUserInSeasonModel.createHistoryUser(listAll, user.getZone());
                } else { //user đã trúng busd từ lần 2 trở đi trong tuần hiện tại
                    TotalBusdUserInSeason totalBusdUserInSeasons = new TotalBusdUserInSeason();
                    totalBusdUserInSeasons.uid = listInfoReward.get(0).uid;
                    totalBusdUserInSeasons.totalBusd = listInfoReward.get(0).totalBusd + money;

                    listAll.remove(index);
                    list.add(totalBusdUserInSeasons);
                    listAll.addAll(list);
                    totalBusdOfUserInSeasonModel.createHistoryUser(listAll, user.getZone());
                }
            } else { // lưu bản ghi trúng sog
                double money = Double.parseDouble(vo.reward.get(0).getValue().toString());
                if (createLuckyDrawHistory(uid, "SOG", money, topUser, user.getZone())) {
                }
            }
        }
    }

    // kiem tra xem số tiền hiện tại của hũ có đủ để phát phần thưởng cao nhât cho user này hay k?
    public boolean checkBonusConditions(String topUser, User user) {
        LuckyDrawRewardModel luckyDrawRewardModel = LuckyDrawRewardModel.copyFromDBtoObject(1L, user.getZone());
        if (topUser.equals("1")) {
            if (luckyDrawRewardModel.busdTopuser1 >= 600) {
                return true;
            }
        }
        if (topUser.equals("2")) {
            if (luckyDrawRewardModel.busdTopuser2 >= 600) {
                return true;
            }
        }
        if (topUser.equals("3")) {
            if (luckyDrawRewardModel.busdTopuser3 >= 600) {
                return true;
            }
        }
        if (topUser.equals("4")) {
            if (luckyDrawRewardModel.busdTopuser4 >= 1000) {
                return true;
            }
        }
        if (topUser.equals("5")) {
            if (luckyDrawRewardModel.busdTopuser5 >= 1000) {
                return true;
            }
        }
        if (topUser.equals("6")) {
            if (luckyDrawRewardModel.busdTopuser6 >= 1000) {
                return true;
            }
        }
        return false;
    }

    // kiểm tra loại topUser 1,2,3,4,5,6 và sinh ngẫu nhiên loại phần thưởng mà user nhận đc là BUSD hay SOG
    public String checkTypeTokenSpin(String topUser, RecLuckyDrawItemsConfig objGet, Long sumTicketLatest, Long turnTicket, Long sumTicket) {
        String typeTokenSpin = "";
        MockNeat mockNeat = MockNeat.threadLocal();
        if ((sumTicket == 0) && (turnTicket == 0)) { // lần đầu sẽ cho trúng thưởng BUSD nếu hũ còn tiền - SOG nếu hũ hết tiền
            if (topUser.equals("3")) {
                typeTokenSpin = "BUSD";
            }
            if (topUser.equals("6")) {
                typeTokenSpin = "BUSD";
            }
            if (topUser.equals("7")) {
                typeTokenSpin = "SOG";
            }
            if (topUser.equals("8")) {
                typeTokenSpin = "SOG";
            }
        } else if (sumTicketLatest == turnTicket) { // lần trúng thưởng cố định
            if (topUser.equals("3")) {
                typeTokenSpin = "BUSD";
            }
            if (topUser.equals("6")) {
                typeTokenSpin = "BUSD";
            }

            if (topUser.equals("8")) {
                typeTokenSpin = "SOG";
            }
            if (topUser.equals("7")) {
                typeTokenSpin = "SOG";
            }
        } else { // trúng thưởng ngẫu nhiên
            if (objGet.typeSpin == 0) {
                if (topUser.equals("7")) {
                    typeTokenSpin = "SOG";
                } else {
                    if (topUser.equals("1")) {
                        typeTokenSpin = mockNeat.probabilites(String.class)
                                .add(0.20, "BUSD")
                                .add(0.80, "SOG")
                                .val();
                    }
                    if (topUser.equals("2")) {
                        typeTokenSpin = mockNeat.probabilites(String.class)
                                .add(0.14, "BUSD")
                                .add(0.86, "SOG")
                                .val();
                    }
                    if (topUser.equals("3")) {
                        typeTokenSpin = mockNeat.probabilites(String.class)
                                .add(0.10, "BUSD")
                                .add(0.90, "SOG")
                                .val();
                    }
                }
            }

            if (objGet.typeSpin == 1) {
                if (topUser.equals("8")) {
                    typeTokenSpin = "SOG";
                } else {
                    if (topUser.equals("4")) {
                        typeTokenSpin = mockNeat.probabilites(String.class)
                                .add(0.20, "BUSD")
                                .add(0.80, "SOG")
                                .val();
                    }
                    if (topUser.equals("5")) {
                        typeTokenSpin = mockNeat.probabilites(String.class)
                                .add(0.14, "BUSD")
                                .add(0.86, "SOG")
                                .val();
                    }
                    if (topUser.equals("6")) {
                        typeTokenSpin = mockNeat.probabilites(String.class)
                                .add(0.10, "BUSD")
                                .add(0.90, "SOG")
                                .val();
                    }
                }
            }
        }
        return typeTokenSpin;
    }

    // kiem tra xem so tien busd cua user hien tai co thoa man dk trung thuong hay k?
    public boolean checkTotalAmountBUSDFollowSeason(Zone zone, long uid, RecLuckyDrawItemsConfig objGet) {
        TotalBusdOfUserInSeasonModel totalBusdOfUserInSeasonModel = TotalBusdOfUserInSeasonModel.copyFromDBtoObject(zone);
        if (totalBusdOfUserInSeasonModel == null) {
            totalBusdOfUserInSeasonModel = TotalBusdOfUserInSeasonModel.createTotalBusdOfUsers(zone);
        }
        if (totalBusdOfUserInSeasonModel.listTotalBusdUserInSeason.size() == 0) {
            return true;
        } else {
            List<TotalBusdUserInSeason> totalBusdUserInSeason = totalBusdOfUserInSeasonModel.listTotalBusdUserInSeason.stream().filter(x -> x.uid == uid).collect(Collectors.toList());
            if (totalBusdUserInSeason.isEmpty()) {
                return true;
            }
            String topUser = LuckyDrawManager.getInstance().checkTypeTopUser(objGet, uid, zone);
            if (objGet.typeSpin == 0) {//1,2,3
                if (topUser.equals("3")) {
                    if (totalBusdUserInSeason.get(0).totalBusd < 1) {
                        return true;
                    }
                }
                if (topUser.equals("2")) {
                    if (totalBusdUserInSeason.get(0).totalBusd < 5) {
                        return true;
                    }
                }
                if (topUser.equals("1")) {
                    if (totalBusdUserInSeason.get(0).totalBusd < 10) {
                        return true;
                    }
                }
            } else {
                if (topUser.equals("6")) {
                    if (totalBusdUserInSeason.get(0).totalBusd < 1) {
                        return true;
                    }
                }
                if (topUser.equals("5")) {
                    if (totalBusdUserInSeason.get(0).totalBusd < 5) {
                        return true;
                    }
                }
                if (topUser.equals("4")) {
                    if (totalBusdUserInSeason.get(0).totalBusd < 10) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkTime() {
        boolean invalidTime = true;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        LocalDateTime current = LocalDateTime.now(ZoneId.of("UTC"));
        DayOfWeek dayOfWeek = current.getDayOfWeek();

        java.util.Date before;
        java.util.Date next;
        java.util.Date now;
        // khóa vào ngày chủ nhật
        if (dayOfWeek.getValue() == DayOfWeek.SUNDAY.getValue()) {
            try {
                before = sdf.parse("23:50:59");
                next = sdf.parse("23:59:59");
                now = sdf.parse(current.format(formatter));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            if (before.before(now) && now.before(next)) {
                invalidTime = true;
            } else {
                invalidTime = false;
            }
        } else {
            invalidTime = false;
        }
        return invalidTime;
    }
}
