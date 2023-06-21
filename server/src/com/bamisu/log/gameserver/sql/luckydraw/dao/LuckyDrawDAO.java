package com.bamisu.log.gameserver.sql.luckydraw.dao;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.sql.game.dbo.ConfigRankLeagueDBO;
import com.bamisu.gamelib.sql.luckydraw.HistoryLuckyDrawDBO;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.log.gameserver.datamodel.luckydraw.TotalBusdOfUserInSeasonModel;
import com.bamisu.log.gameserver.module.lucky_draw.LuckyDrawManager;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class LuckyDrawDAO {

    @WithSpan
    public boolean createLuckyDrawHistoryToDB(Zone zone, HistoryLuckyDrawDBO obj) {
        boolean result = false;

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            session.saveOrUpdate(obj);

            session.getTransaction().commit();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.isOpen() && session.getTransaction() != null)
                session.getTransaction().rollback();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }
        return result;
    }

    @WithSpan
    public double totalAmountBUSDByWeekInUser(Zone zone, long uid) {
        double totalAmountBUSD = 0;
        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            String sql = "SELECT sum(amount) as sum FROM lucky_draw where user_id = " + uid + " and season = (select max(season) from lucky_draw );";
            SQLQuery sqlQuerySum = session.createSQLQuery(sql).addScalar("sum");
            try {
                Object obj = sqlQuerySum.uniqueResult();
                if (obj != null) {
                    totalAmountBUSD = (double) obj;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.isOpen() && session.getTransaction() != null)
                session.getTransaction().rollback();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }
        return totalAmountBUSD;
    }

    // lấy season mới nhất từ config rank league
    @WithSpan
    public int getCurrentSeasonValue(Zone zone) {
        int result = 0;

        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();

            ConfigRankLeagueDBO configRankLeagueDBO = (ConfigRankLeagueDBO) session.get(ConfigRankLeagueDBO.class, 1);
            result = configRankLeagueDBO.season;

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.isOpen() && session.getTransaction() != null)
                session.getTransaction().rollback();
        } finally {
            ManagedSessionContext.unbind(sqlController.getSessionFactory());
            if (session != null) {
                session.close();
            }
        }
        return result;
    }

    // lap lịch tạo bảng xếp hạng phân rank của user vào 23h55p chủ nhật hàng tuần
    public static void startThreadCreateHistoryRankTopUser(Zone zone) {
        // lập lịch 1 tuần chạy 1 lần
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        long initialDelay = 0;
        Duration duration;
        ZonedDateTime nextRun;
        nextRun = now.withHour(23).withMinute(55).withSecond(00);

        // tinh toan khoang tgian tu thoi diem hien tai den Chu nhat la bao lau?
        if(dayOfWeek.getValue() == DayOfWeek.MONDAY.getValue()){
            nextRun = nextRun.plusDays(6);
            duration = Duration.between(now, nextRun);
            initialDelay = duration.getSeconds();
        }
        if(dayOfWeek.getValue() == DayOfWeek.TUESDAY.getValue()){
            nextRun = nextRun.plusDays(5);
            duration = Duration.between(now, nextRun);
            initialDelay = duration.getSeconds();
        }
        if(dayOfWeek.getValue() == DayOfWeek.WEDNESDAY.getValue()){
            nextRun = nextRun.plusDays(4);
            duration = Duration.between(now, nextRun);
            initialDelay = duration.getSeconds();
        }
        if(dayOfWeek.getValue() == DayOfWeek.THURSDAY.getValue()){
            nextRun = nextRun.plusDays(3);
            duration = Duration.between(now, nextRun);
            initialDelay = duration.getSeconds();
        }
        if(dayOfWeek.getValue() == DayOfWeek.FRIDAY.getValue()){
            nextRun = nextRun.plusDays(2);
            duration = Duration.between(now, nextRun);
            initialDelay = duration.getSeconds();
        }
        if(dayOfWeek.getValue() == DayOfWeek.SATURDAY.getValue()){
            nextRun = nextRun.plusDays(1);
            duration = Duration.between(now, nextRun);
            initialDelay = duration.getSeconds();
        }
        if(dayOfWeek.getValue() == DayOfWeek.SUNDAY.getValue()){
            duration = Duration.between(now, nextRun);
            initialDelay = duration.getSeconds();
        }

        LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.SQL_QUERY, 1).scheduleAtFixedRate(
                () -> genHisoryRankTopUser(zone), initialDelay, TimeUnit.SECONDS.toSeconds(604800), TimeUnit.SECONDS);

//         //test lập lich 1h/lần
//        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
//        ZonedDateTime nextRun = now.withMinute(57).withSecond(00);
//        Duration duration = Duration.between(now, nextRun);
//        long initialDelay = duration.getSeconds();
//        LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.SQL_QUERY, 1).scheduleAtFixedRate(
//                () -> genHisoryRankTopUser(zone), initialDelay, TimeUnit.SECONDS.toSeconds(3600), TimeUnit.SECONDS);
    }
    
    @WithSpan
    private synchronized static void genHisoryRankTopUser(Zone zone) {
        LuckyDrawManager.getInstance().genHistoryRank(zone);
        TotalBusdOfUserInSeasonModel.createTotalBusdOfUsers(zone);
    }

}
