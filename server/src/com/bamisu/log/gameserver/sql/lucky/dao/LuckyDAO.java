package com.bamisu.log.gameserver.sql.lucky.dao;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.sql.lucky.HistoryWinnerDBO;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.log.gameserver.module.lucky.LuckyManager;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LuckyDAO {

    // lấy lịch sử winner
    @WithSpan
    public static List<HistoryWinnerDBO> getListHistoryWinner(Zone zone) {
        List<HistoryWinnerDBO> lstHistoryWinner = new ArrayList<>();
        SQLController sqlController = ((BaseExtension) zone.getExtension()).getSQLController();
        Session session = null;
        try {
            session = sqlController.getSessionFactory().openSession();
            ManagedSessionContext.bind(session);
            session.beginTransaction();
            String sql = "select * from lucky_winer_history order by create_date DESC;";
            SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(HistoryWinnerDBO.class);
            lstHistoryWinner = sqlQuery.list();

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
        return lstHistoryWinner;
    }


    @WithSpan
    public static void startThreadGenerateLuckyNumber(Zone zone) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime nextRun = now.withHour(23).withMinute(35).withSecond(00);//23:35:00
        if (now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);
        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();
        LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.SQL_QUERY, 1).scheduleAtFixedRate(
                () -> genLuckyNumber(zone), initialDelay, TimeUnit.SECONDS.toSeconds(86400), TimeUnit.SECONDS);
    }

    @WithSpan
    private synchronized static void genLuckyNumber(Zone zone) {
        //Gen so
        LuckyManager.getInstance().createLuck(zone);
        LuckyManager.getInstance(). doCreateWinner(zone);
    }
}
